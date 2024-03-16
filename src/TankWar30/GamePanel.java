package TankWar30;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.Random;
import java.util.Vector;

public class GamePanel extends JPanel implements KeyListener, Runnable {//类继承窗口函数
    //当该线程停止时全部停止(游戏状态)
    static boolean pause = false;
    static final int time = 40;
    //这个控制应当只属于一个对象的，不应该上升到类
    boolean live = true;
    //游戏模式（大乱斗或常规模式）
    boolean brawl = true;//这里默认的是大乱斗的模式
    //出生点随机出现，一段时间后消失
    boolean randBirth = true;
    boolean randFragile = true;
    //随机冷却
    private int coolCountBirth;
    private int coolCountFragile;
    //定义一个node的集合来初始化敌人坦克
    private Vector<Node> nodes = new Vector<>();
    //定义一个敌人坦克做图标用
    private final TankEnemy enemyIcon = new TankEnemy(1040, 90, 0, 1, 1, false, true);
    //定义我的坦克
    private TankPlayer tankPlayer;
    //定义敌人坦克，放入Vector集合中进行多线程操作；
    private final Vector<TankEnemy> tankEnemies = new Vector<>();
    //定义一个Vector存放炸弹
    //当子弹击中坦克时添加bomb对象
    private final Vector<Bomb> bombs = new Vector<>();
    //出生点(初始化时创建)
    private Vector<BirthPlace> birthPlaces = new Vector<>();
    //障碍物
    private final Vector<Obstacle> obstacles = new Vector<>();
    //遮蔽物
    private final Vector<HayStack> hayStacks = new Vector<>();
    //音乐
    private WavMusic wavMusic;
    //面板属性
    static int width = GameWindow.width - 200, height = GameWindow.height;
    //双缓存技术
    private Image offScreenImage = null;
    private Graphics gOffScreen = null;

    public GamePanel(String key) {
        constructStage(key);
    }

    private void constructStage(String level) {
        //重开与续盘
        //判断记录文件是否存在（在jar包中无法通过newFile来解决）
        URL game = Recorder.getRecordFilePath();
        //获取文件数据
        if (game!=null) {
            //只有当选择续盘时才判断文件是否完整
            if (Objects.equals(level, "R")) {
                Recorder.readRecord(true);
                if (!Recorder.isComplete()) {
                    System.out.println("上一局游戏已结束，自动开启新游戏");
                    //这里应该让文件记录上一局是哪一关
                    level = Recorder.getInitial();//让上一关重新开始
                }
            } else {
                Recorder.readRecord(false);
            }
            //文件存在才有资本选择何种种初始化
            switch (level) {
                //选择重开
                case "1" -> initial1();
                case "2" -> initial2();
                //选择续盘
                case "R" -> {
                    nodes = Recorder.getNodes();
                    initialR();
                    LayAbsoluteScenario(Recorder.getInitial());
                }
                default -> {
                    JOptionPane.showMessageDialog(null, "你未选择正确的关卡", "TankWar", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);//这种打开失败的情况不会改变文件中的记录
                }
            }
        } else {
            System.out.println("无游戏数据，自动开启第一关游戏");
            initial1();
        }
        if (!Objects.equals(level, "R"))
            Recorder.setInitial(level);
        //GamePanel对象的tankEnemies,tankPlayer设置给recorder
        //因为recorder里都是static所以不需要实例化就可以直接通过类名访问
        //初始化之后再传
        Recorder.setTankPlayer(tankPlayer);
        Recorder.setTankEnemies(tankEnemies);
        Recorder.setBirthPlaces(birthPlaces);
        Recorder.setObstacles(obstacles);
        //辅助线程计算判定的
        JudgeThread judgeThread = new JudgeThread();
        judgeThread.start();
        //播放指定音乐
        wavMusic = new WavMusic("_- Blue Sky Athletics (Bonus Track).wav.wav");
    }

