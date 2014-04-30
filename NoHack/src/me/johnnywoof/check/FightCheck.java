package me.johnnywoof.check;

import java.util.HashMap;

import me.johnnywoof.CheckType;
import me.johnnywoof.NoHack;
import me.johnnywoof.util.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FightCheck {

	private HashMap<String, Long> lastAttack = new HashMap<String, Long>();
	
	public boolean check(NoHack nh, long ls, Player k, LivingEntity v, double damage){

		if(!v.isDead()){
		
			k.closeInventory();
			
			if(k.isDead()){
				
				int id = nh.raiseViolationLevel(k.getName(), CheckType.GOD_MODE);
				
				if(id != 0){
					
					Utils.messageAdmins(ChatColor.YELLOW + "" + k.getName() + "" + ChatColor.GREEN + " failed GodMoe! Tried to attack while dead. VL " + id);
					
				}
				return true;
				
			}
			
			//prevents attacking while blocking
			if(k.isBlocking()){
				
				int id = nh.raiseViolationLevel(k.getName(), CheckType.IMPOSSIBLE);
				
				if(id != 0){
					
					Utils.messageAdmins(ChatColor.YELLOW + "" + k.getName() + "" + ChatColor.GREEN + " failed Impossible! Tried to attack while blocking. VL " + id);
					
				}
				return true;
				
			}
			
			boolean critical = false;
			
			if(k.getItemInHand() != null){
				critical = damage > this.calculateDamage(k.getItemInHand(), (v.getType() == EntityType.SKELETON || v.getType() == EntityType.ZOMBIE ||v.getType() == EntityType.WITHER || v.getType() == EntityType.PIG_ZOMBIE), (v.getType() == EntityType.CAVE_SPIDER || v.getType() == EntityType.SPIDER ||v.getType() == EntityType.SILVERFISH));
			}
			
			if(critical){
				
				if(k.getFallDistance() <= 0.05){//client must have onground to false for fall distance, no need to check for onground.
					
					int id = nh.raiseViolationLevel(k.getName(), CheckType.CRITICAL);
					
					if(id != 0){
						
						Utils.messageAdmins(ChatColor.YELLOW + "" + k.getName() + "" + ChatColor.GREEN + " failed Critical! Tried to do a critical hit when not possible. VL " + id);
						
					}
					return true;
					
				}
				
			}
			
			if((System.currentTimeMillis() - ls) >= 10){
				
				int id = nh.raiseViolationLevel(k.getName(), CheckType.NOSWING);
				
				if(id != 0){
					
					Utils.messageAdmins(ChatColor.YELLOW + "" + k.getName() + "" + ChatColor.GREEN + " failed NoSwing! Difference was " + (System.currentTimeMillis() - ls) + ". VL " + id);
					
				}
				return true;
				
			}
			
			double d = k.getEyeLocation().distanceSquared(v.getEyeLocation());
			
			//reach check
			
			if(d > ((k.getGameMode() == GameMode.CREATIVE) ? 27.5 : 14)){
				
				int id = nh.raiseViolationLevel(k.getName(), CheckType.ATTACK_REACH);
				
				if(id != 0){
					
					Utils.messageAdmins(ChatColor.YELLOW + "" + k.getName() + "" + ChatColor.GREEN + " failed Attack reach! Tried to do an attack far away. VL " + id);
					
				}
				return true;
				
			}
			
			if(this.lastAttack.containsKey(k.getName())){
				
				long diff = (System.currentTimeMillis() - this.lastAttack.get(k.getName()));
				
				if(diff <= 90){
					
					this.registerLastAttack(k.getName());
					int id = nh.raiseViolationLevel(k.getName(), CheckType.ATTACK_SPEED);
					
					if(id != 0){
						
						Utils.messageAdmins(ChatColor.YELLOW + "" + k.getName() + "" + ChatColor.GREEN + " failed Attack Speed! Tried to attack too fast. VL " + id);
						
					}
					return true;
					
				}
				
			}
			
			//forcefield look check, this helps prevent aimbot. This is popular in a lot of forcefields.
			int[] a = this.getPandY(k, v, Float.MAX_VALUE, Float.MAX_VALUE);
			
			if(k.getLocation().getYaw() <= (a[1] + 2) && k.getLocation().getYaw() >= (a[1] - 2) && k.getLocation().getPitch() <= (a[0] + 2) && k.getLocation().getPitch() >= (a[0] - 2)){
				
				int id = nh.raiseViolationLevel(k.getName(), CheckType.AIMBOT);
				
				if(id != 0){
					
					Utils.messageAdmins(ChatColor.YELLOW + "" + k.getName() + "" + ChatColor.GREEN + " failed Aimbot! Position head in a weird way. VL " + id);
					
				}
				return true;
				
			}
			
			if(this.attackHistoryMatches(k.getName())){
				
				Bukkit.broadcastMessage("Attack time to much the same");
				return true;
				
			}
		
		}
		
		this.registerLastAttack(k.getName());
		
		return false;
		
	}
	
	private boolean attackHistoryMatches(String v){
		
		return false;
		
	}
	
	public long getLastAttackTime(String v){
		
		if(this.lastAttack.containsKey(v)){
			return this.lastAttack.get(v);
		}else{
			return 0;
		}
		
	}
	
	private void registerLastAttack(String v){
		
		this.lastAttack.put(v, System.currentTimeMillis());
		
	}
	
	public double calculateDamage(ItemStack i, boolean undead, boolean arthropod){
		
		double d = 0.0;
		
		if(i.getType() == Material.WOOD_SWORD ||i.getType() == Material.GOLD_SWORD){
			
			d = 5;
			
		}else if(i.getType() == Material.STONE_SWORD){
			
			d = 6;
			
		}else if(i.getType() == Material.IRON_SWORD){
			
			d = 7;
			
		}else if(i.getType() == Material.DIAMOND_SWORD){
			
			d = 8;
			
		}else if(i.getType() == Material.WOOD_AXE ||i.getType() == Material.GOLD_AXE){
				
			d = 4;
				
		}else if(i.getType() == Material.STONE_AXE){
				
			d = 5;
				
		}else if(i.getType() == Material.IRON_AXE){
				
			d = 6;
				
		}else if(i.getType() == Material.DIAMOND_AXE){
				
			d = 7;
			
		}else if(i.getType() == Material.WOOD_PICKAXE ||i.getType() == Material.GOLD_PICKAXE){
			
			d = 3;
				
		}else if(i.getType() == Material.STONE_PICKAXE){
				
			d = 4;
				
		}else if(i.getType() == Material.IRON_PICKAXE){
				
			d = 5;
				
		}else if(i.getType() == Material.DIAMOND_PICKAXE){
				
			d = 6;
			
		}else if(i.getType() == Material.WOOD_SPADE ||i.getType() == Material.GOLD_SPADE){
			
			d = 2;
				
		}else if(i.getType() == Material.STONE_SPADE){
				
			d = 3;
				
		}else if(i.getType() == Material.IRON_SPADE){
				
			d = 4;
				
		}else if(i.getType() == Material.DIAMOND_SPADE){
				
			d = 5;
				
		}else{
			
			d = 1;
			
		}
		
		if(i.containsEnchantment(Enchantment.DAMAGE_ALL)){
			
			d = d + (i.getEnchantmentLevel(Enchantment.DAMAGE_ALL) * 1.25);
			
		}
		
		if(undead){
			
			if(i.containsEnchantment(Enchantment.DAMAGE_UNDEAD)){
				
				d = d + (i.getEnchantmentLevel(Enchantment.DAMAGE_UNDEAD) * 2.5);
				
			}
			
		}
		
		if(arthropod){
			
			if(i.containsEnchantment(Enchantment.DAMAGE_ARTHROPODS)){
				
				d = d + (i.getEnchantmentLevel(Enchantment.DAMAGE_ARTHROPODS) * 2.5);
				
			}
			
		}
		
		return d;
			
	}
	
	public int[] getPandY(Player p, LivingEntity e, float par2, float par3)
    {
    	int[] a = new int[2];
        double var4 = e.getLocation().getX() - p.getLocation().getX();
        double var8 = e.getLocation().getZ() - p.getLocation().getZ();
        double var6 = e.getLocation().getY() + (double)e.getEyeHeight() - (p.getLocation().getY() + (double)p.getEyeHeight());

        double var14 = (double)this.sqrt_double(var4 * var4 + var8 * var8);
        float var12 = (float)(Math.atan2(var8, var4) * 180.0D / Math.PI) - 90.0F;
        float var13 = (float)(-(Math.atan2(var6, var14) * 180.0D / Math.PI));
        a[0] = Math.round(updateRotation(p.getLocation().getPitch(), var13, par3));
        a[1] = Math.round(updateRotation(p.getLocation().getYaw(), var12, par2));
        return a;
    }
	
	private float updateRotation(float par1, float par2, float par3)
    {
        float var4 = this.wrapAngleTo180_float(par2 - par1);

        if (var4 > par3)
        {
            var4 = par3;
        }

        if (var4 < -par3)
        {
            var4 = -par3;
        }

        return par1 + var4;
    }
	
	 private final float sqrt_double(double par0)
	    {
	        return (float)Math.sqrt(par0);
	    }
	
	private float wrapAngleTo180_float(float par0)
    {
        par0 %= 360.0F;

        if (par0 >= 180.0F)
        {
            par0 -= 360.0F;
        }

        if (par0 < -180.0F)
        {
            par0 += 360.0F;
        }

        return par0;
    }
	
}
