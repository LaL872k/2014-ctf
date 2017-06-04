/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package invincibe83.CaptureTheFlag;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.ImageIcon;

public class Player {
    private ImageIcon front1;
    private ImageIcon front2;
    private ImageIcon back1;
    private ImageIcon back2;
    private ImageIcon right1;
    private ImageIcon left1;
    private ImageIcon left2;
    private ImageIcon right2;
    private ImageIcon profile = new ImageIcon(new ImageIcon("Images/profileImage.png").getImage().getScaledInstance(35, 24, 1));
    
    private String sprintimagelocation = "Images/SprintImage.png";
    private String sneakimagelocation = "Images/SneakImage.png";
    private Point spawnpoint = new Point(0, 0), destination;
    private String name = "", skin;
    private boolean userControl, hasFlag, movingLeft, movingRight, movingUp, movingDown, moving, sneaking, sprinting, inJail, reposition, autoai = true, blocked;
    private int x, y, width = 32, height = 32, maxstanima = 0, speed = 0, directionfacing = 1, team, currentpes = 0, currentpbs = 0, currentpis, counter = 0, sneakDistance = 200,
            timesWentToJail = 0, selected = 1, freedPlayers = 0, taggedPlayers = 0, score = 0, pic = 1, timer = 0, sight = 400, xline;
    private int moveRightKey = KeyEvent.VK_RIGHT, moveLeftKey = KeyEvent.VK_LEFT, 
            moveUpKey = KeyEvent.VK_UP, moveDownKey = KeyEvent.VK_DOWN, sneakKey = KeyEvent.VK_SHIFT,
            sprintKey = KeyEvent.VK_SPACE, bot1 = KeyEvent.VK_E, bot2 = KeyEvent.VK_R, bot3 = KeyEvent.VK_T, bot4 = KeyEvent.VK_Y, bot5 = KeyEvent.VK_U;
    private int[] itemUse = {KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_5, KeyEvent.VK_6, KeyEvent.VK_7, 
        KeyEvent.VK_8, KeyEvent.VK_9, KeyEvent.VK_0, KeyEvent.VK_MINUS};
    private double stanima = 0D, stanimaRegain = 0D;
    private float speedCountH = 0F, speedCountV = 0F;
    private PlayerEffect[] pe = new PlayerEffect[9];
    private PlayerBoundary[] pb = new PlayerBoundary[9];
    private Rectangle jail;
    private PlayerItem[] pi = new PlayerItem[11];
    private int speedAnimation = 60-speed/100*10, ai = 1, aiType = 1, botSelected = 1;

    public Player(String name, Point spawnpoint, boolean usercontrolled, int speed, double stanima, double stanimaRegain, int team, int line, String skin){
        setSpawn(spawnpoint);
        setMaxStanima((int) stanima);
        setStanima(stanima);
        setStanimaRegain(stanimaRegain);
        setName(name);
        setSpeed(speed);
        x = getSpawn().x;
        y = getSpawn().y;
        setTeam(team);
        setUserControl(usercontrolled);
        xline = line;
        this.skin = skin;
        
        front1 = new ImageIcon(new ImageIcon("Images/" + skin + "/Front1.png").getImage().getScaledInstance(32, 32, 1));
        front2 = new ImageIcon(new ImageIcon("Images/" + skin + "/Front2.png").getImage().getScaledInstance(32, 32, 1));
        back1 = new ImageIcon(new ImageIcon("Images/" + skin + "/Back1.png").getImage().getScaledInstance(32, 32, 1));
        back2 = new ImageIcon(new ImageIcon("Images/" + skin + "/Back2.png").getImage().getScaledInstance(32, 32, 1));
        right1 = new ImageIcon(new ImageIcon("Images/" + skin + "/Right1.png").getImage().getScaledInstance(32, 32, 1));
        left1 = new ImageIcon(new ImageIcon("Images/" + skin + "/Left1.png").getImage().getScaledInstance(32, 32, 1));
        left2 = new ImageIcon(new ImageIcon("Images/" + skin + "/Left2.png").getImage().getScaledInstance(32, 32, 1));
        right2 = new ImageIcon(new ImageIcon("Images/" + skin + "/Right2.png").getImage().getScaledInstance(32, 32, 1));
    }
    
    public void update(World w, Game g){
        //System.out.println(stanima);
        if (!userControl){
            ai(g);
        }
        move(w);
        checkEffectWearOff();
        checkBoundaryWearOff();
        checkInventoryItemsGone();
        for (int q = 0; q < currentpes; q++){
            pe[q].update(this);
            checkStanima();
        }
        for (int q = 0; q < currentpbs; q++){
            pb[q].update();
        }
        for (int q = 0; q < currentpis; q++){
            pi[q].update();
        }
        animation();
        updateMoving();
        counter++;
        if (counter >= 60){
            counter-=60;
            onceASecond();
        }
    }
    
    public void setUserControl(boolean newUserControl){
        userControl = newUserControl;
    }
    
    public void ai(Game g){
        autoAICheck(g);
        if (ai == 1){
            aiDefend(g);
        }else{
            aiAttack(g);
        }
    }
    
