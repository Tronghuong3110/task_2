package com.newlife.Connect_multiple.service.impl;

import com.newlife.Connect_multiple.api.ApiCheckConnect;
import com.newlife.Connect_multiple.converter.ProbeModuleConverter;
import com.newlife.Connect_multiple.dto.ProbeModuleDto;
import com.newlife.Connect_multiple.entity.*;
import com.newlife.Connect_multiple.repository.*;
import com.newlife.Connect_multiple.service.IProbeModuleService;
import com.newlife.Connect_multiple.util.JsonUtil;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@Service
public class ProbeModuleService implements IProbeModuleService {

    @Autowired
    private ModuleProbeRepository moduleProbeRepository;
    @Autowired
    private ModuleHistoryRepository moduleHistoryRepository;
    @Autowired
    private CmdHistoryRepository cmdHistoryRepository;
    @Autowired
    private SubtopicOnServerRepository subtopicOnServerRepository;
    @Autowired
    private ProbeRepository probeRepository;
    @Autowired
    private BrokerRepository brokerRepository;
    @Autowired
    private ServerRepository serverRepository;
    private boolean checkResponse = false;
    private JSONObject responseMessage;
    private Integer retry;
    private Integer count;
    private String pId;
    private MqttClient client;
    @Override
    public List<ProbeModuleDto> findAllProbeModule(String moduleName, String status, Integer page, String sortBy) {
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, sortBy);
            Pageable pageable = PageRequest.of(page, 1, sort);
            Page<ProbeModuleEntity> listProbeModuleEntity = moduleProbeRepository.findAllByProbeNameOrStatus(moduleName, status, pageable);
            List<ProbeModuleDto> listProbeModuleDto = new ArrayList<>();
            for(ProbeModuleEntity entity : listProbeModuleEntity.toList()) {
                listProbeModuleDto.add(ProbeModuleConverter.toDto(entity));
            }
            return listProbeModuleDto;
        }
        catch (Exception e) {
            System.out.println("Find All ProbeModule error");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    @Override
    public JSONObject runModule(Integer idProbeModule) {
        retry = 0;
        count = -2;
        pId = null;
        checkResponse = false;
        responseMessage = new JSONObject();
        ServerEntity server = serverRepository.findAll().get(0); // lấy thông tin server
        ProbeOptionEntity probeOption = server.getProbeOptionEntity(); // lấy ra các option của server
        String clientID = server.getServerIdConnect(); // lấy ra clientId của server
        ProbeModuleEntity probeModuleEntity = moduleProbeRepository.findById(idProbeModule)
                .orElse(null); // lấy ra module cần chạy
        if(probeModuleEntity == null) {
            return JsonUtil.createJsonResponse("Không tồn tại module có id = " + idProbeModule, "5");
        }
        SubtopicServerEntity subTopic = subtopicOnServerRepository.findByIdProbe(probeModuleEntity.getIdProbe())
                .orElse(null);
        String idCmd = saveCmd(probeModuleEntity); // lưu thông tin lệnh vào database
        ProbeEntity probe = probeRepository.findById(probeModuleEntity.getIdProbe()).orElse(new ProbeEntity());
        // create message json
        String jsonObject = JsonUtil.createJson(probeModuleEntity, idCmd, Optional.ofNullable(null), Optional.ofNullable(null), "run");
        MqttConnectOptions connectOptions = createOption(probeOption);
        // kết nối server voi broker
        List<BrokerEntity> broker = brokerRepository.findAll();
        String brokerURL = broker.get(0).getUrl();
        // connect to broker
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            client = new MqttClient(brokerURL, clientID, persistence);
            if(client.isConnected()) {
                client.disconnect();
                client = new MqttClient(brokerURL, clientID, persistence);
            }
            client.connect(connectOptions);
            client.subscribe(subTopic.getSubTopic());
            System.out.println("Json object " + jsonObject);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    while (!client.isConnected()) {
                        try {
                            System.out.println("Đang kết nối lại...");
                            Thread.sleep(5000);
                            client.reconnect();
                            client.subscribe(subTopic.getSubTopic());
                            System.out.println("Kết nối lại thành công!!");
                        }
                        catch(InterruptedException | MqttException e) {
                            e.printStackTrace();
                        }
                    }
                }
                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) {
                    String message = new String(mqttMessage.getPayload());
                    JSONObject json = JsonUtil.parseJson(message);
                    if(json.containsKey("check")) {
                        checkResponse = true; // đánh dấu cliet đã nhận được lệnh ==> không gửi lại lệnh nữa
                        System.out.println("Response " + message);
                        responseMessage = JsonUtil.parseJson(message);
                        String status = (String) responseMessage.get("statusModule");
                        System.out.println("Phản hồi từ client: " + responseMessage.get("statusCmd"));
                        // client phản hồi đã nhận được lệnh
                        if (responseMessage.containsKey("statusCmd") && (responseMessage.get("statusCmd").equals("OK") || responseMessage.get("statusCmd").equals("restart"))) {
                            System.out.println("Message " + responseMessage.get("message"));
                            // update status cmd history
                            updateCmdHistory(idCmd, -1, 1);
                        }
                        // client phản hổi đã thực hiện lệnh thành công hoặc thất bại
                        else if(status.equals("1") ||  status.equals("2")) {
                            System.out.println("Status cmd " + json.get("message"));
                            pId = (String) json.get("PID");
                            saveModuleHistory(status, 1, probeModuleEntity);
                        }
                    }
                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                }
            });
            // send message
            MqttMessage message = new MqttMessage(jsonObject.getBytes());
            message.setQos(2);
            client.publish(subTopic.getSubTopic(), message);
            // gửi lại lệnh khi chưa nhận được phản hồi từ client
            while (true) {
                Thread.sleep(5000);
                if (retry <= 2 && !checkResponse) {
                    retry++;
                    // Gửi lại tin nhắn
                    client.publish(subTopic.getSubTopic(), message);
                } else if (!checkResponse && retry > 2) {
                    if (retry > 2) {
                        updateCmdHistory(idCmd, 3, 4);
                        Boolean clientIsDisconnect = checkClientIsDisconnect(probe.getClientId());
                        if (!clientIsDisconnect) {
                            // Thông báo cho FE client đã mất kết nối
                            System.out.println("Send to front end " + "đã mất kết nối tới broker");
                            return responseMessageToFE("Probe " + probe.getName() + " đã mất kết nối tới broker", probeModuleEntity, "4", 1);
                        } else {
                            // Thông báo gửi lệnh thất bại
                            System.out.println("Send to front end " + "không nhận được yêu cầu thực hiện");
                            return responseMessageToFE("Probe " + probe.getName() + " không nhận được yêu cầu thực hiện", probeModuleEntity, "4", 1);
                        }
                    }
                } else if(checkResponse) {
                    break;
                }
            }
            // client đã nhận được lệnh, kiểm tra xem lệnh chạy thành công hay thất bại
            while(true) {
                Thread.sleep(2000);
                String statusModule = (String) responseMessage.get("statusModule");
                System.out.println("Status module " + statusModule);
                // TH nhận được phản hồi về trạng thái module
                if (statusModule != null && (statusModule.equals("1") || statusModule.equals("3"))) {
                    System.out.println("Module thành công hoặc thất bại" + responseMessage.toJSONString());
                    String mess = statusModule.equals("1") ? "Module chạy thành công" : "Module chạy thất bại";
                    return responseMessageToFE(mess, probeModuleEntity, statusModule, 1);
                }
                // TH không nhận được phản hồi
                else if (count >= 3) {
                    System.out.println("Client không có phản hồi " + responseMessage.toJSONString());
                    // có nên gửi lại không??
                    return responseMessageToFE("Không nhận được phản hồi từ client", probeModuleEntity, "4", 1);
                } else {
                    count++;
                }
            }
        }
        catch (Exception me) {
            me.printStackTrace();
            System.out.println("Chạy module lỗi rồi!");
            return JsonUtil.createJsonResponse("Error", "0");
        }
    }
    @Override
    public Object stopModule(Integer idProbeModule) {
        retry = 0;
        count = -2;
        pId = null;
        checkResponse = false;
        responseMessage = new JSONObject();
        ServerEntity server = serverRepository.findAll().get(0); // lấy thông tin server
        ProbeOptionEntity probeOption = server.getProbeOptionEntity(); // lấy ra các option của server
        String clientID = server.getServerIdConnect(); // lấy ra clientId của server
        ProbeModuleEntity probeModuleEntity = moduleProbeRepository.findById(idProbeModule)
                .orElse(null); // lấy ra module cần chạy
        if(probeModuleEntity == null) {
            return JsonUtil.createJsonResponse("Không tồn tại module có id = " + idProbeModule, "5");
        }
        SubtopicServerEntity subTopic = subtopicOnServerRepository.findByIdProbe(probeModuleEntity.getIdProbe())
                .orElse(null); // lấy topic để thực hiện gửi message
        String idCmd = saveCmd(probeModuleEntity); // lưu thông tin lệnh vào database
        ProbeEntity probe = probeRepository.findById(probeModuleEntity.getIdProbe()).orElse(new ProbeEntity());
        // tạo json để gửi tới client
        String jsonObject = JsonUtil.createJson(probeModuleEntity, idCmd, Optional.of("cmd /c TASKKILL /F /PID "), Optional.of("kill -9 "), "stop");
        MqttConnectOptions connectOptions = createOption(probeOption);
        // kết nối server voi broker
        List<BrokerEntity> broker = brokerRepository.findAll();
        String brokerURL = broker.get(0).getUrl();
        // connect to broker
        MemoryPersistence persistence = new MemoryPersistence();
        try {
           client = new MqttClient(brokerURL, clientID, persistence);
            if(client.isConnected()) {
                client.disconnect();
                client = new MqttClient(brokerURL, clientID, persistence);
            }
            client.connect(connectOptions);
            client.subscribe(subTopic.getSubTopic());
            System.out.println("Json object " + jsonObject);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    while (!client.isConnected()) {
                        try {
                            Thread.sleep(5000);
                            client = new MqttClient(brokerURL, clientID, persistence);
                            client.subscribe(subTopic.getSubTopic());
                            System.out.println("Kết nối lại thành công!!");
                        }
                        catch(InterruptedException | MqttException e) {
                            e.printStackTrace();
                        }
                    }
                }
                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) {
                    String message = new String(mqttMessage.getPayload());
                    JSONObject json = JsonUtil.parseJson(message);
                    if(json.containsKey("check")) {
                        checkResponse = true; // đánh dấu cliet đã nhận được lệnh ==> không gửi lại lệnh nữa
                        System.out.println("Response " + message);
                        responseMessage = JsonUtil.parseJson(message);
                        String status = (String) responseMessage.get("statusModule");
                        System.out.println("Phản hồi từ client: " + responseMessage.get("statusCmd"));
                        // client phản hồi đã nhận được lệnh
                        if (responseMessage.containsKey("statusCmd") && (responseMessage.get("statusCmd").equals("OK") || responseMessage.get("statusCmd").equals("restart"))) {
                            System.out.println("Message " + responseMessage.get("message"));
                            // update status cmd history
                            updateCmdHistory(idCmd, -1, 1);
                        }
                        // client phản hổi đã thực hiện lệnh thành công hoặc thất bại
                        else if(status.equals("1") ||  status.equals("2")) {
                            System.out.println("Status cmd " + json.get("message"));
                            pId = (String) json.get("PID");
                            saveModuleHistory(status, 2, probeModuleEntity);
                        }
                    }
                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                }
            });
            // send message
            MqttMessage message = new MqttMessage(jsonObject.getBytes());
            message.setQos(2);
            client.publish(subTopic.getSubTopic(), message);
            // gửi lại lệnh khi chưa nhận được phản hồi từ client
            while (true) {
                Thread.sleep(5000);
                if (retry <= 2 && !checkResponse) {
                    retry++;
                    // Gửi lại tin nhắn
                    client.publish(subTopic.getSubTopic(), message);
                } else if (!checkResponse && retry > 2) {
                    if (retry > 2) {
                        updateCmdHistory(idCmd, 3, 4);
                        Boolean clientIsDisconnect = checkClientIsDisconnect(probe.getClientId());
                        if (!clientIsDisconnect) {
                            // Thông báo cho FE client đã mất kết nối
                            System.out.println("Send to front end " + "đã mất kết nối tới broker");
                            return responseMessageToFE("Probe " + probe.getName() + " đã mất kết nối tới broker", probeModuleEntity, "4", 2);
                        } else {
                            // Thông báo gửi lệnh thất bại
                            System.out.println("Send to front end " + "không nhận được yêu cầu thực hiện");
                            return responseMessageToFE("Probe " + probe.getName() + " không nhận được yêu cầu thực hiện", probeModuleEntity, "4", 2);
                        }
                    }
                } else if(checkResponse) {
                    break;
                }
            }
            // client đã nhận được lệnh, kiểm tra xem lệnh chạy thành công hay thất bại
            while(true) {
                Thread.sleep(2000);
                String statusModule = (String) responseMessage.get("statusModule");
                System.out.println("Status module " + statusModule);
                // TH nhận được phản hồi về trạng thái module
                if (statusModule != null && (statusModule.equals("1") || statusModule.equals("2"))) {
                    System.out.println("Module stop thành công hoặc thất bại" + responseMessage.toJSONString());
                    String mess = statusModule.equals("2") ? "Module stop thành công" : "Module stop thất bại";
                    return responseMessageToFE(mess, probeModuleEntity, statusModule, 2);
                }
                // TH không nhận được phản hồi
                else if (count >= 3) {
                    System.out.println("Client không có phản hồi " + responseMessage.toJSONString());
                    // có nên gửi lại không??
                    return responseMessageToFE("Không nhận được phản hồi từ client", probeModuleEntity, "4", 2);
                } else {
                    count++;
                }
            }
        }
        catch (Exception me) {
            me.printStackTrace();
            System.out.println("Chạy module lỗi rồi!");
            return JsonUtil.createJsonResponse("Error", "0");
        }
