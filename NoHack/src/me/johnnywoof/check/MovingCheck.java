package me.johnnywoof.check;

import java.util.HashMap;

import me.johnnywoof.CheckType;
import me.johnnywoof.NoHack;
import me.johnnywoof.util.MoveData;
import me.johnnywoof.util.Utils;
import me.johnnywoof.util.XYZ;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MovingCheck {

	private final HashMap<String, MovePacketData> movepackets = new HashMap<String, MovePacketData>();
	public final HashMap<String, XYZ> lastGround = new HashMap<String, XYZ>();
	
	public int checkMove(NoHack nh, Player p, Location from, Location to){
		
		double yd = Math.abs((from.getY() - to.getY()));//Vertical speed
		boolean up = ((to.getY() - from.getY()) > 0);//Moving up?
		double md = this.getXZDistance(from.getX(), to.getX(), from.getZ(), to.getZ());//Horizontal speed
		boolean inwater = ((CraftPlayer) p).getHandle().inWater;
		boolean onladder = ((CraftPlayer) p).getHandle().h_();//On ladder? NMS ftw!
		
		@SuppressWarnings("deprecation")
		boolean onground = p.isOnGround();//Yeah...I'm aware how clients can send "always true" booleans.
		XYZ lg = this.lastGround.get(p.getName());
		if(lg == null){
			lg = new XYZ(from);
		}
		
		if(to.getBlockY() != from.getBlockY()){
			
			if(up && onground && !inwater){
				
				if(to.getY() % 1 != 0){
				
					if(to.getBlock().getRelative(BlockFace.DOWN).getType() == org.bukkit.Material.AIR){
						
						int id = nh.raiseViolationLevel(p.getName(), CheckType.FLY);
						
						if(id != 0){
							
							Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed Fly! Tried to fly with nofall enabled. VL " + id);
							
						}
						return 4;
						
					}
					
				}
				
			}
			
		}
		
		if(to.getX() != from.getX() || to.getY() != from.getY() || to.getZ() != from.getZ()){
			
			if(p.isSneaking() && p.isSprinting()){
				
				int id = nh.raiseViolationLevel(p.getName(), CheckType.IMPOSSIBLE);
				
				if(id != 0){
					
					Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed Impossible! Tried to sneak and sprint at the same time. VL " + id);
					
				}
				return 1;
				
			}
			
			double dis = from.distanceSquared(to);
			
			if(dis > 30){
				
				p.kickPlayer("You moved too fast! Hacking? :(");
				int id = nh.raiseViolationLevel(p.getName(), CheckType.IMPOSSIBLE);
				
				if(id != 0){
					
					Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed Impossible! Tried to avoid anti-cheating by using code filter. VL " + id);
					
				}
				return 0;
				
			}
			
			int tid = this.checkTimer(nh, p);
			
			if(tid != 0){
				return tid;
			}
			
			if(yd != 0){//Actually move on the y axis
				
				if(up && onladder){
					
					if(yd > ((p.getAllowFlight() || (to.getY() % 1) <= 0.4) ? 0.424 : 0.118)){
						
						int id = nh.raiseViolationLevel(p.getName(), CheckType.VERTICAL_SPEED);
						
						if(id != 0){
							
							Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed Vertical Speed! Moved too fast on a ladder (" + yd + "). VL " + id);
							
						}
						return 1;
						
					}
					
				}
				
				if(up){
					
					if(yd > this.getMaxVertical(p)){//Moving up only
						
						int id = nh.raiseViolationLevel(p.getName(), CheckType.VERTICAL_SPEED);
						
						if(id != 0){
							
							Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed Vertical Speed! Speed was " + yd + " and max is " + this.getMaxVertical(p) + ". VL " + id);
							
						}
						return 1;
						
					}
					
				}
				
			}
			
			double ydis = Math.abs(lg.y - to.getY());
			
			if(md != 0){
				
				if(md > this.getMaxHorizontal(onground, inwater, p, nh.getMoveData(p.getName()))){
					
					int id = nh.raiseViolationLevel(p.getName(), CheckType.HORIZONTAL_SPEED);
					
					if(id != 0){
						
						Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed Horizontal Speed! Speed was " + md + ". VL " + id);
						
					}
					return 1;
					
				}else{
					
					if(!onground && !p.getAllowFlight()){
						
						double mdis = this.getXZDistance(to.getX(), lg.x, to.getZ(), lg.z);
						//Bukkit.broadcastMessage("FAILED @ " + mdis + ":" + ydis);
						if(mdis > this.getMaxMD(inwater, onground, p, ydis, nh.getMoveData(p.getName()))){
							
							int id = nh.raiseViolationLevel(p.getName(), CheckType.FLY);
							
							if(id != 0){
								
								Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed Fly! MDIS was " + mdis + ". VL " + id);
								
							}
							return 1;
							
						}
					
					}
					
				}
				
			}
			
			if(!up && yd > 0.25 && onground){ //Falling while onground? I DON'T THINK SO
				
				int id = nh.raiseViolationLevel(p.getName(), CheckType.NOFALL);
					
				if(id != 0){
						
					Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed NoFall! Tried to fall while stating on the ground. VL " + id);
						
				}
				
				return 4;//More expensive to put the player back than to check it
				
			}
			
			if(onground || inwater || p.isFlying() || onladder){
				
				this.lastGround.put(p.getName(), new XYZ(from));
				
			}else{
				
				if(!p.getAllowFlight() && !inwater && !onladder){//Ignore users that are allowed to fly. Doesn't count for the hack fly!
					
					if(up){
						
						if(ydis > this.getMaxHight(p)){
							
							int id = nh.raiseViolationLevel(p.getName(), CheckType.FLY);
							
							if(id != 0){
								
								Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed Fly! Height was " + ydis + ". VL " + id);
								
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
		
		if(!p.isSneaking()){
			
			if((now - md.sneaktime) <= 1000){
				
				csneak = true;
				
			}
			
		}
		
		if(!p.isSprinting()){
			
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
	
	private double getMaxHight(Player p){
		
		double d = 1.35;
		
		if(p.hasPotionEffect(PotionEffectType.JUMP)){
			
			int level = this.getPotionEffectLevel(p, PotionEffectType.JUMP);
			
			if(level == 1){
				
				d = 1.9;
				
			}else if(level == 2){
				
				d = 2.7;
				
			}else if(level == 3){
				
				d = 3.36;
				
			}else if(level == 4){
				
				d = 4.22;
				
			}else if(level == 5){
				
				d = 5.16;
				
			}else if(level == 6){
				
				d = 6.19;
				
			}else if(level == 7){
				
				d = 7.31;
				
			}else if(level == 8){
				
				d = 8.5;
				
			}else if(level == 9){
				
				d = 9.76;
				
			}else if(level == 10){
				
				d = 11.1;
				
			}else{
				
				d = (level) + 1;
				
			}
			
		}
		
		return d;
		
	}
	
	private double getXZDistance(double x1, double x2, double z1, double z2){
		
		double a1 = (x2 - x1), a2 = (z2 - z1);
		
		return ((a1 * (a1)) + (a2 * a2));
		
	}
	
	private int checkTimer(NoHack nh, Player p){
		MovePacketData mpd = this.movepackets.get(p.getName());
		
		if(mpd == null){
			
			mpd = new MovePacketData(System.currentTimeMillis(), 1);
			
		}
		
		if(mpd.lastloc == null){
			
			mpd.lastloc = new XYZ(p.getLocation());
			
		}
		
		mpd.setAmount(mpd.getAmount() + 1);
		
		if((System.currentTimeMillis() - mpd.getTimeStart()) >= 500){
			
			if(mpd.getAmount() >= (13 + Math.round(Utils.getPing(p) / 100))){
					
				int id = nh.raiseViolationLevel(p.getName(), CheckType.TIMER);
					
				if(id != 0){
						
					Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed Timer! Sent more packets than expected. VL " + id);
						
				}
				p.teleport(mpd.lastloc.toLocation(p.getLocation().getPitch(), p.getLocation().getYaw()), TeleportCause.PLUGIN);
				mpd.reset(new XYZ(p.getLocation()));
				return 0;
				
			}else{
				
				mpd.reset(new XYZ(p.getLocation()));
				
			}
			
		}
		
		this.movepackets.put(p.getName(), mpd);
		return 0;
		
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
	
	public boolean isReallyOnGround(Location loc)
	{
		if(loc.getBlock().getRelative(BlockFace.DOWN).getType().isSolid()){
			double dv = loc.getY() % 1;
			if(dv == 0){
				
				return true;
				
			}else{
				
				//Bukkit.broadcastMessage(dv + "");
				
				if(dv <= 0.1){
					
					return true;
				
				}else{
						
					return false;
					
				}
				
			}
		}else{
			
			return false;
		
		}
		
	}
	
}
