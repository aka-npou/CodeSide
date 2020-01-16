package myModel;

import model.*;
import sims.Sim_v3;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by aka_npou on 28.11.2019.
 */
public class Dodge {

    public static ArrayList<Vec2Double> bullets;
    public static int[] hits;

    public static void setBullets(Unit unit, Game game, Debug debug) {

        bullets = new ArrayList<>();

        for (Bullet b:game.getBullets()) {

            //System.out.println("b x=" + b.position.x + " y=" + b.position.y + " vx=" + b.velocity.x + " vy=" + b.velocity.y);

            Vec2Double bpf = new Vec2Double(b.velocity.x/game.getProperties().getTicksPerSecond(), b.velocity.y/game.getProperties().getTicksPerSecond());

            //System.out.println("bpf " + bpf.x + " " + bpf.y);

            bullets.add(bpf);

        }

    }

    public static void dodge(Unit unit, Game game, Debug debug, UnitAction action) {

        //dodge_v1(unit, game, debug, action);
        //dodge_v2(unit, game, debug, action);
        //dodge_v3(unit, game, debug, action);
        dodge_v4(unit, game, debug, action);

    }

    private static int checkHit(double bx, double by, double ux, double uy, double size) {

        if (bx+size > ux-Constants.UNIT_W2 &&
                bx-size < ux+Constants.UNIT_W2 &&
                by+size > uy &&
                by-size < uy+Constants.UNIT_H)
            return 1;

        return 0;
    }

    private static int checkHitBoxes(double bx, double by, double ux1, double uy1, double ux2, double uy2) {

        double b=0.005;
        if (bx+bx*b >= Math.min(ux1, ux2) &&
                bx-bx*b <= Math.max(ux1, ux2) &&
                by+by*b >= Math.min(uy1, uy2) &&
                by-by*b <= Math.max(uy1, uy2))
            return 1;

        return 0;
    }

