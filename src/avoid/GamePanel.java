package avoid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

// 화면 + 입력 + 시계 + 상대와 주고받기
public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private static final int POS_SEND_INTERVAL = 3; // 3틱(60ms)마다 위치 전송

    private Image background = new ImageIcon("src/images/background.jpg").getImage(); // 배경
    private Timer timer = new Timer(20, this);
    private Game game; // 게임 규칙
    private boolean[] keys = new boolean[256]; //키보드 상태표

    private final MainPanel main;
    private final NetClient net;
    private int tick;
    private int lastSentX = -1, lastSentHp = -1;

    public GamePanel(Game game, MainPanel main, NetClient net) { // 그릴 대상
        this.game = game;
        this.main = main;
        this.net = net;
        setFocusable(true); // 패널 포커스
        setPreferredSize(new Dimension(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT));
        addKeyListener(this);
    }

    // 서버의 /start 시드로 시작 → 두 사람이 같은 장애물을 본다
    public void start(long seed) {
        game.reset(seed);
        Arrays.fill(keys, false);
        tick = 0;
        lastSentX = -1;
        lastSentHp = -1;
        timer.start();
    }

    // 1번 자리 = 하얀 캐릭터, 2번 자리 = 아이보리. 상대는 반대 캐릭터
    public void setMySlot(int slot) {
        game.setMySlot(slot);
    }

    public void stop() {
        timer.stop();
    }

    // 상대가 보낸 게임 메시지 (MainPanel이 Swing 스레드에서 넘겨줌)
    public void onMessage(String msg) {
        String[] p = msg.split(" ", 2);
        String arg = p.length > 1 ? p[1].trim() : "";
        try {
            switch (p[0]) {
                case "/pos":
                    game.setOpponentX(Integer.parseInt(arg));
                    break;
                case "/hp":
                    game.setOpponentHp(Integer.parseInt(arg));
                    break;
                default:
            }
        } catch (NumberFormatException ignored) {
        }
        repaint();
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
        sendState();
        repaint();
    }

    private void sendState() {
        tick++;
        int x = game.getPlayerX();
        if (tick % POS_SEND_INTERVAL == 0 && x != lastSentX) {
            net.send("/pos " + x);
            lastSentX = x;
        }
        int hp = game.getHp();
        if (hp != lastSentHp) {
            net.send("/hp " + hp);
            lastSentHp = hp;
        }
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
            case KeyEvent.VK_ESCAPE: // 대기실로
                main.showLobby();
                break;
            case KeyEvent.VK_ENTER:
                if(game.isOver()) main.showLobby();
                break;
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code < keys.length) keys[code] = false;
    }
}
