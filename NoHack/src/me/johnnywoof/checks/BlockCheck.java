package me.johnnywoof.checks;

import java.util.HashMap;

import me.johnnywoof.Settings;
import me.johnnywoof.Variables;
import me.johnnywoof.check.CheckType;
import me.johnnywoof.util.Utils;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public class BlockCheck {

	private Variables vars;
	
	private final HashMap<String, Long> lastBreak = new HashMap<String, Long>();
	private final HashMap<String, Long> lastSign = new HashMap<String, Long>();
	
	public BlockCheck(Variables vars){
		
		this.vars = vars;
		
	}
	
	public int runSignChecks(Player p, Sign s){
		
		long diff = (System.currentTimeMillis() - this.getLastSign(p.getName()));
		
		String mes = "";
		
		for(String e : s.getLines()){
			
			mes = mes + e + ".";
			
		}
		
		if(diff < (mes.length() * 50)){//TODO Test it...?
			
			return 1;
			
		}
		
		return 0;
		
	}
	
	public int runPlaceChecks(Player p, Block b){
		
		Material m = b.getType();
		
		if(m == Material.SIGN_POST || m == Material.SIGN || m == Material.WALL_SIGN){
			
			this.updateLastSign(p.getName());
			
		}
		
		return 0;
		
	}
	
	public int runBlockChecks(Player p, Block b, long ls){
		
		if(p == null || b == null){
			return 0;
		}
		
		//****************Start NoSwing******************
		
		if((System.currentTimeMillis() - ls) >= Settings.noswingblock){
			
			if(this.vars.issueViolation(p, CheckType.NOSWING)){
				
				return 1;
				
			}
			
		}
		
		//****************End NoSwing******************
		
		//****************Start FastBreak******************
		
		long diff = (System.nanoTime() - this.getLastBreak(p.getName()));
		
		this.lastBreak.put(p.getName(), System.nanoTime());
			
		//TODO Add fastbreak check
		
		//****************End FastBreak******************
		
		//****************Start Block Visible & SpeedBreak******************
		
		//TODO Account for blocks that can be broken instantly
		
		if(Utils.instantBreak(b.getType()) || p.getGameMode() == GameMode.CREATIVE){
			
			if(true){
				
				if(diff < 360000){
					
					if(Settings.debug){
						
						Bukkit.broadcastMessage("Diff: " + diff);
						
					}
					
					if(this.vars.issueViolation(p, CheckType.SPEED_BREAK)){
						
						return 1;
						
					}
					
				}
				
			}
			
	    	BlockIterator bl = new BlockIterator(p, 8);
	    	
	    	double md = 1;
	    	
	    	boolean goodie = false;
	    	
	    	while(bl.hasNext()){
	    		
	    		double d = bl.next().getLocation().distanceSquared(b.getLocation());
	    		
	    		if(d <= md){
	    			
	    			goodie = true;
	    			break;
	    			
	    		}
	    		
	    	}
	    	
	    	bl = null;
	    	
	    	if(!goodie){
	    		
	    		if(this.vars.issueViolation(p, CheckType.BLOCK_VISIBLE)){
					
					return 1;
					
				}
	    		
	    	}
			
		}else{
		
			if(!Utils.canSeeBlock(p, b)){
				
				if(this.vars.issueViolation(p, CheckType.BLOCK_VISIBLE)){
					
					return 4;
					
				}
				
			}
		
		}
		
		//****************End Block Visible******************
		
		return 0;
		
	}
	
	public void updateLastSign(String v){
		
		this.lastSign.put(v, System.currentTimeMillis());
		
	}
	
	private long getLastSign(String v){
		
		if(this.lastSign.containsKey(v)){
			
			return this.lastSign.get(v);
			
		}else{
			
			return 0;
			
		}
		
	}
	
	private long getLastBreak(String v){
		
		if(this.lastBreak.containsKey(v)){
			
			return this.lastBreak.get(v);
			
		}else{
			
			return 0;
			
		}
		
	}
	
}
