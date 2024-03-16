package TankWar30;

import java.awt.*;
import java.util.Vector;

public class Bullet implements Runnable {
    private int x;
    private int y;
    private final int direct;
    private int speed;//定义子弹的x,y坐标和方向和速度，可以通过坦克位置获取
    private final int bulletSize = 10;
    private boolean live = true;
    private final Vector<Obstacle> obstacles;
    private Bomb bomb;

    public Bullet(int x, int y, int direct, int speed, Vector<Obstacle> obstacles) {
        this.x = x;
        this.y = y;
        this.direct = direct;
        this.speed = (int) (speed * 1.5);
        if (this.speed > 12) {
            this.speed = 12;
        }
        this.obstacles = obstacles;
    }

    public void draw(Graphics g) {
        g.fillOval(x - bulletSize / 2, y - bulletSize / 2, bulletSize, bulletSize);
    }

    //子弹的线程
    @Override
    public void run() {
        while (live) {
            while (GamePanel.pause) {
                try {
                    Thread.sleep(100);//画面延迟必须是最小的
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //System.out.println("子弹线程");
            try {
                Thread.sleep(GamePanel.time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //根据方向来发射炮弹
            switch (direct) {
                case 0 -> y -= speed;
                case 1 -> x += speed;
                case 2 -> y += speed;
                case 3 -> x -= speed;
            }
            //System.out.println("子弹的位置x="+x+"y="+y);//测试代码
            //子弹超出边界退出子弹线程,或子弹因撞击死亡(注意子弹对象也要销毁)
            if (x > GamePanel.width || x < 0 || y > GamePanel.height || y < 0) {
                live = false;//销毁子弹，（对象仍在，但所有生命特征都消失了，直到被new）
            }
            //解决遍历集合在集合内部移除集合的报错（迭代器法）
            Obstacle obstacle;
            //Iterator<Obstacle>iterator=obstacles.iterator();
            //使用同步锁或迭代器互斥解决遍历元素的索引问题
            for (int i = obstacles.size() - 1; i >= 0; i--) {
                obstacle = obstacles.get(i);
                if (obstacle instanceof Wall) {
                    if (this.getRect().intersects(obstacle.getRect())) {
                        setBomb(new Bomb(x, y));
                        setLive(false);
                        break;
                    }
                } else if (obstacle instanceof FragileWall) {
                    if (this.getRect().intersects(obstacle.getRect())) {
                        //这里的强转，就是在提示使用泛型
                        if (((FragileWall) obstacle).isHit(this)) {
                            setLive(false);
                            if (((FragileWall) obstacle).fragileWalls.size() == 0)
                                obstacles.remove(obstacle);
                            break;
                        }

                    }
                }
            }
        }
    }

    public Bomb getBomb() {
        return bomb;
    }

    public void setBomb(Bomb bomb) {
        this.bomb = bomb;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public Rectangle getRect() {//打的稍微入一点，擦边死太惨了
        return new Rectangle(x - bulletSize / 4, y - bulletSize / 4, bulletSize / 2, bulletSize / 2);
    }
}
