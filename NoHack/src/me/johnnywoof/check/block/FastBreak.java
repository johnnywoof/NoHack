package me.johnnywoof.check.block;

import me.johnnywoof.Setting;
import me.johnnywoof.Variables;
import me.johnnywoof.check.Check;
import me.johnnywoof.check.CheckType;
import me.johnnywoof.check.DetectionType;
import me.johnnywoof.event.ViolationTriggeredEvent;
import me.johnnywoof.util.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class FastBreak extends Check{

	public FastBreak(Variables vars, CheckType ct) {
		super(vars, ct, DetectionType.BLOCK);
	}

	@Override
	public int runBlockCheck(Player p, Block b, BlockFace bf, long ls, int aid){
		
		if(p == null || b == null || aid != 0){
			return 0;
		}
		
		//Start better block visible check
		
		//TODO Account for blocks that can be broken instantly
		
		if(Utils.instantBreak(b.getType())){
			
			
			
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
	
}
