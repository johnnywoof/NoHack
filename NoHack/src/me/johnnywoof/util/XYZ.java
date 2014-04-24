package me.johnnywoof.util;

import org.bukkit.Location;

import org.bukkit.Bukkit;

public class XYZ {

	public double x, y, z;
	
	public String world;
	
	public XYZ(String world, double x, double y, double z){
		
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		
	}
	
	public XYZ(Location loc){
		
		this.world = loc.getWorld().getName();
		this.x = loc.getX();
		this.y = loc.getY();
		this.z = loc.getZ();
		
	}
	
	public Location toLocation(){
		return new Location(Bukkit.getWorld(world), x, y, z);
	}
	
	public Location toLocation(float pitch, float yaw){
		return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
	}
	
}
