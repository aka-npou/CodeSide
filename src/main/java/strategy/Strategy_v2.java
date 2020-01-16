package strategy;

import model.*;
import myModel.*;
import sims.*;

import java.util.ArrayList;

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



        if (game.getCurrentTick() == 0 && Constants.INIT) {
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

            s = System.nanoTime();
            Sim_v3.init();
            f = System.nanoTime();
            System.out.println("i s " + (f - s));

            Constants.INIT=false;

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

        //todo добавить мины. если одна и в радиусе враг или если две и в радиусе враг и могу стрелять
        if (canMine(unit, game, debug)) {
            unitStatus = UnitStatus.Mine;
        } else if (needDodge(unit, game, debug)) {
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

        if (unitStatus==UnitStatus.Mine) {
            plaintMine(unit, game, debug, action);
        }
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

        if (unitStatus==UnitStatus.Wait) {
            wait(unit, game, debug, action);
        }

        s = System.nanoTime();
        if (unitStatus!=UnitStatus.Mine)
            shoot.canShoot(unit, nearestEnemy, debug, action, game);
        f = System.nanoTime();
        System.out.println("shoot " + (f-s));


        return action;
    }

    private void wait(Unit unit, Game game, Debug debug, UnitAction action) {

        int minHit=10000;
        for (int h:Dodge.hits) {
            if (h<minHit)
                minHit=h;
        }

        Vec2Double minP = null;

        for (int i=0;i<Dodge.hits.length;i++) {
            if (Dodge.hits[i]==minHit) {
                minP = Sim_v3.steps[i][0];
                break;
            }
        }


        action.setVelocity((minP.x - unit.position.x)*Constants.TICKS_PER_SECOND);
        if (minP.y-unit.position.y>Constants.UNIT_Y_SPEED_PER_TICK/2d)
            action.jump=true;

        if (minP.y-unit.position.y<-Constants.UNIT_Y_SPEED_PER_TICK/2d && Sim_v3.steps[mI][2].y<Sim_v3.steps[mI][1].y)
            action.jumpDown=true;

    }

    private boolean canMine(Unit unit, Game game, Debug debug) {

        if (true)
            return false;

        if (unit.onGround) {
            if (unit.mines!=0) {

                if (unit.weapon!=null && (unit.weapon.fireTimer==null || unit.weapon.fireTimer<Constants.TICK*10)) {

                    int rMy=0;
                    int rEnemy=0;

                    int hMy=0;
                    int hEnemy=0;
                    double radius=2.4;

                    for (Unit u:game.getUnits()) {

                        if (u.playerId==unit.playerId) {
                            hMy+=u.health;
                        } else {
                            hEnemy+=u.health;
                        }

                        if ((Math.abs(unit.position.x-u.position.x)<radius && Math.abs(unit.position.y-u.position.y)<radius) || (Math.abs(unit.position.x-u.position.x-Constants.UNIT_H)<radius && Math.abs(unit.position.y-u.position.y-Constants.UNIT_H)<radius)) {
                            if (u.health<unit.mines*game.getProperties().getMineExplosionParams().getDamage()) {
                                if (u.playerId==unit.playerId) {
                                    //hMy-=u.health;
                                } else {
                                    //hEnemy-=u.health;
                                    rMy+=u.health+game.getProperties().getKillScore();
                                }
                            } else {
                                if (u.playerId==unit.playerId) {
                                    hMy-=game.getProperties().getMineExplosionParams().getDamage();
                                } else {
                                    hEnemy-=game.getProperties().getMineExplosionParams().getDamage();
                                    rMy+=game.getProperties().getMineExplosionParams().getDamage();
                                }
                            }
                        }

                    }
                    System.out.println("t="+game.getCurrentTick()+" hMy="+hMy+" hE="+hEnemy+" rMy="+rMy+" rE="+rEnemy);

                    if (hMy>0 && rMy>1000) {
                        return true;
                    }

                }

            }
        }

        int mines=0;
        for (Mine m:game.getMines()) {
            if (m.getPlayerId()==unit.playerId)
                mines++;
        }

        if (mines!=0)
            return true;


        return false;
    }

    private void plaintMine(Unit unit, Game game, Debug debug, UnitAction action) {

        int mines=0;
        for (Mine m:game.getMines()) {
            if (m.getPlayerId()==unit.playerId)
                mines++;
        }

        if (mines>=1 && unit.mines==0) {
            action.aim.x=0;
            action.aim.y=-10;
            action.shoot=true;
        } else {
            action.plantMine=true;
        }

        action.velocity=0;
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
        //todo посмотреть расстояния всех до всех аптечек и выбрать лучшую


        LootBox nearestHP = null;
        int enemyL;
        for (LootBox lootBox : game.getLootBoxes()) {
            if (lootBox.getItem().TAG == 0) {
                /*if (nearestHP == null || distanceSqr(unit.getPosition(),
                        lootBox.getPosition()) < distanceSqr(unit.getPosition(), nearestHP.getPosition())) {
                    nearestHP = lootBox;
                }*/
                enemyL=100;
                for (Unit u:game.getUnits()) {
                    if (unit.playerId == u.playerId)
                        continue;

                    if(enemyL>World.maps.get((int)lootBox.position.x*10+(int)lootBox.position.y*10*World.x).map[(int)u.position.y][(int)u.position.x]) {
                        enemyL=World.maps.get((int)lootBox.position.x*10+(int)lootBox.position.y*10*World.x).map[(int)u.position.y][(int)u.position.x];
                    }
                }
                if (nearestHP == null || World.liMap[(int)lootBox.position.y][(int)lootBox.position.x]<World.liMap[(int)nearestHP.position.y][(int)nearestHP.position.x])
                    if (enemyL>=World.maps.get((int)lootBox.position.x*10+(int)lootBox.position.y*10*World.x).map[(int)unit.position.y][(int)unit.position.x])
                        nearestHP = lootBox;
            }
        }

        //нет аптечки ближе к нам чем к врагу, то след статус
        if (nearestHP==null) {
            if (enemyNearly(unit, game, debug)) {
                unitStatus = UnitStatus.GoFromEnemy;
            } else if (enemyFar(unit, game, debug)) {
                unitStatus = UnitStatus.GoToEnemy;
            } else
                unitStatus = UnitStatus.Wait;

            return;
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

        return distanceSqr(unit.position, nearestEnemy.position) < 49;

    }

    private boolean enemyFar(Unit unit, Game game, Debug debug) {

        return distanceSqr(unit.position, nearestEnemy.position) >= 81;
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

        if (Constants.is2x2) {
            Unit ne2 = null;
            for (Unit other : game.getUnits()) {
                if (other.getPlayerId() != unit.getPlayerId() && nearestEnemy.id!=other.id) {
                    ne2=other;
                    break;
                }
            }

            //второй есть
            if (ne2!=null) {
                if (ne2.health<nearestEnemy.health) {
                    if (Math.abs(Math.sqrt(distanceSqr(unit.position, nearestEnemy.position))-Math.sqrt(distanceSqr(unit.position, ne2.position)))<=4) {
                        nearestEnemy=ne2;
                    }
                }
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

            for (int i=0;i<World.x;i++) {
                debug.draw(new CustomData.PlacedText(""+i, new Vec2Float(i, World.y+0.5f), TextAlignment.CENTER,10f, new ColorFloat(1,1,1,1)));
            }
            for (int i=0;i<World.y;i++) {
                debug.draw(new CustomData.PlacedText(""+i, new Vec2Float(World.x+0.5f, i), TextAlignment.CENTER,10f, new ColorFloat(1,1,1,1)));
            }
        }
    }


    Vec2Double getP(Vec2Double targetPosition, Unit unit, Game game, Debug debug, UnitAction action, boolean goTo, Unit enemy) {

        //выбирать те, где не надо падать или прыгать
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

        int chainlength = goTo?Sim_v3.steps[0].length:Sim_v3.steps[0].length/3;
        for (int i=0;i<Sim_v3.steps.length;i++) {

            int tick=1;
            arrayML[i]=goTo?500*500:-1;
            arrayTL[i]=-1;
            //for (Vec2Double p:Sim_v3.steps[i]) {
            for (int p=0;p<chainlength;p++) {
                /*if (goTo && arrayML[i] > (int)(distanceSqr(p, targetPosition)*100) || !goTo && arrayML[i] < (int)(distanceSqr(p, targetPosition)*100)) {
                    arrayML[i] = (int)(distanceSqr(p, targetPosition)*100);
                    //minL = Math.abs(World.liMap[(int)p.y][(int)p.x]-cellLootBox);
                    arrayTL[i] = tick;
                    ps[i]=p;
                }*/

                L = World.maps.get(id).map[(int)Sim_v3.steps[i][p].y][(int)Sim_v3.steps[i][p].x];

                if (goTo && arrayML[i]>L || !goTo && arrayML[i]<L) {
                    arrayML[i] = L;
                    arrayTL[i] = tick;
                    ps[i]=Sim_v3.steps[i][p];

                    if (Constants.ON_DEBUG)
                        debug.draw(new CustomData.Rect(new Vec2Float(ps[i].x-0.05f, ps[i].y-0.05f), new Vec2Float(0.1f, 0.1f), new ColorFloat(0,1,1,0.5f)));

                }
                tick++;
            }
        }

        if (Constants.ON_DEBUG) {
            for (int i=0;i<Sim_v3.steps.length;i++) {
                System.out.println(i+ " l="+arrayML[i]+" t="+arrayTL[i]);
                debug.draw(new CustomData.Rect(new Vec2Float(ps[i].x-0.05f, ps[i].y-0.05f), new Vec2Float(0.1f, 0.1f), new ColorFloat(0,1,1,1)));
            }
        }

        int minHit=10000;
        for (int h:Dodge.hits) {
            if (h<minHit)
                minHit=h;
        }

        //todo если минимальный 50 и более(это ракета) то пытаемся подбить врагов и если я ближе к ракете
        mI=-1;
        int mTick=-1;

        Vec2Double firstStep = null;

        for (int i = 0; i < arrayML.length; i++) {
            if (Dodge.hits[i] == minHit) {
                if (mI == -1) {
                    mI = i;
                    mTick = arrayTL[i];

                    continue;
                }

                if (goTo && arrayML[i] < arrayML[mI] || !goTo && arrayML[i] > arrayML[mI]) {
                    mI = i;
                    mTick = arrayTL[i];
                } else if (arrayML[i] == arrayML[mI]) {
                    if (arrayTL[i] < mTick) {
                        mI = i;
                        mTick = arrayTL[i];
                    }
                }
            }
        }

//        if (enemy==null) {
//            for (int i = 0; i < arrayML.length; i++) {
//                if (Dodge.hits[i] == minHit) {
//                    if (mI == -1) {
//                        mI = i;
//                        mTick = arrayTL[i];
//
//                        continue;
//                    }
//
//                    if (goTo && arrayML[i] < arrayML[mI] || !goTo && arrayML[i] > arrayML[mI]) {
//                        mI = i;
//                        mTick = arrayTL[i];
//                    } else if (arrayML[i] == arrayML[mI]) {
//                        if (arrayTL[i] < mTick) {
//                            mI = i;
//                            mTick = arrayTL[i];
//                        }
//                    }
//                }
//            }
//        } else {
//            //надо идти не в притык, а где расстояние норм и не надо прыгать/падать
//            int enemyL=9;
//
//            if (World.maps.get(id).map[(int)unit.position.y][(int)unit.position.x]<12) {
//
//                ArrayList<Integer> steps = new ArrayList<>();
//                ArrayList<Integer> stepsV = new ArrayList<>();
//
//
//                int maxV = 0;
//                for (int i = 0; i < arrayML.length; i++) {
//                    if (Dodge.hits[i] == minHit) {
//
//                        if (goTo && arrayML[i] < enemyL || !goTo && arrayML[i] > enemyL) {
//                            //если мы сваливаем, то не берем такие шаги где стоим
//                            if (!goTo && Math.abs(Sim_v3.steps[i][0].y - unit.position.y) < Constants.EPS * 2 && Math.abs(Sim_v3.steps[i][0].x - unit.position.x) < Constants.EPS * 2)
//                                continue;
//
//                            //если мы когда-то дойдем до врага, а сейчас стоим то пропускаем такой ход
//                            if (goTo && Math.abs(Sim_v3.steps[i][0].y - unit.position.y) < Constants.EPS * 2 && Math.abs(Sim_v3.steps[i][0].x - unit.position.x) < Constants.EPS * 2 && arrayTL[i] > 1)
//                                continue;
//
//                            steps.add(i);
//
//                            int v = 3;
//
//                            if (Sim_v3.steps[i][0].y - unit.position.y > Constants.UNIT_Y_SPEED_PER_TICK / 2d)
//                                v--;
//
//                            if (Sim_v3.steps[i][0].y - unit.position.y < -Constants.UNIT_Y_SPEED_PER_TICK / 2d && Sim_v3.steps[i][2].y < Sim_v3.steps[i][1].y)
//                                v--;
//
//                            stepsV.add(v);
//
//                            maxV = Math.max(v, maxV);
//
//                        }
//
//                    }
//                }
//
//                for (int i = 0; i < steps.size(); i++) {
//                    if (stepsV.get(i) == maxV) {
//                        mI = steps.get(i);
//                        mTick = 1;
//                        break;
//                    }
//                }
//
//            }
//
//            if (mI==-1) {
//
//                for (int i = 0; i < arrayML.length; i++) {
//                    if (Dodge.hits[i] == minHit) {
//                        if (mI == -1) {
//                            mI = i;
//                            mTick = arrayTL[i];
//
//                            continue;
//                        }
//
//                        if (goTo && arrayML[i] < arrayML[mI] || !goTo && arrayML[i] > arrayML[mI]) {
//                            mI = i;
//                            mTick = arrayTL[i];
//                        } else if (arrayML[i] == arrayML[mI]) {
//                            if (arrayTL[i] < mTick) {
//                                mI = i;
//                                mTick = arrayTL[i];
//                            }
//                        }
//                    }
//                }
//
//            }
//
//        }

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
