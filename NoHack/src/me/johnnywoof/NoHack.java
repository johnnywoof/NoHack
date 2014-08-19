package me.johnnywoof;

import com.lenis0012.bukkit.npc.NPCFactory;
import me.johnnywoof.check.CheckType;
import me.johnnywoof.check.Violation;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class NoHack extends JavaPlugin {

    /**
     * =====Todo List=====
     * Add AutoSoup check
     * Use velocities
     */

    public static int tps = 0;
    private long second = 0;
    public NPCFactory factory;

    public Variables vars;

    public void onEnable() {

        String ver = this.getServer().getVersion();

        if (!ver.contains("1.7.10")) {

            this.getLogger().severe("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
            this.getLogger().severe("THIS PLUGIN IS DESIGNED FOR");
            this.getLogger().severe("1.7.10");
            this.getLogger().severe("DETECTED VERSION: " + ver);
            this.getLogger().severe("NOHACK WILL TRY TO RUN, BUT MAY NOT WORK PROPERLY");
            this.getLogger().severe("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");

        }

        this.factory = new NPCFactory(this);

        if (this.getServer().getPluginManager().getPlugin("NoCheatPlus") != null) {

            this.getLogger().warning("Another anti-cheating plugin detected! (NoCheatPlus). Two anti hacking plugins does not make checks more accurate or a safer server, it's recommended to run only one anti hacking plugin.");

        }

        if (this.getServer().getPluginManager().getPlugin("AntiCheat") != null) {

            this.getLogger().warning("Another anti-cheating plugin detected! (AntiCheat). Two anti hacking plugins does not make checks more accurate or a safer server, it's recommended to run only one anti hacking plugin.");

        }

        this.reload();

        this.getServer().getPluginManager().registerEvents(new NoHackListener(this), this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            long sec;
            int ticks;

            @Override
            public void run() {
                sec = (System.currentTimeMillis() / 1000);

                if (second == sec) {
                    ticks++;
                } else {
                    second = sec;
                    tps = (tps == 0 ? ticks : ((tps + ticks) / 2));
                    ticks = 0;

                    tps = tps + 1;

                    if (tps > 20) {
                        tps = 20;
                    }

                }
            }
        }, 20, 1);

        if (this.getServer().getAllowFlight() && this.getServer().getDefaultGameMode() != GameMode.CREATIVE) {

            this.getLogger().warning("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
            this.getLogger().warning("Allowed flight in server.properties is true!");
            this.getLogger().warning("Please set it to false for best preformance!");
            this.getLogger().warning("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");

        }

        this.getLogger().info("[NoHack] NoHack has been enabled!");
    }

    public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);
        this.getLogger().info("[NoHack] NoHack has been disabled!");
    }

    public void reload() {

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        this.saveDefaultConfig();

        this.vars = new Variables();

        Settings.reload(this.getConfig());

    }

    private void displayHelp(CommandSender sender) {

        sender.sendMessage(ChatColor.GREEN + "/nohack ak <name> " + ChatColor.GOLD + "- Checks for anti-knockback");
        sender.sendMessage(ChatColor.GREEN + "/nohack info <name> " + ChatColor.GOLD + "- Views violation data");
        sender.sendMessage(ChatColor.GREEN + "/nohack reload " + ChatColor.GOLD + "- Reloads the config file");
        sender.sendMessage(ChatColor.GREEN + "/nohack test " + ChatColor.GOLD + "- Developer left over");

    }

    @SuppressWarnings("deprecation")
    public boolean onCommand(final CommandSender sender, Command cmd, String commandLabel, String[] args) {

        if (sender instanceof Player) {

            Player p = (Player) sender;

            if (!p.isOp()) {

                sender.sendMessage(ChatColor.WHITE + "Unknown command. Type \"/help\" for a list of commands.");
                return true;

            }

            if (args.length <= 0) {

                this.displayHelp(sender);

            } else {

                if (args[0].equalsIgnoreCase("reload")) {

                    this.reloadConfig();

                    sender.sendMessage(ChatColor.GOLD + "Config file has been reloaded.");

                } else if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("disable")) {

                    HandlerList.unregisterAll(this);

                    if (args[0].equalsIgnoreCase("enable")) {

                        this.getServer().getPluginManager().registerEvents(new NoHackListener(this), this);
                        sender.sendMessage(ChatColor.GREEN + "Listener is now active");

                    } else {

                        sender.sendMessage(ChatColor.GREEN + "Listener is no longer active");

                    }

                } else if (args[0].equalsIgnoreCase("test")) {

                    final int a = Integer.parseInt(args[1]);

                    Vector vec = p.getVelocity();

                    vec.setX(a);
                    vec.setY(a);

                    p.setVelocity(vec);

                } else if (args[0].equalsIgnoreCase("info")) {

                    if (args.length <= 1) {
                        this.displayHelp(sender);
                    } else {

                        Player t = this.getServer().getPlayer(args[1]);

                        if (t != null) {

                            //sender.sendMessage(ChatColor.GREEN + t.getName() + "" + ChatColor.GOLD + "'s ping is " + Utils.getPing(t) + " milliseconds.");
                            //sender.sendMessage(ChatColor.GREEN + t.getName() + "" + ChatColor.GOLD + "'s ip is " + Utils.getIP(t) + ".");
                            sender.sendMessage(ChatColor.AQUA + "" + ChatColor.STRIKETHROUGH + "------------------------------------------");
                            Violation vio = this.vars.getViolation(t.getName());

                            for (CheckType ct : CheckType.values()) {

                                int level = vio.getLevel(ct);

                                if (level > 0) {

                                    sender.sendMessage(ChatColor.GREEN + "" + ct.toString().toLowerCase() + "" + ChatColor.RED + ": " + ChatColor.YELLOW + "" + level);

                                }

                            }

                            sender.sendMessage(ChatColor.AQUA + "" + ChatColor.STRIKETHROUGH + "------------------------------------------");

                        } else {

                            sender.sendMessage(ChatColor.RED + "" + args[1] + " is not online.");

                        }

                    }

                } else {

                    this.displayHelp(sender);

                }

            }

            p = null;

        } else {

            sender.sendMessage(ChatColor.GREEN + "Console is not supported, sorry!");

        }

        return true;

    }

}
