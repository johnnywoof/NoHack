package me.johnnywoof.util;

public class MoveData {

	public long sneaktime, sprinttime, blocktime, lastmounting, velexpirey, velexpirex, tptime;
	public double mda, yda;
	public boolean wassneaking = false, wassprinting = false, wasblocking = false;
	private long timestart;
	private int amount;
	public int lastamount = 0;
	public XYZ lastloc = null;
	
	public MoveData(long lsneak, long lsprint){
		this.sneaktime = lsneak;
		this.sprinttime = lsprint;
	}
	
	public long getTimeStart(){
		return this.timestart;
	}
	
	public void setTimeStart(long v){
		this.timestart = v;
	}
	
	public int getAmount(){
		return this.amount;
	}
	
	public void setAmount(int v){
		this.amount = v;
	}
	
	public void reset(XYZ now){
		this.setTimeStart(System.nanoTime());
		this.setAmount(0);
		this.lastamount = -1;
		this.lastloc = now;
	}
	
}
