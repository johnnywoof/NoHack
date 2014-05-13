package me.johnnywoof.util;

import me.johnnywoof.NoHack;
import net.minecraft.server.v1_7_R3.Vec3D;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Utils {

    public static boolean canSee(Player player, Location loc2) {
    	return ((CraftWorld) player.getLocation().getWorld()).getHandle().a(Vec3D.a(player.getLocation().getX(), player.getLocation().getY() + player.getEyeHeight(), player.getLocation().getZ()), Vec3D.a(loc2.getX(), loc2.getY(), loc2.getZ())) == null;
    }
	
	public static int getPing(Player p){
		
		return ((CraftPlayer) p).getHandle().ping;
		
	}
	
	public static double getXZDistance(double x1, double x2, double z1, double z2){

		double a1 = (x2 - x1), a2 = (z2 - z1);

		return ((a1 * (a1)) + (a2 * a2));

	}
	
	public static boolean isOnLadder(Player p){
		
		return ((CraftPlayer) p).getHandle().h_();
		
	}
	
	public static boolean inWater(Player e){
		
		return ((CraftPlayer) e).getHandle().inWater;
		
	}
	
	public static int getPotionEffectLevel(Player p, PotionEffectType pet){
		
		for(PotionEffect pe : p.getActivePotionEffects()){

			if(pe.getType().getName().equals(pet.getName())){
				
				return pe.getAmplifier() + 1;
				
			}
			
		}
		
		return 0;
		
	}
	
	public static String getIP(Player p){
		
		return ((CraftPlayer) p).getHandle().getName();
		
	}
	
	public static void messageAdmins(String message){
		
		for(Player p : Bukkit.getOnlinePlayers()){
			
			if(p.hasPermission("nohack.notification") || p.isOp()){
				
				p.sendMessage(ChatColor.RED + "[NoHack] " + ChatColor.GREEN + "" + message + ChatColor.GREEN + ". TPS " + NoHack.tps);
				
			}
			
		}
		
	}
	
}
