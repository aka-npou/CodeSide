package strategy;

import model.*;
import myModel.*;
import sims.*;

/**
 * Created by aka_npou on 30.11.2019.
 */

//уклонения
public class Strategy_v2 extends AkaNpouStrategy {
    long s,f;
    Vec2Int lastP = new Vec2Int(-1,-1);

    int mI;

    @Override
    public UnitAction getAction(Unit unit, Game game, Debug debug) {

        if (Constants.LOCAL && game.getCurrentTick()>=Constants.STOP_TICK)
            Constants.ON_DEBUG=true;

        if(Constants.ON_DEBUG)
            System.out.println("TICK "+game.getCurrentTick());



        if (game.getCurrentTick() == 0) {
            world.setMap(game.getLevel());
            world.setPatencyMap(game.getLevel(), unit.position);


            world.print(World.patencyMap, debug);
            World.liMap = new int[World.y][World.x];
            Li.setVectors();

            if (game.getUnits().length>=4)
                Constants.is2x2=true;

            s = System.nanoTime();
            world.setMaps(game, unit);
            f = System.nanoTime();
            System.out.println("maps " + (f - s));

            //чтобы не было ошибок нулевой тик стоим, тк он в падении
            UnitAction action = new UnitAction();
            action.aim = Vec2Double.ZERO;
            return action;
        }

        if (game.getCurrentTick()>currentTick) {
            s = System.nanoTime();
            world.actualMaps(game, unit);
            f = System.nanoTime();
            System.out.println("act " + (f - s));

            Dodge.setBullets(unit, game, debug);

            for (Unit u:game.getUnits()) {
                u.jumpState.speed /= Constants.TICKS_PER_SECOND;
            }

            currentTick=game.getCurrentTick();
        }

        if (lastP.x==-1 && lastP.y==-1 || (int)unit.position.x!=lastP.x || (int)unit.position.y!=lastP.y) {
            s = System.nanoTime();
            world.setLiMap(unit.position);
            f = System.nanoTime();
            System.out.println("li " + (f - s));
            world.print(World.liMap, debug);

            lastP.x=(int)unit.position.x;
            lastP.y=(int)unit.position.y;
        }


        getNearestEnemy(unit, game, debug);

        drawSomething(unit, game, debug);

        if (needDodge(unit, game, debug)) {
            unitStatus = UnitStatus.Dodge;
        } else if (unit.weapon == null || unit.weapon.typ == WeaponType.ROCKET_LAUNCHER) {
            unitStatus = UnitStatus.GoToWeapon;
        } else if (unit.health<game.getProperties().getUnitMaxHealth() && mapHaveHP(game)) {
            unitStatus = UnitStatus.GoToHP;
        } else {
            if (enemyNearly(unit, game, debug)) {
                unitStatus = UnitStatus.GoFromEnemy;
            } else if (enemyFar(unit, game, debug)) {
                unitStatus = UnitStatus.GoToEnemy;
            } else
                unitStatus = UnitStatus.Wait;
        }


        if (Constants.ON_DEBUG)
            debug.draw(new CustomData.Log(unitStatus.toString()));

        UnitAction action = new UnitAction();
        action.aim = Vec2Double.ZERO;

        s = System.nanoTime();
        Sim_v3.sim(unit, game, debug, action);
        f = System.nanoTime();
        System.out.println("sim " + (f-s));


        s = System.nanoTime();
        dodge(unit, game, debug, action);
        f = System.nanoTime();
        System.out.println("dodge " + (f-s));

        if (unitStatus == UnitStatus.GoToWeapon) {
            goToWeapon(unit, game, debug, action);
        }

        if (unitStatus == UnitStatus.GoToHP) {
            goToHP(unit, game, debug, action);
        }

        if (unitStatus == UnitStatus.GoToEnemy) {
            goToEnemy(unit, game, debug, action);
        }

        if (unitStatus == UnitStatus.GoFromEnemy) {
            goFromEnemy(unit, game, debug, action);
        }

        s = System.nanoTime();
        shoot.canShoot(unit, nearestEnemy, debug, action, game);
        f = System.nanoTime();
        System.out.println("shoot " + (f-s));


        return action;
    }

