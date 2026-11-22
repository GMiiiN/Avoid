package avoid;

import javax.swing.*;
import java.awt.*;

// 화면 전환 담당: 서버 접속 → 대기실 → 인게임을 교체

public class MainPanel extends JPanel {

    private static final String CONNECT = "connect", LOBBY = "lobby", GAME = "game";

    private final CardLayout cards = new CardLayout();
    private final NetClient net = new NetClient();
    private final ConnectPanel connectPanel;
    private final LobbyPanel lobbyPanel;
    private final GamePanel gamePanel;
    private String current = CONNECT;

    public MainPanel() {
        setLayout(cards);
        setPreferredSize(new Dimension(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT));

        connectPanel = new ConnectPanel(this, net);
        lobbyPanel = new LobbyPanel(this, net);
        gamePanel = new GamePanel(new Game(), this, net);
        add(connectPanel, CONNECT);
        add(lobbyPanel, LOBBY);
        add(gamePanel, GAME);

        net.setListener(this::route);
        showConnect();
    }

    // 서버 메시지 분배
    private void route(String msg) {
        String cmd = msg.split(" ", 2)[0];
        switch (cmd) {
            case "/you": { // "/you 자리번호 이름" → 1번 하얀 캐릭터, 2번 아이보리
                String[] args = msg.split(" ", 3);
                int slot = Integer.parseInt(args[1]);
                lobbyPanel.setMySlot(slot, args.length > 2 ? args[2] : "");
                gamePanel.setMySlot(slot);
                break;
            }
            case "/chat":
            case "/sys":
            case "/users":
                lobbyPanel.onMessage(msg);
                break;
            case NetClient.DISCONNECTED:
            case "/full":
                gamePanel.stop();
                showConnect();
                connectPanel.onMessage(msg);
                break;
            default:
                if (GAME.equals(current)) gamePanel.onMessage(msg);
                else lobbyPanel.onMessage(msg);
        }
    }

    public void showConnect() {
        current = CONNECT;
        cards.show(this, CONNECT);
        connectPanel.focus();
    }

    public void showLobby() {
        gamePanel.stop();
        current = LOBBY;
        cards.show(this, LOBBY);
        lobbyPanel.enter();
    }

    public void showGame(long seed) {
        current = GAME;
        cards.show(this, GAME);
        gamePanel.start(seed);
        gamePanel.requestFocusInWindow(); // 안 하면 키 입력이 안 먹힘
    }

    // 대기실 나가기
    public void exitRoom() {
        net.send("/exit");
        net.close();
        lobbyPanel.clearChat();
        showConnect();
    }

    public void shutdown() {
        net.send("/exit");
        net.close();
    }
}
