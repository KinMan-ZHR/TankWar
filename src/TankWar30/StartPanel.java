package TankWar30;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Objects;

/**
 * �����ͣ������ֹͣ��������ΪΧǽ�Ĵ�����ظ�������֪ʶѧϰ��
 *��ס��Щ��������û��Ҫ��˭����Ǯ�أ�
 * @author �������ź�Ȼ2021141461113��2022/4/14
 * ����̹�˴�ս2.7
 * issuses to be addressed
 * <p>
 *ʵ��ͼƬ�ļ��Ķ�ȡ
 *����exe�ļ���3.0��
 *
 * <p>
 * how to solve��or solve what��
 * <p>
 *
 * <p>
 * bequeath problem
 * �ļ���ȡ���ִ��벻����ȫ����������������ǽ���޷�������ʵ������ѭ����������ѭ����ֵ�Ϳ����ˣ�
 * ���̵߳��������⣨bullet64�У�tankModel251�У�����ǽ��ը���ǲ��ȶ�
 * �����޷���ȡ,
 * �ļ���Ҫ��idea��ʹ��GBK����,��������ʱ��-Dfile.encoding=UTF-8ִ�з��ɲ������룬���Ǹо��ܳ�������
 *
 * <p>
 *
 * �������ࣩ��escape���޷������ӵ�������������˺ܶ࣬��Ϊescape�Ѿ����벻��ѭ�����룬�ܿ�������룩
 * �����߼���Ӧ����Ϊ�����߳���drawTank����
 * <p>
 * ready to do
 * <p>
 * escape�е��ٶȿ��Ը��ģ���ͬ��ƽ�����н��ٶ�
 * <p>
 */
public class StartPanel extends JFrame {
    //���ھ����Ƿ��ؿ������Լ��ؿ�ѡ���ؿ����ȡ�ļ����ݣ��ж���һ������һ�صģ�
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
        startPanel.setLayout(null);//�������о��Բ���
        //δ��������С���Զ�������������
        this.add(startPanel);
        startUI();
        chooseInitial();
        chooseGameMode();

        //��Ӧ�رձ�ǩҳʱ�Ķ������ֶ������
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setUIFont(startPanel, "SimSun");
    }

    private void startUI() {
        //������
        JLabel information = new JLabel("TankWar  (*^��^*)");
       createNewButton(information,startPanel,200, 10, 250, 100,Color.yellow,24);
       //�ؿ���ť
        JButton btnNewButton1 = new JButton("�ؿ�");
        createNewButton(btnNewButton1,startPanel,200, 250, 80, 30,Color.cyan,16);
        //ȥ����
        btnNewButton1.setFocusPainted(false);
        //ȥ�߿�
        btnNewButton1.setBorderPainted(false);
        btnNewButton1.addActionListener(listener1);
        //���̰�ť
        JButton btnNewButton2 = new JButton("����");
        createNewButton(btnNewButton2,startPanel,320, 250, 80, 30,Color.cyan,16);
        //ȥ����
        btnNewButton2.setFocusPainted(false);
        //ȥ�߿�
        btnNewButton2.setBorderPainted(false);
        btnNewButton2.addActionListener(listener1);
    }

    private void chooseInitial() {
        //�ؿ�ѡ��
        JLabel label1 = new JLabel("�ؿ���");//�½���ǩ���
        //��������setBounds
        createNewButton(label1,startPanel,150, 125, 100, 20,Color.yellow,18);

        //׼��һ�����鶨�������б�
        String[] counties = {"Hello","1", "2" };
        comboBox = new JComboBox<>();//�½�һ�������б����
        for (String county : counties) {//�������б�ѡ����������
            comboBox.addItem(county);//ÿһ�����������Ӧ��ֵ
        }
        comboBox.setBounds(230, 120, 150, 30);//���������б������λ��
        comboBox.setEditable(true);//���������б����Ϊ�ɱ༭
        comboBox.setBackground(Color.yellow);
        comboBox.setForeground(Color.red);
        startPanel.add(comboBox);//�������б������ӵ����
    }

    private void chooseGameMode() {
        //ģʽѡ��
        JLabel label2 = new JLabel("ģʽ��");//�½���ǩ���
        //��������setBounds
        label2.setBounds(150, 200, 80, 20);//���ñ�ǩ���λ��
        label2.setFont(new Font("Dialog", Font.BOLD, 18));
        label2.setForeground(Color.yellow);
        startPanel.add(label2);//����ǩ�����������
        //ģʽ��ѡ��ť
        /*������ѡ��ť��������ڴ�����*/
        buttonRadio1=new JRadioButton("��ͨģʽ");
        createNewButton(buttonRadio1, startPanel , 220, 200, 100, 25);
        buttonRadio2 = new JRadioButton("�Ҷ�ģʽ");
                createNewButton(buttonRadio2,startPanel,330, 200, 100, 25);
        buttonRadio3 = new JRadioButton("����ģʽ");
        createNewButton(buttonRadio3,startPanel,330, 230, 100, 25);
        buttonRadio4 = new JRadioButton("����ģʽ");
        createNewButton(buttonRadio4,startPanel,220, 230, 100, 25);
        buttonRadio1.setSelected(true);//����ѡ��ť1��Ϊѡ��״̬
        buttonRadio3.addActionListener(listener2);
        buttonRadio1.addActionListener(listener2);
        buttonRadio2.addActionListener(listener2);
        buttonRadio4.addActionListener(listener2);
    }
    //�������أ�����һ�����Ի��İ�ť
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
    //���µ�¼��ʱ�ж��Ƿ��������Ϣ
    //�¼�������Ϣ�����Դ���
    ActionListener listener1 = actionEvent -> {
        if (actionEvent.getActionCommand().equals("�ؿ�")) {
            //System.out.println("�û���: " + user.getText() + "����" + pwd.getText());
            JOptionPane.showMessageDialog(null, "��һ����Ϸ", "TankWar", JOptionPane.INFORMATION_MESSAGE);
            //���Ǳ��������ѡ�ɻ��ȡ�����򲻻���
            startWay = Objects.requireNonNull(comboBox.getSelectedItem()).toString();
        }
        if (actionEvent.getActionCommand().equals("����")) {
            //System.out.println("�û���: " + user.getText() + "����" + pwd.getText());
            JOptionPane.showMessageDialog(null, "��һ����Ϸ", "TankWar", JOptionPane.INFORMATION_MESSAGE);
            startWay = "R";
        }
        boolean gameMode1 = buttonRadio2.isSelected();
        boolean gameMode2 = buttonRadio3.isSelected();
        boolean gameMode3 = buttonRadio4.isSelected();
        //�����ȴ�һ�ֶԻ�����������ִ��һ�³���
        new GameWindow(startWay, gameMode1,gameMode2,gameMode3);
        this.dispose();
    };
    ActionListener listener2=actionEvent->{
        if(actionEvent.getActionCommand().equals("��ͨģʽ")){
        if(buttonRadio1.isSelected()){
            buttonRadio2.setSelected(false);
            buttonRadio3.setSelected(false);
            buttonRadio4.setSelected(false);
        }}
        if(actionEvent.getActionCommand().equals("�Ҷ�ģʽ")||actionEvent.getActionCommand().equals("����ģʽ")
        ||actionEvent.getActionCommand().equals("����ģʽ")){
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