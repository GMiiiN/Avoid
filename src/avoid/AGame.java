package avoid;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// 창 만들고 MainPanel(접속 → 대기실 → 인게임) 담기.
public class AGame extends JFrame{

    public AGame(){
        MainPanel mainPanel = new MainPanel();

        setTitle("치이카와의 장애물 피하기"); //타이틀 이름
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mainPanel.shutdown(); // 서버에 /exit 보내고 소켓 닫기
            }
        });

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

}