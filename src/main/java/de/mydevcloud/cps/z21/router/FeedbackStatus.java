/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.mydevcloud.cps.z21.router;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.time.Instant;
import java.time.Duration;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 *
 * @author andreas
 */
public class FeedbackStatus {
    private Feedback[] fbArray = new Feedback[8];
    
    public FeedbackStatus(){
        for(int i = 0; i < fbArray.length; i++){
            fbArray[i] = new Feedback();
        }
    }
    
    public void update(int[] newFeedback, Mqtt mqtt) throws Exception{
        if(newFeedback.length == 8){
            for(int i = 0; i < 8; i++){
                if(compareTimestampIsNew(fbArray[i].getTimestamp())){
                    if(newFeedback[i] != fbArray[i].getIntStatus()){
                        //New value --> Upate
                        fbArray[i].setIntStatus(newFeedback[i]);
                        fbArray[i].setTimestamp();
                        System.out.println("Abschnitt " + (i+1) + ": " + fbArray[i].getIntStatus());
                        String topic = "section/" + (i+1) + "/status";
                        byte[] message = Integer.toString(fbArray[i].getIntStatus()).getBytes(UTF_8);
                        mqtt.publish(topic, new MqttMessage(message));
                    }
                }
            }
        } else {
            System.out.println("Length of feedback array not equal to 8. Not updating...");
        }
    }
    
    private boolean compareTimestampIsNew(Instant lastFeedbackTimestamp){
        boolean isNew = false;
        Instant now = Instant.now();
        long delta = Duration.between(lastFeedbackTimestamp, now).toMillis();
        if(delta > 5000){
            isNew = true;
        } 
        return isNew;
    }
}