    public void autoAICheck(Game g){
        if (autoai){
            //save your teammates from jail
            boolean found = false;
            for (int t = 0; t < g.team1.length; t++){
                if (g.team1[t].getAIType() == 2 && team == 1){
                    found = true;
                    break;
                }if (g.team2[t].getAIType() == 2 && team == 2){
                    found = true;
                    break;
                }
            }
            if (!found){
                for (int q = 0; q < g.team1.length; q++){
                    if (g.team1[q].inJail() && team == 1){
                        setMainai(2);
                        setAIType(2);
                        break;
                    }if (g.team2[q].inJail() && team == 2){
                        setMainai(2);
                        setAIType(2);
                        break;
                    }
                }
            }
            //finish the game and get the flag
            int count = 0;
            for (int t = 0; t < g.team1.length; t++){
                if (g.team1[t].getViewableRect().intersects(g.flag2) && team == 1){
                    for (int q = 0; q < g.team1.length; q++){
                        if (g.team1[q].getAI() == 2){
                            count++;
                        }
                    }
                    for (int q = 0; q < g.team1.length; q++){
                        if (count >= 3){
                            break;
                        }
                        if (g.team1[q].getAI() == 1){
                            count++;
                            g.team1[q].setMainai(2);
                        }
                    }
                    break;
                }
                if (g.team2[t].getViewableRect().intersects(g.flag1) && team == 2){
                    for (int q = 0; q < g.team2.length; q++){
                        if (g.team2[q].getAI() == 2){
                            count++;
                        }
                    }
                    for (int q = 0; q < g.team2.length; q++){
                        if (count >= 2){
                            break;
                        }
                        if (g.team2[q].getAI() == 1){
                            count++;
                            g.team2[q].setMainai(2);
                        }
                    }
                    break;
                }
            }
            //to many people on offense
            int jail = 0;
            int attack = 0;
            for (int q = 0; q < g.team1.length; q++){
                if (g.team1[q].inJail() && team == 1){
                    jail++;
                }if (g.team1[q].getAI() == 2 && team == 1){
                    attack++;
                }
                if (g.team2[q].inJail() && team == 2){
                    jail++;
                }if (g.team2[q].getAI() == 2 && team == 2){
                    attack++;
                }
            }
            if (jail == 0 && attack >= 3){
                setMainai(1);
            }
            //to many people defending
            if (((g.flag1onground && team == 1) || (g.flag2onground && team == 2)) && (attack < 2)){
                setMainai(2);
            }
            //has the flag and get out of there
            if (hasFlag()){
                if (team == 1){
                    destination = new Point(xline-width-1, y);
                }else{
                    destination = new Point(xline+1, y);
                }
            }
        }
    }
    
    public void setMainai(int ai){
        this.ai = ai;
    }
    
    public void aiDefend(Game g){
        if (!aiChase(g)){ //first check that know one is around
            if (!moving){ //dont find a new location every time method is called
                Random rand = new Random();
                if (aiType == 1){ //random
                    if (getTeam() == 1){
                        destination = new Point(rand.nextInt(xline-width), rand.nextInt(64*38-height));
                    }else{
                        destination = new Point(xline+rand.nextInt(xline-width), rand.nextInt(64*38-height));
                    }
                }else if (aiType == 2){ //protect jail
                    if (getTeam() == 1){
                        destination = new Point(rand.nextInt(g.jail1.x-500+500*2+200), rand.nextInt(g.jail1.y-500+500*2+200));
                    }else{
                        destination = new Point(xline+rand.nextInt(g.jail2.x-500+500*2+200), rand.nextInt(g.jail2.y-500+500*2+200));
                    }
                }else if (aiType == 3){ // protect flag
                    if (getTeam() == 1){
                        destination = new Point(rand.nextInt(g.flag1.x-500+500*2+200), rand.nextInt(g.flag1.y-500+500*2+200));
                    }else{
                        destination = new Point(xline+rand.nextInt(g.flag2.x-500+500*2+200), rand.nextInt(g.flag2.y-500+500*2+200));
                    }
                }
            }
        }
        blockai(g);
    }
    
    public void aiAttack(Game g){
        if (!aiRun(g)){ //make sure no one is around
            if (!moving){ //only do it when stoped(
                Random rand = new Random();
                if (aiType == 1){ //search
                    if (getTeam() == 2){
                        destination = new Point(rand.nextInt(xline-width), rand.nextInt(64*38-height));
                    }else{
                        destination = new Point(xline+rand.nextInt(xline-width), rand.nextInt(64*38-height));
                    }
                }else if (aiType == 2){ //save from jail
                    for (int q = 0; q < g.team1.length; q++){
                        if (g.team1[q].inJail()){
                            destination = g.team1[q].getLocation();
                        }else{
                            aiType = 1;
                        }
                    }
                }else if(aiType == 3){ //get flag
                    for (int q = 0; q < g.team1.length; q++){
                        if (g.team1[q].getViewableRect().intersects(g.flag2) && getTeam() == 1){
                            destination = new Point(g.flag1.x, g.flag1.y);
                        }
                        if (g.team2[q].getViewableRect().intersects(g.flag1) && getTeam() == 2){
                            destination = new Point(g.flag2.x, g.flag2.y);
                        }
                    }
                    aiType = 1;
                }
            }
        }
        blockai(g);
    }
    
    public void aiRightCheck(Game g){
        try{
        if (destination.x > x && destination.y == y){
            int count = 0;
            boolean goOn = false;
            for (int q = 0; q < g.world.getMap().length; q++){
                if (!g.world.getMap()[q].getMovableThrough() && g.world.getMap()[q].getDemensions().intersects(new Rectangle(x+1+width, y, 1, height))){
                    count = q;
                    goOn = true;
                    break;
                }
            }
            if (goOn){
                Random rand = new Random();
                int choice = rand.nextInt(2);
                boolean down = false;
                if (choice == 0){
                    down = true;
                }
                if (down){
                    for (int q = 0; q < g.world.map.length/72-((count/72)+1); q++){
                        if (g.world.getMap()[q*72+count].getMovableThrough()){
                            destination = new Point(x, g.world.getMap()[q*72+count].getY());
                            break;
                        }
                    }
                }else{
                    for (int q = 0; q < ((count/72)+1); q--){
                        if (g.world.getMap()[q*72+count].getMovableThrough()){
                            destination = new Point(x, g.world.getMap()[q*72+count].getY());
                            break;
                        }
                    }
                }
            }
        }
        }catch(Exception e){e.printStackTrace();}
    }
    
