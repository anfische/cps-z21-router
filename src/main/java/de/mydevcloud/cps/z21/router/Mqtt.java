/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.mydevcloud.cps.z21.router;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 *
 * @author andreas
 */
public class Mqtt {
    private MqttClient mqttClient;
    
    public Mqtt(){
        try{
            mqttClient = new MqttClient("tcp://localhost:1883", "z21Router", new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName("mqttuser");
            options.setPassword("mqttpassword".toCharArray());
            mqttClient.connect(options);
        } catch (MqttException e){
            e.printStackTrace();
        }
    }
    
    public void publish(String topic, MqttMessage message){
        try{
            mqttClient.publish(topic, message);
        } catch (MqttException e){
            e.printStackTrace();
        }
    }
    
    public void subscribe(String topic) {
        try {
            mqttClient.subscribe(topic);
        } catch (MqttException ex) {
            Logger.getLogger(Mqtt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void createCallback(MqttCallback mqttCallback){
        mqttClient.setCallback(mqttCallback);
    }
}
