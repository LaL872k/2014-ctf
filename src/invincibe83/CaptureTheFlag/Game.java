/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package invincibe83.CaptureTheFlag;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Scanner;
import javax.swing.JFrame;

public class Game implements Runnable{
    boolean running = true, updating = true; //is the thread running
    boolean flag1onground = true, flag2onground = true;
    final double amountOfTicks = 60D; //updates per second
    public String title = "Capture The Flag - Beta Version";
    float time = 0f, jailTime = 300f, orginaljailbreak = 300f;
    int userNumber = 1, centerLine = 2304, team1score = 0, team2score = 0;
    Rectangle flag1, flag2, jail1, jail2;
    Player[] team1 = new Player[6];
    Player[] team2 = new Player[6];
    AL al = new AL(this);
    int[] mapLayout = new int[72*38];
    JFrame frame;
    
    World world;
    
    public Game(Menu n, Map m, Player[] team1, Player[] team2, int user){
        this.team1 = team1;
        this.team2 = team2;
        jail1 = m.getJail1();
        jail2 = m.getJail2();
        flag1 = m.getFlag1();
        flag2 = m.getFlag2();
        userNumber = user;
        for (int q = 0; q < team1.length && q < team2.length; q++){
            team1[q].addBoundary("MapBoundary", 10000f, new Rectangle(0, 0, 72*64, mapLayout.length/72*64), false);
            team2[q].addBoundary("MapBoundary", 10000f, new Rectangle(0, 0, 72*64, mapLayout.length/72*64), false);
        }
        //creating the frame or window in which we play
        mapLayout = m.getInts();
        world = new World(getUser(user), mapLayout, 72);
        world.update(getUser(userNumber), getTotalMatchTime(), this);
        frame = new JFrame();
        frame.setTitle(title);
        //adds the panel where we can draw on
        frame.add(world);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMaximumSize(new Dimension(500+6, 500 + 29));
        frame.setMinimumSize(new Dimension(500+6, 500 + 29));
        frame.setPreferredSize(new Dimension(500+6, 500 + 29));
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.addKeyListener(al);
        frame.setVisible(true);
        frame.pack();
        //getUser(userNumber).addBoundary("hi", 6f, new Rectangle(100, 100, 100, 100), true);
        //getUser(userNumber).addItem(new PlayerItem(new PlayerEffect("Speed", 60f, 1, 100, -600, ""), 50, 50));
//        for (int q = 0; q < team2.length; q++){
//            team2[q].turnSneakOn();
//        }
    }
    
    public void run(){
        //*The code that is getting excuted by the thread  
        try{
            while(running){
                //*Updating only 60 times a second
                long lastTime = System.nanoTime();
                double ns = 1000000000 / amountOfTicks;
                double delta = 0;

                //Render as fast as the computer and update 60FPS
                while(running){
                    long now = System.nanoTime();
                    delta += (now - lastTime) / ns;
                    lastTime = now;
                    if(delta >= 1){
                        if (updating){ //only updates when updating = true
                            update();
                        }
                        delta--;
                    }
                    render();
                }
            }
        }catch(Exception e){}
    }
    
    public void update(){
        checkWin();
        world.update(getUser(userNumber), getTotalMatchTime(), this);
        updateTime();
        checkJailBreak();
        //System.out.println(getUser(userNumber).getSpeed());
        for (int q = 0; q < team1.length && q < team2.length; q++){
            team1[q].update(world, this);
            team2[q].update(world, this);
            flagtakenCheck(1, team1[q]);
            flagtakenCheck(2, team2[q]);
            flagScoreCheck(team1[q]);
            flagScoreCheck(team2[q]);
            jailEscapeCheck(team1[q]);
            jailEscapeCheck(team2[q]);
        }
        playertoplayerintersection();
    }
    
    public void checkWin(){
        if (team1score >= 5 || team2score >= 5){
            updating = false;
            frame.dispose();
        }
    }
    
    public void playertoplayerintersection(){
        for (int q = 0; q < team1.length; q++){
            for (int t = 0; t < team2.length; t++){
                if (team1[q].getDimensions().intersects(team2[t].getDimensions()) && team1[q].getX() > centerLine && !team1[q].inJail()){
                    team1[q].gotoJail(jail2);
                    team2[t].taggedaplayer();
                    if(team1[q].hasFlag()){
                        flag2onground = true;
                        team1[q].setFlag(false);
                        team1[q].removeItem("Flag");
                    }
                }
            }
        }
        for (int q = 0; q < team2.length; q++){
            for (int t = 0; t < team1.length; t++){
                if (team2[q].getDimensions().intersects(team1[t].getDimensions())  && team2[q].getX() < centerLine && !team2[q].inJail()){
                    team2[q].gotoJail(jail1);
                    team1[t].taggedaplayer();
                    if(team2[q].hasFlag()){
                        flag1onground = true;
                        team2[q].setFlag(false);
                        team2[q].removeItem("Flag");
                    }
                }
            }
        }
    }
    
