package TankWar30;

import java.util.Vector;

public class TankEnemy extends TankModel implements Runnable{
    //为了让tankEnemy可以得到其集合的所有坦克和玩家的位置信息
    //注意其Vector和TankPlayer是在GamePanel里的对象
    //需要walls,tankEnemies,tankPlayer的信息才能跑
    private Vector<TankEnemy>tankEnemies=new Vector<>();
    private TankPlayer tankPlayer=null;
    private Vector<Obstacle> obstacles =new Vector<>();
    private int randomCool=5;
    private int rdDirCoolCount =0;
    private final int coolStaticTime=750;
    //构造器
    public TankEnemy(int x, int y,int direct,int category,int level,boolean auto,boolean isIcon) {
        super(x, y, direct, category, level, auto);
        if (!isIcon) {
            setCool(new CoolDownTime(1000));
            if(auto)
            new Thread(this).start();
        }
    }
    public void setObstacles(Vector<Obstacle> obstacles) {this.obstacles = obstacles;}
    public void setTankEnemies(Vector<TankEnemy> tankEnemies) {
        this.tankEnemies = tankEnemies;
    }
    public void setTankPlayer(TankPlayer tankPlayer){
        this.tankPlayer=tankPlayer;
    }
//碰撞逃离机制（也可以用stay机制）
public void moveAutoFinal() {
        //主要逻辑：尝试逃跑，判断collide(),成功就执行该操作，否则就退回来，再次进行尝试
    if (collide(tankPlayer,tankEnemies)){//一旦撞上了
        int  distance=minDistance(tankPlayer,tankEnemies);
        //准备处理事件
        setDirect ((int) (Math.random() * 4));
        //行动前得设置方向(为了有效地逃离，不做无谓的判断，和盲目的改向)
        while (collide(tankPlayer,tankEnemies)) {
        move(obstacles);//一直按这个方向走
        if (distance>minDistance(tankPlayer,tankEnemies)) {//不对劲就换向
            move(false);
            setDirect((int) (Math.random() * 4));
        }
            try {
                Thread.sleep(GamePanel.time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    else {//正常情况
        move(obstacles);
        rdDirCoolCount++;
        if (collide(tankPlayer, tankEnemies))//保证不主动撞
            move(false);
        if(rdDirCoolCount ==50){//再随机一点吧
            //随机改变方向0-3的整数
            setDirect((int)(Math.random()*4));
            rdDirCoolCount =(int)(Math.random()*30);//取0-29的数
        }
    }
    }
@Override
    public void run() {
        while (isSelfLive()){
            while (GamePanel.pause){
                try {
                    Thread.sleep(100);//画面延迟必须是最小的
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
           // System.out.println("坦克线程");
            if(getCool().isCoolDown()){//冷却好了
                //变换冷却时间（发射一次随机一次）
                Shot(obstacles);
                getCool().setCoolDownTime((int) (Math.random() * randomCool+1) * coolStaticTime);
            }
            moveAutoFinal();
            //休眠50毫秒(现实的速度则是speed/25ms,因为这个speed是位移量)
            try {
                Thread.sleep(GamePanel.time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void setRandomCool(int randomCool) {
        this.randomCool = randomCool;
    }
}
