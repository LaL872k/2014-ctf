/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package invincibe83.CaptureTheFlag;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
public class Menu implements Runnable{
    final String title = "Capture The Flag - Beta Version";
    private boolean running = true;
    MenuScreen ms;
    double amountOfTicks = 60D;
    AL al = new AL();
    ML ml = new ML();
    MMA mma = new MMA();
    Player[] team1 = new Player[6], team2 = new Player[6];
    String name = "Player123", skin = "RockSkin", botSkin = "GoldKnightSkin";
    int user = -1;
    JFrame frame;
    
    public Menu(){
        frame = new JFrame();
        frame.setTitle(title);
        
        ms = new MenuScreen(frame);
        
        frame.add(ms);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMaximumSize(new Dimension(770, 510));
        frame.setMinimumSize(new Dimension(770, 510));
        frame.setPreferredSize(new Dimension(770, 510));
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.addKeyListener(al);
        frame.addMouseListener(ml);
        frame.addMouseMotionListener(mma);
        frame.setVisible(true);
        frame.pack();
        
        ms.updateFrame(frame);
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
                        update();
                        delta--;
                    }
                    render();
                }
            }
        }catch(Exception e){}
    }
    
    public void autoLaunch(){
        user = 0;
        team1[0] = new Player(name, new Point(1, 65*38/2-32/2), true, 100, 40, 1, 1, 72*64/2, skin);
        for (int q = 1; q < team1.length; q++){
            team1[q] = new Player("bot", new Point(1, 65*38/2-32/2), false, 100, 40, 1, 1, 72*64/2, botSkin);
        }
        for (int q = 0; q < team2.length; q++){
            team2[q] = new Player("bot", new Point(64*72-33, 65*38/2-32/2), false, 100, 40, 1, 2, 72*64/2, botSkin);
        }
        //this.launch();
    }
    
    public void update(){
        ms.update(this);
        
        if (ms.changes){
            frame.setSize(500 + 6, 400 + 29);
            frame.setMaximumSize(new Dimension(500 + 6, 400 + 29));
            frame.setMinimumSize(new Dimension(500 + 6, 400 + 29));
            frame.setPreferredSize(new Dimension(500 + 6, 400 + 29));
            frame.pack();
            frame.setLocationRelativeTo(null);
            ms.changes = false;
            System.out.println("change");
        }
    }
    
    public void render(){
        ms.repaint();
    }
    
    public void launcher(Map m, Player[] team1, Player[] team2){
        Thread g = new Thread(new Game(this, m, team1, team2, user));
        g.start();
    }
    
    public void setFrame(JFrame frame){
        this.frame = null;
        this.frame = frame;
    }
    
    public boolean checkReadyToLaunch(){
        for (int q = 0; q < team1.length; q++){
            if (team1[q] == null || team2[q] == null){
                ms.errorMessage = "You can not leave any players equal to unknown";
                return false;
            }
        }
        if (user == -1){
            ms.errorMessage = "The Owner of the match must be playing";
            return false;
        }
        return true;
    }
    
    public void launch(){
        if (checkReadyToLaunch()){
            launcher(ms.maps.get(ms.mapuse), team1, team2);
        }
    }
    
    private class AL extends KeyAdapter{
        public void keyPressed(KeyEvent e){
            
        }
        public void keyReleased(KeyEvent e){
            
        }
    }
    
    private class MMA extends MouseMotionAdapter{
        public void mouseMoved(MouseEvent m){
            int x = m.getX()+5-8;
            int y = m.getY()+5-31;
            //System.out.println(x + " " + y);

            if (new Rectangle(50, 200, 200, 40).contains(x, y) && ms.page == 1){
                ms.create = ms.create2;
            }else{
                ms.create = ms.create1;
            }
            if (new Rectangle(50, 320, 192, 50).contains(x, y) && ms.page == 2){
                ms.start = ms.start2;
            }else{
                ms.start = ms.start1;
            }
        }
    }
    
    public void turnUserToNull(){
        if (user != -1){
            if (user <= 6){
                team1[user] = null;
            }else{
                team2[user-6] = null;
            }
        }
    }
    
    private class ML extends MouseAdapter{
        public void mouseClicked(MouseEvent m) {
            int x = m.getX()-8+5;
            int y = m.getY()-31+5;
            
            if (ms.page == 4){
                ms.page = 2;
                x = -1;
                y = -1;
            }
            if (new Rectangle(50, 320, 192, 50).contains(x, y) && ms.page == 2){
               launch();
               x = -1;
               y = -1;
            }

            if (new Rectangle(50, 200, 200, 40).contains(x, y) && ms.page == 1){
                ms.page = 2;
                x = -1;
                y = -1;
            }
            
            if (new Rectangle(425, 50, 50, 50).contains(x, y) && ms.page == 2){
                ms.page = 1;
                x = -1;
                y = -1;
            }
            
            if (new Rectangle(425, 110, 50, 50).contains(x, y) && ms.page == 2){
                ms.page = 4;
                x = -1;
                y = -1;
            }
            
            if (new Rectangle(50, 255, 200, 20).contains(x, y) && ms.page == 2){
                String namebefore = name;
                name = JOptionPane.showInputDialog(null, "Name : ", "Name Change", 1);
                if (name == null){
                    name = namebefore;
                }
                x = -1;
                y = -1;
            }
            if (new Rectangle(50, 285, 200, 20).contains(x, y) && ms.page==2){
                ms.page = 3;
                x = -1;
                y = -1;
            }
            for (int q = 0; q < 4; q++){
                if (new Rectangle(40, 120 + 70*q, 450, 50).contains(x, y) && ms.page == 3){
                    ms.mapuse = q+((ms.mappage-1)*4);
                    ms.page = 2;
                }
            }
            for (int q = 0; q < ms.totalpages; q++){
                if (new Rectangle(90+q*40, 60, 30, 30).contains(x, y) && ms.page == 3){
                    ms.mappage = q+1;
                }
            }
            for (int q = 0; q < team1.length; q++){
                if (new Rectangle(155, 54 + q*34, 20, 20).contains(x, y) && ms.page == 2){
                    turnUserToNull();
                    team1[q] = new Player(name, new Point(1, 65*38/2-32/2), true, 100, 40, 1, 1, 72*64/2, skin);
                    user = q;
                    x = -1;
                    y = -1;
                }
            }
            for (int q = 0; q < team2.length; q++){
                if (new Rectangle(340, 54 + q*34, 20, 20).contains(x, y) && ms.page == 2){
                    turnUserToNull();
                    team2[q] = new Player(name, new Point(64*72-33, 65*38/2-32/2), true, 100, 40, 1, 2, 72*64/2, skin);
                    user = q+6;
                    x = -1;
                    y = -1;
                }
            }
            for (int q = 0; q < team1.length; q++){
                if (new Rectangle(185, 54 + q*34, 20, 20).contains(x, y) && ms.page == 2){
                    team1[q] = new Player("bot", new Point(1, 65*38/2-32/2), false, 100, 40, 1, 1, 72*64/2, botSkin);
                    x = -1;
                    y = -1;
                }
            }
            for (int q = 0; q < team2.length; q++){
                if (new Rectangle(370, 54 + q*34, 20, 20).contains(x, y) && ms.page == 2){
                    team2[q] = new Player("bot", new Point(64*72-33, 65*38/2-32/2), false, 100, 40, 1, 2, 72*64/2, botSkin);
                    x = -1;
                    y = -1;
                }
            }
        }

        public void mouseExited(MouseEvent m) {
            
        }

        public void mouseEntered(MouseEvent m) {
            
        }

        public void mousePressed(MouseEvent m) {
            
        }

        public void mouseReleased(MouseEvent m) {
            
        }
        
        public void mouseDragged(MouseEvent m){
            
        }
        
        public void mouseWheelMoved(MouseWheelEvent e){
            
        }
        
        public void mousemMoved(MouseEvent m){
            
        }
    }
    
}
