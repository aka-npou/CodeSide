package myModel;

import model.Vec2Float;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by aka_npou on 22.07.2019.
 */
public class Li {

    static Vec2Int[][] vectors;

    public static void setVectors() {

        vectors = new Vec2Int[World.y][World.x];

        for (int y=0;y<World.y;y++) {
            for (int x=0;x<World.x;x++) {
                vectors[y][x]=new Vec2Int(x, y);
            }
        }

    }
    public static void getLiMap(int[][] map, Vec2Int p, int[][]liMap) {

        //int[][] liMap = new int[World.y][World.x];

        for (int y=0;y<World.y;y++) {
            for (int x=0;x<World.x;x++) {
                liMap[y][x]=0;
            }
        }

        //LinkedList<Vec2Int> q = new LinkedList();
        ArrayDeque<Vec2Int> q = new ArrayDeque<>();

        Vec2Int cp;
        //q.add(new Point(Point.getCell(position.x), Point.getCell(position.y)));

        liMap[p.y][p.x] = -1;
        liMap[p.y][p.x] = 1;

        //Point p = new Point(Point.getCell(position.x), Point.getCell(position.y));

        if (p.x-1>=0) {
            setValue(liMap, map, p, q, -1, 0);
        }

        if (p.x+1<World.x) {
            setValue(liMap, map, p, q, 1, 0);
        }

        if (p.y-1>=0) {
            setValue(liMap, map, p, q, 0, -1);
        }

        if (p.y+1<World.y) {
            setValue(liMap, map, p, q, 0, 1);
        }

        while(true) {
            if (q.size()==0)
                break;
            cp = q.pop();

            if (cp.x-1>=0) {
                setValue(liMap, map, cp, q, -1, 0);
            }

            if (cp.x+1<World.x) {
                setValue(liMap, map, cp, q, 1, 0);
            }

            if (cp.y-1>=0) {
                setValue(liMap, map, cp, q, 0, -1);
            }

            if (cp.y+1<World.y) {
                setValue(liMap, map, cp, q, 0, 1);
            }
        }

        //return liMap;
    }


    private static void setValue(int[][] liMap, int[][] map, Vec2Int cp, ArrayDeque<Vec2Int> q, int dx, int dy) {
        if (map[cp.y+dy][cp.x+dx]==1){
            if (liMap[cp.y+dy][cp.x+dx]==0) {
                //liMap[cp.y+dy][cp.x+dx] = liMap[cp.y][cp.x]==-1?1:liMap[cp.y][cp.x]+1;
                liMap[cp.y+dy][cp.x+dx] = liMap[cp.y][cp.x]+1;
                //не помню почему убрал свою территорию, вернем прохождение по ней, а то противники не считают расстояние до меня по своей территории
                //if (map[cp.y+dy][cp.x+dx]!=Game.CELL_MY_TERRITORY || map[cp.y][cp.x]==Game.CELL_MY_TERRITORY)
                    //q.add(new Vec2Int(cp.x+dx, cp.y+dy));
                    q.add(vectors[cp.y+dy][cp.x+dx]);
            } else {
                //int a = liMap[cp.y][cp.x]==-1?1:liMap[cp.y][cp.x]+1;
                if (liMap[cp.y+dy][cp.x+dx]>liMap[cp.y][cp.x]+1) {
                    liMap[cp.y+dy][cp.x+dx]=liMap[cp.y][cp.x]+1;
                }
            }
        }
    }
}
