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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        try {
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
            ProbeEntity probe = probeRepository.findById(probeModuleEntity.getIdProbe()).orElse(null);
            // create message json
            String jsonObject = JsonUtil.createJson(probeModuleEntity, idCmd, Optional.ofNullable(null), Optional.ofNullable(null));
            MqttConnectOptions connectOptions = createConnection(probeOption);
            // kết nối server voi broker
            MqttClient client = connectToClient(probeModuleEntity, probeOption, clientID, idCmd, connectOptions, subTopic.getSubTopic());
            // set callback nhận response từ client
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    while (!client.isConnected()) {
                        try {
                            System.out.println("Đang kết nối lại...");
                            Thread.sleep(5000);
                            connectOptions.setUserName(probeOption.getUserName());
                            connectOptions.setPassword(probeOption.getPassword().toCharArray());
                            client.connect(connectOptions);
                            client.subscribe(subTopic.getSubTopic());
                            System.out.println("Kết nối lại thành công!!");
                        }
                        catch(InterruptedException | MqttException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    String message = new String(mqttMessage.getPayload());
                    responseMessage = JsonUtil.parseJson(message);
                    Object status = responseMessage.get("statusModule");
                    System.out.println("Phản hồi từ client: ");
                    // client phản hồi đã nhận được lệnh
                    if (responseMessage.containsKey("statusCmd") && (responseMessage.get("statusCmd").equals("OK") || responseMessage.get("statusCmd").equals("restart"))) {
                        checkResponse = true; // đánh dấu cliet đã nhận được lệnh ==> không gửi lại lệnh nữa
                        System.out.println(responseMessage.get("message"));
                        // update status cmd history
                        updateCmdHistory(idCmd, -1, 1);
                    }
                    // client phản hổi đã thực hiện lệnh thành công hoặc thất bại
                    else if(status.equals("1") ||  status.equals("3")) {
                        saveModuleHistory((String) status, 1, probeModuleEntity);
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
            // gửi lệnh tới client
            client.connect(connectOptions);
            client.subscribe(subTopic.getSubTopic());
            sendMessage(client, jsonObject, subTopic.getSubTopic(), 0, idCmd, 0);
            // gửi lại lệnh khi chưa nhận được phản hồi từ client
            int retry = 0;
            while (checkResponse == false && retry <= 2) {
                retry++;
                sendMessage(client, jsonObject, subTopic.getSubTopic(), retry, idCmd, 0);
                Thread.sleep(5);
            }
            // TH gửi lệnh không thành công
            if(checkResponse == false) {
                // gửi lệnh thất bại
                updateCmdHistory(idCmd, 3, 4);
                Boolean clientIsDisconnect = checkClientIsDisconnect(clientID);
                if(!clientIsDisconnect) { // TH client mất kết noois tới broker
                    // thông báo cho FE client đã mất kết nối
                    return responseMessageToFE("Probe " + probe.getName() + " đã mất kết nối tới broker", probeModuleEntity);
                }
                else {
                    // thông báo gửi lệnh thất bại
                    return responseMessageToFE("Probe " + probe.getName() + " không nhận được yêu cầu thực hiện", probeModuleEntity);
                }
            }
            // TH client nhận được lệnh và chạy lệnh nhưng không có phản hổi chạy thành công hay thất bại
            String statusModule = (String) responseMessage.get("statusModule");
            Thread.sleep(5);
            if(statusModule != null) { // TH nhận được phản hồi về trạng thái module
                return responseMessageToFE("", probeModuleEntity);
            }
            // Có nên gửi lại lệnh không???
            return responseMessageToFE("Không nhận được phản hồi từ client", probeModuleEntity);
        }
        catch (Exception e) {
            System.out.println("Chạy module lỗi rồi!");
            e.printStackTrace();
            return JsonUtil.createJsonResponse("Error", "0");
        }
    }

    //Kết nối server với broker
    private MqttClient connectToClient(ProbeModuleEntity probeModuleEntity, ProbeOptionEntity probeOption, String clientID, String idCmd, MqttConnectOptions connectOptions, String subTopic) {
        try {
            // get subtopic to server subscribe send message to client
            // chi co 1 Broker duy nhat
            List<BrokerEntity> broker = brokerRepository.findAll();
            String brokerURL = broker.get(0).getUrl();
            // connect to broker
            try {
                MemoryPersistence persistence = new MemoryPersistence();
                MqttClient client = new MqttClient(brokerURL, clientID, persistence);

                // set callback
                System.out.println("Subscribe = " + subTopic);
                return client;
            } catch (MqttException me) {
                System.out.println("reason " + me.getReasonCode());
                System.out.println("msg " + me.getMessage());
                System.out.println("loc " + me.getLocalizedMessage());
                System.out.println("cause " + me.getCause());
                System.out.println("excep " + me);
                me.printStackTrace();
            }
        }
        catch (Exception e) {
            System.out.println("Connect to broker fail");
            e.printStackTrace();
            return null;
        }
        return null;
    }

    private void sendMessage(MqttClient client, String jsonObject, String pubTopic, Integer retry, String idCmd, Integer status) {
        // cập nhật bảng cmd history // từ lần gửi lại đầu tiên
        if(retry >= 1) {
            updateCmdHistory(idCmd, retry, status);
        }
        int qos = 0;
//        System.out.println(jsonObject + " " + pubTopic);

        try {
            MqttMessage message = new MqttMessage(jsonObject.getBytes());
            message.setQos(qos);
            client.publish(pubTopic, message);
        }
        catch (MqttException e) {
            System.out.print("Send message error");
            e.printStackTrace();
        }
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

    private JSONObject responseMessageToFE(String message, ProbeModuleEntity probeModuleEntity) {
        if(!message.isEmpty()) {
            saveModuleHistory("4", 1, probeModuleEntity);
            return JsonUtil.createJsonResponse(message, "4");
        }
        // lưu thông tin module_history vào database
        Object status = responseMessage.get("statusModule");
        if(status.equals("1")) {
            // lưu thông tin module_history
            saveModuleHistory("1", 1, probeModuleEntity);
            return JsonUtil.createJsonResponse("Chạy module thành công", "1");
        }
        if(status.equals("3")) {
//            return "Chạy lệnh thất bại";
            saveModuleHistory("3", 1, probeModuleEntity);
            return JsonUtil.createJsonResponse("Chạy module thất bại", "4");
        }
        return null;
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
            if(statusProcess.equals("1") || statusProcess.equals("3")) {
                return 3;
            }
            return 2;
        }
    }

    private MqttConnectOptions createConnection(ProbeOptionEntity probeOption) {
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
}
