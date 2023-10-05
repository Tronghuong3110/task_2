package com.newlife.Connect_multiple.test;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1")
public class Client2 {

    public static void main(String[] args) {
        try {
            String broker = "tcp://localhost:1883";
            String clientId = "Client_Id_2";
            MemoryPersistence persistence = new MemoryPersistence();
            MqttClient client = new MqttClient(broker, clientId, persistence);

            // MQTT connection option
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName("client1");
            connOpts.setPassword("1234".toCharArray());
            connOpts.setKeepAliveInterval(3);
            connOpts.setCleanSession(true);
            client.connect(connOpts);
            String subTopic = "client_123456789";
            client.subscribe(subTopic);
            // set callback
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    String messageContent = new String(mqttMessage.getPayload());
                    String response = "Client 1 đã nhận được tin nhắn";
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

        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

}
