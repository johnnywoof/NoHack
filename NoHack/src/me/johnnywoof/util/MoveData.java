package me.johnnywoof.util;

public class MoveData {

	public long sneaktime, sprinttime, blocktime;
	public boolean sneaking = false, sprinting = false, blocking = false;
	
	public MoveData(long lsneak, long lsprint){
		this.sneaktime = lsneak;
		this.sprinttime = lsprint;
	}
	
}
