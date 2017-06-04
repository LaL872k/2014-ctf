/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package invincibe83.CaptureTheFlag;

import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.*;

/**
 *
 * @author Arthur
 */
public class PlayerItem {
    
    private int id, stack, stacklimit, page = 1, keyPage = KeyEvent.VK_P;
    private PlayerEffect pe;
    private PlayerBoundary pb;
    private String imageLocation, name;
    private ImageIcon image, unknownimage = new ImageIcon(new ImageIcon("Images/EffectImageUnknown.png").getImage().getScaledInstance(16, 16, 1));
    private boolean exsist = true;
    private Font myFont = new Font("San Serif", Font.PLAIN, 10);
    
    public PlayerItem(PlayerEffect pe, int stack, int stacklimit){
        id = 1;
        this.pe = pe;
        this.stack = stack;
        this.stacklimit = stacklimit;
        if (stack > stacklimit){
            stack = stacklimit;
        }
        name = pe.getName();
        setImage(pe.getImageLocation());
    }
    
    public PlayerItem(PlayerBoundary pb, int stack, int stacklimit){
        id = 2;
        this.pb = pb;
        this.stack = stack;
        this.stacklimit = stacklimit;
        if (stack > stacklimit){
            stack = stacklimit;
        }
        name = pb.getname();
        setImage(pb.getImageLocation());
    }
    
    public PlayerItem(PlayerBoundary pb, PlayerEffect pe, int stack, int stacklimit){
        id = 3;
        this.pb = pb;
        this.pe = pe;
        this.stack = stack;
        this.stacklimit = stacklimit;
        if (stack > stacklimit){
            stack = stacklimit;
        }
        name = pe.getName();
        setImage(pe.getImageLocation());
    }
    
    public PlayerItem(String name, String imageLocation, int stack, int stacklimit){
        id = 4;
        this.name = name;
        this.imageLocation = imageLocation;
        this.stack = stack;
        this.stacklimit = stacklimit;
        if (stack > stacklimit){
            stack = stacklimit;
        }
        setImage(imageLocation);
    }
    
    public void update(){
        checkStack();
    }
    
    public void checkStack(){
        if (stack <= 0){
            exsist = false;
        }
    }
    
    public void setImage(String imageLocation){
        image = new ImageIcon(new ImageIcon(imageLocation).getImage().getScaledInstance(16, 16, 1));
        if (!imageLocation.contains("src/CaptureTheFlag/Image/")){
            image = unknownimage;
            imageLocation = "src/CaptureTheFlag/Image/EffectImageUnknown.png";
        }
    }
    
    public void addAnotherItem(){
        stack++;
    }
    
    public void takeAwayItem(){
        stack--;
    }
    
    public void displayPreveiw(Graphics g, Component h, int x, int y){
        if (id == 1){
            ImageIcon example = new ImageIcon(pe.getImage().getImage().getScaledInstance(48, 48, 1));
            example.paintIcon(h, g, x, y);
            //g.drawString(""+pe.getName(), x+34, y+20);
            String time = ""+pe.getTimeRemaining();
            String amountName = "Amount";
            String amounterName = "Speed";
            double loss = pe.getStanimaLoss();
            if (pe.getTimeRemaining() > 10000f){
                time = "forever";
            }
            if (pe.getEffectID() == 1 && pe.getStanimaLoss() >= 0){
                amountName = "StanimaLoss";
            }
            if (pe.getEffectID() == 1 && pe.getStanimaLoss() < 0){
                amountName = "StanimaBoost";
                loss *= -1;
            }
            if (pe.getEffectID() == 1 && pe.getEffectAmount() >= 0){
                amounterName = "SpeedBoost";
            }
            if (pe.getEffectID() == 1 && pe.getEffectAmount() < 0){
                amounterName = "SpeedLost";
            }
            g.setFont(myFont);
            if (page == 1){
                g.drawString("Name: " + pe.getName(), x+50, y+10);
                g.drawString("EffectID: " + pe.getEffectID(), x+50, y+22);
                g.drawString("TimeLast: " + time, x+50, y+34);
                g.drawString(amounterName + ": " + pe.getEffectAmount(), x+50, y+46);
            }else if (page == 2){
                g.drawString(amountName + ": " + loss, x+50, y+10);
            }
        }else if (id == 2){
            ImageIcon example = new ImageIcon(pb.getImage().getImage().getScaledInstance(48, 48, 1));
            example.paintIcon(h, g, x, y);
            //g.drawString(""+pe.getName(), x+34, y+20);
            String time = ""+pb.getTimeRemaining();
            if (pb.getTimeRemaining() > 10000f){
                time = "forever";
            }
            g.setFont(myFont);
            if (page == 1){
                g.drawString("Name: " + pb.getname(), x+50, y+10);
                g.drawString("Dem.: " + ((int)pb.getDemensions().getWidth()) + "by" + ((int)pb.getDemensions().getHeight()), x+50, y+22);
                g.drawString("Visible: " + pb.isVisible(), x+50, y+34);
                g.drawString("TimeLast: " + time, x+50, y+46);
            }
        }else if (id == 3){
            
        }
        else if (id == 4){
            ImageIcon example = new ImageIcon(getImage().getImage().getScaledInstance(48, 48, 1));
            example.paintIcon(h, g, x, y);
            g.drawString("Name: " + getName(), x+50, y+10);
        }
    }
    
    public void turnPage(KeyEvent e){
        int key = e.getKeyCode();
        if (key == keyPage){
            if (page == 1){
                page = 2;
            }else{
                page = 1;
            }
        }
    }
    
    public void useItem(Player owner){
        if (id == 1){
            owner.addEffect(pe);
            stack--;
        }else if (id == 2){
            owner.addBoundary(pb);
            stack--;
        }else if (id == 3){
            stack--;
            owner.addBoundary(pb);
            owner.addEffect(pe);
        }
    }
    
    public boolean exsist(){
        return exsist;
    }
    
    public ImageIcon getImage(){
        return image;
    }
    
    public int getStack(){
        return stack;
    }
    
    public int getStackLimit(){
        return stacklimit;
    }
    
    public int getPageKey(){
        return keyPage;
    }
    
    public String getName(){
        return name;
    }
    
}
