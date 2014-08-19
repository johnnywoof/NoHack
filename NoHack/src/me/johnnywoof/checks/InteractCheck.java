package me.johnnywoof.checks;

import me.johnnywoof.Variables;
import me.johnnywoof.check.CheckType;
import me.johnnywoof.util.Utils;
import me.johnnywoof.util.XYZ;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class InteractCheck {

    private final HashMap<String, Long> lastinteractright = new HashMap<String, Long>();
    private final HashMap<String, Long> lastinteractleft = new HashMap<String, Long>();

    private Variables vars;

    public InteractCheck(Variables vars) {

        this.vars = vars;

    }

    public int runInteractChecks(Player p, PlayerInteractEvent event) {

        //****************Start FastInteract******************
        //Might remove fast interact right click, since spigot now checks it

        if (event.hasBlock() && event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            if (event.getClickedBlock().getType().toString().toLowerCase().contains("rail") || event.getClickedBlock().isLiquid()) {

                ItemStack i = event.getPlayer().getItemInHand();

                if (i != null) {

                    Material m = i.getType();

                    if (m == Material.MINECART || m == Material.HOPPER_MINECART || m == Material.COMMAND_MINECART ||
                            m == Material.BOAT || m == Material.STORAGE_MINECART || m == Material.POWERED_MINECART ||
                            m == Material.EXPLOSIVE_MINECART || m == Material.BOAT) {

                        return 0;

                    }

                }

            }

            long diff = 0;

            if (this.lastinteractright.containsKey(p.getName())) {
                diff = (System.currentTimeMillis() - this.lastinteractright.get(p.getName()));

                int am = 145;

                this.lastinteractright.put(p.getName(), System.currentTimeMillis());

                if (this.lastinteractleft.containsKey(p.getName())) {

                    if ((System.nanoTime() - this.lastinteractleft.get(p.getName())) <= 100000000) {

                        am = 90;

                    }

                }

                if (diff <= am) {

                    if (this.vars.issueViolation(p, CheckType.FAST_INTERACT)) {

                        return 1;

                    }

                }
            } else {

                this.lastinteractright.put(p.getName(), System.currentTimeMillis());

            }

            Material m = event.getClickedBlock().getType();

            if (m == Material.CHEST || m == Material.TRAPPED_CHEST || m == Material.BREWING_STAND || m == Material.ENDER_CHEST || m == Material.ANVIL || m == Material.TRAP_DOOR || m == Material.IRON_DOOR_BLOCK || m == Material.WOODEN_DOOR
                    || m == Material.BEACON || m == Material.BURNING_FURNACE || m == Material.CAKE_BLOCK || m == Material.CAULDRON || m == Material.BED_BLOCK || m == Material.COMMAND || m == Material.DIODE_BLOCK_OFF || m == Material.DIODE_BLOCK_ON || m == Material.DISPENSER ||
                    m == Material.WORKBENCH || m == Material.WOOD_BUTTON || m == Material.NOTE_BLOCK || m == Material.STONE_BUTTON || m == Material.JUKEBOX || m == Material.HOPPER || m == Material.DRAGON_EGG || m == Material.DROPPER || m == Material.FENCE_GATE ||
                    m == Material.FURNACE || m == Material.ENCHANTMENT_TABLE) {

                if (!Utils.canSee(p, event.getClickedBlock().getRelative(event.getBlockFace()).getLocation()) && !new XYZ(event.getClickedBlock().getLocation()).equalsLoc(new XYZ(p.getEyeLocation().getBlock().getLocation()))) {

                    if (this.vars.issueViolation(p, CheckType.BLOCK_VISIBLE)) {

                        return 1;

                    }

                }

            }

        } else if (event.hasBlock() && event.getAction() == Action.LEFT_CLICK_BLOCK) {

            long diff = 0;

            if (this.lastinteractleft.containsKey(p.getName())) {

                diff = (System.nanoTime() - this.lastinteractleft.get(p.getName()));

                this.lastinteractleft.put(p.getName(), System.nanoTime());

                if (diff <= 180000) {

                    if (this.vars.issueViolation(p, CheckType.FAST_INTERACT)) {

                        return 1;

                    }

                }
            } else {

                this.lastinteractleft.put(p.getName(), System.nanoTime());

            }

        }

        //****************End FastInteract******************

        return 0;

    }

}
