package model;

import util.StreamUtil;

public class JumpState {
    public boolean canJump;
    public boolean isCanJump() { return canJump; }
    public void setCanJump(boolean canJump) { this.canJump = canJump; }
    public double speed;
    //public double getSpeed() { return speed; }
    //public void setSpeed(double speed) { this.speed = speed; }
    public double maxTime;
    public double getMaxTime() { return maxTime; }
    public void setMaxTime(double maxTime) { this.maxTime = maxTime; }
    public boolean canCancel;
    public boolean isCanCancel() { return canCancel; }
    public void setCanCancel(boolean canCancel) { this.canCancel = canCancel; }
    public JumpState() {}
    /*public JumpState(boolean canJump, double speed, double maxTime, boolean canCancel) {
        this.canJump = canJump;
        this.speed = speed;
        this.maxTime = maxTime;
        this.canCancel = canCancel;
    }*/

    public JumpState(JumpState jumpState) {
        this.canJump    = jumpState.canJump;
        this.speed      = jumpState.speed;
        this.maxTime    = jumpState.maxTime;
        this.canCancel  = jumpState.canCancel;
    }

    public static JumpState readFrom(java.io.InputStream stream) throws java.io.IOException {
        JumpState result = new JumpState();
        result.canJump = StreamUtil.readBoolean(stream);
        result.speed = StreamUtil.readDouble(stream);
        result.maxTime = StreamUtil.readDouble(stream);
        result.canCancel = StreamUtil.readBoolean(stream);
        return result;
    }
    public void writeTo(java.io.OutputStream stream) throws java.io.IOException {
        StreamUtil.writeBoolean(stream, canJump);
        StreamUtil.writeDouble(stream, speed);
        StreamUtil.writeDouble(stream, maxTime);
        StreamUtil.writeBoolean(stream, canCancel);
    }

    @Override
    public String toString() {
        return "JumpState{" +
                "canJump=" + canJump +
                ", speed=" + speed +
                ", maxTime=" + maxTime +
                ", canCancel=" + canCancel +
                '}';
    }
}
