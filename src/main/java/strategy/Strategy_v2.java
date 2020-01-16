package strategy;

import model.*;
import myModel.Constants;
import myModel.Dodge;
import myModel.UnitStatus;
import myModel.World;
import sims.*;

/**
 * Created by aka_npou on 30.11.2019.
 */

//уклонения
public class Strategy_v2 extends AkaNpouStrategy {

    @Override
    public UnitAction getAction(Unit unit, Game game, Debug debug) {

        if (game.getCurrentTick()>currentTick) {
            Dodge.setBullets(unit, game, debug);
            currentTick=game.getCurrentTick();
        }

        if (game.getCurrentTick() == 0) {
            world.setMap(game.getLevel());
            world.setPatencyMap(game.getLevel(), unit.getPosition());


            world.print(World.patencyMap, debug);
        }


        drawSomething(unit, game, debug);

        if (needDodge(unit, game, debug)) {
            unitStatus = UnitStatus.Dodge;
        } else if (unit.weapon == null || unit.weapon.typ == WeaponType.ROCKET_LAUNCHER) {
            unitStatus = UnitStatus.GoToWeapon;
        } else {
            if (enemyNearly(unit, game, debug)) {
                unitStatus = UnitStatus.GoFromEnemy;
            } else if (enemyFar(unit, game, debug)) {
                unitStatus = UnitStatus.GoToEnemy;
            } else
                unitStatus = UnitStatus.Wait;
        }


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

        Unit enemy = null;

        for (Unit u:game.getUnits()) {
            if (u.getPlayerId() != unit.getPlayerId()) {
                enemy = u;
                break;
            }
        }

        //shoot.drawShoot(enemy, unit, debug);
        shoot.canShoot(unit, enemy, debug, action);


        return action;
    }

