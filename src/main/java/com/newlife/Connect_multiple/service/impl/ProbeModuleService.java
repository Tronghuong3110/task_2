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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private MqttClient client = null;
    private ExecutorService executorService = Executors.newFixedThreadPool(10);
    @Override
    public List<ProbeModuleDto> findAllProbeModule(String moduleName, String status, Integer idProbe) {
        try {
            List<ProbeModuleEntity> listProbeModuleEntity = moduleProbeRepository.findAllByProbeNameOrStatusAndIdProbe(moduleName, status, idProbe);
            List<ProbeModuleDto> listProbeModuleDto = new ArrayList<>();
            for(ProbeModuleEntity entity : listProbeModuleEntity) {
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
    // Yêu cầu chạy module
    @Override
    public JSONObject runModule(Integer idProbeModule) {
        try {
            retry = 0;
            count = -2;
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
                if (client == null) {
                    client = new MqttClient(brokerURL, clientID, persistence);
                    client.connect(connectOptions);
                    client.subscribe(subTopic.getSubTopic());
                }
                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable throwable) {
                        while (!client.isConnected()) {
                            try {
                                System.out.println("Đang kết nối lại...");
                                Thread.sleep(5000);
                                client.reconnect();
//                                client.subscribe(subTopic.getSubTopic());
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
//                            executorService.submit(() -> {
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
                                    saveModuleHistory(status, 1, probeModuleEntity, (String) json.get("PID"), (String)json.get("nameProcess"));
                                }
//                            });
                        }
                    }
                    @Override
                    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    }
                });
                System.out.println("Json object " + jsonObject);
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
                                checkResponse = false;
                                return responseMessageToFE("Probe " + probe.getName() + " đã mất kết nối tới broker", probeModuleEntity, "2", 1, null, null);
                            } else {
                                // Thông báo gửi lệnh thất bại
                                System.out.println("Send to front end " + "không nhận được yêu cầu thực hiện");
                                checkResponse = false;
                                return responseMessageToFE("Probe " + probe.getName() + " không nhận được yêu cầu thực hiện", probeModuleEntity, "2", 1, null, null);
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
                        checkResponse = false;
                        return responseMessageToFE(mess, probeModuleEntity, statusModule, 1, (String) responseMessage.get("PID"), (String)responseMessage.get("nameProcess"));
                    }
                    // TH không nhận được phản hồi
                    else if (count >= 3) {
                        System.out.println("Client không có phản hồi " + responseMessage.toJSONString());
                        // có nên gửi lại không??
                        checkResponse = false;
                        return responseMessageToFE("Không nhận được phản hồi từ client", probeModuleEntity, "2", 1, null, null);
                    } else {
                        count++;
                    }
                }
            }
            catch (Exception me) {
                me.printStackTrace();
                System.out.println("Kết nối broker lỗi");
                return JsonUtil.createJsonResponse("Error", "0");
            }
        }
        catch (Exception e) {
            System.out.println("Chạy lệnh lỗi rồi");
            e.printStackTrace();
        }
        return null;
    }
    // Yêu cầu dừng module
    @Override
    public Object stopModule(Integer idProbeModule) {
        try {
            retry = 0;
            count = -2;
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
            String jsonObject = JsonUtil.createJson(probeModuleEntity, idCmd, Optional.of("cmd /c taskkill /F /PID "), Optional.of("pkill -f "), "stop");
            MqttConnectOptions connectOptions = createOption(probeOption);
            // kết nối server voi broker
            List<BrokerEntity> broker = brokerRepository.findAll();
            String brokerURL = broker.get(0).getUrl();
            // connect to broker
            MemoryPersistence persistence = new MemoryPersistence();
            try {
                if (client == null) {
                    client = new MqttClient(brokerURL, clientID, persistence);
                    client.connect(connectOptions);
                    client.subscribe(subTopic.getSubTopic());
                }
                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable throwable) {
                        while (!client.isConnected()) {
                            try {
                                Thread.sleep(5000);
                                client = new MqttClient(brokerURL, clientID, persistence);
//                                client.subscribe(subTopic.getSubTopic());
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
//                            executorService.submit(() ->{
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
                                    saveModuleHistory(status, 2, probeModuleEntity, (String) json.get("PID"), "");
                                }
//                            });
                        }
                    }
                    @Override
                    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    }
                });
                System.out.println("Json object " + jsonObject);
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
                                checkResponse = false;
                                return responseMessageToFE("Probe " + probe.getName() + " đã mất kết nối tới broker", probeModuleEntity, "1", 2, null, null);
                            } else {
                                // Thông báo gửi lệnh thất bại
                                System.out.println("Send to front end " + "không nhận được yêu cầu thực hiện");
                                checkResponse = false;
                                return responseMessageToFE("Probe " + probe.getName() + " không nhận được yêu cầu thực hiện", probeModuleEntity, "1", 2, null, null);
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
                        checkResponse = false;
                        return responseMessageToFE(mess, probeModuleEntity, statusModule, 2, (String) responseMessage.get("PID"), (String) responseMessage.get("nameProcess"));
                    }
                    // TH không nhận được phản hồi
                    else if (count >= 3) {
                        System.out.println("Client không có phản hồi " + responseMessage.toJSONString());
                        // có nên gửi lại không??
                        checkResponse = false;
                        return responseMessageToFE("Không nhận được phản hồi từ client", probeModuleEntity, "1", 2, null, null);
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
        catch (Exception e) {
            System.out.println("Dừng lệnh lỗi rồi");
            e.printStackTrace();
        }
        return null;
    }
    // kiểm tra trạng thái các module theo chu kì
    @Override
    public void getStatusModulePeriodically() {
        // status probe: connected 1 or disconnected 0
        List<ProbeEntity> listProbes = probeRepository.findProbeByStatus("1");
        // danh sách topic - listModule của từng probe đc để gưi tới client để kiểm tra trạng thái
        Map<String, String> messageToClient = new HashMap<>();
        for(ProbeEntity probe : listProbes) {
            List<ProbeModuleEntity> listProbeModule = moduleProbeRepository.findAllModuleByProbeIdAndStatus(probe.getId(), "Running", "Pending");
            String json = JsonUtil.createJsonStatus("getStatus", listProbeModule);
            messageToClient.put(probe.getPubTopic(), json);
        }
        if(!checkResponse) {
            sendMessageGetStatus(messageToClient, listProbes);
        }
    }
    // gửi tin nhắn tới các probe để hỏi trạng thái của các module trong probe
    private String sendMessageGetStatus(Map<String, String> messageToClient, List<ProbeEntity> listProbe) {
        try {
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
                if (client == null) {
                    client = new MqttClient(brokerURL, clientID, persistence);
                    client.connect(connectOptions);
                    // Đăng kí để nhận tin phản hồi từ topic của client
                    for (ProbeEntity probe : listProbe) {
                        client.subscribe(probe.getPubTopic());
                        System.out.println("Đã subscribe tới topic " + probe.getPubTopic());
                    }
                }
                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable throwable) {
                        while (!client.isConnected()) {
                            try {
                                Thread.sleep(5000);
                                client.reconnect();
//                                client = new MqttClient(brokerURL, clientID, persistence);
//                                for (ProbeEntity probe : listProbe) {
//                                    client.subscribe(probe.getPubTopic());
//                                    System.out.println("Đã subscribe tới topic " + probe.getPubTopic());
//                                }
                                System.out.println("Kết nối lại thành công!!");
                            } catch (InterruptedException | MqttException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void messageArrived(String s, MqttMessage mqttMessage) {
                        String message = new String(mqttMessage.getPayload());
                        // chuyển tin nhắn từ client dạng string sang json
                        JSONObject json = JsonUtil.parseJson(message);
                        // kiểm tra để đảm bản không dính vòng lặp vô hạn khi server gửi tin nhắn tới topic
                        // có biến check ==> tin nhắn được gửi từ client
//                        executorService.submit(() -> {
                            if (json.containsKey("check")) {
                                // TH client thông báo nhận được tin nhắn
                                try {
                                    if (json.containsKey("statusCmd") && json.get("statusCmd").equals("OK")) {
                                        System.out.println("Message " + json.get("message"));
                                    }
                                    // TH client thông báo danh sách các trạng thái khi đã xử lý xong
                                    else {
                                        System.out.println("Response(phản hồi từ client) " + message);
                                        // chuyển tin nhắn từ client gửi tới thành chuỗi json object
                                        String messageFromClient = (String) json.get("message");
                                        System.out.println("Tin nhắn (get Status) " + messageFromClient);
                                        // danh sách trạng thái của các module của probe
                                        System.out.println("Danh sách trạng thái các module của probe thứ " + (String) json.get("idProbe"));
                                        JSONArray jsonArray = (JSONArray) json.get("listStatus");
                                        // duyệt để cập nhật trạng thái của các module của probe
                                        for(Object object : jsonArray) {
                                            updateProbeModule((JSONObject) object);
                                        }
                                    }
                                }
                                catch (Exception e) {
                                    System.out.println("Lỗi tại chỗ nhận tin nhắn tại lấy trạng thái theo chu kì");
                                    e.printStackTrace();
                                }
                            }
//                        });
                    }
                    @Override
                    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    }
                });
                // gửi tin nhắn tới từng topic
                for(ProbeEntity probe : listProbe) {
                    String mess = messageToClient.get(probe.getPubTopic());
                    MqttMessage message = new MqttMessage(mess.getBytes());
                    message.setQos(2);
                    client.publish(probe.getPubTopic(), message);
                }
            }
            catch (Exception e) {
                System.out.println("Kết nối broker lỗi");
                e.printStackTrace();
            }
        }
        catch (Exception e) {
            System.out.println("Gửi tin nhắn lỗi");
            e.printStackTrace();
        }
        return null;
    }
    // Lưu thông tin lịch sử gửi lệnh
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
    // Cập nhật trạng thái của lịch sử gửi lệnh từ server tới client
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
    // tạo json gửi đến front end
    private JSONObject responseMessageToFE(String message, ProbeModuleEntity probeModuleEntity, String status, Integer statusExcept, String pId, String nameProcess) {
        String statusResult = statusResult(statusExcept, status);
        saveModuleHistory(status, statusExcept, probeModuleEntity, pId, nameProcess);
        return JsonUtil.createJsonResponse(message, statusResult.toString());
    }
    // Kiểm tra xem probe có còn kết nói với broker không
    private Boolean checkClientIsDisconnect(String clientId) {
        return ApiCheckConnect.checkExistClient(clientId);
    }
    // Lưu thông tin lịch sử của các module
    private void saveModuleHistory(String status, Integer statusExcept, ProbeModuleEntity probeModuleEntity, String pId, String nameProcess) {
        try {
            String statusResult = statusResult(statusExcept, status);
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
            if(pId != null && !pId.equals(-1)) {
                probeModuleEntity.setProcessId(pId);
                System.out.println("PID " + pId);
            }
            probeModuleEntity.setStatus(statusResult);
            moduleProbeRepository.save(probeModuleEntity);
        }
        catch (Exception e) {
            System.out.println("Lưu thông tin lịch sử module lỗi!!");
            e.printStackTrace();
        }
    }
    // So status trong task manager của client(1(Running), 2(Stoped), 3(Pending)) với status mà người dùng chọn(1(Running), 2(Stopped))
    private String statusResult(Integer statusExcept, String statusProcess) {
        if(statusExcept.equals(1)) {
            if(statusProcess.equals("1")) {
                return "Running";
            }
            else if(statusProcess.equals("2")) {
                return "Failed";
            }
            return "Pending";
        }
        // trường hợp stop
        else {
            if(statusProcess.equals("1")) {
                return "Failed";
            }
            else if(statusProcess.equals("2")) {
                return "Stoped";
            }
            return "Pending";
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
    // Cập nhật trạng thái các module của probe
    private void updateProbeModule(JSONObject jsonObject) {
        Integer idProbeModule = Integer.parseInt((String) jsonObject.get("id_probe_module"));
        // status lấy từ process của client
        String statusResult = (String) jsonObject.get("status");
        ProbeModuleEntity probeModuleEntity = moduleProbeRepository.findById(idProbeModule).orElse(null);
        // TH module có thay đổi về trạng thái
        if(!probeModuleEntity.getStatus().equals(statusResult)) {
            String pId = null;
            // TH module không còn chạy nữa (lỗi) ==> set lại processId = 0 (đã dừng)
            if(statusResult.equals("Failed")) {
                pId = "0";
            }
            ModuleHistoryEntity moduleHistoryEntity = new ModuleHistoryEntity();
            String id = String.valueOf(System.nanoTime());
            moduleHistoryEntity.setIdModuleHistory(id);
            moduleHistoryEntity.setIdProbe(probeModuleEntity.getIdProbe());
            moduleHistoryEntity.setContent(null);
            moduleHistoryEntity.setTitle(null);
            moduleHistoryEntity.setAtTime(new Date(System.currentTimeMillis()));
            moduleHistoryEntity.setCaption(probeModuleEntity.getCaption());
            moduleHistoryEntity.setArg(probeModuleEntity.getArg());
            moduleHistoryEntity.setStatus(statusResult);
            moduleHistoryEntity.setModuleName(probeModuleEntity.getModuleName());
            moduleHistoryRepository.save(moduleHistoryEntity);

            probeModuleEntity.setStatus(statusResult);
            if (pId != null) {
                probeModuleEntity.setProcessId("0");
            }
            moduleProbeRepository.save(probeModuleEntity);
        }
    }

    @Override
    public String delete(Integer idProbeModule) {
        try {
            //ProbeModuleEntity probeModule = moduleProbeRepository.findById(idProbeModule).orElse(null);
            moduleProbeRepository.deleteById(idProbeModule);
            return "Delete success";
        } catch (Exception e) {
            return "Delete failed";
        }
    }


            // Thêm mới 1 module của 1 probe
    @Override
    public String saveProbeModule(ProbeModuleDto probeModuleDto) {
        try {
            ProbeModuleEntity probeModule = ProbeModuleConverter.toEntity(probeModuleDto);

            String cmd = probeModule.getCommand();
            if (moduleProbeRepository.existsByCommand(cmd)) {
                return "trùng câu lệnh command";
            } else {
                probeModule.setProcessStatus(2); // dừng
                probeModule.setExpectStatus(0);
                moduleProbeRepository.save(probeModule);
                return "Save probe module success";
            }
        } catch (Exception e) {
            return "Save probe module failed";
        }
    }
    // Man Dashboard
    @Override
    public Integer countModuleByStatus(String status) {
        try {
            Integer module = moduleProbeRepository.countAllByStatus(status);
            return module;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
