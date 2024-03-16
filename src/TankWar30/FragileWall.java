package TankWar30;

import java.util.AbstractList;
import java.util.Vector;

public class FragileWall extends Obstacle {
    Vector<FragileWall> fragileWalls = new Vector<>();

    public FragileWall(int x, int y, int width, int height, int rows, int cols) {
        super(x, y, width, height, rows, cols);
        //极度危险(千万不能将createUnit放入构造器中)
    }

    public void createUnit() {//若wall不存在实体单元，就不会参与碰撞，所以每个wall都要createUnit
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getCols(); j++) {
                fragileWalls.add(new FragileWall(getX() + j * getWidth(), getY() + i * getHeight(), getWidth(), getHeight(), 1, 1));
            }
        }
    }
//泛型函数
    //作用与局限性：
    //可以不在使用多态（会有向上转型和向下转型的缺点，并且传参对象受到继承机制限制）
    //（局限性无法使用字段）
public<T> T getUnit(AbstractList<T> tList){
        T t;
       for (int i=tList.size()-1; i >= 0; i--){
            t=tList.get(i);
            if(t.equals(this))
                return t;
       }
       return null;
}
//泛型与多态，减少上下转型（但受到继承限制）
//可以单独创建一个类（例如碰撞对象类，字段int x,int y,int z...用不上的可以不使用）
    //好处就是不再需要强转类型因为可以返回任意类型
public<T extends TankModel> T getUnit(T t){
        if(t instanceof TankPlayer){
            t.setSelfLive(true);
        }
        t.setDirect(0);
        return null;
}
    public boolean isHit(Bullet bullet) {
        //解决遍历集合在集合内部移除集合的报错(反遍历法)
        FragileWall unit;
        for (int i = fragileWalls.size() - 1; i >= 0; i--) {
            unit = fragileWalls.get(i);
            if (bullet.getRect().intersects(unit.getRect())) {
                bullet.setBomb(new Bomb(unit.getX(), unit.getY()));
                fragileWalls.remove(unit);
                return true;
            }
        }
        return false;
    }

    //易碎墙单元判定方法
    public boolean isHit(TankModel tankModel) {
        //解决遍历集合在集合内部移除集合的报错(反遍历法)
        FragileWall unit;
        for (int i = fragileWalls.size() - 1; i >= 0; i--) {
            unit = fragileWalls.get(i);
            if (tankModel.getRect().intersects(unit.getRect())) {
                return true;
            }
        }
        return false;
    }
}

