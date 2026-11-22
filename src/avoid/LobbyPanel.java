package avoid;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

// 대기실: 왼쪽 채팅, 오른쪽 캐릭터 자리 2개 + 준비/나가기
public class LobbyPanel extends JPanel {

    private static final int ICON_SIZE = 200;
    private static final Color BG = new Color(0xF2F2F2);
    private static final Color SLOT_BG = new Color(0xE6EEF8);
    private static final Color MY_BORDER = new Color(0x2E7D32);

    private final MainPanel main;
    private final NetClient net;
    private final JTextArea textArea = new JTextArea();
    private final JTextField txtInput = new JTextField();
    private final JButton btnSend = new JButton("Send");
    private final JButton btnReady = new JButton("준비");
    private final JButton btnExit = new JButton("나가기");
    private final JLabel lblMyName = new JLabel(" ", SwingConstants.CENTER);

    private final CharView[] slotImage = new CharView[2];
    private final JLabel[] slotName = new JLabel[2];
    private final JPanel[] slotBox = new JPanel[2];
    private final Image[] images = {
            CharacterType.WHITE.lobbyImage(), CharacterType.IVORY.lobbyImage()
    };
    private int mySlot = 0;

    public LobbyPanel(MainPanel main, NetClient net) {
        this.main = main;
        this.net = net;
        setLayout(new GridLayout(1, 2, 24, 0));
        setBorder(new EmptyBorder(24, 24, 24, 24));
        setBackground(BG);
        add(buildChat());
        add(buildRoom());

        btnSend.addActionListener(e -> sendChat());
        txtInput.addActionListener(e -> sendChat());
        btnReady.addActionListener(e -> {
            net.send("/ready");
            btnReady.setEnabled(false);
            txtInput.requestFocusInWindow();
        });
        btnExit.addActionListener(e -> main.exitRoom());
    }

    // 왼쪽: 채팅창 + 입력칸 + 내 이름
    private JPanel buildChat() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
        p.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel input = new JPanel(new BorderLayout(8, 0));
        input.setOpaque(false);
        input.add(txtInput, BorderLayout.CENTER);
        input.add(btnSend, BorderLayout.EAST);

        lblMyName.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
        lblMyName.setBorder(new LineBorder(Color.DARK_GRAY));
        lblMyName.setPreferredSize(new Dimension(120, 36));
        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        nameRow.setOpaque(false);
        nameRow.add(lblMyName);

        JPanel bottom = new JPanel(new BorderLayout(0, 8));
        bottom.setOpaque(false);
        bottom.add(input, BorderLayout.NORTH);
        bottom.add(nameRow, BorderLayout.SOUTH);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    // 오른쪽: 캐릭터 자리 2개 + 준비/나가기 버튼
    private JPanel buildRoom() {
        JPanel p = new JPanel(new BorderLayout(0, 24));
        p.setOpaque(false);

        JPanel slots = new JPanel(new GridLayout(1, 2, 16, 0));
        slots.setOpaque(false);
        for (int i = 0; i < 2; i++) {
            slotImage[i] = new CharView();
            slotImage[i].setPreferredSize(new Dimension(ICON_SIZE + 20, ICON_SIZE + 20));
            slotName[i] = new JLabel(" ", SwingConstants.CENTER);
            slotName[i].setFont(new Font(Font.DIALOG, Font.BOLD, 15));
            slotName[i].setOpaque(true);
            slotName[i].setBackground(Color.WHITE);
            slotName[i].setBorder(new EmptyBorder(8, 4, 8, 4));

            slotBox[i] = new JPanel(new BorderLayout());
            slotBox[i].setBackground(SLOT_BG);
            slotBox[i].add(slotImage[i], BorderLayout.CENTER);
            slotBox[i].add(slotName[i], BorderLayout.SOUTH);
            slots.add(slotBox[i]);
        }
        JPanel slotWrap = new JPanel(new GridBagLayout()); // 자리 칸을 위쪽 가운데에 고정 크기로
        slotWrap.setOpaque(false);
        slots.setPreferredSize(new Dimension(2 * (ICON_SIZE + 20) + 16, ICON_SIZE + 70));
        slotWrap.add(slots);
        p.add(slotWrap, BorderLayout.CENTER);

        Font bf = new Font(Font.DIALOG, Font.BOLD, 16);
        btnReady.setFont(bf);
        btnExit.setFont(bf);
        JPanel buttons = new JPanel(new GridLayout(2, 1, 0, 8));
        buttons.setOpaque(false);
        buttons.setPreferredSize(new Dimension(300, 104));
        buttons.add(btnReady);
        buttons.add(btnExit);
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        btnWrap.setOpaque(false);
        btnWrap.add(buttons);
        p.add(btnWrap, BorderLayout.SOUTH);

        showSlots(new String[]{"-", "-"});
        return p;
    }

