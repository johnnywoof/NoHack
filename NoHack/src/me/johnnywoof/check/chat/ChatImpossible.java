package me.johnnywoof.check.chat;

import me.johnnywoof.Setting;
import me.johnnywoof.Variables;
import me.johnnywoof.check.Check;
import me.johnnywoof.check.CheckType;
import me.johnnywoof.check.DetectionType;
import me.johnnywoof.event.ViolationTriggeredEvent;
import me.johnnywoof.util.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatImpossible extends Check{

	public ChatImpossible(Variables vars, CheckType ct) {
		super(vars, ct, DetectionType.CHAT);
	}
	
	@Override
	public int runChatCheck(Player p, String message){
		
		if(p.isSneaking() || p.isBlocking() || p.isSprinting() || p.isDead() || message.toString().contains(ChatColor.COLOR_CHAR + "")){
			
			int id = this.vars.raiseViolationLevel(CheckType.IMPOSSIBLE, p);
			
			ViolationTriggeredEvent vte = new ViolationTriggeredEvent(id, CheckType.IMPOSSIBLE, p);
			
			Bukkit.getServer().getPluginManager().callEvent(vte);
			
			if(!vte.isCancelled()){
				
				if(id != 0){
					
					String mes = Setting.chatimpossiblemes;
					
					mes = mes.replaceAll(".name.", ChatColor.YELLOW + "" + p.getName() + "" + ChatColor.GREEN);
					mes = mes.replaceAll(".vl.", id + "");

					Utils.messageAdmins(mes);
					
				}
				return 1;
			
			}
			
		}
		
		return 0;
		
	}

}
