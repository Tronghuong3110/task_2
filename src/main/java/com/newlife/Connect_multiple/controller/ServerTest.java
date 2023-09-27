package com.newlife.Connect_multiple.controller;

import org.eclipse.paho.client.mqttv3.*;

import java.util.Scanner;

public class ServerTest {
    public static void main(String[] args) throws MqttException {
        // Tạo 10 topic
       while (true) {
           String[] topics = {"topic-1"};

           // Khởi tạo client
           MqttClient client = new MqttClient("tcp://localhost:1883", "client-1");

           // Kết nối tới broker
           MqttConnectOptions options = new MqttConnectOptions();
           options.setCleanSession(true);
           options.setUserName("client-1");
           options.setPassword("1234".toCharArray());
           client.setCallback(new MqttCallback() {
               @Override
               public void connectionLost(Throwable throwable) {

               }

               @Override
               public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                   String response = new String(mqttMessage.getPayload());
                   System.out.println(response);
               }
               @Override
               public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

               }
           });
           Scanner sc = new Scanner(System.in);
           String sendMessage = sc.nextLine();
           client.connect(options);

           // Subscribe tất cả các topic
           for (String topic : topics) {
               client.subscribe(topic, 2);
               System.out.println( "Đã sub "+topic);
           }

           MqttMessage message = new MqttMessage(sendMessage.getBytes());
           message.setQos(2);
           client.publish(topics[0], message);
           System.out.println("Đã gửi lệnh " + topics[0] + " " + sendMessage);
       }
    }
}
