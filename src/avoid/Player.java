package avoid;

import javax.swing.*;
import java.awt.*;

// 플레이어 한명
public class Player {
    private Image player = new ImageIcon("src/images/bate.png").getImage();

    private int x, y;
    private int pWidth = player.getWidth(null);
    private int pHeight = player.getHeight(null);
    private int pSpeed = 8;

    public void resetPosition(){
        x = (Main.SCREEN_WIDTH - pWidth) / 2;
        y = (Main.SCREEN_HEIGHT - pHeight) - 10;
    }

    public void move(boolean left, boolean right){
        if (left && x - pSpeed > 0)
            x -= pSpeed;
        if (right && x + pWidth + pSpeed < Main.SCREEN_WIDTH)
            x += pSpeed;
    }

    public void draw(Graphics g){
        g.drawImage(player, x, y, null);
    }

    public Image getPlayer() {
        return player;
    }

    public void setPlayer(Image player) {
        this.player = player;
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
