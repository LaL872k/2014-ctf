/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package invincibe83.CaptureTheFlag;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author Arthur
 */
public class World extends JPanel {

    private ImageIcon botPanel = new ImageIcon(new ImageIcon("Images/bottom.png").getImage().getScaledInstance(500, 50, 1));
    private ImageIcon topPanel = new ImageIcon(new ImageIcon("Images/TopPanel.png").getImage().getScaledInstance(300, 50, 1));
    private ImageIcon selected = new ImageIcon(new ImageIcon("Images/Selected.png").getImage().getScaledInstance(17, 17, 1));
    private ImageIcon stack = new ImageIcon(new ImageIcon("Images/Stack.png").getImage().getScaledInstance(32, 32, 1));
    private ImageIcon flag = new ImageIcon(new ImageIcon("Images/Flag.png").getImage().getScaledInstance(50, 50, 1));
    private ImageIcon flage = new ImageIcon(new ImageIcon("Images/FlagEmpty.png").getImage().getScaledInstance(50, 50, 1));
    private ImageIcon jail = new ImageIcon(new ImageIcon("Images/Jail.png").getImage().getScaledInstance(200, 200, 1));
    private ImageIcon scoreboard = new ImageIcon(new ImageIcon("Images/scoreboard.png").getImage().getScaledInstance(500, 300, 1));
    private ImageIcon currentFlag1 = flag, currentFlag2 = flag;
    private Player user;
    private float time = 0;
    private String currentMessage = "";
    private float currentMessageTime = 0f;
    Block[] map;
    private Font myFont = new Font("San Serif", Font.PLAIN, 14), scoreFont = new Font("San Serif", Font.BOLD, 24), smallFont = new Font("San Serif", Font.PLAIN, 9);
    private int score1 = 0, score2 = 0, scorekey = KeyEvent.VK_Z;
    private int[] playerscoresteam1, playerscoresteam2;
    private Player[] team1, team2;
    private Rectangle flag1, flag2, jail1, jail2;
    private Color unknown = new Color(25, 66, 15), bluenormal = new Color(20, 28, 133), bluespecial = new Color(20,113,133); 
    private Color rednormal = new Color(99, 15, 15), redspecial = new Color(215, 25, 25);
    private boolean showscore;
    
    public World(Player p, int[] ids, int sidelength) {
        user = p;
        int x2 = 0;
        int y = 0;
        Rectangle[] mapGrid = new Rectangle[ids.length];
        for (int q = 0; q < ids.length; q++) {
            mapGrid[q] = new Rectangle(x2, y, 64, 64);
            if (x2 == (ids.length / (ids.length / sidelength) - 1) * 64) {
                y += 64;
                x2 = -64;
            }
            x2 += 64;
        }

        map = new Block[ids.length];
        for (int q = 0; q < ids.length; q++) {
            map[q] = new Block(ids[q], mapGrid[q].x, mapGrid[q].y, mapGrid[q].height);
        }
    }

    public void drawJailandFlag(Graphics g) {
        currentFlag1.paintIcon(this, g, doDrawLogic(flag1.x, user.getX()), doDrawLogic(flag1.y, user.getY()));
        currentFlag2.paintIcon(this, g, doDrawLogic(flag2.x, user.getX()), doDrawLogic(flag2.y, user.getY()));
        jail.paintIcon(this, g, doDrawLogic(jail1.x, user.getX()), doDrawLogic(jail1.y, user.getY()));
        jail.paintIcon(this, g, doDrawLogic(jail2.x, user.getX()), doDrawLogic(jail2.y, user.getY()));
    }

    public boolean collisionDetection(Rectangle rect) {
        for (int q = 0; q < map.length; q++) {
            if (map[q].getDemensions().intersects(rect) && (!map[q].getMovableThrough())) {
                return true;
            }
        }
        return false;
    }

