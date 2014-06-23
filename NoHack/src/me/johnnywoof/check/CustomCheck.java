package me.johnnywoof.check;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class CustomCheck {

	private final HashMap<String, Long> startBow = new HashMap<String, Long>();
	private final HashMap<String, Long> lastShot = new HashMap<String, Long>();
	private final HashMap<String, Long> startEat = new HashMap<String, Long>();
	
	public void onStartingShoot(Player p){
		
		startBow.put(p.getName(), System.currentTimeMillis());
		
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
	
	public boolean onShoot(Player p, float force){
		
		int ticks = (int) ((((System.currentTimeMillis() - startBow.get(p.getName())) * 20) / 1000) + 3);
		startBow.remove(p.getName());
        float f = (float) ticks / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        f = f > 1.0F ? 1.0F : f;
        if (Math.abs(force - f) > 0.25) {
           return true;
        } else {
            return false;
        }
		
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
