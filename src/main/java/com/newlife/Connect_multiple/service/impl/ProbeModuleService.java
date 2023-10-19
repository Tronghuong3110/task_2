package com.newlife.Connect_multiple.service.impl;

import com.newlife.Connect_multiple.api.ApiCheckConnect;
import com.newlife.Connect_multiple.converter.ProbeConverter;
import com.newlife.Connect_multiple.converter.ProbeModuleConverter;
import com.newlife.Connect_multiple.dto.ProbeDto;
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
import java.util.concurrent.*;

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
    private MqttClient client = null;

    // các biến phục vụ cho stop
    private Map<String, Boolean> clientStatusMapStop = new ConcurrentHashMap<>(); // lưu trữ trạng thái để đánh dấu client đã nhận được lệnh
    private ConcurrentHashMap<String, JSONObject> responseMessageMapStop = new ConcurrentHashMap<>(); // lưu trữ phản hồi của các client khi yêu cầu stop
    private Queue<SubtopicServerEntity> topicrequestStopResend = new LinkedList<>(); // lưu trữ thứ tự các topic khi thực hiện stop (để kiểm tra xem có cần gửi lại lệnh không)
    private Queue<SubtopicServerEntity> topicrequestStopResPonseToClient = new LinkedList<>();
    private  Map<String, ProbeModuleEntity> probeModuleEntityMapStop = new ConcurrentHashMap<>();
    private Map<String, Boolean> checkErrorMapStop = new ConcurrentHashMap<>();

    // các biến phục vụ cho run
    private Map<String, Boolean> clientStatusMapRun = new ConcurrentHashMap<>(); // lưu trữ trạng thái để đánh dấu client đã nhận được lệnh
    private Queue<SubtopicServerEntity> topicrequestRunResend = new LinkedList<>(); // lưu trữ thứ tự các topic khi thực hiện stop (để kiểm tra xem có cần gửi lại lệnh không)
    private Queue<SubtopicServerEntity> topicrequestRun = new LinkedList<>();
    private  Map<String, ProbeModuleEntity> probeModuleEntityMapRun = new ConcurrentHashMap<>();
    private Map<String, Boolean> checkErrorMapRun = new ConcurrentHashMap<>();
    private Map<String, Boolean> checkResultCommand = new ConcurrentHashMap<>();
    private Map<String, String> messageToClient = new ConcurrentHashMap<>();
    private Queue<String> topicCheckResultRun = new LinkedList<>();
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
        ProbeModuleEntity probeModuleEntity = moduleProbeRepository.findById(idProbeModule).orElse(null); // Lấy ra module cần chạy
        try {
            ServerEntity server = serverRepository.findAll().get(0); // Lấy thông tin server
            ProbeOptionEntity probeOption = server.getProbeOptionEntity(); // Lấy ra các option của server
            String clientID = server.getServerIdConnect(); // Lấy ra clientId của server
            if (probeModuleEntity == null) {
                return JsonUtil.createJsonResponse("Không tồn tại module có id = " + idProbeModule, "5");
            }

            // Kết nối server với broker
            List<BrokerEntity> broker = brokerRepository.findAll();
            String brokerURL = broker.get(0).getUrl();
            MemoryPersistence persistence = new MemoryPersistence();
            MqttConnectOptions connectOptions = createOption(probeOption);
            if (client == null) {
                client = new MqttClient(brokerURL, clientID, persistence);
                client.connect(connectOptions);
            }
            // Set lại trường loading = 1 (đang loading), loading = 0 (loading xong)
            probeModuleEntity.setLoading(1);
            moduleProbeRepository.save(probeModuleEntity);

            SubtopicServerEntity subTopic = subtopicOnServerRepository.findByIdProbe(probeModuleEntity.getIdProbe()).orElse(null);
            topicrequestRunResend.add(subTopic); // tạo hàng đợi để gửi lại tin nhắn khi client không nhận được tin nhăắn
            topicrequestRun.add(subTopic); // tạo hàng đợi để gửi tin nhắn
            probeModuleEntityMapRun.put(subTopic.getSubTopic(), probeModuleEntity); // lưu probeModule theo topic của probe

            ProbeModuleEntity probeModule = probeModuleEntityMapRun.get(topicrequestRun.peek().getSubTopic());
            String idCmd = saveCmd(probeModule); // Lưu thông tin lệnh vào database
            ProbeEntity probe = probeRepository.findById(probeModule.getIdProbe()).orElse(new ProbeEntity());
            topicCheckResultRun.add(idCmd + "-" + topicrequestRun.peek());
            // Create message json
            String jsonObject = JsonUtil.createJson(probeModule, "idCmd", Optional.ofNullable(null), Optional.ofNullable(null), "run");
            messageToClient.put(topicrequestRun.peek().getSubTopic(), jsonObject);
            // đăng ký topic và nhận tin nhắn gửi về
            client.subscribe(subTopic.getSubTopic(), (topic, msg) -> {
                String message = new String(msg.getPayload());
                JSONObject json = JsonUtil.parseJson(message);
                if (json.containsKey("check") && json.containsKey("action") && json.get("action").equals("run")) {
                    clientStatusMapRun.put(topic, true); // đánh dấu client đã nhận được lệnh, không phải gửi lại
                    Object pid = json.get("PID");
                   if(!pid.equals("")) {
                       checkResultCommand.put(topic, true); // đánh dấu đã nhận được kết quả lệnh từ client
                       solveResponseMessage(topic, json, probeModuleEntityMapRun.get(topic), topicCheckResultRun.peek().split("-")[0]);
                   }
                }
            });
            // gửi tin nhắn
            System.out.println("Tin nhắn gửi đi " + jsonObject);
            MqttMessage message = new MqttMessage(jsonObject.getBytes());
            message.setQos(2);
            System.out.println("Topic đang được gửi tin nhắn: " + subTopic.getSubTopic());
            client.publish(topicrequestRun.poll().getSubTopic(), message); // gửi tin nắn tới topic cần gửi
            // gửi lại tin nhắn khi khoong nhận được phản hồi từ client
            reSendMessage(client, probe.getClientId(), idCmd);
            // kiểm tra nếu client xử lý xong mà không có phản hồi
            try {
                while (!topicCheckResultRun.isEmpty()) {
                    Boolean check = false;
                    String tmp = topicCheckResultRun.poll();
                    String topic = tmp.split("-")[1];
                    String idCmdHistory = tmp.split("-")[0];
                    Long timeCurrent = System.currentTimeMillis();
                    Long time = 0L;
                    // set thời gian chờ là 7s, nếu sau 7s không nhận được kết quả chạy lênh ==> set cmdHistory = failed
                    while ((!checkResultCommand.containsKey(topic) || (checkResultCommand.containsKey(topic) && !checkResultCommand.get(topic))) && time <= 7) {
                        time = System.currentTimeMillis() - timeCurrent;
                        System.out.println("Chờ kết quả");
                        if(checkResultCommand.containsKey(topic) && checkResultCommand.get(topic)) {
                            check = true;
                            break;
                        }
                    }
                    if(!check) {
                        responseMessageToFE(probeModule, "2", 1, null, null);
                        clientStatusMapRun.put(topic, false);
                        updateCmdHistory(idCmdHistory, 3, 4);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception me) {
            me.printStackTrace();
            System.out.println("Kết nối broker lỗi");
            probeModuleEntity.setLoading(0);
            moduleProbeRepository.save(probeModuleEntity);
            return JsonUtil.createJsonResponse("Error", "0");
        }
        return null;
    }
    // Yêu cầu dừng module
    @Override
    public Object stopModule(Integer idProbeModule) {
        ProbeModuleEntity probeModuleEntity = moduleProbeRepository.findById(idProbeModule).orElse(null);
        try {
            ServerEntity server = serverRepository.findAll().get(0);
            ProbeOptionEntity probeOption = server.getProbeOptionEntity();
            String clientID = server.getServerIdConnect();

            if (probeModuleEntity == null) {
                return JsonUtil.createJsonResponse("Không tồn tại module có id = " + idProbeModule, "5");
            }

            probeModuleEntity.setLoading(1);
            moduleProbeRepository.save(probeModuleEntity);

            SubtopicServerEntity subTopic = subtopicOnServerRepository.findByIdProbe(probeModuleEntity.getIdProbe()).orElse(null);
            topicrequestStopResend.add(subTopic); // thêm topic vào hàng đợi
            topicrequestStopResPonseToClient.add(subTopic);
            probeModuleEntityMapStop.put(subTopic.getSubTopic(), probeModuleEntity);

            String idCmd = saveCmd(probeModuleEntity);
            ProbeEntity probe = probeRepository.findById(probeModuleEntity.getIdProbe()).orElse(new ProbeEntity());

            String jsonObject = JsonUtil.createJson(probeModuleEntity, idCmd, Optional.of("cmd /c taskkill /F /PID "), Optional.of("pkill -f "), "stop");
            MqttConnectOptions connectOptions = createOption(probeOption);
            List<BrokerEntity> broker = brokerRepository.findAll();
            String brokerURL = broker.get(0).getUrl();
            MemoryPersistence persistence = new MemoryPersistence();

            if (client == null) {
                client = new MqttClient(brokerURL, clientID, persistence);
                client.connect(connectOptions);
                client.subscribe(topicrequestStopResend.peek().getSubTopic());
            }
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    while (!client.isConnected()) {
                        try {
                            Thread.sleep(5000);
//                            client.reconnect();
                            client = new MqttClient(brokerURL, clientID, persistence);
                            client.connect(connectOptions);
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
                    if (json.containsKey("check") && json.containsKey("action") && json.get("action").equals("stop")) {
                        String topicOfClient = topicrequestStopResend.peek().getSubTopic();
                        responseMessageMapStop.put(topicOfClient, json);
//                        if (topicOfClient != null) {
//                            Boolean hasReceivedCommand = clientStatusMapStop.get(topicOfClient);
//                            if (hasReceivedCommand != null && !hasReceivedCommand) {
                        clientStatusMapStop.put(topicOfClient, true);
                        System.out.println("Response " + message);
                        String status = (String) json.get("statusModule");
                        System.out.println("Phản hồi từ client: " + json.get("statusCmd"));

                        if (json.containsKey("statusCmd") && (json.get("statusCmd").equals("OK") || json.get("statusCmd").equals("restart"))) {
                            System.out.println("Message " + json.get("message"));
                            updateCmdHistory(idCmd, -1, 1);
                        } else if (status.equals("1") || status.equals("2")) {
                            System.out.println("Status cmd " + json.get("message"));
                            saveModuleHistory(status, 2, probeModuleEntityMapStop.get(topicOfClient), (String) json.get("PID"), responseMessageMapStop.get(topicOfClient));
                        }
//                            }
//                        }
                    }
                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                }
            });

            System.out.println("Json object " + jsonObject);
            MqttMessage message = new MqttMessage(jsonObject.getBytes());
            message.setQos(2);
            client.publish(topicrequestStopResend.peek().getSubTopic(), message);

            // kiểm tra xem có topic nào cần gửi lại không
            while (!topicrequestStopResend.isEmpty()) {
                String topic = topicrequestStopResend.poll().getSubTopic();
                JSONObject responseMessage = responseMessageMapStop.get(topic);
                Integer retry = 0;
                while (true) {
                    try {
                        Thread.sleep(5000);
                        // kiểm tra xem server đã nhận được phản hồi từ client có topic trong đầu hàng đợi chưa
//                        Boolean hasReceivedCommand = clientStatusMapStop.get(topic);
                        if (clientStatusMapStop.containsKey(topic) && !clientStatusMapStop.get(topic)) {
                            retry++;
                            client.publish(topic, message);
                        } else if (!clientStatusMapStop.get(topic) && retry > 2) {
                            if (retry > 2) {
                                updateCmdHistory(idCmd, 3, 4);
                                Boolean clientIsDisconnect = checkClientIsDisconnect(probe.getClientId());

                                if (!clientIsDisconnect) {
                                    System.out.println("Send to front end " + "đã mất kết nối tới broker");
                                    responseMessageToFE(probeModuleEntity, "1", 2, null, responseMessage);
                                    break; // Thoát khỏi luồng khi xử lý xong
                                } else {
                                    System.out.println("Send to front end " + "không nhận được yêu cầu thực hiện");
                                    responseMessageToFE(probeModuleEntity, "1", 2, null, responseMessage);
                                    break; // Thoát khỏi luồng khi xử lý xong
                                }
                            }
                        } else if (clientStatusMapStop.get(topic)) {
                            break; // Thoát khỏi luồng khi xử lý xong
                        }
                    } catch (InterruptedException | MqttException e) {
                        e.printStackTrace();
                    }
                }
            }

            // client đã nhận được lệnh, kiểm tra xem lệnh chạy thành công hay thất bại
            while (!topicrequestStopResPonseToClient.isEmpty()) {
                String topic = topicrequestStopResPonseToClient.poll().getSubTopic();
                ProbeModuleEntity probeModule = probeModuleEntityMapStop.get(topic);
                JSONObject responseMessage = responseMessageMapStop.get(topic);
                Integer count = -2;
                while (true) {
                    try {
                        Thread.sleep(2000);
                        String statusModule = (String) responseMessage.get("statusModule");
                        System.out.println("Status module " + statusModule);

                        if (statusModule != null && (statusModule.equals("1") || statusModule.equals("2"))) {
                            System.out.println("Module stop thành công hoặc thất bại" + responseMessage.toJSONString());
                            String mess = statusModule.equals("2") ? "Module stop thành công" : "Module stop thất bại";
                            responseMessageToFE(probeModule, statusModule, 2, (String) responseMessage.get("PID"), responseMessage);
                        } else if (count >= 3) {
                            System.out.println("Client không có phản hồi " + responseMessage.toJSONString());
                            responseMessageToFE(probeModule, "1", 2, null, responseMessage);
                        } else {
                            count++;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception me) {
            me.printStackTrace();
            System.out.println("Chạy module lỗi rồi!");
            probeModuleEntity.setLoading(0);
            moduleProbeRepository.save(probeModuleEntity);
            return JsonUtil.createJsonResponse("Error", "0");
        }
        return null;
    }
    // kiểm tra trạng thái các module theo chu kì
    @Override
    public void getStatusModulePeriodically() {
        Boolean checkProcess = false;
        // status probe: connected 1 or disconnected 0
        List<ProbeEntity> listProbes = probeRepository.findProbeByStatus(0);
        // danh sách topic - listModule của từng probe đc để gưi tới client để kiểm tra trạng thái
        Map<String, String> messageToClient = new HashMap<>();
        for(ProbeEntity probe : listProbes) {
            List<ProbeModuleEntity> listProbeModule = moduleProbeRepository.findAllModuleByProbeIdAndStatus(probe.getId(), "Running", "Pending");
            // không kiểm tra các client đang có yêu cầu thực hiện lệnh
            String json = JsonUtil.createJsonStatus("getStatus", listProbeModule);
            messageToClient.put(probe.getPubTopic(), json);
            if((clientStatusMapRun.containsKey(probe.getPubTopic()) && clientStatusMapRun.get(probe.getPubTopic())) || (clientStatusMapStop.containsKey(probe.getPubTopic()) && clientStatusMapStop.get(probe.getPubTopic()))) {
                checkProcess = true;
            }
        }
        if(!checkProcess) {
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
            // lấy toàn bộ topic mà server subscribe
            List<SubtopicServerEntity> listTopics = subtopicOnServerRepository.findAll();
            // connect to broker
            MemoryPersistence persistence = new MemoryPersistence();
            try {
                if (client == null) {
                    client = new MqttClient(brokerURL, clientID, persistence);
                    client.connect(connectOptions);
                    // Đăng kí để nhận tin phản hồi từ topic của client
                    for (SubtopicServerEntity topic : listTopics) {
                        client.subscribe(topic.getSubTopic());
                        System.out.println("Đã subscribe tới topic " + topic.getSubTopic());
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
                            if (json.containsKey("check") && json.containsKey("action") && json.get("action").equals("getStatus")) {
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
                for (SubtopicServerEntity topic : listTopics) {
                    String mess = messageToClient.get(topic.getSubTopic());
                    MqttMessage message = new MqttMessage(mess.getBytes());
                    message.setQos(2);
                    client.publish(topic.getSubTopic(), message);
                    System.out.println("Gửi tin nhắn tới topic " + topic.getSubTopic() + " " + mess);
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
            System.out.println("Lưu thông tin cmd history vào database");
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
        System.out.println("Cập nhật lại cmd history");
        if(retry >= 0) {
            cmdHistoryEntity.setRetryTimes(retry);
        }
        cmdHistoryEntity.setModifiledate(new Date(System.currentTimeMillis()));
        if(!status.equals(0)) {
            cmdHistoryEntity.setStatus(status);
            if(status.equals(1)) {
                System.out.println("Thành công");
                cmdHistoryEntity.setMessage("Thành công");
            }
            else {
                System.out.println("Thất bại");
                cmdHistoryEntity.setMessage("Thất bại");
            }
        }
        cmdHistoryRepository.save(cmdHistoryEntity);
    }
    // tạo json gửi đến front end
    private void responseMessageToFE(ProbeModuleEntity probeModuleEntity, String status, Integer statusExcept, String pId, JSONObject responseMessage) {
        String statusResult = statusResult(statusExcept, status);
        if(responseMessage != null) {
            saveModuleHistory(status, statusExcept, probeModuleEntity, pId, responseMessage);
        }
//        checkResponse = false;
        // set lại trường loading = 0(đã loading xong)
        probeModuleEntity.setLoading(0);
        probeModuleEntity = moduleProbeRepository.save(probeModuleEntity);
//        System.out.println("Giá trị của biến kiểm tra client có nhận được lệnh hay không(sau khi thực hiện run hoặc stop lệnh) " + checkResponse);
//        return JsonUtil.createJsonResponse(message, statusResult.toString());
    }
    // Kiểm tra xem probe có còn kết nói với broker không
    private Boolean checkClientIsDisconnect(String clientId) {
        return ApiCheckConnect.checkExistClient(clientId);
    }
    // Lưu thông tin lịch sử của các module
    private void saveModuleHistory(String status, Integer statusExcept, ProbeModuleEntity probeModuleEntity, String pId, JSONObject responseMessage) {
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
    // So status trong task manager của client(1(Running), 2(Stopped), 3(Pending)) với status mà người dùng chọn(1(Running), 2(Stopped))
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
                return "Stopped";
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
            connectOptions.setAutomaticReconnect(true);
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
    public JSONObject delete(Integer idProbeModule) {
        JSONObject json = new JSONObject();
        try {
            //ProbeModuleEntity probeModule = moduleProbeRepository.findById(idProbeModule).orElse(null);
            moduleProbeRepository.deleteById(idProbeModule);
            json.put("code", "1");
            json.put("message", "Delete success");
            return json;
        } catch (Exception e) {
            System.out.println("Xóa probe module thất bại!(Line 636)");
            json.put("code", "0");
            json.put("message", "Delete failed");
            return json;
        }
    }
    // Thêm mới 1 module của 1 probe
    @Override
    public JSONObject saveProbeModule(ProbeModuleDto probeModuleDto) {
        JSONObject json = new JSONObject();
        try {
            ProbeModuleEntity probeModule = ProbeModuleConverter.toEntity(probeModuleDto);
            String cmd = probeModuleDto.getCaption().trim() + " " + probeModuleDto.getArg().trim();
            if (moduleProbeRepository.existsByCommandAndIdProbe(cmd, probeModuleDto.getIdProbe())) {
                json.put("code", "3");
                json.put("message", "Can not save probeModule due to duplicate commands");
                return json;
            } else {
                probeModule.setProcessStatus(2); // dừng
                probeModule.setExpectStatus(0);
                probeModule.setCommand(cmd);
                probeModule.setStatus("Stopped");
                moduleProbeRepository.save(probeModule);
                json.put("code", "1");
                json.put("message", "Save probe module success");
                return json;
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.put("code", "0");
            json.put("message", "Save probe module failed");
            return json;
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

    @Override
    public ProbeModuleDto findOneById(Integer idProbeModule) {
        try {
            ProbeModuleEntity probeModuleEntity = moduleProbeRepository.findById(idProbeModule).orElse(null);
            if(probeModuleEntity == null) {
                return null;
            }
            ProbeModuleDto probeDto = ProbeModuleConverter.toDto(probeModuleEntity);
            return probeDto;
        }
        catch (Exception e) {
            System.out.println("Lấy ra 1 module probe lỗi rồi(Line 676)");
            e.printStackTrace();
            return null;
        }
    }

    // cập nhật thông tin probeModule (Hướng)
    @Override
    public JSONObject updateProbeModule(ProbeModuleDto probeModuleDto) {
        JSONObject json = new JSONObject();
        try {
            ProbeModuleEntity probeModuleEntity = moduleProbeRepository.findById(probeModuleDto.getId())
                    .orElse(null);
            if(probeModuleEntity == null) {
                json.put("code", "3");
                json.put("message", "Can not found module of probe with id = " + probeModuleDto.getId());
                return json;
            }
            // chuyển đổi các thông số của probeModule cần cập nhật
            probeModuleEntity = ProbeModuleConverter.toEntity(probeModuleDto, probeModuleEntity);
            if(probeModuleEntity == null) {
                System.out.println("Có trường null không thực hiện được!!");
            }
            String commandLine = probeModuleEntity.getCaption().trim() + " " + probeModuleEntity.getArg().trim();
            Integer newId = getIdOfProbeModuleInDatabase(commandLine, probeModuleDto.getIdProbe());
            System.out.println(commandLine);
            System.out.println("NewId " + newId);
            System.out.println();
            if(newId != null && newId != probeModuleDto.getId()) {
                json.put("code", "0");
                json.put("message", "Can not update probeModule due to duplicate commands");
                return json;
            }
            probeModuleEntity.setCommand(commandLine);
            probeModuleEntity = moduleProbeRepository.save(probeModuleEntity);
            json.put("code", "1");
            json.put("message", "Update probeModule success");
            return json;
        }
        catch (Exception e) {
            System.out.println("Update probeModule lỗi rồi!!(Line 695)");
            e.printStackTrace();
            json.put("code", "0");
            json.put("message", "Can not update probeModule");
            return json;
        }
    }

    // lấy ra id của probeModule theo command từ database để check có trùng không
    private Integer getIdOfProbeModuleInDatabase(String commandLine, Integer idProbe) {
        try {
            ProbeModuleEntity probeModule = moduleProbeRepository.findByCommandAndIdProbe(commandLine, idProbe).orElse(null);
            if(probeModule == null) {
                return null;
            }
            return probeModule.getId();
        }
        catch (Exception e) {
            System.out.println("Kiểm tra probeModule lỗi rồi!! (Line 716)");
            e.printStackTrace();
            return 0;
        }
    }

    // xử lý các phản hồi
    private void solveResponseMessage(String topic, JSONObject response, ProbeModuleEntity probeModuleEntity, String idCmd) {
        System.out.println("Kết quả chạy lệnh của topic " + topic);
        System.out.println("Kết quả: " + response);
        System.out.println("==============================================================");
        String status = (String) response.get("statusModule");
//        if (response.containsKey("statusCmd") && (response.get("statusCmd").equals("OK") || response.get("statusCmd").equals("restart"))) {
//            System.out.println("Message " + response.get("message"));
////            updateCmdHistory(idCmd, -1, 1);
//        } else
        updateCmdHistory(idCmd, -1, 1);
        if (status.equals("1") || status.equals("2")) {
            System.out.println("Status cmd " + response.get("message"));
            saveModuleHistory(status, 1, probeModuleEntity, (String) response.get("PID"), response);
        }
    }
    // gửi lại tin nhắn
    private void reSendMessage(MqttClient client, String clientId, String idCmd) {
        while (!topicrequestRunResend.isEmpty()) {
            String topic = topicrequestRunResend.poll().getSubTopic(); // lấy ra topic từ trong queue
            ProbeModuleEntity probeModule1 = probeModuleEntityMapRun.get(topic); // lấy ra probeModule theo topic
            String mess = messageToClient.get(topic);
            MqttMessage message = new MqttMessage(mess.getBytes());
            message.setQos(2);
            Integer retry = 0;
            while (true) {
                try {
                    Thread.sleep(5000);
                    System.out.println("Kiểm tra xem trong queue có chứa topic không? " + clientStatusMapRun.containsKey(topic));
                    // không tôồn tại hoặc tồn tại nhưng bằng false
                    if (retry <= 2 && (!clientStatusMapRun.containsKey(topic) || (clientStatusMapRun.containsKey(topic) && !clientStatusMapRun.get(topic)))) {
                        System.out.println("TH gửi lại");
                        retry++;
                        client.publish(topic, message);
                    }
                    // không tồn tại hoặc tồn tại những == false và số lần gửi vượt quá quy định
                    else if (!clientStatusMapRun.containsKey(topic) || (clientStatusMapRun.containsKey(topic) && !clientStatusMapRun.get(topic)) && retry > 2) {
                        if (retry > 2) {
                            System.out.println("TH gửi lại quá số lần quy định ");
                            updateCmdHistory(idCmd, 3, 4);
                            Boolean clientIsDisconnect = checkClientIsDisconnect(clientId);

                            if (!clientIsDisconnect) {
                                System.out.println("Send to front end " + "đã mất kết nối tới broker");
                                responseMessageToFE(probeModule1, "2", 1, null, null);
                                clientStatusMapRun.put(topic, false);
                                checkErrorMapRun.put(topic, true);
                                break; // Thoát khỏi luồng khi xử lý xong
                            } else {
                                System.out.println("Send to front end " + "không nhận được yêu cầu thực hiện");
                                responseMessageToFE(probeModule1, "2", 1, null, null);
                                clientStatusMapRun.put(topic, false);
                                checkErrorMapRun.put(topic, true);
                                break; // Thoát khỏi luồng khi xử lý xong
                            }
                        }
                    }
                    // tồn tại và bằng true
                    else if (clientStatusMapRun.containsKey(topic) && clientStatusMapRun.get(topic)) {
                        System.out.println("TH không cần gửi lại(Client đã có phản hồi nhận được lệnh)");
                        break; // Thoát khỏi luồng khi xử lý xong
                    }
                } catch (InterruptedException | MqttException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // xử lý khi client không trả về kết quả của module
//    private void solveMessageWhenClienNotResponse() {
//        while (!topicrequestRunResPonseToClient.isEmpty()) {
//            String topic = topicrequestRunResPonseToClient.poll().getSubTopic();
//            ProbeModuleEntity probeModule1 = probeModuleEntityMapRun.get(topic);
//            System.out.println("Phản hồi của topic " + topic);
//            // TH không không tồn tại(topic có phản hồi không gặp lỗi) hoặc có tồn tại nhưng là false
//            if(!checkErrorMapRun.containsKey(topic) || (checkErrorMapRun.containsKey(topic) && !checkErrorMapRun.get(topic))) {
//                Integer count = -2;
//                while (true) {
////                    JSONObject responseMessage = responseMessageMapRun.get(topic);
//                    try {
//                        Thread.sleep(2000);
////                        String statusModule = (String) responseMessage.get("statusModule");
////                        System.out.println("Status module " + statusModule);
//
////                        if (statusModule != null && (statusModule.equals("1") || statusModule.equals("3"))) {
////                            System.out.println("Module thành công hoặc thất bại" + responseMessage.toJSONString());
////                            String mess = statusModule.equals("1") ? "Module chạy thành công" : "Module chạy thất bại";
////                            responseMessageToFE(probeModule1, statusModule, 1, (String) responseMessage.get("PID"), responseMessage);
////                            clientStatusMapRun.put(topic, false);
////                            break;
////                        }
//                        if (count >= 3) {
////                            System.out.println("Client không có phản hồi " + responseMessage.toJSONString());
//                            responseMessageToFE(probeModule1, "2", 1, null, null);
//                            clientStatusMapRun.put(topic, false);
//                            break; // Thoát khỏi luồng khi xử lý xong
//                        } else {
//                            count++;
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            else {
//
//            }
//        }
//    }
}
