package me.johnnywoof.check.block;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.johnnywoof.Variables;
import me.johnnywoof.check.Check;
import me.johnnywoof.check.CheckType;
import me.johnnywoof.check.DetectionType;
import me.johnnywoof.event.ViolationTriggeredEvent;
import me.johnnywoof.util.Utils;
import me.johnnywoof.util.XYZ;

public class NoSwingBlock extends Check{

	public NoSwingBlock(Variables vars, CheckType ct) {
		super(vars, ct, DetectionType.BREAK);
	}
	
	@Override
	public int run(Player p, Location from, Location to, long ls, LivingEntity e, double damage, Block b, BlockFace bf, String mes, boolean blockmove, boolean onladder, boolean up, boolean inwater, double yd, double md, XYZ lg){
		
		if((System.currentTimeMillis() - ls) >= 2500){
			
			int id = this.vars.raiseViolationLevel(CheckType.NOSWING, p);
			
			ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.NOSWING, p);
			
			Bukkit.getServer().getPluginManager().callEvent(vte);
			
			if(!vte.isCancelled()){
			
				if(id != 0){
					
					String message = this.vars.noswingmes;
					
					message = message.replaceAll("%name%", ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN);
					message = message.replaceAll("%vl%", id + "");

					Utils.messageAdmins(message);
					
				}
				return 1;
			
			}
			
		}
		
		return 0;
		
	}

}
