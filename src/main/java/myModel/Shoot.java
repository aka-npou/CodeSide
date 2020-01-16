package myModel;

import model.*;

/**
 * Created by aka_npou on 28.11.2019.
 */
public class Shoot {

    public CellChain[][] shootCells = new CellChain[5][5];

    double ldx=0, ldy=0;

    int lastTickShoot1=0;
    int lastTickShoot2=0;

    public void setShootCells() {

        shootCells[4][4] = new CellChain();
        shootCells[4][4].chain = new Vec2Int[8];
        shootCells[4][4].chain[0] = new Vec2Int(1, 1);
        shootCells[4][4].chain[1] = new Vec2Int(2, 2);
        shootCells[4][4].chain[2] = new Vec2Int(3, 3);
        shootCells[4][4].chain[3] = new Vec2Int(4, 4);
        shootCells[4][4].chain[4] = new Vec2Int(0, 1);
        shootCells[4][4].chain[5] = new Vec2Int(1, 2);
        shootCells[4][4].chain[6] = new Vec2Int(2, 3);
        shootCells[4][4].chain[7] = new Vec2Int(3, 4);

        shootCells[4][3] = new CellChain();
        shootCells[4][3].chain = new Vec2Int[7];
        shootCells[4][3].chain[0] = new Vec2Int(1, 1);
        shootCells[4][3].chain[1] = new Vec2Int(2, 2);
        shootCells[4][3].chain[2] = new Vec2Int(3, 3);
        shootCells[4][3].chain[3] = new Vec2Int(0, 1);
        shootCells[4][3].chain[4] = new Vec2Int(1, 2);
        shootCells[4][3].chain[5] = new Vec2Int(2, 3);
        shootCells[4][3].chain[6] = new Vec2Int(3, 4);

        shootCells[4][2] = new CellChain();
        shootCells[4][2].chain = new Vec2Int[6];
        shootCells[4][2].chain[0] = new Vec2Int(1, 1);
        shootCells[4][2].chain[1] = new Vec2Int(0, 1);
        shootCells[4][2].chain[2] = new Vec2Int(1, 2);
        shootCells[4][2].chain[3] = new Vec2Int(2, 3);
        shootCells[4][2].chain[4] = new Vec2Int(1, 3);
        shootCells[4][2].chain[5] = new Vec2Int(2, 4);

        shootCells[4][1] = new CellChain();
        shootCells[4][1].chain = new Vec2Int[5];
        shootCells[4][1].chain[0] = new Vec2Int(0, 1);
        shootCells[4][1].chain[1] = new Vec2Int(1, 2);
        shootCells[4][1].chain[2] = new Vec2Int(0, 2);
        shootCells[4][1].chain[3] = new Vec2Int(1, 3);
        shootCells[4][1].chain[4] = new Vec2Int(1, 4);

        shootCells[4][0] = new CellChain();
        shootCells[4][0].chain = new Vec2Int[4];
        shootCells[4][0].chain[0] = new Vec2Int(0, 1);
        shootCells[4][0].chain[1] = new Vec2Int(0, 2);
        shootCells[4][0].chain[2] = new Vec2Int(0, 3);
        shootCells[4][0].chain[3] = new Vec2Int(0, 4);

        shootCells[3][4] = new CellChain();
        shootCells[3][4].chain = new Vec2Int[7];
        shootCells[3][4].chain[0] = new Vec2Int(2, 1);
        shootCells[3][4].chain[1] = new Vec2Int(3, 2);
        shootCells[3][4].chain[2] = new Vec2Int(4, 3);
        shootCells[3][4].chain[3] = new Vec2Int(1, 1);
        shootCells[3][4].chain[4] = new Vec2Int(2, 2);
        shootCells[3][4].chain[5] = new Vec2Int(3, 3);
        shootCells[3][4].chain[6] = new Vec2Int(0, 1);

        shootCells[3][3] = new CellChain();
        shootCells[3][3].chain = new Vec2Int[5];
        shootCells[3][3].chain[0] = new Vec2Int(1, 1);
        shootCells[3][3].chain[1] = new Vec2Int(2, 2);
        shootCells[3][3].chain[2] = new Vec2Int(3, 3);
        shootCells[3][3].chain[3] = new Vec2Int(0, 1);
        shootCells[3][3].chain[4] = new Vec2Int(1, 2);

        shootCells[3][2] = new CellChain();
        shootCells[3][2].chain = new Vec2Int[5];
        shootCells[3][2].chain[0] = new Vec2Int(1, 1);
        shootCells[3][2].chain[1] = new Vec2Int(2, 2);
        shootCells[3][2].chain[2] = new Vec2Int(0, 1);
        shootCells[3][2].chain[3] = new Vec2Int(1, 2);
        shootCells[3][2].chain[4] = new Vec2Int(2, 3);

        shootCells[3][1] = new CellChain();
        shootCells[3][1].chain = new Vec2Int[4];
        shootCells[3][1].chain[0] = new Vec2Int(0, 1);
        shootCells[3][1].chain[1] = new Vec2Int(1, 2);
        shootCells[3][1].chain[2] = new Vec2Int(0, 2);
        shootCells[3][1].chain[3] = new Vec2Int(1, 3);

        shootCells[3][0] = new CellChain();
        shootCells[3][0].chain = new Vec2Int[3];
        shootCells[3][0].chain[0] = new Vec2Int(0, 1);
        shootCells[3][0].chain[1] = new Vec2Int(0, 2);
        shootCells[3][0].chain[2] = new Vec2Int(0, 3);

        shootCells[2][4] = new CellChain();
        shootCells[2][4].chain = new Vec2Int[6];
        shootCells[2][4].chain[0] = new Vec2Int(3, 1);
        shootCells[2][4].chain[1] = new Vec2Int(4, 2);
        shootCells[2][4].chain[2] = new Vec2Int(2, 1);
        shootCells[2][4].chain[3] = new Vec2Int(3, 2);
        shootCells[2][4].chain[4] = new Vec2Int(1, 1);
        shootCells[2][4].chain[5] = new Vec2Int(0, 1);

        shootCells[2][3] = new CellChain();
        shootCells[2][3].chain = new Vec2Int[5];
        shootCells[2][3].chain[0] = new Vec2Int(2, 1);
        shootCells[2][3].chain[1] = new Vec2Int(3, 2);
        shootCells[2][3].chain[2] = new Vec2Int(1, 1);
        shootCells[2][3].chain[3] = new Vec2Int(2, 2);
        shootCells[2][3].chain[4] = new Vec2Int(0, 1);

        shootCells[2][2] = new CellChain();
        shootCells[2][2].chain = new Vec2Int[4];
        shootCells[2][2].chain[0] = new Vec2Int(1, 1);
        shootCells[2][2].chain[1] = new Vec2Int(2, 2);
        shootCells[2][2].chain[2] = new Vec2Int(0, 1);
        shootCells[2][2].chain[3] = new Vec2Int(1, 2);

        shootCells[2][1] = new CellChain();
        shootCells[2][1].chain = new Vec2Int[3];
        shootCells[2][1].chain[0] = new Vec2Int(1, 1);
        shootCells[2][1].chain[1] = new Vec2Int(0, 1);
        shootCells[2][1].chain[2] = new Vec2Int(1, 2);

        shootCells[2][0] = new CellChain();
        shootCells[2][0].chain = new Vec2Int[2];
        shootCells[2][0].chain[0] = new Vec2Int(0, 1);
        shootCells[2][0].chain[1] = new Vec2Int(0, 2);

        shootCells[1][4] = new CellChain();
        shootCells[1][4].chain = new Vec2Int[5];
        shootCells[1][4].chain[0] = new Vec2Int(4, 1);
        shootCells[1][4].chain[1] = new Vec2Int(3, 1);
        shootCells[1][4].chain[2] = new Vec2Int(1, 0);
        shootCells[1][4].chain[3] = new Vec2Int(2, 1);
        shootCells[1][4].chain[4] = new Vec2Int(1, 1);

        shootCells[1][3] = new CellChain();
        shootCells[1][3].chain = new Vec2Int[3];
        shootCells[1][3].chain[0] = new Vec2Int(3, 1);
        shootCells[1][3].chain[1] = new Vec2Int(2, 1);
        shootCells[1][3].chain[2] = new Vec2Int(1, 1);

        shootCells[1][2] = new CellChain();
        shootCells[1][2].chain = new Vec2Int[3];
        shootCells[1][2].chain[0] = new Vec2Int(2, 1);
        shootCells[1][2].chain[1] = new Vec2Int(1, 1);
        shootCells[1][2].chain[2] = new Vec2Int(0, 1);

        shootCells[1][1] = new CellChain();
        shootCells[1][1].chain = new Vec2Int[2];
        shootCells[1][1].chain[0] = new Vec2Int(1, 1);
        shootCells[1][1].chain[1] = new Vec2Int(0, 1);

        shootCells[1][0] = new CellChain();
        shootCells[1][0].chain = new Vec2Int[1];
        shootCells[1][0].chain[0] = new Vec2Int(0, 1);

        shootCells[0][4] = new CellChain();
        shootCells[0][4].chain = new Vec2Int[4];
        shootCells[0][4].chain[0] = new Vec2Int(3, 0);
        shootCells[0][4].chain[1] = new Vec2Int(2, 0);
        shootCells[0][4].chain[2] = new Vec2Int(1, 0);
        shootCells[0][4].chain[3] = new Vec2Int(4, 0);

        shootCells[0][3] = new CellChain();
        shootCells[0][3].chain = new Vec2Int[3];
        shootCells[0][3].chain[0] = new Vec2Int(3, 0);
        shootCells[0][3].chain[1] = new Vec2Int(2, 0);
        shootCells[0][3].chain[2] = new Vec2Int(1, 0);

        shootCells[0][2] = new CellChain();
        shootCells[0][2].chain = new Vec2Int[2];
        shootCells[0][2].chain[0] = new Vec2Int(2, 0);
        shootCells[0][2].chain[1] = new Vec2Int(1, 0);

        shootCells[0][1] = new CellChain();
        shootCells[0][1].chain = new Vec2Int[1];
        shootCells[0][1].chain[0] = new Vec2Int(1, 0);

    }

