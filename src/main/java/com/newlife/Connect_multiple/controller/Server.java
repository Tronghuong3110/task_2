package com.newlife.Connect_multiple.controller;

import com.newlife.Connect_multiple.service.OnMessageCallback;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Scanner;

public class Server {
    private static boolean checkResponse = true;
    public static void main(String[] args) {
        int qos = 0;
        boolean check = true;
        Scanner sc = new Scanner(System.in);
        while (check) {
            try {
                String subTopic1 = "test/client_1_test";
                String subTopic2 = "test/test_1_client_2";

                System.out.println("Nhập lệnh muốn gửi");
                String content = sc.nextLine();

                String pubTopic = "test/test_1";
//                String broker = "tcp://192.168.100.12:1883";
//                String broker = "tcp://192.168.0.101:1883";
                String broker = "tcp://192.168.27.101:1883";
//                String broker = "tcp://192.168.113.122:1883";
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
                System.out.println("Subscribe = " + subTopic1);
                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable throwable) {

                    }

                    @Override
                    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                        String message = new String(mqttMessage.getPayload());
                        System.out.println("Phản hồi từ client: " + message);
                        if(message.equals("")) { // không nhận được phản hồi từ client
                            checkResponse = false;
                        }
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                    }
                });

                client.connect(connOpts);
                client.subscribe(subTopic1);
                client.subscribe(subTopic2);

//              Send message to client
//              xử lý gửi tin nhắn tới client khi client không nhận được
                Integer count = 0;
                do {
                    MqttMessage message = new MqttMessage(content.getBytes());
//                  message.setQos(qos);
//                  client.publish(pubTopic, message);
//                  set thời gian chờ sau mỗi lần gửi lại
                }
                while (count++ <= 2 && checkResponse == false);

            } catch (MqttException me) {
                System.out.println("reason " + me.getReasonCode());
                System.out.println("msg " + me.getMessage());
                System.out.println("loc " + me.getLocalizedMessage());
                System.out.println("cause " + me.getCause());
                System.out.println("excep " + me);
                me.printStackTrace();
            }
        }
    }
}


/*
*
*
# {allow, {ipaddr, "192.168.100.17"}, publish, ["$SYS/#", "#"]}.

# {allow, {ipaddr, "192.168.100.13"}, subscribe, ["$SYS/#", "#"]}.
* */