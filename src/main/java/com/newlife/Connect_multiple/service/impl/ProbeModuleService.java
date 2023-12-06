package com.newlife.Connect_multiple.service.impl;

import com.newlife.Connect_multiple.api.ApiCheckConnect;
import com.newlife.Connect_multiple.converter.ProbeConverter;
import com.newlife.Connect_multiple.converter.ProbeModuleConverter;
import com.newlife.Connect_multiple.dto.MemoryDto;
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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @Autowired
    private MemoryRepository memoryRepository;
    @Autowired
    private PerformanceRepository performanceRepository;
    private MqttClient client = null;
    private Map<String, Boolean> checkProcessStop = new ConcurrentHashMap<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(100);

    // các biến phục vụ cho stop
    private Map<String, Boolean> clientStatusMapStop = new ConcurrentHashMap<>(); // lưu trữ trạng thái để đánh dấu client đã nhận được lệnh
    private Queue<String> topicrequestStopResend = new LinkedList<>(); // lưu trữ thứ tự các topic khi thực hiện stop (để kiểm tra xem có cần gửi lại lệnh không)
    private Queue<String> topicrequestStop = new LinkedList<>();
    private  Map<String, ProbeModuleEntity> probeModuleEntityMapStop = new ConcurrentHashMap<>();
    private Map<String, Boolean> checkErrorMapStop = new ConcurrentHashMap<>();
    private Map<String, Boolean> checkResultCommandStop = new ConcurrentHashMap<>();
    private Map<String, String> messageToClientStop = new ConcurrentHashMap<>();
    private Map<String, String> topicCheckResultStop = new ConcurrentHashMap<>();
    private Queue<String> topicCheckResultComandStop = new LinkedList<>();

    // các biến phục vụ cho run
    private Map<String, Boolean> clientStatusMapRun = new ConcurrentHashMap<>(); // lưu trữ trạng thái để đánh dấu client đã nhận được lệnh
    private Queue<String> topicrequestRunResend = new LinkedList<>(); // lưu trữ thứ tự các topic khi thực hiện stop (để kiểm tra xem có cần gửi lại lệnh không)
    private Queue<String> topicrequestRun = new LinkedList<>();
    private  Map<String, ProbeModuleEntity> probeModuleEntityMapRun = new ConcurrentHashMap<>();
    private Map<String, Boolean> checkErrorMapRun = new ConcurrentHashMap<>();
    private Map<String, Boolean> checkResultCommand = new ConcurrentHashMap<>();
    private Map<String, String> messageToClientMap = new ConcurrentHashMap<>();
    private Queue<String> topicCheckResultComandRun = new LinkedList<>();
    private Map<String, String> topicCheckResultRun = new ConcurrentHashMap<>();

    @Override
    public List<ProbeModuleDto> findAllProbeModule(String moduleName, String status, Integer idProbe) {
        try {
            List<ProbeModuleEntity> listProbeModuleEntity = moduleProbeRepository.findAllByProbeNameOrStatusAndIdProbe(moduleName, status, idProbe);
            List<ProbeModuleDto> listProbeModuleDto = new ArrayList<>();
            for(ProbeModuleEntity entity : listProbeModuleEntity) {
                ProbeModuleDto dto = ProbeModuleConverter.toDto(entity);
                Long error = countErrorModuleProbe(entity.getId());
//                System.out.println("Error " + error);
                dto.setErrorPerWeek(error);
                listProbeModuleDto.add(dto);
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
    public Object runModule(Integer idProbeModule) {
//        return CompletableFuture.supplyAsync(() -> {
            if (client == null || !client.isConnected()) {
                solveConnection();
            }
            ProbeModuleEntity probeModuleEntity = moduleProbeRepository.findById(idProbeModule).orElse(null); // Lấy ra module cần chạy
            try {
                if (probeModuleEntity == null) {
                    return JsonUtil.createJsonResponse("Không tồn tại module có id = " + idProbeModule, "5");
                }
                // Set lại trường loading = 1 (đang loading), loading = 0 (loading xong)
                probeModuleEntity.setLoading(1);
                probeModuleEntity = moduleProbeRepository.save(probeModuleEntity);

                SubtopicServerEntity subTopic = subtopicOnServerRepository.findByIdProbe(probeModuleEntity.getIdProbe()).orElse(null);
                topicrequestRunResend.add(subTopic.getSubTopic() + "-" + probeModuleEntity.getId()); // topicrequestRunResend = topic + "-" + idProbeModule
                topicrequestRun.add(subTopic.getSubTopic() + "-" + probeModuleEntity.getId()); // tạo hàng đợi để gửi tin nhắn
                probeModuleEntityMapRun.put(probeModuleEntity.getId().toString(), probeModuleEntity); // probeModuleEntityMapRun = idProbeModule, probeModuleEntity

                ProbeEntity probe = probeRepository.findByIdAndStatus(probeModuleEntity.getIdProbe(), "connected").orElse(new ProbeEntity());
                if(probe == null) {
                    return -1;
                }
                // topicrequestRunResend = topic + "-" + idProbeModule
                // topicrequestRun = topic + "-" + idProbeModule
                // probeModuleEntityMapRun = idProbeModule, probeModuleEntity
                // topicCheckResultComandRun = idCmd + "-" + topic + "-" + idProbeModule
                // topicCheckResultRun = topic+"-"+idProbeModule, idCmd + "-" + topic + "-" + idProbeModule
                // messageToClientMap = idProbeModule, json
                // checkResultCommand = topic + "-" + idProbeModule, true(false)
                // clientStatusMapRun = topic + "-" + idProbeModule
                // checkErrorMapRun = topic + "- + idprobe, true(false)
                String idCmd = saveCmd(probeModuleEntity); // Lưu thông tin lệnh vào database
                String jsonObject = JsonUtil.createJson(probeModuleEntity, idCmd, Optional.ofNullable(null), Optional.ofNullable(null), "run", probe.getName());
                messageToClientMap.put(probeModuleEntity.getId().toString(), jsonObject); // messageToClientMap = idProbeModule, json
                topicCheckResultComandRun.add(idCmd + "-" + topicrequestRun.peek()); // topicCheckResultComandRun = idCmd + "-" + topic + "-" + idProbeModule
                topicCheckResultRun.put(topicrequestRun.peek(), topicCheckResultComandRun.peek()); // topicCheckResultRun = topic+"-"+idProbeModule, idCmd + "-" + topic + "-" + idProbeModule
                while (!topicrequestRun.isEmpty()) {
                    String tmp = topicrequestRun.poll(); // topicrequestRun = topic + "-" idPProbeModule
                    String topic = tmp.split("-")[0];
                    String idModule = tmp.split("-")[1];
                    MqttMessage message = new MqttMessage(messageToClientMap.get(idModule).getBytes());
                    message.setQos(2);
//                    System.out.println("Topic đang được gửi tin nhắn: " + subTopic.getSubTopic());
                    // TH 2 lệnh được gửi liên tiếp nhau của cùng 1 topic
                    // // topic + "-" + idProbeModule, idCmd + "-" + topic + "-" + idProbeModule
                    if(!topicrequestRun.isEmpty() && topic.equals(topicrequestRun.peek().split("-")[0])) {
                        // module đang xử lý hoặc đã có thông báo nhận được module
                        // checkResultCommand = topic + "-" + idProbeModule, true(false)
                        // clientStatusMapRun = topic + "-" + idProbeModule = tmp
                        while ((checkResultCommand.containsKey(tmp) && checkResultCommand.get(tmp) || (clientStatusMapRun.containsKey(tmp) && clientStatusMapRun.get(tmp)))) {
                            try {
                                Thread.sleep(1000);
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    System.out.println("Tin nhắn gửi đi " + messageToClientMap.get(idModule));
                    client.publish(topic, message); // gửi tin nắn tới topic cần gửi
                }
                reSendMessageRun(client, probe.getClientId());
                // kiểm tra nếu client xử lý xong mà không có phản hồi
                try {
                    while (!topicCheckResultComandRun.isEmpty()) {
                        Boolean check = false;
                        String tmp = topicCheckResultComandRun.poll(); // idCmd + "-" + topic + "-" + idProbeModule
                        String topic = tmp.split("-")[1];
                        String idCmdHistory = tmp.split("-")[0];
                        String idModule = tmp.split("-")[2];
                        ProbeModuleEntity probeModule1 = probeModuleEntityMapRun.get(idModule);
                        // kiểm tra nếu gửi lại sau (không tồn tại hoặc tồn tại nhưng == false)
                        if(!checkErrorMapRun.containsKey(topic+"-"+idModule) || (checkErrorMapRun.containsKey(topic+"-"+idModule) && !checkErrorMapRun.get(topic+"-"+idModule))) {
                            Long timeCurrent = System.currentTimeMillis();
                            Long time = 0L;
                            if(checkResultCommand.containsKey(topic + "-" + idModule) && checkResultCommand.get(topic + "-" + idModule)) {
                                check = true;
                            }
                            // set thời gian chờ là 7s, nếu sau 7s không nhận được kết quả chạy lênh ==> set cmdHistory = failed
                            while ((!checkResultCommand.containsKey(topic + "-" + idModule) || (checkResultCommand.containsKey(topic + "-" + idModule) && !checkResultCommand.get(topic + "-" + idModule))) && time <= 8000) {
                                Thread.sleep(1000);
                                time = System.currentTimeMillis() - timeCurrent;
//                                System.out.println("Chờ kết quả của topic " + topic + " " + checkResultCommand.get(topic + "-" + idModule));
                                if(checkResultCommand.containsKey(topic + "-" + idModule) && checkResultCommand.get(topic + "-" + idModule)) {
                                    check = true;
                                    break;
                                }
                            }
                        }
//                        System.out.println("Check " + check);
                        if(check) continue;
//                        System.out.println("Không nhận được phản hồi!!!");
                        responseMessageToFE(probeModule1, "2", 1, null, null);
                        probeModule1.setLoading(0);
                        moduleProbeRepository.save(probeModule1);
                        // clientStatusMapRun = topic + "-" + idProbeModule
                        clientStatusMapRun.put(topic+"-"+idProbeModule, false);
                        updateCmdHistory(idCmdHistory, 3, 4);
                        saveModuleHistory("2", 1, probeModuleEntity, null, JsonUtil.parseJson(messageToClientMap.get(idModule)));
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception me) {
                me.printStackTrace();
                System.out.println("Yêu cầu chạy module lỗi rồi!!!");
                probeModuleEntity.setLoading(0);
                moduleProbeRepository.save(probeModuleEntity);
                return JsonUtil.createJsonResponse("Error", "0");
            }
            return null;
//        }, executorService);
    }
    // Yêu cầu dừng module
    @Override
    public Object stopModule(Integer idProbeModule) {
//        return CompletableFuture.supplyAsync(() -> {
            if(client == null || !client.isConnected()) {
                solveConnection();
            }
//            System.out.println("ID probe module " + idProbeModule);
            ProbeModuleEntity probeModuleEntity = moduleProbeRepository.findById(idProbeModule).orElse(null);
            try {
                if (probeModuleEntity == null || probeModuleEntity.getStatus().equals("Stopped")) {
                    return -1;
                }
                probeModuleEntity.setLoading(1);
                moduleProbeRepository.save(probeModuleEntity);
                SubtopicServerEntity subTopic = subtopicOnServerRepository.findByIdProbe(probeModuleEntity.getIdProbe()).orElse(null);
                topicrequestStopResend.add(subTopic.getSubTopic() + "-" + probeModuleEntity.getId()); // thêm topic vào hàng đợi
                topicrequestStop.add(subTopic.getSubTopic() + "-" + probeModuleEntity.getId());
                probeModuleEntityMapStop.put(probeModuleEntity.getId().toString(), probeModuleEntity);
                checkProcessStop.put(subTopic.getSubTopic(), true);

                ProbeEntity probe = probeRepository.findByIdAndStatus(probeModuleEntity.getIdProbe(), "connected").orElse(new ProbeEntity());
                if(probe == null) {
                    return -1;
                }
                // topicrequestStopResend = topic + "-" + idProbeModule
                // topicrequestStop = topic + "-" + idProbeModule
                // probeModuleEntityMapStop = idProbeModule, probeModuleEntity
                // topicCheckResultComandStop = idCmd + "-" + topic + "-" + idProbeModule
                // topicCheckResultStop = topic+"-"+idProbeModule, idCmd + "-" + topic + "-" + idProbeModule
                // messageToClientMapStop = idProbeModule, json
                // checkResultCommandStop = topic + "-" + idProbeModule, true(false)
                // clientStatusMapStop = topic + "-" + idProbeModule
                // checkErrorMapStop = topic + "- + idProbeModule, true(false)
                String idCmd = saveCmd(probeModuleEntity);
                // topicCheckResultStop = topic+"-"+idProbeModule, idCmd + "-" + topic + "-" + idProbeModule
                // topicrequestStop = topic + "-" + idProbeModule
                topicCheckResultStop.put(topicrequestStop.peek(), idCmd + "-" + topicrequestStop.peek());
                // topicCheckResultComandStop = idCmd + "-" + topic + "-" + idProbeModule
                // topicrequestStop = topic + "-" + idProbeModule
                topicCheckResultComandStop.add(idCmd + "-" + topicrequestStop.peek());

                String jsonObject = JsonUtil.createJson(probeModuleEntity, idCmd, Optional.of("cmd /c taskkill /F /PID "), Optional.of("pkill -f "), "stop", probe.getName());
                // messageToClientMapStop = idProbeModule, json
                messageToClientStop.put(probeModuleEntity.getId().toString(), jsonObject);

                // gửi tin nhắn tới các topic có trong hàng đợi
                while (!topicrequestStop.isEmpty()) {
                    String tmp = topicrequestStop.poll(); // topicrequestStop = topic + "-" + idProbeModule
                    String topic = tmp.split("-")[0];
                    String idModule = tmp.split("-")[1];
//                    System.out.println("Tin nhắn gửi tới probe " + topic + " (Stop) " + messageToClientStop.get(idModule));
                    String messageSendToClientStop = messageToClientStop.get(idModule);
                    // topicrequestStop = topic + "-" + idProbeModule
                    if(!topicrequestStop.isEmpty() && topic.equals(topicrequestStop.peek().split("-")[0])) {
                        // module đang xử lý hoặc đã có thông báo nhận được module
                        // checkResultCommandStop = topic + "-" + idProbeModule, true(false)
                        while ((checkResultCommandStop.containsKey(tmp) && checkResultCommandStop.get(tmp) || (clientStatusMapStop.containsKey(tmp) && clientStatusMapStop.get(tmp)))) {
                            try {
                                Thread.sleep(1000);
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    MqttMessage message = new MqttMessage(messageSendToClientStop.getBytes());
                    message.setQos(2);
                    client.publish(topic, message);
                }
                // kiểm tra xem có topic nào cần gửi lại không
                reSendMessageStop(client, probe.getClientId());
                // client đã nhận được lệnh, kiểm tra xem lệnh chạy thành công hay thất bại
                while (!topicCheckResultComandStop.isEmpty()) {
                    Boolean check = false;
                    String tmp = topicCheckResultComandStop.poll(); // idCmd + "-" + topic + "-" + idProbeModule
                    String topic = tmp.split("-")[1];
                    String idCmdHistory = tmp.split("-")[0];
                    String idModule = tmp.split("-")[2];
                    ProbeModuleEntity probeModule = probeModuleEntityMapStop.get(idModule);
                    // kiểm tra nếu gửi lại sau (không tồn tại hoặc tồn tại nhưng == false)
                    // checkErrorMapStop = topic + "- + idProbeModule, true(false)
                    if(!checkErrorMapStop.containsKey(topic + "-" + idModule) || (checkErrorMapStop.containsKey(topic + "-" + idModule) && !checkErrorMapStop.get(topic + "-" + idModule))) {
                        Long timeCurrent = System.currentTimeMillis();
                        Long time = 0L;
                        if(checkResultCommandStop.containsKey(topic + "-" + idModule)) {
//                            System.out.println("Check result command " + checkResultCommandStop.get(topic + "-" + idModule));
                        }
                        if(checkResultCommandStop.containsKey(topic + "-" + idModule) && checkResultCommandStop.get(topic + "-" + idModule)) {
                            check = true;
                        }
                        // set thời gian chờ là 7s, nếu sau 7s không nhận được kết quả chạy lênh ==> set cmdHistory = failed
                        // checkResultCommandStop = topic + "-" + idProbeModule, true(false)
                        while ((!checkResultCommandStop.containsKey(topic + "-" + idModule) || (checkResultCommandStop.containsKey(topic + "-" + idModule) && !checkResultCommandStop.get(topic + "-" + idModule))) && time <= 8000) {
                            Thread.sleep(1000);
                            time = System.currentTimeMillis() - timeCurrent;
//                            System.out.println("Chờ kết quả");
                            if(checkResultCommandStop.containsKey(topic + "-" + idModule) && checkResultCommandStop.get(topic + "-" + idModule)) {
                                check = true;
                                break;
                            }
                        }
                    }
//                    System.out.println("check " + check);
                    if(check) continue;
//                    System.out.println("Không nhận được phản hồi!!!");
                    responseMessageToFE(probeModule, "2", 1, null, null);
                    updateCmdHistory(idCmdHistory, 3, 4);
                    saveModuleHistory("1", 2, probeModuleEntity, null, JsonUtil.parseJson(messageToClientStop.get(idModule)));
                    clientStatusMapStop.put(topic + "-" + idModule, false);
                }
            }
            catch (Exception me) {
                me.printStackTrace();
                System.out.println("Dừng module lỗi rồi!!!");
                probeModuleEntity.setLoading(0);
                moduleProbeRepository.save(probeModuleEntity);
                return JsonUtil.createJsonResponse("Error", "0");
            }
            return null;
//        }, executorService);
    }
    // kiểm tra trạng thái các module theo chu kì
    @Override
    public void getStatusModulePeriodically() {
        CompletableFuture.runAsync(() -> {
            Boolean checkProcess = false;
            // TH server chưa kết nối tới broker ==> gọi hàm xử lý để kết nối tới broker
            if (client == null || !client.isConnected()) {
                solveConnection();
            }
            // status probe: connected 1 or disconnected 0
            List<ProbeEntity> listProbes = probeRepository.findProbeByDeletedAndStatus(0, "connected");
            // danh sách topic - listModule của từng probe đc để gưi tới client để kiểm tra trạng thái
            Map<String, String> messageToClient = new HashMap<>();
            for(ProbeEntity probe : listProbes) {
                List<ProbeModuleEntity> listProbeModule = moduleProbeRepository.findAllModuleByProbeIdAndStatus(probe.getId(), "Running", "Pending");
                // không kiểm tra các client đang có yêu cầu thực hiện lệnh
                String json = JsonUtil.createJsonStatus("getStatus", listProbeModule, probe.getName());
                System.out.println("Message to client " + json);
                messageToClient.put(probe.getPubTopic(), json);
//            if()
            }
            sendMessageGetStatus(messageToClient, listProbes);
        }, executorService);
    }
    // gửi tin nhắn tới các probe để hỏi trạng thái của các module trong probe
    private String sendMessageGetStatus(Map<String, String> messageToClient, List<ProbeEntity> listProbe) {
        try {
            // gửi tin nhắn tới từng topic
            for (ProbeEntity probe : listProbe) {
                String mess = messageToClient.get(probe.getPubTopic());
//                System.out.println("Message to client " + mess);
                if(checkProcessStop.containsKey(probe.getPubTopic()) && checkProcessStop.get(probe.getPubTopic())) {
                    checkProcessStop.remove(probe.getPubTopic());
                    continue;
                }
                if(mess != null) {
                    MqttMessage message = new MqttMessage(mess.getBytes());
                    message.setQos(2);
//                    System.out.println("Gửi tin nhắn tới topic(kiểm tra trạng thái theo chu kì) " + probe.getPubTopic() + " " + mess);
                    client.publish(probe.getPubTopic(), message);
                }
            }
        }
        catch (Exception e) {
            System.out.println("Kiểm tra trạng thái probeModule thất bại!!");
            e.printStackTrace();
        }
        return null;
    }
    // Lưu thông tin lịch sử gửi lệnh
    private String saveCmd(ProbeModuleEntity probeModuleEntity) {
        try {
//            System.out.println("Lưu thông tin cmd history vào database");
            String id = System.nanoTime() + "_" + probeModuleEntity.getIdProbe();
            CmdHistoryEntity cmd = new CmdHistoryEntity();
            cmd.setId(id);
            cmd.setIdProbeModule(probeModuleEntity.getId());
            cmd.setIdProbe(probeModuleEntity.getIdProbe());
            cmd.setArg(probeModuleEntity.getArg());
            cmd.setCaption(probeModuleEntity.getCaption());
            cmd.setCommand(probeModuleEntity.getCommand());
            cmd.setPath(probeModuleEntity.getPath());
            cmd.setAtTime(new Timestamp(System.currentTimeMillis()));
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
//        System.out.println("IDCMD " + idCmd);
//        System.out.println("Cập nhật lại cmd history");
        if(retry >= 0) {
            cmdHistoryEntity.setRetryTimes(retry);
        }
        cmdHistoryEntity.setModifiledate(new Timestamp(System.currentTimeMillis()));
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
    // cập nhật lại trường loadding
    private void responseMessageToFE(ProbeModuleEntity probeModuleEntity, String status, Integer statusExcept, String pId, JSONObject responseMessage) {
        String statusResult = statusResult(statusExcept, status);
        probeModuleEntity.setStatus(statusResult);
        probeModuleEntity.setLoading(0);
        moduleProbeRepository.save(probeModuleEntity);
    }
    // Kiểm tra xem probe có còn kết nói với broker không
    private Boolean checkClientIsDisconnect(String clientId) {
        return ApiCheckConnect.checkExistClient(clientId);
    }
    // Lưu thông tin lịch sử của các module
    private void saveModuleHistory(String status, Integer statusExcept, ProbeModuleEntity probeModuleEntity, String pId, JSONObject responseMessage) {
        try {
            String statusResult = statusResult(statusExcept, status);
            String probeName = responseMessage.get("probeName").toString();
            String content = null;
            if(statusResult.equals("Failed")) {
                content = "Module " + probeModuleEntity.getModuleName() + " của " + probeName + " lỗi rồi!";
            }
            // lưu thông tin module history
            ModuleHistoryEntity moduleHistoryEntity = new ModuleHistoryEntity();
            String id = String.valueOf(System.nanoTime());
            moduleHistoryEntity.setIdModuleHistory(id);
            moduleHistoryEntity.setIdProbe(probeModuleEntity.getIdProbe());
            moduleHistoryEntity.setContent(content);
            moduleHistoryEntity.setTitle((String)responseMessage.get("title"));
            moduleHistoryEntity.setAtTime(new Timestamp(System.currentTimeMillis()));
            moduleHistoryEntity.setCaption((String)responseMessage.get("caption"));
            moduleHistoryEntity.setArg((String)responseMessage.get("arg"));
            moduleHistoryEntity.setStatus(statusResult);
            moduleHistoryEntity.setModuleName((String)responseMessage.get("moduleName"));
            moduleHistoryEntity.setIdProbeModule(probeModuleEntity.getId());
            moduleHistoryEntity.setProbeName(probeName);
            moduleHistoryEntity.setAck(0);
            moduleHistoryRepository.save(moduleHistoryEntity);

//            if(probeModuleEntity != null) {
//                if(pId != null && !pId.equals("-1")) {
//                    probeModuleEntity.setProcessId(pId);
//                    System.out.println("PID " + pId);
//                }
//                probeModuleEntity.setStatus(statusResult);
//                moduleProbeRepository.save(probeModuleEntity);
//            }
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
        Boolean check = false;
        Integer idProbeModule = Integer.parseInt((String) jsonObject.get("id_probe_module"));
        // status lấy từ process của client
        String statusResult = (String) jsonObject.get("status");
        ProbeModuleEntity probeModuleEntity = moduleProbeRepository.findById(idProbeModule).orElse(null);
        // TH module có thay đổi về trạng thái
        if(!probeModuleEntity.getStatus().equals(statusResult)) {
            String pId = null;
            String content = "";
            // TH module không còn chạy nữa (lỗi) ==> set lại processId = 0 (đã dừng)
            if(statusResult.equals("Failed")) {
                pId = "0";
                content = probeModuleEntity.getModuleName() + " đã bị lỗi";
            }
            ModuleHistoryEntity moduleHistoryEntity = new ModuleHistoryEntity();
            String id = String.valueOf(System.nanoTime());
            moduleHistoryEntity.setIdModuleHistory(id);
            moduleHistoryEntity.setIdProbeModule(probeModuleEntity.getId());
            moduleHistoryEntity.setIdProbe(probeModuleEntity.getIdProbe());
            moduleHistoryEntity.setContent(content);
            moduleHistoryEntity.setTitle(null);
            moduleHistoryEntity.setAtTime(new Timestamp(System.currentTimeMillis()));
            moduleHistoryEntity.setCaption(probeModuleEntity.getCaption());
            moduleHistoryEntity.setArg(probeModuleEntity.getArg());
            moduleHistoryEntity.setStatus(statusResult);
            moduleHistoryEntity.setModuleName(probeModuleEntity.getModuleName());
            moduleHistoryEntity.setProbeName(jsonObject.get("probeName").toString());
            moduleHistoryEntity.setAck(0);
            moduleHistoryRepository.save(moduleHistoryEntity);
            // TH module này đã được yêu cầu dừng ==> Status = Stopped
            // Bây giờ trạng thái mới là Failed == > không cập nhật trạng thái
            if(!probeModuleEntity.getStatus().equals("Stopped")) {
                probeModuleEntity.setStatus(statusResult);
            }
            if (pId != null) {
                probeModuleEntity.setProcessId("0");
            }
            moduleProbeRepository.save(probeModuleEntity);
        }
    }
    @Override
    public JSONObject delete(List<String> ids) {
        JSONObject json = new JSONObject();
        try {
            //ProbeModuleEntity probeModule = moduleProbeRepository.findById(idProbeModule).orElse(null);
            for(String id : ids) {
                ProbeModuleEntity module = moduleProbeRepository.findById(Integer.parseInt(id)).orElse(null);
                String status = module.getStatus();
                if(status.equals("Running") || status.equals("Pending")) {
                    continue;
                }
                moduleProbeRepository.deleteById(Integer.parseInt(id));
            }
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
//        System.out.println("ID probe " + probeModuleDto.getIdProbe());
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
                probeModule.setErrorPerWeek(0);
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
//            System.out.println(commandLine);
//            System.out.println("NewId " + newId);
//            System.out.println();
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

    @Override
    public List<ProbeModuleDto> findAllProbeModuleAndError(Integer probeId, Integer mooduleId) {
        return null;
    }

    @Override
    public List<MemoryClient> findAllMemories(Integer probeId) {
        List<MemoryClient> listMemories = memoryRepository.findAllByProbeId(probeId);
        List<MemoryDto> listResponse = new ArrayList<>();
        return listMemories;
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
    private void solveResponseMessage(String topic, JSONObject response, String idCmd, Integer statusExcept) {
        System.out.println("KQ offffff " + topic);
        System.out.println("KQ: " + response);
        System.out.println("==============================================================");
        String status = (String) response.get("statusModule");
        String action = response.get("action").toString();
        updateCmdHistory(idCmd, -1, 1);
        if (status.equals("1") || status.equals("2")) {
            String id = response.get("idProbeModule").toString();
            ProbeModuleEntity probeModuleEntity = new ProbeModuleEntity();
            if(action.equals("run")) {
                probeModuleEntity = probeModuleEntityMapRun.get(id);
                checkResultCommand.put(topic + "-" + id, true);
            }
            else {
                probeModuleEntity = probeModuleEntityMapStop.get(id);
//                System.out.println("Module dừng thành công " + probeModuleEntity.getId());
                checkResultCommandStop.put(topic + "-" + id, true);
            }
            String pId = (String) response.get("PID");
            if(pId != null && !pId.equals(-1)) {
                probeModuleEntity.setProcessId(pId);
//                System.out.println("PID " + pId);
            }
            String statusResult = statusResult(statusExcept, status);
            probeModuleEntity.setStatus(statusResult);
            probeModuleEntity.setLoading(0);
            probeModuleEntity = moduleProbeRepository.save(probeModuleEntity);
            saveModuleHistory(status, statusExcept, probeModuleEntity, null, response);
//            System.out.println("Status " + probeModuleEntity.getStatus());
        }
    }
    // gửi lại tin nhắn trong TH chạy module
    private void reSendMessageRun(MqttClient client, String clientId) {
        while (!topicrequestRunResend.isEmpty()) {
            try {
                String tmp = topicrequestRunResend.poll(); // topic + "-" + idProbeModule
                String topic = tmp.split("-")[0];
                String idModule = tmp.split("-")[1];
                ProbeModuleEntity probeModule1 = probeModuleEntityMapRun.get(idModule); // lấy ra probeModule theo topic
                String mess = messageToClientMap.get(idModule);
//                System.out.println("Topic gửi lại " + topic);
//                System.out.println("Tin nhắn gửi lại " + mess);
                MqttMessage message = new MqttMessage(mess.getBytes());
                message.setQos(2);
                Integer retry = 0;
                // topic + "-" + idProbeModule, idCmd + "-" + topic + "-" + idProbeModule
                String idCmd = topicCheckResultRun.get(tmp).split("-")[0];
//                System.out.println("id cmd trong TH gửi lại " + idCmd);
                int loop = 0;
                while (loop <= 5) {
                    if(clientStatusMapRun.containsKey(tmp) && clientStatusMapRun.get(tmp)) {
                        break;
                    }
                    loop++;
                    Thread.sleep(1000);
                }
                while (true) {
                    try {
//                        System.out.println("Kiểm tra xem trong queue có chứa topic không? " + clientStatusMapRun.containsKey(tmp));
                        // không tôồn tại hoặc tồn tại nhưng bằng false
                        if (retry <= 2 && (!clientStatusMapRun.containsKey(tmp) || (clientStatusMapRun.containsKey(tmp) && !clientStatusMapRun.get(tmp)))) {
//                            System.out.println("TH gửi lại");
                            retry++;
                            client.publish(topic, message);
                        }
                        // không tồn tại hoặc tồn tại những == false và số lần gửi vượt quá quy định
                        else if (!clientStatusMapRun.containsKey(tmp) || (clientStatusMapRun.containsKey(tmp) && !clientStatusMapRun.get(tmp)) && retry > 2) {
//                            System.out.println("TH gửi lại quá số lần quy định ");
                            // kiểm tra cmdHistory đã được cập nhật thành công chưa
                            CmdHistoryEntity cmdHistoryEntity = cmdHistoryRepository.findById(idCmd).orElse(null);
                            if(cmdHistoryEntity == null || cmdHistoryEntity.getMessage().equals("")) {
                                if(!clientStatusMapRun.containsKey(tmp) || (clientStatusMapRun.containsKey(tmp) && !clientStatusMapRun.get(tmp))) {
                                    updateCmdHistory(idCmd, 3, 4);
//                                    saveModuleHistory("2", 1, probeModule1, null, JsonUtil.parseJson(messageToClientMap.get(idModule)));
                                }
                                Boolean clientIsDisconnect = checkClientIsDisconnect(clientId);
                                if (!clientIsDisconnect) {
//                                    System.out.println("Send to front end " + "đã mất kết nối tới broker");
                                    responseMessageToFE(probeModule1, "2", 1, null, null);
                                    probeModule1.setLoading(0);
                                    moduleProbeRepository.save(probeModule1);
                                    clientStatusMapRun.put(tmp, false);
                                    checkErrorMapRun.put(tmp, true);
                                    break; // Thoát khỏi luồng khi xử lý xong
                                } else {
//                                    System.out.println("Send to front end " + "không nhận được yêu cầu thực hiện");
                                    responseMessageToFE(probeModule1, "2", 1, null, null);
                                    probeModule1.setLoading(0);
                                    moduleProbeRepository.save(probeModule1);
                                    clientStatusMapRun.put(tmp, false);
                                    checkErrorMapRun.put(tmp, true);
                                    break; // Thoát khỏi luồng khi xử lý xong
                                }
                            }
                            break;
                        }
                        // tồn tại và bằng true
                        else if (clientStatusMapRun.containsKey(tmp) && clientStatusMapRun.get(tmp)) {
//                            System.out.println("TH không cần gửi lại(Client đã có phản hồi nhận được lệnh)");
                            break; // Thoát khỏi luồng khi xử lý xong
                        }
                        Thread.sleep(5000);
                    } catch (InterruptedException | MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // gửi lại tin nhắn trong TH dừng module
    private void reSendMessageStop(MqttClient client, String clientId) {
        while (!topicrequestStopResend.isEmpty()) {
            try {
                String tmp = topicrequestStopResend.poll(); // topicrequestStopResend = topic + "-" + idProbeModule
                String topic = tmp.split("-")[0]; // lấy ra topic từ trong queue
                String idModule = tmp.split("-")[1];
                ProbeModuleEntity probeModule = probeModuleEntityMapStop.get(idModule); // lấy ra probeModule theo topic
                // messageToClientMapStop = idProbeModule, json
                String mess = messageToClientStop.get(idModule);
                MqttMessage message = new MqttMessage(mess.getBytes());
                message.setQos(2);
                Integer retry = 0;
                Boolean check = false;
                // topicCheckResultStop = topic+"-"+idProbeModule, idCmd + "-" + topic + "-" + idProbeModule
                String idCmd = topicCheckResultStop.get(topic + "-" + idModule).split("-")[0];
                ProbeModuleEntity probeModuleCheck = moduleProbeRepository.findById(probeModule.getId()).orElse(null);
                if(probeModuleCheck!=null && !probeModuleCheck.getStatus().equals("Stopped") && !probeModuleCheck.equals(probeModule.getStatus())) {
                    check = true;
                }
                while (!check) {
                    // clientStatusMapStop = topic + "-" + idProbeModule = tmp
//                    System.out.println("Client status check resend " + clientStatusMapStop.get(tmp));
                    try {
//                        System.out.println("Kiểm tra xem trong queue có chứa topic không? " + clientStatusMapStop.containsKey(tmp));
                        // không tôồn tại hoặc tồn tại nhưng bằng false
//                        System.out.println("Topic " + topic + (clientStatusMapStop.containsKey(tmp) ? clientStatusMapStop.get(tmp) : "null"));
                        if (retry <= 2 && (!clientStatusMapStop.containsKey(tmp) || (clientStatusMapStop.containsKey(tmp) && !clientStatusMapStop.get(tmp)))) {
//                            System.out.println("TH gửi lại");
                            retry++;
                            client.publish(topic, message);
                        }
                        // không tồn tại hoặc tồn tại những == false và số lần gửi vượt quá quy định
                        else if (!clientStatusMapStop.containsKey(tmp) || (clientStatusMapStop.containsKey(tmp) && !clientStatusMapStop.get(tmp)) && retry > 2) {
                            CmdHistoryEntity cmdHistoryEntity = cmdHistoryRepository.findById(idCmd).orElse(null);
                            if(cmdHistoryEntity == null || cmdHistoryEntity.getMessage().equals("")) {
//                                System.out.println("TH gửi lại quá số lần quy định ");
                                if(!clientStatusMapStop.containsKey(tmp) || (clientStatusMapStop.containsKey(tmp) && !clientStatusMapStop.get(tmp))) {
                                    updateCmdHistory(idCmd, 3, 4);
//                                    saveModuleHistory("2", 1, probeModule, null, JsonUtil.parseJson(messageToClientStop.get(idModule)));
                                }
                                Boolean clientIsDisconnect = checkClientIsDisconnect(clientId);
                                if (!clientIsDisconnect) {
//                                    System.out.println("Send to front end " + "đã mất kết nối tới broker");
                                    responseMessageToFE(probeModule, "2", 1, null, null);
                                    probeModule.setLoading(0);
                                    moduleProbeRepository.save(probeModuleCheck);
                                    clientStatusMapStop.put(tmp, false);
                                    // checkErrorMapStop = topic + "- + idProbeModule, true(false) = tmp
                                    checkErrorMapStop.put(tmp, true);
                                    break; // Thoát khỏi luồng khi xử lý xong
                                }
                                else {
//                                    System.out.println("Send to front end " + "không nhận được yêu cầu thực hiện");
                                    responseMessageToFE(probeModule, "2", 1, null, null);
                                    probeModule.setLoading(0);
                                    moduleProbeRepository.save(probeModuleCheck);
                                    clientStatusMapStop.put(tmp, false);
                                    checkErrorMapStop.put(tmp, true);
                                    break; // Thoát khỏi luồng khi xử lý xong
                                }
                            }
                            break;
                        }
                        // tồn tại và bằng true
                        else if (clientStatusMapStop.containsKey(tmp) && clientStatusMapStop.get(tmp)) {
//                            System.out.println("TH không cần gửi lại(Client đã có phản hồi nhận được lệnh)");
                            break; // Thoát khỏi luồng khi xử lý xong
                        }
                        Thread.sleep(5000);
                    } catch (InterruptedException | MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // kết nối broker và đăng ký topic nhận tin nhắn
    private void solveConnection() {
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
            // thực hiện kết nối tới broker
            if (client == null || !client.isConnected()) {
                client = new MqttClient(brokerURL, clientID, persistence);
                client.connect(connectOptions);
            }
            // Đăng kí để nhận tin phản hồi từ topic của client
            for (SubtopicServerEntity pubtopic : listTopics) {
                CompletableFuture.runAsync(() -> {
                    try {
                        client.subscribe(pubtopic.getSubTopic(), (topic, mes) -> {
                            String message = new String(mes.getPayload());
                            // chuyển tin nhắn từ client dạng string sang json
                            JSONObject json = JsonUtil.parseJson(message);
                            // Th lấy trạng thái theo chu kì
                            if (json.containsKey("check") && json.containsKey("action") && json.get("action").equals("getStatus")) {
                                // TH client thông báo nhận được tin nhắn
                                try {
                                    if (json.containsKey("statusCmd") && json.get("statusCmd").equals("OK")) {
//                                        System.out.println("Message " + json.get("message"));
                                    }
                                    // TH client thông báo danh sách các trạng thái khi đã xử lý xong
                                    else {
//                                        System.out.println("Response(phản hồi từ client) " + message);
                                        // chuyển tin nhắn từ client gửi tới thành chuỗi json object
                                        String messageFromClient = (String) json.get("message");
//                                        System.out.println("Tin nhắn (get Status) " + messageFromClient);
                                        // danh sách trạng thái của các module của probe
//                                        System.out.println("Danh sách trạng thái các module của probe thứ " + (String) json.get("idProbe"));
                                        JSONArray jsonArray = (JSONArray) json.get("listStatus");
                                        // duyệt để cập nhật trạng thái của các module của probe
                                        for(Object object : jsonArray) {
                                            updateProbeModule((JSONObject) object);
                                        }
                                        // update thông tin về memory
                                        JSONArray listMemory = (JSONArray) json.get("memories");
                                        Integer probeId = Integer.parseInt(json.get("idProbe").toString());
                                        saveInfoMemory(listMemory, probeId);
                                        // update thông tin về tải trung bình cpu
                                        JSONObject load_average = (JSONObject) json.get("load_average");
                                        saveLoadAverage(load_average, probeId);
                                    }
                                }
                                catch (Exception e) {
                                    System.out.println("Lỗi tại chỗ nhận tin nhắn tại lấy trạng thái theo chu kì");
                                    e.printStackTrace();
                                }
                            }
                            // TH dừng module
                            if (json.containsKey("check") && json.containsKey("action") && json.get("action").equals("stop")) {
                                String idProbeModule = json.get("idProbeModule").toString();
                                // clientStatusMapStop = topic + "-" + idProbeModule
                                clientStatusMapStop.put(topic + "-" + idProbeModule, true); // đánh dấu đã nhận được phản hồi của topic
                                Object pid = json.get("PID");
//                                System.out.println("Topic result " + topic);
                                if (!pid.equals("")) {
//                                    System.out.println("Dừng module");
                                    // checkResultCommandStop = topic + "-" + idProbeModule, true(false)
                                    checkResultCommandStop.put(topic + "-" + idProbeModule, true); // đaánh dấu đã nhận được kết quả chạy module
//                                    System.out.println("Status cmd " + json.get("message"));
                                    // lưu lại kết quả của module
                                    // topicCheckResultStop = topic+"-"+idProbeModule, idCmd + "-" + topic + "-" + idProbeModule
                                    solveResponseMessage(topic, json, topicCheckResultStop.get(topic + "-" + idProbeModule).split("-")[0], 2);
                                    try {
                                        Thread.sleep(2000);
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    clientStatusMapStop.put(topic + "-" + idProbeModule, false);
                                    checkResultCommandStop.put(topic + "-" + idProbeModule, false);
                                }
                            }
                            // Th chạy module
                            if (json.containsKey("check") && json.containsKey("action") && json.get("action").equals("run")) {
                                String idProbeModule = json.get("idProbeModule").toString();
                                // clientStatusMapRun = topic + "-" + idProbeModule
                                clientStatusMapRun.put(topic+"-"+idProbeModule, true); // đánh dấu client đã nhận được lệnh, không phải gửi lại
                                Object pid = json.get("PID");
                                if(!pid.equals("")) {
                                    // checkResultCommand = topic + "-" + idProbeModule, true(false)
                                    checkResultCommand.put(topic+"-"+idProbeModule, true); // đánh dấu đã nhận được kết quả lệnh từ client
                                    // // topic + "-" + idProbeModule, idCmd + "-" + topic + "-" + idProbeModule
                                    String tmp = topic + "-" + json.get("idProbeModule");
                                    solveResponseMessage(topic, json, topicCheckResultRun.get(tmp).split("-")[0], 1);
                                    try {
                                        Thread.sleep(2000);
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    clientStatusMapRun.put(topic+"-"+idProbeModule, false);
                                    checkResultCommand.put(topic + "-" + idProbeModule, false);
                                }
                            }
                        });
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }, executorService);
//                System.out.println("Đã subscribe tới topic " + pubtopic.getSubTopic());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveInfoMemory(JSONArray jsonArray, Integer probeId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime time = LocalDateTime.now().minusSeconds(20);
        String timeAfterSecond = formatter.format(time);
        String currentTime = formatter.format(LocalDateTime.now());
        if(probeId != -1) {
            for(Object object : jsonArray) {
                JSONObject jsonObject = (JSONObject) object;
                Double memoryFree = Double.valueOf(jsonObject.get("memory_free").toString());
                MemoryClient memoryClientBefore = memoryRepository.findByTime(timeAfterSecond, currentTime).orElse(null);
                if(memoryClientBefore != null && memoryClientBefore.getMemoryDisk().equals(memoryFree)) {
                    continue;
                }
                MemoryClient memoryClient = new MemoryClient();
                memoryClient.setDiskName(jsonObject.get("nameDisk").toString());
                memoryClient.setMemoryDisk(memoryFree);
                memoryClient.setTotalMemory(Double.valueOf(jsonObject.get("memory_total").toString()));
                memoryClient.setProbeId(probeId);
                memoryClient.setModifiedTime(new Timestamp(System.currentTimeMillis()));
                memoryRepository.save(memoryClient);
            }
        }
    }
    // lưu thông tin tải trung bình CPU
    private void saveLoadAverage(JSONObject jsonObject, Integer probeId) {
        PerformanceCpu performanceCpu = new PerformanceCpu();
        performanceCpu.setLoadAverage(Double.valueOf(jsonObject.get("load_average").toString()));
        performanceCpu.setProbeId(probeId);
        performanceCpu.setMessage(jsonObject.get("message").toString());
        performanceCpu.setModifiedTime(new Timestamp(System.currentTimeMillis()));
        performanceRepository.save(performanceCpu);
    }
    // count error per week of module
    private Long countErrorModuleProbe(Integer idProbeModule) {
        try {
            String timeBefore = getTimeBefore();
            String timeAfter = getTimeAfter();
            if(timeBefore == null || timeAfter == null) {
                System.out.println("Đếm lỗi module lồi rồi (line 50) !!!");
                return null;
            }
            JSONObject jsonError = moduleHistoryRepository.solveErrorPerWeekOfModule(idProbeModule, timeAfter, timeBefore, "Failed");
//            System.out.println(jsonError);
            return (jsonError != null && jsonError.containsKey("epw")) ? Long.parseLong(jsonError.get("epw").toString()) : 0;
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi tính error (line 54) ");
            return null;
        }
    }
    private String getTimeBefore() {
        try {
            LocalDateTime currentDate = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return currentDate.format(formatter);
        }
        catch (Exception e) {
            System.out.println("Lấy ngày hiêện tại lỗi rồi line 77");
            e.printStackTrace();
            return null;
        }
    }
    private String getTimeAfter() {
        try {
            LocalDateTime currentDate = LocalDateTime.now();
            LocalDateTime sevenDateFromCurrent = currentDate.minusDays(7);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return sevenDateFromCurrent.format(formatter);
        }
        catch (Exception e) {
            System.out.println("Tính thời gian sau 7 ngày từ ngày hiện tại lỗi rồi");
            e.printStackTrace();
            return null;
        }
    }
}
