package me.johnnywoof.check.interact;

import java.util.HashMap;

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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class FastInteract extends Check{

	private final HashMap<String, Long> lastinteractright = new HashMap<String, Long>();
	private final HashMap<String, Long> lastinteractleft = new HashMap<String, Long>();
	
	public FastInteract(Variables vars, CheckType ct) {
		super(vars, ct, DetectionType.INVENTORY);
	}
	
	public boolean checkCustom(PlayerInteractEvent event, Player p){
		
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
				
				if(diff <= 147){
					
					int id = this.vars.raiseViolationLevel(CheckType.FAST_INTERACT, p);
					
					ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.FAST_INTERACT, p);
					
					Bukkit.getServer().getPluginManager().callEvent(vte);
					
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
				
				if(!Utils.canSee(p, event.getClickedBlock().getRelative(event.getBlockFace()).getLocation()) && !new XYZ(event.getClickedBlock().getLocation()).equalsLoc(new XYZ(p.getEyeLocation().getBlock().getLocation()))){
					
					int id = this.vars.raiseViolationLevel(CheckType.VISIBLE, p);
					
					ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.VISIBLE, p);
					
					Bukkit.getServer().getPluginManager().callEvent(vte);
					
					if(!vte.isCancelled()){
					
						if(id != 0){
							
							Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed Visible! Tried to interact with a block out of sight. VL " + id);					
						}
						return true;
					
					}
					
				}
			
			}
		
		}else if(event.hasBlock() && event.getAction() == Action.LEFT_CLICK_BLOCK){
			
			long diff = 0;
			
			if(this.lastinteractleft.containsKey(p.getName())){
				
				diff = (System.nanoTime() - this.lastinteractleft.get(p.getName()));
				
				this.lastinteractleft.put(p.getName(), System.nanoTime());
				
				if(diff <= 190000){		
					
					int id = this.vars.raiseViolationLevel(CheckType.FAST_INTERACT, p);
					
					ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.FAST_INTERACT, p);
					
					Bukkit.getServer().getPluginManager().callEvent(vte);
					
					if(!vte.isCancelled()){
					
						if(id != 0){
							
							//TODO Add block breaking out of sync kick
							
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
	
	@Override
	@Deprecated
	public int run(Player p, Location from, Location to, long ls, LivingEntity e, double damage, Block b, BlockFace bf, String mes, boolean blockmove, boolean onladder, boolean up, boolean inwater, double yd, double md, XYZ lg){
		
		return 0;
		
	}

}
