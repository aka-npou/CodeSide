package model;

import util.StreamUtil;

public class ExplosionParams {
    private float radius;
    public float getRadius() { return radius; }
    public void setRadius(float radius) { this.radius = radius; }
    private int damage;
    public int getDamage() { return damage; }
    public void setDamage(int damage) { this.damage = damage; }
    public ExplosionParams() {}
    /*public ExplosionParams(double radius, int damage) {
        this.radius = radius;
        this.damage = damage;
    }*/
    public static ExplosionParams readFrom(java.io.InputStream stream) throws java.io.IOException {
        ExplosionParams result = new ExplosionParams();
        result.radius = (float)StreamUtil.readDouble(stream);
        result.damage = StreamUtil.readInt(stream);
        return result;
    }
    public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
        StreamUtil.writeDouble(stream, radius);
        StreamUtil.writeInt(stream, damage);
    }
}