    private void sendChat() {
        String text = txtInput.getText().trim();
        if (!text.isEmpty()) net.send("/chat " + text);
        txtInput.setText("");
        txtInput.requestFocusInWindow();
    }

    // 접속 직후 게임 끝난 뒤 대기실로 올 때마다
    public void enter() {
        btnReady.setEnabled(true);
        txtInput.requestFocusInWindow();
    }

    public void setMySlot(int slot, String name) {
        mySlot = slot;
        lblMyName.setText(name);
    }

    public void clearChat() {
        textArea.setText("");
    }

    public void onMessage(String msg) {
        String[] args = msg.split(" ", 2);
        String arg = args.length > 1 ? args[1] : "";
        switch (args[0]) {
            case "/chat": { // "/chat 이름 내용"
                String[] c = arg.split(" ", 2);
                append("[" + c[0] + "] " + (c.length > 1 ? c[1] : ""));
                break;
            }
            case "/sys":
                append("[SERVER] " + arg);
                break;
            case "/users": // "/users 시은:1,-"
                showSlots(arg.split(",", -1));
                break;
            case "/start":
                append("게임을 시작합니다.");
                main.showGame(Long.parseLong(arg.trim()));
                break;
            default:
                // 대기실에서 필요 없는 게임 메시지(/pos 등)는 무시
        }
    }

    // 자리마다: 사람이 있으면 캐릭터 + "이름 (준비 완료)", 없으면 빈칸 + "대기 중..."
    private void showSlots(String[] users) {
        for (int i = 0; i < 2; i++) {
            String u = i < users.length ? users[i].trim() : "-";
            boolean empty = u.isEmpty() || u.equals("-");
            if (empty) {
                slotImage[i].show(null);
                slotName[i].setText(" ");
            } else {
                String[] nr = u.split(":");
                boolean isReady = nr.length > 1 && nr[1].equals("1");
                slotImage[i].show(images[i]);
                slotName[i].setText(nr[0] + (i + 1 == mySlot ? " (나)" : "") + (isReady ? "  ✔ 준비 완료" : ""));
                slotName[i].setForeground(isReady ? MY_BORDER : Color.BLACK);
            }
            slotBox[i].setBorder(i + 1 == mySlot ? new LineBorder(MY_BORDER, 3) : new LineBorder(Color.LIGHT_GRAY, 1));
        }
    }

    // 자리 칸의 캐릭터 그림
    private static class CharView extends JComponent {
        private Image img;

        void show(Image img) {
            this.img = img;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (img == null) {
                g.setColor(Color.GRAY);
                g.setFont(new Font(Font.DIALOG, Font.PLAIN, 15));
                String t = "대기 중...";
                FontMetrics fm = g.getFontMetrics();
                g.drawString(t, (getWidth() - fm.stringWidth(t)) / 2, getHeight() / 2);
                return;
            }
            int s = Math.min(ICON_SIZE, Math.min(getWidth(), getHeight()));
            g.drawImage(img, (getWidth() - s) / 2, (getHeight() - s) / 2, s, s, this);
        }
    }

    private void append(String line) {
        textArea.append(line + "\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}
