package sims;

import model.*;
import myModel.Constants;
import myModel.World;

/**
 * Created by aka_npou on 04.12.2019.
 */
public class Sim_v1 {

    public static int ticks = 60;
    public static Vec2Float[][] steps;

    public static void  sim(Unit unit, Game game, Debug debug, UnitAction action) {

        steps = new Vec2Float[8][ticks];

        chain(steps[0], unit, game, debug, action, Constants.UNIT_X_SPEED_PER_TICK, 0d);
        chain(steps[1], unit, game, debug, action, -Constants.UNIT_X_SPEED_PER_TICK, 0d);
        chain(steps[2], unit, game, debug, action, 0d, Constants.UNIT_Y_SPEED_PER_TICK);
        chain(steps[3], unit, game, debug, action, 0d, -Constants.UNIT_Y_SPEED_PER_TICK);

        chain(steps[4], unit, game, debug, action, Constants.UNIT_X_SPEED_PER_TICK, Constants.UNIT_Y_SPEED_PER_TICK);
        chain(steps[5], unit, game, debug, action, -Constants.UNIT_X_SPEED_PER_TICK, Constants.UNIT_Y_SPEED_PER_TICK);
        chain(steps[6], unit, game, debug, action, Constants.UNIT_X_SPEED_PER_TICK, -Constants.UNIT_Y_SPEED_PER_TICK);
        chain(steps[7], unit, game, debug, action, -Constants.UNIT_X_SPEED_PER_TICK, -Constants.UNIT_Y_SPEED_PER_TICK);

        Unit enemy = null;

        for (Unit u:game.getUnits()) {
            if (u.getPlayerId() != unit.getPlayerId()) {
                enemy = u;
                break;
            }
        }

        System.out.println(game.getCurrentTick() + " tj" + enemy.jumpState.getMaxTime() + " cj=" + enemy.jumpState.isCanJump() + " cc=" + enemy.jumpState.isCanCancel() + " y=" + enemy.position.y);

    }

    private static void chain(Vec2Float[] chain, Unit unit, Game game, Debug debug, UnitAction action, double vx, double vy) {

        Vec2Double p = new Vec2Double();

        p.x = unit.position.x;
        p.y = unit.position.y;

        JumpState jumpState = new JumpState(unit.jumpState);
        jumpState.speed /= Constants.TICKS_PER_SECOND;

        for (int tick = 0; tick<ticks; tick++) {

            step(p, vx, vy, jumpState);
            /*p.x+=vx;

            if (vy!=0 && jumpState.canJump) {
                p.y += vy;
                jumpState.maxTime-=Constants.TICK;
            }

            if(checkWall(p, jumpState)) {
                p.x-=vx;
                p.y-=vy;
            }

            checkEmpty(p, vx, vy);

            chain[tick] = new Vec2Float(p.x, p.y);

            debug.draw(new CustomData.Rect(new Vec2Float(p.x, p.y), new Vec2Float(0.1f, 0.1f), new ColorFloat(0,1,0,1)));

            if (jumpState.maxTime <= 0) {
                jumpState.canJump=false;
                jumpState.canCancel=false;
            }*/
            chain[tick] = new Vec2Float(p.x, p.y);

            debug.draw(new CustomData.Rect(new Vec2Float(p.x, p.y), new Vec2Float(0.1f, 0.1f), new ColorFloat(0,1,0,1)));
        }


    }

    private static void step(Vec2Double p, double vx, double vy, JumpState jumpState) {

        double dx,dy;

        dx=vx;
        dy=vy;

        if (jumpState.maxTime <= 0) {
            jumpState.canJump = false;
            jumpState.maxTime = 0;
            jumpState.canCancel = false;
            dy=0;
        }

        if (dx>0) {
            if (World.map[(int)(p.y)][(int)(p.x+vx + Constants.UNIT_W2)] == 1 ||
                    World.map[(int)(p.y + Constants.UNIT_H2)][(int)(p.x+vx + Constants.UNIT_W2)] == 1 ||
                    World.map[(int)(p.y + Constants.UNIT_H)][(int)(p.x+vx + Constants.UNIT_W2)] == 1) {
                dx=0d;
            }
        }

        if (dx<0) {
            if (World.map[(int)(p.y)][(int)(p.x+vx - Constants.UNIT_W2)] == 1 ||
                    World.map[(int)(p.y + Constants.UNIT_H2)][(int)(p.x+vx - Constants.UNIT_W2)] == 1 ||
                    World.map[(int)(p.y + Constants.UNIT_H)][(int)(p.x+vx - Constants.UNIT_W2)] == 1) {
                dx=0d;
            }
        }
        p.x+=dx;

        if (vy>0 && !jumpState.canJump)
            dy=0;
        if (vy<0 && !jumpState.canCancel)
            dy=0;

        if (dy>0) {
            if(World.map[(int)(p.y+vy + Constants.UNIT_H)][(int)(p.x - Constants.UNIT_W2)] == 1 ||
                    World.map[(int)(p.y+vy + Constants.UNIT_H)][(int)(p.x + Constants.UNIT_W2)] == 1) {
                jumpState.canJump = false;
                jumpState.maxTime = 0;
                jumpState.canCancel = false;

                dy=-jumpState.speed;
            }
        }

        if(World.map[(int)(p.y-Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x - Constants.UNIT_W2)] != 0 ||
                World.map[(int)(p.y-Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x + Constants.UNIT_W2)] != 0) {
            jumpState.canJump = true;
            jumpState.maxTime = Constants.JUMP_TIME;
            jumpState.canCancel = true;

        }

        if (dy<0) {
            if (World.map[(int) (p.y - Constants.UNIT_Y_SPEED_PER_TICK)][(int) (p.x - Constants.UNIT_W2)] == 1 ||
                    World.map[(int) (p.y - Constants.UNIT_Y_SPEED_PER_TICK)][(int) (p.x + Constants.UNIT_W2)] == 1) {
                dy = 0;

            }
        }

        if (dy==0) {
            if(World.map[(int)(p.y-Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x - Constants.UNIT_W2)] == 0 &&
                    World.map[(int)(p.y-Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x + Constants.UNIT_W2)] == 0) {
                jumpState.canJump = false;
                jumpState.maxTime = 0;
                jumpState.canCancel = false;

                dy=-Constants.UNIT_Y_SPEED_PER_TICK;
            }
        }

        p.y+=dy;

        if (dy>0) {
            jumpState.maxTime-=Constants.TICK;
        }

    }