    public void canShoot(Unit unit, Unit enemy, Debug debug, UnitAction action, Game game, Vec2Double unitP) {

        if (unit.weapon==null)
            return;

        if (unit.weapon.typ==WeaponType.ROCKET_LAUNCHER)
            return;

        //boolean canShoot = true;
        //boolean lShoot = false;

        //int ls=150;
        Vec2Double b;

        double dx, dy;// a, l;
        //Vec2Double v;

        //todo если старый угол в цели то не менять
        //todo опережение еще добавить
        b = new Vec2Double(unitP.x, unitP.y+Constants.UNIT_H2);
        dx = (enemy.position.x - unitP.x);
        dy = ((enemy.position.y+Constants.UNIT_H2) - (unitP.y+Constants.UNIT_H2));

        double h=Math.sqrt((unitP.x-enemy.position.x)*(unitP.x-enemy.position.x)+(unitP.y-enemy.position.y)*(unitP.y-enemy.position.y))*Math.tan(unit.weapon.spread)*2;

        if (Constants.UNIT_H/h<0.4 && (game.getCurrentTick()-lastTickShoot1<80 && (unit.id==3 || unit.id==4) || game.getCurrentTick()-lastTickShoot2<80 && (unit.id==5 || unit.id==6))) {
            action.setAim(new Vec2Double(dx, dy));
            return;
        }

        //if (unit.weapon!=null)
            //System.out.println("la="+unit.weapon.lastAngle+" sa="+Math.atan2(dy, dx));

//        if (unit.weapon!=null && ldx!=0 && ldy!=0) {
//
//            double d = Math.sqrt(dx*dx+dy*dy);
//
//            double bx, by;
//            //bx = b.x + dx;
//            //by = b.y + ldy * dx/ldx;
//
//            bx = (b.x + d*Math.cos(unit.weapon.lastAngle));
//            by = (b.y + d*Math.sin(unit.weapon.lastAngle));
//
//            if (Constants.ON_DEBUG)
//                debug.draw(new CustomData.Line(new Vec2Float(bx, by), new Vec2Float(bx+0.1f, by+0.1f), 0.1f, new ColorFloat(0, 128, 0, 0.5f)));
//
//            if (bx>=enemy.position.x-Constants.UNIT_W2
//                && bx<=enemy.position.x+Constants.UNIT_W2
//                && by>=enemy.position.y
//                && by<=enemy.position.y+Constants.UNIT_H) {
//
//                boolean prevShoot = checkShoot(ldx, ldy, enemy, new Vec2Double(unit.position.x, unit.position.y+Constants.UNIT_H2), game, unit, debug);
//                boolean curShoot = checkShoot(dx, dy, enemy, new Vec2Double(unit.position.x, unit.position.y+Constants.UNIT_H2), game, unit, debug);
//
//                if (prevShoot) {
//                    dx = ldx;
//                    dy = ldy;
//
//                    //System.out.println("last angel");
//                }
//
//                if (prevShoot || curShoot)
//                    action.setShoot(true);
//
//            } else {
//                boolean curShoot = checkShoot(dx, dy, enemy, new Vec2Double(unit.position.x, unit.position.y+Constants.UNIT_H2), game, unit, debug);
//
//                if (curShoot) {
//                    action.setShoot(true);
//                }
//
//            }
//
//        }

        boolean curShoot = checkShoot(dx, dy, enemy, new Vec2Double(unitP.x, unitP.y+Constants.UNIT_H2), game, unit, debug, unitP);

        if (curShoot) {
            action.setShoot(true);
        }

        //в голову
        if (!action.shoot) {
            dx = (enemy.position.x - unitP.x);
            dy = ((enemy.position.y+Constants.UNIT_H) - (unitP.y+Constants.UNIT_H2));

            curShoot = checkShoot(dx, dy, enemy, new Vec2Double(unitP.x, unitP.y+Constants.UNIT_H2), game, unit, debug, unitP);

            if (curShoot) {
                action.setShoot(true);
            }
        }

        //todo проверить что не попадет в стену при этом
        //пока не будем так
        /*if (enemy.jumpState.canJump && !enemy.jumpState.canCancel) {
            dy= (float) (dy + Math.abs(dx)*Constants.BULLET_SPEED_PER_TICK);// 0.9f;//Math.abs(dy*0.3f);
        } else if (!enemy.jumpState.canJump && !enemy.jumpState.canCancel) {
            dy= (float) (dy - Math.abs(dx)*Constants.BULLET_SPEED_PER_TICK/2);//0.9f;//Math.abs(dy*0.3f);
        }*/

        if (!enemy.jumpState.canJump && !enemy.jumpState.canCancel) {
            dy-=Constants.UNIT_Y_SPEED_PER_TICK*6;
        }

        if (enemy.jumpState.canJump && !enemy.jumpState.canCancel) {
            dy+=Constants.UNIT_Y_SPEED_PER_TICK*6;
        }

        if (Constants.ON_DEBUG)
            debug.draw(new CustomData.Line(new Vec2Float(unitP.x, unitP.y+Constants.UNIT_H2), new Vec2Float(unitP.x+dx, unitP.y+dy+Constants.UNIT_H2), 0.1f, new ColorFloat(128, 128, 0, 0.5f)));

        action.setAim(new Vec2Double(dx, dy));

        if (action.shoot)
            if (unit.id==3 || unit.id==4)
                lastTickShoot1=game.getCurrentTick();
            else
                lastTickShoot2=game.getCurrentTick();

        //ldx=dx;
        //ldy=dy;
    }

