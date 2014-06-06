package me.johnnywoof.check.fight;

import me.johnnywoof.Setting;
import me.johnnywoof.Variables;
import me.johnnywoof.check.Check;
import me.johnnywoof.check.CheckType;
import me.johnnywoof.check.DetectionType;
import me.johnnywoof.event.ViolationTriggeredEvent;
import me.johnnywoof.util.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class FightAutoSoup extends Check{

	public FightAutoSoup(Variables vars, CheckType ct) {
		super(vars, ct, DetectionType.FIGHT);
	}
	
	@Override
	public int runAttackCheck(Player p, LivingEntity e, long ls){
		
		if(p.getInventory().getViewers().size() > 0){
			
			for(HumanEntity he : p.getInventory().getViewers()){//Prevent false checking from /invsee
				
				if(he.getName().equals(p.getName())){//We have the right one, attacking while inventory is open.
					
					int id = this.vars.raiseViolationLevel(CheckType.AUTOSOUP, p);
					
					ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.AUTOSOUP, p);
					
					Bukkit.getServer().getPluginManager().callEvent(vte);
					
					if(!vte.isCancelled()){
					
						if(id != 0){
							
							String message = Setting.autosoupmes;
							
							message = message.replaceAll(".name.", ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN);
							message = message.replaceAll(".vl.", id + "");

							Utils.messageAdmins(message);
							
						}
						return 1;
					
					}
					
				}
				
			}
			
		}
		
		return 0;
		
	}

}
