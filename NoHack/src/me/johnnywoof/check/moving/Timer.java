package me.johnnywoof.check.moving;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import me.johnnywoof.Setting;
import me.johnnywoof.Variables;
import me.johnnywoof.check.Check;
import me.johnnywoof.check.CheckType;
import me.johnnywoof.check.DetectionType;
import me.johnnywoof.event.ViolationTriggeredEvent;
import me.johnnywoof.util.Utils;
import me.johnnywoof.util.XYZ;

public class Timer extends Check{

	//Keep track the amount of packets
	private final HashMap<String, MovePacketData> movepackets = new HashMap<String, MovePacketData>();
	
	public Timer(Variables vars, CheckType ct) {
		super(vars, ct, DetectionType.MOVING);
	}
	
	@Override
	public int run(Player p, Location from, Location to, long ls, LivingEntity e, double damage, Block clicked, BlockFace bf, String mes, boolean blockmove, boolean onladder, boolean up, boolean inwater, double yd, double md, XYZ lg){
		
		MovePacketData mpd = this.movepackets.get(p.getName());
		
		if(mpd == null){
			
			mpd = new MovePacketData(System.currentTimeMillis(), 1);
			
		}
		
		if(mpd.lastloc == null){
			
			mpd.lastloc = new XYZ(p.getLocation());
			
		}
		
		mpd.setAmount(mpd.getAmount() + 1);
		
		if((System.currentTimeMillis() - mpd.getTimeStart()) >= 500){
			
			int expected = (14 + Math.round(Utils.getPing(p) / 100));
			
			if(mpd.getAmount() >= expected){
					
				int id = this.vars.raiseViolationLevel(CheckType.TIMER, p);
					
				ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.TIMER, p);
				
				Bukkit.getServer().getPluginManager().callEvent(vte);
				
				if(!vte.isCancelled()){
				
					if(id != 0){
							
						String message = Setting.timermes;
						
						message = message.replaceAll(".name.", ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN);
						message = message.replaceAll(".vl.", id + "");
						message = message.replaceAll(".packets.", mpd.getAmount() + "");
						message = message.replaceAll(".expected.", expected + "");

						Utils.messageAdmins(message);
							
					}
					p.teleport(mpd.lastloc.toLocation(p.getLocation().getPitch(), p.getLocation().getYaw()), TeleportCause.PLUGIN);
					mpd.reset(new XYZ(p.getLocation()));
					return 0;
				
				}
				
			}else{
				
				mpd.reset(new XYZ(p.getLocation()));
				
			}
			
		}
		
		this.movepackets.put(p.getName(), mpd);
		return 0;
		
	}

}
