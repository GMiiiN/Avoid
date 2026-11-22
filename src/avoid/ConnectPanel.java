package avoid;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

// 접속 화면: 이름, IP, 포트 입력
public class ConnectPanel extends JPanel {

    private final MainPanel main;
    private final NetClient net;
    private final JTextField txtName = new JTextField(12);
    private final JTextField txtIp = new JTextField("127.0.0.1", 12);
    private final JTextField txtPort = new JTextField("30000", 12);
    private final JButton btnConnect = new JButton("Connect");
    private final JLabel lblStatus = new JLabel(" ");

    public ConnectPanel(MainPanel main, NetClient net) {
        this.main = main;
        this.net = net;
        setLayout(new GridBagLayout()); // 가운데 정렬

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("User Name"));
        form.add(txtName);
        form.add(new JLabel("IP Address"));
        form.add(txtIp);
        form.add(new JLabel("Port Number"));
        form.add(txtPort);
        form.add(new JLabel());
        form.add(btnConnect);

        JPanel box = new JPanel(new BorderLayout(0, 12));
        JLabel title = new JLabel("치이카와의 장애물 피하기 - 1:1 대전", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        box.add(title, BorderLayout.NORTH);
        box.add(form, BorderLayout.CENTER);
        lblStatus.setForeground(Color.RED);
        box.add(lblStatus, BorderLayout.SOUTH);
        add(box);

        btnConnect.addActionListener(e -> connect());
        txtName.addActionListener(e -> connect());
        txtIp.addActionListener(e -> connect());
        txtPort.addActionListener(e -> connect());
    }

    private void connect() {
        String name = txtName.getText().trim();
        String ip = txtIp.getText().trim();
        int port;
        if (name.isEmpty()) {
            lblStatus.setText("이름을 입력하세요.");
            return;
        }
        try {
            port = Integer.parseInt(txtPort.getText().trim());
        } catch (NumberFormatException e) {
            lblStatus.setText("포트 번호가 올바르지 않습니다.");
            return;
        }
        btnConnect.setEnabled(false);
        lblStatus.setText("접속 중...");
        // connect()는 블로킹이라 Swing 스레드가 멈추지 않도록 별도 스레드에서 실행
        new Thread(() -> {
            try {
                net.connect(ip, port, name);
                SwingUtilities.invokeLater(() -> {
                    lblStatus.setText(" ");
                    btnConnect.setEnabled(true);
                    main.showLobby();
                });
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    lblStatus.setText("접속 실패: " + e.getMessage());
                    btnConnect.setEnabled(true);
                });
            }
        }).start();
    }

    public void onMessage(String msg) {
        if (msg.equals("/full")) lblStatus.setText("방이 가득 찼습니다. (최대 2명)");
        else if (msg.equals(NetClient.DISCONNECTED)) lblStatus.setText("서버와 연결이 끊겼습니다.");
    }

    public void focus() {
        txtName.requestFocusInWindow();
    }
}
