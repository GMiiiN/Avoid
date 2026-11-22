package avoid;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

// 게임 규칙
public class Game {
    // 게임 점수 관련
    private int cnt;
    private int score;
    private boolean isOver;
    // 플레이어 및 개체관련.
    private ArrayList<Obstacle> enemyList=new ArrayList<Obstacle>();
    private Player player = new Player();
    private Obstacle enemy;
    private Random random;
    static final long TEST_SEED = 12345L;

    // 상대 플레이어 (서버에서 받은 위치·체력만 저장, 화면에는 안 그림)
    private Player opponent = new Player();
    private boolean hasOpponentPos;
    private int opponentHp;

    public Game(){
        reset(TEST_SEED);
    }

    public void update(boolean left, boolean right){
        if(isOver){
            return;
        }
        enemyAppearProcess();
        enemyMoveProcess();
        player.move(left, right);
        cnt++;
        score++;
    }
    public void reset(long seed) {	//다시하기 기능
        random = new Random(seed);
        isOver=false;
        cnt = 0;
        score=0;
        player.reset();
        enemyList.clear();
        opponent.reset();
        hasOpponentPos = false;
        opponentHp = Player.MAX_HP;

    }
    private void enemyAppearProcess() {
        if(cnt%7==0) {
            enemy=new Obstacle(0, 0);
            int maxX = Main.SCREEN_WIDTH - enemy.width;
            enemy.x = random.nextInt(maxX + 1);
            enemyList.add(enemy);


        }
    }
    private void enemyMoveProcess() {
        for(Iterator<Obstacle> it = enemyList.iterator(); it.hasNext();){
            Obstacle enemy = it.next();
            enemy.move();

            if (enemy.y > Main.SCREEN_HEIGHT) {
                it.remove();
                continue;
            }
            if(!player.isInvincible() && enemy.getBounds().intersects(player.getBounds())){
                it.remove();
                player.hit();
                if(player.isDead()){
                    isOver = true;
                }
            }
        }
    }
    public void gameDraw(Graphics g) { // 화면에는 나만 그림 (상대 위치는 받기만 함)
        player.draw(g);
        enemyDraw(g);
        infoDraw(g);
    }

    public void infoDraw(Graphics g) {	//점수표와 게임이 끝났을때 안내표
        g.setColor(Color.RED);
        g.setFont(new Font("Arial",Font.BOLD,20));
        drawCentered(g, "SCORE : "+score, 60);
        g.drawString("HP : " + player.getHp(), Main.SCREEN_WIDTH - 80, 60);
        if(isOver) { // 목숨 3개를 다 잃으면 끝 (Enter → 대기실)
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial",Font.BOLD,40));
            FontMetrics fm = g.getFontMetrics(); // 40 폰트 기준 치수
            int centerY = (Main.SCREEN_HEIGHT - fm.getHeight()) / 2 + fm.getAscent();
            drawCentered(g, "Press Enter to restart", centerY);
        }
    }

    // ---- 네트워크에서 받은 상대 정보 ----
    public void setMySlot(int slot) {
        player.setCharacter(CharacterType.ofSlot(slot));
        opponent.setCharacter(CharacterType.ofSlot(slot == 1 ? 2 : 1));
    }

    public void setOpponentX(int x) {
        opponent.setRemoteX(x);
        hasOpponentPos = true;
    }

    public void setOpponentHp(int hp) {
        opponentHp = hp;
    }

    public int getPlayerX() {
        return player.getX();
    }

    public int getHp() {
        return player.getHp();
    }

    public int getScore() {
        return score;
    }

    private void drawCentered(Graphics g, String text, int y) { // 가로 가운데 정렬
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, (Main.SCREEN_WIDTH - fm.stringWidth(text)) / 2, y);
    }

    public void enemyDraw(Graphics g) {
        for(int i=0;i<enemyList.size();i++) {
            enemy=enemyList.get(i);
            g.drawImage(Obstacle.image,enemy.x,enemy.y,enemy.width, enemy.height,null);
        }
    }

    public boolean isOver() {
        // TODO Auto-generated method stub
        return isOver;
    }
}