    public void update(Player p, float time, Game g) {
        user = p;
        this.time = time;
        updateMessage();
        score1 = g.team1score;
        score2 = g.team2score;

        int[] inters = new int[g.team1.length];
        int[] inters2 = new int[g.team1.length];
        for (int q = 0; q < g.team1.length; q++) {
            inters[q] = g.team1[q].getScore();
            inters2[q] = g.team2[q].getScore();
        }
        
        playerscoresteam1 = inters;
        playerscoresteam2 = inters2;

        team1 = g.team1;
        team2 = g.team2;

        flag1 = g.flag1;
        flag2 = g.flag2;
        
        jail1 = g.jail1;
        jail2 = g.jail2;
        
        if (!g.flag1onground){
            currentFlag1 = flage;
        }else{
            currentFlag1 = flag;
        }
        if (!g.flag2onground){
            currentFlag2 = flage;
        }else{
            currentFlag2 = flag;
        }
        
        for (int t = 0; t < g.team1.length; t++){
            for (int q = 0 ; q < map.length; q++){
                map[q].putOnEffect(team1[t]);
                map[q].putOnEffect(team2[t]);
            }
        }
        for (int q = 0; q < map.length; q++){
            map[q].update();
        }
    }

    public void jails(Graphics g) {
        g.setColor(Color.pink);
        g.drawRect(doDrawLogic(flag1.x, user.getX()), doDrawLogic(flag1.y, user.getY()), flag1.width, flag1.height);
        g.drawRect(doDrawLogic(flag2.x, user.getX()), doDrawLogic(flag2.y, user.getY()), flag2.width, flag2.height);
    }

    public void updateMessage() {
        if (currentMessageTime > 0) {
            currentMessageTime -= 1 / 60f;
        }
    }

    public void changeMessage(String newMessage) {
        currentMessage = newMessage;
        currentMessageTime = 5f;
    }

    public Block[] getMap() {
        return map;
    }

    public void getMapBoundaries() {

    }

    public void getDividingLine() {

    }

    //centers screen
    public int doDrawLogic(int x, int px) {
        return x + (250 - user.getWidth() / 2 - px);
    }

    public int doPercentLogic(int current, int total, int length) {
        return (int) (((double) current / total) * (double) length);
    }

    public void drawTopPanel(Graphics g) {
        topPanel.paintIcon(this, g, 50, 0);
        g.setColor(Color.white);
        g.drawString(getTime((int) (Math.ceil(time * 2) / 2)), 140 + 50, 49);
        g.setFont(scoreFont);
        g.drawString("" + score1, 120 + 50, 26);
        g.drawString("" + score2, 160 + 50, 26);
        g.setFont(smallFont);
        g.setColor(Color.black);
        for (int q = 0; q < playerscoresteam1.length; q++) {
            if (q <= 2) {
                team1[q].getProfileImage().paintIcon(this, g, 36*q+50, 0);
                if (user.getTeam() == 1){
                    g.drawString("" + team1[q].getAIType(), 36 * (q) + 50 + 26, 25);
                }
            } else {
                team1[q].getProfileImage().paintIcon(this, g, 36*(q-3)+50, 25);
                if (user.getTeam() == 1){
                    g.drawString("" + team1[q].getAIType(), 36 * (q - 3) + 50 + 26, 50);
                }
            }
        }
        for (int q = 0; q < playerscoresteam2.length; q++) {
            if (q <= 2) {
                team2[q].getProfileImage().paintIcon(this, g, 36*q+50 + 210-20, 0);
                if (user.getTeam() == 2){
                    g.drawString("" + team2[q].getAIType(), 36 * (q) + 50 + 210, 25);
                }
            } else {
                team2[q].getProfileImage().paintIcon(this, g, 36*(q-3)+ 50 + 210-20, 25);
                if (user.getTeam() == 2){
                    g.drawString("" + team2[q].getAIType(), 36 * (q - 3) + 50 + 210, 50);
                }
            }
        }
        //25, 25
    }

