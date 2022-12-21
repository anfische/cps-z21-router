/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.mydevcloud.cps.z21.router;

import java.time.Instant;
import java.time.Duration;

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
    
    public void update(int[] newFeedback) throws Exception{
        if(newFeedback.length == 8){
            for(int i = 0; i < 8; i++){
                if(compareTimestampIsNew(fbArray[i].getTimestamp())){
                    if(newFeedback[i] != fbArray[i].getIntStatus()){
                        //New value --> Upate
                        fbArray[i].setIntStatus(newFeedback[i]);
                        fbArray[i].setTimestamp();
                        System.out.println("Abschnitt " + (i+1) + ": " + fbArray[i].getIntStatus());
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
        if(delta > 3000){
            isNew = true;
        } 
        return isNew;
    }
}