    public void aiLeftCheck(Game g){
        try{
        if (destination.x < x && destination.y == y){
            int count = 0;
            boolean goOn = false;
            for (int q = 0; q < g.world.getMap().length; q++){
                if (!g.world.getMap()[q].getMovableThrough() && g.world.getMap()[q].getDemensions().intersects(new Rectangle(x-1, y, 1, height))){
                    count = q;
                    goOn = true;
                    break;
                }
            }
            if (goOn){
                Random rand = new Random();
                int choice = rand.nextInt(2);
                boolean down = false;
                if (choice == 0){
                    down = true;
                }
                if (down){
                    for (int q = 0; q < g.world.map.length/72-((count/72)+1); q++){
                        if (g.world.getMap()[q*72+count].getMovableThrough()){
                            destination = new Point(x, g.world.getMap()[q*72+count].getY());
                            break;
                        }
                    }
                }else{
                    for (int q = 0; q < ((count/72)+1); q--){
                        if (g.world.getMap()[q*72+count].getMovableThrough()){
                            destination = new Point(x, g.world.getMap()[q*72+count].getY());
                            break;
                        }
                    }
                }
            }
        }
        }catch(Exception e){e.printStackTrace();}
    }
    
    public void aiUpCheck(Game g){
        try{
        if (destination.y < y && destination.x == x){
            int count = 0;
            boolean goOn = false;
            for (int q = 0; q < g.world.getMap().length; q++){
                if (!g.world.getMap()[q].getMovableThrough() && g.world.getMap()[q].getDemensions().intersects(new Rectangle(x, y-1, width, 1))){
                    count = q;
                    goOn = true;
                    break;
                }
            }
            if (goOn){
                Random rand = new Random();
                int choice = rand.nextInt(2);
                boolean down = false;
                if (choice == 0){
                    down = true;
                }
                if (down){
                    for (int q = 0; q < g.world.map.length/38-((count/38)+1); q++){
                        if (g.world.getMap()[q*38+count].getMovableThrough()){
                            destination = new Point(g.world.getMap()[q*38+count].getX(), y);
                            break;
                        }
                    }
                }else{
                    for (int q = 0; q < ((count/38)+1); q--){
                        if (g.world.getMap()[q*38+count].getMovableThrough()){
                            destination = new Point(g.world.getMap()[q*38+count].getX(), y);
                            break;
                        }
                    }
                }
            }
        }
        }catch(Exception e){e.printStackTrace();}
    }
    
    public void aiDownCheck(Game g){
        try{
        if (destination.y > y && destination.x == x){
            int count = 0;
            boolean goOn = false;
            for (int q = 0; q < g.world.getMap().length; q++){
                if (!g.world.getMap()[q].getMovableThrough() && g.world.getMap()[q].getDemensions().intersects(new Rectangle(x, y+height+1, width, 1))){
                    count = q;
                    goOn = true;
                    break;
                }
            }
            if (goOn){
                Random rand = new Random();
                int choice = rand.nextInt(2);
                boolean down = false;
                if (choice == 0){
                    down = true;
                }
                if (down){
                    for (int q = 0; q < g.world.map.length/38-((count/38)+1); q++){
                        if (g.world.getMap()[q*38+count].getMovableThrough()){
                            destination = new Point(g.world.getMap()[q*38+count].getX(), y);
                            break;
                        }
                    }
                }else{
                    for (int q = 0; q < ((count/38)+1); q--){
                        if (g.world.getMap()[q*38+count].getMovableThrough()){
                            destination = new Point(g.world.getMap()[q*38+count].getX(), y);
                            break;
                        }
                    }
            }
            }
        }
        }catch(Exception e){e.printStackTrace();}
    }
    
    public boolean cornerAIMoving(Game g){
        boolean right = false;
        boolean left = false;
        boolean up = false;
        boolean down = false;
        int count = 0;
        for (int q = 0; q < g.world.getMap().length; q++){
            if (!g.world.getMap()[q].getMovableThrough() && g.world.getMap()[q].getDemensions().intersects(new Rectangle(x, y+height+1, width, 1))){
                down = true;
            }
            if (!g.world.getMap()[q].getMovableThrough() && g.world.getMap()[q].getDemensions().intersects(new Rectangle(x, y-1, width, 1))){
                up = true;
            }
            if (!g.world.getMap()[q].getMovableThrough() && g.world.getMap()[q].getDemensions().intersects(new Rectangle(x-1, y, 1, height))){
                left = true;
            }
            if (!g.world.getMap()[q].getMovableThrough() && g.world.getMap()[q].getDemensions().intersects(new Rectangle(x+1+width, y, 1, height))){
                right = true;
            }
        }
        
        Random rand = new Random();
        if (right){
            count++;
        }if (left){
            count++;
        }if (up){
            count++;
        }if (down){
            count++;
        }
        
        if (count >= 2){
            stop();
            if (ai == 1){
                if (getTeam() == 1){
                    destination = new Point(rand.nextInt(xline-width), rand.nextInt(64*38-height));
                }else{
                    destination = new Point(xline+rand.nextInt(xline-width), rand.nextInt(64*38-height));
                }
            }else{
                if (getTeam() == 2){
                    destination = new Point(rand.nextInt(xline-width), rand.nextInt(64*38-height));
                }else{
                    destination = new Point(xline+rand.nextInt(xline-width), rand.nextInt(64*38-height));
                }
            }
            return true;
        }
        return false;
    }
    
