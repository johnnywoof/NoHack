package me.johnnywoof.check.moving;

import me.johnnywoof.Setting;
import me.johnnywoof.Variables;
import me.johnnywoof.check.Check;
import me.johnnywoof.check.CheckType;
import me.johnnywoof.check.DetectionType;
import me.johnnywoof.event.ViolationTriggeredEvent;
import me.johnnywoof.util.MoveData;
import me.johnnywoof.util.Utils;
import me.johnnywoof.util.XYZ;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class Timer extends Check{
	
	public Timer(Variables vars, CheckType ct) {
		super(vars, ct, DetectionType.MOVING);
	}
	
	@Override
	public int runMoveCheck(Player p, Location to, Location from, double yd, double md, MoveData movedata, boolean up, boolean inwater, boolean onladder, XYZ lg){
		
		if(movedata.lastloc == null){
			
			movedata.lastloc = new XYZ(p.getLocation());
			
		}
		
		if((System.currentTimeMillis() - movedata.tptime) <= 2500){
			
			return 0;
			
		}
		
		movedata.setAmount(movedata.getAmount() + 1);
		
		if((System.nanoTime() - movedata.getTimeStart()) >= 500000000){//Must be precise!
			
			int expected = (Setting.maxpacket + Math.round(Utils.getPing(p) / 100));
			
			if(movedata.getAmount() > expected){
					
				int id = this.vars.raiseViolationLevel(CheckType.TIMER, p);
					
				ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.TIMER, p);
				
				Bukkit.getServer().getPluginManager().callEvent(vte);
				
				if(!vte.isCancelled()){
				
					if(id != 0){
							
						String message = Setting.timermes;
						
						message = message.replaceAll(".name.", ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN);
						message = message.replaceAll(".vl.", id + "");
						message = message.replaceAll(".packets-sent.", movedata.getAmount() + "");
						message = message.replaceAll(".expected-packets.", expected + "");

						Utils.messageAdmins(message);
							
					}
					
					if(p.isInsideVehicle()){
						
						p.getVehicle().teleport(movedata.lastloc.toLocation(p.getLocation().getPitch(), p.getLocation().getYaw()), TeleportCause.UNKNOWN);
						
					}else{
					
						p.teleport(movedata.lastloc.toLocation(p.getLocation().getPitch(), p.getLocation().getYaw()), TeleportCause.UNKNOWN);
					
					}
					movedata.reset(new XYZ(p.getLocation()));
					this.vars.setMoveData(p.getName(), movedata);
					return 0;
				
				}
				
			}else{
				
				movedata.reset(new XYZ(p.getLocation()));
				
			}
			
		}
		
		this.vars.setMoveData(p.getName(), movedata);
		return 0;
		
	}

}
