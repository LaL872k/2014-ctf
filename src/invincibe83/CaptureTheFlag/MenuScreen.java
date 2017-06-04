/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package invincibe83.CaptureTheFlag;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Arthur
 */
public class MenuScreen extends JPanel{
    private ImageIcon back = new ImageIcon(new ImageIcon("Images\\MenuScreen.png").getImage());
    private ImageIcon companyname = new ImageIcon(new ImageIcon("Images\\name.png").getImage());
    private ImageIcon title = new ImageIcon(new ImageIcon("Images\\MenuTitle.png").getImage());
    ImageIcon create1 = new ImageIcon(new ImageIcon("Images\\CreateGameButton1.png").getImage().getScaledInstance(200, 40, 1));
    ImageIcon create2 = new ImageIcon(new ImageIcon("Images\\CreateGameButton2.png").getImage().getScaledInstance(200, 40, 1));
    private ImageIcon createMatch = new ImageIcon(new ImageIcon("Images\\CreateMatchBorder.png").getImage());
    private ImageIcon join = new ImageIcon(new ImageIcon("Images\\Join.png").getImage());
    private ImageIcon botjoin = new ImageIcon(new ImageIcon("Images\\BotJoin.png").getImage());
    private ImageIcon mapborder = new ImageIcon(new ImageIcon("Images\\MapImageBorder.png").getImage());
    private ImageIcon exit = new ImageIcon(new ImageIcon("Images\\Exit.png").getImage().getScaledInstance(50, 50, 1));
    private ImageIcon keyinfo = new ImageIcon(new ImageIcon("Images\\Keys.png").getImage().getScaledInstance(450, 450, 1));
    private ImageIcon info = new ImageIcon(new ImageIcon("Images\\Info.png").getImage().getScaledInstance(50, 50, 1));
    ImageIcon start1 = new ImageIcon(new ImageIcon("Images\\StartGame1.png").getImage().getScaledInstance(192, 50, 1));
    ImageIcon start2 = new ImageIcon(new ImageIcon("Images\\StartGame2.png").getImage().getScaledInstance(192, 50, 1));
    private ImageIcon aellc = new ImageIcon(new ImageIcon("Images\\AELLC.png").getImage());
    ImageIcon create = create1, start = start1;
    int page = 1, mappage = 1, totalpages;
    Color myColor = new Color(80, 69, 23);
    Font myFont = new Font("San Serif", Font.PLAIN, 12);
    Player[] team1, team2;
    String name = "", errorMessage = "";
    Countdown count;
    JFrame frame;
    //map
    ArrayList <Map> maps = new ArrayList();
    int mapuse = 0;
    public boolean changes = false, remain = false;
    
    public MenuScreen(JFrame frame){
        openFile();
        readFile();
        closeFile();
        count = new Countdown(5f);
        this.frame = frame;
    }
    
    public void update(Menu m){
        name = m.name;
        team1 = m.team1;
        team2 = m.team2;
        
        if (count.finished() && !remain){
            changes = true;
            remain = true;
        }
    }
    
    private Scanner x;
    public void openFile(){
        try{
            x = new Scanner(new File("Maps\\Map.txt"));
        }catch(Exception e){
            System.out.println("could not load file");
        }
    }
    
    public void readFile(){
        int count = 1;
        int step = 1;
        int n = 0;
        while(x.hasNext()){
            if (step == 1){
                maps.add(new Map());
                maps.get(n).setName(x.next());
                maps.get(n).setOwner(x.next());
                maps.get(n).setImageLocation(x.next());
                maps.get(n).setFlag1(new Rectangle(Integer.parseInt(x.next()), Integer.parseInt(x.next()), 50, 50));
                maps.get(n).setFlag2(new Rectangle(Integer.parseInt(x.next()), Integer.parseInt(x.next()), 50, 50));
                maps.get(n).setJail1(new Rectangle(Integer.parseInt(x.next()), Integer.parseInt(x.next()), 200, 200));
                maps.get(n).setJail2(new Rectangle(Integer.parseInt(x.next()), Integer.parseInt(x.next()), 200, 200));
                step = 2;
            }else if (step == 2){
                String[] line = new String[72*38];
                for (int q = 0; q < line.length; q++){
                    line[q] = x.next();
                    maps.get(n).setCertainMap(q, Integer.parseInt(line[q].toString()));
                }
                step = 1;
                count++;
                n++;
            }
        }
        totalpages = count/4;
        if (count/4*4 != count){
            totalpages++;
        }
    }
    
