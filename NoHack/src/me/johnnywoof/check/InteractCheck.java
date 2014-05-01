package me.johnnywoof.check;

import java.util.HashMap;

import me.johnnywoof.CheckType;
import me.johnnywoof.NoHack;
import me.johnnywoof.Violation;
import me.johnnywoof.event.ViolationTriggeredEvent;
import me.johnnywoof.util.Utils;
import me.johnnywoof.util.XYZ;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class InteractCheck {

	final HashMap<String, Long> lastlaunch = new HashMap<String, Long>();
	final HashMap<String, Long> lastclick = new HashMap<String, Long>();
	final HashMap<String, Long> lastinteractright = new HashMap<String, Long>();
	final HashMap<String, Long> lastinteractleft = new HashMap<String, Long>();
	
	public boolean checkInventoryClick(Player p, NoHack nh){
		
		if(p.isBlocking() || p.isSneaking() || p.isSprinting() || p.isSleeping()){
			
			int id = nh.raiseViolationLevel(p.getName(), CheckType.IMPOSSIBLE, p);
			
			ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.IMPOSSIBLE, p);
			
			nh.getServer().getPluginManager().callEvent(vte);
			
			if(!vte.isCancelled()){
			
				if(id != 0){
					
					Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed Impossible! Tried to do illegal clicks. VL " + id);
					
				}
				
				return true;
			
			}
			
		}
		
		long diff = (System.currentTimeMillis() - this.getLastClicked(p.getName()));
		
		if(diff <= 50){
			
			int id = nh.raiseViolationLevel(p.getName(), CheckType.FASTCLICK, p);
			
			ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.FASTCLICK, p);
			
			nh.getServer().getPluginManager().callEvent(vte);
			
			this.lastclick.put(p.getName(), System.currentTimeMillis());
			
			if(!vte.isCancelled()){
			
				if(id != 0){
					
					Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed FastClick! Interacted with a container too fast. VL " + id);
					
				}
				
				return true;
			
			}
			
		}else{
			
			if((System.currentTimeMillis() - nh.fc.getLastAttackTime(p.getName())) <= 600){
				
				int id = nh.raiseViolationLevel(p.getName(), CheckType.AUTOSOUP, p);
				
				ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.AUTOSOUP, p);
				
				nh.getServer().getPluginManager().callEvent(vte);
				
				if(!vte.isCancelled()){
					
					if(id != 0){
						
						Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed AutoSoup! Tried to click inventory while fighting. VL " + id);
						
					}
					
					this.lastclick.put(p.getName(), System.currentTimeMillis());
					return true;
				
				}
				
			}
			
		}
		
		this.lastclick.put(p.getName(), System.currentTimeMillis());
		
		return false;
		
	}
	
	public boolean checkEntityInteract(NoHack nh, Entity e, Player p){
		
		if(!p.hasLineOfSight(e)){
			
			int id = nh.raiseViolationLevel(p.getName(), CheckType.VISIBLE, p);
			
			ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.VISIBLE, p);
			
			nh.getServer().getPluginManager().callEvent(vte);
			
			if(!vte.isCancelled()){
			
				if(id != 0){
					
					Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed Visible! Tried to interact with an entity out of sight. VL " + id);					
				}
				return true;
			
			}
			
		}
		
		return false;
		
	}
	
	public boolean checkInteract(NoHack nh, PlayerInteractEvent event, Player p){
		
		if(event.hasBlock() && event.getAction() == Action.RIGHT_CLICK_BLOCK){
		
			if(event.getClickedBlock().getType().toString().toLowerCase().contains("rail") || event.getClickedBlock().isLiquid()){
				
				ItemStack i = event.getPlayer().getItemInHand();
				
				if(i != null){
					
					Material m = i.getType();
					
					if(m == Material.MINECART || m == Material.HOPPER_MINECART || m == Material.COMMAND_MINECART ||
							m == Material.BOAT || m == Material.STORAGE_MINECART || m == Material.POWERED_MINECART ||
							m == Material.EXPLOSIVE_MINECART || m == Material.BOAT){
						
						return false;
						
					}
					
				}
			
			}
			
			long diff = 0;
			
			if(this.lastinteractright.containsKey(p.getName())){
				diff = (System.currentTimeMillis() - this.lastinteractright.get(p.getName()));
				
				this.lastinteractright.put(p.getName(), System.currentTimeMillis());
				
				if(diff <= 150){
					
					int id = nh.raiseViolationLevel(p.getName(), CheckType.FAST_INTERACT, p);
					
					ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.FAST_INTERACT, p);
					
					nh.getServer().getPluginManager().callEvent(vte);
					
					if(!vte.isCancelled()){
					
						if(id != 0){
							
							Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed Fast Interact! Diff " + diff + ". VL " + id);
							
						}
						return true;
					
					}
					
				}
			}else{
				
				this.lastinteractright.put(p.getName(), System.currentTimeMillis());
				
			}

			Material m = event.getClickedBlock().getType();
			
			if(m == Material.CHEST || m == Material.TRAPPED_CHEST || m == Material.BREWING_STAND || m == Material.ENDER_CHEST || m == Material.ANVIL || m == Material.TRAP_DOOR || m == Material.IRON_DOOR_BLOCK || m == Material.WOODEN_DOOR
					|| m == Material.BEACON || m == Material.BURNING_FURNACE || m == Material.CAKE_BLOCK || m == Material.CAULDRON || m == Material.BED_BLOCK || m == Material.COMMAND || m == Material.DIODE_BLOCK_OFF || m == Material.DIODE_BLOCK_ON || m == Material.DISPENSER ||
					m == Material.WORKBENCH || m == Material.WOOD_BUTTON || m == Material.NOTE_BLOCK || m == Material.STONE_BUTTON || m == Material.JUKEBOX || m == Material.HOPPER || m == Material.DRAGON_EGG || m == Material.DROPPER || m == Material.FENCE_GATE || 
					m == Material.FURNACE || m == Material.ENCHANTMENT_TABLE){
				
				if(!Utils.canSee(p, event.getClickedBlock().getRelative(event.getBlockFace()).getLocation())){
					
					int id = nh.raiseViolationLevel(p.getName(), CheckType.VISIBLE, p);
					
					ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.VISIBLE, p);
					
					nh.getServer().getPluginManager().callEvent(vte);
					
					if(!vte.isCancelled()){
					
						if(id != 0){
							
							Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed Visible! Tried to interact with a block out of sight. VL " + id);					
						}
						return true;
					
					}
					
				}
			
			}
		
		}else if(event.hasBlock() && event.getAction() == Action.LEFT_CLICK_BLOCK){
			
			nh.setCurrentBlock(p.getName(), new XYZ(event.getClickedBlock().getLocation()));
			
			long diff = 0;
			
			if(this.lastinteractleft.containsKey(p.getName())){
				
				diff = (System.nanoTime() - this.lastinteractleft.get(p.getName()));
				
				this.lastinteractleft.put(p.getName(), System.nanoTime());
				
				if(diff <= 190000){		
					
					int id = nh.raiseViolationLevel(p.getName(), CheckType.FAST_INTERACT, p);
					
					ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.FAST_INTERACT, p);
					
					nh.getServer().getPluginManager().callEvent(vte);
					
					if(!vte.isCancelled()){
					
						if(id != 0){
							
							if(id > 50){
								
								Violation vio = nh.getViolation(p.getName());
								
								vio.resetLevel(CheckType.FAST_INTERACT, p);
								
								nh.setViolation(p.getName(), vio);
								
								p.kickPlayer(ChatColor.RED + "Block breaking out of sync!");
								
							}
							
							Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed Fast Interact! Diff " + diff + ". VL " + id);
							
						}
						return true;
					
					}
					
				}
			}else{
				
				this.lastinteractleft.put(p.getName(), System.nanoTime());
				
			}
			
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
						
						int id = nh.raiseViolationLevel(p.getName(), CheckType.FAST_THROW, p);
						
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
