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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class VerticalSpeed extends Check{

	public VerticalSpeed(Variables vars, CheckType ct) {
		super(vars, ct, DetectionType.MOVING);
	}

	@Override
	public int run(Player p, Location from, Location to, long ls, LivingEntity e, double damage, Block clicked, BlockFace bf, String mes, boolean blockmove, boolean onladder, boolean up, boolean inwater, double yd, double md, XYZ lg){
		
		if(yd != 0){//Actually move on the y axis
			
			if(up && onladder){
				
				if(yd > ((p.getAllowFlight() || (to.getY() % 1) <= 0.4) ? 0.424 : 0.118)){
					
					int id = this.vars.raiseViolationLevel(CheckType.VERTICAL_SPEED, p);
					
					ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.VERTICAL_SPEED, p);
					
					Bukkit.getServer().getPluginManager().callEvent(vte);
					
					if(!vte.isCancelled()){
						
						if(id != 0){
							
							String message = Setting.verticalspeedmes;
							
							message = message.replaceAll(".name.", ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN);
							message = message.replaceAll(".vl.", id + "");

							Utils.messageAdmins(message);
							
						}
						return 1;
					
					}
					
				}
				
			}
			
			if(up){
				
				if(yd > this.getMaxVertical(p)){//Moving up only
					
					int id = this.vars.raiseViolationLevel(CheckType.VERTICAL_SPEED, p);
					
					ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.VERTICAL_SPEED, p);
					
					Bukkit.getServer().getPluginManager().callEvent(vte);
					
					if(!vte.isCancelled()){
					
						if(id != 0){
							
							String message = Setting.verticalspeedmes;
							
							message = message.replaceAll("%name%", ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN);
							message = message.replaceAll("%vl%", id + "");

							Utils.messageAdmins(message);
							
						}
						return 1;
					
					}
					
				}
				
			}
			
		}
		
		return 0;
		
	}
	
	private double getMaxVertical(Player p){
		
		double d = 0.5;
		
		if(p.hasPotionEffect(PotionEffectType.JUMP)){
			
			d = d + ((this.getPotionEffectLevel(p, PotionEffectType.JUMP)) * 0.11);
			
		}
		
		if(p.getVelocity().getY() > 0){
			
			d = d + (p.getVelocity().getY());
			
		}
		
		return d;
		
	}
	
	private int getPotionEffectLevel(Player p, PotionEffectType pet){
		
		for(PotionEffect pe : p.getActivePotionEffects()){

			if(pe.getType().getName().equals(pet.getName())){
				
				return pe.getAmplifier() + 1;
				
			}
			
		}
		
		return 0;
		
	}
	
}
