package me.johnnywoof;

import com.lenis0012.bukkit.npc.NPCDamageEvent;
import me.johnnywoof.check.CheckType;
import me.johnnywoof.checks.*;
import me.johnnywoof.event.ViolationChangedEvent;
import me.johnnywoof.threads.KillAuraThread;
import me.johnnywoof.util.MoveData;
import me.johnnywoof.util.Utils;
import me.johnnywoof.util.XYZ;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import java.io.*;
import java.util.HashMap;

public class NoHackListener implements Listener {

    private NoHack nh;

    private CustomCheck cc;
    private MovingCheck mc;
    private ChatCheck chatc;
    private FightCheck fc;
    private InteractCheck ic;
    private InventoryCheck invc;
    private BlockCheck bc;

    private final HashMap<String, Long> lastHealhed = new HashMap<String, Long>();

    private KillAuraThread kat = null;

    public NoHackListener(NoHack nh) {

        this.nh = nh;
        this.mc = new MovingCheck(nh.vars);
        this.cc = new CustomCheck();
        this.chatc = new ChatCheck(nh.vars);
        this.fc = new FightCheck(nh.vars);
        this.ic = new InteractCheck(nh.vars);
        this.invc = new InventoryCheck(nh.vars);
        this.bc = new BlockCheck(nh.vars);

        if (Settings.killaura) {

            this.kat = new KillAuraThread(nh);

            nh.getServer().getScheduler().runTaskTimer(nh, this.kat, 100, 1200);//Every minute = 1200

        }

    }

