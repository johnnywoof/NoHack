package me.johnnywoof.check;

import java.util.HashMap;

import me.johnnywoof.CheckType;
import me.johnnywoof.NoHack;
import me.johnnywoof.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatCheck {

	final private HashMap<String, Long> lastmessage = new HashMap<String, Long>();
	
	public boolean check(NoHack nh, Player p, String message){
		
		if(p.isBlocking() || p.isSneaking() || p.isSprinting()){
			
			int id = nh.raiseViolationLevel(p.getName(), CheckType.IMPOSSIBLE);
			
			if(id != 0){
				
				Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed Impossible! Tried to send a chat message when not possible. VL " + id);
				
			}
			
			p.sendMessage(ChatColor.RED + "You tried to send a chat message in an illegal state! Are you hacking? :(");
			
			return true;
			
		}
		
		if(message.contains("§")){
			
			int id = nh.raiseViolationLevel(p.getName(), CheckType.IMPOSSIBLE);
			
			if(id != 0){
				
				Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed Impossible! Tried to send colored messages. VL " + id);
				
			}
			return true;
			
		}
		
		long diff = (System.currentTimeMillis() - this.getLastMessageTime(p.getName()));
		
		if(diff <= 400){
			
			int id = nh.raiseViolationLevel(p.getName(), CheckType.SPAM);
			
			if(id != 0){
				
				Utils.messageAdmins(ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN + " failed Spam! Tried to spam the chat. VL " + id);
				
			}
			
		}
		
		if(diff <= 400){
			
			p.kickPlayer("You are not allowed to spam in the chat!");
			nh.deniedlogin.put(Utils.getIP(p), (System.currentTimeMillis() + 30000));
			return true;
			
		}else if(diff <= 400){
			
			p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Please slow down chat, you might be kicked for spam.");
			return true;
			
		}
		
		this.lastmessage.put(p.getName(), System.currentTimeMillis());
		return false;
		
	}
	
	private long getLastMessageTime(String v){
		
		if(this.lastmessage.containsKey(v)){
			
			return this.lastmessage.get(v);
			
		}else{
			
			return 0;
			
		}
		
	}
	
}
