package model;

import util.StreamUtil;

public class Vec2Float {

    public static final Vec2Float ZERO = new Vec2Float(0,0);

    public float x;
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float y;
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public Vec2Float() {}
    public Vec2Float(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public Vec2Float(double x, double y) {
        this.x = (float) x;
        this.y = (float) y;
    }
    public static Vec2Float readFrom(java.io.InputStream stream) throws java.io.IOException {
        Vec2Float result = new Vec2Float();
        result.x = StreamUtil.readFloat(stream);
        result.y = StreamUtil.readFloat(stream);
        return result;
    }
    public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
        StreamUtil.writeFloat(stream, x);
        StreamUtil.writeFloat(stream, y);
    }

    public void normalize() {
        double length = Math.sqrt(x*x+y*y);
        x/=length;
        y/=length;
    }
}
