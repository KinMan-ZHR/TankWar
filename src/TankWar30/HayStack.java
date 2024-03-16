package TankWar30;

import java.awt.*;

public class HayStack extends Obstacle{
    public HayStack(int x, int y, int width, int height, int rows, int cols) {
        super(x, y, width, height, rows, cols);
    }
    public void drawGrass(Graphics g){
        for (int i=0;i<getRows();i++){
            for (int j=0;j<getCols();j++){
                g.drawImage(img,getX()-getWidth()/2+j*getWidth() , getY()-getHeight()/2+i*getHeight(), getX()+getWidth()/2+j*getWidth() , getY()+getHeight()/2+i*getHeight(), 4*sWidth+3, 7*sHeight+1, 4*sWidth+sWidth-3, 7*sHeight+sHeight-1, null);
            }
        }
    }
}
