package me.johnnywoof.checks;

import me.johnnywoof.Settings;
import me.johnnywoof.Variables;
import me.johnnywoof.check.CheckType;
import me.johnnywoof.util.MoveData;
import me.johnnywoof.util.Utils;
import me.johnnywoof.util.XYZ;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MovingCheck {

    private Variables vars;

    public MovingCheck(Variables vars) {

        this.vars = vars;

    }

    @SuppressWarnings("deprecation")
    public int runMovingChecks(Player p, Location to, Location from, double yd, double xs, double zs, MoveData movedata, boolean up, boolean inwater, boolean onladder, XYZ lg) {

        boolean jumped = (movedata.wasonground != p.isOnGround());

        if (!jumped) {

            if ((System.currentTimeMillis() - movedata.groundtime) < 600 && !inwater) {

                jumped = true;

            }

        }

        //****************Start Impossible Moving******************

        //Prevents bypass of packet sneak and enforcement of blocking
        if ((p.isSneaking() || p.isBlocking()) && p.isSprinting()) {

            if (p.isBlocking() && p.isSprinting()) {

                if ((System.currentTimeMillis() - movedata.blocktime) > 150) {

                    if (this.vars.issueViolation(p, CheckType.IMPOSSIBLE_MOVE)) {

                        return 1;

                    }

                }

            }

        }

        //****************End Impossible Moving******************

        //
        if (yd != 0) {//Actually move on the y axis

            if (up && onladder) {

                if (yd > ((p.getAllowFlight() || (jumped) ? 0.424 : 0.118))) {

                    if (this.vars.issueViolation(p, CheckType.VERTICAL_SPEED)) {

                        return 1;

                    }

                }

            }

            if (up) {

                if (yd > this.getMaxVertical(p, inwater, up)) {//Moving up only

                    if (this.vars.issueViolation(p, CheckType.VERTICAL_SPEED)) {

                        return 1;

                    }

                }

            }

        }
        //

        //****************Start Survival Fly******************

        if (p.isOnGround() || p.isInsideVehicle() || inwater || (p.isFlying()) || onladder) {

            this.vars.lastGround.put(p.getName(), new XYZ(from));

        } else {

            if (!p.getAllowFlight() && !inwater && !onladder) {//Ignore users that are allowed to fly. Doesn't count for the hack fly!

                if (up) {

                    double ydis = Math.abs(lg.y - to.getY());

                    if (ydis > this.getMaxHight(p, movedata)) {

                        if (this.vars.issueViolation(p, CheckType.FLY)) {

                            //I've discovered this trick on mineplex
                            p.setFlying(false);
                            p.setAllowFlight(false);

                            return 3;

                        }

                    }

                }

            }

        }

        //****************End Survival Fly******************

        //****************Start Horizontal Speed******************
        if ((System.currentTimeMillis() - movedata.lastmounting) > 200) {

            double ydis = Math.abs(lg.y - to.getY());

            if (xs > 0 || zs > 0) {

                double mxs = 0;

                boolean csneak = p.isSneaking();

                if (csneak) {

                    if (!movedata.wassneaking) {

                        long diff = (System.currentTimeMillis() - movedata.sneaktime);

                        if (diff < 501) {//There is a known bypass....gonna fix it sometime

                            csneak = false;

                        }

                    }

                }

                boolean csprint = p.isSprinting();

                if (!csprint) {

                    if ((System.currentTimeMillis() - movedata.sprinttime) < 1001) {

                        csprint = true;

                    }

                }

                boolean cfly = p.isFlying();

                if (!cfly) {

                    if ((System.currentTimeMillis() - movedata.flytime) < 2001) {

                        cfly = true;

                    }

                }

                if (cfly) {

                    mxs = (p.getFlySpeed() * 5.457);

                } else if (csprint) {

                    if (jumped) {//Player is jumping/landing

                        mxs = (p.getWalkSpeed() / 0.3);

                    } else {

                        mxs = (p.getWalkSpeed() / 0.71);

                    }

                } else if (csneak) {

                    if (jumped) {

                        mxs = (p.getWalkSpeed() / 1.7);//Lucky 7!

                    } else {

                        mxs = (p.getWalkSpeed() / 2);

                    }

                } else {

                    if (jumped) {

                        mxs = (p.getWalkSpeed() / 0.60);

                    } else {

                        mxs = (p.getWalkSpeed() / 0.85);

                    }

                }

                if (p.hasPotionEffect(PotionEffectType.SPEED)) {

                    int level = Utils.getPotionEffectLevel(p, PotionEffectType.SPEED);

                    if (level > 0) {

                        mxs = (mxs * (((level * 20) * 0.011) + 1));

                    }

                }

                if (xs > mxs || zs > mxs) {

                    if (this.vars.issueViolation(p, CheckType.HORIZONTAL_SPEED)) {

                        if (Settings.debug) {

                            //p.sendMessage("XS: " + xs + ";XZ:" + zs + "; Max: " + mxs + ";G: " + p.isOnGround() + ";WG: " + movedata.wasonground + ";GT: " + (System.currentTimeMillis() - movedata.groundtime));

                        }

                        return 1;

                    }

                }

            }

            if (!p.isOnGround() && !p.getAllowFlight() && !inwater && !onladder) {

                double mdis = this.getXZDistance(to.getX(), lg.x, to.getZ(), lg.z);

                if (mdis > this.getMaxMD(inwater, p.isOnGround(), p, ydis, movedata)) {

                    if (this.vars.issueViolation(p, CheckType.GLIDE)) {

                        return 1;

                    }

                }

            }

        }
        //****************End Horizontal Speed******************

        //****************Start NoFall*****************
        //If flying, ignore this check
        if (!p.isFlying()) {

            //Prevent bypassing fly checks when moving in an horiztonal motion
            if (!inwater && !p.getAllowFlight() && p.isOnGround() && !jumped && !onladder) {//User is allowed to fly, why check it!

                //There needs to be a waaaaaaaay more efficient way to calculate this

                Material m = null;

                int bx = to.getBlockX();
                int by = to.getBlockY();
                int bz = to.getBlockZ();

                if (bx != from.getBlockX() || bz != from.getBlockZ()) {

                    int boy = by + 1;

                    int oy = boy;

                    boolean con = true;

                    while (con) {

                        boy--;

                        m = from.getBlock().getRelative(0, ((oy - boy) * -1) + 1, 0).getType();

                        if (m.isSolid()) {

                            con = false;
                            break;

                        }

                        if (boy < 0) {

                            con = false;//Safe check for flying over bedrock...which should be impossible
                            break;

                        }

                    }

                    int dis = (oy - boy);

                    if (dis > 2) {//Prevent bypassing fly checks when moving in an horiztonal motion

                        boolean nearblock = false;

                        //Make sure they are not standing at the end of a block
                        for (int x = bx - 1; x < bx + 1; x++) {

                            for (int z = bz - 1; z < bz + 1; z++) {

                                if (p.getWorld().getBlockAt(x, (to.getBlockY() - 1), z).getType().isSolid()) {

                                    nearblock = true;
                                    break;

                                }

                            }

                        }

                        if (!nearblock) {

                            if (this.vars.issueViolation(p, CheckType.FLY)) {

                                return 1;

                            }

                        }

                    }

                }

            }

            //Start nofall & fly check
            if (!p.getAllowFlight()) {

                if (to.getBlockY() != from.getBlockY()) {

                    if (up && p.isOnGround() && !inwater) {

                        if (p.getVelocity().getY() < 0) {//Moving up when velocity says to go down...seems legit

                            Material m = from.getBlock().getType();

                            if (m != Material.CHEST && m != Material.TRAPPED_CHEST) {

                                if (this.vars.issueViolation(p, CheckType.NOFALL)) {

                                    return 1;

                                }

                            }

                        }

                    }

                }

            }
            //End nofall & fly check

            if (!up && yd > 0.25 && p.isOnGround()) { //Falling while onground? I DON'T THINK SO

                if (this.vars.issueViolation(p, CheckType.NOFALL)) {

                    return 4;

                }

            }

        }

        //****************End NoFall******************

        //****************Start Timer******************
        if (to.getX() != from.getX() || to.getY() != from.getY() || to.getZ() != from.getZ()) {

            if ((System.currentTimeMillis() - movedata.getTimeStart()) > 500) {

                int max = 0;

                max = Math.round((Utils.getPing(p) / 100));

                if (max < 0) {

                    max = 0;

                }

                max = max + Settings.maxpacket;

                if (movedata.getAmount() > max) {

                    //Maybe block the checkpoint exploit?
                    //double xzdis = Utils.getXZDistance(movedata.lastloc.x, to.getX(), movedata.lastloc.z, to.getZ());

                    if (this.vars.issueViolation(p, CheckType.TIMER)) {

                        if (movedata.getAmount() > 50) {

                            p.kickPlayer("Too many packets! Are you (or the server) lagging badly?");

                        } else {

                            p.teleport(movedata.lastloc.toLocation(to.getPitch(), to.getYaw()), TeleportCause.UNKNOWN);

                        }

                        movedata.reset(movedata.lastloc);

                    }

                } else {

                    movedata.reset(new XYZ(from));

                }

            } else {

                movedata.setAmount(movedata.getAmount() + 1);

            }

            this.vars.setMoveData(p.getName(), movedata);

        }
        //****************End Timer******************

        return 0;

    }

    //****************API METHODS*********************
    private double getMaxHight(Player p, MoveData md) {

        double d = 0;

        if (p.hasPotionEffect(PotionEffectType.JUMP)) {

            int level = Utils.getPotionEffectLevel(p, PotionEffectType.JUMP);

            if (level == 1) {

                d = 1.9;

            } else if (level == 2) {

                d = 2.7;

            } else if (level == 3) {

                d = 3.36;

            } else if (level == 4) {

                d = 4.22;

            } else if (level == 5) {

                d = 5.16;

            } else if (level == 6) {

                d = 6.19;

            } else if (level == 7) {

                d = 7.31;

            } else if (level == 8) {

                d = 8.5;

            } else if (level == 9) {

                d = 9.76;

            } else if (level == 10) {

                d = 11.1;

            } else {

                d = (level) + 1;

            }

            d = d + 1.35;

        } else {

            d = 1.35;

        }

        if (md.yda != 0 && (System.currentTimeMillis() < md.velexpirey)) {

            d = d + md.yda;

        }

        return d;

    }

    private double getMaxVertical(Player p, boolean inwater, boolean up) {

        double d = 0.5;

        if (p.hasPotionEffect(PotionEffectType.JUMP)) {

            d = d + ((this.getPotionEffectLevel(p, PotionEffectType.JUMP)) * 0.11);

        }

        if (inwater && !p.getAllowFlight()) {

            if (up) {

                d = 0.3401;

            } else {

                d = Math.abs(p.getVelocity().getY());

            }

        }

        if (p.getVelocity().getY() > 0) {

            d = d + (p.getVelocity().getY());

        }

        return d;

    }

    private int getPotionEffectLevel(Player p, PotionEffectType pet) {

        for (PotionEffect pe : p.getActivePotionEffects()) {

            if (pe.getType().getName().equals(pet.getName())) {

                return pe.getAmplifier() + 1;

            }

        }

        return 0;

    }

    private double getMaxMD(boolean inwater, boolean onground, Player p, double ydis, MoveData md) {

        double d = 0D;

        boolean csneak = p.isSneaking();
        boolean csprint = p.isSprinting();
        boolean cblock = p.isBlocking();

        long now = System.currentTimeMillis();

        if (cblock) {

            if ((now - md.blocktime) <= 1500) {

                cblock = false;

            }

        }

        if (!csneak) {

            if ((now - md.sneaktime) <= 1000) {

                csneak = true;

            }

        } else {

            if (!onground) {

                if ((now - md.sneaktime) <= 1000) {

                    csneak = false;

                }

            }

        }

        if (!csprint) {

            if ((now - md.sprinttime) <= 1000) {

                csprint = true;

            }

        }

        //TODO Account jump effect

        if (p.isFlying()) {

            d = 1.30;

        } else if (csprint) {

            d = (18.3 + d) + (8 * ydis);

        } else if (csneak) {

            if (onground) {

                d = 0.065;

            } else {

                d = 0.67;

            }

        } else if (cblock) {

            d = 0.015;

        } else {

            d = (5.6 + d) + (3 * ydis);

        }

        if (md.mda != 0 && (System.currentTimeMillis() < md.velexpirex)) {

            d = d + md.mda;

        }

        return d;

    }

    private double getXZDistance(double x1, double x2, double z1, double z2) {

        double a1 = (x2 - x1), a2 = (z2 - z1);

        return ((a1 * (a1)) + (a2 * a2));

    }

}
