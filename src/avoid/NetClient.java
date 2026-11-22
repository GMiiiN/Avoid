package avoid;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

// 소켓 통신 전담
// 받은 메시지는 Swing 스레드에서 Listener에게 넘겨준다.
public class NetClient {

    public interface Listener {
        void onMessage(String msg);
    }

    public static final String DISCONNECTED = "/disconnected"; // 연결이 끊기면 Listener에게 보내는 가짜 메시지

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private volatile Listener listener;
    private volatile boolean closed = true;

    // 서버 접속 후 "/login 이름" 전송, 수신 스레드 시작
    public void connect(String ip, int port, String name) throws IOException {
        socket = new Socket(ip, port);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
        closed = false;
        send("/login " + name);
        Thread t = new Thread(this::receiveLoop, "NetClient-receive");
        t.setDaemon(true);
        t.start();
    }

    public void setListener(Listener l) {
        this.listener = l;
    }

    // 게임 타이머(/pos)와 채팅이 동시에 보내도 섞이지 않도록 synchronized
    public synchronized void send(String msg) {
        if (closed) return;
        try {
            dos.writeUTF(msg);
            dos.flush();
        } catch (IOException e) {
            // 소켓만 닫으면 수신 스레드가 예외로 깨어나 DISCONNECTED를 알린다
            try { socket.close(); } catch (IOException ignored) { }
        }
    }

    private void receiveLoop() {
        try {
            while (true) {
                deliver(dis.readUTF());
            }
        } catch (IOException e) {
            // 서버 종료 또는 close() 호출
        }
        boolean wasOpen = !closed;
        close();
        if (wasOpen) deliver(DISCONNECTED);
    }

    private void deliver(String msg) {
        Listener l = listener;
        if (l != null) SwingUtilities.invokeLater(() -> l.onMessage(msg));
    }

    public boolean isConnected() {
        return !closed;
    }

    public void close() {
        closed = true;
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {
        }
    }
}
