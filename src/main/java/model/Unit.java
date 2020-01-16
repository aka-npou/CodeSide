package model;

import util.StreamUtil;

public class Unit {
    public int playerId;
    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
    public int id;
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int health;
    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
    public model.Vec2Double position;
    public model.Vec2Double getPosition() { return position; }
    public void setPosition(model.Vec2Double position) { this.position = position; }
    public model.Vec2Double size;
    public model.Vec2Double getSize() { return size; }
    public void setSize(model.Vec2Double size) { this.size = size; }
    public model.JumpState jumpState;
    public model.JumpState getJumpState() { return jumpState; }
    public void setJumpState(model.JumpState jumpState) { this.jumpState = jumpState; }
    public boolean walkedRight;
    public boolean isWalkedRight() { return walkedRight; }
    public void setWalkedRight(boolean walkedRight) { this.walkedRight = walkedRight; }
    public boolean stand;
    public boolean isStand() { return stand; }
    public void setStand(boolean stand) { this.stand = stand; }
    public boolean onGround;
    public boolean isOnGround() { return onGround; }
    public void setOnGround(boolean onGround) { this.onGround = onGround; }
    public boolean onLadder;
    public boolean isOnLadder() { return onLadder; }
    public void setOnLadder(boolean onLadder) { this.onLadder = onLadder; }
    public int mines;
    public int getMines() { return mines; }
    public void setMines(int mines) { this.mines = mines; }
    public model.Weapon weapon;
    public model.Weapon getWeapon() { return weapon; }
    public void setWeapon(model.Weapon weapon) { this.weapon = weapon; }
    public Unit() {}
    public Unit(int playerId, int id, int health, model.Vec2Double position, model.Vec2Double size, model.JumpState jumpState, boolean walkedRight, boolean stand, boolean onGround, boolean onLadder, int mines, model.Weapon weapon) {
        this.playerId = playerId;
        this.id = id;
        this.health = health;
        this.position = position;
        this.size = size;
        this.jumpState = jumpState;
        this.walkedRight = walkedRight;
        this.stand = stand;
        this.onGround = onGround;
        this.onLadder = onLadder;
        this.mines = mines;
        this.weapon = weapon;
    }
    public static Unit readFrom(java.io.InputStream stream) throws java.io.IOException {
        Unit result = new Unit();
        result.playerId = StreamUtil.readInt(stream);
        result.id = StreamUtil.readInt(stream);
        result.health = StreamUtil.readInt(stream);
        result.position = model.Vec2Double.readFrom(stream);
        result.size = model.Vec2Double.readFrom(stream);
        result.jumpState = model.JumpState.readFrom(stream);
        result.walkedRight = StreamUtil.readBoolean(stream);
        result.stand = StreamUtil.readBoolean(stream);
        result.onGround = StreamUtil.readBoolean(stream);
        result.onLadder = StreamUtil.readBoolean(stream);
        result.mines = StreamUtil.readInt(stream);
        if (StreamUtil.readBoolean(stream)) {
            result.weapon = model.Weapon.readFrom(stream);
        } else {
            result.weapon = null;
        }
        return result;
    }
    public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
        StreamUtil.writeInt(stream, playerId);
        StreamUtil.writeInt(stream, id);
        StreamUtil.writeInt(stream, health);
        position.writeTo(stream);
        size.writeTo(stream);
        jumpState.writeTo(stream);
        StreamUtil.writeBoolean(stream, walkedRight);
        StreamUtil.writeBoolean(stream, stand);
        StreamUtil.writeBoolean(stream, onGround);
        StreamUtil.writeBoolean(stream, onLadder);
        StreamUtil.writeInt(stream, mines);
        if (weapon == null) {
            StreamUtil.writeBoolean(stream, false);
        } else {
            StreamUtil.writeBoolean(stream, true);
            weapon.writeTo(stream);
        }
    }

    @Override
    public String toString() {
        return "Unit{" +
                "playerId=" + playerId +
                ", id=" + id +
                ", health=" + health +
                ", position=" + position +
                ", size=" + size +
                ", jumpState=" + jumpState +
                //", walkedRight=" + walkedRight +
                ", stand=" + stand +
                ", onGround=" + onGround +
                //", onLadder=" + onLadder +
                //", mines=" + mines +
                ", weapon=" + weapon +
                '}';
    }
}
