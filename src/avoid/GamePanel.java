package avoid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// 화면 + 입력 + 시계
public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private Image background = new ImageIcon("").getImage(); // 배경
    private Timer timer = new Timer(20, this);
    private Game game; // 게임 규칙
    private boolean[] keys = new boolean[256]; //키보드 상태표

    public GamePanel(Game game) { // 그릴 대상
        this.game = game;
        setFocusable(true); // 패널 포커스
        setPreferredSize(new Dimension(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT));
        addKeyListener(this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g); // 이전 그림 지우기
        g.drawImage(background, 0, 0, this); // 배경그리기
        game.gameDraw(g); // 플레이어, 장애물, 점수
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean left  = keys[KeyEvent.VK_LEFT]  || keys[KeyEvent.VK_A];
        boolean right = keys[KeyEvent.VK_RIGHT]  || keys[KeyEvent.VK_D];
        game.update(left, right);
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code < keys.length) // 어떤 키든 기록
            keys[code] = true;
        switch(code) { // 한 번만 할 동작
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
            case KeyEvent.VK_ENTER:
                if(game.isOver()) game.reset();
                break;
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code < keys.length) keys[code] = false;
    }
}
