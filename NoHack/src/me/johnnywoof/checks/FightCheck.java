package me.johnnywoof.checks;

import java.util.HashMap;

import me.johnnywoof.Setting;
import me.johnnywoof.Variables;
import me.johnnywoof.check.CheckType;
import me.johnnywoof.event.ViolationTriggeredEvent;
import me.johnnywoof.util.MoveData;
import me.johnnywoof.util.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftLivingEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class FightCheck {

	private Variables vars;
	
	private HashMap<String, Long> lastAttack = new HashMap<String, Long>();
	
	public FightCheck(Variables vars){
		
		this.vars = vars;
		
	}
	
	public int runFightChecks(Player p, LivingEntity e, long ls){
		
		//****************Start Fight Impossible******************
		
		if(p.isBlocking() || p.isSleeping() || p.isDead() || p.getEntityId() == e.getEntityId()){
			
			int id = this.vars.raiseViolationLevel(CheckType.IMPOSSIBLE, p);
			
			ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.IMPOSSIBLE, p);
			
			Bukkit.getServer().getPluginManager().callEvent(vte);
			
			if(!vte.isCancelled()){
				
				if(id != 0){
					
					String message = Setting.impossibleattack;
					
					message = message.replaceAll(".name.", ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN);
					message = message.replaceAll(".vl.", id + "");

					Utils.messageAdmins(message);
					
				}
				return 1;
			
			}
			
		}
		
		//****************End Fight Impossible******************
		
		//****************Start Fight Visible******************
		
		//Kind of performance heavy, only check it when they get the damage
		if(((CraftLivingEntity) e).getHandle().hurtTicks <= 3){
			
			if(!Utils.canReallySeeEntity(p, e)){
				
				int id = this.vars.raiseViolationLevel(CheckType.VISIBLE, p);
				
				ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.VISIBLE, p);
				
				Bukkit.getServer().getPluginManager().callEvent(vte);
				
				if(!vte.isCancelled()){
					
					if(id != 0){
						
						String message = Setting.fightvisiblemes;
						
						message = message.replaceAll(".name.", ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN);
						message = message.replaceAll(".vl.", id + "");

						Utils.messageAdmins(message);
						
					}
					return 1;
				
				}
				
			}
			
		}
		
		//****************End Fight Visible******************
		
		//****************Start Fight NoSwing******************
		
		if((System.currentTimeMillis() - ls) >= Setting.noswingfight){
			
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
				
		//****************End Fight NoSwing******************
		
		//****************Start Fight Reach******************
		
		//TODO Make this a better reach check
		if(e.getType() != EntityType.WITHER){
		
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
		
		}
		
		//****************End Fight Reach******************

		//****************Start Fight Speed******************
		
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
		
		//****************End Fight Speed******************
		
		//****************Start Fight Knockback******************
		
		//TODO Make this better...
		if(p.getItemInHand() != null){
			
			if(!p.getItemInHand().containsEnchantment(Enchantment.KNOCKBACK)){
				
				MoveData md = this.vars.getMoveData(p.getName());

				if((System.currentTimeMillis() - md.sprinttime) < 101){
						
					int id = this.vars.raiseViolationLevel(CheckType.FIGHT_KNOCKBACK, p);
						
					ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.FIGHT_KNOCKBACK, p);
						
					Bukkit.getServer().getPluginManager().callEvent(vte);
					
					if(!vte.isCancelled()){
						
						if(id != 0){
								
							String message = Setting.fightknock;
								
							message = message.replaceAll(".name.", ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN);
							message = message.replaceAll(".vl.", id + "");

							Utils.messageAdmins(message);
								
						}
						return 1;
						
					}
					
				}
				
			}
			
		}
		
		//****************End Fight Knockback******************
		
		//****************Start Fight AutoSoup******************
		
		
		
		//****************End Fight AutoSoup******************
		
		return 0;		
	}
	
	private void registerLastAttack(String v){
		
		this.lastAttack.put(v, System.currentTimeMillis());
		
	}
	
}