    @Override
    public void paint(Graphics g) {
        if (offScreenImage == null) {
            //第一次调用就创建一个图片
            offScreenImage = this.createImage(GameWindow.width, GameWindow.height);
            gOffScreen = offScreenImage.getGraphics();
        }
        Color origin = gOffScreen.getColor();
        //信息面板
        showInfo(gOffScreen);
        //游戏面板涂色(覆盖操作)
        gOffScreen.setColor(Color.black);
        gOffScreen.fillRect(0, 0, width, height);
        //死亡
        if (tankPlayer == null || !tankPlayer.isSelfLive()) {
            gOffScreen.setColor(Color.orange);
            Font font = new Font("楷体", Font.BOLD, 50);
            gOffScreen.setFont(font);
            gOffScreen.drawString("you died!!!", 400, 350);
        } else if (tankEnemies.size() == 0) {
            gOffScreen.setColor(Color.orange);
            Font font = new Font("楷体", Font.BOLD, 50);
            gOffScreen.setFont(font);
            gOffScreen.drawString("you win!!!", 400, 350);
        } else {
            //画出生点
            for (int i = 0; i < birthPlaces.size(); i++) {
                BirthPlace birthPlace = birthPlaces.get(i);
                birthPlace.draw(gOffScreen);
            }
            //墙
            for (int i = 0; i < obstacles.size(); i++) {
                Obstacle obstacle = obstacles.get(i);
                if (obstacle instanceof Wall)
                    obstacle.drawWall(gOffScreen);
                if (obstacle instanceof River)
                    obstacle.drawRiver(gOffScreen);
                if (obstacle instanceof FragileWall) {
                    for (int j = 0; j < ((FragileWall) obstacle).fragileWalls.size(); j++) {
                        ((FragileWall) obstacle).fragileWalls.get(j).drawFragileWall(gOffScreen);
                    }
                }
            }
            //画player坦克,活着与否由画的函数判定
            tankPlayer.drawTankWithBullets(gOffScreen);
            //画enemy坦克，存不存在靠的是集合的remove判定
            for (int i = tankEnemies.size() - 1; i >= 0; i--) {
                TankEnemy tankEnemy = tankEnemies.get(i);//在集合中获取一个实体，注意从0开始取（这是系统规定的）
                tankEnemy.drawTankWithBullets(gOffScreen);
                if (!tankEnemy.isAllLive()) {//全部引用都结束才移除
                    tankEnemies.remove(tankEnemy);
                }
            }
            //画遮盖物HayStack
            for (HayStack hayStack : hayStacks) {
                hayStack.drawGrass(gOffScreen);
            }

            //画爆炸(动态绘画原理)
            //根据for找到所有的bomb,并绘制当前bomb的状态
            for (int i = bombs.size() - 1; i >= 0; i--) {
                Bomb bomb = bombs.get(i);
                //画bomb，在drawBomb中改变其状态
                bomb.drawBomb(gOffScreen);
                //如果bomb生命结束就在集合中删除
                if (!bomb.isLive()) {
                    bombs.remove(bomb);
                }
            }
        }
        gOffScreen.setColor(origin);
        g.drawImage(offScreenImage, 0, 0, this);
    }

    private void fireResult() {
        //遍历所有的坦克
        for (int i = 0; i < tankEnemies.size(); i++) {
            TankEnemy tankEnemy = tankEnemies.get(i);
            //由于在碰撞系统中tankEnemy类已经获得了玩家坦克的信息
            tankEnemy.bulletHit(tankPlayer, bombs);
            tankPlayer.bulletHit(tankEnemy, bombs);
            //允许敌方坦克自相残杀
            if (brawl) {
                for (TankEnemy tankOtherEnemy : tankEnemies) {
                    tankEnemy.bulletHit(tankOtherEnemy, bombs);
                }
            }
        }
    }

