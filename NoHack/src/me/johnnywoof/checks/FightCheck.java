package me.johnnywoof.checks;

import me.johnnywoof.Settings;
import me.johnnywoof.Variables;
import me.johnnywoof.check.CheckType;
import me.johnnywoof.util.MoveData;
import me.johnnywoof.util.Utils;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftLivingEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class FightCheck {

    private Variables vars;

    private HashMap<String, Long> lastAttack = new HashMap<String, Long>();

    public FightCheck(Variables vars) {

        this.vars = vars;

    }

    @SuppressWarnings("deprecation")
    public int runFightChecks(Player p, LivingEntity e, long ls) {

        //****************Start Fight Impossible******************

        if (p.isBlocking() || p.isSleeping() || p.isDead() || p.getEntityId() == e.getEntityId()) {

            if (this.vars.issueViolation(p, CheckType.IMPOSSIBLE_FIGHT)) {

                return 1;

            }

        }

        //****************End Fight Impossible******************

        //****************Start Fight Visible******************

        //Kind of performance heavy, only check it when they get the damage
        if (((CraftLivingEntity) e).getHandle().hurtTicks <= 3) {

            if (!Utils.canReallySeeEntity(p, e)) {

                if (this.vars.issueViolation(p, CheckType.FIGHT_VISIBLE)) {

                    return 1;

                }

            }

        }

        //****************End Fight Visible******************

        //****************Start Fight NoSwing******************

        if ((System.currentTimeMillis() - ls) >= Settings.noswingfight) {

            if (this.vars.issueViolation(p, CheckType.NOSWING)) {

                return 1;

            }

        }

        //****************End Fight NoSwing******************

        //****************Start Fight Reach******************

        //TODO Make this a better reach check
        if (e.getType() != EntityType.WITHER) {

            //double d = p.getEyeLocation().distanceSquared(e.getEyeLocation());

            //reach check

            //if(d > ((p.getGameMode() == GameMode.CREATIVE) ? Settings.creativeattack : Settings.survivalattack)){

            //if(this.vars.issueViolation(p, CheckType.ATTACK_REACH)){

            //	return 1;

            //}

            //}

        }

        //****************End Fight Reach******************

        //****************Start Fight Speed******************

        if (this.lastAttack.containsKey(p.getName())) {

            long diff = (System.currentTimeMillis() - this.lastAttack.get(p.getName()));

            if (diff <= Settings.attackspeed) {

                if (this.vars.issueViolation(p, CheckType.ATTACK_SPEED)) {

                    return 1;

                }

            }

        }

        this.registerLastAttack(p.getName());

        //****************End Fight Speed******************

        //****************Start Fight Knockback******************

        if (p.getItemInHand() != null) {

            if (!p.getItemInHand().containsEnchantment(Enchantment.KNOCKBACK)) {

                MoveData md = this.vars.getMoveData(p.getName());

                if ((System.currentTimeMillis() - md.sprinttime) < 15) {

                    if (this.vars.issueViolation(p, CheckType.FIGHT_KNOCKBACK)) {

                        return 1;

                    }

                }

            }

        }

        //****************End Fight Knockback******************

        //****************Start Fight Criticals******************

        if (!p.isOnGround() && !p.getAllowFlight()) {

            if (p.getLocation().getY() % 1 == 0) {

                if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {

                    if (this.vars.issueViolation(p, CheckType.CRITICAL)) {

                        return 1;

                    }

                }

            }

        }

        //****************End Fight Criticals******************

        //****************Start Fight AutoSoup******************


        //****************End Fight AutoSoup******************

        return 0;
    }

    private void registerLastAttack(String v) {

        this.lastAttack.put(v, System.currentTimeMillis());

    }

}