    public void blockai(Game g){
        if (!cornerAIMoving(g)){
            aiDownCheck(g);
            aiUpCheck(g);
            aiRightCheck(g);
            aiLeftCheck(g);
        }
        
        if (destination.x == x || destination.y == y){
            stop();
        }
        
        if (destination.x > x){
            moveRight();
            stopMovingLeft();
        }else if (destination.x < x){
            moveLeft();
            stopMovingRight();
        }if (destination.y > y){
            moveDown();
            stopMovingUp();
        }else if (destination.y < y){
            moveUp();
            stopMovingDown();
        }
    }
    
    public boolean aiChase(Game g){
        for (int q = 0; q < g.team1.length; q++){
            if (getViewableRect().intersects(g.team1[q].getDimensions()) && getTeam() == 2  && !g.team1[q].inJail() && g.team1[q].getCurrentSide() == 2){
                destination = new Point(g.team1[q].getLocation());
                turnSprintOn();
                return true;
            }
            else if (getViewableRect().intersects(g.team2[q].getDimensions()) && getTeam() == 1  && !g.team2[q].inJail()  && g.team2[q].getCurrentSide() == 1){
                destination = new Point(g.team2[q].getLocation());
                turnSprintOn();
                return true;
            }
        }
        turnSprintOff();
        return false;
    }
    
    public boolean aiRun(Game g){
        for (int q = 0; q < g.team1.length; q++){
            if (getCurrentSide() == getTeam() && reposition){
                Random rand = new Random();
                destination = new Point(x, rand.nextInt(38*64-height));
                reposition = false;
                return true;
            }else if (getViewableRect().intersects(g.team1[q].getDimensions()) && getTeam() == 2 && getCurrentSide() != getTeam()){
                if (g.team1[q].getDirectionFacing() == 3){
                    destination = new Point(xline, y+speed);
                }else if (g.team1[q].getDirectionFacing() == 1){
                    destination = new Point(xline, y-speed);
                }else if (g.team1[q].getDirectionFacing() == 2 && y < 38*64/2){
                    destination = new Point(x-speed, y+speed);
                }else if (g.team1[q].getDirectionFacing() == 2 && y > 38*64/2){
                    destination = new Point(x-speed, y-speed);
                }else{
                    destination = new Point(xline, y);
                }
                turnSprintOn();
                reposition = true;
                return true;
            }else if (getViewableRect().intersects(g.team2[q].getDimensions()) && getTeam() == 1 && getCurrentSide() != getTeam()){
                if (g.team2[q].getDirectionFacing() == 3){
                    destination = new Point(xline-width-1, y+speed);
                }else if (g.team2[q].getDirectionFacing() == 1){
                    destination = new Point(xline-width-1, y-speed);
                }else if (g.team2[q].getDirectionFacing() == 4 && y < 38*64/2){
                    destination = new Point(x-speed-width-1, y+speed);
                }else if (g.team2[q].getDirectionFacing() == 4 && y > 38*64/2){
                    destination = new Point(x-speed-width-1, y-speed);
                }else{
                    destination = new Point(xline-width-1, y);
                }
                turnSprintOn();
                reposition = true;
                return true;
            }
        }
        turnSprintOff();
        return false;
    }
    
