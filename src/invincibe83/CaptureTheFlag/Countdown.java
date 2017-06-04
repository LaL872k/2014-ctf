/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package invincibe83.CaptureTheFlag;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Arthur
 */
public class Countdown implements Runnable{
    private float totaltime, currenttime = 0f;
    private Thread loop;
    private boolean running = true;
    private MenuScreen ms;
    private JFrame frame;
            
    public Countdown(float totaltime){
        this.totaltime = totaltime;
        
        loop = new Thread(this);
        loop.start();
    }
    
    public Countdown(float totaltime, MenuScreen ms, JFrame frame){
        this.totaltime = totaltime;
        this.ms = ms;
        
        loop = new Thread(this);
        loop.start();
    }
    
    public void run(){
        try{
            while(running){
                //*Updating only 60 times a second
                double amountOfTicks = 60D;
                long lastTime = System.nanoTime();
                double ns = 1000000000 / amountOfTicks;
                double delta = 0;

                //Render as fast as the computer and update 60FPS
                while(running){
                    long now = System.nanoTime();
                    delta += (now - lastTime) / ns;
                    lastTime = now;
                    if(delta >= 1){
                       currenttime += 1/60f;
                       if (currenttime > totaltime){
                           running = false;
                       }
                       delta--;
                    }
                }
            }
        }catch(Exception e){}
    }
    
    public void enchance(){
        if (finished()){
            ms.enchance(frame);
        }
    }
    
    public boolean finished(){
        if (currenttime > totaltime){
           return true;
       }
        return false;
    }
}
