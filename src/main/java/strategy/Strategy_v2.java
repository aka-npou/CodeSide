package strategy;

import model.*;
import myModel.*;
import sims.*;

/**
 * Created by aka_npou on 30.11.2019.
 */

//уклонения
public class Strategy_v2 extends AkaNpouStrategy {

    @Override
    public UnitAction getAction(UnitF unit, Game game, Debug debug) {

        if (game.getCurrentTick() == 0) {
            world.setMap(game.getLevel());
            world.setPatencyMap(game.getLevel(), unit.position);


            world.print(World.patencyMap, debug);
            World.liMap = new int[World.y][World.x];
            Li.setVectors();
        }

        if (game.getCurrentTick()>currentTick) {
            Dodge.setBullets(unit, game, debug);
            world.setLiMap(unit);
            world.print(World.liMap, debug);

            currentTick=game.getCurrentTick();
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

        Sim_v2.sim(unit, game, debug, action);

        if (unitStatus == UnitStatus.Dodge) {
            dodge(unit, game, debug, action);
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

        /*UnitF enemy = null;

        for (UnitF u:game.getUnits()) {
            if (u.getPlayerId() != unit.getPlayerId()) {
                enemy = u;
                break;
            }
        }*/

        //shoot.drawShoot(enemy, unit, debug);
        shoot.canShoot(unit, nearestEnemy, debug, action);


        return action;
    }

    private void goToWeapon(UnitF unit, Game game, Debug debug, UnitAction action) {
        /*LootBox nearestWeapon = null;
        for (LootBox lootBox : game.getLootBoxes()) {
            if (lootBox.getItem().TAG == 1) {
                if (nearestWeapon == null || distanceSqr(unit.getPosition(),
                        lootBox.getPosition()) < distanceSqr(unit.getPosition(), nearestWeapon.getPosition())) {
                    nearestWeapon = lootBox;
                }
            }
        }*/

        LootBoxF nearestWeapon = null;
        for (LootBoxF lootBox : game.getLootBoxes()) {
            if (lootBox.getItem().TAG == 1) {
                Item.Weapon w = (Item.Weapon)lootBox.item;
                if (w.getWeaponType() !=WeaponType.ROCKET_LAUNCHER) {
                    /*if (nearestWeapon == null || distanceSqr(unit.getPosition(),
                            lootBox.getPosition()) < distanceSqr(unit.getPosition(), nearestWeapon.getPosition())) {
                        nearestWeapon = lootBox;
                    }*/
                    if (nearestWeapon == null ||World.liMap[(int)lootBox.position.y][(int)lootBox.position.x]<World.liMap[(int)nearestWeapon.position.y][(int)nearestWeapon.position.x])
                        nearestWeapon = lootBox;
                }
            }
        }

        //action.setVelocity((nearestWeapon.position.x - unit.position.x)*100f);

        double minL = 50*50;
        Vec2Float minP = null;
        for (int i=0;i<Sim_v2.steps.length;i++) {
            if (minP==null) {
                minP = Sim_v2.steps[i][0];
                for (Vec2Float p:Sim_v2.steps[i])
                    minL = distanceSqr(p, nearestWeapon.position);

            } else {
                for (Vec2Float p:Sim_v2.steps[i]) {
                    if (minL > distanceSqr(p, nearestWeapon.position)) {
                        minP = Sim_v2.steps[i][0];
                        minL = distanceSqr(p, nearestWeapon.position);
                    }
                }
            }
        }

        action.setVelocity((minP.x - unit.position.x)*Constants.TICKS_PER_SECOND);
        if (minP.y>unit.position.y)
            action.jump=true;

        if (minP.y<unit.position.y)
            action.jumpDown=true;

        action.swapWeapon=true;

        if (Constants.ON_DEBUG)
            debug.draw(new CustomData.Line(new Vec2Float(unit.position.x, unit.position.y), new Vec2Float(nearestWeapon.position.x, nearestWeapon.position.y), 0.1f, new ColorFloat(0.1f, 0.1f, 0.1f, 1f)));

        //jump(unit, game, debug, action, nearestWeapon.position);
    }

    private void goToHP(UnitF unit, Game game, Debug debug, UnitAction action) {
        LootBoxF nearestHP = null;
        for (LootBoxF lootBox : game.getLootBoxes()) {
            if (lootBox.getItem().TAG == 0) {
                /*if (nearestHP == null || distanceSqr(unit.getPosition(),
                        lootBox.getPosition()) < distanceSqr(unit.getPosition(), nearestHP.getPosition())) {
                    nearestHP = lootBox;
                }*/
                if (nearestHP == null ||World.liMap[(int)lootBox.position.y][(int)lootBox.position.x]<World.liMap[(int)nearestHP.position.y][(int)nearestHP.position.x])
                    nearestHP = lootBox;
            }
        }

        double minL = 50*50;
        Vec2Float minP = null;
        for (int i=0;i<Sim_v2.steps.length;i++) {
            if (minP==null) {
                minP = Sim_v2.steps[i][0];
                for (Vec2Float p:Sim_v2.steps[i])
                    minL = distanceSqr(p, nearestHP.position);

            } else {
                for (Vec2Float p:Sim_v2.steps[i]) {
                    if (minL > distanceSqr(p, nearestHP.position)) {
                        minP = Sim_v2.steps[i][0];
                        minL = distanceSqr(p, nearestHP.position);
                    }
                }
            }
        }

        action.setVelocity((minP.x - unit.position.x)*Constants.TICKS_PER_SECOND);
        if (minP.y>unit.position.y)
            action.jump=true;

        if (minP.y<unit.position.y)
            action.jumpDown=true;

    }

    private void goToEnemy(UnitF unit, Game game, Debug debug, UnitAction action) {
        //UnitF nearestEnemy = null;

//        for (UnitF other : game.getUnits()) {
//            if (other.getPlayerId() != unit.getPlayerId()) {
//                /*if (nearestEnemy == null || distanceSqr(unit.getPosition(),
//                        other.getPosition()) < distanceSqr(unit.getPosition(), nearestEnemy.getPosition())) {
//                    nearestEnemy = other;
//                }*/
//                if (nearestEnemy == null ||World.liMap[(int)other.position.y][(int)other.position.x]<World.liMap[(int)nearestEnemy.position.y][(int)nearestEnemy.position.x])
//                    nearestEnemy = other;
//            }
//        }

        double minL = 50*50;
        Vec2Float minP = null;
        int minI=-1;
        int minTick=-1;
        for (int i=0;i<Sim_v2.steps.length;i++) {
            if (minP==null) {
                minP = Sim_v2.steps[i][0];
                int tick = 1;
                for (Vec2Float p:Sim_v2.steps[i]) {
                    if (minL > distanceSqr(p.x, p.y, nearestEnemy.position.x, nearestEnemy.position.y)) {
                        minL = distanceSqr(p, nearestEnemy.position);
                        minI=i;
                        minTick=tick;
                    }
                    tick++;
                }

            } else {
                int tick = 1;
                for (Vec2Float p:Sim_v2.steps[i]) {
                    if (minL > distanceSqr(p, nearestEnemy.position)) {
                        minP = Sim_v2.steps[i][0];
                        minL = distanceSqr(p, nearestEnemy.position);
                        minI=i;
                        minTick=tick;
                    }
                    tick++;
                }
            }
        }

        if (Constants.ON_DEBUG)
            debug.draw(new CustomData.Rect(new Vec2Float(Sim_v2.steps[minI][minTick-1].x-0.2f,Sim_v2.steps[minI][minTick-1].y-0.2f), new Vec2Float(0.4f,0.4f), new ColorFloat(0.5f,0.5f, 0.5f, 0.5f)));

        action.setVelocity((minP.x - unit.position.x)*Constants.TICKS_PER_SECOND);
        if (minP.y>unit.position.y)
            action.jump=true;

        if (minP.y<unit.position.y)
            action.jumpDown=true;

    }

    private void goFromEnemy(UnitF unit, Game game, Debug debug, UnitAction action) {
        //UnitF nearestEnemy = null;

//        for (UnitF other : game.getUnits()) {
//            if (other.getPlayerId() != unit.getPlayerId()) {
//                /*if (nearestEnemy == null || distanceSqr(unit.getPosition(),
//                        other.getPosition()) < distanceSqr(unit.getPosition(), nearestEnemy.getPosition())) {
//                    nearestEnemy = other;
//                }*/
//                if (nearestEnemy == null ||World.liMap[(int)other.position.y][(int)other.position.x]<World.liMap[(int)nearestEnemy.position.y][(int)nearestEnemy.position.x])
//                    nearestEnemy = other;
//            }
//        }

        double maxL = -1;
        Vec2Float maxP = null;
        for (int i=0;i<Sim_v2.steps.length;i++) {
            if (maxP==null) {
                maxP = Sim_v2.steps[i][0];
                for (Vec2Float p:Sim_v2.steps[i])
                    maxL = distanceSqr(p, nearestEnemy.position);

            } else {
                for (Vec2Float p:Sim_v2.steps[i]) {
                    if (maxL < distanceSqr(p, nearestEnemy.position)) {
                        maxP = Sim_v2.steps[i][0];
                        maxL = distanceSqr(p, nearestEnemy.position);
                    }
                }
            }
        }

        action.setVelocity((maxP.x - unit.position.x)*Constants.TICKS_PER_SECOND);
        if (maxP.y>unit.position.y)
            action.jump=true;

        if (maxP.y<unit.position.y)
            action.jumpDown=true;
    }


    private void jump(UnitF unit, Game game, Debug debug, UnitAction action, Vec2Double targetPos) {
        boolean jump = targetPos.getY() > unit.position.y;
        if (targetPos.x > unit.position.x && game.getLevel()
                .getTiles()[(int) (unit.position.x + 1)][(int) (unit.position.y)] == Tile.WALL) {
            jump = true;
        }

        if (targetPos.x < unit.position.x && game.getLevel()
                .getTiles()[(int) (unit.position.x - 1)][(int) (unit.position.y)] == Tile.WALL) {
            jump = true;
        }

        action.jump = jump;
        action.jumpDown = !jump;

    }

    private boolean mapHaveHP(Game game) {
        for (LootBoxF lootBox : game.getLootBoxes()) {
            if (lootBox.getItem().TAG == 0) {
                return true;
            }
        }

        return false;
    }

    private boolean needDodge(UnitF unit, Game game, Debug debug) {

        //if (1==1)
        //    return true;

        if (game.getBullets().length == 0)
            return false;

        for (int b=0; b<Dodge.bullets.size();b++) {
            //выкинуть свои пули
            if (game.getBullets()[b].unitId ==  unit.id && game.getBullets()[b].explosionParams == null)
                continue;

            //if (distanceSqr(game.getBullets()[b].position.x, game.getBullets()[b].position.y, unit.position.x, unit.position.y+Constants.UNIT_H2) >= 9) {
                //выкинуть пули которые уже точно не в нас, позиция и скорость не в нашу сторону
                if (game.getBullets()[b].position.x > unit.position.x + Constants.UNIT_W2 && Dodge.bullets.get(b).x < 0)
                    return true;

                if (game.getBullets()[b].position.x < unit.position.x - Constants.UNIT_W2 && Dodge.bullets.get(b).x > 0)
                    return true;

                if (game.getBullets()[b].position.y > unit.position.y + Constants.UNIT_H && Dodge.bullets.get(b).y < 0)
                    return true;

                if (game.getBullets()[b].position.y < unit.position.y && Dodge.bullets.get(b).y > 0)
                    return true;
            //}

        }

        return false;
    }

    private void dodge(UnitF unit, Game game, Debug debug, UnitAction action) {
        Dodge.dodge(unit, game, debug, action);
    }

    private boolean enemyNearly(UnitF unit, Game game, Debug debug) {
        //nearestEnemy = null;

//        for (UnitF other : game.getUnits()) {
//            if (other.getPlayerId() != unit.getPlayerId()) {
//                /*if (nearestEnemy == null || distanceSqr(unit.getPosition(),
//                        other.getPosition()) < distanceSqr(unit.getPosition(), nearestEnemy.getPosition())) {
//                    nearestEnemy = other;
//                }*/
//                if (nearestEnemy == null ||World.liMap[(int)other.position.y][(int)other.position.x]<World.liMap[(int)nearestEnemy.position.y][(int)nearestEnemy.position.x])
//                    nearestEnemy = other;
//            }
//        }

        if (distanceSqr(unit.position, nearestEnemy.position) < 64)
            return true;
        else
            return false;

    }

    private boolean enemyFar(UnitF unit, Game game, Debug debug) {
//        UnitF nearestEnemy = null;
//
//        for (UnitF other : game.getUnits()) {
//            if (other.getPlayerId() != unit.getPlayerId()) {
//                /*if (nearestEnemy == null || distanceSqr(unit.getPosition(),
//                        other.getPosition()) < distanceSqr(unit.getPosition(), nearestEnemy.getPosition())) {
//                    nearestEnemy = other;
//                }*/
//                if (nearestEnemy == null ||World.liMap[(int)other.position.y][(int)other.position.x]<World.liMap[(int)nearestEnemy.position.y][(int)nearestEnemy.position.x])
//                    nearestEnemy = other;
//            }
//        }

        if (distanceSqr(unit.position, nearestEnemy.position) >= 64)
            return true;
        else
            return false;
    }

    private void getNearestEnemy(UnitF unit, Game game, Debug debug) {
        nearestEnemy = null;

        for (UnitF other : game.getUnits()) {
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

    private void drawSomething(UnitF unit, Game game, Debug debug) {
        if (Constants.ON_DEBUG) {
            System.out.println("tick " + game.getCurrentTick());
            System.out.println(unit);
        }

        if (Constants.ON_DEBUG) {
            for (BulletF b : game.getBullets()) {
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


            Vec2Float p = new Vec2Float(unit.position.x, unit.position.y);
            p.setX(34.5f);
            p.setY(1f);
            float v = Constants.UNIT_X_SPEED_PER_TICK*6;//(game.getProperties().getUnitMaxHorizontalSpeed()/game.getProperties().getTicksPerSecond() * 6f);
            for (int i=0;i<10; i++) {
                debug.draw(new CustomData.Line( new Vec2Float(p.x,p.getY() + (i%2==0?0.2f:-0.2f)),
                        new Vec2Float(p.x - v,p.getY() + (i%2==0?-0.2f:0.2f)), 0.1f, new ColorFloat(255,0,255,1f)));


                debug.draw(new CustomData.Line( new Vec2Float(p.x,p.getY()),
                        new Vec2Float(p.x - v,p.getY() + v), 0.1f, new ColorFloat(255,0,255,1f)));

                p.setX(p.x - v);
            }
        }
    }

    /*static double distanceSqr(Vec2Double a, Vec2Double b) {
        return (a.x - b.x) * (a.x - b.x) + (a.getY() - b.getY()) * (a.getY() - b.getY());
    }*/

    static double distanceSqr(Vec2Float a, Vec2Float b) {
        return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
    }

    /*static double distanceSqr(double ax, double ay, double bx, double by) {
        return (ax - bx) * (ax - bx) + (ay - by) * (ay - by);
    }*/

    static double distanceSqr(float ax, float ay, float bx, float by) {
        return (ax - bx) * (ax - bx) + (ay - by) * (ay - by);
    }
}
