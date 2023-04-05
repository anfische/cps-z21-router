/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package de.mydevcloud.cps.z21.router;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.Arrays;
import java.util.List;
import z21Drive.LocoAddressOutOfRangeException;
import z21Drive.Z21;

import z21Drive.actions.Z21ActionLanXSetTurnout;
import z21Drive.actions.Z21ActionSetLocoDrive;

import z21Drive.broadcasts.Z21BroadcastLanXTurnoutsInfo;
import z21Drive.broadcasts.Z21BroadcastLanRmBusDataChanged;
import z21Drive.broadcasts.BroadcastTypes;
import z21Drive.broadcasts.Z21BroadcastListener;
import z21Drive.broadcasts.Z21Broadcast;
import z21Drive.broadcasts.BroadcastFlagHandler;
import z21Drive.broadcasts.BroadcastFlags;

import org.eclipse.paho.client.mqttv3.*;
import z21Drive.actions.Z21Action;
/**
 *
 * @author andreas
 */

public class CpsZ21Router {
        
    public static void main(String[] args) {
        final Z21 z21 = Z21.instance;
        FeedbackStatus fb = new FeedbackStatus();
        Mqtt mqtt = new Mqtt();
        MqttCallback mqttCallback; 
        
        mqttCallback = new MqttCallback() {
            @Override
            public void connectionLost(Throwable thrwbl) {
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                //System.out.println(topic + ": " + message.toString());
                List<String> topicList = Arrays.asList(topic.split("/"));

                switch(topicList.get(0)){
                    case "turnout":
                        if ("get".equals(topicList.get(1))) {
                            System.out.println("Turnout Info from z21 received: ");
                            System.out.println("Turnout " + topicList.get(2) + ": " + message.toString());
                        } else if ("set".equals(topicList.get(1))) {
                            System.out.println("Turnout Action received, forward to z21");
                            int address = 99;
                            byte position = 3;
                            try {
                                address = Integer.parseInt(topicList.get(2));
                                // address -1 to adapt to z21 addresses
                                address--;
                                position = (byte) Integer.parseInt(message.toString());
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }                            
                            z21.sendActionToZ21(new Z21ActionLanXSetTurnout(address,position, true));
                            try {
                                Thread.sleep(150);
                            } catch (InterruptedException e){
                                Thread.currentThread().interrupt();
                            }
                            z21.sendActionToZ21(new Z21ActionLanXSetTurnout(address,position, false));
                        }
                        break;
                    case "train":
                        if("set".equals(topicList.get(1))) {
                            System.out.println("Train command received:");
                            System.out.println("Address: " + topicList.get(2) + " | Speed: " + message.toString());
                            int address = 99;
                            int speed = 0;
                            try {
                                address = Integer.parseInt(topicList.get(2));
                                speed = Integer.parseInt(message.toString());
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                            z21.sendActionToZ21(new Z21ActionSetLocoDrive(address, speed, 3, true));
                        }
                        break;
                    default:
                        System.out.println("Unknown MQTT message received");
                        break;
                }
                
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken imdt) {
            }
        };
        mqtt.createCallback(mqttCallback);
        mqtt.subscribe("turnout/#");
        mqtt.subscribe("train/set/#");
        
        

        //TURNOUT INFO
        z21.addBroadcastListener(new Z21BroadcastListener() {
            @Override
            public void onBroadCast(BroadcastTypes type, Z21Broadcast broadcast) {
                if (type == BroadcastTypes.LAN_X_GET_TURNOUT_INFO){
                	
                	
                    Z21BroadcastLanXTurnoutsInfo t = (Z21BroadcastLanXTurnoutsInfo) broadcast;
                    //DEBUG Output
                    //System.out.println("Turnout address: " + t.getTurnoutAddress());
                    //System.out.println("Position: " + t.getPosition());
                    
                    // address +1 and position -1
                    String sTopic = "turnout/get/" + Integer.toString(t.getTurnoutAddress()+1);
                    byte[] message = Integer.toString(t.getPosition()-1).getBytes(UTF_8);
                    mqtt.publish(sTopic, new MqttMessage(message));
                }
            }

            @Override
            public BroadcastTypes[] getListenerTypes() {
                return new BroadcastTypes[]{BroadcastTypes.LAN_X_GET_TURNOUT_INFO};
            }
        });
        
        // RMBUS INFO
        z21.addBroadcastListener(new Z21BroadcastListener() {
            @Override
            public void onBroadCast(BroadcastTypes type, Z21Broadcast broadcast) {
                if (type == BroadcastTypes.LAN_RMBUS_DATACHANGED){
                	
                	
                    Z21BroadcastLanRmBusDataChanged r = (Z21BroadcastLanRmBusDataChanged) broadcast;
                    //System.out.println(r.getFeedbackStatusString());
                    try{
                        fb.update(r.getFeedbackStatus()[0], mqtt);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public BroadcastTypes[] getListenerTypes() {
                return new BroadcastTypes[]{BroadcastTypes.LAN_RMBUS_DATACHANGED};
            }
        });
        
        BroadcastFlagHandler.setReceive(BroadcastFlags.GLOBAL_BROADCASTS, true);
        BroadcastFlagHandler.setReceive(BroadcastFlags.RECEIVE_RM_BUS, true);
        
        /*try{
            z21.sendActionToZ21(new Z21ActionSetLocoDrive(74,30,3,true));
        } catch (z21Drive.LocoAddressOutOfRangeException e){
            e.printStackTrace();
        }
*/
        //z21.shutdown();
        System.out.println("Hello World!");
        while(true){}
    }
}
