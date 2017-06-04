/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package invincibe83.CaptureTheFlag;

import java.awt.Rectangle;
import javax.swing.ImageIcon;

/**
 *
 * @author Arthur
 */
public class PlayerBoundary {
    private boolean visible;
    private Rectangle demensions;
    private float time = 0f;
    private String name = "", imageLocation = "";
    private ImageIcon image, unknownimage = new ImageIcon(new ImageIcon("Images/BoundaryDefaultImage.png").getImage().getScaledInstance(16, 16, 1));
    private int x, y, width, height;
    
    public PlayerBoundary(String name, float time, Rectangle demension, boolean visible){
        this.name = name;
        this.time = time;
        demensions = demension;
        x = demensions.x;
        y = demensions.y;
        width = demensions.width;
        height = demensions.height;
        this.visible = visible;
        setImage("");
    }
    
    public PlayerBoundary(String name, float time, Rectangle demension, boolean visible, String imageLocation){
        this.name = name;
        this.time = time;
        demensions = demension;
        this.visible = visible;
        setImage(imageLocation);
    }
    
    public void setImage(String imageLocation){
        image = new ImageIcon(new ImageIcon(imageLocation).getImage().getScaledInstance(16, 16, 1));
        this.imageLocation = imageLocation;
        if (!imageLocation.contains("src/CaptureTheFlag/Image/")){
            image = unknownimage;
            this.imageLocation = "src/CaptureTheFlag/Image/BoundaryDefaultImage.png";
        }
    }
    
    public void update(){
        if (time < 10000f){
            time -= 1/60f;
        }
    }
    
    public boolean exsists(){
        if (time > 0){
            return true;
        }else{
            return false;
        }
    }
    
    public boolean isVisible(){
        return visible;
    }
    
    public Rectangle getDemensions(){
        return demensions;
    }
    
    public float getTime(){
        return time;
    }
    
    public String getname(){
        return name;
    }
    
    public ImageIcon getImage(){
        return image;
    }
    
    public float getTimeRemaining(){
        return time;
    }
    
    public String getImageLocation(){
        return imageLocation;
    }
    
    public int getX(){
        return x;
    }
    
    public int getY(){
        return y;
    }
    
    public int getWidth(){
        return width;
    }
    
    public int getHeight(){
        return height;
    }
    
    public void changeX(int x){
        this.x = x;
    }
    
    public void changeY(int y){
        this.y = y;
    }
    
}
