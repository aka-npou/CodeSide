package myModel;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by aka_npou on 28.11.2019.
 */
public class Dodge {

    public static void dodge(Unit unit, Game game, Debug debug, UnitAction action) {

        //dodge_v1(unit, game, debug, action);
        dodge_v2(unit, game, debug, action);

    }

    public static void dodge_v1(Unit unit, Game game, Debug debug, UnitAction action) {
        for (Bullet b:game.getBullets()) {

            double dx;
            dx = Math.abs(b.position.x - unit.position.x);

            double kx;
            kx = dx/b.velocity.x;

            if (Constants.ON_DEBUG)
                debug.draw(new CustomData.Rect(new Vec2Float(b.position.x + kx*b.velocity.x, b.position.y + kx*b.velocity.y), new Vec2Float(0.2f, 0.2f), new ColorFloat(1,1,0,1)));

            Vec2Double pbf = new Vec2Double(b.position.x + kx*b.velocity.x, b.position.y + kx*b.velocity.y);

            //
            if (!check(pbf, unit))
                return;

            System.out.println("pbf= " + pbf.x + " " + pbf.y);
            double t = Math.sqrt((pbf.x-b.position.x)*(pbf.x-b.position.x)+(pbf.y-b.position.y)*(pbf.y-b.position.y));
            System.out.println("l= " + t + " " + Math.abs(unit.position.y-pbf.y));
            t/=game.getProperties().getWeaponParams().get(b.getWeaponType()).getBullet().getSpeed();
            t*=game.getProperties().getTicksPerSecond();

            double dt = Math.abs(unit.position.y-pbf.y)/game.getProperties().getUnitJumpSpeed() * game.getProperties().getTicksPerSecond();

            System.out.println("t= " + t + " " + dt);
            if (t-2 <= dt || t-1 <= dt || t <= dt)
                action.jump = true;
        }

        //сделать переборы движений вверх вниз вправо влево и смотреть куда валить



        /*Unit enemy = null;

        for (Unit u:game.getUnits()) {
            if (u.getPlayerId() != unit.getPlayerId()) {
                enemy = u;
                break;
            }
        }

        if (enemy.weapon != null) {
            System.out.println("m " + enemy.weapon.magazine);
            System.out.println("ft " + enemy.weapon.fireTimer);
            debug.draw(new CustomData.Log("m " + enemy.weapon.magazine));
            debug.draw(new CustomData.Log("ft " + enemy.weapon.fireTimer));
        }

        action.jump = true;*/
    }

    private static boolean check(Vec2Double b, Unit unit) {

        if (b.x+0.1f > unit.position.x-Constants.UNIT_W2 &&
            b.x-0.1f < unit.position.x+Constants.UNIT_W2 &&
            b.y+0.1f > unit.position.y &&
            b.y-0.1f < unit.position.y+Constants.UNIT_H)
            return true;

        return false;
    }

    private static boolean checkHit(double bx, double by, double ux, double uy) {

        if (bx+0.1f > ux-Constants.UNIT_W2 &&
                bx-0.1f < ux+Constants.UNIT_W2 &&
                by+0.1f > uy &&
                by-0.1f < uy+Constants.UNIT_H)
            return true;

        return false;
    }

    //todo хранить все координаты приблизительно в инт до 5 знака и считать пули один раз их скорости

