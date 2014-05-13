package me.johnnywoof.check;

import me.johnnywoof.Variables;
import me.johnnywoof.util.XYZ;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Check {

	public Variables vars;
	
	private CheckType ct;
	
	private DetectionType dt;
	
	public Check(Variables vars, CheckType ct, DetectionType dt){
		this.vars = vars;
		this.ct = ct;
		this.dt = dt;
	}
	
	public DetectionType getDetectType(){
		return this.dt;
	}
	
	public CheckType getType(){
		
		return this.ct;
		
	}
	
	public Variables getVars(){
		return this.vars;
	}
	
	public int run(Player p, Location from, Location to, long ls, LivingEntity e, double damage, Block clicked, BlockFace bf, String mes, boolean blockmove, boolean onladder, boolean up, boolean inwater, double yd, double md, XYZ lg){return 0;}
	
}
