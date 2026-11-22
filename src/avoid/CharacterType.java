package avoid;

import javax.swing.*;
import java.awt.*;

// 자리 번호별 캐릭터: 1번(먼저 들어온 사람) = 치이카와, 2번 = 우사기
public enum CharacterType {
    WHITE("src/images/chiikawa.png", "src/images/white-in-place.gif"),
    IVORY("src/images/usagi.png", "src/images/usagi_move.gif");

    final Image standImg; // 가만히 있을 때
    final Image moveImg;  // 움직일 때 / 대기실
    final String movePath;

    CharacterType(String standPath, String movePath) {
        this.standImg = new ImageIcon(standPath).getImage();
        this.moveImg = new ImageIcon(movePath).getImage();
        this.movePath = movePath;
    }

    public static CharacterType ofSlot(int slot) {
        return slot == 2 ? IVORY : WHITE;
    }

    // 대기실 자리에 띄울 움직이는 그림
    public Image lobbyImage() {
        return new ImageIcon(movePath).getImage();
    }
}
