package model;

import util.StreamUtil;

public class UnitF {
    public int playerId;
    public int getPlayerId() { return playerId; }
    //public void setPlayerId(int playerId) { this.playerId = playerId; }
    public int id;
    public int getId() { return id; }
    //public void setId(int id) { this.id = id; }
    public int health;
    //public int getHealth() { return health; }
    //public void setHealth(int health) { this.health = health; }
    public Vec2Float position;
    //public Vec2Float getPosition() { return position; }
    //public void setPosition(Vec2Float position) { this.position = position; }
    public Vec2Float size;
    //public Vec2Float getSize() { return size; }
    //public void setSize(Vec2Float size) { this.size = size; }
    public JumpState jumpState;
    //public JumpState getJumpState() { return jumpState; }
    //public void setJumpState(JumpState jumpState) { this.jumpState = jumpState; }
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
    public Weapon weapon;
    //public Weapon getWeapon() { return weapon; }
    //public void setWeapon(Weapon weapon) { this.weapon = weapon; }
    public UnitF() {}
    public UnitF(int playerId, int id, int health, Vec2Float position, Vec2Float size, JumpState jumpState, boolean walkedRight, boolean stand, boolean onGround, boolean onLadder, int mines, Weapon weapon) {
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
    public static UnitF readFrom(java.io.InputStream stream) throws java.io.IOException {
        UnitF result = new UnitF();
        result.playerId = StreamUtil.readInt(stream);
        result.id = StreamUtil.readInt(stream);
        result.health = StreamUtil.readInt(stream);
        result.position = model.Vec2Double.readFromF(stream);
        result.size = model.Vec2Double.readFromF(stream);
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
