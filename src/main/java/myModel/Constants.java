package myModel;

import model.ColorFloat;

/**
 * Created by aka_npou on 30.11.2019.
 */
public class Constants {
    public static boolean ON_DEBUG = false;
    public static final boolean LOCAL = false;

    public static final double EPS = 0.000000001d;
    public static final int STOP_TICK = 91;
    public static final double UNIT_H = 1.8d;
    public static final double UNIT_H2 = 1.8d/2d;
    public static final double UNIT_W = 0.9d;
    public static final double UNIT_W2 = 0.9d/2d;

    public static final int TICKS_PER_SECOND = 60;
    public static final double UNIT_X_SPEED_PER_SECOND = 10d;
    public static final double UNIT_X_SPEED_PER_TICK = 10d/60d;
    public static final double UNIT_Y_SPEED_PER_SECOND = 10d;
    public static final double UNIT_Y_SPEED_PER_TICK = 10d/60d;
    public static final double UNIT_Y_SPEED_PER_TICK_JUMP_PAD = 20d/60d;

    public static final double BULLET_SPEED_PER_TICK = 20d/60d;

    public static final double JUMP_TIME = 0.55d;
    public static final double JUMP_TIME_PAD = 0.525d;

    public static final double TICK = 1d/60d;

    public static final double FIRST_FALL_TICK_SPEED_Y = 0.05d;

    public static final ColorFloat[] uc = new ColorFloat[6];

    static {
        uc[0] = new ColorFloat(1, 0.5f, 0, 0.2f);
        uc[1] = new ColorFloat(0, 0, 1, 0.2f);
        uc[2] = new ColorFloat(0, 1, 1, 0.2f);
        uc[3] = new ColorFloat(0, 1, 0, 0.2f);
        uc[4] = new ColorFloat(0.56f, 0, 1, 0.2f);
        uc[5] = new ColorFloat(1, 0, 1, 0.2f);
    }

    public static boolean is2x2=false;

    //todo дописать все константы из game и тд
}
