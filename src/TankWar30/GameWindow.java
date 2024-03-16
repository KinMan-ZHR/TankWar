package TankWar30;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

public class GameWindow extends JFrame {
    private GamePanel mp;
    static final int width = 1200, height = 750;

    public GameWindow(String startWay, Boolean gameMode1, Boolean gameMode2, Boolean gameMode3) {
        //设置大小
        this.setSize(width, height);
        //设置居中
        this.setLocationRelativeTo(null);
        //设置窗口不可拉伸改变大小
        this.setResizable(false);
        this.setTitle("TankWar");
        mp = new GamePanel(startWay);//mp构造器所需要的参数
        //若是重开
        if (!Objects.equals(startWay, "R")) {
            mp.brawl = gameMode1;
            mp.randBirth = gameMode2;
            mp.randFragile = gameMode3;
        }//若是续盘不管是什么模式
        else {
            mp.brawl = Recorder.isBrawl();
            mp.randBirth = Recorder.isRandBirth();
            mp.randFragile = Recorder.isRandFragile();
        }
        Recorder.setBrawl(mp.brawl);
        Recorder.setRandBirth(mp.randBirth);
        Recorder.setRandFragile(mp.randFragile);
        //静态代理模式启动线程
        new Thread(mp).start();
        this.add(mp);
        //监听面板的键盘事件（谁完成了keyAdapter就可以听谁的）
        this.addKeyListener(mp);
        //设置面板可见
        this.setVisible(true);
        //在frame中增加相应关闭窗口的处理
        //this将感应windowClosing的情况
        this.addWindowListener(new WindowAdapter() {
            //windowAdapter只需重写需要的方法
            //这里是监听窗口被关闭了，就执行一下程序
            @Override
            public void windowClosing(WindowEvent e) {
                //super.windowClosing(e);
                System.out.println("数据将自动保存");
                //Recorder.keepRecord();
                //置空一切
                mp.live = false;//设其死亡就会置空就会保存数据
                //mp.initialOver();
                mp = null;//当一个对象的所有字段均不在活跃时，就会销毁（若字段活跃，销毁mp没用）
                new StartPanel();
            }
        });
    }

}

