package avoid;

import javax.swing.*;
import java.awt.*;
// 화면 + 입력 + 시계
public class GamePanel extends JPanel {
    private Image background = new ImageIcon("").getImage(); // 배경

    private Game game; // 게임 규칙

    public GamePanel(Game game) { // 그릴 대상
        this.game = game;
        setPreferredSize(new Dimension(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT));
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g); // 이전 그림 지우기
        g.drawImage(background, 0, 0, this); // 배경그리기
        game.gameDraw(g); // 플레이어, 장애물, 점수
    }
}
