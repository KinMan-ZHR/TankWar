package TankWar30;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Objects;

/**
 * 完成暂停程序后就停止开发。因为围墙的处理很重复（无新知识学习）
 *记住有些可以做但没必要，谁给我钱呢？
 * @author 谨漫（张浩然2021141461113，2022/4/14
 * 基于坦克大战2.7
 * issuses to be addressed
 * <p>
 *实现图片文件的读取
 *生成exe文件（3.0）
 *
 * <p>
 * how to solve（or solve what）
 * <p>
 *
 * <p>
 * bequeath problem
 * 文件读取部分代码不够健全，若是有两种易碎墙我无法处理（其实可以先循环计数，再循环存值就可以了）
 * 多线程迭代器问题（bullet64行，tankModel251行）易碎墙爆炸还是不稳定
 * 音乐无法读取,
 * 文件需要在idea中使用GBK加载,并在运行时以-Dfile.encoding=UTF-8执行方可不会乱码，就是感觉很扯淡！！
 *
 * <p>
 *
 * （功能类）在escape中无法发射子弹（现在问题好了很多，因为escape已经脱离不定循环范畴，很快就能逃离）
 * 但在逻辑上应当成为独立线程与drawTank并行
 * <p>
 * ready to do
 * <p>
 * escape中的速度可以更改，不同于平常的行进速度
 * <p>
 */
public class StartPanel extends JFrame {
    //用于决定是否重开续盘以及关卡选择（重开需读取文件数据，判断上一局是哪一关的）
    private String startWay;
    private JComboBox<String> comboBox;
    private JRadioButton buttonRadio1;
    private JRadioButton buttonRadio2;
    private JRadioButton buttonRadio3;
    private JRadioButton buttonRadio4;
    private final JPanel startPanel;

    public static void main(String[] args) {
        new StartPanel();
    }

    public StartPanel() {

        this.setTitle("TankWar");
        this.setSize(600, 375);
        this.setLocationRelativeTo(null);
        startPanel = new JPanel();

        startPanel.setBackground(Color.black);
        startPanel.setLayout(null);//对面板进行绝对布局
        //未定义面板大小将自动充满整个窗口
        this.add(startPanel);
        startUI();
        chooseInitial();
        chooseGameMode();

        //感应关闭标签页时的动作（手动点×）
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setUIFont(startPanel, "SimSun");
    }

    private void startUI() {
        //面板组件
        JLabel information = new JLabel("TankWar  (*^▽^*)");
       createNewButton(information,startPanel,200, 10, 250, 100,Color.yellow,24);
       //重开按钮
        JButton btnNewButton1 = new JButton("重开");
        createNewButton(btnNewButton1,startPanel,200, 250, 80, 30,Color.cyan,16);
        //去焦点
        btnNewButton1.setFocusPainted(false);
        //去边框
        btnNewButton1.setBorderPainted(false);
        btnNewButton1.addActionListener(listener1);
        //续盘按钮
        JButton btnNewButton2 = new JButton("续盘");
        createNewButton(btnNewButton2,startPanel,320, 250, 80, 30,Color.cyan,16);
        //去焦点
        btnNewButton2.setFocusPainted(false);
        //去边框
        btnNewButton2.setBorderPainted(false);
        btnNewButton2.addActionListener(listener1);
    }

    private void chooseInitial() {
        //关卡选择
        JLabel label1 = new JLabel("关卡：");//新建标签组件
        //可以利用setBounds
        createNewButton(label1,startPanel,150, 125, 100, 20,Color.yellow,18);

        //准备一串数组定义下拉列表
        String[] counties = {"Hello","1", "2" };
        comboBox = new JComboBox<>();//新建一个下拉列表组件
        for (String county : counties) {//向下拉列表选项中添加组件
            comboBox.addItem(county);//每一个对象有其对应的值
        }
        comboBox.setBounds(230, 120, 150, 30);//设置下拉列表组件的位置
        comboBox.setEditable(true);//设置下拉列表组件为可编辑
        comboBox.setBackground(Color.yellow);
        comboBox.setForeground(Color.red);
        startPanel.add(comboBox);//将下拉列表组件添加到面板
    }

