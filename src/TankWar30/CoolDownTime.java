package TankWar30;

public class CoolDownTime implements Runnable {
    private boolean coolDown = false;
    private int coolDownTime;
    private boolean live = true;
    //无意义，单纯要一个锁
    private final Object lock = new Object();
    //标志线程阻塞情况
    private boolean pause = false;

    public CoolDownTime(int coolDownTime) {
        this.coolDownTime = coolDownTime;
        new Thread(this).start();//创建时就启动线程
    }

    public void pauseThread() {
        this.pause = true;
    }

    public void resumeThread() {
        this.pause = false;
        synchronized (lock) {
            //唤醒线程
            lock.notify();
        }
    }

    //进入暂停状态（只能在线程内部使用）
    void onPause() {
        synchronized (lock) {//同步代码块
            try {
                //线程 等待/阻塞
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //CoolDowntime函数逻辑
    //当使用冷却时希望可以
    //判定当前冷却情况
    //若未冷却进入休眠线程，冷却后，将情况置为已冷却状态
    //若冷却成功，则则直接置入未冷却状态则等待未冷却的情况唤醒线程
    //冷却态变成未冷却不要时间(但应当感受是否执行程序才变化)
    //即在动作的内部函数必须将冷却置为false
    //在执行前判断cool.down,决定是否执行该程序
    @Override
    public void run() {
        while (live) {
            //很可能影响了冷却时
            while (GamePanel.pause) {
                try {
                    Thread.sleep(100);//画面延迟必须是最小的
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // System.out.println("冷却线程");
            if (pause) {
                //线程 阻塞/等待
                onPause();
            }//我的代码
            if (!coolDown) {//未冷却变成冷却态有时间
                try {
                    Thread.sleep(coolDownTime);
                    coolDown = !coolDown;
                    //System.out.println(!coolDown+"变成"+coolDown);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            } else {//冷却是成功的，需要阻塞
                pauseThread();//自我暂停
            }
        }
    }

    public boolean isCoolDown() {
        return coolDown;
    }

    public void setCoolDown(boolean coolDown) {
        this.coolDown = coolDown;
    }

    public int getCoolDownTime() {
        return coolDownTime;
    }

    public void setCoolDownTime(int coolDownTime) {
        this.coolDownTime = coolDownTime;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }
}
//1.创建自己的线程
//        MyThread myThread = new MyThread();
//
// 2.在合适的地方启动线程(你需要在什么地方启动它)
//        myThread.start();
//
// 3.启动后线程的 阻塞/暂停
//        myThread.pauseThread();
//
// 4.以及 阻塞/暂停 线程后的 唤醒/继续
//        myThread.resumeThread();