//        return null;
    }
    @Override
    public void getStatusModulePeriodically() {
        List<ProbeEntity> listProbes = probeRepository.findAll();
        // danh sách topic - listModule của từng probe đc để gưi tới client để kiểm tra trạng thái
        Map<String, String> messageToClient = new HashMap<>();
        for(ProbeEntity probe : listProbes) {
            List<ProbeModuleEntity> listProbeModule = moduleProbeRepository.findAllModuleByProbeIdAndStatus(probe.getId(), "Running", "Pending");
            String json = JsonUtil.createJsonStatus("getStatus", listProbeModule);
            messageToClient.put(probe.getPubTopic(), json);
        }
    }

    private String sendMessageGetStatus(Map<String, String> messageToClient, List<ProbeEntity> listProbe) {
        count = -2;
        checkResponse = false;
        responseMessage = new JSONObject();
        // lấy thông tin server
        ServerEntity server = serverRepository.findAll().get(0);
        // lấy ra các option của server
        ProbeOptionEntity probeOption = server.getProbeOptionEntity();
        // lấy ra clientId của server
        String clientID = server.getServerIdConnect();
        // tạo json để gửi tới client
         MqttConnectOptions connectOptions = createOption(probeOption);
        // kết nối server voi broker
        List<BrokerEntity> broker = brokerRepository.findAll();
        String brokerURL = broker.get(0).getUrl();
        // connect to broker
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            client = new MqttClient(brokerURL, clientID, persistence);
            if (client.isConnected()) {
                client.disconnect();
                client = new MqttClient(brokerURL, clientID, persistence);
            }
            client.connect(connectOptions);
            // Đăng kí để nhận tin phản hồi từ topic của client
            for (ProbeEntity probe : listProbe) {
                client.subscribe(probe.getPubTopic());
                System.out.println("Đã subscribe tới topic " + probe.getPubTopic());
            }
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    while (!client.isConnected()) {
                        try {
                            Thread.sleep(5000);
                            client = new MqttClient(brokerURL, clientID, persistence);
                            for (ProbeEntity probe : listProbe) {
                                client.subscribe(probe.getPubTopic());
                                System.out.println("Đã subscribe tới topic " + probe.getPubTopic());
                            }
                            System.out.println("Kết nối lại thành công!!");
                        } catch (InterruptedException | MqttException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) {
                    String message = new String(mqttMessage.getPayload());
                    JSONObject json = JsonUtil.parseJson(message);
                    // kiểm tra để đảm bản không dính vòng lặp vô hạn khi server gửi tin nhắn tới topic
                    if (json.containsKey("check")) {
                        System.out.println("Response(phản hồi từ client) " + message);
                        // chuyển tin nhắn từ client gửi tới thành chuỗi json object
                        responseMessage = JsonUtil.parseJson(message);
                        String status = (String) responseMessage.get("status");
                        // client phản hồi đã nhận được lệnh
                        if (responseMessage.get("statusCmd").equals("restart")) {
                            System.out.println("Message " + responseMessage.get("message"));
                        }
                        // client phản hổi đã thực hiện lệnh thành công hoặc thất bại
                        else if (status.equals("1") || status.equals("2")) {
                            System.out.println("Status cmd " + json.get("message"));
                            pId = (String) json.get("PID");
//                            saveModuleHistory(status, 2, probeModuleEntity);
                        }
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                }
            });
            // gửi tin nhắn tới topic
            MqttMessage message = new MqttMessage(jsonObject.getBytes());
            message.setQos(2);
            client.publish(subTopic.getSubTopic(), message);
        }
        catch (Exception e) {

        }
        return null;
    }

    private String saveCmd(ProbeModuleEntity probeModuleEntity) {
        try {
            String id = System.nanoTime() + "_" + probeModuleEntity.getIdProbe();
            CmdHistoryEntity cmd = new CmdHistoryEntity();
            cmd.setId(id);
            cmd.setIdProbeModule(probeModuleEntity.getId());
            cmd.setIdProbe(probeModuleEntity.getIdProbe());
            cmd.setArg(probeModuleEntity.getArg());
            cmd.setCaption(probeModuleEntity.getCaption());
            cmd.setCommand(probeModuleEntity.getCommand());
            cmd.setPath(probeModuleEntity.getPath());
            cmd.setAtTime(new Date(System.currentTimeMillis()));
            cmd.setMessage("");
            cmd.setStatus(0);
            cmd.setRetryTimes(0);
            cmd = cmdHistoryRepository.save(cmd);
            return cmd.getId();
        }
        catch (NullPointerException e) {
            System.out.println("Save cmd error");
            e.printStackTrace();
            return null;
        }
    }
    private void updateCmdHistory(String idCmd, Integer retry, Integer status) {
        CmdHistoryEntity cmdHistoryEntity = cmdHistoryRepository.findById(idCmd).orElse(null);
        if(retry >= 0) {
            cmdHistoryEntity.setRetryTimes(retry);
        }
        cmdHistoryEntity.setModifiledate(new Date(System.currentTimeMillis()));
        if(!status.equals(0)) {
            cmdHistoryEntity.setStatus(status);
            if(status.equals(1)) {
                cmdHistoryEntity.setMessage("Thành công");
            }
            else {
                cmdHistoryEntity.setMessage("Thất bại");
            }
        }
        cmdHistoryRepository.save(cmdHistoryEntity);
    }
    private JSONObject responseMessageToFE(String message, ProbeModuleEntity probeModuleEntity, String status, Integer statusExcept) {
        Integer statusResult = statusResult(statusExcept, status);
        saveModuleHistory(status, statusExcept, probeModuleEntity);
        return JsonUtil.createJsonResponse(message, statusResult.toString());
    }
    private Boolean checkClientIsDisconnect(String clientId) {
        return ApiCheckConnect.checkExistClient(clientId);
    }
    private void saveModuleHistory(String status, Integer statusExcept, ProbeModuleEntity probeModuleEntity) {
        Integer statusResult = statusResult(statusExcept, status);
        // lưu thông tin module history
        ModuleHistoryEntity moduleHistoryEntity = new ModuleHistoryEntity();
        String id = String.valueOf(System.nanoTime());
        moduleHistoryEntity.setIdModuleHistory(id);
        moduleHistoryEntity.setIdProbe((Integer) responseMessage.get(""));
        moduleHistoryEntity.setContent((String)responseMessage.get("content"));
        moduleHistoryEntity.setTitle((String)responseMessage.get("title"));
        moduleHistoryEntity.setAtTime(new Date(System.currentTimeMillis()));
        moduleHistoryEntity.setCaption((String)responseMessage.get("caption"));
        moduleHistoryEntity.setArg((String)responseMessage.get("arg"));
        moduleHistoryEntity.setStatus(statusResult);
        moduleHistoryEntity.setModuleName((String)responseMessage.get("moduleName"));
        moduleHistoryRepository.save(moduleHistoryEntity);

        // cập nhật status probe_module
        String statusModuleProbe;
        if(statusResult.equals(1)) {
            statusModuleProbe = "Running";
        }
        else if (statusResult.equals(2)) {
            statusModuleProbe = "Stoped";
        }
        else if(statusResult.equals(3)) {
            statusModuleProbe = "Failed";
        }
        else {
            statusModuleProbe = "Pending";
        }
        if(pId != null && !pId.equals(-1)) {
            probeModuleEntity.setProcessId(pId);
            System.out.println("PID " + pId);
        }
        probeModuleEntity.setStatus(statusModuleProbe);
        moduleProbeRepository.save(probeModuleEntity);
    }
    private Integer statusResult(Integer statusExcept, String statusProcess) {
        if(statusExcept.equals(1)) {
            if(statusProcess.equals("1")) {
                return 1;
            }
            else if(statusProcess.equals("2")) {
                return 3;
            }
            return 4;
        }
        else {
            if(statusProcess.equals("1")) {
                return 3;
            }
            else if(statusProcess.equals("2")) {
                return 2;
            }
            return 4;
        }
    }
    private MqttConnectOptions createOption(ProbeOptionEntity probeOption) {
        try {
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setUserName(probeOption.getUserName());
            connectOptions.setPassword(probeOption.getPassword().toCharArray());
            connectOptions.setKeepAliveInterval(probeOption.getKeepAlive());
            connectOptions.setCleanSession(probeOption.getCleanSession());
            return connectOptions;
        }
        catch (Exception e) {
            System.out.println("Create connection error");
            e.printStackTrace();
            return null;
        }
    }

    private void updateProbeModule() {

    }
}
