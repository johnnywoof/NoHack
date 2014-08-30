package me.johnnywoof.util;

public class MoveData {

    public long sneaktime, sprinttime, blocktime, lastmounting, velexpirex, velexpirey, tptime, flytime, groundtime, usetime;
    public double mda, yda;
    public boolean wassneaking = false, wassprinting = false, wasblocking = false, wasflying = false,
            wasonground = true;
    private long timestart;
    private int amount;
    public XYZ lastloc = null;

    public MoveData(XYZ lastloc) {
        this.reset(lastloc);
    }

    public long getTimeStart() {
        return this.timestart;
    }

    public void setTimeStart(long v) {
        this.timestart = v;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int v) {
        this.amount = v;
    }

    public void reset(XYZ now) {
        this.setTimeStart(System.currentTimeMillis());
        this.setAmount(0);
        this.lastloc = now;
    }

}
