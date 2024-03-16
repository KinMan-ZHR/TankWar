package TankWar30;

import java.awt.*;
import java.util.Random;
import java.util.Vector;

public class BirthPlace extends Picture {
    private int x;
    private int y;
    private final int width = 5 * sWidth / 4;
    private final int height = 5 * sHeight / 4;
    /**
     * 注意这里Walls,不参与画图与碰撞，单纯为BirthPlace的组件,将其添加至GamePanel里
    */

    private final Vector<Wall> walls = new Vector<>();
    private final CoolDownTime cool;

    public BirthPlace(int x, int y) {
        this.x = x;
        this.y = y;
        cool = new CoolDownTime(10 * 1000);//10s后开始生产
        birthWall();
    }

    public void birthWall() {
        //边线切割位(绕切法)
        Wall wall;
        //放缩倍率zoom(指的的是相对源width,sHeight，不代表精度，决定在画面中实际大小位置，方便布局)
        int zoom = 4;//缩小为之前的1/4
        int dxFence = sWidth / zoom;//相当于width
        int dyFence = sHeight / zoom;
        //1列2-6行
        wall = new Wall(x - width / 2 - dxFence / 2, y - height / 2 - dyFence / 2 + dyFence, dxFence, dyFence, 5, 1);
        walls.add(wall);
        //7列2-6行
        wall = new Wall(x - width / 2 - dxFence / 2 + 6 * dxFence, y - height / 2 - dyFence / 2 + dyFence, dxFence, dyFence, 5, 1);
        walls.add(wall);
        //1行1-7列
        wall = new Wall(x - width / 2 - dxFence / 2, y - height / 2 - dyFence / 2, dxFence, dyFence, 1, 7);
        walls.add(wall);
    }

    public void draw(Graphics gOffScreen) {
        gOffScreen.drawImage(img, x - width / 2, y - height / 2, x + width / 2, y + height / 2, 19 * sWidth, 5 * sHeight, 20 * sWidth, 6 * sHeight, null);
    }

    public TankEnemy createTankEnemy(boolean brawl) {
        TankEnemy tankEnemy;
        Random random = new Random();
        cool.setCoolDown(false);//只有产生了才重新启动线程
        cool.setCoolDownTime((int) (Math.random() * 10 + 5) * 1000);
        cool.resumeThread();
        //这种方法是通过线程冷却，在这种情况下，至多10s也不一定会产生一个坦克
        if (brawl) {
            if ((int) (Math.random() * 3) == 0) {
                tankEnemy = new TankEnemy(x, y, 2, Math.abs(random.nextInt()) % 2 + 1, Math.abs(random.nextInt()) % 3 + 1, true, false);
                return tankEnemy;
            } else {
                return null;
            }
        } else {//不是乱斗，产生要再慢一点
            if ((int) (Math.random() * 6) == 0) {
                tankEnemy = new TankEnemy(x, y, 2, Math.abs(random.nextInt()) % 2 + 1, Math.abs(random.nextInt()) % 3 + 1, true, false);
                return tankEnemy;
            }
            return null;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Rectangle getRect() {
        return new Rectangle(x - width / 2, y - height / 2, width, height);
    }

    public Vector<Wall> getWalls() {
        return walls;
    }

    public CoolDownTime getCool() {
        return cool;
    }

}


