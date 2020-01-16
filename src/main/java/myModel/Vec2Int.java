package myModel;

/**
 * Created by aka_npou on 27.11.2019.
 */
public class Vec2Int {
    public int x,y;

    public Vec2Int(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vec2Int(float x, float y) {
        this.x = (int)x;
        this.y = (int)y;
    }

    public Vec2Int(double x, double y) {
        this.x = (int)x;
        this.y = (int)y;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vec2Int vec2Int = (Vec2Int) o;

        if (x != vec2Int.x) return false;
        return y == vec2Int.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
