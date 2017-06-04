/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package invincibe83.CaptureTheFlag;

import java.awt.*;
import javax.swing.*;

/**
 *
 * @author Arthur
 */
public class PlayerEffect {

    private String name, imageLocation;
    private float time;
    private int id, amount, counter;
    private double stanimaloss;
    private ImageIcon image, unknownimage = new ImageIcon(new ImageIcon("Images/EffectImageUnknown.png").getImage().getScaledInstance(16, 16, 1));

    public PlayerEffect(String name, float time, int id, int amount, double stanimaloss, String imageLocation) {
        this.name = name;
        this.time = time;
        this.id = id;
        this.amount = amount;
        this.stanimaloss = stanimaloss;
        this.imageLocation = imageLocation;
        image = new ImageIcon(new ImageIcon(imageLocation).getImage().getScaledInstance(16, 16, 1));
        if (!imageLocation.contains("Images/")) {
            image = unknownimage;
        }
    }

    public void update(Player owner) {
        counter++;
        if (counter >= 60) {
            counter = 0;
            owner.increaseStanima(-stanimaloss);
        }
        if (time < 10000f) {
            time -= 1 / 60f;
        }
    }

    public String getName() {
        return name;
    }

    public float getTimeRemaining() {
        return time;
    }

    public int getEffectID() {
        return id;
    }

    public int getEffectAmount() {
        return amount;
    }

    public ImageIcon getImage() {
        return image;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void undoEffect(Player owner) {
        if (id == 1) {
            owner.speed(-amount);
        }
    }

    public void enhanceEffect(Player owner) {
        if (id == 1) {
            owner.speed(amount);
        }
    }

    public void deleteEffect() {
        time = 0;
    }

    public boolean exsists() {
        if (time > 0) {
            return true;
        } else {
            return false;
        }
    }

    public double getStanimaLoss() {
        return stanimaloss;
    }
}