    public void jailBreak(){
        jailTime = orginaljailbreak;
        for (int q = 0; q < team1.length && q < team2.length; q++){
            team1[q].freeFromJail();
            team2[q].freeFromJail();
        }
        world.changeMessage("Jail Break!");
    }
    
    public void jailEscapeCheck(Player p){
        for (int q = 0; q < team1.length; q++){
            if (p.getTeam() == 1 && p.getDimensions().intersects(team1[q].getDimensions()) && team1[q].inJail() && p != team1[q] && !p.inJail()){
                team1[q].freeFromJail();
                p.freedaplayer();
                p.respawn();
            }
            if (p.getTeam() == 2 && p.getDimensions().intersects(team2[q].getDimensions()) && team2[q].inJail() && p != team1[q] && !p.inJail()){
                team2[q].freeFromJail();
                p.freedaplayer();
                p.respawn();
            }
        }
    }
    
    public void flagtakenCheck(int team, Player p){
        if (p.getDimensions().intersects(flag1) && team == 2 && flag1onground){
            p.setFlag(true);
            p.addItem(new PlayerItem("Flag", "Flag", 1, 1));
            flag1onground = false;
            world.changeMessage("Team2 Has Taken The Flag!");
        }if (p.getDimensions().intersects(flag2) && team == 1 && flag2onground){
            p.setFlag(true);
            flag2onground = false;
            p.addItem(new PlayerItem("Flag", "Flag", 1, 1));
            world.changeMessage("Team1 Has Taken The Flag!");
        }
    }
    
    public void reset(){
        for (int q = 0; q < team1.length && q < team2.length; q++){
            team1[q].tp(team1[q].getSpawn().x, team1[q].getSpawn().y);
            team2[q].tp(team2[q].getSpawn().x, team2[q].getSpawn().y);
            team1[q].setFlag(false);
            team2[q].setFlag(false);
            team1[q].removeItem("Flag");
            team2[q].removeItem("Flag");
        }
        flag1onground = true;
        flag2onground = true;
    }
    
    public void flagScoreCheck(Player p){
        if (p.getTeam() == 1 && p.getX() < centerLine && p.hasFlag()){
            team1score++;
            p.scored();
            p.setFlag(false);
            p.turnSneakOff();
            p.turnSprintOff();
            reset();
            world.changeMessage("Team1 has captured the flag!");
        }if (p.getTeam() == 2 && p.getX() > centerLine && p.hasFlag()){
            team2score++;
            p.scored();
            p.setFlag(false);
            p.turnSneakOff();
            p.turnSprintOff();
            reset();
            world.changeMessage("Team2 has captured the flag!");
        }
    }
    
    public void checkJailBreak(){
        if (jailTime <= 0){
            jailBreak();
        }
    }
    
    public void render(){
        world.repaint();
    }
    
    public void updateTime(){
        time += 1/60f;
        jailTime -= 1/60f;
    }
    
    public float getTotalMatchTime(){
        return time;
    }
    
    public float getTimeLeftTillJailBreak(){
        return jailTime;
    }
    
    public Player getUser(int p){
        if (p < 6){
            return team1[p];
        }else{
            return team2[p-6];
        }
    }
    
    public Rectangle getFlag1Location(){
        return flag1;
    }
    
    public Rectangle getFlag2Location(){
        return flag2;
    }
    
    public Rectangle getJail1Location(){
        return jail1;
    }
    
    public Rectangle getJail2Location(){
        return jail2;
    }
    
    private class AL extends KeyAdapter{
        Game g;
        public AL(Game g){
            this.g = g;
        }
        public void keyPressed(KeyEvent e){
//            for (int q = 0; q < team1.length && q < team2.length; q++){
//                team1[q].KeyPressed(e);
//                team2[q].KeyPressed(e);
//            }
            getUser(userNumber).KeyPressed(e, g);
            world.keyPressed(e);
        }
        public void keyReleased(KeyEvent e){
//            for (int q = 0; q < team1.length && q < team2.length; q++){
//                team1[q].KeyReleased(e);
//                team2[q].KeyReleased(e);
//            }
            getUser(userNumber).KeyReleased(e);
            world.keyRelesed(e);
        }
    }
}
