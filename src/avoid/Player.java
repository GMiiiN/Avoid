package avoid;

import javax.swing.*;
import java.awt.*;

// 플레이어 한명
public class Player {
    private Image walkImg = new ImageIcon("src/images/usagi_move.gif").getImage();
    private Image standImg = new ImageIcon("src/images/usagi.png").getImage();

    private boolean moving = false, facingRight = true; // 원본 그림이 왼쪽을 보고 있음.

    static final int SIZE = 80;
    private int x, y;
    private final int pWidth = SIZE;
    private final int pHeight = SIZE;

    static final int HIT_LEFT = 20, HIT_TOP = 8, HIT_WIDTH = 40, HIT_HEIGHT = 64;

    private int pSpeed = 8;

    public void resetPosition(){
        x = (Main.SCREEN_WIDTH - pWidth) / 2;
        y = (Main.SCREEN_HEIGHT - pHeight) - 10;
    }

    public void move(boolean left, boolean right){
        moving = left != right;
        if (right && !left) facingRight = true;
        if (left && !right) facingRight = false;

        if (left && x - pSpeed > 0)
            x -= pSpeed;
        if (right && x + pWidth + pSpeed < Main.SCREEN_WIDTH)
            x += pSpeed;
    }

    public void draw(Graphics g){
        Image img = moving ? walkImg : standImg;
        if (facingRight)
            g.drawImage(img, x + SIZE, y, -SIZE, SIZE, null);
        else
            g.drawImage(img, x, y, SIZE, SIZE, null);
    }
    public Rectangle getBounds(){
        return new Rectangle(x + HIT_LEFT, y + HIT_TOP, HIT_WIDTH, HIT_HEIGHT);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public int getpWidth() {
        return pWidth;
    }

    public int getpHeight() {
        return pHeight;
    }

    public int getpSpeed() {
        return pSpeed;
    }

    public void setpSpeed(int pSpeed) {
        this.pSpeed = pSpeed;
    }
}
