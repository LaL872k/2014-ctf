/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package invincibe83.CaptureTheFlag;

import java.awt.Color;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class Block {
    
    private Color blockcolor;
    private int id, x, y, width, pic = 1, countRef = 0, count = 12;
    private boolean movableThrough, animation = false;
    private PlayerEffect playerEffect;
    private ImageIcon image, unknownimage = new ImageIcon(new ImageIcon("Images/EffectImageUnknown.png").getImage().getScaledInstance(64, 64, 1));
    private String imageLocation = "";
    
    public Block(int id, int x, int y, int width){
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        getBlockStats(id);
    }
    
    public void update(){
        animation();
    }
    
    public void getBlockStats(int id){
        if (id == 0){
            blockcolor = Color.GREEN;
            this.movableThrough = true;
            this.playerEffect = null;
            animation = false;
            setImage("Images/Grass.png");
        }
        if (id == 1){
            this.blockcolor = Color.GRAY;
            this.movableThrough = false;
            this.playerEffect = null;
            animation = false;
            setImage("Images/Stone.png");
        }
    }
    
    public int getID(){
        return id;
    }
    
    public boolean getMovableThrough(){
        return movableThrough;
    }
    
    public ImageIcon getImage(){
        if (animation){
            if (pic == 1){
                return new ImageIcon(new ImageIcon("Images/" + imageLocation + "/Image1.png").getImage().getScaledInstance(32, 32, 1));
            }else if (pic == 2){
                return new ImageIcon(new ImageIcon("Images/" + imageLocation + "/Image2.png").getImage().getScaledInstance(32, 32, 1));
            }else if (pic == 3){
                return new ImageIcon(new ImageIcon("Images/" + imageLocation + "/Image3.png").getImage().getScaledInstance(32, 32, 1));
            }else if (pic == 4){
                return new ImageIcon(new ImageIcon("Images/" + imageLocation + "/Image4.png").getImage().getScaledInstance(32, 32, 1));
            }else if (pic == 5){
                return new ImageIcon(new ImageIcon("Images/" + imageLocation + "/Image5.png").getImage().getScaledInstance(32, 32, 1));
            }
        }
        return image;
    }
    
    public PlayerEffect getEffectOnPlayer(){
        return playerEffect;
    }
    
    public void animation(){
        count++;
        if (count >= countRef){
            count = 0;
            pic++;
            if (pic >= 6){
                pic = 1;
            }
        }
    }
    
    public void putOnEffect(Player owner){
        if (getDemensions().contains(owner.getLocation()) && getEffectOnPlayer() != null){
            for (int q = 0; q < owner.getEffects().length; q++){
                if (owner.getEffects()[q] != null){
                    if (owner.getEffects()[q].getName().equals(getEffectOnPlayer().getName())){
                        return;
                    }
                }
            }
            owner.addEffect(getEffectOnPlayer());
        }
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
    
    public void setImage(String imageLocation){
        image = new ImageIcon(new ImageIcon(imageLocation).getImage().getScaledInstance(64, 64, 1));
        if (!imageLocation.contains("Images/")){
            image = unknownimage;
        }
    }
    
    public Rectangle getDemensions(){
        return new Rectangle(getX(), getY(), getWidth(), getWidth());
    }
    
    public void changeBlock(int id, boolean movableThrough, boolean sneakHelp, Color blockColor, PlayerEffect playerEfffect){
        this.id = id;
        this.blockcolor = blockColor;
        this.movableThrough = movableThrough;
        this.playerEffect = playerEffect;
    }
    
    public Color getColor(){
        return blockcolor;
    }
    
}
