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
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class HorizontalSpeed extends Check{

	public HorizontalSpeed(Variables vars, CheckType ct) {
		super(vars, ct, DetectionType.MOVING);
	}

	@SuppressWarnings("deprecation")
	@Override
	public int run(Player p, Location from, Location to, long ls, LivingEntity e, double damage, Block clicked, BlockFace bf, String mes, boolean blockmove, boolean onladder, boolean up, boolean inwater, double yd, double md, XYZ lg){
		
		double ydis = Math.abs(lg.y - to.getY());
		
		if(md != 0){
			
			MoveData moved = this.vars.getMoveData(p.getName());
			
			if((System.currentTimeMillis() - moved.lastmounting) <= 200){
				
				moved = null;
				return 0;
				
			}
			
			if(md > this.getMaxHorizontal(p.isOnGround(), inwater, p, moved)){
				
				int id = this.vars.raiseViolationLevel(CheckType.HORIZONTAL_SPEED, p);
				
				ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.HORIZONTAL_SPEED, p);
				
				Bukkit.getServer().getPluginManager().callEvent(vte);
				
				if(!vte.isCancelled()){
				
					if(id != 0){
						
						String message = Setting.horizontalspeedmes;
						
						message = message.replaceAll(".name.", ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN);
						message = message.replaceAll(".vl.", id + "");

						Utils.messageAdmins(message);
						
					}
					return 1;
				
				}
				
			}else{
				
				if(!p.isOnGround() && !p.getAllowFlight()){
					
					double mdis = this.getXZDistance(to.getX(), lg.x, to.getZ(), lg.z);
					
					if(mdis > this.getMaxMD(inwater, p.isOnGround(), p, ydis, moved)){
						
						int id = this.vars.raiseViolationLevel(CheckType.GLIDE, p);
						
						ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.GLIDE, p);
						
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
			
		}
		
		return 0;
		
	}
	
	private double getMaxMD(boolean inwater, boolean onground, Player p, double ydis, MoveData md){
		
		double d = 0D;
		
		boolean csneak = p.isSneaking();
		boolean csprint = p.isSprinting();
		
		long now = System.currentTimeMillis();
		
		if(!csneak){
			
			if((now - md.sneaktime) <= 1000){
				
				csneak = true;
				
			}
			
		}else{
			
			if(!onground){
				
				if((now - md.sneaktime) <= 1000){
					
					csneak = false;
					
				}
				
			}
			
		}
		
		if(!csprint){
			
			if((now - md.sprinttime) <= 1000){
				
				csprint = true;
				
			}
			
		}

		//TODO Account jump effect
		
		if(p.isFlying()){
		
			d = 1.25;
			
		}else if(csprint){
			
			d = (18.3 + d) + (8 * ydis);
			
		}else if(csneak){
			
			if(onground){
				
				d = 0.065;
				
			}else{
					
				d = 0.67;
				
			}
			
		}else{
			
			d = (5.6 + d) + (3 * ydis);
			
		}
		
		return d;
		
	}
	
	private double getXZDistance(double x1, double x2, double z1, double z2){
		
		double a1 = (x2 - x1), a2 = (z2 - z1);
		
		return ((a1 * (a1)) + (a2 * a2));
		
	}
	
	private double getMaxHorizontal(boolean onground, boolean inwater, Player p, MoveData md){
		
		double d = 0;
		
		boolean csneak = p.isSneaking();
		
		if(!csneak){
			
			if((System.currentTimeMillis() - md.sneaktime) <= 1000){
				
				csneak = true;
				
			}
			
		}
		
		boolean csprint = p.isSprinting();
		
		if(!csprint){
			
			if((System.currentTimeMillis() - md.sprinttime) <= 1000){
				
				csprint = true;
				
			}
			
		}
		
		if(p.isFlying()){
		
			d = 0.305;
			
		}else{
			
			if(csprint){
				
				d = 0.788;
				
			}else if(csneak){
				
				if(p.getAllowFlight()){
					
					d = 0.305;
					
				}else{
					
					if(onground && p.isSneaking()){
				
						d = 0.025;
					
					}else if(onground){
						
						d = 0.467;
						
					}else if(!onground && p.isSneaking()){
						
						d = 0.467;
						
					}
				
				}
				
			}else if(p.isBlocking()){
				
				d = 0.0075;
				
			}else{
				
				d = 0.467;
			
			}
		
		}
		
		double cd = ((p.getVelocity().getX() + p.getVelocity().getZ()) * 5);
		
		if(cd > 0){
		
			d = cd + d;
		
		}
		
		return d;
		
	}
	
}
