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

public class NoSwingBlock extends Check{

	public NoSwingBlock(Variables vars, CheckType ct) {
		super(vars, ct, DetectionType.BLOCK);
	}
	
	@Override
	public int runBlockCheck(Player p, Block clicked, BlockFace bf, long ls, int aid){
		
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
		
		return 0;
		
	}

}
