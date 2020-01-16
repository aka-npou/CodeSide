package myModel;

import model.*;

import java.util.LinkedList;

/**
 * Created by aka_npou on 27.11.2019.
 */
public class World {

    public static int[][] map;
    public static int[][] patencyMap;
    public static int[][] reachabilityMap;
    public int x;
    public int y;

    //EMPTY(0)
    //WALL(1)
    //PLATFORM(2)
    //LADDER(3)
    //JUMP_PAD(4)

    public void setMap(Level level) {
        y = level.getTiles()[0].length;
        x = level.getTiles().length;

        map = new int[y][x];

        for (int yy=0; yy<y; yy++) {
            for (int xx=0; xx<x; xx++) {

                map[yy][xx] = level.getTiles()[xx][yy].ordinal();

            }
        }

    }

    public void setPatencyMap(Level level, Vec2Double unit) {

        patencyMap = new int[y][x];

        Vec2Int position = new Vec2Int(unit.getX(), unit.getY());

        LinkedList<Vec2Int> q = new LinkedList();

        q.add(position);

        while(true) {
            if (q.size()==0)
                break;
            Vec2Int cp = q.pop();

            if (cp.x-1>=0) {
                setValue(patencyMap, map, cp, q, -1, 0);
            }

            if (cp.x+1<x) {
                setValue(patencyMap, map, cp, q, 1, 0);
            }

            if (cp.y-1>=0) {
                setValue(patencyMap, map, cp, q, 0, -1);
            }

            if (cp.y+1<y) {
                setValue(patencyMap, map, cp, q, 0, 1);
            }
        }

    }

    private static void setValue(int[][] patencyMap, int[][] map, Vec2Int cp, LinkedList<Vec2Int> q, int dx, int dy) {
        if (map[cp.y+dy][cp.x+dx]!=1){
            if (patencyMap[cp.y+dy][cp.x+dx]!=1) {
                patencyMap[cp.y + dy][cp.x + dx] = 1;
                q.add(new Vec2Int(cp.x + dx, cp.y + dy));
            }
        }
    }


    public void setReachabilityMap() {

        reachabilityMap = new int[y][x];

        for (int _y=0; _y<y; _y++) {
            for (int _x=0; _x<x; _x++) {

                if (patencyMap[_y][_x] == 0)
                    continue;

                if (map[_y][_x] == 0) {
                    if (_y>1) {
                        if (map[_y-1][_x] == 0)
                            fall(_x,_y);
                        else {
                            if (map[_y][_x] != 4){
                                jump(_x, _y);
                                go(_x, _y);
                            }
                        }
                    }
                }

                if (map[_y][_x] == 1)
                    continue;

                if (map[_y][_x] == 2) {
                    go(_x ,_y);
                    jump(_x,_y);
                    fall(_x,_y);
                }

                if (map[_y][_x] == 3) {
                    ladder(_x,_y);
                }

                if (map[_y][_x] == 4) {
                    trampline(_x,_y);
                }

            }
        }
    }


    private void jump(int px, int py) {

    }

    private void fall(int px, int py) {

        if (py>1) {
            if (map[py-1][px]==0 || map[py-1][px]==4)
                reachabilityMap[py-1][px]=1;

            if (px>1 && map[py-1][px-1]!=1)
                reachabilityMap[py-1][px-1]=1;

            if (px<x && map[py-1][px+1]!=1)
            reachabilityMap[py-1][px+1]=1;
        }
    }

    private void go(int px, int py) {
        if (py>1) {

            if (px>1 && map[py-1][px-1]!=0)
                reachabilityMap[py][px-1]=1;

            if (px<x && map[py-1][px+1]!=0)
                reachabilityMap[py][px+1]=1;

        }
    }

    private void trampline(int px, int py) {

    }

    private void ladder(int px, int py) {

    }



    public void print(int[][] _map, Debug debug) {
        if (!Constants.ON_DEBUG)
            return;

        for (int yy=0; yy<y; yy++) {
            for (int xx=0; xx<x; xx++) {

                System.out.print(_map[yy][xx]);

            }
            System.out.println("");
        }

        ColorFloat yes = new ColorFloat(0,255,0,0.5f);
        ColorFloat no = new ColorFloat(255,0,0,0.5f);
        for (int yy=0; yy<y; yy++) {
            for (int xx=0; xx<x; xx++) {

                debug.draw(new CustomData.Rect(new Vec2Float(xx+0.1f,yy+0.1f), new Vec2Float(0.8f,0.8f), _map[yy][xx]==1?yes:no));

            }
        }

    }
}


//pistol
//  aim = 1 = 1/60 = 0.01666 за тик
//  fire = 0.4 = 24 тика

//rifle
//  aim = 1.9 = 1.9/60 = 0.031666 за тик
//  fire = 0.1 = 6 тиков


//jump
//time = 0.55 = .55*60=33 тика
//speed = 10 = 10/60 = 0,1666 за тик
//высота 5.5 клеток

