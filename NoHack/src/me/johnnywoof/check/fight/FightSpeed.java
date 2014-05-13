package me.johnnywoof.check.fight;

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
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class FightSpeed extends Check{

	private HashMap<String, Long> lastAttack = new HashMap<String, Long>();
	
	public FightSpeed(Variables vars, CheckType ct) {
		super(vars, ct, DetectionType.FIGHT);
	}
	
	@Override
	public int run(Player p, Location from, Location to, long ls, LivingEntity e, double damage, Block b, BlockFace bf, String mes, boolean blockmove, boolean onladder, boolean up, boolean inwater, double yd, double md, XYZ lg){
		
		if(this.lastAttack.containsKey(p.getName())){
			
			long diff = (System.currentTimeMillis() - this.lastAttack.get(p.getName()));
			
			if(diff <= 90){
				
				this.registerLastAttack(p.getName());
				int id = this.vars.raiseViolationLevel(CheckType.ATTACK_SPEED, p);
				
				ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.ATTACK_SPEED, p);
				
				Bukkit.getServer().getPluginManager().callEvent(vte);
				
				if(!vte.isCancelled()){
				
					if(id != 0){
						
						String message = this.vars.fightspeed;
						
						message = message.replaceAll("%name%", ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN);
						message = message.replaceAll("%vl%", id + "");

						Utils.messageAdmins(message);
						
					}
					return 1;
				
				}
				
			}
			
		}else{
			
			this.registerLastAttack(p.getName());
			
		}
		
		return 0;
		
	}
	
	private void registerLastAttack(String v){
		
		this.lastAttack.put(v, System.currentTimeMillis());
		
	}

}
