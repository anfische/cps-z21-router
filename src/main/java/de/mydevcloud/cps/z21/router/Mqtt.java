/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.mydevcloud.cps.z21.router;

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
            mqttClient = new MqttClient("", "");
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
    
    public void createCallback(MqttCallback mqttCallback){
        mqttClient.setCallback(mqttCallback);
    }
}
