package sims;

import model.*;
import myModel.*;

/**
 * Created by aka_npou on 04.12.2019.
 */
public class Sim_v3 {

    public static int ticks = 120;
    public static Vec2Double[][] steps = new Vec2Double[9+9][ticks];

    public static void  sim(Unit unit, Game game, Debug debug, UnitAction action) {

        //steps = new Vec2Double[9][ticks];

        chain(0, unit, unit.jumpState, unit.position, game, debug, action, 0, 0,0, true);

        chain( 1, unit, unit.jumpState, unit.position, game, debug, action, 0d, -1,0, true);
        chain(2, unit, unit.jumpState, unit.position, game, debug, action, 0d, 1,0, true);
        chain(3, unit, unit.jumpState, unit.position, game, debug, action, Constants.UNIT_X_SPEED_PER_TICK, 0d,0, true);
        chain(4, unit, unit.jumpState, unit.position, game, debug, action, -Constants.UNIT_X_SPEED_PER_TICK, 0d,0, true);

        chain(5, unit, unit.jumpState, unit.position, game, debug, action, Constants.UNIT_X_SPEED_PER_TICK, 1,0, true);
        chain(6, unit, unit.jumpState, unit.position, game, debug, action, -Constants.UNIT_X_SPEED_PER_TICK, 1,0, true);
        chain(7, unit, unit.jumpState, unit.position, game, debug, action, Constants.UNIT_X_SPEED_PER_TICK, -1,0, true);
        chain(8, unit, unit.jumpState, unit.position, game, debug, action, -Constants.UNIT_X_SPEED_PER_TICK, -1,0, true);

    }