    private void goToWeapon(Unit unit, Game game, Debug debug, UnitAction action) {

        LootBox nearestWeapon = null;
        for (LootBox lootBox : game.getLootBoxes()) {
            if (lootBox.getItem().TAG == 1) {
                Item.Weapon w = (Item.Weapon)lootBox.item;
                if (w.getWeaponType() !=WeaponType.ROCKET_LAUNCHER) {
                    /*if (nearestWeapon == null || distanceSqr(unit.getPosition(),
                            lootBox.getPosition()) < distanceSqr(unit.getPosition(), nearestWeapon.getPosition())) {
                        nearestWeapon = lootBox;
                    }*/
                    if (nearestWeapon == null || World.liMap[(int)lootBox.position.y][(int)lootBox.position.x]<World.liMap[(int)nearestWeapon.position.y][(int)nearestWeapon.position.x])
                        nearestWeapon = lootBox;
                }
            }
        }

        Vec2Double minP = getP(nearestWeapon.position, unit, game, debug, action, true, null);

        action.setVelocity((minP.x - unit.position.x)*Constants.TICKS_PER_SECOND);
        if (minP.y-unit.position.y>Constants.UNIT_Y_SPEED_PER_TICK/2d)
            action.jump=true;

        if (minP.y-unit.position.y<-Constants.UNIT_Y_SPEED_PER_TICK/2d && Sim_v3.steps[mI][2].y<Sim_v3.steps[mI][1].y)
            action.jumpDown=true;

        action.swapWeapon=true;

        if (Constants.ON_DEBUG)
            debug.draw(new CustomData.Line(new Vec2Float(unit.position.x, unit.position.y), new Vec2Float(nearestWeapon.position.x, nearestWeapon.position.y), 0.1f, new ColorFloat(0.1f, 0.1f, 0.1f, 1f)));

    }

    private void goToHP(Unit unit, Game game, Debug debug, UnitAction action) {
        LootBox nearestHP = null;
        for (LootBox lootBox : game.getLootBoxes()) {
            if (lootBox.getItem().TAG == 0) {
                /*if (nearestHP == null || distanceSqr(unit.getPosition(),
                        lootBox.getPosition()) < distanceSqr(unit.getPosition(), nearestHP.getPosition())) {
                    nearestHP = lootBox;
                }*/
                if (nearestHP == null || World.liMap[(int)lootBox.position.y][(int)lootBox.position.x]<World.liMap[(int)nearestHP.position.y][(int)nearestHP.position.x])
                    nearestHP = lootBox;
            }
        }

        Vec2Double minP = getP(nearestHP.position, unit, game, debug, action, true, null);

        action.setVelocity((minP.x - unit.position.x)*Constants.TICKS_PER_SECOND);
        if (minP.y-unit.position.y>Constants.UNIT_Y_SPEED_PER_TICK/2d)
            action.jump=true;

        if (minP.y-unit.position.y<-Constants.UNIT_Y_SPEED_PER_TICK/2d && Sim_v3.steps[mI][2].y<Sim_v3.steps[mI][1].y)
            action.jumpDown=true;

    }

    private void goToEnemy(Unit unit, Game game, Debug debug, UnitAction action) {

        Vec2Double minP = getP(nearestEnemy.position, unit, game, debug, action, true, nearestEnemy);

        action.setVelocity((minP.x - unit.position.x)*Constants.TICKS_PER_SECOND);
        if (minP.y-unit.position.y>Constants.UNIT_Y_SPEED_PER_TICK/2d)
            action.jump=true;

        if (minP.y-unit.position.y<-Constants.UNIT_Y_SPEED_PER_TICK/2d && Sim_v3.steps[mI][2].y<Sim_v3.steps[mI][1].y)
            action.jumpDown=true;

    }

    private void goFromEnemy(Unit unit, Game game, Debug debug, UnitAction action) {

        Vec2Double maxP = getP(nearestEnemy.position, unit, game, debug, action, false, nearestEnemy);

        action.setVelocity((maxP.x - unit.position.x)*Constants.TICKS_PER_SECOND);
        if (maxP.y-unit.position.y>Constants.UNIT_Y_SPEED_PER_TICK/2d)
            action.jump=true;

        if (maxP.y-unit.position.y<-Constants.UNIT_Y_SPEED_PER_TICK/2d && Sim_v3.steps[mI][2].y<Sim_v3.steps[mI][1].y)
            action.jumpDown=true;
    }

    private boolean mapHaveHP(Game game) {
        for (LootBox lootBox : game.getLootBoxes()) {
            if (lootBox.getItem().TAG == 0) {
                return true;
            }
        }

        return false;
    }

