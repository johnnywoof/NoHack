package me.johnnywoof.check;

import me.johnnywoof.CheckType;
import me.johnnywoof.NoHack;
import me.johnnywoof.util.Utils;
import me.johnnywoof.util.XYZ;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BlockCheck {
	
	public boolean checkPlace(NoHack nh, Block b, Block pa, Player p){
		
		if(pa.isLiquid()){
			
			int id = nh.raiseViolationLevel(p.getName(), CheckType.IMPOSSIBLE);
			
			if(id != 0){
				
				Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed Impossible! Placed a block on a liquid. VL " + id);
				
			}
			return true;
			
		}
		return false;
		
	}
	
	public boolean checkBreak(NoHack nh, long ls, Block b, Player p){
		
		XYZ c = nh.getCurrentBlock(p.getName());
		
		if(c != null){
			
			if(c.x != b.getX() || c.y != b.getY() || c.z != b.getZ()){
				
				int id = nh.raiseViolationLevel(p.getName(), CheckType.VISIBLE);
				
				if(id != 0){
					
					Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed Visible! Tried to break another block than the one interacting with. VL " + id);					
				}
				return true;
				
			}
			
		}
		
		if((System.currentTimeMillis() - ls) >= 2500){
			
			int id = nh.raiseViolationLevel(p.getName(), CheckType.NOSWING);
			
			if(id != 0){
				
				Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed NoSwing! Difference was " + (System.currentTimeMillis() - ls) + ". VL " + id);
				
			}
			return true;
			
		}
		return false;
		
	}
	
	/*/**
	 * @param b = airblock
	 * 
	private boolean checkFullbright(Block b){
		
		return (b.getLightLevel() <= 0);
		
	}*/
	
}
