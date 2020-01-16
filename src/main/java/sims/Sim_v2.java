package sims;

import model.*;
import myModel.Constants;
import myModel.World;

/**
 * Created by aka_npou on 04.12.2019.
 */
public class Sim_v2 {

    public static int ticks = 120;
    public static Vec2Float[][] steps;

    public static void  sim(UnitF unit, Game game, Debug debug, UnitAction action) {

        steps = new Vec2Float[9][ticks];

        chain(steps[0], unit, game, debug, action, 0, 0);

        chain(steps[1], unit, game, debug, action, 0d, -1);
        chain(steps[2], unit, game, debug, action, 0d, 1);
        chain(steps[3], unit, game, debug, action, Constants.UNIT_X_SPEED_PER_TICK, 0d);
        chain(steps[4], unit, game, debug, action, -Constants.UNIT_X_SPEED_PER_TICK, 0d);

        chain(steps[5], unit, game, debug, action, Constants.UNIT_X_SPEED_PER_TICK, 1);
        chain(steps[6], unit, game, debug, action, -Constants.UNIT_X_SPEED_PER_TICK, 1);
        chain(steps[7], unit, game, debug, action, Constants.UNIT_X_SPEED_PER_TICK, -1);
        chain(steps[8], unit, game, debug, action, -Constants.UNIT_X_SPEED_PER_TICK, -1);

        /*Unit enemy = null;

        for (UnitF u:game.getUnits()) {
            if (u.getPlayerId() != unit.getPlayerId()) {
                enemy = u;
                break;
            }
        }*/

        //System.out.println(game.getCurrentTick() + "e tj" + enemy.jumpState.getMaxTime() + " cj=" + enemy.jumpState.isCanJump() + " cc=" + enemy.jumpState.isCanCancel() + " y=" + enemy.position.y + " s=" + enemy.jumpState.speed);
        //System.out.println(game.getCurrentTick() + "u tj" + unit.jumpState.getMaxTime() + " cj=" + unit.jumpState.isCanJump() + " cc=" + unit.jumpState.isCanCancel() + " y=" + unit.position.y + " s=" + unit.jumpState.speed);

    }

    private static void chain(Vec2Float[] chain, UnitF unit, Game game, Debug debug, UnitAction action, double vx, double vy) {

        Vec2Double p = new Vec2Double();

        p.x = unit.position.x;
        p.y = unit.position.y;

        JumpState jumpState = new JumpState(unit.jumpState);

        jumpState.speed /= Constants.TICKS_PER_SECOND;

        if (jumpState.maxTime==0 && p.y-(int)p.y<Constants.UNIT_Y_SPEED_PER_TICK && vy>=0) {
            //todo если внизу не пусто будет менее чем через тик то считаем что встали. и хотим не вниз

            if((World.map[(int)(p.y-Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x - Constants.UNIT_W2)] == 2 ||
                    World.map[(int)(p.y-Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x + Constants.UNIT_W2)] == 2)
                || (World.map[(int)(p.y-Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x - Constants.UNIT_W2)] == 1 ||
                    World.map[(int)(p.y-Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x + Constants.UNIT_W2)] == 1)
                || (World.map[(int)(p.y-Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x)] == 3 ||
                    World.map[(int)(p.y-Constants.UNIT_Y_SPEED_PER_TICK+Constants.UNIT_H2)][(int)(p.x)] == 3)) {

                p.y = (int) p.y;
                jumpState.maxTime = Constants.JUMP_TIME;
                jumpState.canJump = true;
                jumpState.canCancel = true;
                jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK;
            }
        }


        for (int tick = 0; tick<ticks; tick++) {

            step(p, vx, vy, jumpState, unit, game, debug);

            chain[tick] = new Vec2Float(p.x, p.y);

            if (Constants.ON_DEBUG)
                debug.draw(new CustomData.Rect(new Vec2Float(p.x, p.y), new Vec2Float(0.1f, 0.1f), new ColorFloat(0,1,0,1)));
        }


    }

