package avoid;
import java.awt.*;
import java.util.ArrayList;

// 게임 규칙
public class Game {

    private int cnt;
    private int score;

    private boolean isOver;

    private ArrayList<Obstacle> enemyList=new ArrayList<Obstacle>();
    private Player player = new Player();
    private Obstacle enemy;

    public Game(){
        reset();
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
    public void reset() {	//다시하기 기능
        isOver=false;
        cnt = 0;
        score=0;
        player.resetPosition();
        enemyList.clear();

    }
    private void enemyAppearProcess() {
        if(cnt%7==0) {
            enemy=new Obstacle(0, 0);
            int maxX = Main.SCREEN_WIDTH - enemy.width;
            enemy.x = (int) (Math.random() * (maxX + 1));
            enemyList.add(enemy);


        }
    }
    private void enemyMoveProcess() {
        for(int i=0;i<enemyList.size();i++) {
            enemy=enemyList.get(i);
            enemy.move();
            if (enemy.getBounds().intersects(player.getBounds())) //플레이어가 똥에 맞을시
            {
                enemyList.remove(enemy); //똥삭제
                isOver=true;
            }
        }
    }
    public void gameDraw(Graphics g) {
        player.draw(g);
        enemyDraw(g);
        infoDraw(g);
    }

    public void infoDraw(Graphics g) {	//점수표와 게임이 끝났을때 안내표
        g.setColor(Color.RED);
        g.setFont(new Font("Arial",Font.BOLD,20));
        g.drawString("SCORE : "+score,250,60);
        if(isOver) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial",Font.BOLD,40));
            g.drawString("Press Enter to restart",40,200);
        }
    }

    public void enemyDraw(Graphics g) {
        for(int i=0;i<enemyList.size();i++) {
            enemy=enemyList.get(i);
            g.drawImage(enemy.image,enemy.x,enemy.y,enemy.width, enemy.height,null);
        }
    }

    public boolean isOver() {
        // TODO Auto-generated method stub
        return isOver;
    }
}