    public void tp(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    public void turnSneakOn(){
        if (!sneaking){
            speedEffect("Sneak", 10000f, 1, -80, 0, sneakimagelocation);
            sneaking = true;
        }
    }
    
    public void turnSneakOff(){
        if (sneaking){
            int i = currentpes;
            removeEffect("sneak");
            int t = currentpes;
            if (i == t){
                //error
            }
            sneaking = false;
        }
    }
    
    public void checkEffectWearOff(){
        for (int q = 0; q < currentpes; q++){
            if (!pe[q].exsists()){
                if (currentpes == 1){
                    pe[0].undoEffect(this);
                    clearEffects();
                    return;
                }
                removeEffectFromArray(q);
            }
        }
    }
    
    public void clearEffects(){
        for (int q = 0; q < pe.length; q++){
            pe[q] = null;
        }
        currentpes = 0;
    }
    
    public void clearBoundarys(){
        for (int q = 0; q < pb.length; q++){
            pb[q] = null;
        }
        currentpbs = 0;
    }
    
    public void checkBoundaryWearOff(){
        for (int q = 0; q < currentpbs; q++){
            if (!pb[q].exsists()){
                removeBoundaryFromArray(q);
                if (currentpbs == 1){
                    clearBoundarys();
                }
            }
        }
    }
    
    public void checkStanima(){
        for (int q = 0; q < currentpes; q++){
            if (stanima <= pe[q].getStanimaLoss()){
                stanima = 1;
                removeAllStanimaNegativeEffects();
                organizeEffects();
                break;
            }
        }
    }
    
    public void checkInventoryItemsGone(){
        for (int q = 0; q < currentpis; q++){
            if (!pi[q].exsist()){
                removeItemFromArray(q);
            }
        }
    }
    
    public void removeItemFromArray(int numinarray){
        if (pi[numinarray] != null){
            pi[numinarray] = null;
            if (numinarray < pi.length-1){
                for (int q = numinarray+1; q < currentpis+1; q++){
                    pi[q-1] = pi[q];
                }
            }
            currentpis--;
        }
    }
    
    public void organizeEffects(){
        int count = 0;
        for (int q = 0; q < pe.length; q++){
            if (pe[q] != null){
                if (count != q){
                    pe[count] = pe[q];
                    count++;
                    pe[q] = null;
                }
            }
        }
    }
    
    public void removeEffectFromArray(int numinarray){
        try{
            if (pe[numinarray] != null){
                pe[numinarray].undoEffect(this);
                pe[numinarray] = null;
                if (numinarray < pe.length-1){
                    for (int q = numinarray+1; q < currentpes+1; q++){
                        if (q == 9){
                            break;
                        }
                        pe[q-1] = pe[q];
                    }
                }
                currentpes--;
            }
        }catch(Exception e){e.printStackTrace();}
    }
    
    public void removeAllStanimaNegativeEffects(){
        int count = currentpes;
        for (int q = 0; q < count; q++){
            if (pe[q].getStanimaLoss() > 0){
                pe[q].undoEffect(this);
                pe[q] = null;
                currentpes--;
            }
        }
    }
    
    public void removeEffect(String name){
        for (int q = 0; q < currentpes; q++){
            if (pe[q].getName().toLowerCase().equals(name.toLowerCase())){
                removeEffectFromArray(q);
                break;
            }
        }
    }
    
    
    public void removeItem(String name){
        for (int q = 0; q < currentpis; q++){
            if (pi[q].getName().toLowerCase().equals(name.toLowerCase())){
                removeItemFromArray(q);
                break;
            }
        }
    }
    public void speedEffect(String name, float time, int id, int amount, double stanimaloss, String imageLocation){
        PlayerEffect peffect = new PlayerEffect(name, time, id, amount, stanimaloss, imageLocation);
        addEffect(peffect);
    }
    
    public void speed(int speedchange){
        speed += speedchange;
    }
    
    public void onceASecond(){
        stanimaRegain();
    }
    
    public void stanimaRegain(){
        if (stanima < getMaxStanima()){
            if (isMoving()){
                increaseStanima(stanimaRegain/2);
            }else{
                increaseStanima(stanimaRegain);
            }
        }else{
            setStanima(getMaxStanima());
        }
    }
    
    public void moveUp(){
        directionfacing = 1;
        movingUp = true;
        if (movingRight){
            speedCountV = 0F;
        }
        if (movingLeft){
            speedCountV = 0F;
        }
        movingDown = false;
    }
    
    public void stopMovingUp(){
        resetTimer(moveUpKey);
        movingUp = false;
        speedCountV = 0F;
        setMovement();
    }
    
    public void moveDown(){
        directionfacing = 3;
        movingDown = true;
        if (movingRight){
            speedCountV = 0F;
        }
        if (movingLeft){
            speedCountV = 0F;
        }
        movingUp = false;
    }
    
    public void stopMovingDown(){
        resetTimer(moveDownKey);
        movingDown = false;
        speedCountV = 0F;
        setMovement();
    }
    
    public void moveRight(){
        directionfacing = 4;
        movingRight = true;
        if (movingUp){
            speedCountH = 0F;
        }
        if (movingDown){
            speedCountH = 0F;
        }
        movingLeft = false;
    }
    
    public void stopMovingRight(){
        resetTimer(moveRightKey);
        movingRight = false;
        speedCountH = 0F;
        setMovement();
    }
    
    public void moveLeft(){
        directionfacing = 2;
        movingLeft = true;
        if (movingUp){
            speedCountH = 0F;
        }
        if (movingDown){
            speedCountH = 0F;
        }
        movingRight = false;
    }
    
    public void stopMovingLeft(){
        resetTimer(moveLeftKey);
        movingLeft = false;
        speedCountH = 0F;
        setMovement();
    }
    
    public boolean collisionDetection(int x, int y, int width, int height){
        Rectangle rect = new Rectangle(x, y, width, height);
        return boundaryCollisionDetection(rect);
    }
    
    public boolean collisionDetection(Rectangle rect){
        return boundaryCollisionDetection(rect);
    }
    
    public boolean boundaryCollisionDetection(Rectangle rect){
        Rectangle[] rects = getBoundaryDimensions();
        for (int q = 0; q < currentpbs; q++){
            if (rect.intersects(new Rectangle(rects[q].x, rects[q].y, 1, rects[q].height))){
                return true;
            }
            if (rect.intersects(new Rectangle(rects[q].x, rects[q].y + rects[q].height, rects[q].width, 1))){
                return true;
            }
            if (rect.intersects(new Rectangle(rects[q].x + rects[q].width, rects[q].y, 1, rects[q].height))){
                return true;
            }
            if (rect.intersects(new Rectangle(rects[q].x, rects[q].y, rects[q].width, 1))){
                return true;
            }
        }
        return false;
    }
    
    public void move(World w){
        //System.out.println(currentpbs);
        blocked = false;
        if(movingRight||movingLeft){
            float speedIncrease = (float) speed / 60;
            speedCountH+=speedIncrease;
            while(true){
                if (speedCountH < 1){
                    break;
                }
                if ((collisionDetection(new Rectangle(x, y, width+1, height)) && movingRight) || (collisionDetection(new Rectangle(x-1, y, width+1, height)) && movingLeft) || (w.collisionDetection(new Rectangle(x, y, width+1, height)) && movingRight) || (w.collisionDetection(new Rectangle(x-1, y, width+1, height)) && movingLeft)){
                    speedCountH = 0F;
                    blocked = true;
                    break;
                }
                if (movingRight){
                    x++;
                }else if(movingLeft){
                    x--;
                }
                speedCountH--;
            }
        }
        if(movingUp||movingDown){
            float speedIncrease = (float) speed / 60;
            speedCountV+=speedIncrease;
            while(true){
                if (speedCountV < 1){
                    break;
                }
                if ((collisionDetection(new Rectangle(x, y+1, width, height+1)) && movingDown) || (collisionDetection(new Rectangle(x, y-1, width, height+1)) && movingUp) || (w.collisionDetection(new Rectangle(x, y+1, width, height+1)) && movingDown) || (w.collisionDetection(new Rectangle(x, y-1, width, height+1)) && movingUp)){
                    speedCountV = 0F;
                    blocked = true;
                    break;
                }
                if (movingDown){
                    y++;
                }else if(movingUp){
                    y--;
                }
                speedCountV--;
            }
        }
    }
    
    public boolean blocked(){
        return blocked;
    }
    
    public void stop(){
        movingRight = false;
        speedCountH = 0F;
        movingLeft = false;
        speedCountH = 0F;
        movingUp = false;
        speedCountV = 0F;
        movingDown = false;
        speedCountV = 0F;
        setMovement();
    }
    
    public void addBoundary(String name, Float time, Rectangle demension, boolean visible){
        PlayerBoundary newpb = new PlayerBoundary(name, time, demension, visible);
        for (int q = 0; q < pb.length; q++){
            if (pb[q] == null){
                pb[q] = newpb;
                currentpbs++;
                break;
            }
        }
    }
    
    public void addBoundary(PlayerBoundary newpboundary){
        PlayerBoundary newpb = new PlayerBoundary(newpboundary.getname(), newpboundary.getTime(), newpboundary.getDemensions(), newpboundary.isVisible());
        for (int q = 0; q < pb.length; q++){
            if (pb[q] == null){
                pb[q] = newpb;
                currentpbs++;
                break;
            }
        }
    }
    
    public void increaseSightDistance(int sightchange){
        sight += sightchange;
    }
    
    public void setSightDistance(int sight){
        this.sight = sight;
    }
    
    public void setSpawn(Point newSpawn){
        spawnpoint = newSpawn;
    }
    
    public void setSpawn(int x, int y){
        spawnpoint = new Point(x, y);
    }
    
    public void respawn(){
        tp(getSpawn().x, getSpawn().y);
    }
    
    public void gotoJail(Rectangle jail){
        gotoJail();
        timesWentToJail++;
        tp(jail.x+1, jail.y+1);
        addBoundary("Jail", 10000f, jail, true);
        this.jail = jail;
    }
    
    public void freeFromJail(){
        if (inJail()){
            removeBoundary("Jail");
            respawn();
            releaseFromJail();
        }
    }
    
    public void removeBoundaryFromArray(int numinarray){
        if (pb[numinarray] != null){
            pb[numinarray] = null;
            if (numinarray < pb.length-1){
                for (int q = numinarray+1; q < currentpbs+1; q++){
                    if (q == 9){
                        break;
                    }
                    pb[q-1] = pb[q];
                }
            }
            currentpbs--;
        }
    }
    
    public void removeBoundary(String name){
        for (int q = 0; q < currentpbs; q++){
            if (pb[q].getname().toLowerCase().equals(name.toLowerCase())){
                //System.out.println(pb[q].getname());
                removeBoundaryFromArray(q);
                break;
            }
        }
    }
    
    public void setName(String newname){
        name = newname;
    }
    
    public void turnSprintOn(){
        if (!sprinting){
            speedEffect("Sprint", 10000f, 1, 100, 2, sprintimagelocation);
            sprinting = true;
        }
    }
        
    public void turnSprintOff(){
        if (sprinting){
            int i = currentpes;
            removeEffect("Sprint");
            int t = currentpes;
            if (i == t){
                //error
            }
            sprinting = false;
            
        }
    }
    
    public void gotoJail(){
        inJail = true;
    }
    
    public void releaseFromJail(){
        inJail = false;
    }
    
    public void setTeam(int newTeam){
        team = newTeam;
    }
    
    public void setSpeed(int newspeed){
        speed = newspeed;
    }
    
    public void increaseSpeed(int increasedspeed){
        speed = speed + increasedspeed;
    }
    
    public void increaseStanima(double stanimachange){
        if (maxstanima - stanima < stanimachange){
            stanima = maxstanima;
            return;
        }
        stanima = stanima + stanimachange;
    }
    
    public void setStanima(double newstanima){
        stanima = newstanima;
    }
    
    public void setMaxStanima(int newmaxstanima){
        maxstanima = newmaxstanima;
    }
    
    public void setStanimaRegain(double newstanimaregain){
        stanimaRegain = newstanimaregain;
    }
    
    public int getCurrentPlayerBoundaryLength(){
        return currentpbs;
    }
    
    public int getCurrentPlayerEffectLength(){
        return currentpes;
    }
    
    public void addEffect(PlayerEffect peffect){
        PlayerEffect effect = new PlayerEffect(peffect.getName(), peffect.getTimeRemaining(), peffect.getEffectID(), peffect.getEffectAmount(), peffect.getStanimaLoss(), peffect.getImageLocation());
        for (int q = 0; q < pe.length; q++){
            if (pe[q] == null && stanima > peffect.getStanimaLoss()){
                pe[q] = effect;
                effect.enhanceEffect(this);
                currentpes++;
                break;
            }
        }
    }
    
    public void addEffect(String name, float time, int id, int amount, double stanimaloss, String imageLocation){
        PlayerEffect effect = new PlayerEffect(name, time, id, amount, stanimaloss, imageLocation);
        for (int q = 0; q < pe.length; q++){
            if (pe[q] == null){
                pe[q] = effect;
                effect.enhanceEffect(this);
                currentpes++;
                break;
            }
        }
    }
    
    public void addItem(PlayerItem item){
        for (int q = 0; q < pi.length; q++){
            if (pi[q] == null){
                pi[q] = item;
                currentpis++;
                break;
            }
        }
    }
    
    public void updateMoving(){
        if (movingLeft || movingRight || movingUp || movingDown){
            moving = true;
        }else{
            moving = false;
        }
    }
    
    public void setAIType(int ai){
        aiType = ai;
    }
    
    public void scored(){
        score++;
    }
    
    public void freedaplayer(){
        freedPlayers++;
    }
    
    public void taggedaplayer(){
        taggedPlayers++;
    }
    
    public boolean inJail(){
        return inJail;
    }
    
    public int getX(){
        return x;
    }
    
    public int getY(){
        return y;
    }
    
    public Point getLocation(){
        return new Point(getX(), getY());
    }
    
    public Rectangle getDimensions(){
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }
    
    public boolean isSneaking(){
        return sneaking;
    }
    
    public boolean hasFlag(){
        return hasFlag;
    }
    
    public void setFlag(boolean truth){
        hasFlag = truth;
    }
    
    public boolean isMoving(){
        return moving;
    }
    
    public int getSpeed(){
        return speed;
    }
    
    public int getTeam(){
        return team;
    }
    
    public int getCurrentSide(){
        if (x <= xline){
            return 1;
        }
        return 2;
    }
    
    public int getSightDistance(){
        return sight;
    }
    
    public int getTimesWentToJail(){
        return timesWentToJail;
    }
    
    public int getTaggedPlayers(){
        return taggedPlayers;
    }
    
    public int getFreedPlayers(){
        return freedPlayers;
    }
    
    public int getScore(){
        return score;
    }
    
    public int getCurrentPlayerInventoryLength(){
        return currentpis;
    }
    
    public Rectangle[] getBoundaryDimensions(){
        Rectangle[] rect = new Rectangle[currentpbs];
        for (int q = 0; q < currentpbs; q++){
            rect[q] = pb[q].getDemensions();
        }
        return rect;
    }
    
    public Point getSpawn(){
        return spawnpoint;
    }
    
    public int getWidth(){
        return width;
    }
    
    public int getHeight(){
        return height;
    }
    
    public Rectangle[] getVisibleBoundaryDimensions(){
        int numcounter = 0;
        for (int q = 0; q < currentpbs; q++){
            if (pb[q].isVisible()){
                numcounter++;
            }
        }
        Rectangle[] rect = new Rectangle[numcounter];
        int count = 0;
        for (int q = 0; q < currentpbs; q++){
            if (pb[q].isVisible()){
                rect[count] = pb[q].getDemensions();
                count++;
            }
        }
        return rect;
    }
    
    public String getName(){
        return name;
    }
    
    public ImageIcon getImage(){
        if (directionfacing == 1 && pic == 1){
            return back1;
        }else if (directionfacing == 1 && pic == 2){
            return back2;
        }else if (directionfacing == 3 && pic == 2){
            return front2;
        }else if (directionfacing == 3 && pic == 1){
            return front1;
        }else if (directionfacing == 2 && pic == 1){
            return left1;
        }else if (directionfacing == 4 && pic == 1){
            return right1;
        }else if (directionfacing == 2 && pic == 2){
            return left2;
        }else if (directionfacing == 4 && pic == 2){
            return right2;
        }
        return back1;
    }
    
    public ImageIcon getProfileImage(){
        return profile;
    }
    
    public PlayerBoundary[] getBoundarys(){
        return pb;
    }
    
    public double getStanima(){
        return stanima;
    }
    
    public int getMaxStanima(){
        return maxstanima;
    }
    
    public double getStanimaRegain(){
        return stanimaRegain;
    }
    
    public PlayerEffect[] getEffects(){
        return pe;
    }
    
    public PlayerItem[] getInventory(){
        return pi;
    }
    
    public int getAIType(){
        return aiType;
    }
    
    public int getSelectedItem(){
        return selected;
    }
    
    public Rectangle getViewableRect(){
        return new Rectangle(x-sight, y-sight, sight*2+width, sight*2+height);
    }
    
    public boolean isUserControled(){
        return userControl;
    }
    
    public int getDirectionFacing(){
        return directionfacing;
    }
    
    public int getAI(){
        return ai;
    }
    
    public int doDrawLogic(int x, int px){
        return x+(250-getWidth()/2-px);
    }
    
    public void render(Graphics g){
        //drawPlayer(g, px, py);
        drawBoundarys(g);
    }
    
    public void drawPlayer(Graphics g, Component h,  int x, int y, Player p){
        int refrence = 0;
        Point ref = p.getLocation();
        ref.x += p.getWidth()/2;
        ref.y += p.getHeight()/2;
        int x2 = 0, y2 = 0;
        if (ref.x > getX()){
            x2 = ref.x - getX();
        }else{
            x2 = getX() - ref.x;
        }
        if (ref.y > getY()){
            y2 = ref.y - getY();
        }else{
            y2 = getY() - ref.y;
        }
        if (x2 > y2){
            refrence = x2;
        }else{
            refrence = y2;
        }

        if ((refrence < getSneakDistance() && sneaking) || p.getTeam() == team || !sneaking){
            if (directionfacing == 1 && pic == 1){
                back1.paintIcon(h, g, doDrawLogic(getX(), x), doDrawLogic(getY(), y));
            }else if (directionfacing == 1 && pic == 2){
                back2.paintIcon(h, g, doDrawLogic(getX(), x), doDrawLogic(getY(), y));
            }else if (directionfacing == 3 && pic == 2){
                front2.paintIcon(h, g, doDrawLogic(getX(), x), doDrawLogic(getY(), y));
            }else if (directionfacing == 3 && pic == 1){
                front1.paintIcon(h, g, doDrawLogic(getX(), x), doDrawLogic(getY(), y));
            }else if (directionfacing == 2 && pic == 1){
                left1.paintIcon(h, g, doDrawLogic(getX(), x), doDrawLogic(getY(), y));
            }else if (directionfacing == 4 && pic == 1){
                right1.paintIcon(h, g, doDrawLogic(getX(), x), doDrawLogic(getY(), y));
            }else if (directionfacing == 2 && pic == 2){
                left2.paintIcon(h, g, doDrawLogic(getX(), x), doDrawLogic(getY(), y));
            }else if (directionfacing == 4 && pic == 2){
                right2.paintIcon(h, g, doDrawLogic(getX(), x), doDrawLogic(getY(), y));
            }
            g.drawRect(doDrawLogic(getX(), x), doDrawLogic(getY(), y), getWidth(), getHeight());
            g.drawString(getName(), doDrawLogic(getX(), p.getX()), doDrawLogic(getY(), p.getY()));
        }
    }
    
    public boolean viewable(Player p){
        Rectangle rect = new Rectangle(x-getSneakDistance(), y-getSneakDistance(), getSneakDistance()*2+width, getSneakDistance()*2+height);
        
        if ((rect.contains(p.getLocation()) && p.isSneaking()) || !p.isSneaking()){
            return true;
        }
        return false;
    }
    
    public int getSneakDistance(){
        return sneakDistance;
    }
    
    public void animation(){
        speedAnimation = 60-speed/100*10;
        if (movingUp || movingLeft || movingDown || movingRight){
            timer++;
            if (speedAnimation <=0){
                speedAnimation = 1;
            }
            if (timer == speedAnimation){
                pic = 2;
            }if (timer >= speedAnimation*2){
                timer = 0;
                pic = 1;
            }
        }
    }
    
    public void resetTimer(int key){
        if (movingUp && key == moveUpKey){
            timer = 0;
            pic = 1;
        }if (movingLeft && key == moveLeftKey){
            timer = 0;
            pic = 1;
        }if (movingDown && key == moveDownKey){
            timer = 0;
            pic = 1;
        }if (movingRight && key == moveRightKey){
            timer = 0;
            pic = 1;
        }
    }
    
    public void setMovement(){
        if (movingRight){
            directionfacing = 4;
        }else if (movingUp){
            directionfacing = 1;
        }else if(movingLeft){
            directionfacing = 2;
        }else if (movingDown){
            directionfacing = 3;
        }
    }
    
    public void drawBoundarys(Graphics g){
        g.setColor(Color.green);
        if (getVisibleBoundaryDimensions().length > 0){
            Rectangle[] rects = getVisibleBoundaryDimensions();
            for (int q = 0; q < rects.length; q++){
                g.drawRect(doDrawLogic(rects[q].x, x), doDrawLogic(rects[q].y, y), rects[q].width, rects[q].height);
            }
        }
    }
    
    public void botChange(int bot, Game g){
        for (int q = 0; q < bot+1; q++){
            if (g.team1[q].isUserControled()){
                bot++;
            }
        }
        if (botSelected == bot && !g.team1[bot].isUserControled() && getTeam() == 1){
            try{
                g.team1[bot].setAIType(g.team1[bot].getAIType()+1);
                if (g.team1[bot].getAIType() >= 4){
                    g.team1[bot].setAIType(1);
                }
            }catch(Exception e){}
        }
        if (botSelected == bot && !g.team2[bot].isUserControled() && getTeam() == 2){
            try{
                g.team2[bot].setAIType(g.team2[bot].getAIType()+1);
                if (g.team2[bot].getAIType() >= 4){
                    g.team2[bot].setAIType(1);
                }
            }catch(Exception e){}
        }
        botSelected = bot;
    }
    
    public void KeyPressed(KeyEvent e, Game g){
        int key = e.getKeyCode();
        if (isUserControled()){
            if (key == moveRightKey){
                moveRight();
            }
            if (key == moveLeftKey){
                moveLeft();
            }
            if (key == moveUpKey){
                moveUp();
            }
            if (key == moveDownKey){
                moveDown();
            }
            if (key == sneakKey){
                turnSneakOn();
                //System.out.println("sneak on");
            }
            if (key == sprintKey){
                turnSprintOn();
                //System.out.println("sprint on");
            }
            if (!autoai){
                if (key == bot1){
                    botChange(0, g);
                }else if (key == bot2){
                    botChange(1, g);
                }else if (key == bot3){
                    botChange(2, g);
                }else if (key == bot4){
                    botChange(3, g);
                }else if (key == bot5){
                    botChange(4, g);
                }
            }
            
            for (int q = 0; q < currentpis; q++){
                if (key == itemUse[q] && selected-1 == q){
                    pi[q].useItem(this);
                }else if (selected-1 == q){
                    pi[q].turnPage(e);
                }
            }
            for (int q = 0; q < itemUse.length; q++){
                if (key == itemUse[q]){
                    selected = q+1;
                }
            }
        }
    }
    
    public void KeyReleased(KeyEvent e){
        int key = e.getKeyCode();
        if (isUserControled()){
            if (key == moveRightKey){
                stopMovingRight();
            }
            if (key == moveLeftKey){
                stopMovingLeft();
            }
            if (key == moveUpKey){
                stopMovingUp();
            }
            if (key == moveDownKey){
                stopMovingDown();
            }
            if (key == sneakKey){
                turnSneakOff();
                //System.out.println("sneak off");
            }
            if (key == sprintKey){
                turnSprintOff();
                //System.out.println("sprint off");
            }
        }
    }
}