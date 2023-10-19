package com.newlife.Connect_multiple.test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.newlife.Connect_multiple.util.JsonUtil;
import org.json.simple.JSONObject;
public class Test1 {

    private static String deCodeToken(String token, String secretKey) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        String str = decodedJWT.getClaim("data").asString();
        JSONObject jsonObject = JsonUtil.parseJson(str);
        String username = (String) jsonObject.get("username");
        System.out.println(username);
        return null;
    }
    private static String enCode(String jsonObject, String secretKey) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.create().withClaim("data", jsonObject)
                .sign(algorithm);
    }
    public static void main(String[] args) {
        String secretKey = "tronghuong";
        String jsonData = "{"
                + "\"broker\": \"1234\", "
                + "\"clientId\": \"client1\", "
                + "\"username\": \"tronghuong\", "
                + "\"password\": \"1234\", "
                + "\"connectTimeOut\": 5, "
                + "\"CleanSession\": true, "
                + "\"subtopic\": \"server\", "
                + "\"pubtopic\": \"client1\""
                + "}";

        String token = enCode(jsonData, secretKey);
        System.out.println(token);
        String decodedJWT = deCodeToken(token, secretKey);
    }
}

//client.setCallback(new MqttCallback() {
//                @Override
//                public void connectionLost(Throwable throwable) {
//                    while (!client.isConnected()) {
//                        try {
//                            System.out.println("Đang kết nối lại...");
//                            Thread.sleep(5000);
////                            client.reconnect();
////                            client.disconnect();
//                            client = new MqttClient(brokerURL, clientID, persistence);
//                            client.connect(connectOptions);
//                            System.out.println("Kết nối lại thành công!!");
//                        } catch (InterruptedException | MqttException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//                @Override
//                public void messageArrived(String s, MqttMessage mqttMessage) {
//                    String message = new String(mqttMessage.getPayload());
//                    JSONObject json = JsonUtil.parseJson(message);
//                    System.out.println("Phản hồi từ client " + message);
//                    String topicOfClient = topicrequestRunResPonseToClient.peek().getSubTopic();
//                    if (json.containsKey("check") && json.containsKey("action") && json.get("action").equals("run")) {
//                        JSONObject response = json;
//                        clientStatusMapRun.put(topicOfClient, true);
//                        responseMessageMapRun.put(topicOfClient, response); // đẩy phản hồi của các client vào map theo topic của client
////                        Boolean hasReceivedCommand = clientStatusMapRun.get(topicOfClient);
////                        if (hasReceivedCommand != null && !hasReceivedCommand) {
//                        System.out.println("Response " + message);
//                        String status = (String) response.get("statusModule");
//                        System.out.println("Phản hồi từ client: " + json.get("statusCmd"));
//
//                        if (response.containsKey("statusCmd") && (response.get("statusCmd").equals("OK") || response.get("statusCmd").equals("restart"))) {
//                            System.out.println("Message " + response.get("message"));
//                            updateCmdHistory(idCmd, -1, 1);
//                        } else if (status.equals("1") || status.equals("2")) {
//                            System.out.println("Status cmd " + response.get("message"));
//                            saveModuleHistory(status, 1, probeModuleEntityMapRun.get(topicOfClient), (String) response.get("PID"), responseMessageMapRun.get(topicOfClient));
//                        }
////                        }
//                    }
//                }
//
//                @Override
//                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
//                }
//            });