    public static void dodge_v2(Unit unit, Game game, Debug debug, UnitAction action) {

        ArrayList<Vec2Double> bullets = new ArrayList<>();

        for (Bullet b:game.getBullets()) {
            System.out.println("b " + b.position.x + " " + b.position.y);

            double bulletSpeed = game.getProperties().getWeaponParams().get(b.getWeaponType()).getBullet().getSpeed()/game.getProperties().getTicksPerSecond();

            double a = Math.atan2(b.velocity.y , b.velocity.x);

            Vec2Double bpf = new Vec2Double(bulletSpeed * Math.cos(a), bulletSpeed * Math.sin(a));

            System.out.println("bpf " + bpf.x + " " + bpf.y);
            
            bullets.add(bpf);

        }

        /*int t=0;

        for (int y = -6; y < 7; y++) {
            for (int x = -6; x < 7; x++) {

                t = Math.abs(y*x);

                //debug.draw(new CustomData.Rect(new Vec2Float(unit.position.x, unit.position.y), new Vec2Float(0.2f, 0.2f), new ColorFloat(0,1,0,1)));

            }
        }*/

        ArrayList<Vec2Float> posiblePosition = new ArrayList<>();
        double vx = game.getProperties().getUnitMaxHorizontalSpeed()/game.getProperties().getTicksPerSecond();
        double vy = game.getProperties().getUnitJumpSpeed()/game.getProperties().getTicksPerSecond();
        float size = 0.1f;
        float tick_f = 0;

        //проверять лучи в 8 сторон и с изгибами и если стоять
        //если луч доходит до конца - то мимо пули
        //если задевает в тики, то стоп - попали и не идти туда
        //для каждого луча хранить попадания
        //идти в меньший урон

        for (int tick = 0; tick < 12; tick++) {

            tick_f = tick;
            
            for (int i=0; i<bullets.size();i++) {
                debug.draw(new CustomData.Rect(new Vec2Float(game.getBullets()[i].position.x + bullets.get(i).x * tick_f, game.getBullets()[i].position.y + bullets.get(i).y * tick_f), new Vec2Float(0.2f, 0.2f), new ColorFloat(0,1,1,1)));

                if (!checkHit(game.getBullets()[i].position.x + bullets.get(i).x * tick_f,
                        game.getBullets()[i].position.y + bullets.get(i).y * tick_f,
                        unit.position.x + vx * tick_f,
                        unit.position.y + vy * tick_f))
                    posiblePosition.add(new Vec2Float(unit.position.x + vx * tick_f, unit.position.y + vy * tick_f));

                if (!checkHit(game.getBullets()[i].position.x + bullets.get(i).x * tick_f,
                        game.getBullets()[i].position.y + bullets.get(i).y * tick_f,
                        unit.position.x - vx * tick_f,
                        unit.position.y + vy * tick_f))
                    posiblePosition.add(new Vec2Float(unit.position.x - vx * tick_f, unit.position.y + vy * tick_f));

                if (!checkHit(game.getBullets()[i].position.x + bullets.get(i).x * tick_f,
                        game.getBullets()[i].position.y + bullets.get(i).y * tick_f,
                        unit.position.x + vx * tick_f,
                        unit.position.y - vy * tick_f))
                    posiblePosition.add(new Vec2Float(unit.position.x + vx * tick_f, unit.position.y - vy * tick_f));

                if (!checkHit(game.getBullets()[i].position.x + bullets.get(i).x * tick_f,
                        game.getBullets()[i].position.y + bullets.get(i).y * tick_f,
                        unit.position.x - vx * tick_f,
                        unit.position.y - vy * tick_f))
                    posiblePosition.add(new Vec2Float(unit.position.x - vx * tick_f, unit.position.y - vy * tick_f));

                if (!checkHit(game.getBullets()[i].position.x + bullets.get(i).x * tick_f,
                        game.getBullets()[i].position.y + bullets.get(i).y * tick_f,
                        unit.position.x + vx * tick_f,
                        unit.position.y))
                    posiblePosition.add(new Vec2Float(unit.position.x + vx * tick_f, unit.position.y));

                if (!checkHit(game.getBullets()[i].position.x + bullets.get(i).x * tick_f,
                        game.getBullets()[i].position.y + bullets.get(i).y * tick_f,
                        unit.position.x - vx * tick_f,
                        unit.position.y))
                    posiblePosition.add(new Vec2Float(unit.position.x - vx * tick_f, unit.position.y));
            }

            /*debug.draw(new CustomData.Rect(new Vec2Float(unit.position.x + tick * vx, unit.position.y), new Vec2Float(size, size), new ColorFloat(0,1,0,1)));
            debug.draw(new CustomData.Rect(new Vec2Float(unit.position.x - tick * vx, unit.position.y), new Vec2Float(size, size), new ColorFloat(0,1,0,1)));

            debug.draw(new CustomData.Rect(new Vec2Float(unit.position.x + tick * vx, unit.position.y + tick * vy), new Vec2Float(size, size), new ColorFloat(0,1,0,1)));
            debug.draw(new CustomData.Rect(new Vec2Float(unit.position.x - tick * vx, unit.position.y + tick * vy), new Vec2Float(size, size), new ColorFloat(0,1,0,1)));

            debug.draw(new CustomData.Rect(new Vec2Float(unit.position.x + tick * vx, unit.position.y - tick * vy), new Vec2Float(size, size), new ColorFloat(0,1,0,1)));
            debug.draw(new CustomData.Rect(new Vec2Float(unit.position.x - tick * vx, unit.position.y - tick * vy), new Vec2Float(size, size), new ColorFloat(0,1,0,1)));


            debug.draw(new CustomData.Rect(new Vec2Float(unit.position.x, unit.position.y + tick * vy), new Vec2Float(size, size), new ColorFloat(0,1,0,1)));
            debug.draw(new CustomData.Rect(new Vec2Float(unit.position.x, unit.position.y - tick * vy), new Vec2Float(size, size), new ColorFloat(0,1,0,1)));*/


        }

        System.out.println("posiblePosition " + posiblePosition.size());
        for (Vec2Float p:posiblePosition) {
            debug.draw(new CustomData.Rect(p, new Vec2Float(size, size), new ColorFloat(0,1,0,1)));
        }


    }
}
