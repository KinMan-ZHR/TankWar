package TankWar30;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

//字段只会根据实例变化而变化，不像函数体中调用一次就还原了
public class Bomb {
    private final int x;
    private final int y;
    private int life = 15;//炸弹生命周期
    private int bombPictureMove = 0;
    private final int flag = 2;//每x次绘画移动图片
    private int pictureWait = flag;
    private boolean live = true;
    //定义三张图片用于实现bomb效果
    private static final Image image = Toolkit.getDefaultToolkit().createImage(GamePanel.class.getResource("/bomb.jpg"));
    //private static final Image image = Toolkit.getDefaultToolkit().getImage(GamePanel.class.getResource("/bomb.jpg"));
   // private static final Image image = Toolkit.getDefaultToolkit().getImage(GamePanel.class.getClassLoader().getResource("/bomb.jpg"));
    //根据bomb的生命周期，画出对应的图片
    // 定义一个图片切割点位
    private final int sx1 = 74, sy1 = 15;
    //定义下一个切割图片的移动距离
    private final int dx = 59, dy = 58;
    //定义一个存储坐标
    private int sx = sx1, sy = sy1;

    public Bomb(int x, int y) {
        this.x = x;
        this.y = y;
        //初始化图片对象(得看是在哪里调用的)
    }

    //面板每40ms(约为24fps/s)调用一次draw
    //目的缓慢播放（调用三次动一下）
    public void drawBomb(Graphics g) {
        pictureWait--;
        //根据bomb的生命周期，画出对应的图片
        if (bombPictureMove <= 6)
            sx = sx1 + bombPictureMove * dx;//从原图片第一排扣7个图
        if (bombPictureMove > 6 && bombPictureMove <= 12) {
            sx = sx1 + (bombPictureMove - 6) * dx;//从原图片第二排扣3个图
            sy = sy1 + dy;
        }
        if (bombPictureMove > 12) {
            sx = sx1 + (bombPictureMove - 12) * dx;//从原图片第二排扣3个图
            sy = sy1 + dy * 2;
        }
        g.drawImage(image, x - 17, y - 17, x + 17, y + 17, sx, sy, sx + 32, sy + 32, null);
        //让炸弹生命值减少（属于炸弹的一种方法）
        if (life > 0 && pictureWait == 0) {//判断冷却
            bombPictureMove++;
            pictureWait = flag;
            life--;
        }
        //不要动不动就用else（用之前一定要仔细斟酌）
        if (life == 0) {
            setLive(false);
        }
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }
}

