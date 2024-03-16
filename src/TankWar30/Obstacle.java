package TankWar30;

import java.awt.*;

public class Obstacle extends Picture {
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final int rows;
    private final int cols;
    public Obstacle(int x, int y, int width, int height, int rows, int cols) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rows = rows;
        this.cols = cols;
    }
    public void drawWall(Graphics g) {
        for (int i=0;i<rows;i++){
            for (int j=0;j<cols;j++){
                g.drawImage(img,x-width/2+j*width , y-height/2+i*height, x+width/2+j*width , y+height/2+i*height, 18*sWidth, 5*sHeight, 18*sWidth+sWidth/2, 5*sHeight+sHeight/2, null);
            }
        }
    }
    public void drawFragileWall(Graphics g) {
        for (int i=0;i<rows;i++){
            for (int j=0;j<cols;j++){
                g.drawImage(img,x-width/2+j*width , y-height/2+i*height, x+width/2+j*width , y+height/2+i*height, 9*sWidth, 7*sHeight, 9*sWidth+sWidth, 7*sHeight+sHeight, null);
            }
        }
    }
    public void drawRiver(Graphics g) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if ((int)(Math.random()*2)==1)
                    g.drawImage(img, x - width / 2 + j * width, y - height / 2 + i * height, x + width / 2 + j * width, y + height / 2 + i * height, sWidth, 7 * sHeight, sWidth + sWidth / 2, 7 * sHeight + sHeight / 2, null);
                else
                    g.drawImage(img, x - width / 2 + j * width, y - height / 2 + i * height, x + width / 2 + j * width, y + height / 2 + i * height, 0, 7 * sHeight, sWidth / 2, 7 * sHeight + sHeight / 2, null);
            }
        }
    }
    public Rectangle getRect() {
        return new Rectangle(x- width/2, y- height /2, cols*width, rows*height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
}

