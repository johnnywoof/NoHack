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
import org.bukkit.entity.Player;

public class InventoryClick extends Check{

	private final HashMap<String, Long> lastclick = new HashMap<String, Long>();
	
	public InventoryClick(Variables vars, CheckType ct) {
		super(vars, ct, DetectionType.INVENTORY);
	}
	
	@Override
	public int runInventoryCheck(Player p){
		
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
		
		long diff = (System.currentTimeMillis() - this.getLastClicked(p.getName()));
		
		if(diff <= Setting.fcs){
			
			int id = this.vars.raiseViolationLevel(CheckType.FASTCLICK, p);
			
			ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.FASTCLICK, p);
			
			Bukkit.getServer().getPluginManager().callEvent(vte);
			
			this.lastclick.put(p.getName(), System.currentTimeMillis());
			
			if(!vte.isCancelled()){
			
				if(id != 0){
					
					Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed FastClick! Interacted with a container too fast. VL " + id);
					
				}
				
				return 1;
			
			}
			
		}
		
		this.lastclick.put(p.getName(), System.currentTimeMillis());
		
		return 0;
		
	}
	
	private long getLastClicked(String v){
		
		if(this.lastclick.containsKey(v)){
			
			return this.lastclick.get(v);
			
		}else{
			
			return 0;
			
		}
		
	}

}
