package me.johnnywoof.check.block;

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
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class FastBreak extends Check{

	private final HashMap<String, Long> lastBreak = new HashMap<String, Long>();
	
	public FastBreak(Variables vars, CheckType ct) {
		super(vars, ct, DetectionType.BLOCK);
	}

	@Override
	public int runBlockCheck(Player p, Block b, BlockFace bf, long ls, int aid){
		
		if(p == null || b == null || aid != 0){
			return 0;
		}
		
		long diff = (System.currentTimeMillis() - this.getLastBreak(p.getName()));
		
		this.lastBreak.put(p.getName(), System.currentTimeMillis());
		
		//Start better block visible check
		
		//TODO Account for blocks that can be broken instantly
		
		if(Utils.instantBreak(b.getType()) || p.getGameMode() == GameMode.CREATIVE){
			
			if(p.getGameMode() == GameMode.CREATIVE){
				
				if(diff <= 200){
					
					int id = this.vars.raiseViolationLevel(CheckType.SPEED_BREAK, p);
					
					ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.SPEED_BREAK, p);
					
					Bukkit.getServer().getPluginManager().callEvent(vte);
					
					if(!vte.isCancelled()){
					
						if(id != 0){
							
							String message = Setting.speedbreakmes;
							
							message = message.replaceAll(".name.", ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN);
							message = message.replaceAll(".vl.", id + "");

							Utils.messageAdmins(message);
							
						}
						return 1;
					
					}
					
				}
				
			}
			
		}else{
		
			if(!Utils.canSeeBlock(p, b)){
				
				int id = this.vars.raiseViolationLevel(CheckType.VISIBLE, p);
				
				ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.VISIBLE, p);
				
				Bukkit.getServer().getPluginManager().callEvent(vte);
				
				if(!vte.isCancelled()){
				
					if(id != 0){
						
						String message = Setting.blockvisiblebreak;
						
						message = message.replaceAll(".name.", ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN);
						message = message.replaceAll(".vl.", id + "");

						Utils.messageAdmins(message);
						
					}
					return 4;
				
				}
				
			}
		
		}
		
		//End
		
		return 0;
		
	}
	
	private long getLastBreak(String v){
		
		if(this.lastBreak.containsKey(v)){
			
			return this.lastBreak.get(v);
			
		}else{
			
			return 0;
			
		}
		
	}
	
}
