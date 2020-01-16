package strategy;

import model.*;
import myModel.Constants;
import myModel.UnitStatus;

/**
 * Created by aka_npou on 30.11.2019.
 */

//идет за оружием, потом стоит и стреляет
public class Strategy_v1 extends AkaNpouStrategy {

    @Override
    public UnitAction getAction(Unit unit, Game game, Debug debug) {

        if (game.getCurrentTick() == 0) {
            world.setMap(game.getLevel());
            world.setPatencyMap(game.getLevel(), unit.getPosition());
            //world.setReachabilityMap();

            //shoot.setShootCells();

            world.print(world.patencyMap, debug);
        }

        drawSomething(unit, game, debug);

        if (needDodge(unit, game, debug)) {
            unitStatus = UnitStatus.Dodge;
        } else if (unit.weapon == null) {
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
        LootBox nearestWeapon = null;
        for (LootBox lootBox : game.getLootBoxes()) {
            if (lootBox.getItem().TAG == 1) {
                if (nearestWeapon == null || distanceSqr(unit.getPosition(),
                        lootBox.getPosition()) < distanceSqr(unit.getPosition(), nearestWeapon.getPosition())) {
                    nearestWeapon = lootBox;
                }
            }
        }

        action.setVelocity((nearestWeapon.position.x - unit.position.x)*100f);

        jump(unit, game, debug, action, nearestWeapon.position);
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

        action.setVelocity((nearestHP.position.x - unit.position.x)*100f);

        jump(unit, game, debug, action, nearestHP.position);
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

        action.setVelocity((20 - unit.position.x)*100f);

        //jump(unit, game, debug, action, nearestEnemy.position);
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

        action.setVelocity((20 - unit.position.x)*100f);

        //jump(unit, game, debug, action, nearestEnemy.position);
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
        return false;
    }

    private void dodge(Unit unit, Game game, Debug debug, UnitAction action) {

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

        if (distanceSqr(unit.getPosition(), nearestEnemy.getPosition()) < 25)
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

        if (distanceSqr(unit.getPosition(), nearestEnemy.getPosition()) >= 25)
            return true;
        else
            return false;
    }


    private void drawSomething(Unit unit, Game game, Debug debug) {
        if (Constants.ON_DEBUG) {
            System.out.println(unit);
            System.out.println("tick " + game.getCurrentTick());
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
}
