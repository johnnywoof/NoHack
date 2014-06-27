package me.johnnywoof;

import java.util.HashMap;
import java.util.UUID;

import me.johnnywoof.check.CheckType;
import me.johnnywoof.check.Violation;
import me.johnnywoof.util.MoveData;
import me.johnnywoof.util.XYZ;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
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
	
	public void reloadConfig(FileConfiguration fc){
		
		Setting.nofallmessage = fc.getString("nofall-message");
		Setting.timermes = fc.getString("timer-message");
		Setting.impossiblemovemes = fc.getString("impossible-move-message");
		Setting.verticalspeedmes = fc.getString("vertical-speed-message");
		Setting.horizontalspeedmes = fc.getString("horizontal-speed-message");
		Setting.flymes = fc.getString("fly-message");
		Setting.blockvisiblebreak = fc.getString("block-visible-break-message");
		Setting.godmodemes = fc.getString("godmode-message");
		Setting.impossibleattack = fc.getString("fight-impossible-message");
		Setting.fightspeed = fc.getString("fight-speed-message");
		Setting.fightreach = fc.getString("fight-reach-message");
		Setting.impossibleclick = fc.getString("inventory-impossible-message");
		Setting.speedclick = fc.getString("inventory-click-speed");
		Setting.noswingmes = fc.getString("noswing-message");
		Setting.chatimpossiblemes = fc.getString("chat-impossible-message");
		Setting.fightvisiblemes = fc.getString("fight-visible-message");
		Setting.ignorenpc = fc.getBoolean("ignore-citizens-npc");
		Setting.noswingblock = fc.getLong("noswing-block-difference");
		Setting.creativeattack = fc.getDouble("creative-attack-reach");
		Setting.survivalattack = fc.getDouble("survival-attack-reach");
		Setting.fightattackspeed = fc.getInt("fight-attack-speed");
		Setting.noswingfight = fc.getInt("noswing-fight-difference");
		Setting.fcs = fc.getInt("inventory-click-max-speed");
		Setting.maxpacket = fc.getInt("max-packets");
		Setting.glidemes = fc.getString("glide-message");
		Setting.autoban = fc.getBoolean("autoban-players");
		Setting.autobantime = fc.getInt("autoban-time");
		Setting.autosoupmes = fc.getString("autosoup-message");
		Setting.fasteatmes = fc.getString("fast-eat-message");
		Setting.speedbreakmes = fc.getString("speed-break-message");
		Setting.useplib = fc.getBoolean("use-protocollib");
		Setting.killmode = fc.getInt("killaura-mode");
		Setting.killcmds = fc.getStringList("killaura-detect");
		
		if(Setting.useplib){
			
			this.usingprolib = Bukkit.getPluginManager().getPlugin("ProtocolLib") != null;
			
		}
		
	}
	
	public void removeDeniedLogin(UUID uuid){
		this.deniedlogin.remove(uuid);
	}
	
	public String[] getDeniedData(Player p){
		
		if(this.deniedlogin.containsKey(p.getUniqueId())){
			
			return this.deniedlogin.get(p.getUniqueId()).split(".:.");
			
		}else{
			
			return null;
			
		}
		
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