    private void randBirthTank() {
        if (tankEnemies.size() > 10)
            return;
        Random random = new Random();
        int temp;
        //判断是哪一个出生地创生了一个坦克
        temp = Math.abs(random.nextInt()) % birthPlaces.size();
        if (birthPlaces.get(temp).getCool().isCoolDown()) {
            TankEnemy tankEnemy = birthPlaces.get(temp).createTankEnemy(brawl);
            if (tankEnemy != null) {
                createRealTank(tankEnemy);
            }
        }
    }

    private void randCreateFragile() {
        if (coolCountFragile == 24) {
            coolCountFragile = (int) (Math.random() * 12);
            if ((int) (Math.random() * 8) == 1) {//控制创生速度
                //创生一个出生地(上半场)(还应该判断是否会与其他出生地，墙等碰撞)
                int tempX = (int) (Math.random() * 850 + 50);
                int tempY = (int) (Math.random() * 600 + 50);
                createFragileWall(tempX, tempY, 34, 34, (int) (Math.random() * 3 + 1), (int) (Math.random() * 3 + 1));
            }
        }
    }

    private void randCreateDamageBirth() {
        if (!randBirth)
            return;
        if (coolCountBirth == 24) {
            coolCountBirth = (int) (Math.random() * 12);
            if ((int) (Math.random() * 10) == 1) {
                //创生一个出生地(上半场)(还应该判断是否会与其他出生地，墙等碰撞)
                int tempX = (int) (Math.random() * 800 + 100);
                int tempY = (int) (Math.random() * 200 + 100);
                if (createBirth(tempX, tempY) != null) {//创生成功，准备销毁
                    BirthPlace birthPlace;
                    //销毁其中之一的出生地(不能销毁新建的)
                    birthPlace = birthPlaces.get((int) (Math.random() * (birthPlaces.size() - 1)));
                    ruinBirth(birthPlace);
                }
            }
        }
    }

    private FragileWall createFragileWall(int x, int y, int width, int height, int rows, int cols) {
        FragileWall wall = new FragileWall(x, y, width, height, rows, cols);
        for (int i = 0; i < obstacles.size(); i++) {
            Obstacle obstacle = obstacles.get(i);
            if (wall.getRect().intersects(obstacle.getRect())) {
                ruinFragileWall(wall);
                return null;//创生失败
            }
        }
        wall.createUnit();
        obstacles.add(wall);
        return wall;
    }

    private void ruinFragileWall(FragileWall wall) {
        obstacles.removeAll(wall.fragileWalls);
        wall.fragileWalls.clear();
        wall = null;

    }

    private BirthPlace createBirth(int x, int y) {
        BirthPlace birthPlace = new BirthPlace(x, y);
        //判断位置是否可以产生这个出生地
        for (int i = 0; i < obstacles.size(); i++) {
            Obstacle obstacle = obstacles.get(i);
            if (birthPlace.getRect().intersects(obstacle.getRect())) {
                ruinBirth(birthPlace);
                return null;
            }
        }
        birthPlaces.add(birthPlace);
        obstacles.addAll(birthPlace.getWalls());
        return birthPlace;
    }

    private void ruinBirth(BirthPlace birthPlace) {
        birthPlace.getCool().setLive(false);
        obstacles.removeAll(birthPlace.getWalls());
        birthPlace.getWalls().clear();
        birthPlaces.remove(birthPlace);
        birthPlace = null;
    }