    public void drawBotPanel(Graphics g) {
        botPanel.paintIcon(this, g, 0, 450);
        g.setColor(Color.GREEN);
        int width = doPercentLogic((int) user.getStanima(), user.getMaxStanima(), 192);
        if (width > 3) {
            g.drawLine(156, 24 + 450, 156 + width - 4, 24 + 450);//342
        }
        if (width > 2) {
            g.drawLine(155, 25 + 450, 156 + width - 3, 25 + 450);//344
        }
        g.fillRect(154, 25 + 450, width, 43 - 25);//192
        if (width > 2) {
            g.drawLine(155, 43 + 450, 156 + width - 3, 43 + 450);//344
        }
        if (width > 3) {
            g.drawLine(156, 44 + 450, 156 + width - 4, 44 + 450);//342
        }
        if (user.getCurrentPlayerEffectLength() > 0) {
            for (int q = 0; q < user.getCurrentPlayerEffectLength(); q++) {
                if (q <= 6) {
                    user.getEffects()[q].getImage().paintIcon(this, g, 9 + q * 19, 13 + 450);
                } else if (q >= 7 || q <= 9) {
                    user.getEffects()[q].getImage().paintIcon(this, g, 9 + (q - 7) * 19, 13 + 450 + 19);
                }
            }
        }
        if (user.getCurrentPlayerInventoryLength() > 0) {
            for (int q = 0; q < user.getCurrentPlayerInventoryLength(); q++) {
                user.getInventory()[q].getImage().paintIcon(this, g, 157 + q * 17, 450 + 3);
            }
        }
        stack.paintIcon(this, g, 156 + ((user.getSelectedItem() - 1) * 17) - 7, 450 - 32);
        selected.paintIcon(this, g, 156 + ((user.getSelectedItem() - 1) * 17), 450 + 2);
        if (user.getSelectedItem() <= user.getCurrentPlayerInventoryLength()) {
            user.getInventory()[user.getSelectedItem() - 1].displayPreveiw(g, this, 351, 450);
        }
        g.setFont(myFont);
        g.setColor(Color.white);
        g.drawString(user.getStanima() + "/" + user.getMaxStanima(), 154, 40 + 450);
        try {
            g.drawString("" + user.getInventory()[user.getSelectedItem() - 1].getStack(), 156 + ((user.getSelectedItem() - 1) * 17) - 5, 450 - 32 + 15);
        } catch (Exception e) {
        }
    }

    public String getTime(int time) {
        int h = 0;
        int m = 0;
        int s = time;
        if (time > 60) {
            m = time / 60;
            s = time - m * 60;
            if (m > 60) {
                h = time / 60;
                m = time - h * 60;
            }
        }
        if (h > 0) {
            if (m < 10 && s < 10) {
                return h + ":0" + m + ":0" + s;
            }
            if (m < 10) {
                return h + ":0" + m + ":" + s;
            }
            if (s < 10) {
                return h + ":" + m + ":0" + s;
            }
            return h + ":" + m + ":" + s;
        }
        if (m > 0) {
            if (s < 10) {
                return m + ":0" + s;
            }
            return m + ":" + s;
        }
        return s + "";
    }

    public void drawMap(Graphics g) {
        for (int q = 0; q < map.length; q++) {
            if (map[q].getX() - user.getX() + 250 <= 516 && map[q].getX() + 65 - user.getX() + 258 >= 0 && map[q].getY() - user.getY() + 250 <= 539 && map[q].getY() + 65 - user.getY() + 256 >= 0) {
                map[q].getImage().paintIcon(this, g, doDrawLogic(map[q].getX(), user.getX()), doDrawLogic(map[q].getY(), user.getY()));
            }
        }
    }

    public void drawPlayers(Graphics g) {
        for (int q = 0; q < team1.length && q < team2.length; q++) {
            g.setColor(Color.black);
            team1[q].drawPlayer(g, this, user.getX(), user.getY(), user);
            //g.drawString(team1[q].getName() + " " + team1[q].isSneaking(), doDrawLogic(team1[q].getX(), user.getX()), doDrawLogic(team1[q].getY(), user.getY()));

            team2[q].drawPlayer(g, this, user.getX(), user.getY(), user);
            //g.drawString(team2[q].getName() + " " + team2[q].isSneaking(), doDrawLogic(team2[q].getX(), user.getX()), doDrawLogic(team2[q].getY(), user.getY()));
        }
    }

