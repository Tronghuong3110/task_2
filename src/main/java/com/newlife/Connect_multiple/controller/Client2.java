package com.newlife.Connect_multiple.controller;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1")
public class Client2 {
    private  static String content = "Xin chào, tao là client 2";
    private static Integer count = 0;
    private static boolean checkConnect = true;

    public static void main(String[] args) {
        try {
            MqttConnectOptions connOpts = new MqttConnectOptions();
            int qos = 2;
            String subTopic = "test/test_1";
            String pubTopic = "test/test_1_client_2";
            String broker = "tcp://192.168.100.8:1883";
//          String broker = "tcp://192.168.0.101:1883";
//          String broker = "tcp://192.168.27.101:1883";
//          String broker = "tcp://192.168.113.122:1883";

            String clientId = "Client_2_Id";
            MemoryPersistence persistence = new MemoryPersistence();
            MqttClient client = new MqttClient(broker, clientId, persistence);
            connOpts.setUserName("client 2");
            connOpts.setPassword("1234".toCharArray());
//            connOpts.setKeepAliveInterval(15);
            connOpts.setConnectionTimeout(50);

            connOpts.setCleanSession(true);
            client.setCallback(new MqttCallback() {
//              Xử lý tự kết nối lại khi mất kết nối tới broker
                @Override
                public void connectionLost(Throwable throwable) {
                    System.out.println(checkConnect);
                    while(!client.isConnected()) {
                        try {
                            System.out.println("Đang kết nối lại...");
                            Thread.sleep(3000);
                            if(checkConnect) {
                                connOpts.setUserName("client 2");
                                connOpts.setPassword("1234".toCharArray());
                                client.connect(connOpts);
                                client.subscribe(subTopic);
                                System.out.println("Kết nối lại thành công!!");
                            }
                        }
                        catch(InterruptedException | MqttException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    String responseFromServer = new String(mqttMessage.getPayload());
                    if(!responseFromServer.equals("") || responseFromServer != null) {
                        MqttMessage message = new MqttMessage("Client 2 nhận được lệnh".getBytes());
                        message.setQos(qos);
                        client.publish(pubTopic, message);
                    }
                    System.out.println(responseFromServer);
                    try {
                        Thread.sleep(5000);
                        test(client, pubTopic, qos);
                    }
                    catch (InterruptedException e) {

                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });


            if(checkConnect) {
                System.out.println("Connecting to broker: " + broker);
                client.connect(connOpts);
            }

            System.out.println("Connected");
            System.out.println("Publishing message: " + content);

            // Subscribe
            client.subscribe(subTopic);
            System.out.println("Message published");

        }
        catch (MqttException  e) {
//                System.out.println("msg " + e.getMessage());
//                System.out.println("loc " + e.getLocalizedMessage());
//                System.out.println("cause " + e.getCause());
//                System.out.println("excep " + e);
            e.printStackTrace();
        }

    }

    private static void test (MqttClient client, String pubTopic, int qos) {
        try {
            for(int i = 0; i <= 5; i++) {
                System.out.println("Lần lặp thứ " + i);
            }
            MqttMessage message = new MqttMessage("Client 2 thực hiện xong yêu cầu".getBytes());
            message.setQos(qos);
            client.publish(pubTopic, message);
        }
        catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
