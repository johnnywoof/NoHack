package me.johnnywoof.check.interact;

import java.util.HashMap;

import me.johnnywoof.Setting;
import me.johnnywoof.Variables;
import me.johnnywoof.check.Check;
import me.johnnywoof.check.CheckType;
import me.johnnywoof.check.DetectionType;
import me.johnnywoof.event.ViolationTriggeredEvent;
import me.johnnywoof.util.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;

public class InventoryClick extends Check{

	private final HashMap<String, Long> lastclick = new HashMap<String, Long>();
	private final HashMap<String, Long> lastviolation = new HashMap<String, Long>();
	
	public InventoryClick(Variables vars, CheckType ct) {
		super(vars, ct, DetectionType.INVENTORY);
	}
	
	@Override
	public int runInventoryCheck(Player p, Inventory inv, InventoryAction ia, InventoryClickEvent event){
		
		if(p.isBlocking() || p.isSneaking() || p.isSprinting() || p.isSleeping()){
			
			int id = this.vars.raiseViolationLevel(CheckType.IMPOSSIBLE, p);
			
			ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.IMPOSSIBLE, p);
			
			Bukkit.getServer().getPluginManager().callEvent(vte);
			
			if(!vte.isCancelled()){
			
				if(id != 0){
					
					String message = Setting.impossibleclick;
					
					message = message.replaceAll(".name.", ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN);
					message = message.replaceAll(".vl.", id + "");

					Utils.messageAdmins(message);
					
				}
				return 1;
			
			}
			
		}
		
		long rdif = (System.currentTimeMillis() - this.getLastViolation(p.getName()));
		
		if(rdif <= 2000){
			
			return 1;//Prevent abuse to bypass check
			
		}
		
		if(p.getGameMode() == GameMode.CREATIVE){
			
			if(inv.getType() == InventoryType.PLAYER || inv.getType() == InventoryType.CREATIVE){
				
				if(ia == InventoryAction.PLACE_ALL){
					
					if(event.getSlotType() != SlotType.OUTSIDE){//We want to check for fastdrops which in turn = lag
						
						this.lastviolation.put(p.getName(), System.currentTimeMillis());
						
						return 0;
						
					}
					
				}
				
			}
			
		}
		
		if(Setting.debug){
			
			Bukkit.broadcastMessage("Type: " + inv.getType() + "; Action: " + ia.toString().toLowerCase());
			
		}
		
		long diff = (System.currentTimeMillis() - this.getLastClicked(p.getName()));
		
		if(diff <= Setting.fcs){
			
			int id = this.vars.raiseViolationLevel(CheckType.FASTCLICK, p);
			
			ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.FASTCLICK, p);
			
			Bukkit.getServer().getPluginManager().callEvent(vte);
			
			this.lastclick.put(p.getName(), System.currentTimeMillis());
			
			if(!vte.isCancelled()){
				
				this.lastviolation.put(p.getName(), System.currentTimeMillis());
			
				if(id != 0){
					
					Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed FastClick! Interacted with a container too fast. VL " + id);
					
				}
				
				return 1;
			
			}
			
		}
		
		this.lastclick.put(p.getName(), System.currentTimeMillis());
		
		return 0;
		
	}
	
	private long getLastViolation(String v){
		
		if(this.lastviolation.containsKey(v)){
			
			return this.lastviolation.get(v);
			
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