    public void closeFile(){
        x.close();
    }
    
    public void updateFrame(JFrame frame){
        this.frame = frame;
    }
    
    public void enchance(JFrame frame){
        frame.setSize(500 + 6, 400 + 29);
        frame.setMaximumSize(new Dimension(500+6, 400 + 29));
        frame.setMinimumSize(new Dimension(500+6, 400 + 29));
        frame.setPreferredSize(new Dimension(500+6, 400 + 29));
        frame.pack();
    }
    
    public void paint(Graphics graphics){
        super.paint(graphics);
        Graphics2D g2 = (Graphics2D) graphics;
        if (!count.finished()){
            aellc.paintIcon(this, g2, 0, 0);
        }else{
            g2.setColor(myColor);
            g2.setFont(myFont);
            back.paintIcon(this, g2, 0, 0);
            if (page == 1){
                title.paintIcon(this, g2, 30, 30);
                companyname.paintIcon(this, g2, 300, 370);
                create.paintIcon(this, g2, 50, 200);
            }else if (page == 2){
                createMatch.paintIcon(this, g2, 50, 50);
                for (int q = 0; q < 6; q++){
                    try{
                        g2.drawString(team1[q].getName(), 55, 70 + q*34);
                    }catch(Exception e){
                        g2.drawString("Unknown", 55, 70 + q*34);
                    }
                    join.paintIcon(this, g2, 155, 54 + q*34);
                    botjoin.paintIcon(this, g2, 185, 54 + q*34);
                }
                for (int q = 0; q < 6; q++){
                    try{
                        g2.drawString(team2[q].getName(), 230, 70 + q*34);
                    }catch(Exception e){
                        g2.drawString("Unknown", 230, 70 + q*34);
                    }
                    join.paintIcon(this, g2, 340, 54 + q*34);
                    botjoin.paintIcon(this, g2, 370, 54 + q*34);
                }
                start.paintIcon(this, g2, 50, 320);
                exit.paintIcon(this, g2, 425, 50);
                info.paintIcon(this, g2, 425, 110);
                g2.drawString("Name = " + name, 55, 270);
                g2.drawRect(50, 255, 200, 20);
                g2.drawString("Map = " + maps.get(mapuse).getName() + " by " + maps.get(mapuse).getOwner(), 55, 300);
                g2.drawRect(50, 285, 200, 20);
                g2.setColor(Color.red);
                g2.drawString(errorMessage, 50, 390);
            }else if (page == 3){
                g2.drawString("Page:", 50, 80);
                for (int q = 0; q < totalpages; q++){
                    g2.fillRect(90+q*40, 60, 30, 30);
                }
                for (int q = 0; q < maps.size(); q++){
                    if (mappage == 1 && q > 3){
                        break;
                    }else if ((mappage == 2) && (q < 4)){
                        q=4;
                    }else if ((mappage == 2) && (q > 7)){
                        break;
                    }
                    int n = q;
                    if (mappage == 2){
                        n = q-4;
                    }
                    maps.get(q).getImage().paintIcon(this, g2, 40, 120 + 70*n);
                    mapborder.paintIcon(this, g2, 40, 120 + 70*n);
                    g2.drawString("Map Name: " + maps.get(q).getName() + "   Owner: " + maps.get(q).getOwner(), 40, 115+70*n);
                }
            }else if (page == 4){
                keyinfo.paintIcon(this, g2, 40, 40);
            }
        }
    }
}
