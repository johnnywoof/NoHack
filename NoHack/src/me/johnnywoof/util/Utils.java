package me.johnnywoof.util;

import net.minecraft.server.v1_7_R3.Vec3D;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Utils {

    public static boolean canSee(Player player, Location loc2) {
    	return ((CraftWorld) player.getLocation().getWorld()).getHandle().a(Vec3D.a(player.getLocation().getX(), player.getLocation().getY() + player.getEyeHeight(), player.getLocation().getZ()), Vec3D.a(loc2.getX(), loc2.getY(), loc2.getZ())) == null;
    }
	
	public static int getPing(Player p){
		
		return ((CraftPlayer) p).getHandle().ping;
		
	}
	
	public static String getIP(Player p){
		
		return ((CraftPlayer) p).getHandle().getName();
		
	}
	
	public static void messageAdmins(String message){
		
		for(Player p : Bukkit.getOnlinePlayers()){
			
			if(p.hasPermission("nohack.notification") || p.isOp()){
				
				p.sendMessage(ChatColor.RED + "[NoHack] " + message);
				
			}
			
		}
		
	}
	
}