    private static void checkWall(Vec2Double p, double vx, double vy) {

        //вверху стена
        if(World.map[(int)(p.y + Constants.UNIT_H)][(int)(p.x - Constants.UNIT_W2)] == 1 ||
                World.map[(int)(p.y + Constants.UNIT_H)][(int)(p.x + Constants.UNIT_W2)] == 1)
            p.y-=Constants.UNIT_Y_SPEED_PER_TICK;

        //внизу стена
        if(World.map[(int)(p.y)][(int)(p.x - Constants.UNIT_W2)] == 1 ||
                World.map[(int)(p.y)][(int)(p.x + Constants.UNIT_W2)] == 1)
            p.y+=Constants.UNIT_Y_SPEED_PER_TICK;

        //справа стена
        if (World.map[(int)(p.y)][(int)(p.x + Constants.UNIT_W2)] == 1 ||
                World.map[(int)(p.y + Constants.UNIT_H2)][(int)(p.x + Constants.UNIT_W2)] == 1 ||
                World.map[(int)(p.y + Constants.UNIT_H)][(int)(p.x + Constants.UNIT_W2)] == 1)
            p.x-=Constants.UNIT_X_SPEED_PER_TICK;

        //слева стена
        if (World.map[(int)(p.y)][(int)(p.x - Constants.UNIT_W2)] == 1 ||
                World.map[(int)(p.y + Constants.UNIT_H2)][(int)(p.x - Constants.UNIT_W2)] == 1 ||
                World.map[(int)(p.y + Constants.UNIT_H)][(int)(p.x - Constants.UNIT_W2)] == 1)
            p.x+=Constants.UNIT_X_SPEED_PER_TICK;

    }

    private static boolean checkWall(Vec2Double p, JumpState jumpState) {

        //вверху стена
        if(World.map[(int)(p.y + Constants.UNIT_H)][(int)(p.x - Constants.UNIT_W2)] == 1 ||
                World.map[(int)(p.y + Constants.UNIT_H)][(int)(p.x + Constants.UNIT_W2)] == 1) {
            jumpState.canJump = false;
            jumpState.maxTime = 0;
            jumpState.canCancel = false;

            p.y-=jumpState.speed;
            return true;
        }

        //внизу стена
        if(World.map[(int)(p.y)][(int)(p.x - Constants.UNIT_W2)] == 1 ||
                World.map[(int)(p.y)][(int)(p.x + Constants.UNIT_W2)] == 1) {
            jumpState.canJump = true;
            jumpState.maxTime = Constants.JUMP_TIME;
            jumpState.canCancel = true;

            return true;
        }

        //справа стена
        if (World.map[(int)(p.y)][(int)(p.x + Constants.UNIT_W2)] == 1 ||
                World.map[(int)(p.y + Constants.UNIT_H2)][(int)(p.x + Constants.UNIT_W2)] == 1 ||
                World.map[(int)(p.y + Constants.UNIT_H)][(int)(p.x + Constants.UNIT_W2)] == 1)
            return true;

        //слева стена
        if (World.map[(int)(p.y)][(int)(p.x - Constants.UNIT_W2)] == 1 ||
                World.map[(int)(p.y + Constants.UNIT_H2)][(int)(p.x - Constants.UNIT_W2)] == 1 ||
                World.map[(int)(p.y + Constants.UNIT_H)][(int)(p.x - Constants.UNIT_W2)] == 1)
            return true;

        return false;
    }

    private static void checkEmpty(Vec2Double p, double vx, double vy) {

        //если не ноль, то мы и так прибавим/убавим y, а тут типа гравитация
        if (vy != 0d)
            return;

        p.y-=Constants.UNIT_Y_SPEED_PER_TICK;

        //внизу стала стена
        if(World.map[(int)(p.y)][(int)(p.x - Constants.UNIT_W2)] == 1 ||
                World.map[(int)(p.y)][(int)(p.x + Constants.UNIT_W2)] == 1)
            p.y+=Constants.UNIT_Y_SPEED_PER_TICK;

    }
}