    private void goToWeapon(Unit unit, Game game, Debug debug, UnitAction action) {
        /*LootBox nearestWeapon = null;
        for (LootBox lootBox : game.getLootBoxes()) {
            if (lootBox.getItem().TAG == 1) {
                if (nearestWeapon == null || distanceSqr(unit.getPosition(),
                        lootBox.getPosition()) < distanceSqr(unit.getPosition(), nearestWeapon.getPosition())) {
                    nearestWeapon = lootBox;
                }
            }
        }*/

        LootBox nearestWeapon = null;
        for (LootBox lootBox : game.getLootBoxes()) {
            if (lootBox.getItem().TAG == 1) {
                Item.Weapon w = (Item.Weapon)lootBox.item;
                if (w.getWeaponType() !=WeaponType.ROCKET_LAUNCHER)
                    if (nearestWeapon == null || distanceSqr(unit.getPosition(),
                            lootBox.getPosition()) < distanceSqr(unit.getPosition(), nearestWeapon.getPosition())) {
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

        //jump(unit, game, debug, action, nearestWeapon.position);
    }

    private void goToHP(Unit unit, Game game, Debug debug, UnitAction action) {
        LootBox nearestHP = null;
        for (LootBox lootBox : game.getLootBoxes()) {
            if (lootBox.getItem().TAG == 0) {
                if (nearestHP == null || distanceSqr(unit.getPosition(),
                        lootBox.getPosition()) < distanceSqr(unit.getPosition(), nearestHP.getPosition())) {
                    nearestHP = lootBox;
                }
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

    private void goToEnemy(Unit unit, Game game, Debug debug, UnitAction action) {
        Unit nearestEnemy = null;

        for (Unit other : game.getUnits()) {
            if (other.getPlayerId() != unit.getPlayerId()) {
                if (nearestEnemy == null || distanceSqr(unit.getPosition(),
                        other.getPosition()) < distanceSqr(unit.getPosition(), nearestEnemy.getPosition())) {
                    nearestEnemy = other;
                }
            }
        }

        double minL = 50*50;
        Vec2Float minP = null;
        for (int i=0;i<Sim_v2.steps.length;i++) {
            if (minP==null) {
                minP = Sim_v2.steps[i][0];
                for (Vec2Float p:Sim_v2.steps[i])
                    minL = distanceSqr(p, nearestEnemy.position);

            } else {
                for (Vec2Float p:Sim_v2.steps[i]) {
                    if (minL > distanceSqr(p, nearestEnemy.position)) {
                        minP = Sim_v2.steps[i][0];
                        minL = distanceSqr(p, nearestEnemy.position);
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

    private void goFromEnemy(Unit unit, Game game, Debug debug, UnitAction action) {
        Unit nearestEnemy = null;

        for (Unit other : game.getUnits()) {
            if (other.getPlayerId() != unit.getPlayerId()) {
                if (nearestEnemy == null || distanceSqr(unit.getPosition(),
                        other.getPosition()) < distanceSqr(unit.getPosition(), nearestEnemy.getPosition())) {
                    nearestEnemy = other;
                }
            }
        }

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


    private void jump(Unit unit, Game game, Debug debug, UnitAction action, Vec2Double targetPos) {
        boolean jump = targetPos.getY() > unit.getPosition().getY();
        if (targetPos.getX() > unit.getPosition().getX() && game.getLevel()
                .getTiles()[(int) (unit.getPosition().getX() + 1)][(int) (unit.getPosition().getY())] == Tile.WALL) {
            jump = true;
        }

        if (targetPos.getX() < unit.getPosition().getX() && game.getLevel()
                .getTiles()[(int) (unit.getPosition().getX() - 1)][(int) (unit.getPosition().getY())] == Tile.WALL) {
            jump = true;
        }

        action.jump = jump;
        action.jumpDown = !jump;

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

        //if (1==1)
        //    return true;

        if (game.getBullets().length == 0)
            return false;

        for (int b=0; b<Dodge.bullets.size();b++) {
            //выкинуть свои пули
            if (game.getBullets()[b].unitId ==  unit.id && game.getBullets()[b].explosionParams == null)
                continue;

            //выкинуть пули которые уже точно не в нас, позиция и скорость не в нашу сторону
            if (game.getBullets()[b].position.x > unit.position.x+Constants.UNIT_W2+0.2f && Dodge.bullets.get(b).x > 0)
                continue;

            if (game.getBullets()[b].position.x < unit.position.x-Constants.UNIT_W2-0.2f && Dodge.bullets.get(b).x < 0)
                continue;

            if (game.getBullets()[b].position.y > unit.position.y+Constants.UNIT_H+0.2f && Dodge.bullets.get(b).y > 0)
                continue;

            if (game.getBullets()[b].position.y < unit.position.y-0.2f && Dodge.bullets.get(b).y < 0)
                continue;

            return true;
        }

        return false;
    }

    private void dodge(Unit unit, Game game, Debug debug, UnitAction action) {
        Dodge.dodge(unit, game, debug, action);
    }

    private boolean enemyNearly(Unit unit, Game game, Debug debug) {
        Unit nearestEnemy = null;

        for (Unit other : game.getUnits()) {
            if (other.getPlayerId() != unit.getPlayerId()) {
                if (nearestEnemy == null || distanceSqr(unit.getPosition(),
                        other.getPosition()) < distanceSqr(unit.getPosition(), nearestEnemy.getPosition())) {
                    nearestEnemy = other;
                }
            }
        }

        if (distanceSqr(unit.getPosition(), nearestEnemy.getPosition()) < 64)
            return true;
        else
            return false;

    }

    private boolean enemyFar(Unit unit, Game game, Debug debug) {
        Unit nearestEnemy = null;

        for (Unit other : game.getUnits()) {
            if (other.getPlayerId() != unit.getPlayerId()) {
                if (nearestEnemy == null || distanceSqr(unit.getPosition(),
                        other.getPosition()) < distanceSqr(unit.getPosition(), nearestEnemy.getPosition())) {
                    nearestEnemy = other;
                }
            }
        }

        if (distanceSqr(unit.getPosition(), nearestEnemy.getPosition()) >= 64)
            return true;
        else
            return false;
    }


    private void drawSomething(Unit unit, Game game, Debug debug) {
        if (Constants.ON_DEBUG) {
            System.out.println("tick " + game.getCurrentTick());
            System.out.println(unit);
        }

        if (Constants.ON_DEBUG) {
            for (Bullet b : game.getBullets()) {
                debug.draw(new CustomData.Line(new Vec2Float((float) b.getPosition().getX(), (float) b.getPosition().getY()),
                        new Vec2Float((float) b.getPosition().getX() + (float) b.getVelocity().getX(), (float) b.getPosition().getY() + (float) b.getVelocity().getY()),
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


            Vec2Float p = new Vec2Float((float)unit.getPosition().getX(), (float)unit.getPosition().getY());
            p.setX(34.5f);
            p.setY(1f);
            float v = (float)(game.getProperties().getUnitMaxHorizontalSpeed()/game.getProperties().getTicksPerSecond() * 6f);
            for (int i=0;i<10; i++) {
                debug.draw(new CustomData.Line( new Vec2Float(p.getX(),p.getY() + (i%2==0?0.2f:-0.2f)),
                        new Vec2Float(p.getX() - v,p.getY() + (i%2==0?-0.2f:0.2f)), 0.1f, new ColorFloat(255,0,255,1f)));


                debug.draw(new CustomData.Line( new Vec2Float(p.getX(),p.getY()),
                        new Vec2Float(p.getX() - v,p.getY() + v), 0.1f, new ColorFloat(255,0,255,1f)));

                p.setX(p.getX() - v);
            }
        }
    }

    static double distanceSqr(Vec2Double a, Vec2Double b) {
        return (a.getX() - b.getX()) * (a.getX() - b.getX()) + (a.getY() - b.getY()) * (a.getY() - b.getY());
    }

    static double distanceSqr(Vec2Float a, Vec2Double b) {
        return (a.getX() - b.getX()) * (a.getX() - b.getX()) + (a.getY() - b.getY()) * (a.getY() - b.getY());
    }
}
