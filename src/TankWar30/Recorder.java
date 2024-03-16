package TankWar30;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.Objects;
import java.util.Vector;

/**
 * 该类用于记录文件信息和文件夹
 */
public class Recorder {
    //定义变量，记录击毁数量
    private static int allEnemyNum = 0;
    //关卡
    private static String initial;
    //游戏模式
    private static boolean brawl=true;
    private static boolean randBirth=true;
    private static boolean randFragile=true;
    //定义一个Node集合来存储所有坦克的信息
    private static final Vector<Node> nodes = new Vector<>();
    //敌人
    private static Vector<TankEnemy> tankEnemies = null;
    //玩家
    private static TankPlayer tankPlayer;
    //坦克总数
    private static int tankNum=0;
    //出生点
    private static Vector<BirthPlace>birthPlaces= new Vector<>();
    //出生点数
    private static int birthNum=0;
    //易碎墙
    //private static int[][] fragileNum;
    private static Vector<Obstacle> obstacles =new Vector<>();
    //定义一个读取文件的必要
    private static boolean complete = true;
    //定义IO对象
    private static BufferedWriter bufferedWriter = null;
    private static BufferedReader bufferedReader = null;
    //写入的文件必须是在out文件夹下的
   private static final URL FilePath = Objects.requireNonNull(Recorder.class.getClassLoader().getResource("myLocal.txt"));
  private static final String recordFilePath=FilePath.getPath().substring(0, FilePath.getPath().lastIndexOf( '/',FilePath.getPath().lastIndexOf('/')-1))+"/myLocal.txt";

