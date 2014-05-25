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
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class FightReach extends Check{

	public FightReach(Variables vars, CheckType ct) {
		super(vars, ct, DetectionType.FIGHT);
	}
	
	@Override
	public int runAttackCheck(Player p, LivingEntity e, long ls){
		
		double d = p.getEyeLocation().distanceSquared(e.getEyeLocation());
		
		//reach check
		
		if(d > ((p.getGameMode() == GameMode.CREATIVE) ? Setting.creativeattack : Setting.survivalattack)){
			
			int id = this.vars.raiseViolationLevel(CheckType.ATTACK_REACH, p);
			
			ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.ATTACK_REACH, p);
			
			Bukkit.getServer().getPluginManager().callEvent(vte);
			
			if(!vte.isCancelled()){
				
				if(id != 0){
					
					String message = Setting.fightreach;
					
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
