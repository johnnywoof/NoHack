package me.johnnywoof.check;

import me.johnnywoof.util.XYZ;

public class MovePacketData {

	private long timestart;
	private int amount;
	public int lastamount = 0;
	public XYZ lastloc = null;
	
	public MovePacketData(){}
	
	public MovePacketData(long t, int a){
		this.timestart = t;
		this.amount = a;
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
		this.setTimeStart(System.currentTimeMillis());
		this.setAmount(0);
		this.lastamount = -1;
		this.lastloc = now;
	}
	
}
