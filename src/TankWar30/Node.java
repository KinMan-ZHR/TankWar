package TankWar30;

//用来储存坦克类
public record Node(int x, int y, int direct, int category, int level, int Hp) {

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDirect() {
        return direct;
    }

    public int getCategory() {
        return category;
    }

    public int getLevel() {
        return level;
    }

    public int getHp() {
        return Hp;
    }

}

