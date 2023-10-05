package com.newlife.Connect_multiple.test;

import com.newlife.Connect_multiple.util.JsonUtil;
import org.eclipse.paho.client.mqttv3.*;
import org.json.simple.JSONObject;

import java.util.Scanner;

public class ServerTest {
    public static void main(String[] args) throws MqttException {
        // Tạo 10 topic
//       while (true) {
           String[] topics = {"client_123456789"};
           // Khởi tạo client
           MqttClient client = new MqttClient("tcp://localhost:1883", "server");

           // Kết nối tới broker
           MqttConnectOptions options = new MqttConnectOptions();
           options.setCleanSession(true);
           options.setUserName("server");
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
            JSONObject jsonObject = new JSONObject();
           jsonObject.put("test", "test");
           MqttMessage message = new MqttMessage(jsonObject.toJSONString().getBytes());
           message.setQos(2);
           client.publish(topics[0], message);
           System.out.println("Đã gửi lệnh " + topics[0] + " " + sendMessage);
//       }
    }
}