    @EventHandler
    public void onNPCDamaged(NPCDamageEvent event) {

        if (this.kat != null) {

            if (event.getCause() == DamageCause.ENTITY_ATTACK) {

                if (event.getDamager().getType() == EntityType.PLAYER) {

                    if (event.getDamager() instanceof Player) {//NPC's man

                        if (kat.onDamaged(event.getNpc(), ((Player) event.getDamager()))) {

                            event.setCancelled(true);

                        }

                    }

                }

            }

        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onRespawn(PlayerRespawnEvent event) {

        MoveData md = nh.vars.getMoveData(event.getPlayer().getName());

        md.lastloc = new XYZ(event.getRespawnLocation());

        md.tptime = System.currentTimeMillis();

        nh.vars.setMoveData(event.getPlayer().getName(), md);

    }

    @EventHandler(ignoreCancelled = true)
    public void onMountExit(VehicleExitEvent event) {

        if (event.getExited() instanceof Player) {

            Player p = (Player) event.getExited();

            MoveData md = nh.vars.getMoveData(p.getName());

            md.lastmounting = System.currentTimeMillis();

            nh.vars.setMoveData(p.getName(), md);

        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onSign(SignChangeEvent event) {

        if (event.getBlock().getState() instanceof Sign) {

            if (bc.runSignChecks(event.getPlayer(), (Sign) event.getBlock().getState()) != 0) {

                event.setCancelled(true);

                BlockBreakEvent cevent = new BlockBreakEvent(event.getBlock(), event.getPlayer());

                nh.getServer().getPluginManager().callEvent(cevent);

                if (!cevent.isCancelled()) {

                    event.getBlock().breakNaturally();

                }

                cevent = null;

            }

        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {

        if (bc.runPlaceChecks(event.getPlayer(), event.getBlock()) != 0) {

            event.setCancelled(true);

        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onMount(VehicleEnterEvent event) {

        if (event.getEntered() instanceof Player) {

            Player p = (Player) event.getEntered();

            MoveData md = nh.vars.getMoveData(p.getName());

            md.lastmounting = System.currentTimeMillis();

            nh.vars.setMoveData(p.getName(), md);

        }

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)//Better make sure it's not canceled!
    public void onVelocity(PlayerVelocityEvent event) {

        if (event.getVelocity().getX() != 0 || event.getVelocity().getY() != 0 || event.getVelocity().getZ() != 0) {

            MoveData md = nh.vars.getMoveData(event.getPlayer().getName());

            double vy = Math.abs(event.getVelocity().getY());
            double vx = Math.abs(event.getVelocity().getX());
            double vz = Math.abs(event.getVelocity().getZ());

            md.yda = (((vy * 25)));

            md.velexpirey = (long) (System.currentTimeMillis() + (md.yda * 51) * 2);

            md.velexpirex = (long) (System.currentTimeMillis() + ((((md.mda == 0) ? 1 : md.mda) * (md.yda * 4)) * 140) * 10);

            md.mda = (vx + vz) * 50;

            nh.vars.setMoveData(event.getPlayer().getName(), md);

            //this.cc.doAntiKnockBackCheck(this.nh, event);

        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onLog(ViolationChangedEvent event) {

        //TODO Configurable logging

        File f = new File("hack_logs.txt");

        try {

            if (!f.exists()) {

                f.createNewFile();

            }

            PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(f, true)));

            w.println(event.getPlayer().getName() + " failed " + event.getCheckType().toString() + ". VL " + event.getNewLevel() + " TPS " + NoHack.tps);

            w.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onSneak(PlayerToggleSneakEvent event) {

        if (event.isSneaking() != event.getPlayer().isSneaking()) {

            MoveData md = nh.vars.getMoveData(event.getPlayer().getName());

            md.sneaktime = System.currentTimeMillis();

            md.wassneaking = !event.isSneaking();

            nh.vars.setMoveData(event.getPlayer().getName(), md);

        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onSprint(PlayerToggleSprintEvent event) {

        MoveData md = nh.vars.getMoveData(event.getPlayer().getName());

        md.sprinttime = System.currentTimeMillis();

        md.wassprinting = !event.isSprinting();

        nh.vars.setMoveData(event.getPlayer().getName(), md);

    }

    @EventHandler(ignoreCancelled = true)
    public void onFlight(PlayerToggleFlightEvent event) {

        if (event.isFlying() != event.getPlayer().isFlying()) {

            MoveData md = nh.vars.getMoveData(event.getPlayer().getName());

            md.flytime = System.currentTimeMillis();

            md.wasflying = !event.isFlying();

            nh.vars.setMoveData(event.getPlayer().getName(), md);

        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {

        if (event.getWhoClicked().getType() == EntityType.PLAYER) {

            if (this.invc.runInventoryChecks((Player) event.getWhoClicked(), event.getInventory(), event.getAction(), event) != 0) {

                event.setCancelled(true);

            }

        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityRegainHealthEvent(EntityRegainHealthEvent event) {

        if (event.getRegainReason() == RegainReason.SATIATED) {

            if (event.getEntity() instanceof Player) {

                Player p = (Player) event.getEntity();

                long diff = 1000;
                if (this.lastHealhed.containsKey(p.getName())) {
                    diff = (System.currentTimeMillis() - this.lastHealhed.get(p.getName()));
                }

                this.lastHealhed.put(p.getName(), System.currentTimeMillis());

                if (diff <= 3800) {

                    if (nh.vars.issueViolation(p, CheckType.GOD_MODE)) {

                        event.setCancelled(true);

                    }

                }

                p = null;

            }

        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {

        if (cc.checkFastEat(event.getPlayer())) {

            if (nh.vars.issueViolation(event.getPlayer(), CheckType.FAST_EAT)) {

                event.setCancelled(true);

            }

        }

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        if (event.hasItem()) {

            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

                Material m = event.getItem().getType();

                if (m == Material.BOW) {

                    cc.onStartingShoot(event.getPlayer());

                } else if (m == Material.WOOD_SWORD || m == Material.STONE_SWORD || m == Material.GOLD_SWORD || m == Material.IRON_SWORD || m == Material.DIAMOND_SWORD) {

                    MoveData md = nh.vars.getMoveData(event.getPlayer().getName());

                    md.blocktime = System.currentTimeMillis();

                    nh.vars.setMoveData(event.getPlayer().getName(), md);

                } else if (Utils.isFood(m)) {

                    this.cc.onStartEat(event.getPlayer());

                }

            }

        }

        if (event.getAction() != Action.PHYSICAL) {

            if (this.ic.runInteractChecks(event.getPlayer(), event) != 0) {

                event.setCancelled(true);

            }

        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {

        MoveData md = nh.vars.getMoveData(event.getPlayer().getName());

        md.lastloc = new XYZ(event.getPlayer().getLocation());

        md.tptime = System.currentTimeMillis();

        nh.vars.setMoveData(event.getPlayer().getName(), md);

    }

    @EventHandler(ignoreCancelled = true)
    public void onLogin(PlayerLoginEvent event) {

        //Special mod-disable codes
        event.getPlayer().sendMessage("�3 �9 �2 �0 �0 �2");
        event.getPlayer().sendMessage("�3 �9 �2 �0 �0 �1");
        event.getPlayer().sendMessage("�3 �9 �2 �0 �0 �3");
        event.getPlayer().sendMessage("�f �f �2 �0 �4 �8");
        event.getPlayer().sendMessage("�f �f �4 �0 �9 �6");
        event.getPlayer().sendMessage("�f �f �1 �0 �2 �4");
        event.getPlayer().sendMessage("�0�0�1�e�f");
        event.getPlayer().sendMessage("�0�0�2�3�4�5�6�7�e�f");
        event.getPlayer().sendMessage("�0�0�1�f�e");
        event.getPlayer().sendMessage("�0�0�2�f�e");
        event.getPlayer().sendMessage("�0�0�3�4�5�6�7�8�f�e");
        event.getPlayer().sendMessage("�0�1�0�1�2�f�f");
        event.getPlayer().sendMessage("�0�1�3�4�f�f");
        event.getPlayer().sendMessage("�0�1�5�f�f");
        event.getPlayer().sendMessage("�0�1�6�f�f");
        event.getPlayer().sendMessage("�0�1�8�9�a�b�f�f");
        event.getPlayer().sendMessage("�0�1�7�f�f");

        String[] s = nh.vars.getDeniedData(event.getPlayer());

        if (s != null) {

            long fut = Long.parseLong(s[0]);

            if (fut > System.currentTimeMillis()) {

                event.disallow(Result.KICK_OTHER, "Please wait " + Math.round((fut - System.currentTimeMillis()) / 1000) + " seconds before joining again.\nReason: " + s[1]);

            } else {

                nh.vars.removeDeniedLogin(event.getPlayer().getUniqueId());

            }

        }

    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerAttack(EntityDamageByEntityEvent event) {

        if (event.getDamager().getType() == EntityType.PLAYER) {

            if (event.getEntity() instanceof LivingEntity) {

                LivingEntity e = ((LivingEntity) event.getEntity());

                if ((e.getHealth() > 0)) {

                    final Player p = ((Player) event.getDamager());

                    long ls = nh.vars.getLastSwong(p.getName());

                    if (this.fc.runFightChecks(p, e, ls) != 0) {

                        e.setNoDamageTicks(20);
                        event.setCancelled(true);

                    }

                    //This fix makes the player a bit..."drunk"

					/*//A fix for stupid bukkit not taking account of ctrl sprint
                    //TODO Test this with craftbukkit and not spigot (and NMS?)
					if(p.isSprinting()){
						
						nh.getServer().getPluginManager().callEvent(new PlayerToggleSprintEvent(p, false));
						
						nh.getServer().getScheduler().runTaskLater(this.nh, new Runnable(){

							@Override
							public void run() {
								
								p.setSprinting(true);
								
							}
							
						}, 1);
					
					}*/

                }

                e = null;

            }

        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {

        if (this.bc.runBlockChecks(event.getPlayer(), event.getBlock(), nh.vars.getLastSwong(event.getPlayer().getName())) != 0) {

            event.setCancelled(true);

        }

    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBedLeaveEvent(PlayerBedLeaveEvent event) {

        if (event.getBed().getType() != Material.BED_BLOCK) {

            event.getPlayer().kickPlayer("Go find a real bed!");

        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onBow(EntityShootBowEvent event) {

        if (event.getEntity() instanceof Player) {

            Player p = (Player) event.getEntity();

            if (cc.onShoot(p, event.getForce())) {

                if (nh.vars.issueViolation(p, CheckType.FAST_BOW)) {

                    event.setCancelled(true);

                }

            }

            p = null;

        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectile(ProjectileLaunchEvent event) {

        if (event.getEntity().getShooter() != null) {

            if (event.getEntity().getShooter() instanceof Player) {

                Player p = (Player) event.getEntity().getShooter();

                if (cc.checkFastShoot(p)) {

                    if (nh.vars.issueViolation(p, CheckType.FAST_THROW)) {

                        event.setCancelled(true);

                    }

                }

                p = null;

            }

        }

    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBedLeaveEvent(PlayerBedEnterEvent event) {

        if (event.getBed().getType() != Material.BED_BLOCK) {

            event.getPlayer().kickPlayer("Go find a real bed!");

        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {

        if (this.chatc.runChatChecks(event.getPlayer(), event.getMessage()) != 0) {

            event.setCancelled(true);

        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerAnimationEvent(org.bukkit.event.player.PlayerAnimationEvent event) {

        if (event.getAnimationType() == PlayerAnimationType.ARM_SWING) {

            nh.vars.updateLastSwong(event.getPlayer().getName());

        }

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)//Make sure a plugin didn't cancel it
    public void onTeleport(PlayerTeleportEvent event) {

        if (event.getCause() != TeleportCause.UNKNOWN) {

            MoveData md = this.nh.vars.getMoveData(event.getPlayer().getName());

            md.tptime = System.currentTimeMillis();

            md.lastloc = new XYZ(event.getTo());

            this.nh.vars.setMoveData(event.getPlayer().getName(), md);

            if (Settings.debug) {

                Bukkit.broadcastMessage("Logged teleport for " + event.getPlayer().getName() + ". Cause=" + event.getCause().toString());

            }

        }

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerChangeWorlds(PlayerChangedWorldEvent event) {

        MoveData md = this.nh.vars.getMoveData(event.getPlayer().getName());

        md.tptime = System.currentTimeMillis();

        this.nh.vars.setMoveData(event.getPlayer().getName(), md);

        if (Settings.debug) {

            Bukkit.broadcastMessage("Logged teleport for " + event.getPlayer().getName() + ". Cause=WORLD_CHANGE");

        }

    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {

        if (event.getPlayer().isInsideVehicle() || event.getPlayer().isDead() || (event.getFrom().getX() == event.getTo().getX() && event.getFrom().getY() == event.getTo().getY() && event.getFrom().getZ() == event.getTo().getZ())) {

            return;

        }

        MoveData mdd = nh.vars.getMoveData(event.getPlayer().getName());

        if (mdd.lastloc == null) {

            mdd.lastloc = new XYZ(event.getPlayer().getLocation());

        }

        if ((System.currentTimeMillis() - mdd.tptime) < 1001) {//Player teleported, don't check it

            return;

        }

        double vd = (event.getTo().getY() - event.getFrom().getY());

        double yd = Math.abs((event.getFrom().getY() - event.getTo().getY()));//Vertical speed
        boolean up = (vd > 0);//Moving up?

        if (!up) {

            if (vd < 0) {

                //Anti weepcraft
                //If someone has a more efficient method please tell me!
                if (String.valueOf(vd).length() <= 5) {

                    up = true;

                    if (Settings.debug) {

                        Bukkit.broadcastMessage("Override up=true: length: " + String.valueOf(vd).length() + "; value=" + vd);

                    }

                }

            }

        }

        int id = mc.runMovingChecks(event.getPlayer(), event.getTo(), event.getFrom(), yd, Math.abs(event.getTo().getX() - event.getFrom().getX()), Math.abs(event.getTo().getZ() - event.getFrom().getZ()), mdd, up, Utils.inWater(event.getPlayer()), ((CraftPlayer) event.getPlayer()).getHandle().h_(), nh.vars.lastGround(event.getPlayer()));

        if (id == 1 || id == 2) {

            event.setCancelled(true);
            if (event.getPlayer().isInsideVehicle()) {

                event.getPlayer().getVehicle().teleport(new Location(event.getFrom().getWorld(), event.getFrom().getX(), event.getFrom().getY(), event.getFrom().getZ(), event.getTo().getYaw(), event.getTo().getPitch()), TeleportCause.UNKNOWN);

            } else {

                event.getPlayer().teleport(new Location(event.getFrom().getWorld(), event.getFrom().getX(), event.getFrom().getY(), event.getFrom().getZ(), event.getTo().getYaw(), event.getTo().getPitch()), TeleportCause.UNKNOWN);

            }

        } else if (id == 3) {

            event.setTo(nh.vars.lastGround(event.getPlayer()).toLocation(event.getTo().getPitch(), event.getTo().getYaw()));

        } else if (id == 4) {

            Location loc = event.getFrom();//nh.vars.lastGround(event.getPlayer()).toLocation(event.getTo().getPitch(), event.getTo().getYaw());

            double mmd = Double.MAX_VALUE;

            for (int x = loc.getBlockX() - 5; x < loc.getBlockX() + 5; x++) {

                for (int y = loc.getBlockY() - 5; y < loc.getBlockY() + 5; y++) {

                    for (int z = loc.getBlockZ() - 5; z < loc.getBlockZ() + 5; z++) {

                        Block b = loc.getWorld().getBlockAt(x, y, z);

                        if (b.getType().isSolid()) {

                            if (!b.getRelative(BlockFace.UP).getType().isSolid()) {

                                double d = b.getLocation().distanceSquared(loc);

                                if (d < mmd) {

                                    event.setTo(new Location(loc.getWorld(), x + 0.5, y + 1, z + 0.5, event.getTo().getYaw(), event.getTo().getPitch()));

                                    mmd = d;

                                }

                            }

                        }

                    }

                }

            }

        } else {

            if (mdd.wasonground != event.getPlayer().isOnGround()) {

                mdd.wasonground = event.getPlayer().isOnGround();
                mdd.groundtime = System.currentTimeMillis();
                this.nh.vars.setMoveData(event.getPlayer().getName(), mdd);

            }

        }

    }

}
