package me.johnnywoof;

import java.util.HashMap;

import me.johnnywoof.check.CheckType;
import me.johnnywoof.check.Violation;
import me.johnnywoof.util.MoveData;
import me.johnnywoof.util.XYZ;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Variables {

	final private HashMap<String, Violation> viodata = new HashMap<String, Violation>();
	final private HashMap<String, Long> lastswong = new HashMap<String, Long>();
	final public HashMap<String, Long> deniedlogin = new HashMap<String, Long>();
	final private HashMap<String, MoveData> movedata = new HashMap<String, MoveData>();
	private final HashMap<String, XYZ> currentInteracting = new HashMap<String, XYZ>();
	
	//Last location of being on the ground
	public final HashMap<String, XYZ> lastGround = new HashMap<String, XYZ>();
	
	public void reloadConfig(FileConfiguration fc){
		
		Settings.nofallmes = fc.getString("nofall-message");
		Settings.timermes = fc.getString("timer-message");
		Settings.impossiblemovemes = fc.getString("impossible-move-message");
		Settings.verticalspeedmes = fc.getString("vertical-speed-message");
		Settings.horizontalspeedmes = fc.getString("horizontal-speed-message");
		Settings.flymes = fc.getString("fly-message");
		Settings.blockvisiblebreak = fc.getString("block-visible-break-message");
		Settings.godmodemes = fc.getString("godmode-message");
		Settings.impossibleattack = fc.getString("fight-impossible-message");
		Settings.fightspeed = fc.getString("fight-speed-message");
		Settings.fightreach = fc.getString("fight-reach-message");
		Settings.impossibleclick = fc.getString("inventory-impossible-message");
		Settings.speedclick = fc.getString("inventory-click-speed");
		Settings.noswingmes = fc.getString("noswing-message");
		
	}
	
	public int raiseViolationLevel(CheckType ct, Player p){
		
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
	
	public XYZ lastGround(Player p){
		
		if(this.lastGround.containsKey(p.getName())){
			
			return this.lastGround.get(p.getName());
			
		}else{
			
			return new XYZ(p.getLocation());
			
		}
		
	}
	
	public XYZ getCurrentBlock(String n){
		if(this.currentInteracting.containsKey(n)){
			
			return this.currentInteracting.get(n);
			
		}else{
			
			return null;
			
		}
	}
	
	public long getLastSwong(String v){
		if(this.lastswong.containsKey(v)){
			return this.lastswong.get(v);
		}else{
			return 0;
		}
	}
	
	public void setCurrentBlock(String n, XYZ v){
		this.currentInteracting.put(n, v);
	}
	
	public MoveData getMoveData(String n){
		if(this.movedata.containsKey(n)){
			
			return this.movedata.get(n);
			
		}else{
			
			return new MoveData(0, 0);
			
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
