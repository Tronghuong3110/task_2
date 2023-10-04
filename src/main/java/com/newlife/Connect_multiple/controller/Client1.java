package com.newlife.Connect_multiple.controller;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.util.Scanner;

public class Client1 {
    public static void main(String[] args) {
        try {
            String broker = "tcp://localhost:1883";
            String clientId = "Client_Id_1";
            MemoryPersistence persistence = new MemoryPersistence();
            MqttClient client = new MqttClient(broker, clientId, persistence);

            // MQTT connection option
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName("client2");
            connOpts.setPassword("1234".toCharArray());
            connOpts.setKeepAliveInterval(3);
            connOpts.setCleanSession(true);
            client.connect(connOpts);
            // Nhập tên chủ đề bạn muốn subscribe (ví dụ: "client/client1")
            String subTopic = "client2_123456789";
            // Subscribe vào chủ đề
            client.subscribe(subTopic);

            // set callback
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    String messageContent = new String(mqttMessage.getPayload());
                    String response = "Client 2 đã nhận được tin nhắn";
                    if(!messageContent.equals(response)) {
                        System.out.println("Received message: " + messageContent);
                    }
                    MqttMessage message = new MqttMessage(response.getBytes());
                    message.setQos(2);
                    client.publish(subTopic, message);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                }
            });

            System.out.println("Client is subscribed to: " + subTopic);

        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

}