    private class JudgeThread extends Thread {
        @Override
        public void run() {
            while (live) {
                while (pause) {
                    try {
                        Thread.sleep(80);//画面延迟必须是最小的
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (birthPlaces.size() != 0) {
                    randBirthTank();
                    coolCountBirth++;
                    randCreateDamageBirth();
                }
                if (randFragile) {
                    coolCountFragile++;
                    randCreateFragile();
                }
            }

        }
    }

    @Override
    public void run() {
        while (live) {
            //System.out.println("面板线程");
            while (pause) {
                try {
                    Thread.sleep(80);//画面延迟必须是最小的
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            fireResult();
            if (tankPlayer != null)
                tankPlayer.drive(tankPlayer, tankEnemies, obstacles);
            //判断结束
            if (tankPlayer == null || !tankPlayer.isSelfLive() || tankEnemies.size() == 0) {
                this.live = false;
            }
            this.repaint();

        }
        Recorder.keepRecord();
        initialOver();
    }

    //编写方法显示我方击毁对方数量
    private void showInfo(Graphics g) {
        //画出玩家总成绩
        g.setColor(Color.gray);
        //掌控右半小区域
        g.fillRect(1000, 0, GameWindow.width - width, height);
        g.setColor(Color.orange);
        Font font = new Font("宋体", Font.BOLD, 25);
        g.setFont(font);
        g.drawString("我方击毁", 1020, 30);
        g.drawString("对方数量", 1020, 60);
        enemyIcon.drawTank(g);
        g.setColor(Color.orange);
        g.drawString(":" + Recorder.getAllEnemyNum(), 1070, 90);
    }

    @Override//有字符输出时触发
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override//有字符输入时触发
    public void keyPressed(KeyEvent e) {
        if (tankPlayer == null)
            return;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> {
                tankPlayer.setDirect(0);
                tankPlayer.setKeyMove(true);
            }
            case KeyEvent.VK_D -> {
                tankPlayer.setDirect(1);
                tankPlayer.setKeyMove(true);
            }
            case KeyEvent.VK_S -> {
                tankPlayer.setDirect(2);
                tankPlayer.setKeyMove(true);
            }
            case KeyEvent.VK_A -> {
                tankPlayer.setDirect(3);
                tankPlayer.setKeyMove(true);
            }
            case KeyEvent.VK_J -> {
                if (tankPlayer.getCool().isCoolDown())
                    tankPlayer.Shot(obstacles);
            }
        }
    }

    @Override//有键盘被释放时出发
    public void keyReleased(KeyEvent e) {
        if (tankPlayer == null)
            return;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_P -> pause = !pause;
            case KeyEvent.VK_Q -> tankPlayer.setHp(3);
            case KeyEvent.VK_W, KeyEvent.VK_D, KeyEvent.VK_S, KeyEvent.VK_A -> tankPlayer.setKeyMove(false);
        }
    }

    public void initialOver() {
        for (TankEnemy tankEnemy : tankEnemies) {
            tankEnemy.setSelfLive(false);
            tankEnemy = null;
        }
        for (int i = birthPlaces.size() - 1; i >= 0; i--) {
            ruinBirth(birthPlaces.get(i));
        }
        if (tankPlayer != null) {
            tankPlayer.setSelfLive(false);
            tankPlayer = null;
        }
        //音乐线程
        wavMusic.setLive(false);
        nodes.clear();
        birthPlaces.clear();
        tankEnemies.clear();
        obstacles.clear();
        bombs.clear();//（因为它是自生自灭式，可以不清）
    }

    //initialR
    private void initialR() {
        nodes = Recorder.getNodes();
        // 初始化我的坦克
        tankPlayer = new TankPlayer(nodes.get(0).getX(), nodes.get(0).getY(), nodes.get(0).getDirect(), nodes.get(0).getCategory(), nodes.get(0).getLevel(), false, false);
        //得到记录文件里的玩家生命
        tankPlayer.setHp(nodes.get(0).getHp());
        //初始化敌人的坦克(因为重开不管敌人上限，且直接向里面加)
        for (int i = 1; i < nodes.size(); i++) {
            //加入了后取出再加方向
            createRealTank(nodes.get(i).getX(), nodes.get(i).getY(), nodes.get(i).getDirect(), nodes.get(i).getCategory(), nodes.get(i).getLevel(), true, false);
            //得到记录文件里的敌人生命
            tankEnemies.get(i - 1).setHp(nodes.get(i).getHp());
        }
        birthPlaces = Recorder.getBirthPlaces();
        for (BirthPlace birthPlace : birthPlaces) {
            obstacles.addAll(birthPlace.getWalls());
        }
        for (Obstacle obstacle : Recorder.getObstacles()) {
            FragileWall wall = (FragileWall) obstacle;
            wall.createUnit();//需要碰撞单元
        }
        obstacles.addAll(Recorder.getObstacles());
    }

    //initial1(之后可以用于制作关卡)
    private void initial1() {
        // 初始化我的坦克
        tankPlayer = new TankPlayer(20, 600, 0, 1, 1, false, false);
        //初始化敌人的坦克
        for (int i = 0; i < 8; i++) {
            //加入了后取出再加方向
            createRealTank(100 * i + 100, 50 * i + 50, i % 4, i / 4 + 1, i % 3 + 1, true, false);
        }
        LayAbsoluteScenario("1");
        layRelationalScenario();
    }

    private void initial2() {
        // 初始化我的坦克
        tankPlayer = new TankPlayer(20, 600, 0, 2, 1, false, false);
        //初始化敌人的坦克
        for (int i = 0; i < 5; i++) {
            //加入了后取出再加方向
            createRealTank(100 * i + 100, 50 * i + 50, i % 4, i / 4 + 1, i % 3 + 1, true, false);
        }
        LayBirth1();
        LayAbsoluteScenario("2");
        layRelationalScenario();
    }

    //初始化出生点
    private void LayBirth1() {
        BirthPlace birthPlace1 = new BirthPlace(100, 100);
        BirthPlace birthPlace2 = new BirthPlace(300, 100);
        BirthPlace birthPlace3 = new BirthPlace(500, 100);
        birthPlaces.add(birthPlace1);
        birthPlaces.add(birthPlace2);
        birthPlaces.add(birthPlace3);
        //不要忘记出生点的组件walls
        for (BirthPlace birthPlace : birthPlaces) {
            obstacles.addAll(birthPlace.getWalls());
        }
    }

    private void LayAbsoluteScenario(String initial) {
        River river;
        Wall wall;
        HayStack hayStack;
        switch (initial) {
            case "1" -> {
                river = new River(500, 500, 12, 12, 2, 20);
                obstacles.add(river);
                wall = new Wall(800, 200, 12, 12, 10, 1);
                obstacles.add(wall);
                hayStack = new HayStack(300, 500, 30, 30, 5, 10);
                hayStacks.add(hayStack);
            }
            case "2" -> {
                river = new River(500, 400, 12, 12, 3, 30);
                obstacles.add(river);
                wall = new Wall(800, 200, 12, 12, 10, 2);
                obstacles.add(wall);
                hayStack = new HayStack(300, 500, 30, 30, 5, 10);
                hayStacks.add(hayStack);
            }
        }

    }

    private void layRelationalScenario() {
        createFragileWall(100, 400, 34, 34, 5, 5);
    }

    private TankModel createRealTank(int x, int y, int direct, int category, int level, boolean auto, boolean isGood) {
        TankModel tank;//多态运用法
        if (isGood) {
            tank = new TankPlayer(x, y, direct, category, level, auto, false);
        } else {
            tank = new TankEnemy(x, y, direct, category, level, auto, false);
            ((TankEnemy) tank).setTankEnemies(tankEnemies);
            ((TankEnemy) tank).setTankPlayer(tankPlayer);
            ((TankEnemy) tank).setObstacles(obstacles);
            tankEnemies.add((TankEnemy) tank);

        }
        return tank;
    }

    //重载
    private TankModel createRealTank(TankModel tank) {
        if (tank instanceof TankEnemy) {
            ((TankEnemy) tank).setTankEnemies(tankEnemies);
            ((TankEnemy) tank).setTankPlayer(tankPlayer);
            ((TankEnemy) tank).setObstacles(obstacles);
            tankEnemies.add((TankEnemy) tank);
        }
        return tank;
    }

}

