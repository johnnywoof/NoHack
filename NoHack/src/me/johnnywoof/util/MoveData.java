package me.johnnywoof.util;

public class MoveData {

	public long sneaktime, sprinttime;
	public boolean sneaking = false, sprinting = false;
	
	public MoveData(long lsneak, long lsprint){
		this.sneaktime = lsneak;
		this.sprinttime = lsprint;
	}
	
}
