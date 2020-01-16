package util;

import model.*;
import myModel.Vec2Int;

import java.util.HashSet;

/**
 * Created by aka_npou on 30.11.2019.
 */
public class GenShootChain {

    int dx=5;
    int dy=4;

    HashSet<Vec2Int> chain = new HashSet<>();

    public void gen(Unit unit, Game game, Debug debug, UnitAction action) {

        debug.draw(new CustomData.Rect(new Vec2Float((float)(unit.getPosition().getX()+dx+0.2f-0.5f), (float)(unit.getPosition().getY()+dy+0.2f+0.4f)), new Vec2Float(0.6f, 0.6f), new ColorFloat(0,255,0,0.5f)));

        if (unit.getWeapon() != null) {
            if (Math.abs(unit.getPosition().getX() - 30.5d) > 0.005d) {
                action.setVelocity((30.5 - unit.getPosition().getX()) * 10f);
                action.setShoot(false);
            } else {
                //debug.draw(new CustomData.Rect(new Vec2Float((float)(unit.getPosition().getX()+dx+0.2f), (float)(unit.getPosition().getY()+dy+0.2f)), new Vec2Float(0.6f, 0.6f), new ColorFloat(0,255,0,0.5f)));
                if (game.getBullets().length == 0 && unit.getWeapon().getFireTimer() == null) {

                    System.out.println("shootCells[" + (int) (dy) + "][" + (int) (dx) + "] = new CellChain();");
                    int n = 0;
                    for (Vec2Int v : chain) {

                        if (v.x - 30 > dx || v.y - 1 > dy)
                            continue;

                        if (v.x - 30 == 0 && v.y - 1 == 0)
                            continue;

                        n++;

                    }
                    System.out.println("shootCells[" + (int) (dy) + "][" + (int) (dx) + "].chain = new Vec2Int[" + n + "];");
                    n = 0;

                    for (Vec2Int v : chain) {

                        if (v.x - 30 > dx || v.y - 1 > dy)
                            continue;

                        if (v.x - 30 == 0 && v.y - 1 == 0)
                            continue;

                        System.out.println("shootCells[" + (int) (dy) + "][" + (int) (dx) + "].chain[" + n + "] = new Vec2Int(" + (v.x - 30) + ", " + (v.y - 1) + ");");
                        n++;

                    }
                    System.out.println("");

                    dx--;
                    if (dx <= -1) {
                        dx = 4;
                        dy--;
                    }
                    chain = new HashSet<>();
                    action.setShoot(true);
                    action.setAim(new Vec2Double(dx, dy - .4f));
                    //action.setAim(new Vec2Double(4f, 3.6f));

                    //System.out.println("SHOOT " + game.getCurrentTick());
                } else {
                    action.setShoot(false);
                }
            }
        }

        for (Bullet b:game.getBullets()) {
            //System.out.println("b " + (b.getPosition().getX()) + " " + (b.getPosition().getY()));
            chain.add(new Vec2Int((int)(b.position.x), (int)(b.position.y)));
        }
    }
}
