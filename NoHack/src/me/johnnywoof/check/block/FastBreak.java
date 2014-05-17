package me.johnnywoof.check.block;

import me.johnnywoof.NoHack;
import me.johnnywoof.Setting;
import me.johnnywoof.Variables;
import me.johnnywoof.check.Check;
import me.johnnywoof.check.CheckType;
import me.johnnywoof.check.DetectionType;
import me.johnnywoof.check.Violation;
import me.johnnywoof.event.ViolationTriggeredEvent;
import me.johnnywoof.util.Utils;
import me.johnnywoof.util.XYZ;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class FastBreak extends Check{

	public FastBreak(Variables vars, CheckType ct) {
		super(vars, ct, DetectionType.BREAK);
	}

	@Override
	public int run(Player p, Location from, Location to, long ls, LivingEntity e, double damage, Block b, BlockFace bf, String mes, boolean blockmove, boolean onladder, boolean up, boolean inwater, double yd, double md, XYZ lg){
		
		//Start better block visible check
		
		//TODO Make this less sensitive somehow
		if(!Utils.canSeeBlock(p, b)){
			
			int id = this.vars.raiseViolationLevel(CheckType.VISIBLE, p);
			
			ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.VISIBLE, p);
			
			Bukkit.getServer().getPluginManager().callEvent(vte);
			
			if(!vte.isCancelled()){
				
				if(id != 0){
					
					String message = Setting.blockvisiblebreak;
					
					message = message.replaceAll(".name.", ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN);
					message = message.replaceAll(".vl.", id + "");
					
					if(NoHack.tps <= 17 && id > 50){
						
						Violation vio = this.vars.getViolation(p.getName());
						
						vio.resetLevel(CheckType.VISIBLE, p);
						
						this.vars.setViolation(p.getName(), vio);
						
						p.kickPlayer(ChatColor.RED + "Block breaking out of sync!");
						
					}

					Utils.messageAdmins(message);
					
				}
				return 1;
			
			}
			
		}
		
		//End
		
		return 0;
		
	}
	
}