    private static void chain(int chain, Unit unit, JumpState unitJumpState, Vec2Double unitPos, Game game, Debug debug, UnitAction action, double vx, double vy, int from, boolean branch) {

        Vec2Double p = new Vec2Double();

        p.x = unitPos.x;
        p.y = unitPos.y;

        JumpState jumpState = new JumpState(unitJumpState);

        //jumpState.speed /= Constants.TICKS_PER_SECOND;

        if (jumpState.maxTime==0 && p.y-(int)p.y<Constants.UNIT_Y_SPEED_PER_TICK && vy>=0) {
            //если внизу не пусто будет менее чем через тик то считаем что встали. и хотим не вниз

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


        for (int tick = from; tick<ticks; tick++) {

            //if(Constants.ON_DEBUG)
            //    System.out.println("tick "+tick);

            step(p, vx, vy, jumpState, unit, game, debug);

            //ошибки какие то, может провалиться
            if (p.y<1 || p.x<1 || p.y>World.y-1 || p.x>World.x-1) {
                for (int t=tick;t<ticks;t++) {
                    steps[chain][t] = new Vec2Double(0, 0);
                }
                return;
            }
            steps[chain][tick] = new Vec2Double(p.x, p.y);

            if (Constants.ON_DEBUG)
                if (tick%2==0)
                    debug.draw(new CustomData.Rect(new Vec2Float(p.x+unit.id/10f, p.y+unit.id/10f), new Vec2Float(0.1f, 0.1f), Constants.uc[unit.id-1]));//new ColorFloat(0,1,0,0.2f)));

            /*if (tick == 1 && branch) {
                for (int i=0;i<2;i++) {
                    steps[chain+9][i]=steps[chain][i];
                }
                chain(chain+9, unit, jumpState, p, game, debug, action, vx, vy==1?-1:1,2, false);
            }*/

            if (tick == 5 && branch) {
                for (int i=0;i<6;i++) {
                    steps[chain+9][i]=steps[chain][i];
                }
                chain(chain+9, unit, jumpState, p, game, debug, action, vx, vy==1?-1:1,6, false);
            }
        }


    }

    private static void step(Vec2Double p, double vx, double vy, JumpState jumpState, Unit unit, Game game, Debug debug) {

        double dx,dy;

        dx=vx;

        if (dx>0) {
            if (World.map[(int)(p.y)][(int)(p.x+vx + Constants.UNIT_W2)] == 1 ||
                    World.map[(int)(p.y + Constants.UNIT_H2)][(int)(p.x+vx + Constants.UNIT_W2)] == 1 ||
                    World.map[(int)(p.y + Constants.UNIT_H)][(int)(p.x+vx + Constants.UNIT_W2)] == 1) {

                dx=0d;

            }

            for (Unit u:game.getUnits()) {
                if (u.id == unit.id)
                    continue;

                if (p.x+vx+Constants.UNIT_W2>=u.position.x-Constants.UNIT_W2 && p.x+vx+Constants.UNIT_W2<=u.position.x+Constants.UNIT_W2
                        &&
                        ((p.y>=u.position.y && p.y<=u.position.y+Constants.UNIT_H)
                        ||
                        (p.y+Constants.UNIT_H>=u.position.y && p.y+Constants.UNIT_H<=u.position.y+Constants.UNIT_H))) {
                    dx=0d;
                    break;
                }
            }
        }

        if (dx<0) {
            if (World.map[(int)(p.y)][(int)(p.x+vx - Constants.UNIT_W2)] == 1 ||
                    World.map[(int)(p.y + Constants.UNIT_H2)][(int)(p.x+vx - Constants.UNIT_W2)] == 1 ||
                    World.map[(int)(p.y + Constants.UNIT_H)][(int)(p.x+vx - Constants.UNIT_W2)] == 1) {

                dx=0d;

            }

            for (Unit u:game.getUnits()) {
                if (u.id == unit.id)
                    continue;

                if (p.x+vx-Constants.UNIT_W2>=u.position.x-Constants.UNIT_W2 && p.x+vx-Constants.UNIT_W2<=u.position.x+Constants.UNIT_W2
                        &&
                        ((p.y>=u.position.y && p.y<=u.position.y+Constants.UNIT_H)
                                ||
                                (p.y+Constants.UNIT_H>=u.position.y && p.y+Constants.UNIT_H<=u.position.y+Constants.UNIT_H))) {
                    dx=0d;
                    break;
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

        if (dy<=0 && jumpState.speed == Constants.UNIT_Y_SPEED_PER_TICK && jumpState.maxTime < Constants.JUMP_TIME)
            dy = -Constants.UNIT_Y_SPEED_PER_TICK;


        //если скорость с трамплина то вверх
        if (jumpState.speed == Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD) {
            dy = Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;
        }

        if (jumpState.speed == 0) {
            dy = -Constants.UNIT_Y_SPEED_PER_TICK;
        }

        boolean canUp=true;
        boolean canDown=true;

        for (Unit u:game.getUnits()) {
            if (u.id == unit.id)
                continue;

            if (((p.x+Constants.UNIT_W2>=u.position.x-Constants.UNIT_W2 && p.x+Constants.UNIT_W2<=u.position.x+Constants.UNIT_W2)
                    ||
                    (p.x-Constants.UNIT_W2>=u.position.x-Constants.UNIT_W2 && p.x-Constants.UNIT_W2<=u.position.x+Constants.UNIT_W2))
                    &&
                    (p.y+Constants.UNIT_Y_SPEED_PER_TICK+Constants.UNIT_H>=u.position.y && p.y+Constants.UNIT_Y_SPEED_PER_TICK+Constants.UNIT_H<=u.position.y+Constants.UNIT_H)) {
                canUp=false;
                break;
            }
        }

        for (Unit u:game.getUnits()) {
            if (u.id == unit.id)
                continue;

            if (((p.x+Constants.UNIT_W2>=u.position.x-Constants.UNIT_W2 && p.x+Constants.UNIT_W2<=u.position.x+Constants.UNIT_W2)
                    ||
                    (p.x-Constants.UNIT_W2>=u.position.x-Constants.UNIT_W2 && p.x-Constants.UNIT_W2<=u.position.x+Constants.UNIT_W2))
                    &&
                    (p.y-Constants.UNIT_Y_SPEED_PER_TICK>=u.position.y && p.y-Constants.UNIT_Y_SPEED_PER_TICK<=u.position.y+Constants.UNIT_H)) {
                canDown=false;
                break;
            }
        }
        /*if (dy>0) {

            for (Unit u:game.getUnits()) {
                if (u.id == unit.id)
                    continue;

                if (((p.x+Constants.UNIT_W2>=u.position.x-Constants.UNIT_W2 && p.x+Constants.UNIT_W2<=u.position.x+Constants.UNIT_W2)
                      ||
                      (p.x-Constants.UNIT_W2>=u.position.x-Constants.UNIT_W2 && p.x-Constants.UNIT_W2<=u.position.x+Constants.UNIT_W2))
                    &&
                    (p.y+vy+Constants.UNIT_H>=u.position.y && p.y+vy+Constants.UNIT_H<=u.position.y+Constants.UNIT_H)) {
                    dy=0d;
                    break;
                }
            }

        }

        if (dy<0) {

            for (Unit u:game.getUnits()) {
                if (u.id == unit.id)
                    continue;

                if (((p.x+Constants.UNIT_W2>=u.position.x-Constants.UNIT_W2 && p.x+Constants.UNIT_W2<=u.position.x+Constants.UNIT_W2)
                        ||
                        (p.x-Constants.UNIT_W2>=u.position.x-Constants.UNIT_W2 && p.x-Constants.UNIT_W2<=u.position.x+Constants.UNIT_W2))
                        &&
                        (p.y+vy>=u.position.y && p.y+vy<=u.position.y+Constants.UNIT_H)) {
                    dy=0d;
                    break;
                }
            }

        }*/

        CanSpeed canSpeed = new CanSpeed();

        //падаю и хочу прыгнуть
        //прыгаю и хочу падать



        if (dy<0f) {
            //трамплин
            if (World.map[(int)(p.y)][(int)(p.x-Constants.UNIT_W2)]==4 || World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==4) {
                jumpState.canJump = true;
                jumpState.maxTime = Constants.JUMP_TIME_PAD;
                jumpState.canCancel = false;
                jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;
                p.y+=Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;
            } else {
                p.y += dy;
                check(p, -1, (int)vy, jumpState, unit, game, debug, canSpeed, true);
                if (!canSpeed.can) {
                    //считаем что приземлились
                    p.y -= dy;

                    p.y = (int) p.y + Constants.EPS;
                    jumpState.maxTime = Constants.JUMP_TIME;
                    jumpState.canJump = true;
                    jumpState.canCancel = true;
                    jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK;
                }
                if (canSpeed.toSpeed != 0) {
                    p.y -= dy;

                    if (canSpeed.toSpeed>0 && canUp || canSpeed.toSpeed<0 && canDown) {
                        p.y += canSpeed.toSpeed;
                        if (canSpeed.toSpeed<0) {
                            jumpState.maxTime=0;
                            jumpState.speed = 0;
                            jumpState.canJump = false;
                            jumpState.canCancel = false;
                        }
                    } else
                        canSpeed.toSpeed=0;

                    /*if (p.y - (int) p.y < Constants.UNIT_Y_SPEED_PER_TICK) {
                        p.y = (int) p.y;

                        jumpState.maxTime = Constants.JUMP_TIME;
                        jumpState.canJump = true;
                        jumpState.canCancel = true;
                        jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK;
                    }

                    if (canSpeed.toSpeed == Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD) {
                        jumpState.canJump = true;
                        jumpState.maxTime = Constants.JUMP_TIME_PAD;
                        jumpState.canCancel = false;
                        jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;
                    }*/
                } else {
                    //dy=canSpeed.toSpeed;
                    /*if (vy < 0) {
                        //ничего, летим вниз дальше
                    }

                    if (vy == 0) {
                        p.y -= dy;
                        p.y = (int) p.y;
                        jumpState.maxTime = Constants.JUMP_TIME;
                        jumpState.canJump = true;
                        jumpState.canCancel = true;
                        jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK;
                    }

                    if (vy > 0) {
                        p.y -= dy;
                        if (p.y - (int) p.y < Constants.UNIT_Y_SPEED_PER_TICK)
                            p.y = (int) p.y;

                        jumpState.maxTime = Constants.JUMP_TIME;
                        jumpState.canJump = true;
                        jumpState.canCancel = true;
                        jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK;
                    }*/
                }
            }
        }
        if (dy>0f) {
            //трамплин
            if (World.map[(int)(p.y)][(int)(p.x-Constants.UNIT_W2)]==4 || World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==4) {
                jumpState.canJump = true;
                jumpState.maxTime = Constants.JUMP_TIME_PAD;
                jumpState.canCancel = false;
                jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;
                p.y+=Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;
            } else {
                p.y += dy;
                p.y += Constants.UNIT_H;
                check(p, 1, (int)vy, jumpState, unit, game, debug, canSpeed, false);
                p.y -= Constants.UNIT_H;
                if (!canSpeed.can) {
                    p.y -= dy;

                    //p.y = (int)p.y;
                    jumpState.canJump = false;
                    jumpState.maxTime = 0;
                    jumpState.canCancel = false;
                    jumpState.speed = 0;
                }
                if (canSpeed.toSpeed != 0) {
                    p.y -= dy;
                    if (canSpeed.toSpeed>0 && canUp || canSpeed.toSpeed<0 && canDown)
                        p.y += canSpeed.toSpeed;
                    else
                        canSpeed.toSpeed=0;

                    /*if (canSpeed.toSpeed == Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD) {
                        jumpState.canJump = true;
                        jumpState.maxTime = Constants.JUMP_TIME_PAD;
                        jumpState.canCancel = false;
                        jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;
                    }*/
                } else {
                    if (!canUp)
                        p.y -= dy;

                    if (vy < 0) {

                    }

                    if (vy == 0) {

                    }

                    if (vy > 0) {

                    }
                }
            }
        }
        if (dy==0f) {
            //трамплин
            if (World.map[(int)(p.y)][(int)(p.x-Constants.UNIT_W2)]==4 || World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==4) {
                jumpState.canJump = true;
                jumpState.maxTime = Constants.JUMP_TIME_PAD;
                jumpState.canCancel = false;
                jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;
                p.y+=Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;
            } else {
                p.y -= Constants.UNIT_Y_SPEED_PER_TICK;
                check(p, 0, (int)vy, jumpState, unit, game, debug, canSpeed, true);
                if (!canSpeed.can)
                    p.y += Constants.UNIT_Y_SPEED_PER_TICK;
                if (canSpeed.toSpeed != 0) {
                    p.y += Constants.UNIT_Y_SPEED_PER_TICK;
                    if (canSpeed.toSpeed>0 && canUp || canSpeed.toSpeed<0 && canDown)
                        p.y += canSpeed.toSpeed;
                    else
                        canSpeed.toSpeed=0;

                    /*if (canSpeed.toSpeed == Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD) {
                        jumpState.canJump = true;
                        jumpState.maxTime = Constants.JUMP_TIME_PAD;
                        jumpState.canCancel = false;
                        jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;
                    }

                    if (canSpeed.toSpeed == -Constants.UNIT_Y_SPEED_PER_TICK) {
                        jumpState.canJump = false;
                        jumpState.maxTime = 0;
                        jumpState.canCancel = false;
                        jumpState.speed = 0;
                    }*/
                } else {
                    //ничего
                    /*if (vy == 0 && canSpeed.can) {
                        p.y += Constants.UNIT_Y_SPEED_PER_TICK;
                    }*/
                }
            }
        }

        if (canSpeed.can && canSpeed.toSpeed!=0)
            dy=canSpeed.toSpeed;

        if (dy<0)
            dy=canSpeed.toSpeed;
        /*if (canSpeed.toSpeed == Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD) {
            jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;
            jumpState.canJump = true;
            jumpState.maxTime = Constants.JUMP_TIME_PAD;
            jumpState.canCancel = false;
            p.y+=Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;
        }*/

        /*if(Constants.ON_DEBUG) {
            System.out.println("dy="+dy);
            System.out.println("c.c "+canSpeed.can+" c.ts "+canSpeed.toSpeed);
            System.out.println(jumpState.toString());
        }*/


        if (World.map[(int)(p.y)][(int)(p.x - Constants.UNIT_W2)] == 4 ||
                World.map[(int)(p.y)][(int)(p.x + Constants.UNIT_W2)] == 4) {
        } else {
            if (dy!=0)
                jumpState.maxTime -= Constants.TICK;
        }

    }

    private static void check(Vec2Double p, int vy, int qy, JumpState jumpState, Unit unit, Game game, Debug debug, CanSpeed canSpeed, boolean bottom) {

        if (World.map[(int)(p.y)][(int)(p.x-Constants.UNIT_W2)]==0 || (World.map[(int)(p.y)][(int)(p.x-Constants.UNIT_W2)]==2 && World.map[(int)(p.y+Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x-Constants.UNIT_W2)]==2)) {
            if (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==0 || (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==2 && World.map[(int)(p.y+Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x+Constants.UNIT_W2)]==2)) {

                canSpeed.can= true;
                canSpeed.toSpeed= vy==-1?-Constants.UNIT_Y_SPEED_PER_TICK:0;
                canSpeed.toSpeed= vy==1?0:-Constants.UNIT_Y_SPEED_PER_TICK;

                return;
            }

            if (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==1) {

                canSpeed.can= false;
                canSpeed.toSpeed= 0;

                return;
            }

            if (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==2) {

                canSpeed.can = true;
                canSpeed.toSpeed = 0;

                if (vy!=1 && qy>=0 && p.y+Constants.UNIT_Y_SPEED_PER_TICK - (int) (p.y+Constants.UNIT_Y_SPEED_PER_TICK) < Constants.UNIT_Y_SPEED_PER_TICK) {
                    p.y = (int) (p.y+Constants.UNIT_Y_SPEED_PER_TICK) + Constants.EPS;

                    jumpState.maxTime = Constants.JUMP_TIME;
                    jumpState.canJump = true;
                    jumpState.canCancel = true;
                    jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK;
                }

                return;
            }

            if (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==3) {

                canSpeed.can= true;

                if (World.map[(int)(p.y-(bottom?0:Constants.UNIT_H))][(int)(p.x)]==3 || World.map[(int)(p.y+Constants.UNIT_H2*(bottom?1:-1))][(int)(p.x)]==3) {
                    canSpeed.toSpeed = 0;

                    jumpState.maxTime = Constants.JUMP_TIME;
                    jumpState.canJump = true;
                    jumpState.canCancel = true;
                    jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK;

                } else
                    canSpeed.toSpeed= vy==1?0:-Constants.UNIT_Y_SPEED_PER_TICK;

                return;
            }

            if (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==4) {

                canSpeed.can= true;
                canSpeed.toSpeed= Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;

                jumpState.canJump = true;
                jumpState.maxTime = Constants.JUMP_TIME_PAD;
                jumpState.canCancel = false;
                jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;

                return;
            }
        }

        if (World.map[(int)(p.y)][(int)(p.x-Constants.UNIT_W2)]==1) {
            if (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==0) {

                canSpeed.can= false;
                canSpeed.toSpeed= 0;

                return;
            }

            if (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==1) {

                canSpeed.can= false;
                canSpeed.toSpeed= 0;

                return;
            }

            if (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==2) {

                canSpeed.can= false;
                canSpeed.toSpeed= 0;

                return;
            }

            if (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==3) {

                canSpeed.can= false;
                canSpeed.toSpeed= 0;

                return;
            }

            if (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==4) {

                canSpeed.can= false;
                canSpeed.toSpeed= 0;

                return;
            }
        }

        if (World.map[(int)(p.y)][(int)(p.x-Constants.UNIT_W2)]==2) {
            if (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==0 || (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==2 && World.map[(int)(p.y+Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x+Constants.UNIT_W2)]==2)) {

                canSpeed.can= true;
                canSpeed.toSpeed= 0;

                return;
            }

            if (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==1) {

                canSpeed.can= false;
                canSpeed.toSpeed= 0;

                return;
            }

            if (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==2) {

                canSpeed.can= true;
                canSpeed.toSpeed= 0;

                if (vy!=1 && qy>=0 && p.y+Constants.UNIT_Y_SPEED_PER_TICK - (int) (p.y+Constants.UNIT_Y_SPEED_PER_TICK) < Constants.UNIT_Y_SPEED_PER_TICK) {
                    p.y = (int) (p.y+Constants.UNIT_Y_SPEED_PER_TICK) + Constants.EPS;

                    jumpState.maxTime = Constants.JUMP_TIME;
                    jumpState.canJump = true;
                    jumpState.canCancel = true;
                    jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK;
                }

                return;
            }

            if (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==3) {

                canSpeed.can= true;
                canSpeed.toSpeed= 0;

                jumpState.maxTime = Constants.JUMP_TIME;
                jumpState.canJump = true;
                jumpState.canCancel = true;
                jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK;

                return;
            }

            if (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==4) {

                canSpeed.can= true;
                canSpeed.toSpeed= vy==-1?Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD:0;

                jumpState.canJump = true;
                jumpState.maxTime = Constants.JUMP_TIME_PAD;
                jumpState.canCancel = false;
                jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;

                return;
            }
        }

        if (World.map[(int)(p.y)][(int)(p.x-Constants.UNIT_W2)]==3) {
            if (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==0 || (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==2 && World.map[(int)(p.y+Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x+Constants.UNIT_W2)]==2)) {

                canSpeed.can= true;

                if (World.map[(int)(p.y-(bottom?0:Constants.UNIT_H))][(int)(p.x)]==3 || World.map[(int)(p.y+Constants.UNIT_H2*(bottom?1:-1))][(int)(p.x)]==3) {
                    canSpeed.toSpeed = 0;

                    jumpState.maxTime = Constants.JUMP_TIME;
                    jumpState.canJump = true;
                    jumpState.canCancel = true;
                    jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK;

                } else
                    canSpeed.toSpeed= vy==1?0:-Constants.UNIT_Y_SPEED_PER_TICK;

                return;
            }

            if (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==1) {

                canSpeed.can= false;
                canSpeed.toSpeed= 0;

                return;
            }

            if (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==2) {

                canSpeed.can= true;
                canSpeed.toSpeed= 0;

                if (vy!=1 && qy>=0 && p.y+Constants.UNIT_Y_SPEED_PER_TICK - (int) (p.y+Constants.UNIT_Y_SPEED_PER_TICK) < Constants.UNIT_Y_SPEED_PER_TICK) {
                    p.y = (int) (p.y+Constants.UNIT_Y_SPEED_PER_TICK) + Constants.EPS;

                    jumpState.maxTime = Constants.JUMP_TIME;
                    jumpState.canJump = true;
                    jumpState.canCancel = true;
                    jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK;
                }

                return;
            }

            if (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==3) {

                canSpeed.can= true;
                canSpeed.toSpeed= 0;

                jumpState.maxTime = Constants.JUMP_TIME;
                jumpState.canJump = true;
                jumpState.canCancel = true;
                jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK;

                return;
            }

            if (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==4) {

                canSpeed.can= true;
                //canSpeed.toSpeed= Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;//todo

                if (World.map[(int)(p.y-(bottom?0:Constants.UNIT_H))][(int)(p.x)]==3 || World.map[(int)(p.y+Constants.UNIT_H2*(bottom?1:-1))][(int)(p.x)]==3)
                    canSpeed.toSpeed= 0;
                else {
                    canSpeed.toSpeed = Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;

                    jumpState.canJump = true;
                    jumpState.maxTime = Constants.JUMP_TIME_PAD;
                    jumpState.canCancel = false;
                    jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;
                }

                return;
            }
        }

        if (World.map[(int)(p.y)][(int)(p.x-Constants.UNIT_W2)]==4) {
            if (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==0 || (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==2 && World.map[(int)(p.y+Constants.UNIT_Y_SPEED_PER_TICK)][(int)(p.x+Constants.UNIT_W2)]==2)) {

                canSpeed.can= true;
                canSpeed.toSpeed= vy==1?Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD:Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;

                jumpState.canJump = true;
                jumpState.maxTime = Constants.JUMP_TIME_PAD;
                jumpState.canCancel = false;
                jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;

                return;
            }

            if (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==1) {

                canSpeed.can= false;
                canSpeed.toSpeed= 0;

                return;
            }

            if (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==2) {

                canSpeed.can= true;
                canSpeed.toSpeed= vy==-1?Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD:0;

                jumpState.canJump = true;
                jumpState.maxTime = Constants.JUMP_TIME_PAD;
                jumpState.canCancel = false;
                jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;

                return;
            }

            if (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==3) {

                canSpeed.can= true;
                //canSpeed.toSpeed= 0;//todo

                if (World.map[(int)(p.y-(bottom?0:Constants.UNIT_H))][(int)(p.x)]==3 || World.map[(int)(p.y+Constants.UNIT_H2*(bottom?1:-1))][(int)(p.x)]==3) {
                    canSpeed.toSpeed = 0;

                    jumpState.maxTime = Constants.JUMP_TIME;
                    jumpState.canJump = true;
                    jumpState.canCancel = true;
                    jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK;

                } else {
                    canSpeed.toSpeed = Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;

                    jumpState.canJump = true;
                    jumpState.maxTime = Constants.JUMP_TIME_PAD;
                    jumpState.canCancel = false;
                    jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;
                }

                return;
            }

            if (World.map[(int)(p.y)][(int)(p.x+Constants.UNIT_W2)]==4) {

                canSpeed.can= true;
                canSpeed.toSpeed= Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;

                jumpState.canJump = true;
                jumpState.maxTime = Constants.JUMP_TIME_PAD;
                jumpState.canCancel = false;
                jumpState.speed = Constants.UNIT_Y_SPEED_PER_TICK_JUMP_PAD;

                return;
            }
        }
    }


}
