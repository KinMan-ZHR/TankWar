package TankWar30;

import javax.sound.sampled.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

//注意该类只能播放wav文件（但属于一个通用文件）
public class WavMusic extends Thread {
    private boolean live=true;
    private static BufferedInputStream bufferedInputStream;
    public WavMusic(String wavFile) {//以后只需调用就可以了，wavFile即为为文件名
        bufferedInputStream =new BufferedInputStream(Objects.requireNonNull(WavMusic.class.getClassLoader().getResourceAsStream(wavFile)));
        this.start();
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }
    @Override
    public void run() {
        while (live) {
           // System.out.println("音乐线程");
            AudioInputStream audioInputStream;
            try {
                audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream);
            } catch (UnsupportedAudioFileException | IOException e) {
                e.printStackTrace();
                return;
            }
            AudioFormat format = audioInputStream.getFormat();
            SourceDataLine auLine;
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            try {
                auLine = (SourceDataLine) AudioSystem.getLine(info);
                auLine.open(format);
            } catch (LineUnavailableException e) {
                e.printStackTrace();
                return;
            }
            auLine.start();
            int nBytesRead = 0;
            //缓冲
            byte[] abData = new byte[512];
            try {
                //不停地读文件(播放音乐)
                while (nBytesRead != -1 && live) {
                    while (GamePanel.pause){
                        try {
                            Thread.sleep(100);//画面延迟必须是最小的
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    nBytesRead = audioInputStream.read(abData, 0, abData.length);
                    if (nBytesRead >= 0)
                        auLine.write(abData, 0, abData.length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                auLine.drain();
                auLine.close();

            }

        }
    }
}