    private boolean needDodge(Unit unit, Game game, Debug debug) {

        if (true)
            return false;

        if (game.getBullets().length == 0)
            return false;

        for (int b=0; b<Dodge.bullets.size();b++) {
            //выкинуть свои пули
            if (game.getBullets()[b].unitId ==  unit.id && game.getBullets()[b].explosionParams == null)
                continue;

            //if (distanceSqr(game.getBullets()[b].position.x, game.getBullets()[b].position.y, unit.position.x, unit.position.y+Constants.UNIT_H2) >= 9) {
                //выкинуть пули которые уже точно не в нас, позиция и скорость не в нашу сторону
                if (game.getBullets()[b].position.x + 4f > unit.position.x + Constants.UNIT_W2 && Dodge.bullets.get(b).x < 0)
                    return true;

                if (game.getBullets()[b].position.x -4f < unit.position.x - Constants.UNIT_W2 && Dodge.bullets.get(b).x > 0)
                    return true;

                if (game.getBullets()[b].position.y + 4f > unit.position.y + Constants.UNIT_H && Dodge.bullets.get(b).y < 0)
                    return true;

                if (game.getBullets()[b].position.y - 4f < unit.position.y && Dodge.bullets.get(b).y > 0)
                    return true;
            //}

        }

        return false;
    }

    private void dodge(Unit unit, Game game, Debug debug, UnitAction action) {
        Dodge.dodge(unit, game, debug, action);
    }

    private boolean enemyNearly(Unit unit, Game game, Debug debug) {

        return distanceSqr(unit.position, nearestEnemy.position) < 64;

    }

    private boolean enemyFar(Unit unit, Game game, Debug debug) {

        return distanceSqr(unit.position, nearestEnemy.position) >= 64;
    }

    private void getNearestEnemy(Unit unit, Game game, Debug debug) {
        nearestEnemy = null;

        for (Unit other : game.getUnits()) {
            if (other.getPlayerId() != unit.getPlayerId()) {
                /*if (nearestEnemy == null || distanceSqr(unit.getPosition(),
                        other.getPosition()) < distanceSqr(unit.getPosition(), nearestEnemy.getPosition())) {
                    nearestEnemy = other;
                }*/
                if (nearestEnemy == null ||World.liMap[(int)other.position.y][(int)other.position.x]<World.liMap[(int)nearestEnemy.position.y][(int)nearestEnemy.position.x])
                    nearestEnemy = other;
            }
        }
    }

    private void drawSomething(Unit unit, Game game, Debug debug) {
        if (Constants.ON_DEBUG) {
            System.out.println("tick " + game.getCurrentTick());
            System.out.println(unit);
        }

        if (Constants.ON_DEBUG) {
            for (Bullet b : game.getBullets()) {
                debug.draw(new CustomData.Line(new Vec2Float( b.position.x,  b.position.y),
                        new Vec2Float( b.position.x +  b.velocity.x,  b.position.y +  b.velocity.y),
                        0.1f, new ColorFloat(0, 128, 0, 255)));
            }

            ColorFloat colorFloat1 = new ColorFloat(255,0,0,0.01f);
            ColorFloat colorFloat2 = new ColorFloat(255,0,0,0.1f);
            for (int x = 0; x < game.getLevel().getTiles().length; x+=2) {
                for (int y = 0; y < game.getLevel().getTiles()[0].length; y+=2) {
                    debug.draw(new CustomData.Line(new Vec2Float(0, y + 0.5f), new Vec2Float(game.getLevel().getTiles().length, y + 0.5f), 1, colorFloat1));
                }
                debug.draw(new CustomData.Line(new Vec2Float(x + 0.5f, 0), new Vec2Float(x + 0.5f, game.getLevel().getTiles()[0].length), 1, colorFloat2));
            }


            Vec2Double p = new Vec2Double(unit.position.x, unit.position.y);
            p.setX(34.5f);
            p.setY(1f);
            double v = Constants.UNIT_X_SPEED_PER_TICK*6;//(game.getProperties().getUnitMaxHorizontalSpeed()/game.getProperties().getTicksPerSecond() * 6f);
            for (int i=0;i<10; i++) {
                debug.draw(new CustomData.Line( new Vec2Float(p.x,p.getY() + (i%2==0?0.2f:-0.2f)),
                        new Vec2Float(p.x - v,p.getY() + (i%2==0?-0.2f:0.2f)), 0.1f, new ColorFloat(255,0,255,1f)));


                debug.draw(new CustomData.Line( new Vec2Float(p.x,p.getY()),
                        new Vec2Float(p.x - v,p.getY() + v), 0.1f, new ColorFloat(255,0,255,1f)));

                p.setX(p.x - v);
            }
        }
    }


