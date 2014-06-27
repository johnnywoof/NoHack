package me.johnnywoof.checks;

import java.util.HashMap;

import me.johnnywoof.Setting;
import me.johnnywoof.Variables;
import me.johnnywoof.check.CheckType;
import me.johnnywoof.event.ViolationTriggeredEvent;
import me.johnnywoof.util.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public class BlockCheck {

	private Variables vars;
	
	private final HashMap<String, Long> lastBreak = new HashMap<String, Long>();
	
	public BlockCheck(Variables vars){
		
		this.vars = vars;
		
	}
	
	public int runBlockChecks(Player p, Block b, long ls){
		
		if(p == null || b == null){
			return 0;
		}
		
		//****************Start NoSwing******************
		
		if((System.currentTimeMillis() - ls) >= Setting.noswingblock){
			
			int id = this.vars.raiseViolationLevel(CheckType.NOSWING, p);
			
			ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.NOSWING, p);
			
			Bukkit.getServer().getPluginManager().callEvent(vte);
			
			if(!vte.isCancelled()){
			
				if(id != 0){
					
					String message = Setting.noswingmes;
					
					message = message.replaceAll(".name.", ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN);
					message = message.replaceAll(".vl.", id + "");

					Utils.messageAdmins(message);
					
				}
				return 1;
			
			}
			
		}
		
		//****************End NoSwing******************
		
		//****************Start FastBreak******************
		
		long diff = (System.nanoTime() - this.getLastBreak(p.getName()));
		
		this.lastBreak.put(p.getName(), System.nanoTime());
		
		if(!Setting.useplib){
			
			//TODO Add fastbreak check for non-protocollib
			
		}
		
		//****************End FastBreak******************
		
		//****************Start Block Visible & SpeedBreak******************
		
		//TODO Account for blocks that can be broken instantly
		
		if(Utils.instantBreak(b.getType()) || p.getGameMode() == GameMode.CREATIVE){
			
			if(true){
				
				if(diff < 360000){
					
					if(Setting.debug){
						
						Bukkit.broadcastMessage("Diff: " + diff);
						
					}
					
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
			
	    	BlockIterator bl = new BlockIterator(p, 8);
	    	
	    	double md = 1;
	    	
	    	boolean goodie = false;
	    	
	    	while(bl.hasNext()){
	    		
	    		double d = bl.next().getLocation().distanceSquared(b.getLocation());
	    		
	    		if(d <= md){
	    			
	    			goodie = true;
	    			break;
	    			
	    		}
	    		
	    	}
	    	
	    	bl = null;
	    	
	    	if(!goodie){
	    		
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
		
		//****************End Block Visible******************
		
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
