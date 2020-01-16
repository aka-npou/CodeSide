package model;

import util.StreamUtil;

public class BulletF {
    public WeaponType weaponType;
    //public WeaponType getWeaponType() { return weaponType; }
    //public void setWeaponType(WeaponType weaponType) { this.weaponType = weaponType; }
    public int unitId;
    //public int getUnitId() { return unitId; }
    //public void setUnitId(int unitId) { this.unitId = unitId; }
    public int playerId;
    //public int getPlayerId() { return playerId; }
    //public void setPlayerId(int playerId) { this.playerId = playerId; }
    public Vec2Float position;
    //public Vec2Float getPosition() { return position; }
    //public void setPosition(Vec2Double position) { this.position = position; }
    public Vec2Float velocity;
    //public Vec2Float getVelocity() { return velocity; }
    //public void setVelocity(Vec2Double velocity) { this.velocity = velocity; }
    public int damage;
    //public int getDamage() { return damage; }
    //public void setDamage(int damage) { this.damage = damage; }
    public float size;
    //public double getSize() { return size; }
    //public void setSize(double size) { this.size = size; }
    public ExplosionParams explosionParams;
    //public ExplosionParams getExplosionParams() { return explosionParams; }
    //public void setExplosionParams(ExplosionParams explosionParams) { this.explosionParams = explosionParams; }
    public BulletF() {}
    /*public BulletF(WeaponType weaponType, int unitId, int playerId, Vec2Double position, Vec2Double velocity, int damage, double size, ExplosionParams explosionParams) {
        this.weaponType = weaponType;
        this.unitId = unitId;
        this.playerId = playerId;
        this.position = position;
        this.velocity = velocity;
        this.damage = damage;
        this.size = size;
        this.explosionParams = explosionParams;
    }*/
    public static BulletF readFrom(java.io.InputStream stream) throws java.io.IOException {
        BulletF result = new BulletF();
        switch (StreamUtil.readInt(stream)) {
        case 0:
            result.weaponType = WeaponType.PISTOL;
            break;
        case 1:
            result.weaponType = WeaponType.ASSAULT_RIFLE;
            break;
        case 2:
            result.weaponType = WeaponType.ROCKET_LAUNCHER;
            break;
        default:
            throw new java.io.IOException("Unexpected discriminant value");
        }
        result.unitId = StreamUtil.readInt(stream);
        result.playerId = StreamUtil.readInt(stream);
        result.position = Vec2Double.readFromF(stream);
        result.velocity = Vec2Double.readFromF(stream);
        result.damage = StreamUtil.readInt(stream);
        result.size = (float)StreamUtil.readDouble(stream);
        if (StreamUtil.readBoolean(stream)) {
            result.explosionParams = ExplosionParams.readFrom(stream);
        } else {
            result.explosionParams = null;
        }
        return result;
    }
    public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
        StreamUtil.writeInt(stream, weaponType.discriminant);
        StreamUtil.writeInt(stream, unitId);
        StreamUtil.writeInt(stream, playerId);
        position.writeTo(stream);
        velocity.writeTo(stream);
        StreamUtil.writeInt(stream, damage);
        StreamUtil.writeDouble(stream, size);
        if (explosionParams == null) {
            StreamUtil.writeBoolean(stream, false);
        } else {
            StreamUtil.writeBoolean(stream, true);
            explosionParams.writeTo(stream);
        }
    }
}
