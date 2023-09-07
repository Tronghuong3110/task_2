package com.newlife.Connect_multiple.controller;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Client2 {
    private  static String content = "Xin chào, tao là client 2";
    private static Integer count = 0;
    public static void main(String[] args) {
        try {
            MqttConnectOptions connOpts = new MqttConnectOptions();
            int qos = 2;
            String subTopic = "test/test_1";
            String pubTopic = "test/test_1_client_2";
          String broker = "tcp://192.168.100.17:1883";
//          String broker = "tcp://192.168.0.101:1883";
//          String broker = "tcp://192.168.27.101:1883";
//          String broker = "tcp://192.168.113.122:1883";

            String clientId = "Client_2_Id";
            MemoryPersistence persistence = new MemoryPersistence();
            MqttClient client = new MqttClient(broker, clientId, persistence);
            connOpts.setUserName("client 2");
            connOpts.setPassword("1234".toCharArray());
            connOpts.setKeepAliveInterval(15);
            connOpts.setConnectionTimeout(30);

            connOpts.setCleanSession(true);
            client.setCallback(new MqttCallback() {
//              Xử lý tự kết nối lại khi mất kết nối tới broker
                @Override
                public void connectionLost(Throwable throwable) {
                    while(!client.isConnected()) {
                        try {
                            System.out.println("Đang kết nối lại...");
                            Thread.sleep(3000);
                            client.connect(connOpts);
                            client.subscribe(subTopic);
                            System.out.println("Kết nối lại thành công!!");
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
                        MqttMessage message = new MqttMessage("Client nhận được lệnh".getBytes());
                        message.setQos(qos);
                        client.publish(pubTopic, message);
                    }
                    if(responseFromServer.equals("dis")) {
                        client.disconnect();
//                        try {
//                            Thread.sleep(10000);
//                            client.connect(connOpts);
//                        }
//                        catch(InterruptedException e) {
//                            e.printStackTrace();
//                        }
                    }
                    count++;
                    System.out.println(responseFromServer);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });


            System.out.println("Connecting to broker: " + broker);
            client.connect(connOpts);

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
}
