package myModel;

import model.*;
import sims.Sim_v1;
import sims.Sim_v2;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by aka_npou on 28.11.2019.
 */
public class Dodge {

    public static ArrayList<Vec2Double> bullets;

    public static void setBullets(Unit unit, Game game, Debug debug) {

        bullets = new ArrayList<>();

        for (Bullet b:game.getBullets()) {

            System.out.println("b x=" + b.position.x + " y=" + b.position.y + " vx=" + b.velocity.x + " vy=" + b.velocity.y);

            Vec2Double bpf = new Vec2Double(b.velocity.x/game.getProperties().getTicksPerSecond(), b.velocity.y/game.getProperties().getTicksPerSecond());

            System.out.println("bpf " + bpf.x + " " + bpf.y);

            bullets.add(bpf);

        }

    }

    public static void dodge(Unit unit, Game game, Debug debug, UnitAction action) {

        //dodge_v1(unit, game, debug, action);
        //dodge_v2(unit, game, debug, action);
        dodge_v3(unit, game, debug, action);

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

    //возможно надо перейти на проверку линии пули с юнитом
    //a c b d
    private static int checkHitFlow(double bx1, double by1, double ux1, double uy1,
                                    double bx2, double by2, double ux2, double uy2) {

        //проверка линии пули с первым положением
        //4 линии

        //верх
        if (intersect(bx1,bx2, by1,by2,
                        ux1-Constants.UNIT_W2, uy1+Constants.UNIT_H, ux1+Constants.UNIT_W2, uy1+Constants.UNIT_H))
            return 1;

        //низ
        if (intersect(bx1,bx2, by1,by2,
                ux1-Constants.UNIT_W2, uy1, ux1+Constants.UNIT_W2, uy1))
            return 1;
        //право
        if (intersect(bx1,bx2, by1,by2,
                ux1+Constants.UNIT_W2, uy1, ux1+Constants.UNIT_W2, uy1+Constants.UNIT_H))
            return 1;
        //лево
        if (intersect(bx1,bx2, by1,by2,
                ux1-Constants.UNIT_W2, uy1, ux1-Constants.UNIT_W2, uy1+Constants.UNIT_H))
            return 1;


        //проверка линии пули с вторым положением
        //4 линии

        //верх
        if (intersect(bx1,bx2, by1,by2,
                ux2-Constants.UNIT_W2, uy2+Constants.UNIT_H, ux2+Constants.UNIT_W2, uy2+Constants.UNIT_H))
            return 1;

        //низ
        if (intersect(bx1,bx2, by1,by2,
                ux2-Constants.UNIT_W2, uy2, ux2+Constants.UNIT_W2, uy2))
            return 1;
        //право
        if (intersect(bx1,bx2, by1,by2,
                ux2+Constants.UNIT_W2, uy2, ux2+Constants.UNIT_W2, uy2+Constants.UNIT_H))
            return 1;
        //лево
        if (intersect(bx1,bx2, by1,by2,
                ux2-Constants.UNIT_W2, uy2, ux2-Constants.UNIT_W2, uy2+Constants.UNIT_H))
            return 1;

        return 0;
    }

    private static boolean intersect(double ax1, double ax2, double ay1, double ay2,
    double bx1, double by1, double bx2, double by2) {

        double v1,v2,v3,v4;

        v1=(bx2-bx1)*(ay1-by1)-(by2-by1)*(ax1-bx1);
        v2=(bx2-bx1)*(ay2-by1)-(by2-by1)*(ax2-bx1);
        v3=(ax2-ax1)*(by1-ay1)-(ay2-ay1)*(bx1-ax1);
        v4=(ax2-ax1)*(by2-ay1)-(ay2-ay1)*(bx2-ax1);

        return (v1*v2<0) && (v3*v4<0);
    }

    /*private static boolean intersect (double ax, double bx, double ay, double by,
                                      double cx, double cy, double dx, double dy) {
        return intersect_1(ax, bx, cx, dx)
                && intersect_1(ay,by, cy, dy)
                && area(ax, bx, cx,ay, by, cy) * area(ax, bx, dx, ay, by, dy) <= 0
                && area(cx, dx, ax, cy, dy, ay) * area(cx, dx, bx, cy, dy, by) <= 0;
    }

    private static boolean intersect_1 (double a, double b, double c, double d) {
        double q;
        if (a > b)  {q=a;a=b;b=q;}//swap (a, b);
        if (c > d)  {q=c;c=d;d=q;}//swap (c, d);
        return Math.max(a,c) <= Math.min(b,d);
    }

    private static double area (double ax, double ay, double bx, double by, double cx, double cy) {
        return (bx - ax) * (cy - ay) - (by - ay) * (cx - ax);
    }*/

    /*private static boolean intersect(double x1, double x2, double y1, double y2,
                                     double x3, double x4, double y3, double y4) {

        double n = (x1-x2)*(y3-y4)-(y1-y2)*(x3-x4);

        if (Math.abs(n) < 1e-9)
            return false;

        double x,y;

        x = ((x1*y2-y1*x2)*(x3-x4)-(x1-x2)*(x3*y4-y3*x4))/n;
        y = ((x1*y2-y1*x2)*(y3-y4)-(y1-y2)*(x3*y4-y3*x4))/n;



    }*/

    private static double det (double ba, double bb, double ua, double ub) {
        return ba * ub - bb * ua;
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

    public static void dodge_v3(Unit unit, Game game, Debug debug, UnitAction action) {

        int[] hits = new int[Sim_v2.steps.length];

        float tick_f;
        int hit;
        Vec2Double bullet = new Vec2Double();
        Vec2Double u = new Vec2Double();

        //todo возможно проверять расстояние до центра юнита и если менее чего-то то уже точнее


        for (int i=0;i<hits.length;i++) {

            for (int b=0; b<bullets.size();b++) {

                //выкинуть свои пули
                if (game.getBullets()[b].unitId ==  unit.id && game.getBullets()[b].explosionParams == null)
                    continue;

                //выкинуть пули которые уже точно не в нас, позиция и скорость не в нашу сторону
                if (game.getBullets()[b].position.x > unit.position.x+Constants.UNIT_W2+0.2f && bullets.get(b).x > 0)
                    continue;

                if (game.getBullets()[b].position.x < unit.position.x-Constants.UNIT_W2-0.2f && bullets.get(b).x < 0)
                    continue;

                if (game.getBullets()[b].position.y > unit.position.y+Constants.UNIT_H+0.2f && bullets.get(b).y > 0)
                    continue;

                if (game.getBullets()[b].position.y < unit.position.y-0.2f && bullets.get(b).y < 0)
                    continue;

                tick_f=1f;

                bullet.x = game.getBullets()[b].position.x;
                bullet.y = game.getBullets()[b].position.y;

                u.x = unit.position.x;
                u.y = unit.position.y;

                for (Vec2Float p:Sim_v2.steps[i]) {

                    hit = checkHit(bullet.x,
                            bullet.y,
                            u.x,
                            u.y);

                    if (hit == 0)
                        hit = checkHit(bullet.x+bullets.get(b).x * tick_f,
                                bullet.y+bullets.get(b).y * tick_f,
                                u.x,
                                u.y);

                    if (hit == 0)
                        hit = checkHit(bullet.x+bullets.get(b).x * tick_f,
                                bullet.y+bullets.get(b).y * tick_f,
                                p.x,
                                p.y);


                    if (hit == 0)
                        hit=checkHitFlow(bullet.x, bullet.y, u.x, u.y,
                            bullet.x+bullets.get(b).x * tick_f, bullet.y+bullets.get(b).y * tick_f, p.x, p.y);

                    if (hit==1) {
                        debug.draw(new CustomData.Rect(new Vec2Float(p.x-Constants.UNIT_W2, p.y), new Vec2Float(Constants.UNIT_W, Constants.UNIT_H), new ColorFloat(1,0,0,0.25f)));
                        debug.draw(new CustomData.Rect(new Vec2Float(bullet.x-0.05f, bullet.y - 0.05f), new Vec2Float(0.1f, 0.1f), new ColorFloat(0,0,1,0.25f)));
                    }
                    hits[i]+=hit;

                    bullet.x = game.getBullets()[b].position.x + bullets.get(b).x * tick_f;
                    bullet.y = game.getBullets()[b].position.y + bullets.get(b).y * tick_f;

                    debug.draw(new CustomData.Rect(new Vec2Float(bullet.x-0.1f, bullet.y - 0.1f), new Vec2Float(0.2f, 0.2f), new ColorFloat(0,0,1,0.25f)));

                    //проверить что пуля в стене
                    if (World.map[(int)bullet.y][(int)bullet.x] == 1) {
                        break;
                    }

                    u.x=p.x;
                    u.y=p.y;

                    tick_f++;
                }

            }


            System.out.println("hit " + i + " " + hits[i]);

        }

        int minHits=1000;
        Vec2Float p = null;

        for (int i=0;i<hits.length;i++) {

            if (p==null) {
                p=Sim_v2.steps[i][0];
                minHits=hits[i];
            } else {
                if (minHits>hits[i]) {
                    p=Sim_v2.steps[i][0];
                    minHits=hits[i];
                }
            }

        }

        action.setVelocity((p.x - unit.position.x)*Constants.TICKS_PER_SECOND);
        if (p.y>unit.position.y)
            action.jump=true;

        if (p.y<unit.position.y)
            action.jumpDown=true;

    }
}
