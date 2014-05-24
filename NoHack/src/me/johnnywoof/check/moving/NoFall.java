package me.johnnywoof.check.moving;

import me.johnnywoof.Setting;
import me.johnnywoof.Variables;
import me.johnnywoof.check.Check;
import me.johnnywoof.check.CheckType;
import me.johnnywoof.check.DetectionType;
import me.johnnywoof.event.ViolationTriggeredEvent;
import me.johnnywoof.util.Utils;
import me.johnnywoof.util.XYZ;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class NoFall extends Check{

	public NoFall(Variables vars, CheckType ct) {
		super(vars, ct, DetectionType.MOVING);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public int run(Player p, Location from, Location to, long ls, LivingEntity e, double damage, Block clicked, BlockFace bf, String mes, boolean blockmove, boolean onladder, boolean up, boolean inwater, double yd, double md, XYZ lg){

		//If flying, ignore this check
		if(p.isFlying()){
			
			return 0;
			
		}
		
		//Start nofall & fly check
		if(!p.getAllowFlight()){
			
			if(to.getBlockY() != from.getBlockY()){
				
				if(up && p.isOnGround() && !inwater){
					
					if(p.getVelocity().getY() < 0){//Moving up when velocity says to go down...seems legit
							
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
