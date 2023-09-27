package com.newlife.Connect_multiple.controller;

import org.eclipse.paho.client.mqttv3.*;
import org.json.simple.JSONObject;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        int qos = 0;
        Scanner sc = new Scanner(System.in);

        try {
            String broker = "tcp://192.168.100.5:1883";
            String clientId = "Server_Id";
            MemoryPersistence persistence = new MemoryPersistence();
            MqttClient client = new MqttClient(broker, clientId, persistence);

            // MQTT connection option
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName("server");
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
                    String responseFromClient = new String(mqttMessage.getPayload());
                    System.out.println(responseFromClient);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
            client.connect(connOpts);

            // Nhập tên client bạn muốn gửi tin nhắn đến
//            System.out.println("Nhập tên client (client1, client2, ...):");
//            String clientName = sc.nextLine();

            // Nhập nội dung tin nhắn bạn muốn gửi
            System.out.println("Nhập lệnh muốn gửi:");
            String content = sc.nextLine();

            // Tạo tên chủ đề của client dựa trên tên client
            String pubTopic = "Probe_2/800137002640200_Probe_2";

            // Gửi tin nhắn đến client cụ thể
            client.publish(pubTopic, new MqttMessage(content.getBytes()));
//            client.disconnect();
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }
}


/*
*
*
# {allow, {ipaddr, "192.168.100.17"}, publish, ["$SYS/#", "#"]}.

# {allow, {ipaddr, "192.168.100.13"}, subscribe, ["$SYS/#", "#"]}.
* */