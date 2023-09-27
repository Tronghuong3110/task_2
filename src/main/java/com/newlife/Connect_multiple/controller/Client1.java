package com.newlife.Connect_multiple.controller;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.util.Scanner;

public class Client1 {
    public static void main(String[] args) {
        try {
            String broker = "tcp://192.168.100.5:1883";
            String clientId = "Client_Id";
            MemoryPersistence persistence = new MemoryPersistence();
            MqttClient client = new MqttClient(broker, clientId, persistence);

            // MQTT connection option
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName("client");
            connOpts.setPassword("1234".toCharArray());
            connOpts.setKeepAliveInterval(3);
            connOpts.setCleanSession(true);

            // set callback
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    String messageContent = new String(mqttMessage.getPayload());
                    System.out.println("Received message: " + messageContent);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                }
            });

            client.connect(connOpts);

            // Nhập tên chủ đề bạn muốn subscribe (ví dụ: "client/client1")
            String subTopic = "Probe_1/800137002640200_Probe_1";

            // Subscribe vào chủ đề
            client.subscribe(subTopic);

            System.out.println("Client is subscribed to: " + subTopic);

        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

}
