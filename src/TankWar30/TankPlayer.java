package TankWar30;

import java.util.Vector;

public class TankPlayer extends TankModel {
    private boolean keyMove=false;
    public TankPlayer(int x, int y, int direct, int category, int level, boolean auto,boolean isIcon) {
        super(x, y, direct, category, level, auto);
        if (!isIcon) {
            setCool(new CoolDownTime(500))  ;
        }
    }
    public void drive(TankPlayer tankPlayer, Vector<TankEnemy> tankEnemies, Vector <Obstacle>obstacles) {
        if(!isKeyMove())
            return;
        if (collide(tankPlayer, tankEnemies)) {
            int distance;
            distance = minDistance(tankPlayer, tankEnemies);
            //行动前得设置方向
            move(obstacles);
            if (distance > minDistance(tankPlayer, tankEnemies))
                move(false);
        }else {
            move(obstacles);
//            if (collide(tankPlayer, tankEnemies))//保证不主动撞，这样我方无法推动敌方
//                move(false);
        }
    }

    public boolean isKeyMove() {
        return keyMove;
    }

    public void setKeyMove(boolean keyMove) {
        this.keyMove = keyMove;
    }
}