    Vec2Double getP(Vec2Double targetPosition, Unit unit, Game game, Debug debug, UnitAction action, boolean goTo, Unit enemy) {

        if (enemy==null) {
            world.print(World.maps.get((int) targetPosition.x * 10 + (int) targetPosition.y * 10 * World.x).map, debug);
            world.printD(World.maps.get((int) targetPosition.x * 10 + (int) targetPosition.y * 10 * World.x).map, debug);
        }

        int[] arrayML = new int[Sim_v3.steps.length];
        int[] arrayTL = new int[Sim_v3.steps.length];
        int L;

        int id;

        if (enemy==null)
            id=(int)targetPosition.x*10+(int)targetPosition.y*10*World.x;
        else
            id=enemy.id;

        Vec2Double[] ps = new Vec2Double[Sim_v3.steps.length];

        for (int i=0;i<Sim_v3.steps.length;i++) {

            int tick=1;
            arrayML[i]=goTo?500*500:-1;
            arrayTL[i]=-1;
            for (Vec2Double p:Sim_v3.steps[i]) {
                /*if (goTo && arrayML[i] > (int)(distanceSqr(p, targetPosition)*100) || !goTo && arrayML[i] < (int)(distanceSqr(p, targetPosition)*100)) {
                    arrayML[i] = (int)(distanceSqr(p, targetPosition)*100);
                    //minL = Math.abs(World.liMap[(int)p.y][(int)p.x]-cellLootBox);
                    arrayTL[i] = tick;
                    ps[i]=p;
                }*/

                L = World.maps.get(id).map[(int)p.y][(int)p.x];

                if (goTo && arrayML[i]>L || !goTo && arrayML[i]<L) {
                    arrayML[i] = L;
                    arrayTL[i] = tick;
                    ps[i]=p;

                    if (Constants.ON_DEBUG)
                        debug.draw(new CustomData.Rect(new Vec2Float(ps[i].x, ps[i].y), new Vec2Float(0.2f, 0.2f), new ColorFloat(0,1,1,0.5f)));

                }
                tick++;
            }
        }

        if (Constants.ON_DEBUG) {
            for (int i=0;i<Sim_v3.steps.length;i++) {
                System.out.println(i+ " l="+arrayML[i]+" t="+arrayTL[i]);
                debug.draw(new CustomData.Rect(new Vec2Float(ps[i].x, ps[i].y), new Vec2Float(0.2f, 0.2f), new ColorFloat(0,1,1,1)));
            }
        }

        int minHit=10000;
        for (int h:Dodge.hits) {
            if (h<minHit)
                minHit=h;
        }

        mI=-1;
        int mTick=-1;

        Vec2Double firstStep = null;

        for (int i=0;i<arrayML.length;i++) {
            if (Dodge.hits[i]==minHit) {
                if (mI==-1) {
                    mI=i;
                    mTick=arrayTL[i];

                    continue;
                }

                if(goTo && arrayML[i]<arrayML[mI] || !goTo && arrayML[i]>arrayML[mI]) {
                    mI=i;
                    mTick=arrayTL[i];
                } else if (arrayML[i]==arrayML[mI]) {
                    if (arrayTL[i]<mTick) {
                        mI=i;
                        mTick=arrayTL[i];
                    }
                }
            }
        }

        firstStep = Sim_v3.steps[mI][0];
        if (Constants.ON_DEBUG)
            System.out.println("mi="+mI+" mt="+mTick);

        if (Constants.ON_DEBUG)
            debug.draw(new CustomData.Rect(new Vec2Float(Sim_v3.steps[mI][mTick-1].x-0.2f,Sim_v3.steps[mI][mTick-1].y-0.2f), new Vec2Float(0.4f,0.4f), new ColorFloat(0.5f,0.5f, 0.5f, 0.5f)));


        return firstStep;
    }


    static double distanceSqr(Vec2Double a, Vec2Double b) {
        return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
    }

    /*static float distanceSqr(Vec2Float a, Vec2Float b) {
        return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
    }*/

    static double distanceSqr(double ax, double ay, double bx, double by) {
        return (ax - bx) * (ax - bx) + (ay - by) * (ay - by);
    }

    /*static float distanceSqr(float ax, float ay, float bx, float by) {
        return (ax - bx) * (ax - bx) + (ay - by) * (ay - by);
    }*/
}
