package me.johnnywoof;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import me.johnnywoof.check.Check;
import me.johnnywoof.check.CheckType;
import me.johnnywoof.check.DetectionType;
import me.johnnywoof.event.ViolationChangedEvent;
import me.johnnywoof.event.ViolationTriggeredEvent;
import me.johnnywoof.util.MoveData;
import me.johnnywoof.util.Utils;
import me.johnnywoof.util.XYZ;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
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
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class NoHackListener implements Listener {

	private NoHack nh;
	
	private final HashMap<String, Long> lastHealhed = new HashMap<String, Long>();
	
	public NoHackListener(NoHack nh){
		
		this.nh = nh;
		
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onMount(VehicleEnterEvent event){
		
		if(event.getEntered() instanceof Player){
		
			Player p = (Player) event.getEntered();
			
			MoveData md = nh.vars.getMoveData(p.getName());
			
			md.lastmounting = System.currentTimeMillis();
			
			nh.vars.setMoveData(p.getName(), md);
		
		}
		
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onLog(ViolationChangedEvent event){
		
		File f = new File("hack_logs.txt");
		
		try{
		
			if(!f.exists()){
				
				f.createNewFile();
				
			}
			
			PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(f, true)));
			
			w.println(event.getPlayer().getName() + " failed " + event.getCheckType().toString() + ". VL " + event.getNewLevel() + " TPS " + NoHack.tps);
			
			w.close();
		
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onSneak(PlayerToggleSneakEvent event){
		
		MoveData md = nh.vars.getMoveData(event.getPlayer().getName());
		
		md.sneaktime = System.currentTimeMillis();
		
		md.wassneaking = !event.isSneaking();
		
		nh.vars.setMoveData(event.getPlayer().getName(), md);
		
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onSprint(PlayerToggleSprintEvent event){
		
		MoveData md = nh.vars.getMoveData(event.getPlayer().getName());
		
		md.sprinttime = System.currentTimeMillis();
		
		md.wassprinting = !event.isSprinting();
		
		nh.vars.setMoveData(event.getPlayer().getName(), md);
		
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onClick(InventoryClickEvent event){
		
		if(event.getWhoClicked().getType() == EntityType.PLAYER){
			
			Player p = (Player) event.getWhoClicked();
			
			for(Check c : nh.getChecks()){
				
				if(c.getDetectType() == DetectionType.INVENTORY){
					
					if(c.run(p, null, null, 0, null, 0D, null, null, null, false, false, false, false, 0, 0, null) != 0){
						
						event.setCancelled(true);
						break;
						
					}
					
				}
				
			}
			
			p = null;
			
		}
		
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityRegainHealthEvent(EntityRegainHealthEvent event){
		
		if(event.getRegainReason() != RegainReason.CUSTOM){
			
			if(event.getEntity() instanceof Player){
			
				Player p = (Player) event.getEntity();
				
				long diff = 1000;
				if(this.lastHealhed.containsKey(p.getName())){
					diff = (System.currentTimeMillis() - this.lastHealhed.get(p.getName()));
				}
				
				this.lastHealhed.put(p.getName(), System.currentTimeMillis());
				
				if(diff <= 3800){
					
					int id = nh.vars.raiseViolationLevel(CheckType.GOD_MODE, p);
					
					ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.GOD_MODE, p);
					
					nh.getServer().getPluginManager().callEvent(vte);
					
					if(!vte.isCancelled()){
					
						if(id != 0){
							
							Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed GodMode! Tried to regain health too fast. Diff " + diff + " VL " + id);
							
						}
						event.setCancelled(true);
					
					}
					
				}
				
				p = null;
				
			}
			
		}
		
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent event){
		
		if(event.getAction() != Action.PHYSICAL){
		
			if(nh.fi.checkCustom(event, event.getPlayer())){
				
				event.setCancelled(true);
				event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
				event.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
				
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
		
		if(nh.vars.deniedlogin.containsKey(Utils.getIP(event.getPlayer()))){
			
			long fut = nh.vars.deniedlogin.get(Utils.getIP(event.getPlayer()));
			
			if(fut > System.currentTimeMillis()){
			
				event.disallow(Result.KICK_OTHER, "Please wait " + Math.round((fut - System.currentTimeMillis()) / 1000) + " seconds before joining again.");
			
			}else{
				
				nh.vars.deniedlogin.remove(Utils.getIP(event.getPlayer()));
				
			}
			
		}
		
	}
	
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
	public void onFood(FoodLevelChangeEvent event){
		
		//TODO Remove this.....
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
					
					for(Check c : nh.getChecks()){
						
						if(c.getDetectType() == DetectionType.FIGHT){
							
							if(c.run(p, null, null, nh.vars.getLastSwong(p.getName()), e, event.getDamage(), null, null, null, false, Utils.isOnLadder(p), false, Utils.inWater(p), 0, 0, nh.vars.lastGround(p)) != 0){
								
								e.setNoDamageTicks(20);
								event.setCancelled(true);
								break;
								
							}
							
						}
						
					}
					
					p = null;
				
				}
				
				e = null;
				
			}
			
		}
		
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event){
		
		for(Check c : nh.getChecks()){
			
			if(c.getDetectType() == DetectionType.BREAK){
				
				if(c.run(event.getPlayer(), null, null, nh.vars.getLastSwong(event.getPlayer().getName()), null, 0D, 
						event.getBlock(), null, null, false, Utils.isOnLadder(event.getPlayer()), false, Utils.inWater(event.getPlayer()), 0, 0, nh.vars.lastGround(event.getPlayer())) != 0){
					
					event.setCancelled(true);
					break;
					
				}
				
			}
			
		}
		
	}

	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBedLeaveEvent(PlayerBedLeaveEvent event){
		
		if(event.getBed().getType() != Material.BED_BLOCK){
			
			event.getPlayer().kickPlayer("Go find a real bed!");
			
		}
		
	}
	
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
	public void onProjectile(ProjectileLaunchEvent event){
		
		//TODO Add fast shoot
		
	}
	
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBedLeaveEvent(PlayerBedEnterEvent event){
		
		if(event.getBed().getType() != Material.BED_BLOCK){
			
			event.getPlayer().kickPlayer("Go find a real bed!");
			
		}
		
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent event){
		
		for(Check c : nh.getChecks()){
			
			if(c.getDetectType() == DetectionType.CHAT){
				
				if(c.run(event.getPlayer(), null, null, nh.vars.getLastSwong(event.getPlayer().getName()), null, 0D, 
						null, null, event.getMessage(), false, false, false, false, 0, 0, null) != 0){
					
					event.setCancelled(true);
					break;
					
				}
				
			}
			
		}
		
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerAnimationEvent(org.bukkit.event.player.PlayerAnimationEvent event){
		
		if(event.getAnimationType() == PlayerAnimationType.ARM_SWING){
		
			nh.vars.updateLastSwong(event.getPlayer().getName());
		
		}
		
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)//need to confirm no plugin canceled this.
	public void onPlayerVelocityEvent(final PlayerVelocityEvent event){

		/*final int before = (int) event.getVelocity().distanceSquared(event.getPlayer().getLocation().toVector());

		nh.getServer().getScheduler().runTaskLater(nh, new Runnable(){

			@Override
			public void run() {
				
				if(before == (int) event.getVelocity().distanceSquared(event.getPlayer().getLocation().toVector())){
					
					int id = nh.raiseViolationLevel(event.getPlayer().getName(), CheckType.NOKNOCKBACK);
					
					if(id != 0){
						
						Utils.messageAdmins(ChatColor.YELLOW + "" + event.getPlayer().getName() + "" + ChatColor.GREEN + " failed NoKnockBack! Tried to avoid taking no knockback. VL " + id);					
					}
					
					event.getPlayer().kickPlayer(ChatColor.RED + "You didn't move correctly! Are you hacking?");
					
				}
				
			}
			
		}, 20);*/
		
	}
	
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
	public void onMove(PlayerMoveEvent event){
		
		int id = 0;
		
		double yd = Math.abs((event.getFrom().getY() - event.getTo().getY()));//Vertical speed
		boolean up = ((event.getTo().getY() - event.getFrom().getY()) > 0);//Moving up?
		double md = Utils.getXZDistance(event.getFrom().getX(), event.getTo().getX(), event.getFrom().getZ(), event.getTo().getZ());//Horizontal speed
		boolean inwater = ((CraftPlayer) event.getPlayer()).getHandle().inWater;
		boolean onladder = ((CraftPlayer) event.getPlayer()).getHandle().h_();//Near ladder? NMS ftw!
		
		XYZ lg = nh.vars.lastGround(event.getPlayer());
		
		for(Check c : nh.getChecks()){
			
			if(c.getDetectType() == DetectionType.MOVING){
				
				id = c.run(event.getPlayer(), event.getFrom(), event.getTo(), 0, null, 0D, 
						null, null, null, false, onladder, up, inwater, yd, md, lg);//TODO possible block move boolean?
				
				if(id != 0){
					
					break;
					
				}
				
			}
			
		}
		
		if(id == 1){
			
			event.getPlayer().teleport(event.getFrom());
			
		}else if(id == 2){
			
			event.setCancelled(true);
			
		}else if(id == 3){
			
			event.setTo(nh.vars.lastGround(event.getPlayer()).toLocation(event.getTo().getPitch(), event.getTo().getYaw()));
			
		}else if(id == 4){
			
			Location loc = nh.vars.lastGround(event.getPlayer()).toLocation(event.getTo().getPitch(), event.getTo().getYaw());
			
			double mmd = Double.MAX_VALUE;
			
			for(int x = loc.getBlockX() - 5; x < loc.getBlockX() + 5; x++){
				
				for(int y = loc.getBlockY() - 5; y < loc.getBlockY() + 5; y++){
				
					for(int z = loc.getBlockZ() - 5; z < loc.getBlockZ() + 5; z++){
						
						Block b = loc.getWorld().getBlockAt(x, y, z);
						
						if(b.getType().isSolid()){
							
							if(!b.getRelative(BlockFace.UP).getType().isSolid()){
								
								double d = b.getLocation().distanceSquared(event.getFrom());
								
								if(d < mmd){
									
									event.setTo(new Location(loc.getWorld(), x + 0.5, y + 1, z + 0.5, event.getTo().getYaw(), event.getTo().getPitch()));
									
									mmd = d;
								
								}
								
							}
							
						}
						
					}
				
				}
				
			}
			
		}
		
	}
	
}