    //把GamePanel里new的对象传入到这里
    public static void setTankEnemies(Vector<TankEnemy> tankEnemies) {
        Recorder.tankEnemies = tankEnemies;
    }
    public static void setBirthPlaces(Vector<BirthPlace> birthPlaces) {Recorder.birthPlaces = birthPlaces;}
    public static void setTankPlayer(TankPlayer tankPlayer) {
        Recorder.tankPlayer = tankPlayer;
    }
    public static void setObstacles(Vector<Obstacle> obstacles) {
        Recorder.obstacles = obstacles;
    }
private static void getFile(){
        //目前只适用于非jar包
//    //取目录
//    String dir = recordFilePath.substring(0, recordFilePath.lastIndexOf( '/'));
//    File file = new File(dir);
//    //判断目录是否存在，目录其实也是一种文件
//    if(!file.exists())
//        file.mkdirs();//创建系列目录
//    File file2 = new File(recordFilePath);
//    if(!file2.exists()) {
//        try {
//            file2.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    System.out.println(  Objects.requireNonNull(Recorder.class.getClassLoader().getResourceAsStream("/myLocal.txt")) );
    System.out.println(recordFilePath);
   // System.out.println( file );
}
    //添加一个保存的方法
    public static void keepRecord()  {
       //getFile();
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(recordFilePath)));
            //记录击杀数
            bufferedWriter.write(allEnemyNum + "\r\n");
            //记录关卡数
            bufferedWriter.write(initial + "\r\n");
            //记录游戏模式
            bufferedWriter.write(brawl + "\r\n");
            bufferedWriter.write(randBirth + "\r\n");
            bufferedWriter.write(randFragile + "\r\n");

            //判断玩家存活，记录其数据
            System.out.println("坦克存活" + tankPlayer.isSelfLive());
            System.out.println("敌人数量" + tankEnemies.size());
            System.out.println("出生点数量" + birthPlaces.size());
            if (tankPlayer == null || !tankPlayer.isSelfLive() || tankEnemies.size() == 0) {
                System.out.println("无需存储");
            } else {
                //记录坦克数
                tankNum=tankEnemies.size()+1;
                bufferedWriter.write(tankNum+ "\r\n");
                String record = tankPlayer.getX() + " " + tankPlayer.getY() +
                        " " + tankPlayer.getDirect() + " " + tankPlayer.getCategory() +
                        " " + tankPlayer.getLevel() + " " + tankPlayer.getHp();
                bufferedWriter.write(record + "\r\n");
                //遍历敌人坦克来获取位置
                for (int i = 0; i < tankEnemies.size(); i++) {
                    TankEnemy tankEnemy = tankEnemies.get(i);
                    if (tankEnemy.isSelfLive()) {
                        record = tankEnemy.getX() + " " + tankEnemy.getY() +
                                " " + tankEnemy.getDirect()  + " " + tankEnemy.getCategory() +
                                " " + tankEnemy.getLevel() + " " + tankEnemy.getHp();
                        bufferedWriter.write(record + "\r\n");
                    }
                }
                birthNum=birthPlaces.size();
                bufferedWriter.write(birthNum+ "\r\n");
                for (int i = 0; i < birthPlaces.size(); i++) {
                    BirthPlace birthPlace = birthPlaces.get(i);
                    record = birthPlace.getX() + " " + birthPlace.getY();
                        bufferedWriter.write(record + "\r\n");
                }
                for (int i = 0; i< obstacles.size(); i++)
                {
                    if (obstacles.get(i) instanceof FragileWall fragileWall){
                         record = fragileWall.getX() + " " + fragileWall.getY()+" "+fragileWall.getWidth()+" "
                                + fragileWall.getHeight()+" "+ fragileWall.getRows()+" "+ fragileWall.getCols();
                         bufferedWriter.write(record + "\r\n");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Vector<Obstacle> getObstacles() {
        return obstacles;
    }

    //增加一个读取文件方法
    public static void readRecord(Boolean necessary){
        System.out.println(  Objects.requireNonNull(Recorder.class.getClassLoader().getResourceAsStream("myLocal.txt")) );
        System.out.println(recordFilePath);
        //读之前清一下已经读的
        nodes.clear();
        //getFile();
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Recorder.class.getClassLoader().getResourceAsStream("myLocal.txt"))));
            String line;
            //读击杀敌人数（并防止第一次打开而报错）
            if ((line = bufferedReader.readLine()) == null) {
                allEnemyNum = 0;
            } else {
                allEnemyNum = Integer.parseInt(line);
            }
            if(!necessary){
                return;
            }
            if ((line = bufferedReader.readLine()) == null)
                initial="1";
            else initial = line;
            if ((line = bufferedReader.readLine()) == null)
                brawl=false;
            else brawl =Boolean.parseBoolean(line);
            if ((line = bufferedReader.readLine()) == null)
                randBirth=false;
            else randBirth =Boolean.parseBoolean(line);
            if ((line = bufferedReader.readLine()) == null)
                randFragile=false;
            else randFragile =Boolean.parseBoolean(line);
            //敌人数量
                if ((line = bufferedReader.readLine()) == null) {
                    complete = false;
                    return;
                } else tankNum = Integer.parseInt(line);
                //循环读取文件，生成nodes集合
                for (int i = 0; i < tankNum; i++) {
                    if ((line = bufferedReader.readLine()) == null) {
                        complete = false;
                        return;
                    } else {
                        String[] split = line.split(" ");//根据空格分隔数据
                        //将分隔的数据传入构造器中
                        Node node = new Node(Integer.parseInt(split[0]), Integer.parseInt(split[1]),
                                Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]), Integer.parseInt(split[5]));
                        nodes.add(node);
                    }
                }
                if ((line = bufferedReader.readLine()) == null) {
                    complete = false;
                    return;
                } else birthNum = Integer.parseInt(line);
                //循环读文件
                for (int i = 0; i < birthNum; i++) {
                    if ((line = bufferedReader.readLine()) == null) {
                        complete = false;
                        return;
                    } else {
                        String[] split = line.split(" ");//根据空格分隔数据
                        BirthPlace birthPlace = new BirthPlace(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
                        birthPlaces.add(birthPlace);
                    }
                }
                while ((line = bufferedReader.readLine()) != null) {
                    String[] split = line.split(" ");//根据空格分隔数据
                    FragileWall fragileWall = new FragileWall(Integer.parseInt(split[0]), Integer.parseInt(split[1]),
                                Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]), Integer.parseInt(split[5]));
                        obstacles.add(fragileWall);

                }
            } catch (Exception e) {
                e.printStackTrace();
                complete = false;
            }
        finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static Vector<Node> getNodes() {
        return nodes;
    }
    public static boolean isComplete() {
        return complete;
    }

    //返回记录文件的路径
    public static URL getRecordFilePath() {
        return FilePath;
    }

    //得到allEnemyNum的方法
    public static int getAllEnemyNum() {
        return allEnemyNum;
    }

    public static void setInitial(String initial) {
        Recorder.initial = initial;
    }

    public static void addAllEnemyNum() {
        allEnemyNum++;
    }

    public static String getInitial() {
        return initial;
    }
    public static Vector<BirthPlace> getBirthPlaces() {
        return birthPlaces;
    }
    public static boolean isBrawl() {
        return brawl;
    }
    public static void setBrawl(boolean brawl) {
        Recorder.brawl = brawl;
    }
    public static boolean isRandBirth() {
        return randBirth;
    }
    public static void setRandBirth(boolean randBirth) {
        Recorder.randBirth = randBirth;
    }
    public static void setRandFragile(boolean randFragile) {
        Recorder.randFragile = randFragile;
    }
    public static boolean isRandFragile() {
        return randFragile;
    }
//    public static File getJarResourceFile(){
//        //获取容器资源解析器
//        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//        try {
//            //获取所有匹配的文件
//            Resource[] resources = resolver.getResources("resources文件夹下的文件路径");
//            if (resources.length > 0) {
//                //获得文件流，因为在jar文件中，不能直接通过文件资源路径拿到文件，但是可以在jar包中拿到文件流
//                InputStream stream = resources[0].getInputStream();
//                if (log.isInfoEnabled()) {
//                    log.info("读取的文件流  [" + stream + "]");
//                }
//                String targetFilePath = System.getProperty("user.home") + File.separator + resources[0].getFilename();
//                if (log.isInfoEnabled()) {
//                    log.info("放置位置  [" + targetFilePath + "]");
//                }
//                File ttfFile = new File(targetFilePath);
//                //把流中的数据写入到文件中
//                InputStream initialStream = stream;
//                byte[] buffer = new byte[initialStream.available()];
//                initialStream.read(buffer);
//                OutputStream outStream = new FileOutputStream(ttfFile);
//                outStream.write(buffer);
//                initialStream.close();
//                outStream.close();
//                return ttfFile;
//            }
//        } catch (IOException e) {
//            if (log.isWarnEnabled()) {
//                log.warn("读取文件流失败，写入本地库失败！ " + e);
//            }
//        }
//        throw new RuntimeException("未找到文件");
//    }
}
