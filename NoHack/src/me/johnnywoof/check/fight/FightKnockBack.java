package me.johnnywoof.check.fight;

import me.johnnywoof.Variables;
import me.johnnywoof.check.Check;
import me.johnnywoof.check.CheckType;
import me.johnnywoof.check.DetectionType;
import me.johnnywoof.util.MoveData;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class FightKnockBack extends Check{

	public FightKnockBack(Variables vars, CheckType ct) {
		super(vars, ct, DetectionType.FIGHT);
	}
	
	@Override
	public int runAttackCheck(Player p, LivingEntity e, long ls){
		
		MoveData md = vars.getMoveData(p.getName());
		
		long diff = (System.currentTimeMillis() - md.sprinttime);
		
		Bukkit.broadcastMessage(diff + "");
		
		return 0;
		
	}
	
}
