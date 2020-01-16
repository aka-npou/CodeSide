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
    public static int[] _hits;
    public static int[] futureHits;
    public static int[] hitsTicks;
    public static Bullet[] bulletsP;
    public static ArrayList<Vec2Double> bulletsV;

    public static void setBullets(Unit unit, Game game, Debug debug) {

        bullets = new ArrayList<>();

        for (Bullet b:game.getBullets()) {

            //System.out.println("b x=" + b.position.x + " y=" + b.position.y + " vx=" + b.velocity.x + " vy=" + b.velocity.y);

            Vec2Double bpf = new Vec2Double(b.velocity.x/game.getProperties().getTicksPerSecond(), b.velocity.y/game.getProperties().getTicksPerSecond());

            //System.out.println("bpf " + bpf.x + " " + bpf.y);

            bullets.add(bpf);

        }

    }

    public static void getFutureBullets(Game game,Unit unit, Debug debug) {


        futureHits = new int[Sim_v3.steps.length];

        bulletsV = new ArrayList<>();

        /*int s=0;
        for (Unit u:game.getUnits()) {
            if (u.playerId == unit.playerId)
                continue;

            if (u.weapon == null)
                continue;

            s++;

        }*/

        dodge_v4(unit, game, debug, false, bullets, game.getBullets(), 0, Sim_v3.ticks);

        //bulletsP = new Bullet[s];
        //проверяем по одной, если попали сюда то есть враг
        bulletsP = new Bullet[1];

        /*Vec2Double bpf;
        for (int i=0;i<game.getBullets().length;i++) {
            bulletsP[i]=game.getBullets()[i];
            bpf = new Vec2Double(bulletsP[i].velocity.x/game.getProperties().getTicksPerSecond(), bulletsP[i].velocity.y/game.getProperties().getTicksPerSecond());
            bulletsV.add(bpf);
        }*/

        bulletsV.add(new Vec2Double());
        //if (s==0)
        //    return;

        //int i=0;

        //todo надо смотреть с определенных точек и прицелы туда. но и смотреть чтобы можно было оттуда стрелять
        for (Unit u:game.getUnits()) {
            if (u.playerId==unit.playerId)
                continue;

            if (u.weapon==null)
                continue;

            Vec2Double t = new Vec2Double(unit.position.x-u.position.x, unit.position.y-u.position.y);

            int dt = (int)(u.weapon.fireTimer==null?0:u.weapon.fireTimer/Constants.TICK);

            double a = Math.atan2(t.y, t.x);

            checkFuture(game,unit, u, debug, a, dt);

            a+=u.weapon.spread/2d;

            checkFuture(game,unit, u, debug, a, dt);

            a+=u.weapon.spread/2d;

            checkFuture(game,unit, u, debug, a, dt);

            a-=u.weapon.spread*1.5d;

            checkFuture(game,unit, u, debug, a, dt);

            a-=u.weapon.spread/2d;

            checkFuture(game,unit, u, debug, a, dt);
            
        }

        for (int q=0;q<hits.length;q++) {
            futureHits[q]+=hits[q];
        }

        System.out.println("FH------------------------");
        for (int q=0;q<futureHits.length;q++) {
            System.out.println("i="+q+" "+futureHits[q]);
        }

    }

    static void checkFuture(Game game,Unit unit, Unit u, Debug debug, double a, int dt) {

        Bullet bpf;
        Vec2Double v;

        bpf = new Bullet();

        bpf.weaponType = u.weapon.typ;
        bpf.unitId = u.id;
        bpf.playerId = u.playerId;
        bpf.position = new Vec2Double(u.position.x, u.position.y+Constants.UNIT_H2);

        v = new Vec2Double(u.weapon.params.getBullet().speed*Math.cos(a), u.weapon.params.getBullet().speed*Math.sin(a));

        if(Constants.ON_DEBUG)
            debug.draw(new CustomData.Line(new Vec2Float(u.position.x, u.position.y+Constants.UNIT_H2), new Vec2Float(u.position.x+v.x*100, u.position.y+Constants.UNIT_H2+v.y*100), 0.1f, new ColorFloat(1,0,1,1)));

        bpf.velocity = v;

        bpf.damage = u.weapon.params.getBullet().getDamage();
        bpf.size = u.weapon.params.getBullet().size;
        bpf.explosionParams = game.getProperties().getWeaponParams().get(u.weapon.typ).getExplosion();

        bulletsP[bulletsP.length-1]=bpf;

        bulletsV.get(bulletsV.size()-1).x = bulletsP[bulletsP.length-1].velocity.x/game.getProperties().getTicksPerSecond();
        bulletsV.get(bulletsV.size()-1).y = bulletsP[bulletsP.length-1].velocity.y/game.getProperties().getTicksPerSecond();

        dodge_v4(unit, game, debug, true, bulletsV, bulletsP, dt, 1);

    }

    public static void dodge(Unit unit, Game game, Debug debug, boolean isFuture, ArrayList<Vec2Double> bulletsV, Bullet[] bulletsP) {

        //dodge_v1(unit, game, debug, action);
        //dodge_v2(unit, game, debug, action);
        //dodge_v3(unit, game, debug, action);
        dodge_v4(unit, game, debug, isFuture, bulletsV, bulletsP, 0, Sim_v3.ticks);

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

        double b=0.0045;
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

        if (checkHitBoxes(ux1-Constants.UNIT_W2, uy1,bx1+sizeB/2d, by1+sizeB/2d, bx2+sizeB/2d, by2+sizeB/2d)==1)
            return 1;
        if (checkHitBoxes(ux1+Constants.UNIT_W2, uy1,bx1-sizeB/2d, by1+sizeB/2d, bx2-sizeB/2d, by2+sizeB/2d)==1)
            return 1;
        if (checkHitBoxes(ux1-Constants.UNIT_W2, uy1+Constants.UNIT_H,bx1+sizeB/2d, by1-sizeB/2d, bx2+sizeB/2d, by2-sizeB/2d)==1)
            return 1;
        if (checkHitBoxes(ux1+Constants.UNIT_W2, uy1+Constants.UNIT_H,bx1-sizeB/2d, by1-sizeB/2d, bx2-sizeB/2d, by2-sizeB/2d)==1)
            return 1;

        if (checkHitBoxes(ux1-Constants.UNIT_W2, uy2,bx1+sizeB/2d, by1+sizeB/2d, bx2+sizeB/2d, by2+sizeB/2d)==1)
            return 1;
        if (checkHitBoxes(ux1+Constants.UNIT_W2, uy2,bx1-sizeB/2d, by1+sizeB/2d, bx2-sizeB/2d, by2+sizeB/2d)==1)
            return 1;
        if (checkHitBoxes(ux1-Constants.UNIT_W2, uy2+Constants.UNIT_H,bx1+sizeB/2d, by1-sizeB/2d, bx2+sizeB/2d, by2-sizeB/2d)==1)
            return 1;
        if (checkHitBoxes(ux1+Constants.UNIT_W2, uy2+Constants.UNIT_H,bx1-sizeB/2d, by1-sizeB/2d, bx2-sizeB/2d, by2-sizeB/2d)==1)
            return 1;

        if (checkHitBoxes(ux2-Constants.UNIT_W2, uy1,bx1+sizeB/2d, by1+sizeB/2d, bx2+sizeB/2d, by2+sizeB/2d)==1)
            return 1;
        if (checkHitBoxes(ux2+Constants.UNIT_W2, uy1,bx1-sizeB/2d, by1+sizeB/2d, bx2-sizeB/2d, by2+sizeB/2d)==1)
            return 1;
        if (checkHitBoxes(ux2-Constants.UNIT_W2, uy1+Constants.UNIT_H,bx1+sizeB/2d, by1-sizeB/2d, bx2+sizeB/2d, by2-sizeB/2d)==1)
            return 1;
        if (checkHitBoxes(ux2+Constants.UNIT_W2, uy1+Constants.UNIT_H,bx1-sizeB/2d, by1-sizeB/2d, bx2-sizeB/2d, by2-sizeB/2d)==1)
            return 1;

        if (checkHitBoxes(ux2-Constants.UNIT_W2, uy2,bx1+sizeB/2d, by1+sizeB/2d, bx2+sizeB/2d, by2+sizeB/2d)==1)
            return 1;
        if (checkHitBoxes(ux2+Constants.UNIT_W2, uy2,bx1-sizeB/2d, by1+sizeB/2d, bx2-sizeB/2d, by2+sizeB/2d)==1)
            return 1;
        if (checkHitBoxes(ux2-Constants.UNIT_W2, uy2+Constants.UNIT_H,bx1+sizeB/2d, by1-sizeB/2d, bx2+sizeB/2d, by2-sizeB/2d)==1)
            return 1;
        if (checkHitBoxes(ux2+Constants.UNIT_W2, uy2+Constants.UNIT_H,bx1-sizeB/2d, by1-sizeB/2d, bx2-sizeB/2d, by2-sizeB/2d)==1)
            return 1;

//        //
//        if (checkHitBoxes(bx1+sizeB/2d, by1+sizeB/2d,ux1-Constants.UNIT_W2, uy1, ux2-Constants.UNIT_W2, uy2)==1)
//            return 1;
//
//        if (checkHitBoxes(bx1-sizeB/2d, by1+sizeB/2d,ux1+Constants.UNIT_W2, uy1, ux2+Constants.UNIT_W2, uy2)==1)
//            return 1;
//
//        if (checkHitBoxes(bx1+sizeB/2d, by1-sizeB/2d,ux1-Constants.UNIT_W2, uy1+Constants.UNIT_H, ux2-Constants.UNIT_W2, uy2+Constants.UNIT_H)==1)
//            return 1;
//
//        if (checkHitBoxes(bx1-sizeB/2d, by1-sizeB/2d,ux1+Constants.UNIT_W2, uy1+Constants.UNIT_H, ux2+Constants.UNIT_W2, uy2+Constants.UNIT_H)==1)
//            return 1;
//
//        //
//        if (checkHitBoxes(bx1+sizeB/2d, by2+sizeB/2d,ux1-Constants.UNIT_W2, uy1, ux2-Constants.UNIT_W2, uy2)==1)
//            return 1;
//
//        if (checkHitBoxes(bx1-sizeB/2d, by2+sizeB/2d,ux1+Constants.UNIT_W2, uy1, ux2+Constants.UNIT_W2, uy2)==1)
//            return 1;
//
//        if (checkHitBoxes(bx1+sizeB/2d, by2-sizeB/2d,ux1-Constants.UNIT_W2, uy1+Constants.UNIT_H, ux2-Constants.UNIT_W2, uy2+Constants.UNIT_H)==1)
//            return 1;
//
//        if (checkHitBoxes(bx1-sizeB/2d, by2-sizeB/2d,ux1+Constants.UNIT_W2, uy1+Constants.UNIT_H, ux2+Constants.UNIT_W2, uy2+Constants.UNIT_H)==1)
//            return 1;
//
//        //
//        if (checkHitBoxes(bx2+sizeB/2d, by1+sizeB/2d,ux1-Constants.UNIT_W2, uy1, ux2-Constants.UNIT_W2, uy2)==1)
//            return 1;
//
//        if (checkHitBoxes(bx2-sizeB/2d, by1+sizeB/2d,ux1+Constants.UNIT_W2, uy1, ux2+Constants.UNIT_W2, uy2)==1)
//            return 1;
//
//        if (checkHitBoxes(bx2+sizeB/2d, by1-sizeB/2d,ux1-Constants.UNIT_W2, uy1+Constants.UNIT_H, ux2-Constants.UNIT_W2, uy2+Constants.UNIT_H)==1)
//            return 1;
//
//        if (checkHitBoxes(bx2-sizeB/2d, by1-sizeB/2d,ux1+Constants.UNIT_W2, uy1+Constants.UNIT_H, ux2+Constants.UNIT_W2, uy2+Constants.UNIT_H)==1)
//            return 1;
//
//        //
//        if (checkHitBoxes(bx2+sizeB/2d, by2+sizeB/2d,ux1-Constants.UNIT_W2, uy1, ux2-Constants.UNIT_W2, uy2)==1)
//            return 1;
//
//        if (checkHitBoxes(bx2-sizeB/2d, by2+sizeB/2d,ux1+Constants.UNIT_W2, uy1, ux2+Constants.UNIT_W2, uy2)==1)
//            return 1;
//
//        if (checkHitBoxes(bx2+sizeB/2d, by2-sizeB/2d,ux1-Constants.UNIT_W2, uy1+Constants.UNIT_H, ux2-Constants.UNIT_W2, uy2+Constants.UNIT_H)==1)
//            return 1;
//
//        if (checkHitBoxes(bx2-sizeB/2d, by2-sizeB/2d,ux1+Constants.UNIT_W2, uy1+Constants.UNIT_H, ux2+Constants.UNIT_W2, uy2+Constants.UNIT_H)==1)
//            return 1;

        //todo для стоячего надо в обратную сторону проверять


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


    public static void dodge_v4(Unit unit, Game game, Debug debug, boolean isFuture, ArrayList<Vec2Double> bulletsV, Bullet[] bulletsP, int dt, int to) {

        if (isFuture) {
            //futureHits = new int[Sim_v3.steps.length];
            _hits=futureHits;
        } else {
            hits = new int[Sim_v3.steps.length];
            _hits=hits;
        }


        hitsTicks = new int[Sim_v3.steps.length];


        //todo возможно проверять расстояние до центра юнита и если менее чего-то то уже точнее

        //todo можно еще смотреть какой урон будет вокруг всем при ракете, может надо и лицом поймать

        for (int i=0;i<_hits.length;i++) {

            checkBullets(unit, game, debug, isFuture, bulletsV, bulletsP, dt, to, i);
//            if (Constants.ON_DEBUG)
//                System.out.println("hit " + i + " " + _hits[i]);

        }

    }

    private static void checkBullets(Unit unit, Game game, Debug debug, boolean isFuture, ArrayList<Vec2Double> bulletsV, Bullet[] bulletsP, int dt, int to, int i) {

        int hit;
        Vec2Double bullet = new Vec2Double();
        Vec2Double u = new Vec2Double();

        boolean neeedCheck=false;
        int tick;
        Vec2Double p;

        for (int b=0; b<bulletsV.size();b++) {

            neeedCheck=false;

            //выкинуть свои пули
            if (bulletsP[b].unitId == unit.id && bulletsP[b].explosionParams == null)
                continue;

            //if (distanceSqr(bulletsP[b].position.x, bulletsP[b].position.y, unit.position.x, unit.position.y+Constants.UNIT_H2) >= 9) {
            //выкинуть пули которые уже точно не в нас, позиция и скорость не в нашу сторону
            if (bulletsP[b].position.x+4f > unit.position.x + Constants.UNIT_W2 && bulletsV.get(b).x < 0)
                neeedCheck = true;

            if (bulletsP[b].position.x-4f < unit.position.x - Constants.UNIT_W2 && bulletsV.get(b).x > 0)
                neeedCheck = true;

            if (bulletsP[b].position.y+4f > unit.position.y + Constants.UNIT_H && bulletsV.get(b).y < 0)
                neeedCheck = true;

            if (bulletsP[b].position.y-4f < unit.position.y && bulletsV.get(b).y > 0)
                neeedCheck = true;
            //}

            if (!neeedCheck)
                continue;

            //tick_f=1f;

            bullet.x = bulletsP[b].position.x;
            bullet.y = bulletsP[b].position.y;

            u.x = unit.position.x;
            u.y = unit.position.y;

            tick=1;
            //for (Vec2Double p:Sim_v3.steps[i]) {
            for (int j=dt; j<to;j++) {
                p=Sim_v3.steps[i][j];

                hit = checkHit(bullet.x,
                        bullet.y,
                        u.x,
                        u.y, bulletsP[b].size/2d*1.1d);

                if (hit == 0)
                    hit = checkHit(bullet.x+bulletsV.get(b).x,
                            bullet.y+bulletsV.get(b).y,
                            u.x,
                            u.y, bulletsP[b].size/2d*1.1d);

                if (hit == 0)
                    hit = checkHit(bullet.x+bulletsV.get(b).x,
                            bullet.y+bulletsV.get(b).y,
                            p.x,
                            p.y, bulletsP[b].size/2d*1.1d);


                if (hit == 0)
                    hit=checkHitFlow(bullet.x, bullet.y, u.x, u.y,
                            bullet.x+bulletsV.get(b).x, bullet.y+bulletsV.get(b).y, p.x, p.y, bulletsP[b].size);

                if (hit==1 && Constants.ON_DEBUG) {
                    debug.draw(new CustomData.Rect(new Vec2Float(p.x-Constants.UNIT_W2, p.y), new Vec2Float(Constants.UNIT_W, Constants.UNIT_H), new ColorFloat(1,0,0,0.25f)));
                    debug.draw(new CustomData.Rect(new Vec2Float(bullet.x-0.05f, bullet.y - 0.05f), new Vec2Float(0.1f, 0.1f), new ColorFloat(0,0,1,0.25f)));
                }
                _hits[i]+=hit*bulletsP[b].damage*(isFuture?1:10);

                if(hit==1 && bulletsP[b].explosionParams != null) {
                    _hits[i]+=hit*bulletsP[b].explosionParams.getDamage()*(isFuture?1:10);
                }

                //bullet.x = game.getBullets()[b].position.x + bulletsV.get(b).x * tick_f;
                //bullet.y = game.getBullets()[b].position.y + bulletsV.get(b).y * tick_f;

                bullet.x+=bulletsV.get(b).x;
                bullet.y+=bulletsV.get(b).y;

                if (Constants.ON_DEBUG)
                    debug.draw(new CustomData.Rect(new Vec2Float(bullet.x-bulletsP[b].size/2f, bullet.y - bulletsP[b].size/2f), new Vec2Float(bulletsP[b].size, bulletsP[b].size), new ColorFloat(0,0,1,0.5f)));

                //todo возмоно при попадании дальше не смотреть
                if (hit==1) {
                    if (hitsTicks[i]>tick)
                        hitsTicks[i]=tick;

                    break;
                }

                //проверить что пуля в стене
                //может на тик раньше быть в стене и взрыв
                //if (World.map[(int)bullet.y][(int)bullet.x] == 1) {
                if (World.map[(int)(bullet.y-bulletsP[b].size/2f)][(int)(bullet.x-bulletsP[b].size/2f)] == 1
                        || World.map[(int)(bullet.y-bulletsP[b].size/2f)][(int)(bullet.x+bulletsP[b].size/2f)] == 1
                        || World.map[(int)(bullet.y+bulletsP[b].size/2f)][(int)(bullet.x-bulletsP[b].size/2f)] == 1
                        || World.map[(int)(bullet.y+bulletsP[b].size/2f)][(int)(bullet.x+bulletsP[b].size/2f)] == 1) {
                    //если взрывная, то урон
                    if (bulletsP[b].explosionParams != null) {

                        //todo смотреть глубину в стене и отнимать и от нее центр взрыва
                        //смотрим текущие и следующее положение может быть в промежутке взрыв
                        hit = checkHit(bullet.x,
                                bullet.y,
                                u.x,
                                u.y, bulletsP[b].explosionParams.getRadius()+bulletsP[b].size/2d);

                        if (hit==0)
                            hit = checkHit(bullet.x,
                                    bullet.y,
                                    p.x,
                                    p.y, bulletsP[b].explosionParams.getRadius()+bulletsP[b].size/2d);

                        _hits[i]+=hit*bulletsP[b].explosionParams.getDamage()*(isFuture?1:10);

                        if (hit==1 && Constants.ON_DEBUG) {
                            debug.draw(new CustomData.Rect(new Vec2Float(p.x-Constants.UNIT_W2, p.y), new Vec2Float(Constants.UNIT_W, Constants.UNIT_H), new ColorFloat(1,0,0,0.25f)));
                            debug.draw(new CustomData.Rect(new Vec2Float(bullet.x-0.05f, bullet.y - 0.05f), new Vec2Float(0.1f, 0.1f), new ColorFloat(0,0,1,0.25f)));
                        }
                    }

                    if (Constants.ON_DEBUG)
                        debug.draw(new CustomData.Rect(new Vec2Float(bullet.x-bulletsP[b].size/2f, bullet.y - bulletsP[b].size/2f), new Vec2Float(bulletsP[b].size, bulletsP[b].size), new ColorFloat(1,0,0,0.5f)));

                    break;
                }

                u.x=p.x;
                u.y=p.y;

                //tick_f++;
                tick++;
            }


        }

    }

}
