package TankWar30;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Objects;

public class LoginWindow extends JFrame{
    //用户名和密码：
    final String username = "1";
    final String password = "1";

    //用户名、密码文本框
    JTextField user;
    JTextField pwd;
    //登录按钮
    JButton Log;
    //取消按钮
    JButton Log1;
//    public static void main(String[] args) {
//        new LoginWindow();
//    }
    public  LoginWindow(){//类继承窗口函数
        setTitle("登录界面");
        setSize(600, 600);//设置大小
        setBackground(Color.black);
        setLocationRelativeTo(null);//设置居中
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//设置可关闭
        setLayout(null);//设置绝对布局（窗口里面的内容不会随着窗口的改变而改变）(也无法设置背景颜色)
        //绝对布局同时会限制按钮的大小
        setResizable(false);//设置窗口不可拉伸改变大小
        //设置用户名标签
        JLabel username_label =new JLabel("用户名:");
        username_label.setBounds(100,100,100,50);
      add(username_label);
        //设置密码标签
        JLabel password_label =new JLabel("密码");
        password_label.setBounds(100,200,100,50);
       add(password_label);
        //设置用户名文本框
        user=new JTextField();
        user.setBounds(150, 100, 300, 50);
     add(user);
        //设置密码文本框
        pwd=new JPasswordField();//隐藏密码
        pwd.setBounds(150, 200, 300, 50);
       add(pwd);
        //设置按钮
        Log=new JButton("登录");
        Log.setBounds(150, 300, 100, 50);
        add(Log);
        //设置按钮
        Log1=new JButton("取消");
        Log1.setBounds(300, 300, 100, 50);
       add(Log1);
        //按下登录键时判断是否输入对信息
        //事件监听信息并加以处理
        ActionListener listener1 = actionEvent -> {
            if (Objects.equals(pwd.getText(), password) && Objects.equals(user.getText(), username)) {
                //System.out.println("用户名: " + user.getText() + "密码" + pwd.getText());
                JOptionPane.showMessageDialog(null, "登录成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                //这里会等待一手对话框，其结束后才执行一下程序
                new StartPanel();
                //注意这里必须指明实例的名子，因为这里在构造器中new的一个东西，有其实例，this指的是类名
                this.dispose();
            } else {
                //System.out.println("用户名: " + user.getText() + "密码" + pwd.getText());
                JOptionPane.showMessageDialog(null, "用户名或密码错误", "失败", JOptionPane.ERROR_MESSAGE);
            }
        };
        //按下取消键时清空文本
        //事件监听信息并加以处理
        ActionListener listener2 = actionEvent -> {
            pwd.setText(null);
            user.setText(null);
            //System.out.println("用户名: " + user.getText() + "密码" + pwd.getText());
            JOptionPane.showMessageDialog(null, "重置成功", "重置", JOptionPane.INFORMATION_MESSAGE);};
        //再点击登录按钮时加入相应事件
        Log.addActionListener(listener1);
        //再点击取消按钮时加入相应事件
        Log1.addActionListener(listener2);
        setVisible(true);//设置面板可见
    }
}

