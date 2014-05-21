package me.johnnywoof.check.chat;

import java.util.HashMap;
import java.util.UUID;

import me.johnnywoof.Variables;
import me.johnnywoof.check.Check;
import me.johnnywoof.check.CheckType;
import me.johnnywoof.check.DetectionType;
import me.johnnywoof.util.XYZ;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ChatSpam extends Check{

	private final HashMap<UUID, Long> lastsent = new HashMap<UUID, Long>();
	
	public ChatSpam(Variables vars, CheckType ct) {
		super(vars, ct, DetectionType.CHAT);
	}
	
	@Override
	public int run(Player p, Location from, Location to, long ls, LivingEntity e, double damage, Block b, BlockFace bf, String mes, boolean blockmove, boolean onladder, boolean up, boolean inwater, double yd, double md, XYZ lg){
		
		long diff = (System.currentTimeMillis() - this.getLastSent(p.getUniqueId()));
		
		if(diff <= 20){
			
			p.kickPlayer("You are not allowed to spam!");
			
		}
		
		return 0;
		
	}
	
	private long getLastSent(UUID uuid){
		
		if(this.lastsent.containsKey(uuid)){
			
			return this.lastsent.get(uuid);
			
		}else{
			
			return 0;
			
		}
		
	}
	
}
