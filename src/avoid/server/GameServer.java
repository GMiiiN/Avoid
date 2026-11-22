package avoid.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class GameServer {

    public static final int DEFAULT_PORT = 30000;
    private static final int MAX_PLAYERS = 2;

    private final UserService[] slots = new UserService[MAX_PLAYERS]; // 0 = 1번 자리, 1 = 2번 자리
    private final Random random = new Random();

    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        new GameServer().run(port);
    }

    public void run(int port) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log("Game Server Running.. port " + port);
            while (true) { // AcceptServer
                Socket s = serverSocket.accept(); // 접속 대기
                log("새로운 접속 " + s.getRemoteSocketAddress());
                new UserService(s).start(); // 사용자별 스레드
            }
        }
    }

    private static void log(String msg) {
        System.out.println("[server] " + msg);
    }

    // 방(2인) 상태 관리

    private synchronized boolean login(UserService u) {
        for (int i = 0; i < MAX_PLAYERS; i++) {
            if (slots[i] == null) { // 비어 있는 앞자리부터
                slots[i] = u;
                u.slot = i + 1;
                u.WriteOne("/you " + u.slot + " " + u.name);
                WriteAll("/sys [" + u.name + "]님이 입장 하였습니다.");
                WriteUsers();
                log(u.name + " 입장 (" + u.slot + "번 자리). 현재 참가자 수 " + count());
                return true;
            }
        }
        return false;
    }

    private synchronized void logout(UserService u) {
        if (u.slot == 0 || slots[u.slot - 1] != u) return;
        slots[u.slot - 1] = null;
        u.ready = false;
        WriteAll("/sys [" + u.name + "]님이 퇴장 하였습니다.");
        WriteUsers();
        log(u.name + " 퇴장. 현재 참가자 수 " + count());
    }

    private synchronized void ready(UserService u) {
        if (u.ready) return;
        u.ready = true;
        WriteAll("/sys [" + u.name + "] 준비 완료");
        WriteUsers();
        if (count() == MAX_PLAYERS && slots[0].ready && slots[1].ready) {
            slots[0].ready = false;
            slots[1].ready = false;
            long seed = random.nextLong(); // 두 사람이 같은 장애물을 보도록 같은 시드 전달
            WriteAll("/start " + seed);
            WriteUsers();
            log("게임 시작 seed=" + seed);
        }
    }

    private synchronized int count() {
        int n = 0;
        for (UserService u : slots) if (u != null) n++;
        return n;
    }

    // 둘 다에게
    private synchronized void WriteAll(String msg) {
        for (UserService u : slots) if (u != null) u.WriteOne(msg);
    }

    // 상대에게만
    private synchronized void WriteOthers(UserService from, String msg) {
        for (UserService u : slots) if (u != null && u != from) u.WriteOne(msg);
    }

    // 대기실 자리 표시용: "/users 시은:1,-"
    private synchronized void WriteUsers() {
        StringBuilder sb = new StringBuilder("/users ");
        for (int i = 0; i < MAX_PLAYERS; i++) {
            if (i > 0) sb.append(',');
            UserService u = slots[i];
            sb.append(u == null ? "-" : u.name + ":" + (u.ready ? 1 : 0));
        }
        WriteAll(sb.toString());
    }

    // ---- 클라이언트 1명당 1개 스레드 ----
    class UserService extends Thread {
        private final Socket socket;
        private DataInputStream dis;
        private DataOutputStream dos;
        private String name = "";
        private int slot = 0; // 1 또는 2, 0 = 아직 자리 없음
        private boolean ready = false;

        UserService(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());

                // 첫 메시지는 "/login 이름"
                String[] args = dis.readUTF().trim().split(" ", 2);
                if (!args[0].equals("/login") || args.length < 2) return;
                name = args[1].trim().replaceAll("[ ,:]", "_");

                if (!login(this)) {
                    WriteOne("/full");
                    return;
                }
                WriteOne("/sys " + name + "님 환영합니다. 두 명 모두 준비를 누르면 시작합니다.");

                while (true) {
                    String msg = dis.readUTF().trim();
                    args = msg.split(" ", 2); // 명령어와 내용 분리
                    String arg = args.length > 1 ? args[1] : "";
                    switch (args[0]) {
                        case "/chat":
                            if (!arg.isEmpty()) WriteAll("/chat " + name + " " + arg);
                            break;
                        case "/ready":
                            ready(this);
                            break;
                        case "/pos":
                        case "/hp":
                            WriteOthers(this, args[0] + " " + arg); // 상대에게만 그대로 전달
                            break;
                        case "/exit":
                            return;
                        default:
                            WriteOne("/sys 알 수 없는 명령: " + args[0]);
                    }
                }
            } catch (IOException e) {
                // 연결 끊김
            } finally {
                logout(this);
                try { socket.close(); } catch (IOException ignored) { }
            }
        }

        void WriteOne(String msg) {
            try {
                synchronized (this) {
                    dos.writeUTF(msg);
                    dos.flush();
                }
            } catch (IOException e) {
                try { socket.close(); } catch (IOException ignored) { } // 수신 쪽 예외로 정리됨
            }
        }
    }
}
