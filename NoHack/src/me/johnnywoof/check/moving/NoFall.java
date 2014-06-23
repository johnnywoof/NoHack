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
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class NoFall extends Check{

	public NoFall(Variables vars, CheckType ct) {
		super(vars, ct, DetectionType.MOVING);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public int runMoveCheck(Player p, Location to, Location from, double yd, double md, MoveData movedata, boolean up, boolean inwater, boolean onladder, XYZ lg){

		//If flying, ignore this check
		if(p.isFlying() && p.getAllowFlight()){
			
			return 0;
			
		}
		
		//Prevent bypassing fly checks when moving in an horiztonal motion
		if(p.isOnGround() && !inwater){
			
			if(to.getBlockX() != from.getBlockX() || to.getBlockZ() != from.getBlockZ()){
				
				int by = to.getBlockY();
				
				int oy = by;
				
				while(true){
					
					by--;
					
					if(to.getBlock().getRelative(0, ((oy - by) * -1), 0).getType().isSolid()){
						
						break;
						
					}
					
					if(by < 0){
						
						break;//Safe check for flying over bedrock...which should be impossible
						
					}
					
				}
				
				if((oy - by) > 2){
					
					int id = this.vars.raiseViolationLevel(CheckType.FLY, p);
					
					ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.FLY, p);
						
					Bukkit.getServer().getPluginManager().callEvent(vte);
						
					if(!vte.isCancelled()){
						
						if(id != 0){
								
							String message = Setting.flymes;
								
							message = message.replaceAll(".name.", ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN);
							message = message.replaceAll(".vl.", id + "");

							Utils.messageAdmins(message);
								
						}
						return 1;
						
					}
					
				}
				
			}
			
		}
		
		//Start nofall & fly check
		if(!p.getAllowFlight()){
			
			if(to.getBlockY() != from.getBlockY()){
				
				if(up && p.isOnGround() && !inwater){
					
					if(p.getVelocity().getY() < 0){//Moving up when velocity says to go down...seems legit
							
						Material m = from.getBlock().getType();
						
						if(m == Material.CHEST || m == Material.TRAPPED_CHEST){
							
							return 0;
							
						}
						
						int id = this.vars.raiseViolationLevel(CheckType.NOFALL, p);
							
						ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.NOFALL, p);
							
						Bukkit.getServer().getPluginManager().callEvent(vte);
							
						if(!vte.isCancelled()){
							
							if(id != 0){
									
								String message = Setting.nofallmessage;
									
								message = message.replaceAll(".name.", ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN);
								message = message.replaceAll(".vl.", id + "");

								Utils.messageAdmins(message);
									
							}
							return 4;
							
						}
					}
					
				}
				
			}
			
		}
		//End nofall & fly check
		
		if(!up && yd > 0.25 && p.isOnGround()){ //Falling while onground? I DON'T THINK SO
			
			int id = this.vars.raiseViolationLevel(CheckType.NOFALL, p);
				
			ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.NOFALL, p);
			
			Bukkit.getServer().getPluginManager().callEvent(vte);
			
			if(!vte.isCancelled()){
			
				if(id != 0){
					
					String message = Setting.nofallmessage;
					
					message = message.replaceAll(".name.", ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN);
					message = message.replaceAll(".vl.", id + "");

					Utils.messageAdmins(message);
						
				}
				
				return 4;//More expensive to put the player back than to check it
			
			}
			
		}
		
		return 0;
		
	}

}