    private static void step(Vec2Double p, double vx, double vy, JumpState jumpState, UnitF unit, Game game, Debug debug) {

        double dx,dy;

        dx=vx;

        //todo проверка на юнита
        if (dx>0) {
            if (World.map[(int)(p.y)][(int)(p.x+vx + Constants.UNIT_W2)] == 1 ||
                    World.map[(int)(p.y + Constants.UNIT_H2)][(int)(p.x+vx + Constants.UNIT_W2)] == 1 ||
                    World.map[(int)(p.y + Constants.UNIT_H)][(int)(p.x+vx + Constants.UNIT_W2)] == 1) {

                dx=0d;

            }

            for (UnitF u:game.getUnits()) {
                if (u.id == unit.id)
                    continue;

                if (u.position.x-Constants.UNIT_W2<p.x+vx + Constants.UNIT_W2 && u.position.x+Constants.UNIT_W2>p.x+vx + Constants.UNIT_W2
                        && u.position.y<=unit.position.y+Constants.UNIT_H
                        && u.position.y+Constants.UNIT_H>=unit.position.y) {
                    dx=0d;
                }
            }
        }

        if (dx<0) {
            if (World.map[(int)(p.y)][(int)(p.x+vx - Constants.UNIT_W2)] == 1 ||
                    World.map[(int)(p.y + Constants.UNIT_H2)][(int)(p.x+vx - Constants.UNIT_W2)] == 1 ||
                    World.map[(int)(p.y + Constants.UNIT_H)][(int)(p.x+vx - Constants.UNIT_W2)] == 1) {

                dx=0d;

            }

            for (UnitF u:game.getUnits()) {
                if (u.id == unit.id)
                    continue;

                if (u.position.x+Constants.UNIT_W2>p.x+vx - Constants.UNIT_W2 && u.position.x-Constants.UNIT_W2<p.x+vx - Constants.UNIT_W2
                        && u.position.y<=unit.position.y+Constants.UNIT_H
                        && u.position.y+Constants.UNIT_H>=unit.position.y) {
                    dx=0d;
                }
            }
        }
        p.x+=dx;

        //куда хотим со скоростью
        dy=vy*jumpState.speed;

        //если ноль времени прыжка то вниз
        if (jumpState.maxTime <= 0) {
            jumpState.speed = 0;
            jumpState.canJump = false;
            jumpState.canCancel = false;
            dy=-Constants.UNIT_Y_SPEED_PER_TICK;
        }

        if (vy>0 && !jumpState.canJump)//уже падаю или прекратил прыгать
            dy=0;
        if (vy<0 && !jumpState.canCancel)//хочу вниз, но трамплин подбросил
            dy=0;

        //если скорость с трамплина то вверх
        if (jumpState.speed == Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD) {
            dy = Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;
        }

        if (jumpState.speed == 0) {
            dy = -Constants.UNIT_Y_SPEED_PER_TICK;
        }

        if (dy>0) {

            for (UnitF u:game.getUnits()) {
                if (u.id == unit.id)
                    continue;

                if (u.position.y<p.y+dy + Constants.UNIT_H && u.position.y+ Constants.UNIT_H>p.y+dy + Constants.UNIT_H
                        && u.position.x+Constants.UNIT_W2>=unit.position.x-Constants.UNIT_W2
                        && u.position.x-Constants.UNIT_W2<=unit.position.x+Constants.UNIT_W2) {
                    dy=0d;
                }
            }

        }

        if (dy<0) {

            for (UnitF u:game.getUnits()) {
                if (u.id == unit.id)
                    continue;

                if (u.position.y+Constants.UNIT_H>p.y+dy && u.position.y<p.y+dy
                        && u.position.x+Constants.UNIT_W2>=unit.position.x-Constants.UNIT_W2
                        && u.position.x-Constants.UNIT_W2<=unit.position.x+Constants.UNIT_W2) {
                    dy=0d;
                }
            }

        }

        //потолок
        if (dy>0) {
            //вверху стена
            if(World.map[(int)(p.y+dy + Constants.UNIT_H)][(int)(p.x - Constants.UNIT_W2)] == 1 ||
                    World.map[(int)(p.y+dy + Constants.UNIT_H)][(int)(p.x + Constants.UNIT_W2)] == 1) {

                //погрешность что должен попасть в потолок с трамплина, но не учитываю
                jumpState.canJump = false;
                jumpState.maxTime = 0;
                jumpState.canCancel = false;
                jumpState.speed = 0;

                dy=-Constants.UNIT_Y_SPEED_PER_TICK;

            }

            //вверху лестница
            if(World.map[(int)(p.y+dy)][(int)(p.x)] == 3 ||
                    World.map[(int)(p.y+dy+Constants.UNIT_H2)][(int)(p.x)] == 3) {

                jumpState.canJump = true;
                jumpState.maxTime = Constants.JUMP_TIME;
                jumpState.canCancel = true;
                jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK;

            }

           /* //внизу платформа, тормозим, тк так можно выше
            if(jumpState.canCancel && (World.map[(int)(p.y-2*Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x - Constants.UNIT_W2)] == 2 ||
                    World.map[(int)(p.y-2*Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x + Constants.UNIT_W2)] == 2)
                    && (World.map[(int)(p.y-1)][(int)(p.x - Constants.UNIT_W2)] == 2 ||
                    World.map[(int)(p.y-1)][(int)(p.x + Constants.UNIT_W2)] == 2)) {

                jumpState.canJump = true;
                jumpState.maxTime = Constants.JUMP_TIME;
                jumpState.canCancel = true;
                jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK;

                //мы хотим вниз
                if (vy>=0) {
                    dy=0;
                }


            }*/

        }

        if (dy==0 || dy<0) {

            //трамплин - прыгаем
            if(World.map[(int)(p.y)][(int)(p.x - Constants.UNIT_W2)] == 4 ||
                    World.map[(int)(p.y)][(int)(p.x + Constants.UNIT_W2)] == 4) {

                jumpState.canJump = true;
                jumpState.maxTime = Constants.JUMP_TIME_PAD;
                jumpState.canCancel = false;
                jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;

                dy = Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;

            } else

            //трамплин внизу - прыгаем
            if(World.map[(int)(p.y-Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x - Constants.UNIT_W2)] == 4 ||
                    World.map[(int)(p.y-Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x + Constants.UNIT_W2)] == 4) {

                jumpState.canJump = true;
                jumpState.maxTime = Constants.JUMP_TIME_PAD;
                jumpState.canCancel = false;
                jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;

                dy = Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;

            } else

            //внизу платформа
            if((World.map[(int)(p.y-Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x - Constants.UNIT_W2)] == 2 ||
                    World.map[(int)(p.y-Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x + Constants.UNIT_W2)] == 2)
                    && (World.map[(int)(p.y-1)][(int)(p.x - Constants.UNIT_W2)] == 2 ||
                    World.map[(int)(p.y-1)][(int)(p.x + Constants.UNIT_W2)] == 2)) {

                jumpState.canJump = true;
                jumpState.maxTime = Constants.JUMP_TIME;
                jumpState.canCancel = true;
                jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK;

                //мы хотим вниз
                if (vy>=0) {
                    dy=0;
                }


            } else

            //внизу стена
            if(World.map[(int)(p.y-Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x - Constants.UNIT_W2)] == 1 ||
                    World.map[(int)(p.y-Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x + Constants.UNIT_W2)] == 1) {

                jumpState.canJump = true;
                jumpState.maxTime = Constants.JUMP_TIME;
                jumpState.canCancel = true;
                jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK;

                if (vy>0) {
                    dy=Constants.UNIT_Y_SPEED_PER_TICK;
                } else {
                    dy=0;
                }

            } else

            //внизу лестница
            if(World.map[(int)(p.y-Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x)] == 3 ||
                    World.map[(int)(p.y-Constants.UNIT_Y_SPEED_PER_TICK+Constants.UNIT_H2)][(int)(p.x)] == 3) {

                jumpState.canJump = true;
                jumpState.maxTime = Constants.JUMP_TIME;
                jumpState.canCancel = true;
                jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK;

                if (vy > 0) {
                    dy = Constants.UNIT_Y_SPEED_PER_TICK;
                } else if (vy == 0) {
                    dy = 0;
                }


            } else

            //внизу пусто
            if((World.map[(int)(p.y-Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x - Constants.UNIT_W2)] == 0 ||
                    World.map[(int)(p.y-Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x - Constants.UNIT_W2)] == 3 ||
                    World.map[(int)(p.y-Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x - Constants.UNIT_W2)] == 2)&&
                    (World.map[(int)(p.y-Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x + Constants.UNIT_W2)] == 0 ||
                    World.map[(int)(p.y-Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x - Constants.UNIT_W2)] == 3 ||
                    World.map[(int)(p.y-Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x - Constants.UNIT_W2)] == 2)) {

                jumpState.canJump = false;
                jumpState.maxTime = 0;
                jumpState.canCancel = false;
                jumpState.speed = 0;

                dy=-Constants.UNIT_Y_SPEED_PER_TICK;

            }
        }

        p.y+=dy;

        if (World.map[(int)(p.y)][(int)(p.x - Constants.UNIT_W2)] == 4 ||
                World.map[(int)(p.y)][(int)(p.x + Constants.UNIT_W2)] == 4) {
        } else {
            jumpState.maxTime -= Constants.TICK;
        }

    }

}
