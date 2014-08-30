package me.johnnywoof.checks;

import me.johnnywoof.Settings;
import me.johnnywoof.Variables;
import me.johnnywoof.check.CheckType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class InventoryCheck {

    private Variables vars;

    private final HashMap<String, Long> lastclick = new HashMap<String, Long>();
    private final HashMap<String, Long> lastviolation = new HashMap<String, Long>();

    public InventoryCheck(Variables vars) {
        this.vars = vars;
    }

    public int runInventoryChecks(Player p, Inventory inv, InventoryAction ia, InventoryClickEvent event) {

        if (p.isBlocking() || p.isSneaking() || p.isSprinting() || p.isSleeping()) {

            if (this.vars.issueViolation(p, CheckType.IMPOSSIBLE_CLICK)) {

                return 1;

            }

        }

        long rdif = (System.currentTimeMillis() - this.getLastViolation(p.getName()));

        if (rdif <= 2000) {

            return 1;//Prevent abuse to bypass check

        }

        if (ia == InventoryAction.NOTHING) {//Who was the idiot that added this -_-

            return 0;

        }

        if (p.getGameMode() == GameMode.CREATIVE) {

            if (inv.getType() == InventoryType.PLAYER || inv.getType() == InventoryType.CREATIVE) {

                if (event.getSlotType() != SlotType.OUTSIDE) {//We want to check for fastdrops which in turn = lag

                    this.lastviolation.put(p.getName(), System.currentTimeMillis());

                    return 0;

                }

            }

        }

        if (Settings.debug) {

            Bukkit.broadcastMessage("Type: " + inv.getType() + "; Action: " + ia.toString().toLowerCase());

        }

        long diff = (System.currentTimeMillis() - this.getLastClicked(p.getName()));

        if (diff <= Settings.fcs) {

            if (this.vars.issueViolation(p, CheckType.SPEED_CLICK)) {

                return 1;

            }

        }

        this.lastclick.put(p.getName(), System.currentTimeMillis());

        return 0;

    }

    private long getLastViolation(String v) {

        if (this.lastviolation.containsKey(v)) {

            return this.lastviolation.get(v);

        } else {

            return 0;

        }

    }

    private long getLastClicked(String v) {

        if (this.lastclick.containsKey(v)) {

            return this.lastclick.get(v);

        } else {

            return 0;

        }

    }

}
