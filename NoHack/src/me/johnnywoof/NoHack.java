package me.johnnywoof;

import java.util.ArrayList;

import me.johnnywoof.check.Check;
import me.johnnywoof.check.CheckType;
import me.johnnywoof.check.Violation;
import me.johnnywoof.check.block.FastBreak;
import me.johnnywoof.check.block.NoSwingBlock;
import me.johnnywoof.check.chat.ChatImpossible;
import me.johnnywoof.check.fight.FightImpossible;
import me.johnnywoof.check.fight.FightReach;
import me.johnnywoof.check.fight.FightSpeed;
import me.johnnywoof.check.fight.GodMode;
import me.johnnywoof.check.fight.NoSwingFight;
import me.johnnywoof.check.interact.FastInteract;
import me.johnnywoof.check.moving.HorizontalSpeed;
import me.johnnywoof.check.moving.ImpossibleMoving;
import me.johnnywoof.check.moving.NoFall;
import me.johnnywoof.check.moving.SurvivalFly;
import me.johnnywoof.check.moving.Timer;
import me.johnnywoof.check.moving.VerticalSpeed;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class NoHack extends JavaPlugin{
	
	/**
	 * =====Todo List=====
	 * Add more checks
	 * 
	 * */
	
	public static int tps = 0;
	private long second = 0;
	
	public Variables vars;
	
	private ArrayList<Check> checks = new ArrayList<Check>();
	//I prefer better preformance, rather than "instanceof". Since this is called a lot
	public FastInteract fi;

	public void onEnable(){
		
		this.getServer().getPluginManager().registerEvents(new NoHackListener(this), this);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			long sec;
			int ticks;
			
			@Override
			public void run()
			{
				sec = (System.currentTimeMillis() / 1000);
				
				if(second == sec)
				{
					ticks++;
				}
				else
				{
					second = sec;
					tps = (tps == 0 ? ticks : ((tps + ticks) / 2));
					ticks = 0;
					
					tps = tps + 1;
					
					if(tps > 20){
						tps = 20;
					}
					
				}
			}
		}, 20, 1);
		this.reload();
		
		if(this.getServer().getAllowFlight()){
		
			this.getLogger().warning("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-");
			this.getLogger().warning("[NoHack] Allowed flight in server.properties is true! Please set it to false for best preformance.");
			this.getLogger().warning("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-");
		
		}
		
		this.getLogger().info("[NoHack] NoHack has been enabled!");
	}
	
	public void onDisable(){
		this.getServer().getScheduler().cancelTasks(this);
		this.getLogger().info("[NoHack] NoHack has been disabled!");
	}
	
	public void reload(){
		
		if(!getDataFolder().exists()){
			getDataFolder().mkdir();
		}
		
		this.saveDefaultConfig();
		
		this.vars = new Variables();
		
		this.vars.reloadConfig(this.getConfig());
		
		this.checks.clear();
		
		this.fi = new FastInteract(this.vars, CheckType.FAST_INTERACT);
		
		//Remember! Higher = more priority
		this.checks.add(new ImpossibleMoving(this.vars, CheckType.IMPOSSIBLE));
		this.checks.add(new Timer(this.vars, CheckType.TIMER));
		this.checks.add(new NoFall(this.vars, CheckType.NOFALL));
		this.checks.add(new VerticalSpeed(this.vars, CheckType.VERTICAL_SPEED));
		this.checks.add(new SurvivalFly(this.vars, CheckType.FLY));
		this.checks.add(new HorizontalSpeed(this.vars, CheckType.HORIZONTAL_SPEED));
		this.checks.add(new FastBreak(this.vars, null));//TODO Change this?
		this.checks.add(new NoSwingBlock(this.vars, CheckType.NOSWING));
		this.checks.add(new GodMode(this.vars, CheckType.GOD_MODE));
		this.checks.add(new FightImpossible(this.vars, CheckType.IMPOSSIBLE));
		this.checks.add(new FightReach(this.vars, CheckType.ATTACK_REACH));
		this.checks.add(new FightSpeed(this.vars, CheckType.ATTACK_SPEED));
		this.checks.add(new NoSwingFight(this.vars, CheckType.NOSWING));
		this.checks.add(new ChatImpossible(this.vars, CheckType.IMPOSSIBLE));
			
	}
	
	public ArrayList<Check> getChecks(){
		return this.checks;
	}
	
	private void displayHelp(CommandSender sender){
		
		sender.sendMessage(ChatColor.GREEN + "/nohack ak <name> " + ChatColor.GOLD + "- Checks for anti-knockback");
		sender.sendMessage(ChatColor.GREEN + "/nohack info <name> " + ChatColor.GOLD + "- Views violation data");
		sender.sendMessage(ChatColor.GREEN + "/nohack reload " + ChatColor.GOLD + "- Reloads the config file");
		sender.sendMessage(ChatColor.GREEN + "/nohack test " + ChatColor.GOLD + "- Developer left over");
		
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(final CommandSender sender, Command cmd, String commandLabel, String[] args) {
		
		if(sender instanceof Player){
			
			Player p = (Player) sender;
			
			if(!p.isOp()){
				
				sender.sendMessage(ChatColor.WHITE + "Unknown command. Type \"/help\" for a list of commands.");
				
			}
			
			if(args.length <= 0){
				
				this.displayHelp(sender);
				
			}else{
				
				if(args[0].equalsIgnoreCase("reload")){
					
					this.reloadConfig();
					
					sender.sendMessage(ChatColor.GOLD + "Config file has been reloaded.");
					
				}else if(args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("disable")){
					
					HandlerList.unregisterAll(this);
					
					if(args[0].equalsIgnoreCase("enable")){
						
						this.getServer().getPluginManager().registerEvents(new NoHackListener(this), this);
						sender.sendMessage(ChatColor.GREEN + "Listener is now active");
						
					}else{
						
						sender.sendMessage(ChatColor.GREEN + "Listener is no longer active");
						
					}
					
				}else if(args[0].equalsIgnoreCase("test")){
					
					p.setVelocity(p.getVelocity().setX(4));
					
				}else if(args[0].equalsIgnoreCase("info")){
					
					if(args.length <= 1){
						this.displayHelp(sender);
					}else{
					
						Player t = this.getServer().getPlayer(args[1]);
						
						if(t != null){
						
							//sender.sendMessage(ChatColor.GREEN + t.getName() + "" + ChatColor.GOLD + "'s ping is " + Utils.getPing(t) + " milliseconds.");
							//sender.sendMessage(ChatColor.GREEN + t.getName() + "" + ChatColor.GOLD + "'s ip is " + Utils.getIP(t) + ".");
							sender.sendMessage(ChatColor.AQUA + "" + ChatColor.STRIKETHROUGH + "------------------------------------------");
							Violation vio = this.vars.getViolation(t.getName());
							
							for(CheckType ct : CheckType.values()){
								
								int level = vio.getLevel(ct);
								
								if(level > 0){
									
									sender.sendMessage(ChatColor.GREEN + "" + ct.toString().toLowerCase() + "" + ChatColor.RED + ": " + ChatColor.YELLOW + "" + level);
									
								}
								
							}
							
							sender.sendMessage(ChatColor.AQUA + "" + ChatColor.STRIKETHROUGH + "------------------------------------------");
						
						}else{
							
							sender.sendMessage(ChatColor.RED + "" + args[1] + " is not online.");
							
						}
					
					}
					
				}else{
					
					this.displayHelp(sender);
					
				}
				
			}
			
			p = null;
			
		}else{
			
			sender.sendMessage(ChatColor.GREEN + "Console is not supported, sorry!");
			
		}
		
		return true;
		
	}
	
}