    private void chooseGameMode() {
        //模式选择
        JLabel label2 = new JLabel("模式：");//新建标签组件
        //可以利用setBounds
        label2.setBounds(150, 200, 80, 20);//设置标签组件位置
        label2.setFont(new Font("Dialog", Font.BOLD, 18));
        label2.setForeground(Color.yellow);
        startPanel.add(label2);//将标签组件添加面板上
        //模式单选按钮
        /*创建单选按钮，将其放在窗体上*/
        buttonRadio1=new JRadioButton("普通模式");
        createNewButton(buttonRadio1, startPanel , 220, 200, 100, 25);
        buttonRadio2 = new JRadioButton("乱斗模式");
                createNewButton(buttonRadio2,startPanel,330, 200, 100, 25);
        buttonRadio3 = new JRadioButton("地鼠模式");
        createNewButton(buttonRadio3,startPanel,330, 230, 100, 25);
        buttonRadio4 = new JRadioButton("地形模式");
        createNewButton(buttonRadio4,startPanel,220, 230, 100, 25);
        buttonRadio1.setSelected(true);//将单选按钮1设为选中状态
        buttonRadio3.addActionListener(listener2);
        buttonRadio1.addActionListener(listener2);
        buttonRadio2.addActionListener(listener2);
        buttonRadio4.addActionListener(listener2);
    }
    //方法重载，创造一个个性化的按钮
private <T extends JComponent> T createNewButton(T t,JPanel jPanel,int x, int y,int width, int height){
       return this.createNewButton(t,jPanel,x,y,width,height,Color.orange,14);
}
private <T extends JComponent> T createNewButton(T t,JPanel jPanel,int x, int y,int width, int height,Color color ,int size){
        t.setBounds(x, y, width, height);
    t.setBackground(Color.black);
    t.setForeground(color);
    t.setFont(new Font("Dialog", Font.BOLD, size));
    jPanel.add(t);
        return t;
}
    //按下登录键时判断是否输入对信息
    //事件监听信息并加以处理
    ActionListener listener1 = actionEvent -> {
        if (actionEvent.getActionCommand().equals("重开")) {
            //System.out.println("用户名: " + user.getText() + "密码" + pwd.getText());
            JOptionPane.showMessageDialog(null, "新一局游戏", "TankWar", JOptionPane.INFORMATION_MESSAGE);
            //这是必须得亲自选采会读取，否则不会变的
            startWay = Objects.requireNonNull(comboBox.getSelectedItem()).toString();
        }
        if (actionEvent.getActionCommand().equals("续盘")) {
            //System.out.println("用户名: " + user.getText() + "密码" + pwd.getText());
            JOptionPane.showMessageDialog(null, "上一局游戏", "TankWar", JOptionPane.INFORMATION_MESSAGE);
            startWay = "R";
        }
        boolean gameMode1 = buttonRadio2.isSelected();
        boolean gameMode2 = buttonRadio3.isSelected();
        boolean gameMode3 = buttonRadio4.isSelected();
        //这里会等待一手对话框，其结束后才执行一下程序
        new GameWindow(startWay, gameMode1,gameMode2,gameMode3);
        this.dispose();
    };
    ActionListener listener2=actionEvent->{
        if(actionEvent.getActionCommand().equals("普通模式")){
        if(buttonRadio1.isSelected()){
            buttonRadio2.setSelected(false);
            buttonRadio3.setSelected(false);
            buttonRadio4.setSelected(false);
        }}
        if(actionEvent.getActionCommand().equals("乱斗模式")||actionEvent.getActionCommand().equals("地鼠模式")
        ||actionEvent.getActionCommand().equals("地形模式")){
        if(buttonRadio2.isSelected()||buttonRadio3.isSelected()||buttonRadio4.isSelected()){
            buttonRadio1.setSelected(false);
        }}
    };
    public static void setUIFont(Component component, String fontName) {
        if (component instanceof Container) {
            Container container = (Container) component;
            for (Component subComponent : container.getComponents()) {
                setUIFont(subComponent, fontName);
            }
        }
        if (component instanceof JTextComponent ||
                component instanceof JButton ||
                component instanceof JLabel ||
                component instanceof JList ||
                component instanceof JComboBox ||
                component instanceof JTable ||
                component instanceof JCheckBox ||
                component instanceof JRadioButton) {
            component.setFont(new Font(fontName, component.getFont().getStyle(), component.getFont().getSize()));
        }
    }
}