    public boolean checkShoot(double dx, double dy, Unit enemy, Vec2Double b, Game game, Unit unit, Debug debug, Vec2Double unitP) {

        boolean canShoot = true;
        boolean lShoot = false;

        int ls=150;

        double l;
        Vec2Double v;

        l = Math.sqrt(dx*dx + dy*dy);
        v = new Vec2Double(0.15 * dx/l, 0.15 * dy/l);

        for (int i=0;i<ls;i++) {
            if (Constants.ON_DEBUG)
                debug.draw(new CustomData.Rect(new Vec2Float(b.x-0.1f, b.y-0.1f), new Vec2Float(0.2f, 0.2f), new ColorFloat(1, 1, 0, 0.5f)));

            if (canShoot && check(b)) {
                canShoot = false;
                break;
            }


            //можно попасть в своего или второго чужого
            if (Constants.is2x2) {
                for (Unit u:game.getUnits()) {
                    if (unit.id == u.id)
                        continue;

                    if (b.x>u.position.x-Constants.UNIT_W2 && b.x<u.position.x+Constants.UNIT_W2 && b.y>u.position.y && b.y<u.position.y+Constants.UNIT_H) {
                        if (unit.playerId==u.playerId)
                            return false;
                        else
                            lShoot=true;

                        break;
                    }
                }
            }

            if (b.x>enemy.position.x-Constants.UNIT_W2 && b.x<enemy.position.x+Constants.UNIT_W2 && b.y>enemy.position.y && b.y<enemy.position.y+Constants.UNIT_H) {
                lShoot = true;
                break;
            }

            b.x+=v.x;
            b.y+=v.y;
        }

        return canShoot && lShoot;
    }

    private boolean check(Vec2Double b) {

        if (World.patencyMap[(int)b.y][(int)b.x] == 0)
            return true;

        if (World.patencyMap[(int)(b.y-0.2f)][(int)(b.x+0.2f)] == 0)
            return true;
        if (World.patencyMap[(int)(b.y-0.2f)][(int)(b.x-0.2f)] == 0)
            return true;
        if (World.patencyMap[(int)(b.y+0.2f)][(int)(b.x+0.2f)] == 0)
            return true;
        if (World.patencyMap[(int)(b.y+0.2f)][(int)(b.x-0.2f)] == 0)
            return true;

        return false;
    }

    private boolean hitMy(Unit unit, Game game, Debug debug) {

        return false;
    }
}
