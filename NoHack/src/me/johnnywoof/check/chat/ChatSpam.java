package me.johnnywoof.check.chat;

import java.util.HashMap;
import java.util.UUID;

import me.johnnywoof.Variables;
import me.johnnywoof.check.Check;
import me.johnnywoof.check.CheckType;
import me.johnnywoof.check.DetectionType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatSpam extends Check{

	private final HashMap<UUID, Long[]> lastsent = new HashMap<UUID, Long[]>();
	private final HashMap<UUID, Long> muted = new HashMap<UUID, Long>();
	
	public ChatSpam(Variables vars, CheckType ct) {
		super(vars, ct, DetectionType.CHAT);
	}
	
	@Override
	public int runChatCheck(Player p, String message){
		
		if(this.isMuted(p.getUniqueId())){
			
			p.sendMessage(ChatColor.RED + "You are muted for another " + Math.round(((this.muted.get(p.getUniqueId()) - System.currentTimeMillis()) / 1000)) + " seconds.");
			
			return 1;
			
		}
		
		long diff = (System.currentTimeMillis() - this.getLastSent(p.getUniqueId()));
		
		this.updateLastSent(p.getUniqueId());
		
		boolean spam = this.isConstantSpam(p.getUniqueId());
		
		if((diff <= 1500 && diff > 1000) || spam){
			
			p.sendMessage(ChatColor.YELLOW + "*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
			p.sendMessage(ChatColor.RED + "Please lower the rate of chat messages being sent");
			p.sendMessage(ChatColor.RED + "Or you might be muted");
			p.sendMessage(ChatColor.YELLOW + "*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
			
			this.lastsent.remove(p.getUniqueId());
			this.updateLastSent(p.getUniqueId());
			
			return 1;
			
		}
		
		if(diff <= 1000 || spam){
			
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You are now muted for one minute because of spamming.");
			
			Bukkit.getLogger().info("[NoHack] Muted player " + p.getName() + " for spamming. Difference was " + diff + ". Said the message \"" + message + "\"");
			
			this.muted.put(p.getUniqueId(), (System.currentTimeMillis() + 60000));
			
			return 1;
			
		}
		
		return 0;
		
	}
	
	private boolean isMuted(UUID uuid){
		
		if(this.muted.containsKey(uuid)){
			
			if(this.muted.get(uuid) < System.currentTimeMillis()){
				
				this.muted.remove(uuid);
				return false;
				
			}else{
				
				return true;
				
			}
			
		}
		
		return false;
		
	}
	
	private boolean isConstantSpam(UUID uuid){
		
		if(uuid != null){
			
			return false;
			
		}
		
		int ss = 0;
		
		long lm = System.currentTimeMillis();
		
		long ld = 0;
		
		for(Long l : this.getData(uuid)){
			
			if(Math.abs((lm - l)) <= ld + 50){
				
				ss++;
				
			}
			
			ld = Math.abs((lm - l));
			
			lm = l;
			
		}
		
		if(ss >= 3){
			
			return true;
			
		}
		
		return false;
		
	}
	
	private Long[] getData(UUID uuid){
		
		return this.lastsent.get(uuid);
		
	}
	
	private void updateLastSent(UUID uuid){
		
		if(this.lastsent.containsKey(uuid)){
		
			Long[] data = this.lastsent.get(uuid);
			
			if(data.length > 4){
			
				data[4] = data[3];

				data[3] = data[2];

				data[2] = data[1];
				
				data[1] = data[0];
				
				data[0] = System.currentTimeMillis();
				
				this.lastsent.put(uuid, data);
				
			}else{
				
				Long[] nd = new Long[data.length + 1];
				
				for(int i = 0; i < data.length; i++){
					
					nd[i] = data[i];
					
				}
				
				nd[data.length] = System.currentTimeMillis();
				
				this.lastsent.put(uuid, nd);
				
			}
		
		}else{
			
			this.lastsent.put(uuid, new Long[]{System.currentTimeMillis()});
			
		}
		
	}
	
	private long getLastSent(UUID uuid){
		
		if(this.lastsent.containsKey(uuid)){
			
			return this.lastsent.get(uuid)[0];
			
		}else{
			
			return 0;
			
		}
		
	}
	
}
