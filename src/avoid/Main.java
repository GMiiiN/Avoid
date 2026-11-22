package avoid;

public class Main {

    public static final int SCREEN_WIDTH=1080;	//스크린 가로크기
    public static final int SCREEN_HEIGHT=740;	//스크린 세로크기

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(AGame::new);
    }
}