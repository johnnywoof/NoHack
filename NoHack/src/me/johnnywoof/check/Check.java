package me.johnnywoof.check;

import me.johnnywoof.Variables;
import me.johnnywoof.util.MoveData;
import me.johnnywoof.util.XYZ;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

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
	
	public int runMoveCheck(Player p, Location to, Location from, double yd, double xs, double zs, MoveData movedata, boolean up, boolean inwater, boolean onladder, XYZ lg){
		
		return 0;
		
	}
	
	public int runAttackCheck(Player p, LivingEntity e, long ls){
		
		return 0;
		
	}
	
	public int runInventoryCheck(Player p, Inventory inv, InventoryAction ia, InventoryClickEvent event){
		
		return 0;
		
	}
	
	public int runChatCheck(Player p, String message){
		
		return 0;
		
	}
	
	public int runBlockCheck(Player p, Block clicked, BlockFace bf, long ls, int aid){
		
		return 0;
		
	}
	
	public int runInteractCheck(Player p, PlayerInteractEvent event){
		
		return 0;
		
	}
	
}
