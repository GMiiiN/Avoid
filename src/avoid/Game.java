package avoid;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;
// 게임 규칙
public class Game {

    private int cnt;
    private int score;

    private boolean isOver;

    private ArrayList<Avoid> enemyList=new ArrayList<Avoid>();
    private Player player = new Player();
    private Avoid enemy;

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
    private void KeyProcess(boolean left, boolean right) {
    }

    private void enemyAppearProcess() {
        if(cnt%7==0) {
            enemy=new Avoid(0, 0);
            int maxX = Main.SCREEN_WIDTH - enemy.width;
            enemy.x = (int) (Math.random() * (maxX + 1));
            enemyList.add(enemy);


        }
    }

    private void enemyMoveProcess() {
        for(int i=0;i<enemyList.size();i++) {
            enemy=enemyList.get(i);
            enemy.move();

			/*
			if (enemy.x<playerX+playerWidth&&enemy.x+enemy.width>playerX&&
					enemy.y<playerY+playerHeight&&enemy.y+enemy.height>playerY) //플레이어가 똥에 맞을시
			{
	            enemyList.remove(enemy); //똥삭제
	        }
			*/
            if (enemy.x+15< player.getX()+player.getpWidth()&&enemy.x+enemy.width>player.getX()+15
                    &&enemy.y<player.getY()+player.getpHeight()-40&&enemy.y+enemy.height>player.getY()) //플레이어가 똥에 맞을시
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
            g.drawImage(enemy.image,enemy.x,enemy.y,null);
        }
    }

    public boolean isOver() {
        // TODO Auto-generated method stub
        return isOver;
    }
}