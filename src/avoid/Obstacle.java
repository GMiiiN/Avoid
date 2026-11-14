package avoid;
//장애물 클래스
import java.awt.Image;

import javax.swing.ImageIcon;

public class Obstacle {
    public static final int SIZE = 50;
    Image image=new ImageIcon("src/images/apple.png").getImage();
    int x,y;
    int width=SIZE;
    int height=SIZE;


    public Obstacle(int x, int y) {
        this.x=x;
        this.y=y;
    }

    public void move() {
        this.y+=7;	//똥이 떨어지는 속도 조절
    }
}