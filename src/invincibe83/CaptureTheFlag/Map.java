/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package invincibe83.CaptureTheFlag;

import java.awt.Rectangle;
import javax.swing.ImageIcon;

/**
 *
 * @author Arthur Lewis
 */
public class Map {
    private String name, owner, imageLocation;
    private int[] map;
    private ImageIcon image;
    private Rectangle jail1, jail2, flag1, flag2;
    
    public Map(String name, String owner, int[] map){
        this.name = name;
        this.owner = owner;
        this.map = map;
    }
    
    public Map(){
        name = "";
        owner = "Anomynous";
        map = new int[72*38];
    }
    
    public String getimageLocation(){
        return imageLocation;
    }
    
    public void setImageLocation(String location){
        imageLocation = location;
        image = new ImageIcon(new ImageIcon(imageLocation).getImage().getScaledInstance(450, 50, 1));
    }
    
    public ImageIcon getImage(){
        return image;
    }
    
    public int[] getInts(){
        return map;
    }
    
    public String getName(){
        return name;
    }
    
    public String getOwner(){
        return owner;
    }
    
    public void setInts(int[] map){
        this.map = map;
    }
    
    public void setCertainMap(int place, int newplaceholder){
        map[place] = newplaceholder;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void setOwner(String owner){
        this.owner = owner;
    }
    
    public void setJail1(Rectangle rect){
        jail1 = rect;
    }
    
    public Rectangle getJail1(){
        return jail1;
    }
    
    public void setJail2(Rectangle rect){
        jail2 = rect;
    }
    
    public Rectangle getJail2(){
        return jail2;
    }
    
    public void setFlag1(Rectangle rect){
        flag1 = rect;
    }
    
    public Rectangle getFlag1(){
        return flag1;
    }
    
    public void setFlag2(Rectangle rect){
        flag2 = rect;
    }
    
    public Rectangle getFlag2(){
        return flag2;
    }
}
