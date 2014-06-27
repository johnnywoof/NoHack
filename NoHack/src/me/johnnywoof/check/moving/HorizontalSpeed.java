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
import org.bukkit.potion.PotionEffectType;

public class HorizontalSpeed extends Check{

	public HorizontalSpeed(Variables vars, CheckType ct) {
		super(vars, ct, DetectionType.MOVING);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public int runMoveCheck(Player p, Location to, Location from, double yd, double xs, double zs, MoveData movedata, boolean up, boolean inwater, boolean onladder, XYZ lg){
		
		if((System.currentTimeMillis() - movedata.lastmounting) <= 200){
			
			return 0;
			
		}
		
		double ydis = Math.abs(lg.y - to.getY());
		
		if(xs > 0 || zs > 0){
		
			boolean wg = movedata.wasonground;
			
			double mxs = Double.MAX_VALUE;
			
			boolean csneak = p.isSneaking();
			
			if(csneak){
				
				if(!movedata.wassneaking){
					
					long diff = (System.currentTimeMillis() - movedata.sneaktime);
						
					if(diff < 501){//There is a known bypass....gonna fix it sometime
					
						csneak = false;
					
					}
					
				}
				
			}
			
			boolean csprint = p.isSprinting();
			
			if(!csprint){
				
				if((System.currentTimeMillis() - movedata.sprinttime) < 1001){
					
					csprint = true;
					
				}
				
			}
			
			boolean cfly = p.isFlying();
			
			if(!cfly){
				
				if((System.currentTimeMillis() - movedata.flytime) < 2001){
					
					cfly = true;
					
				}
				
			}
			
			if(cfly){
				
				mxs = 0.5443;
				
			}else if(csneak){
				
				if(p.isOnGround()){
					
					mxs = 0.12;
					
				}else{
					
					mxs = 0.15;
					
				}
				
			}else if(csprint){
				
				if(!wg && p.isOnGround()){
					
					mxs = 0.613;
					
				}else if(wg && !p.isOnGround()){
					
					mxs = 0.4;
					
				}else if(!wg && !p.isOnGround()){
					
					mxs = 0.4;
					
				}else{
					
					if((System.currentTimeMillis() - movedata.groundtime) <= 600){
						
						mxs = 0.55;
						
					}else{
					
						mxs = 0.353;
					
					}
					
				}
				
			}else if(inwater){
				
				if((System.currentTimeMillis() - movedata.groundtime) <= 1200){
					
					mxs = 0.3;
					
				}else{
				
					mxs = 0.13;
				
				}
				
			}else{
				
				if(!wg && p.isOnGround()){
					
					mxs = 0.283;
					
				}else{
				
					mxs = 0.243;
				
				}
				
			}
			
			if(p.hasPotionEffect(PotionEffectType.SPEED)){
				
				int level = Utils.getPotionEffectLevel(p, PotionEffectType.SPEED);
				
				if(level > 0){
				
					mxs = (0.0812) * ((0.5 * level) + 1);
				
				}
				
			}
			
			if(xs > mxs || zs > mxs){
					
				int id = this.vars.raiseViolationLevel(CheckType.HORIZONTAL_SPEED, p);
				
				ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.HORIZONTAL_SPEED, p);
					
				Bukkit.getServer().getPluginManager().callEvent(vte);
					
				if(!vte.isCancelled()){
						
					if(Setting.debug){
							
						p.sendMessage("XS: " + xs + ";XZ:" + zs + "; Max: " + mxs + ";G: " + p.isOnGround() + ";WG: " + movedata.wasonground + ";GT: " + (System.currentTimeMillis() - movedata.groundtime));
							
					}
					
					if(id != 0){
							
						String message = Setting.horizontalspeedmes;
							
						message = message.replaceAll(".name.", ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN);
						message = message.replaceAll(".vl.", id + "");
	
						Utils.messageAdmins(message);
							
					}
					return 1;
					
				}
					
			}
		
		}
		
		if(!p.isOnGround() && !p.getAllowFlight()){
			
			double mdis = this.getXZDistance(to.getX(), lg.x, to.getZ(), lg.z);
			
			if(mdis > this.getMaxMD(inwater, p.isOnGround(), p, ydis, movedata)){
				
				int id = this.vars.raiseViolationLevel(CheckType.GLIDE, p);
				
				ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.GLIDE, p);
				
				Bukkit.getServer().getPluginManager().callEvent(vte);
				
				if(!vte.isCancelled()){
				
					if(id != 0){
						
						String message = Setting.glidemes;
						
						message = message.replaceAll(".name.", ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN);
						message = message.replaceAll(".vl.", id + "");

						Utils.messageAdmins(message);
						
					}
					
					return 1;
				
				}
				
			}
		
		}
		
		return 0;
		
	}
	
	private double getMaxMD(boolean inwater, boolean onground, Player p, double ydis, MoveData md){
		
		double d = 0D;
		
		boolean csneak = p.isSneaking();
		boolean csprint = p.isSprinting();
		boolean cblock = p.isBlocking();
		
		long now = System.currentTimeMillis();
		
		if(cblock){
			
			if((now - md.blocktime) <= 1500){
				
				cblock = false;
				
			}
			
		}
		
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
		
			d = 1.30;
			
		}else if(csprint){
			
			d = (18.3 + d) + (8 * ydis);
			
		}else if(csneak){
			
			if(onground){
				
				d = 0.065;
				
			}else{
					
				d = 0.67;
				
			}
			
		}else if(cblock){
			
			d = 0.015;
			
		}else{
			
			d = (5.6 + d) + (3 * ydis);
			
		}
		
		if(md.mda != 0 && (System.currentTimeMillis() < md.velexpirex)){
			
			d = d + md.mda;
			
		}
		
		return d;
		
	}
	
	private double getXZDistance(double x1, double x2, double z1, double z2){
		
		double a1 = (x2 - x1), a2 = (z2 - z1);
		
		return ((a1 * (a1)) + (a2 * a2));
		
	}
	
}
