package me.johnnywoof.check;

public class MovePacketData {

	private long timestart;
	private int amount;
	public int lastamount = 0;
	
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
	
	public void reset(){
		this.setTimeStart(System.currentTimeMillis());
		this.setAmount(0);
		this.lastamount = -1;
	}
	
}