    //возможно надо перейти на проверку линии пули с юнитом
    //a c b d
    private static int checkHitFlow(double bx1, double by1, double ux1, double uy1,
                                    double bx2, double by2, double ux2, double uy2, double sizeB) {
        //
        if (checkHitBoxes(bx1+sizeB/2d, by1+sizeB/2d,ux1-Constants.UNIT_W2, uy1, ux2-Constants.UNIT_W2, uy2)==1)
            return 1;

        if (checkHitBoxes(bx1-sizeB/2d, by1+sizeB/2d,ux1+Constants.UNIT_W2, uy1, ux2+Constants.UNIT_W2, uy2)==1)
            return 1;

        if (checkHitBoxes(bx1+sizeB/2d, by1-sizeB/2d,ux1-Constants.UNIT_W2, uy1+Constants.UNIT_H, ux2-Constants.UNIT_W2, uy2+Constants.UNIT_H)==1)
            return 1;

        if (checkHitBoxes(bx1-sizeB/2d, by1-sizeB/2d,ux1+Constants.UNIT_W2, uy1+Constants.UNIT_H, ux2+Constants.UNIT_W2, uy2+Constants.UNIT_H)==1)
            return 1;

        //
        if (checkHitBoxes(bx1+sizeB/2d, by2+sizeB/2d,ux1-Constants.UNIT_W2, uy1, ux2-Constants.UNIT_W2, uy2)==1)
            return 1;

        if (checkHitBoxes(bx1-sizeB/2d, by2+sizeB/2d,ux1+Constants.UNIT_W2, uy1, ux2+Constants.UNIT_W2, uy2)==1)
            return 1;

        if (checkHitBoxes(bx1+sizeB/2d, by2-sizeB/2d,ux1-Constants.UNIT_W2, uy1+Constants.UNIT_H, ux2-Constants.UNIT_W2, uy2+Constants.UNIT_H)==1)
            return 1;

        if (checkHitBoxes(bx1-sizeB/2d, by2-sizeB/2d,ux1+Constants.UNIT_W2, uy1+Constants.UNIT_H, ux2+Constants.UNIT_W2, uy2+Constants.UNIT_H)==1)
            return 1;

        //
        if (checkHitBoxes(bx2+sizeB/2d, by1+sizeB/2d,ux1-Constants.UNIT_W2, uy1, ux2-Constants.UNIT_W2, uy2)==1)
            return 1;

        if (checkHitBoxes(bx2-sizeB/2d, by1+sizeB/2d,ux1+Constants.UNIT_W2, uy1, ux2+Constants.UNIT_W2, uy2)==1)
            return 1;

        if (checkHitBoxes(bx2+sizeB/2d, by1-sizeB/2d,ux1-Constants.UNIT_W2, uy1+Constants.UNIT_H, ux2-Constants.UNIT_W2, uy2+Constants.UNIT_H)==1)
            return 1;

        if (checkHitBoxes(bx2-sizeB/2d, by1-sizeB/2d,ux1+Constants.UNIT_W2, uy1+Constants.UNIT_H, ux2+Constants.UNIT_W2, uy2+Constants.UNIT_H)==1)
            return 1;

        //
        if (checkHitBoxes(bx2+sizeB/2d, by2+sizeB/2d,ux1-Constants.UNIT_W2, uy1, ux2-Constants.UNIT_W2, uy2)==1)
            return 1;

        if (checkHitBoxes(bx2-sizeB/2d, by2+sizeB/2d,ux1+Constants.UNIT_W2, uy1, ux2+Constants.UNIT_W2, uy2)==1)
            return 1;

        if (checkHitBoxes(bx2+sizeB/2d, by2-sizeB/2d,ux1-Constants.UNIT_W2, uy1+Constants.UNIT_H, ux2-Constants.UNIT_W2, uy2+Constants.UNIT_H)==1)
            return 1;

        if (checkHitBoxes(bx2-sizeB/2d, by2-sizeB/2d,ux1+Constants.UNIT_W2, uy1+Constants.UNIT_H, ux2+Constants.UNIT_W2, uy2+Constants.UNIT_H)==1)
            return 1;

        double border=0.1;
        //проверка линии пули с первым положением
        //4 линии

        //верх
        if (intersect(bx1,bx2, by1,by2,
                        ux1-Constants.UNIT_W2-border, uy1+Constants.UNIT_H+border, ux1+Constants.UNIT_W2+border, uy1+Constants.UNIT_H+border))
            return 1;

        //низ
        if (intersect(bx1,bx2, by1,by2,
                ux1-Constants.UNIT_W2-border, uy1-border, ux1+Constants.UNIT_W2+border, uy1-border))
            return 1;
        //право
        if (intersect(bx1,bx2, by1,by2,
                ux1+Constants.UNIT_W2+border, uy1-border, ux1+Constants.UNIT_W2+border, uy1+Constants.UNIT_H+border))
            return 1;
        //лево
        if (intersect(bx1,bx2, by1,by2,
                ux1-Constants.UNIT_W2-border, uy1-border, ux1-Constants.UNIT_W2-border, uy1+Constants.UNIT_H+border))
            return 1;


        //проверка линии пули с вторым положением
        //4 линии

        //верх
        if (intersect(bx1,bx2, by1,by2,
                ux2-Constants.UNIT_W2-border, uy2+Constants.UNIT_H+border, ux2+Constants.UNIT_W2+border, uy2+Constants.UNIT_H+border))
            return 1;

        //низ
        if (intersect(bx1,bx2, by1,by2,
                ux2-Constants.UNIT_W2-border, uy2-border, ux2+Constants.UNIT_W2+border, uy2-border))
            return 1;
        //право
        if (intersect(bx1,bx2, by1,by2,
                ux2+Constants.UNIT_W2+border, uy2-border, ux2+Constants.UNIT_W2+border, uy2+Constants.UNIT_H+border))
            return 1;
        //лево
        if (intersect(bx1,bx2, by1,by2,
                ux2-Constants.UNIT_W2-border, uy2-border, ux2-Constants.UNIT_W2-border, uy2+Constants.UNIT_H+border))
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

    //todo хранить все координаты приблизительно в инт до 5 знака и считать пули один раз их скорости

    public static void dodge_v3(Unit unit, Game game, Debug debug, UnitAction action) {

        int[] hits = new int[Sim_v3.steps.length];

        //float tick_f;
        int hit;
        Vec2Double bullet = new Vec2Double();
        Vec2Double u = new Vec2Double();


        boolean neeedCheck=false;
        for (int i=0;i<hits.length;i++) {

            for (int b=0; b<bullets.size();b++) {

                neeedCheck=false;

                //выкинуть свои пули
                if (game.getBullets()[b].unitId ==  unit.id && game.getBullets()[b].explosionParams == null)
                    continue;

                //if (distanceSqr(game.getBullets()[b].position.x, game.getBullets()[b].position.y, unit.position.x, unit.position.y+Constants.UNIT_H2) >= 9) {
                    //выкинуть пули которые уже точно не в нас, позиция и скорость не в нашу сторону
                    if (game.getBullets()[b].position.x+4f > unit.position.x + Constants.UNIT_W2 && Dodge.bullets.get(b).x < 0)
                        neeedCheck = true;

                    if (game.getBullets()[b].position.x-4f < unit.position.x - Constants.UNIT_W2 && Dodge.bullets.get(b).x > 0)
                        neeedCheck = true;

                    if (game.getBullets()[b].position.y+4f > unit.position.y + Constants.UNIT_H && Dodge.bullets.get(b).y < 0)
                        neeedCheck = true;

                    if (game.getBullets()[b].position.y-4f < unit.position.y && Dodge.bullets.get(b).y > 0)
                        neeedCheck = true;
                //}

                if (!neeedCheck)
                    continue;

                //tick_f=1f;

                bullet.x = game.getBullets()[b].position.x;
                bullet.y = game.getBullets()[b].position.y;

                u.x = unit.position.x;
                u.y = unit.position.y;

                for (Vec2Double p:Sim_v3.steps[i]) {

                    hit = checkHit(bullet.x,
                            bullet.y,
                            u.x,
                            u.y, game.getBullets()[b].size/2d);

                    if (hit == 0)
                        hit = checkHit(bullet.x+bullets.get(b).x,
                                bullet.y+bullets.get(b).y,
                                u.x,
                                u.y, game.getBullets()[b].size/2d);

                    if (hit == 0)
                        hit = checkHit(bullet.x+bullets.get(b).x,
                                bullet.y+bullets.get(b).y,
                                p.x,
                                p.y, game.getBullets()[b].size/2d);


                    if (hit == 0)
                        hit=checkHitFlow(bullet.x, bullet.y, u.x, u.y,
                            bullet.x+bullets.get(b).x, bullet.y+bullets.get(b).y, p.x, p.y, game.getBullets()[b].size);

                    if (hit==1 && Constants.ON_DEBUG) {
                        debug.draw(new CustomData.Rect(new Vec2Float(p.x-Constants.UNIT_W2, p.y), new Vec2Float(Constants.UNIT_W, Constants.UNIT_H), new ColorFloat(1,0,0,0.25f)));
                        debug.draw(new CustomData.Rect(new Vec2Float(bullet.x-0.05f, bullet.y - 0.05f), new Vec2Float(0.1f, 0.1f), new ColorFloat(0,0,1,0.25f)));
                    }
                    hits[i]+=hit*game.getBullets()[b].damage;

                    if(hit==1 && game.getBullets()[b].explosionParams != null) {
                        hits[i]+=hit*game.getBullets()[b].explosionParams.getDamage();
                    }

                    //bullet.x = game.getBullets()[b].position.x + bullets.get(b).x * tick_f;
                    //bullet.y = game.getBullets()[b].position.y + bullets.get(b).y * tick_f;

                    bullet.x+=bullets.get(b).x;
                    bullet.y+=bullets.get(b).y;

                    if (Constants.ON_DEBUG)
                        debug.draw(new CustomData.Rect(new Vec2Float(bullet.x-game.getBullets()[b].size/2f, bullet.y - game.getBullets()[b].size/2f), new Vec2Float(game.getBullets()[b].size, game.getBullets()[b].size), new ColorFloat(0,0,1,0.5f)));

                    //проверить что пуля в стене
                    //может на тик раньше быть в стене и взрыв
                    //if (World.map[(int)bullet.y][(int)bullet.x] == 1) {
                    if (World.map[(int)(bullet.y-game.getBullets()[b].size/2f)][(int)(bullet.x-game.getBullets()[b].size/2f)] == 1
                            || World.map[(int)(bullet.y-game.getBullets()[b].size/2f)][(int)(bullet.x+game.getBullets()[b].size/2f)] == 1
                            || World.map[(int)(bullet.y+game.getBullets()[b].size/2f)][(int)(bullet.x-game.getBullets()[b].size/2f)] == 1
                            || World.map[(int)(bullet.y+game.getBullets()[b].size/2f)][(int)(bullet.x+game.getBullets()[b].size/2f)] == 1) {
                        //если взрывная, то урон
                        if (game.getBullets()[b].explosionParams != null) {

                            hit = checkHit(bullet.x,
                                    bullet.y,
                                    u.x,
                                    u.y, game.getBullets()[b].explosionParams.getRadius()+game.getBullets()[b].size/2d);

                            hits[i]+=hit*game.getBullets()[b].explosionParams.getDamage();

                            if (hit==1 && Constants.ON_DEBUG) {
                                debug.draw(new CustomData.Rect(new Vec2Float(p.x-Constants.UNIT_W2, p.y), new Vec2Float(Constants.UNIT_W, Constants.UNIT_H), new ColorFloat(1,0,0,0.25f)));
                                debug.draw(new CustomData.Rect(new Vec2Float(bullet.x-0.05f, bullet.y - 0.05f), new Vec2Float(0.1f, 0.1f), new ColorFloat(0,0,1,0.25f)));
                            }
                        }

                        if (Constants.ON_DEBUG)
                            debug.draw(new CustomData.Rect(new Vec2Float(bullet.x-game.getBullets()[b].size/2f, bullet.y - game.getBullets()[b].size/2f), new Vec2Float(game.getBullets()[b].size, game.getBullets()[b].size), new ColorFloat(1,0,0,0.5f)));

                        break;
                    }

                    u.x=p.x;
                    u.y=p.y;

                    //tick_f++;
                }

            }

            if (Constants.ON_DEBUG)
                System.out.println("hit " + i + " " + hits[i]);

        }

        int minHits=1000;
        Vec2Double p = null;
        int minI = -1;

        for (int i=0;i<hits.length;i++) {

            if (p==null) {
                p=Sim_v3.steps[i][0];
                minHits=hits[i];
                minI=i;
            } else {
                if (minHits>hits[i]) {
                    p=Sim_v3.steps[i][0];
                    minHits=hits[i];
                    minI=i;
                }
            }

        }

        action.setVelocity((p.x - unit.position.x)*Constants.TICKS_PER_SECOND);
        /*if (p.y>unit.position.y)
            action.jump=true;

        if (p.y<unit.position.y)
            action.jumpDown=true;*/

        if (p.y-unit.position.y>Constants.UNIT_Y_SPEED_PER_TICK/2d)
            action.jump=true;

        if (p.y-unit.position.y<-Constants.UNIT_Y_SPEED_PER_TICK/2d)
            action.jumpDown=true;

        if (Constants.ON_DEBUG)
            debug.draw(new CustomData.Log("go to " + minI));

    }


    public static void dodge_v4(Unit unit, Game game, Debug debug, UnitAction action) {

        hits = new int[Sim_v3.steps.length];

        //float tick_f;
        int hit;
        Vec2Double bullet = new Vec2Double();
        Vec2Double u = new Vec2Double();

        //todo возможно проверять расстояние до центра юнита и если менее чего-то то уже точнее


        boolean neeedCheck=false;
        for (int i=0;i<hits.length;i++) {

            for (int b=0; b<bullets.size();b++) {

                neeedCheck=false;

                //выкинуть свои пули
                if (game.getBullets()[b].unitId ==  unit.id && game.getBullets()[b].explosionParams == null)
                    continue;

                //if (distanceSqr(game.getBullets()[b].position.x, game.getBullets()[b].position.y, unit.position.x, unit.position.y+Constants.UNIT_H2) >= 9) {
                //выкинуть пули которые уже точно не в нас, позиция и скорость не в нашу сторону
                if (game.getBullets()[b].position.x+4f > unit.position.x + Constants.UNIT_W2 && Dodge.bullets.get(b).x < 0)
                    neeedCheck = true;

                if (game.getBullets()[b].position.x-4f < unit.position.x - Constants.UNIT_W2 && Dodge.bullets.get(b).x > 0)
                    neeedCheck = true;

                if (game.getBullets()[b].position.y+4f > unit.position.y + Constants.UNIT_H && Dodge.bullets.get(b).y < 0)
                    neeedCheck = true;

                if (game.getBullets()[b].position.y-4f < unit.position.y && Dodge.bullets.get(b).y > 0)
                    neeedCheck = true;
                //}

                if (!neeedCheck)
                    continue;

                //tick_f=1f;

                bullet.x = game.getBullets()[b].position.x;
                bullet.y = game.getBullets()[b].position.y;

                u.x = unit.position.x;
                u.y = unit.position.y;

                for (Vec2Double p:Sim_v3.steps[i]) {

                    hit = checkHit(bullet.x,
                            bullet.y,
                            u.x,
                            u.y, game.getBullets()[b].size/2d*1.1d);

                    if (hit == 0)
                        hit = checkHit(bullet.x+bullets.get(b).x,
                                bullet.y+bullets.get(b).y,
                                u.x,
                                u.y, game.getBullets()[b].size/2d*1.1d);

                    if (hit == 0)
                        hit = checkHit(bullet.x+bullets.get(b).x,
                                bullet.y+bullets.get(b).y,
                                p.x,
                                p.y, game.getBullets()[b].size/2d*1.1d);


                    if (hit == 0)
                        hit=checkHitFlow(bullet.x, bullet.y, u.x, u.y,
                                bullet.x+bullets.get(b).x, bullet.y+bullets.get(b).y, p.x, p.y, game.getBullets()[b].size);

                    if (hit==1 && Constants.ON_DEBUG) {
                        debug.draw(new CustomData.Rect(new Vec2Float(p.x-Constants.UNIT_W2, p.y), new Vec2Float(Constants.UNIT_W, Constants.UNIT_H), new ColorFloat(1,0,0,0.25f)));
                        debug.draw(new CustomData.Rect(new Vec2Float(bullet.x-0.05f, bullet.y - 0.05f), new Vec2Float(0.1f, 0.1f), new ColorFloat(0,0,1,0.25f)));
                    }
                    hits[i]+=hit*game.getBullets()[b].damage;

                    if(hit==1 && game.getBullets()[b].explosionParams != null) {
                        hits[i]+=hit*game.getBullets()[b].explosionParams.getDamage();
                    }

                    //bullet.x = game.getBullets()[b].position.x + bullets.get(b).x * tick_f;
                    //bullet.y = game.getBullets()[b].position.y + bullets.get(b).y * tick_f;

                    bullet.x+=bullets.get(b).x;
                    bullet.y+=bullets.get(b).y;

                    if (Constants.ON_DEBUG)
                        debug.draw(new CustomData.Rect(new Vec2Float(bullet.x-game.getBullets()[b].size/2f, bullet.y - game.getBullets()[b].size/2f), new Vec2Float(game.getBullets()[b].size, game.getBullets()[b].size), new ColorFloat(0,0,1,0.5f)));

                    //проверить что пуля в стене
                    //может на тик раньше быть в стене и взрыв
                    //if (World.map[(int)bullet.y][(int)bullet.x] == 1) {
                    if (World.map[(int)(bullet.y-game.getBullets()[b].size/2f)][(int)(bullet.x-game.getBullets()[b].size/2f)] == 1
                            || World.map[(int)(bullet.y-game.getBullets()[b].size/2f)][(int)(bullet.x+game.getBullets()[b].size/2f)] == 1
                            || World.map[(int)(bullet.y+game.getBullets()[b].size/2f)][(int)(bullet.x-game.getBullets()[b].size/2f)] == 1
                            || World.map[(int)(bullet.y+game.getBullets()[b].size/2f)][(int)(bullet.x+game.getBullets()[b].size/2f)] == 1) {
                        //если взрывная, то урон
                        if (game.getBullets()[b].explosionParams != null) {

                            //todo смотреть глубину в стене и отнимать и от нее центр взрыва
                            /*hit = checkHit(bullet.x,
                                    bullet.y,
                                    u.x,
                                    u.y, game.getBullets()[b].explosionParams.getRadius()+game.getBullets()[b].size/2d);*/
                            hit = checkHit(bullet.x,
                                    bullet.y,
                                    p.x,
                                    p.y, game.getBullets()[b].explosionParams.getRadius()+game.getBullets()[b].size/2d);

                            hits[i]+=hit*game.getBullets()[b].explosionParams.getDamage();

                            if (hit==1 && Constants.ON_DEBUG) {
                                debug.draw(new CustomData.Rect(new Vec2Float(p.x-Constants.UNIT_W2, p.y), new Vec2Float(Constants.UNIT_W, Constants.UNIT_H), new ColorFloat(1,0,0,0.25f)));
                                debug.draw(new CustomData.Rect(new Vec2Float(bullet.x-0.05f, bullet.y - 0.05f), new Vec2Float(0.1f, 0.1f), new ColorFloat(0,0,1,0.25f)));
                            }
                        }

                        if (Constants.ON_DEBUG)
                            debug.draw(new CustomData.Rect(new Vec2Float(bullet.x-game.getBullets()[b].size/2f, bullet.y - game.getBullets()[b].size/2f), new Vec2Float(game.getBullets()[b].size, game.getBullets()[b].size), new ColorFloat(1,0,0,0.5f)));

                        break;
                    }

                    u.x=p.x;
                    u.y=p.y;

                    //tick_f++;
                }

            }

            if (Constants.ON_DEBUG)
                System.out.println("hit " + i + " " + hits[i]);

        }

    }

}