    public void miniMap(Graphics g, int x, int y) {
        for (int q = 0; q < map.length; q++) {
            for (int n = 0; n < team1.length; n++){
                if (user.getTeam() == 1){
                    if (map[q].getDemensions().intersects(team1[n].getViewableRect())){
                        g.setColor(map[q].getColor());
                        break;
                    }else{
                        g.setColor(unknown);
                    }
                }else{
                    if (map[q].getDemensions().intersects(team2[n].getViewableRect())){
                        g.setColor(map[q].getColor());
                        break;
                    }else{
                        g.setColor(unknown);
                    }
                }
            }
            int num = q;
            num -= (num / 72) * 72;

            int num2 = q;
            num2 = (num2 / 72);

            g.fillRect(num * 2 + x, num2 * 2 + y, 2, 2);
        }
        for (int q = 0; q < team1.length; q++){
            if (user.getTeam() == 1){
                if (team1[q].hasFlag()){
                    g.setColor(bluespecial);
                }else{
                    g.setColor(bluenormal);
                }
                g.fillRect(team1[q].getX() / 32 + x - 1, team1[q].getY() / 32 + y - 1, 4, 4);
                for (int n = 0; n < team2.length; n++){
                    if (team1[q].getViewableRect().intersects(team2[n].getDimensions()) && user.viewable(team2[n])){
                        if (team2[n].hasFlag()){
                            g.setColor(redspecial);
                        }else{
                            g.setColor(rednormal);
                        }
                        g.fillRect(team2[n].getX() / 32 + x - 1, team2[n].getY() / 32 + y - 1, 4, 4);
                    }
                }
            }
            if (user.getTeam() == 2){
                if (team2[q].hasFlag()){
                    g.setColor(redspecial);
                }else{
                    g.setColor(rednormal);
                }
                g.fillRect(team2[q].getX() / 32 + x - 1, team2[q].getY() / 32 + y - 1, 4, 4);
                for (int n = 0; n < team1.length; n++){
                    if (team2[q].getViewableRect().intersects(team1[n].getDimensions()) && user.viewable(team1[n])){
                        if (team1[n].hasFlag()){
                            g.setColor(bluespecial);
                        }else{
                            g.setColor(bluenormal);
                        }
                        g.fillRect(team1[n].getX() / 32 + x - 1, team1[n].getY() / 32 + y - 1, 4, 4);
                    }
                }
            }
        }
    }

    public void playerStats(Graphics g) {
        g.setColor(Color.black);
        g.drawString("x=" + user.getX(), 0, 10);
        g.drawString("y=" + user.getY(), 0, 20);
        g.drawString("s=" + user.getSpeed(), 0, 30);
    }
    
    public void drawScoreboard(Graphics g, int x, int y){
        if (showscore){
            g.setColor(Color.black);
            scoreboard.paintIcon(this, g, x, y);
            for (int q = 0; q < team1.length; q++){
                g.drawString("Flags Stolen=" + team1[q].getScore() + " Players Tagged=" + team1[q].getTaggedPlayers(), x+5, y+20+q*50);
                g.drawString("Went To Jail=" + team1[q].getTimesWentToJail() + " Players Freed=" + team1[q].getFreedPlayers(), x+5, y+40+q*50);
            }
            for (int q = 0; q < team2.length; q++){
                g.drawString("Flags Stolen=" + team2[q].getScore() + " Players Tagged=" + team2[q].getTaggedPlayers(), x+255, y+20+q*50);
                g.drawString("Went To Jail=" + team2[q].getTimesWentToJail() + " Players Freed=" + team2[q].getFreedPlayers(), x+255, y+40+q*50);
            }
        }
    }
    
    public void keyPressed(KeyEvent e){
        int key = e.getKeyCode();
        if (key == scorekey){
            showscore = true;
        }
    }
    
    public void keyRelesed(KeyEvent e){
        int key = e.getKeyCode();
        if (key == scorekey){
            showscore = false;
        }
    }
    
    public void drawCenterLine(Graphics g){
        g.setColor(Color.green);
        g.drawLine(doDrawLogic(72/2*64, user.getX()), doDrawLogic(0, user.getY()), doDrawLogic(72/2*64, user.getX()), doDrawLogic(38*64, user.getY()));
    }

    public void paint(Graphics graphics) {
        super.paint(graphics);
        Graphics2D g2 = (Graphics2D) graphics;

        //screen
        //24, 156
        //42
        //152, 3
        //156, 2
        //351 149
        drawMap(g2);
        drawJailandFlag(g2);
        if (currentMessageTime > 0) {
            g2.setColor(Color.black);
            g2.drawString(currentMessage, 200, 100);
        }
        drawPlayers(g2);
        user.render(g2);
        miniMap(g2, 350, 0);
        drawTopPanel(g2);
        drawBotPanel(g2);
        playerStats(g2);
        drawCenterLine(g2);
        drawScoreboard(g2, 0, 100);
        //g2.drawLine(250, 0, 250, 500);
        //g2.drawLine(0, 250, 500, 250);
    }
}
