package me.johnnywoof.checks;

import java.util.HashMap;
import java.util.UUID;

import me.johnnywoof.NoHack;
import me.johnnywoof.Settings;
import me.johnnywoof.util.XYZ;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerVelocityEvent;

public class CustomCheck {

	private final HashMap<String, Long> lastShot = new HashMap<String, Long>();
	private final HashMap<String, Long> startEat = new HashMap<String, Long>();
	
	public void doAntiKnockBackCheck(NoHack nh, PlayerVelocityEvent event){
		
		final UUID uuid = event.getPlayer().getUniqueId();
		
		final XYZ old = new XYZ(event.getPlayer().getLocation());
		
		final XYZ expected = new XYZ(event.getPlayer().getLocation());
		
		nh.getServer().getScheduler().runTaskLater(nh, new Runnable(){

			@Override
			public void run() {
				
				Player p = Bukkit.getPlayer(uuid);
				
				if(p != null){
				
					if(Settings.debug){
	
						p.sendMessage("Old Location: " + old.toString());
						p.sendMessage("New Location: " + new XYZ(p.getLocation()).toString());
						p.sendMessage("Expected Location: " + expected.toString());
						p.sendMessage("Distance (NEW): " + old.getDistanceSqrd(new XYZ(p.getLocation())));
					
					}
				
				}
				
				p = null;
				
			}
			
		}, 10);
		
	}
	
	public void onStartEat(Player p){
		
		this.startEat.put(p.getName(), System.currentTimeMillis());
		
	}
	
	public boolean checkFastEat(Player p){
		
		if(this.startEat.containsKey(p.getName())){
			
			long diff = (System.currentTimeMillis() - this.startEat.get(p.getName()));
			
			if(diff <= 1200){
				
				return true;
				
			}
			
		}
		
		return false;
		
	}
	
	public boolean checkFastShoot(Player p){
		
		long diff = 0;
		
		if(this.lastShot.containsKey(p.getName())){
			
			diff = (System.currentTimeMillis() - this.lastShot.get(p.getName()));
			
		}else{
			
			diff = System.currentTimeMillis();
			
		}
		
		this.lastShot.put(p.getName(), System.currentTimeMillis());
		
		if(diff <= 175){
			
			return true;
			
		}
		
		return false;
		
	}
	
}
