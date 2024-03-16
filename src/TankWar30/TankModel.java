package TankWar30;

import java.awt.*;
import java.util.Vector;
public class TankModel  extends Picture {
    //属性，坦克的位置信息
    private int x;//横坐标
    private int y;//纵坐标
    private int direct;//方向
    private int speed;//速度
    private int category;//种类
    private int level;//等级
    private int Hp;//生命
    //定义一个子弹发射的冷却时间
    private CoolDownTime cool;
    //坦克的存活信息
    private boolean selfLive =true;
    //坦克所有方法的调用信息(专门针对子弹)
    private boolean allLive=true;
    //先来子弹一个集合
    Vector<Bullet> bullets=new Vector<>();
    //子弹数量
    private int bulletNum;
    //再定义一个子弹个体（方便new然后add）(这里的bullet不再是一个真的子弹，本应放入函数体内)
    private Bullet bullet;
    //更换图片表现行进
    private  boolean flag=true;
    private boolean isMove=false;
    //切割起点
    private int sx = 0;
    private int sy = 0;
    //画出的图形长宽（此即为坦克大小）
    private final int width=34;
    private final int height =34;
    //定义一个变量判断是初始化调用levelAttribute,还是levelUp调用
    private boolean init=false;
    private final boolean auto;
    //初始化构造器（实体）
    public TankModel(int x, int y, int direct, int category, int level,boolean auto) {
        this.x = x;
        this.y = y;
        this.direct=direct;
        this.category = category;
        this.level=level;
        this.auto = auto;
        levelAttribute();
        categoryAttribute();
    }
    public void drawTank(Graphics g){
        Font font=g.getFont();
       g.setFont(new Font("楷体",Font.BOLD,20));
        Color color=g.getColor();
        //为字体取色
        g.setColor(Color.gray);
        if(flag) {
            g.drawImage(img,x-width/2,y- height /2,x+width/2,y+ height /2, sx, sy, sx + sWidth, sy + sHeight,null);
            g.drawString("Hp:"+Hp+" lv:"+level,x-width,y- height /2);
            //每一个方向源图切割点向右移动两位
        }else {
            g.drawImage(img,x-width/2,y- height /2,x+width/2,y+ height /2, sx + sWidth, sy, sx + sWidth *2, sy + sHeight,null);
            g.drawString("Hp:"+Hp+" lv:"+level,x-width,y- height /2);
        }
        //用完就换回去不然会引起冲突
        g.setColor(color);
        g.setFont(font);
        if(isMove){
            flag=!flag;
            isMove=false;
        }
    }
    //注意这里的子弹方法是又tank类调用的，所以坦克一旦没了子弹自动消亡
    public void drawTankWithBullets(Graphics g){
        if (this instanceof TankPlayer) {
            //为子弹取色
            g.setColor(Color.orange);
        }else if (this instanceof TankEnemy) {
            //为子弹取色
            g.setColor(Color.green);
        }
        if (isSelfLive()) {
            drawTank(g);
        }
        //画出其所有的子弹（每个坦克都有多个子弹）(死坦克还是可以有生前的子弹的)
            for (int j = bullets.size()-1; j >=0; j--) {
                //取出子弹
                Bullet bullet = bullets.get(j);
                //检查一下
                if(!bullet.isLive()){
                    bullets.remove(bullet);
                }
                //绘制子弹
                bullet.draw(g);
            }
    }
    //升级
    private void levelUp(){
        if(level<3){//最高三级
            level++;
            if(this.getHp()<3)
            this.setHp(this.getHp()+1);//升级加1条命
        }else{//当已三级时
            if(category<2){
                category++;
                categoryAttribute();
                level=1;
            }

        }

        //之后用switch语句设置不同等级的基础属性值
        levelAttribute();
    }
    //种类设定
    private void categoryAttribute(){
        if (this instanceof TankPlayer) {
            switch (category) {
                case 1 -> sy = 0;
                case 2 -> sy = sHeight * 11;
                default -> {
                }
            }
        }else if (this instanceof TankEnemy) {
            switch (category) {
                case 1 -> sy = sHeight;
                case 2 -> sy = sHeight * 12;
                default -> {
                }
            }
        }
    }
    //图片切割点x的计算
    public void calImageX(){
        sx=sWidth*8*(level-1) + sWidth*2*direct;
    }
    //等级设定
    private void levelAttribute(){
        calImageX();
        if(category==1){
            if(this instanceof TankEnemy)
                ((TankEnemy) this).setRandomCool(5);//在1到5乘以基础值发射
        switch (level) {
            case 1 -> {
                this.setSpeed(2);
                this.setBulletNum(2);
                //用于判断是否初始化，之后的升级调用不应当使生命值提升
                if(!init)
                    this.setHp(2);
            }
            case 2 -> {
                this.setSpeed(3);
                this.setBulletNum(2);
                if(!init)
                    this.setHp(2);
            }
            case 3 -> {
                this.setSpeed(4);
                this.setBulletNum(3);
                if(!init)
                    this.setHp(3);
            }
        }
        }
        if(category==2){
            //若是敌方则加快子弹射击频率
            if(this instanceof TankEnemy)
                ((TankEnemy) this).setRandomCool(3);//在1到3乘以基础值发射
        switch (level) {
            case 1 -> {
                this.setSpeed(2);
                this.setBulletNum(3);
                //用于判断是否初始化，之后的升级调用不应当使生命值提升
                if(!init)
                    this.setHp(2);
            }
            case 2 -> {
                this.setSpeed(3);
                this.setBulletNum(4);
                if(!init)
                    this.setHp(2);
            }
            case 3 -> {
                this.setSpeed(4);
                this.setBulletNum(5);
                if(!init)
                    this.setHp(3);
            }
        }
        }
        //初始化判定
        init=true;
    }
   private final int  safeDistance=width*width+ height * height;//直径的平方
    //最小距离判定机制(解决collide范围内的问题)
    public int minDistance(TankPlayer tankPlayer, Vector<TankEnemy> tankEnemies){
        int distance;
        int minDistance =safeDistance;//这里最小距离平方最大值返回的就是safeDistance(也是距离判断的最大限值)
        for (TankEnemy tankEnemy : tankEnemies) {
            //先和我方坦克比较
            if (this != tankPlayer && tankPlayer.isSelfLive()) {//活着才比，敌人的靠集合判断的存活
                distance = (int) (Math.pow(this.x - tankPlayer.getX(), 2) + Math.pow(this.y - tankPlayer.getY(), 2));
                if (distance < minDistance)
                    minDistance = distance;
            }
            if (tankEnemy != this) {
                //判断距离
                distance = (int) (Math.pow(this.x - tankEnemy.getX(), 2) + Math.pow(this.y - tankEnemy.getY(), 2));
                if (distance < minDistance)
                    minDistance = distance;
            }
        }

        return minDistance;
    }
    //碰撞判定机制（包含我与敌方）由于实际两类对象所以只得传参解决（这样也可以不用获取其他类的创建的数据）
    public boolean collide(TankPlayer tankPlayer, Vector<TankEnemy> tankEnemies) {
        //return minDistance(tankPlayer, tankEnemies) < safeDistance;（圆形碰撞模型）
        //一下为方形模型代码
        TankEnemy tankEnemy;
        for (int i=tankEnemies.size()-1;i>=0;i--) {
            tankEnemy=tankEnemies.get(i);
            //先和我方坦克比较
            if (this != tankPlayer && tankPlayer.isSelfLive() && this.getRect().intersects(tankPlayer.getRect())) {//活着才比，敌人的靠集合判断的存活
                return true;
            }
            if (this != tankEnemy && tankEnemy.isSelfLive() && this.getRect().intersects(tankEnemy.getRect())) {
                return true;
            }
        }
            return false;
        }
    //本来可以和minDistance做成一个函数，但是这样平常与坦克碰撞时，其最近参考会收wall影响
    //分成两个函数可以方便控制移动优先级
    private int minDistanceWithWall(Vector<Obstacle>obstacles){
        int distance;
        int minDistance=safeDistance/4;
        Obstacle obstacle;
        int x,y;
        for (int i=0;i<obstacles.size();i++) {
            obstacle=obstacles.get(i);
            x = obstacle.getX() + (obstacle.getCols() - 1) * obstacle.getWidth() / 2;
            y = obstacle.getY() + (obstacle.getRows() - 1) * obstacle.getHeight() / 2;
            distance = (int) (Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2));
            if (distance < minDistance) {
                minDistance = distance;
            }
        }
        return minDistance;
    }
    private boolean collideWithWall(Vector<Obstacle> obstacles){
        for (int i=obstacles.size()-1;i>=0;i--) {
            Obstacle obstacle=obstacles.get(i);
            if (obstacle.getRect().intersects(this.getRect())){
                if(obstacle instanceof FragileWall){//易碎墙单元判定
                       if (((FragileWall) obstacle).isHit(this)){
                           return true;
                       }
                }else return true;
            }
        }
        return false;
    }

    //方法,行为
    //移动
    public void move(boolean positive){
        switch (direct){
            case 0:  if (positive)
                y -= speed;
            else y+=speed;break;
            case 1:  if(positive)
                x += speed;
            else x-=speed;break;
            case 2:if (positive)
                y += speed;
            else y-=speed;break;
            case 3:  if(positive)
                x -= speed;
            else x+=speed;break;
        }
            isMove= true;
    }
    private void move(){//保证不会主动冲进墙内
        move(true);
        //绝对不准冲击静态物体（所以直接回来）
        if (y- height / 2 <= 0
                ||x + width / 2 >= GamePanel.width||y + height *3/2>= GamePanel.height
                ||x - width/ 2 <= 0) {//保证不会主动穿过去
            move(false);
            if (auto)//如果是自动行进的(不用TankEnemy是为了我方行动自动化作准备)
                setDirect((int)(Math.random()*4));
        }
    }
    public void move(Vector<Obstacle> obstacles){
        //若卡进移动墙里（意外发生）
        if(collideWithWall(obstacles)){//类似escape方法（可惜玩家和敌人坦克的方向改变方法不同）
            //记录当前距离
            int distance=minDistanceWithWall(obstacles);
            move();
            if(distance>minDistanceWithWall(obstacles)) {
                move(false);
                if (auto)//如果是自动行进的(不用TankEnemy是为了我方行动自动化作准备)
                    setDirect((int)(Math.random()*4));//因为direct和x坐标计算有关系，所以用的函数体
            }
        }else {//正常情况与moveWithBorder极为相似（保证不会主动卡进墙内）
            move();//同时保证不会卡进边界中
            if(collideWithWall(obstacles)) {//正常情况wall和border等价不得僭越
                move(false);
                if (auto)//如果是自动行进的(不用TankEnemy是为了我方行动自动化作准备)
                    setDirect((int)(Math.random()*4));
            }
        }


    }

    //射击行为
    //所有的shot都应当受一个coolDown调控，当一个coolDown消亡时才能再发射
    public void Shot( Vector<Obstacle> obstacles){
        cool.setCoolDown(false);
        cool.resumeThread();//记得唤醒线程使得false会到true
        //如果存在子弹超出了bulletNum，则无法射出多余子弹
        if(bullets.size()<bulletNum&& selfLive){
            //根据坦克的方向修正bullet的初始位置
            switch (direct) {
                case 0 -> bullet = new Bullet(x , y- height /2, 0, speed,obstacles);
                case 1 -> bullet = new Bullet(x +width/2, y , 1, speed,obstacles);
                case 2 -> bullet = new Bullet(x , y + height /2, 2, speed,obstacles);
                case 3 -> bullet = new Bullet(x-width/2, y , 3, speed,obstacles);
            }
            //将创建的bullet纳入集合中
            bullets.add(bullet);
            //启动线程
            new Thread(bullet).start();
        }
    }
    //判断子弹击中并相应减少其生命值，提升射出方的等级
    public void bulletHit(TankModel tank, Vector<Bomb> bombs) {
        if(this==tank||!tank.selfLive||!this.allLive){//判断射击对象是否为自己,以及对象和自己是否活着
            return;
        }
        for (int j = bullets.size()-1; j >=0 ; j--) {
            Bullet bullet = bullets.get(j);
            //因为不知道我方的哪一颗子弹会击中敌方
            //一个子弹一个敌人
            //顺便处理子弹自身的问题，不是由于打到敌人产生的（因为该方法在GamePanel中）
            if(!bullet.isLive()) {
                //从Vector移除
                bullets.remove(bullet);
                if (bullet.getBomb() != null) {//该子弹有爆炸所以无了（统一在画图区消除它）
                bombs.add(bullet.getBomb());
                }
                continue;
            }
            if (bullet.getRect().intersects(tank.getRect())) {
                //击中，销毁子弹
                bullet.setLive(false);
                tank.setHp(tank.getHp() - 1);
                //击中了，添加bomb
                Bomb bomb = new Bomb(tank.getX(), tank.getY());
                bombs.add(bomb);
                //从Vector移除
                bullets.remove(bullet);
                //生命值为零
                if (tank.getHp() == 0) {
                    //设置其死亡（其相应的冷却线程同时死亡）
                    tank.setSelfLive(false);
                    //我方升级
                    this.levelUp();
                    //统计被我方击杀死去的敌人
                    if (this instanceof TankPlayer && tank instanceof TankEnemy) {
                        Recorder.addAllEnemyNum();
                    }
                }
            }
        }
        //自己无了，判断是否子弹还在
        if(!this.selfLive&&bullets.size()==0)
        {
            this.setAllLive(false);
        }
    }
    public Rectangle getRect(){
        return new Rectangle(x-width/2,y-height/2,width,height);
    }
    public int getDirect() {return direct;}
    public void setDirect(int direct) {this.direct = direct;calImageX();}
    public int getX() {return x;}
    public void setX(int x) {this.x = x;}
    public int getY() {return y;}
    public void setY(int y) {this.y = y;}
    public int getCategory() {return category;}
    public int getHp() {return Hp;}
    public int getLevel() {return level;}
    public int getSpeed() {return speed;}
    public void setSpeed(int speed) {this.speed = speed;}
    public void setHp(int hp) {Hp = hp;}
    public boolean isSelfLive() {return selfLive;}
    public void setSelfLive(boolean selfLive) {this.selfLive = selfLive;
    if(!selfLive){
        cool.setLive(false);
    }
    }

    public CoolDownTime getCool() {
        return cool;
    }

    public void setCool(CoolDownTime cool) {
        this.cool = cool;
    }

    public boolean isAllLive() {

        return allLive;
    }

    public void setAllLive(boolean allLive) {
        this.allLive = allLive;
        if(!allLive){
            for (int i=bullets.size()-1;i>=0;i--){
                bullets.get(i).setLive(false);
            }
            bullets.clear();
        }
    }
    public void setBulletNum(int bulletNum) {this.bulletNum = bulletNum;}
}

