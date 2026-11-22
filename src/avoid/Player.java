package avoid;

import javax.swing.*;
import java.awt.*;

// 플레이어 한명
public class Player {
    private Image walkImg = CharacterType.IVORY.moveImg;
    private Image standImg = CharacterType.IVORY.standImg;

    private boolean moving = false, facingRight = true; // 원본 그림이 왼쪽을 보고 있음.

    static final int SIZE = 80;
    private int x, y;
    private final int pWidth = SIZE;
    private final int pHeight = SIZE;

    static final int HIT_LEFT = 20, HIT_TOP = 8, HIT_WIDTH = 40, HIT_HEIGHT = 64;

    private int pSpeed = 8;

    static final int MAX_HP = 3; // 최대체력
    static final int INVINCIBLE_TICKS = 50;
    private int hp;
    private int invincibleTicks;

    public void reset(){
        x = (Main.SCREEN_WIDTH - pWidth) / 2;
        y = (Main.SCREEN_HEIGHT - pHeight) - 10;
        hp = MAX_HP;
        invincibleTicks = 0;
    }

    public void move(boolean left, boolean right){
        moving = left != right;
        if (right && !left) facingRight = true;
        if (left && !right) facingRight = false;

        if (left && x - pSpeed > 0)
            x -= pSpeed;
        if (right && x + pWidth + pSpeed < Main.SCREEN_WIDTH)
            x += pSpeed;
        if(invincibleTicks > 0){
            invincibleTicks--;
        }
    }

    public void draw(Graphics g){
        if (isInvincible() && (invincibleTicks / 5) % 2 == 0) return;
        Image img = moving ? walkImg : standImg;
        if (facingRight)
            g.drawImage(img, x + SIZE, y, -SIZE, SIZE, null);
        else
            g.drawImage(img, x, y, SIZE, SIZE, null);
    }
    // 자리 번호에 맞는 캐릭터로 바꾸기 (1번 하얀색, 2번 아이보리)
    public void setCharacter(CharacterType type){
        walkImg = type.moveImg;
        standImg = type.standImg;
    }

    // 상대 캐릭터: 받은 x좌표로 옮기고, 움직인 방향으로 얼굴을 돌림
    public void setRemoteX(int nx){
        moving = nx != x;
        if (nx > x) facingRight = true;
        if (nx < x) facingRight = false;
        x = nx;
    }
    public Rectangle getBounds(){
        return new Rectangle(x + HIT_LEFT, y + HIT_TOP, HIT_WIDTH, HIT_HEIGHT);
    }
    // 피격 범위 판정
    public void hit(){ // 피격
        if(hp > 0){
            hp--;
            invincibleTicks = INVINCIBLE_TICKS;
        }
    }
    public boolean isInvincible(){ //무적판정 틱 / true면 무적, false면 무적 끝
        return invincibleTicks > 0;
    }
    public boolean isDead(){ //사망판정
        return hp <= 0;
    }
    public int getHp() {
        return hp;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getpWidth() {
        return pWidth;
    }
    public int getpHeight() {
        return pHeight;
    }
    public int getpSpeed() {
        return pSpeed;
    }
    public void setpSpeed(int pSpeed) {
        this.pSpeed = pSpeed;
    }

}
