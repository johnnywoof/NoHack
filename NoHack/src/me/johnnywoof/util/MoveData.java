package me.johnnywoof.util;

public class MoveData {

	public long sneaktime, sprinttime, blocktime, lastmounting;
	public boolean wassneaking = false, wassprinting = false, wasblocking = false;
	
	public MoveData(long lsneak, long lsprint){
		this.sneaktime = lsneak;
		this.sprinttime = lsprint;
	}
	
}
