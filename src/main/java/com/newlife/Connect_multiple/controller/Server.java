package com.newlife.Connect_multiple.controller;

import org.eclipse.paho.client.mqttv3.*;
import org.json.simple.JSONObject;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        int qos = 0;
        Scanner sc = new Scanner(System.in);
        String[] publics = {"client_123456789", "client2_123456789"};
        try {
            String broker = "tcp://localhost:1883";
            String clientId = "Server_Id";
            MemoryPersistence persistence = new MemoryPersistence();
            MqttClient client = new MqttClient(broker, clientId, persistence);

            // MQTT connection option
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName("server");
            connOpts.setPassword("1234".toCharArray());
            connOpts.setKeepAliveInterval(3);
            connOpts.setCleanSession(true);
            // Nhập nội dung tin nhắn bạn muốn gửi
            System.out.println("Nhập lệnh muốn gửi:");
            String content = sc.nextLine();
            client.connect(connOpts);
            for(String str : publics) {
                client.subscribe(str);
            }

            // set callback
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    String responseFromClient = new String(mqttMessage.getPayload());
                    if(!responseFromClient.contains("Client")) {
                        System.out.println(responseFromClient);
                    }
                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });


            // Tạo tên chủ đề của client dựa trên tên client

            // Gửi tin nhắn đến client cụ thể
            int i = 0;
            for(String str : publics) {
                i++;
                MqttMessage message = new MqttMessage((content + i).getBytes());
                message.setQos(2);
                client.publish(str, message);
            }
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