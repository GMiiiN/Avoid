package avoid;

import javax.swing.*;

// 창 만들고 연결하고 담기.
public class AGame extends JFrame{

    private Game game = new Game(); // 규칙 객체 생성

    public AGame(){
        GamePanel gamePanel = new GamePanel(game);

        setTitle("치이카와의 장애물 피하기"); //타이틀 이름
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(gamePanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        gamePanel.requestFocusInWindow();
    }

}