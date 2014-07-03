package me.johnnywoof;

import java.util.HashMap;
import java.util.UUID;

import me.johnnywoof.check.CheckType;
import me.johnnywoof.check.Violation;
import me.johnnywoof.util.MoveData;
import me.johnnywoof.util.Utils;
import me.johnnywoof.util.XYZ;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Variables {

	final private HashMap<String, Violation> viodata = new HashMap<String, Violation>();
	final private HashMap<String, Long> lastswong = new HashMap<String, Long>();
	//A string is used to parse the data
	final private HashMap<UUID, String> deniedlogin = new HashMap<UUID, String>();
	final private HashMap<String, MoveData> movedata = new HashMap<String, MoveData>();
	
	//Last location of being on the ground
	public final HashMap<String, XYZ> lastGround = new HashMap<String, XYZ>();
	
	public boolean usingprolib = false;
	
	public void removeDeniedLogin(UUID uuid){
		this.deniedlogin.remove(uuid);
	}
	
	/**
	 * 
	 * Issues a violation
	 * @return if to cancel the event
	 * 
	 * */
	public boolean issueViolation(Player p, CheckType ct){
		
		int id = this.raiseViolationLevel(ct, p);
		
		if(id != 0){
			
			Utils.messageStaff(Settings.getFormatted(p.getName(), ct, id));
			
		}
		
		return true;
		
	}
	
	public String[] getDeniedData(Player p){
		
		if(this.deniedlogin.containsKey(p.getUniqueId())){
			
			return this.deniedlogin.get(p.getUniqueId()).split(".:.");
			
		}else{
			
			return null;
			
		}
		
	}
	
	private int raiseViolationLevel(CheckType ct, Player p){
		
		Violation vio = null;
		
		if(this.viodata.containsKey(p.getName())){
			
			vio = this.viodata.get(p.getName());
			
		}else{
			
			vio = new Violation();
			
		}
		
		boolean non = vio.shouldNotify();
		
		if(vio.raiseLevel(ct, p)){//Plugin canceled it, do not notify
			
			non = false;
			
		}
		
		if(non){
			
			vio.updateNotify();
			
		}
		
		this.viodata.put(p.getName(), vio);
		
		if(non){
			
			return vio.getLevel(ct);
			
		}else{
			
			return 0;
			
		}
		
	}
	
	public void setDeniedLogin(Player p, long future, String reason){
		
		this.deniedlogin.put(p.getUniqueId(), String.valueOf(future) + ".:." + reason);
		
	}
	
	public XYZ lastGround(Player p){
		
		if(this.lastGround.containsKey(p.getName())){
			
			return this.lastGround.get(p.getName());
			
		}else{
			
			return new XYZ(p.getLocation());
			
		}
		
	}
	
	public long getLastSwong(String v){
		if(this.lastswong.containsKey(v)){
			return this.lastswong.get(v);
		}else{
			return 0;
		}
	}
	
	@SuppressWarnings("deprecation")
	public MoveData getMoveData(String n){
		if(this.movedata.containsKey(n)){
			
			return this.movedata.get(n);
			
		}else{
			
			return new MoveData(new XYZ(Bukkit.getPlayer(n).getLocation()));//Hope this is always a player
			
		}
	}
	
	public void setMoveData(String n, MoveData v){
		this.movedata.put(n, v);
	}
	
	public void setViolation(String v, Violation vio){
		this.viodata.put(v, vio);
	}
	
	public Violation getViolation(String v){
		
		Violation vio = null;
		
		if(this.viodata.containsKey(v)){
			
			vio = this.viodata.get(v);
			
		}else{
			
			vio = new Violation();
			
		}
		
		return vio;
		
	}
	
	public void updateLastSwong(String v){
		this.lastswong.put(v, System.currentTimeMillis());
	}
	
}
