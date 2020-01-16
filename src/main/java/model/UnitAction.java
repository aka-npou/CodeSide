package model;

import util.StreamUtil;

public class UnitAction {
    public double velocity;
    public double getVelocity() { return velocity; }
    public void setVelocity(double velocity) { this.velocity = velocity; }
    public boolean jump;
    public boolean isJump() { return jump; }
    public void setJump(boolean jump) { this.jump = jump; }
    public boolean jumpDown;
    public boolean isJumpDown() { return jumpDown; }
    public void setJumpDown(boolean jumpDown) { this.jumpDown = jumpDown; }
    public model.Vec2Double aim;
    public model.Vec2Double getAim() { return aim; }
    public void setAim(model.Vec2Double aim) { this.aim = aim; }
    public boolean shoot;
    public boolean isShoot() { return shoot; }
    public void setShoot(boolean shoot) { this.shoot = shoot; }
    public boolean reload;
    public boolean isReload() { return reload; }
    public void setReload(boolean reload) { this.reload = reload; }
    private boolean swapWeapon;
    public boolean isSwapWeapon() { return swapWeapon; }
    public void setSwapWeapon(boolean swapWeapon) { this.swapWeapon = swapWeapon; }
    public boolean plantMine;
    public boolean isPlantMine() { return plantMine; }
    public void setPlantMine(boolean plantMine) { this.plantMine = plantMine; }
    public UnitAction() {}
    public UnitAction(double velocity, boolean jump, boolean jumpDown, model.Vec2Double aim, boolean shoot, boolean swapWeapon, boolean plantMine) {
        this.velocity = velocity;
        this.jump = jump;
        this.jumpDown = jumpDown;
        this.aim = aim;
        this.shoot = shoot;
        this.reload = reload;
        this.swapWeapon = swapWeapon;
        this.plantMine = plantMine;
    }
    public static UnitAction readFrom(java.io.InputStream stream) throws java.io.IOException {
        UnitAction result = new UnitAction();
        result.velocity = StreamUtil.readDouble(stream);
        result.jump = StreamUtil.readBoolean(stream);
        result.jumpDown = StreamUtil.readBoolean(stream);
        result.aim = model.Vec2Double.readFrom(stream);
        result.shoot = StreamUtil.readBoolean(stream);
        result.reload = StreamUtil.readBoolean(stream);
        result.swapWeapon = StreamUtil.readBoolean(stream);
        result.plantMine = StreamUtil.readBoolean(stream);
        return result;
    }
    public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
        StreamUtil.writeDouble(stream, velocity);
        StreamUtil.writeBoolean(stream, jump);
        StreamUtil.writeBoolean(stream, jumpDown);
        aim.writeTo(stream);
        StreamUtil.writeBoolean(stream, shoot);
        StreamUtil.writeBoolean(stream, reload);
        StreamUtil.writeBoolean(stream, swapWeapon);
        StreamUtil.writeBoolean(stream, plantMine);
    }
}
