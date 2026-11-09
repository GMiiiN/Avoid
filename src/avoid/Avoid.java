package avoid;
//장애물 클래스
import java.awt.Image;

import javax.swing.ImageIcon;

public class Avoid {
    Image image=new ImageIcon("src/images/ddong.png").getImage();
    int x,y;
    int width=image.getWidth(null);
    int height=image.getHeight(null);


    public Avoid(int x, int y) {
        this.x=x;
        this.y=y;
    }

    public void move() {
        this.y+=7;	//똥이 떨어지는 속도 조절
    }
}