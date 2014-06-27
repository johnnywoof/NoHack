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
	public int runMoveCheck(Player p, Location to, Location from, double yd, double xs, double zs, MoveData movedata, boolean up, boolean inwater, boolean onladder, XYZ lg){
		
		if(to.getX() != from.getX() || to.getY() != from.getY() || to.getZ() != from.getZ()){
			
			if((System.currentTimeMillis() - movedata.getTimeStart()) > 500){
			
				int max = 0;
				
				max = Math.round((Utils.getPing(p) / 100));
				
				if(max < 0){
					
					max = 0;
					
				}
				
				max = max + Setting.maxpacket;
				
				if(movedata.getAmount() > max){
					
					//Maybe block the checkpoint exploit?
					//double xzdis = Utils.getXZDistance(movedata.lastloc.x, to.getX(), movedata.lastloc.z, to.getZ());
					
					int id = this.vars.raiseViolationLevel(CheckType.TIMER, p);
					
					ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.TIMER, p);
					
					Bukkit.getServer().getPluginManager().callEvent(vte);
					
					if(!vte.isCancelled()){
						
						if(movedata.getAmount() > 50){
							
							p.kickPlayer("Too many packets! Are you (or the server) lagging badly?");
							
						}else{
					
							p.teleport(movedata.lastloc.toLocation(to.getPitch(), to.getYaw()), TeleportCause.UNKNOWN);
						
						}
						
						if(id != 0){
							
							String message = Setting.timermes;
							
							message = message.replaceAll(".name.", ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN);
							message = message.replaceAll(".vl.", id + "");
							message = message.replaceAll(".packets-sent.", movedata.getAmount() + "");
							message = message.replaceAll(".expected-packets.", max + "");

							Utils.messageAdmins(message);
							
						}
						
						movedata.reset(movedata.lastloc);
					
					}
					
				}else{
				
					movedata.reset(new XYZ(from));
				
				}
				
			}else{
				
				movedata.setAmount(movedata.getAmount() + 1);
				
			}
			
			this.vars.setMoveData(p.getName(), movedata);
		
		}
		
		return 0;
		
	}

}
