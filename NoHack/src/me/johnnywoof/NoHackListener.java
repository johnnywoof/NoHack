package me.johnnywoof;

import me.johnnywoof.util.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;

public class NoHackListener implements Listener {

	private NoHack nh;
	
	public NoHackListener(NoHack nh){
		
		this.nh = nh;
		
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onClick(InventoryClickEvent event){
		
		if(event.getWhoClicked().getType() == EntityType.PLAYER){
			
			Player p = (Player) event.getWhoClicked();
			
			if(nh.ic.checkInventoryClick(p, nh)){
				
				event.setCancelled(true);
				
			}
			
			p = null;
			
		}
		
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent event){
		
		if(event.hasBlock()){
			
			if(nh.bc.checkInteract(nh, event.getClickedBlock(), event.getBlockFace(), event.getPlayer())){
				
				event.setCancelled(true);
				
			}
			
		}
		
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onLogin(PlayerLoginEvent event){
		
		//Special mod-disable codes
		event.getPlayer().sendMessage("§3 §9 §2 §0 §0 §2");
		event.getPlayer().sendMessage("§3 §9 §2 §0 §0 §1");
		event.getPlayer().sendMessage("§3 §9 §2 §0 §0 §3");
		event.getPlayer().sendMessage("§f §f §2 §0 §4 §8");
		event.getPlayer().sendMessage("§f §f §4 §0 §9 §6");
		event.getPlayer().sendMessage("§f §f §1 §0 §2 §4");
		event.getPlayer().sendMessage("§0§0§1§e§f");
		event.getPlayer().sendMessage("§0§0§2§3§4§5§6§7§e§f");
		event.getPlayer().sendMessage("§0§0§1§f§e");
		event.getPlayer().sendMessage("§0§0§2§f§e");
		event.getPlayer().sendMessage("§0§0§3§4§5§6§7§8§f§e");
		event.getPlayer().sendMessage("§0§1§0§1§2§f§f");
		event.getPlayer().sendMessage("§0§1§3§4§f§f");
		event.getPlayer().sendMessage("§0§1§5§f§f");
		event.getPlayer().sendMessage("§0§1§6§f§f");
		event.getPlayer().sendMessage("§0§1§8§9§a§b§f§f");
		event.getPlayer().sendMessage("§0§1§7§f§f");
		
		if(nh.deniedlogin.containsKey(Utils.getIP(event.getPlayer()))){
			
			long fut = nh.deniedlogin.get(Utils.getIP(event.getPlayer()));
			
			if(fut > System.currentTimeMillis()){
			
				event.disallow(Result.KICK_OTHER, "Please wait " + Math.round((fut - System.currentTimeMillis()) / 1000) + " seconds before joining again.");
			
			}else{
				
				nh.deniedlogin.remove(Utils.getIP(event.getPlayer()));
				
			}
			
		}
		
	}
	
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
	public void onFood(FoodLevelChangeEvent event){
		
		event.setFoodLevel(20);
		((Player) event.getEntity()).setSaturation(20);
		
	}
	
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerAttack(EntityDamageByEntityEvent event){
		
		if(event.getDamager().getType() == EntityType.PLAYER){
			
			if(event.getEntity() instanceof LivingEntity){
				
				LivingEntity e = ((LivingEntity) event.getEntity());
				
				if(!e.isDead()){
				
					Player p = ((Player) event.getDamager());
					
					if(nh.fc.check(nh, nh.getLastSwong(p.getName()), p, e, event.getDamage())){
						
						e.setNoDamageTicks(20);
						event.setCancelled(true);
						
					}
					
					p = null;
				
				}
				
				e = null;
				
			}
			
		}
		
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event){
		
		if(nh.bc.checkBreak(nh, nh.getLastSwong(event.getPlayer().getName()), event.getBlock(), event.getPlayer())){
			
			event.setCancelled(true);
			
		}
		
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event){
		
		if(nh.bc.checkPlace(nh, event.getBlock(), event.getBlockAgainst(), event.getPlayer())){
			
			event.setCancelled(true);
			
		}
		
	}
	
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBedLeaveEvent(PlayerBedLeaveEvent event){
		
		if(event.getBed().getType() != Material.BED_BLOCK){
			
			Bukkit.broadcastMessage("Detected spoof bed leave");
			
		}
		
	}
	
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
	public void onProjectile(ProjectileLaunchEvent event){
		
		if(nh.ic.checkProjectile(nh, event.getEntity())){
			
			event.setCancelled(true);
			
		}
		
	}
	
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBedLeaveEvent(PlayerBedEnterEvent event){
		
		if(event.getBed().getType() != Material.BED_BLOCK){
			
			Bukkit.broadcastMessage("Detected spoof bed enter");
			
		}
		
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent event){
		
		if(nh.cc.check(nh, event.getPlayer(), event.getMessage())){
			
			event.setCancelled(true);
			
		}
		
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerAnimationEvent(org.bukkit.event.player.PlayerAnimationEvent event){
		
		if(event.getAnimationType() == PlayerAnimationType.ARM_SWING){
		
			nh.updateLastSwong(event.getPlayer().getName());
		
		}
		
	}
	
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
	public void onMove(PlayerMoveEvent event){
		
		int id = nh.mc.checkMove(nh, event.getPlayer(), event.getFrom(), event.getTo());
		
		if(id == 1){
			
			event.getPlayer().teleport(event.getFrom());
			
		}else if(id == 2){
			
			event.setCancelled(true);
			
		}else if(id == 3){
			
			
			
		}
		
	}
	
}
