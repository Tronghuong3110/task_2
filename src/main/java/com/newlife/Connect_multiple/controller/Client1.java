package com.newlife.Connect_multiple.controller;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.util.Scanner;

public class Client1 {
    private  static String content = "Xin chào tao là client 1";
    public static void main(String[] args) {
        boolean check = true;

        Scanner sc = new Scanner(System.in);
//        while (check) {
            try {
                MqttConnectOptions connOpts = new MqttConnectOptions();
                int qos = 2;
                String subTopic = "test/test_1"; // đăng kí nhận thông điệp từ server
                String pubTopic = "test/client_1_test"; // đăng kí để gửi thông điệp
//                String broker = "tcp://192.168.100.12:1883";
//                String broker = "tcp://192.168.0.101:1883";
                String broker = "tcp://192.168.27.101:1883";
                String clientId = "Client_1_Id";
                MemoryPersistence persistence = new MemoryPersistence();
                MqttClient client = new MqttClient(broker, clientId, persistence);
                connOpts.setUserName("client 1");
                connOpts.setPassword("1234".toCharArray());
                connOpts.setKeepAliveInterval(3);
//                connOpts.setAutomaticReconnect(true);

                connOpts.setCleanSession(true);
                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable throwable) {

                    }

                    @Override
                    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                        String responseFromServer = new String(mqttMessage.getPayload());
                        if(!responseFromServer.equals("") || responseFromServer != null) {
//                            Test test = solveRequest(responseFromServer);
                            MqttMessage message = new MqttMessage();
                            message.setQos(qos);
                            client.publish(pubTopic, message);
                        }
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
            }
            catch (MqttException  e) {
                System.out.println("msg " + e.getMessage());
                System.out.println("loc " + e.getLocalizedMessage());
                System.out.println("cause " + e.getCause());
                System.out.println("excep " + e);
                e.printStackTrace();
            }
//        }

    }

}
