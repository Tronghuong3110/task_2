package com.newlife.Connect_multiple.controller;

import org.eclipse.paho.client.mqttv3.*;

public class ClientTest1 {
    public static void main(String[] args) throws MqttException {
        // Tạo client
        MqttClient client = new MqttClient("tcp://localhost:1883", "client-1");

        // Kết nối tới broker
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);

        // Subscribe topic
        String topic = "topic-1";
//        MqttTopic mqttTopic = client.getTopic(topic);

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                String response = new String(mqttMessage.getPayload());
                System.out.println("-->" + s + " id " + mqttMessage.toString());
                System.out.println(response);
                String responseMessage1 = "Client 1 nhận được tin nhắn";
                if(!response.equals(responseMessage1)) {
                    MqttMessage responseMessage = new MqttMessage(responseMessage1.getBytes());
                    responseMessage.setQos(2);
                    client.publish(topic, responseMessage);
                    Thread.sleep(10000);
                    System.out.println("Hoàn thành");
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            }
        });

        client.connect(options);
        client.subscribe(topic, 2);
    }
}
