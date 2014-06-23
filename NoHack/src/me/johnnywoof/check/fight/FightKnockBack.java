package me.johnnywoof.check.fight;

import me.johnnywoof.Variables;
import me.johnnywoof.check.Check;
import me.johnnywoof.check.CheckType;
import me.johnnywoof.check.DetectionType;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class FightKnockBack extends Check{

	public FightKnockBack(Variables vars, CheckType ct) {
		super(vars, ct, DetectionType.FIGHT);
	}
	
	@Override
	public int runAttackCheck(Player p, LivingEntity e, long ls){
		
		//Start dealing more knockback than it should
		if(p.getItemInHand() != null){
			
			if(p.getItemInHand().containsEnchantment(Enchantment.KNOCKBACK)){
				
				return 0;
				
			}
			
		}
		
		//MoveData md = vars.getMoveData(p.getName());
		
		//long diff = (System.currentTimeMillis() - md.sprinttime);
		
		//Bukkit.broadcastMessage(diff + "");
		
		return 0;
		
	}
	
}
