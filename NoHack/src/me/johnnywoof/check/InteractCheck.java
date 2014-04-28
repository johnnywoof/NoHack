package me.johnnywoof.check;

import java.util.HashMap;

import me.johnnywoof.CheckType;
import me.johnnywoof.NoHack;
import me.johnnywoof.Violation;
import me.johnnywoof.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractCheck {

	final HashMap<String, Long> lastlaunch = new HashMap<String, Long>();
	final HashMap<String, Long> lastclick = new HashMap<String, Long>();
	final HashMap<String, Long> lastinteractright = new HashMap<String, Long>();
	final HashMap<String, Long> lastinteractleft = new HashMap<String, Long>();
	
	public boolean checkInventoryClick(Player p, NoHack nh){
		
		if(p.isBlocking() || p.isSneaking() || p.isSprinting() || p.isSleeping()){
			
			int id = nh.raiseViolationLevel(p.getName(), CheckType.IMPOSSIBLE);
			
			if(id != 0){
				
				Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed Impossible! Tried to do illegal clicks. VL " + id);
				
			}
			
			return true;
			
		}
		
		long diff = (System.currentTimeMillis() - this.getLastClicked(p.getName()));
		
		if(diff <= 50){
			
			int id = nh.raiseViolationLevel(p.getName(), CheckType.FASTCLICK);
			
			if(id != 0){
				
				Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed FastClick! Interacted with a container too fast. VL " + id);
				
			}
			
			this.lastclick.put(p.getName(), System.currentTimeMillis());
			return true;
			
		}else{
			
			if((System.currentTimeMillis() - nh.fc.getLastAttackTime(p.getName())) <= 600){
				
				int id = nh.raiseViolationLevel(p.getName(), CheckType.AUTOSOUP);
				
				if(id != 0){
					
					Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed AutoSoup! Tried to click inventory while fighting. VL " + id);
					
				}
				
				this.lastclick.put(p.getName(), System.currentTimeMillis());
				return true;
				
			}
			
		}
		
		this.lastclick.put(p.getName(), System.currentTimeMillis());
		
		return false;
		
	}
	
	public boolean checkInteract(NoHack nh, PlayerInteractEvent event, Player p){
		
		if(event.hasBlock() && event.getAction() == Action.RIGHT_CLICK_BLOCK){
		
			long diff = 0;
			
			if(this.lastinteractright.containsKey(p.getName())){
				diff = (System.currentTimeMillis() - this.lastinteractright.get(p.getName()));
				
				this.lastinteractright.put(p.getName(), System.currentTimeMillis());
				
				if(diff <= 180){
					
					int id = nh.raiseViolationLevel(p.getName(), CheckType.FAST_INTERACT);
					
					if(id != 0){
						
						Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed Fast Interact! Diff " + diff + ". VL " + id);
						
					}
					return true;
					
				}
			}
			
			this.lastinteractright.put(p.getName(), System.currentTimeMillis());
		
		}else if(event.hasBlock() && event.getAction() == Action.LEFT_CLICK_BLOCK){
			
			long diff = 0;
			
			if(this.lastinteractleft.containsKey(p.getName())){
				
				diff = (System.currentTimeMillis() - this.lastinteractleft.get(p.getName()));
				
				this.lastinteractleft.put(p.getName(), System.currentTimeMillis());
				
				if(diff <= 4){		
					
					int id = nh.raiseViolationLevel(p.getName(), CheckType.FAST_INTERACT);
					
					if(id != 0){
						
						if(id > 50){
							
							Violation vio = nh.getViolation(p.getName());
							
							vio.resetLevel(CheckType.FAST_INTERACT);
							
							nh.setViolation(p.getName(), vio);
							
							p.kickPlayer(ChatColor.RED + "Block breaking out of sync!");
							
						}
						
						Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed Fast Interact! Diff " + diff + ". VL " + id);
						
					}
					return true;
					
				}
			}
			
			this.lastinteractleft.put(p.getName(), System.currentTimeMillis());
			
		}
		
		return false;
		
	}
	
	public boolean checkProjectile(NoHack nh, Projectile pro){
		
		if(pro.getShooter() != null){
			
			if(pro.getShooter() instanceof Player){
			
				Player p = (Player) pro.getShooter();
				
				if(p != null){
				
					long diff = (System.currentTimeMillis() - this.getLastLaunched(p.getName()));
					
					this.lastlaunch.put(p.getName(), System.currentTimeMillis());
					
					if(diff <= 180){
						
						int id = nh.raiseViolationLevel(p.getName(), CheckType.FAST_THROW);
						
						if(id != 0){
							
							Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed Fast Throw! Speed was " + diff + ". VL " + id);
							
						}
						return true;
						
					}
					
				}
			
			}
		
		}
		
		return false;
		
	}
	
	private long getLastLaunched(String v){
		
		if(this.lastlaunch.containsKey(v)){
			
			return this.lastlaunch.get(v);
			
		}else{
			
			return 0;
			
		}
		
	}
	
	private long getLastClicked(String v){
		
		if(this.lastclick.containsKey(v)){
			
			return this.lastclick.get(v);
			
		}else{
			
			return 0;
			
		}
		
	}
	
}
