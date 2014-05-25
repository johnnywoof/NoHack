package me.johnnywoof.check.fight;

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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class FightSpeed extends Check{

	private HashMap<String, Long> lastAttack = new HashMap<String, Long>();
	
	public FightSpeed(Variables vars, CheckType ct) {
		super(vars, ct, DetectionType.FIGHT);
	}
	
	@Override
	public int runAttackCheck(Player p, LivingEntity e, long ls){
		
		if(this.lastAttack.containsKey(p.getName())){
			
			long diff = (System.currentTimeMillis() - this.lastAttack.get(p.getName()));
			
			if(diff <= Setting.fightattackspeed){
				
				this.registerLastAttack(p.getName());
				int id = this.vars.raiseViolationLevel(CheckType.ATTACK_SPEED, p);
				
				ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.ATTACK_SPEED, p);
				
				Bukkit.getServer().getPluginManager().callEvent(vte);
				
				if(!vte.isCancelled()){
				
					if(id != 0){
						
						String message = Setting.fightspeed;
						
						message = message.replaceAll(".name.", ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN);
						message = message.replaceAll(".vl.", id + "");

						Utils.messageAdmins(message);
						
					}
					return 1;
				
				}
				
			}
			
		}
		
		this.registerLastAttack(p.getName());
		
		return 0;
		
	}
	
	private void registerLastAttack(String v){
		
		this.lastAttack.put(v, System.currentTimeMillis());
		
	}

}
