package myModel;

import model.*;
import sims.Sim_v2;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by aka_npou on 28.11.2019.
 */
public class Dodge {

    public static ArrayList<Vec2Float> bullets;

    public static void setBullets(UnitF unit, Game game, Debug debug) {

        bullets = new ArrayList<>();

        for (BulletF b:game.getBullets()) {

            //System.out.println("b x=" + b.position.x + " y=" + b.position.y + " vx=" + b.velocity.x + " vy=" + b.velocity.y);

            Vec2Float bpf = new Vec2Float(b.velocity.x/game.getProperties().getTicksPerSecond(), b.velocity.y/game.getProperties().getTicksPerSecond());

            //System.out.println("bpf " + bpf.x + " " + bpf.y);

            bullets.add(bpf);

        }

    }

    public static void dodge(UnitF unit, Game game, Debug debug, UnitAction action) {

        //dodge_v1(unit, game, debug, action);
        //dodge_v2(unit, game, debug, action);
        dodge_v3(unit, game, debug, action);

    }

    private static int checkHit(float bx, float by, float ux, float uy, float size) {

        if (bx+size > ux-Constants.UNIT_W2 &&
                bx-size < ux+Constants.UNIT_W2 &&
                by+size > uy &&
                by-size < uy+Constants.UNIT_H)
            return 1;

        return 0;
    }

    //возможно надо перейти на проверку линии пули с юнитом
    //a c b d
    private static int checkHitFlow(float bx1, float by1, float ux1, float uy1,
                                    float bx2, float by2, float ux2, float uy2) {

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

    private static boolean intersect(float ax1, float ax2, float ay1, float ay2,
                                     float bx1, float by1, float bx2, float by2) {

        double v1,v2,v3,v4;

        v1=(bx2-bx1)*(ay1-by1)-(by2-by1)*(ax1-bx1);
        v2=(bx2-bx1)*(ay2-by1)-(by2-by1)*(ax2-bx1);
        v3=(ax2-ax1)*(by1-ay1)-(ay2-ay1)*(bx1-ax1);
        v4=(ax2-ax1)*(by2-ay1)-(ay2-ay1)*(bx2-ax1);

        return (v1*v2<0) && (v3*v4<0);
    }

    //todo хранить все координаты приблизительно в инт до 5 знака и считать пули один раз их скорости

    public static void dodge_v3(UnitF unit, Game game, Debug debug, UnitAction action) {

        int[] hits = new int[Sim_v2.steps.length];

        //float tick_f;
        int hit;
        Vec2Float bullet = new Vec2Float();
        Vec2Float u = new Vec2Float();

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
                    if (game.getBullets()[b].position.x+3.2f > unit.position.x + Constants.UNIT_W2 && Dodge.bullets.get(b).x < 0)
                        neeedCheck = true;

                    if (game.getBullets()[b].position.x-3.2f < unit.position.x - Constants.UNIT_W2 && Dodge.bullets.get(b).x > 0)
                        neeedCheck = true;

                    if (game.getBullets()[b].position.y+3.2f > unit.position.y + Constants.UNIT_H && Dodge.bullets.get(b).y < 0)
                        neeedCheck = true;

                    if (game.getBullets()[b].position.y-3.2f < unit.position.y && Dodge.bullets.get(b).y > 0)
                        neeedCheck = true;
                //}

                if (!neeedCheck)
                    continue;

                //tick_f=1f;

                bullet.x = game.getBullets()[b].position.x;
                bullet.y = game.getBullets()[b].position.y;

                u.x = unit.position.x;
                u.y = unit.position.y;

                for (Vec2Float p:Sim_v2.steps[i]) {

                    hit = checkHit(bullet.x,
                            bullet.y,
                            u.x,
                            u.y, game.getBullets()[b].size);

                    if (hit == 0)
                        hit = checkHit(bullet.x+bullets.get(b).x,
                                bullet.y+bullets.get(b).y,
                                u.x,
                                u.y, game.getBullets()[b].size);

                    if (hit == 0)
                        hit = checkHit(bullet.x+bullets.get(b).x,
                                bullet.y+bullets.get(b).y,
                                p.x,
                                p.y, game.getBullets()[b].size);


                    if (hit == 0)
                        hit=checkHitFlow(bullet.x, bullet.y, u.x, u.y,
                            bullet.x+bullets.get(b).x, bullet.y+bullets.get(b).y, p.x, p.y);

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
                        debug.draw(new CustomData.Rect(new Vec2Float(bullet.x-0.1f, bullet.y - 0.1f), new Vec2Float(0.2f, 0.2f), new ColorFloat(0,0,1,0.25f)));

                    //проверить что пуля в стене
                    //todo может на тик раньше быть в стене и взрыв
                    if (World.map[(int)bullet.y][(int)bullet.x] == 1) {
                        //todo если взрывная, то урон
                        if (game.getBullets()[b].explosionParams != null) {

                            hit = checkHit(bullet.x,
                                    bullet.y,
                                    u.x,
                                    u.y, game.getBullets()[b].explosionParams.getRadius()+game.getBullets()[b].size);

                            hits[i]+=hit*game.getBullets()[b].explosionParams.getDamage();

                            if (hit==1 && Constants.ON_DEBUG) {
                                debug.draw(new CustomData.Rect(new Vec2Float(p.x-Constants.UNIT_W2, p.y), new Vec2Float(Constants.UNIT_W, Constants.UNIT_H), new ColorFloat(1,0,0,0.25f)));
                                debug.draw(new CustomData.Rect(new Vec2Float(bullet.x-0.05f, bullet.y - 0.05f), new Vec2Float(0.1f, 0.1f), new ColorFloat(0,0,1,0.25f)));
                            }
                        }
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
        Vec2Float p = null;
        int minI = -1;

        for (int i=0;i<hits.length;i++) {

            if (p==null) {
                p=Sim_v2.steps[i][0];
                minHits=hits[i];
                minI=i;
            } else {
                if (minHits>hits[i]) {
                    p=Sim_v2.steps[i][0];
                    minHits=hits[i];
                    minI=i;
                }
            }

        }

        action.setVelocity((p.x - unit.position.x)*Constants.TICKS_PER_SECOND);
        if (p.y>unit.position.y)
            action.jump=true;

        if (p.y<unit.position.y)
            action.jumpDown=true;

        if (Constants.ON_DEBUG)
            debug.draw(new CustomData.Log("go to " + minI));

    }

}
