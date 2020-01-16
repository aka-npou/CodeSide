package myModel;

import model.*;
import sims.Sim_v1;

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

    private static int checkHit(double bx, double by, double ux, double uy) {

        if (bx+0.1f > ux-Constants.UNIT_W2 &&
                bx-0.1f < ux+Constants.UNIT_W2 &&
                by+0.1f > uy &&
                by-0.1f < uy+Constants.UNIT_H)
            return 1;

        return 0;
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

        double vx = Constants.UNIT_X_SPEED_PER_TICK;//game.getProperties().getUnitMaxHorizontalSpeed()/game.getProperties().getTicksPerSecond();
        double vy = Constants.UNIT_Y_SPEED_PER_TICK;//game.getProperties().getUnitJumpSpeed()/game.getProperties().getTicksPerSecond();
        float size = 0.1f;
        float tick_f = 0;

        //проверять лучи в 8 сторон и с изгибами и если стоять
        //если луч доходит до конца - то мимо пули
        //если задевает в тики, то стоп - попали и не идти туда
        //для каждого луча хранить попадания
        //идти в меньший урон

        float checkHit;
        float[] steps = new float[4];
        Vec2Double p = new Vec2Double();
        boolean isBreak;

        isBreak=false;
        checkHit=0;
        p.x = unit.position.x;
        p.y = unit.position.y;

        vy=0f;

        for (int tick = 1; tick < 12; tick++) {

            if (vx != 0) {
                p.x += vx;

                debug.draw(new CustomData.Rect(new Vec2Float(p.x, p.y), new Vec2Float(size, size), new ColorFloat(0,1,0,1)));

                if (World.map[(int)(p.y)][(int)(p.x + Constants.UNIT_W2)] == 1 ||
                        World.map[(int)(p.y + Constants.UNIT_H2)][(int)(p.x + Constants.UNIT_W2)] == 1 ||
                        World.map[(int)(p.y + Constants.UNIT_H)][(int)(p.x + Constants.UNIT_W2)] == 1) {
                    p.x -= vx;
                    vx = 0d;
                }

                //если внизу пусто то падаем и всё
                if (World.map[(int)(p.y-1)][(int)(p.x + Constants.UNIT_W2)] == 0 && World.map[(int)(p.y-1)][(int)(p.x - Constants.UNIT_W2)] == 0) {
                    vy = Constants.UNIT_Y_SPEED_PER_TICK;
                }
            }

            p.y+=vy;

            tick_f=tick;
            for (int i=0; i<bullets.size();i++) {
                checkHit += checkHit(game.getBullets()[i].position.x + bullets.get(i).x * tick_f,
                        game.getBullets()[i].position.y + bullets.get(i).y * tick_f,
                        p.x,
                        p.y);
            }

        }

        if (!isBreak)
            checkHit /=12f;


        steps[0] = checkHit;

        vx = Constants.UNIT_X_SPEED_PER_TICK;
        vy = Constants.UNIT_Y_SPEED_PER_TICK;

        isBreak=false;
        checkHit=0;
        p.x = unit.position.x;
        p.y = unit.position.y;

        for (int tick = 1; tick < 12; tick++) {

            if (vx != 0) {
                p.x -= vx;

                debug.draw(new CustomData.Rect(new Vec2Float(p.x, p.y), new Vec2Float(size, size), new ColorFloat(0,1,0,1)));

                if (World.map[(int)(p.y)][(int)(p.x - Constants.UNIT_W2)] == 3 ||
                        World.map[(int)(p.y + Constants.UNIT_H2)][(int)(p.x - Constants.UNIT_W2)] == 3 ||
                        World.map[(int)(p.y + Constants.UNIT_H)][(int)(p.x - Constants.UNIT_W2)] == 3) {
                    p.x += vx;
                    vx = 0d;
                }

                //если внизу пусто то падаем и всё
                if (World.map[(int)(p.y-1)][(int)(p.x + Constants.UNIT_W2)] == 0 && World.map[(int)(p.y-1)][(int)(p.x - Constants.UNIT_W2)] == 0) {
                    checkHit /= tick_f==0f?1f:tick_f;
                    isBreak=true;
                    break;
                }
            }

            tick_f=tick;
            for (int i=0; i<bullets.size();i++) {
                checkHit += checkHit(game.getBullets()[i].position.x + bullets.get(i).x * tick_f,
                        game.getBullets()[i].position.y + bullets.get(i).y * tick_f,
                        p.x,
                        p.y);
            }

        }

        if (!isBreak)
            checkHit /=12f;

        steps[1] = checkHit;

        vx = Constants.UNIT_X_SPEED_PER_TICK;
        vy = Constants.UNIT_Y_SPEED_PER_TICK;

        isBreak=false;
        checkHit=0;
        p.x = unit.position.x;
        p.y = unit.position.y;

        for (int tick = 0; tick < 12; tick++) {

            p.y += vy;

            debug.draw(new CustomData.Rect(new Vec2Float(p.x, p.y), new Vec2Float(size, size), new ColorFloat(0,1,0,1)));

            if (World.map[(int)(p.y + Constants.UNIT_H)][(int)(p.x + Constants.UNIT_W2)] == 3 ||
                    World.map[(int)(p.y + Constants.UNIT_H)][(int)(p.x + Constants.UNIT_W2)] == 3) {
                checkHit /= tick_f==0f?1f:tick_f;
                isBreak=true;
                break;
            }

            tick_f=tick;
            for (int i=0; i<bullets.size();i++) {
                checkHit += checkHit(game.getBullets()[i].position.x + bullets.get(i).x * tick_f,
                        game.getBullets()[i].position.y + bullets.get(i).y * tick_f,
                        unit.position.x,
                        unit.position.y + vy * tick_f);
            }

        }

        if (!isBreak)
            checkHit /=12f;

        steps[2] = checkHit;

        vx = Constants.UNIT_X_SPEED_PER_TICK;
        vy = Constants.UNIT_Y_SPEED_PER_TICK;

        isBreak=false;
        checkHit=0;
        p.x = unit.position.x;
        p.y = unit.position.y;

        for (int tick = 0; tick < 12; tick++) {

            if (vy !=0f) {
                p.y -= vy;

                debug.draw(new CustomData.Rect(new Vec2Float(p.x, p.y), new Vec2Float(size, size), new ColorFloat(0, 1, 0, 1)));

                if (World.map[(int) (p.y)][(int) (p.x + Constants.UNIT_W2)] == 1 ||
                        World.map[(int) (p.y)][(int) (p.x + Constants.UNIT_W2)] == 1) {
//                checkHit /= tick_f==0f?1f:tick_f;
//                isBreak=true;
//                break;
                    vy = 0f;
                }
            }

            tick_f=tick;
            for (int i=0; i<bullets.size();i++) {
                checkHit += checkHit(game.getBullets()[i].position.x + bullets.get(i).x * tick_f,
                        game.getBullets()[i].position.y + bullets.get(i).y * tick_f,
                        p.x,
                        p.y);
            }

        }

        if (!isBreak)
            checkHit /=12f;

        steps[3] = checkHit;

        for (float i:steps) {
            System.out.println("hit = " + i);
        }

    }
}
