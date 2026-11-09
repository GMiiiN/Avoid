package avoid;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
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

        addKeyListener(new KeyListener());
        game.start();
    }

    class KeyListener extends KeyAdapter{

        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()) {
			/*	case KeyEvent.VK_W:
					game.setUp(true);
					break;
				case KeyEvent.VK_S:
					game.setDown(true);
					break;
			*/
                case KeyEvent.VK_A:
                    game.setLeft(true);
                    break;
                case KeyEvent.VK_D:
                    game.setRight(true);
                    break;
                case KeyEvent.VK_LEFT:
                    game.setLeft(true);
                    break;
                case KeyEvent.VK_RIGHT:
                    game.setRight(true);
                    break;
                case KeyEvent.VK_ESCAPE:
                    System.exit(0);
                    break;
                case KeyEvent.VK_ENTER:
                    if(game.isOver()) game.reset();
                    break;
            }
        }
        public void keyReleased(KeyEvent e) {
            switch(e.getKeyCode()) {
			/*
			 * case KeyEvent.VK_W:
					game.setUp(false);
					break;
				case KeyEvent.VK_S:
					game.setDown(false);
					break;
			*/
                case KeyEvent.VK_A:
                    game.setLeft(false);
                    break;
                case KeyEvent.VK_D:
                    game.setRight(false);
                    break;
                case KeyEvent.VK_LEFT:
                    game.setLeft(false);
                    break;
                case KeyEvent.VK_RIGHT:
                    game.setRight(false);
                    break;

            }
        }
    }
}