// Tạo một luồng độc lập để kiểm tra và gửi lại lệnh khi cần
//            while (!topicrequestRunResend.isEmpty()) {
//                String topic = topicrequestRunResend.poll().getSubTopic(); // lấy ra topic từ trong queue
//                System.out.println("Topic " + topic);
//                ProbeModuleEntity probeModule1 = probeModuleEntityMapRun.get(topic); // lấy ra probeModule theo topic
//                Integer retry = 0;
//                while (true) {
//                    try {
//                        Thread.sleep(5000);
////                        Boolean hasReceivedCommand = clientStatusMapRun.get(topic); // client có topic đang xét chưa nhận được tin nhắn
//                        System.out.println("Kiểm tra xem trong queue có chứa topic không? " + clientStatusMapRun.containsKey(topic));
//                        // không tôồn tại hoặc tồn tại nhưng bằng false
//                        if (retry <= 2 && (!clientStatusMapRun.containsKey(topic) || (clientStatusMapRun.containsKey(topic) && !clientStatusMapRun.get(topic)))) {
//                            System.out.println("TH gửi lại");
//                            retry++;
//                            client.publish(topic, message);
//                        }
//                        // không tồn tại hoặc tồn tại những == false và số lần gửi vượt quá quy định
//                        else if (!clientStatusMapRun.containsKey(topic) || (clientStatusMapRun.containsKey(topic) && !clientStatusMapRun.get(topic)) && retry > 2) {
//                            if (retry > 2) {
//                                System.out.println("TH gửi lại quá số lần quy định ");
//                                updateCmdHistory(idCmd, 3, 4);
//                                System.out.println("ClientId của probe có tên " + probe.getName() + " là " + probe.getClientId());
//                                Boolean clientIsDisconnect = checkClientIsDisconnect(probe.getClientId());
//
//                                if (!clientIsDisconnect) {
//                                    System.out.println("Send to front end " + "đã mất kết nối tới broker");
//                                    responseMessageToFE(probeModule1, "2", 1, null, null);
//                                    clientStatusMapRun.put(topic, false);
//                                    checkErrorMapRun.put(topic, true);
//                                    break; // Thoát khỏi luồng khi xử lý xong
//                                } else {
//                                    System.out.println("Send to front end " + "không nhận được yêu cầu thực hiện");
//                                    responseMessageToFE(probeModule1, "2", 1, null, null);
//                                    clientStatusMapRun.put(topic, false);
//                                    checkErrorMapRun.put(topic, true);
//                                    break; // Thoát khỏi luồng khi xử lý xong
//                                }
//                            }
//                        }
//                        // tồn tại và bằng true
//                        else if (clientStatusMapRun.containsKey(topic) && clientStatusMapRun.get(topic)) {
//                            System.out.println("TH không cần gửi lại(Client đã có phản hồi nhận được lệnh)");
//                            break; // Thoát khỏi luồng khi xử lý xong
//                        }
//                    } catch (InterruptedException | MqttException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
// Chờ đợi cho đến khi có phản hồi từ client
//            while (!topicrequestRunResPonseToClient.isEmpty()) {
//                String topic = topicrequestRunResPonseToClient.poll().getSubTopic();
//                ProbeModuleEntity probeModule1 = probeModuleEntityMapRun.get(topic);
//                System.out.println("Phản hồi của topic " + topic);
//                // TH không không tồn tại(topic có phản hồi không gặp lỗi) hoặc có tồn tại nhưng là false
//                if(!checkErrorMapRun.containsKey(topic) || (checkErrorMapRun.containsKey(topic) && !checkErrorMapRun.get(topic))) {
//                    Integer count = -2;
//                    while (true) {
//                        JSONObject responseMessage = responseMessageMapRun.get(topic);
//                        try {
//                            Thread.sleep(2000);
//                            String statusModule = (String) responseMessage.get("statusModule");
//                            System.out.println("Status module " + statusModule);
//
//                            if (statusModule != null && (statusModule.equals("1") || statusModule.equals("3"))) {
//                                System.out.println("Module thành công hoặc thất bại" + responseMessage.toJSONString());
//                                String mess = statusModule.equals("1") ? "Module chạy thành công" : "Module chạy thất bại";
//                                responseMessageToFE(probeModule1, statusModule, 1, (String) responseMessage.get("PID"), responseMessage);
//                                clientStatusMapRun.put(topic, false);
//                                break;
//                            } else if (count >= 3) {
//                                System.out.println("Client không có phản hồi " + responseMessage.toJSONString());
//                                responseMessageToFE(probeModule1, "2", 1, null, responseMessage);
//                                clientStatusMapRun.put(topic, false);
//                                break; // Thoát khỏi luồng khi xử lý xong
//                            } else {
//                                count++;
//                            }
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }