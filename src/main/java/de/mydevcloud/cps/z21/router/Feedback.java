/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.mydevcloud.cps.z21.router;

import java.time.Instant;

/**
 *
 * @author andreas
 */
public class Feedback {
    private boolean status; // true = besetzt | false = frei
    private Instant timestamp;
    
    public Feedback(){
        status = false;
        timestamp = Instant.now();
    }
    
    public void setStatus(boolean status){
        this.status = status;
    }
    
    public void setIntStatus(int intStatus) throws Exception{
        if(intStatus == 0){
            this.status = false;
        }
        else if(intStatus == 1){
            this.status = true;
        }
        else {
            throw new Exception("For intStatus only values 0 and 1 are allowed!");
        }
    }
    
    public boolean getStatus(){
        return status;
    }
    
    public int getIntStatus(){
        int intStatus = status ? 1 : 0;
        return intStatus;
    }
    
    public void setTimestamp(){
        timestamp = Instant.now();
    }
    
    public Instant getTimestamp(){
        return timestamp;
    }
}
