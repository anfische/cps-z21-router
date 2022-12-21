/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package de.mydevcloud.cps.z21.router;

import java.util.Arrays;
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
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 *
 * @author andreas
 */

public class CpsZ21Router {
    
    private static String MQTT_SERVER = "";
        
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
                System.out.println(topic + ": " + Arrays.toString(message.getPayload()));     
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken imdt) {
            }
        };
        mqtt.createCallback(mqttCallback);
        
        

        //TURNOUT INFO
        z21.addBroadcastListener(new Z21BroadcastListener() {
            @Override
            public void onBroadCast(BroadcastTypes type, Z21Broadcast broadcast) {
                if (type == BroadcastTypes.LAN_X_GET_TURNOUT_INFO){
                	
                	
                    Z21BroadcastLanXTurnoutsInfo t = (Z21BroadcastLanXTurnoutsInfo) broadcast;
                    System.out.println("Turnout address: " + t.getTurnoutAddress());
                    System.out.println("Position: " + t.getPosition());
                    mqtt.publish("", new MqttMessage());
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
                        fb.update(r.getFeedbackStatus()[0]);
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
