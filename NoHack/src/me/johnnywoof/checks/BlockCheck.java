package me.johnnywoof.checks;

import me.johnnywoof.Settings;
import me.johnnywoof.Variables;
import me.johnnywoof.check.CheckType;
import me.johnnywoof.util.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class BlockCheck {

    private Variables vars;

    private final HashMap<String, Long> lastBreak = new HashMap<String, Long>();
    private final HashMap<String, Long> lastSign = new HashMap<String, Long>();

    public BlockCheck(Variables vars) {

        this.vars = vars;

    }

    public int runSignChecks(Player p, Sign s) {

        long diff = (System.currentTimeMillis() - this.getLastSign(p.getName()));

        String mes = "";

        for (String e : s.getLines()) {

            mes = mes + e + ".";

        }

        if (diff < (mes.length() * 50)) {//TODO Test it...?

            return 1;

        }

        return 0;

    }

    public int runPlaceChecks(Player p, Block b) {

        Material m = b.getType();

        if (m == Material.SIGN_POST || m == Material.SIGN || m == Material.WALL_SIGN) {

            this.updateLastSign(p.getName());

        }

        return 0;

    }

    public int runBlockChecks(Player p, Block b, long ls) {

        if (p == null || b == null) {
            return 0;
        }

        //****************Start NoSwing******************

        if ((System.currentTimeMillis() - ls) >= Settings.noswingblock) {

            if (this.vars.issueViolation(p, CheckType.NOSWING)) {

                return 1;

            }

        }

        //****************End NoSwing******************

        //****************Start FastBreak******************

        long diff = (System.nanoTime() - this.getLastBreak(p.getName()));

        if (diff <= 90000) {

            if (this.vars.issueViolation(p, CheckType.SPEED_BREAK)) {

                return 1;

            }

        }

        long dm = TimeUnit.MILLISECONDS.convert(diff, TimeUnit.NANOSECONDS);

        long timemax = Utils.calcSurvivalFastBreak(p.getInventory().getItemInHand(), b.getType());

        this.lastBreak.put(p.getName(), System.nanoTime());

        if (dm < timemax) {

            if (this.vars.issueViolation(p, CheckType.FAST_BREAK)) {

                return 1;

            }

        }

        //****************End FastBreak******************

        return 0;

    }

    public void updateLastSign(String v) {

        this.lastSign.put(v, System.currentTimeMillis());

    }

    private long getLastSign(String v) {

        if (this.lastSign.containsKey(v)) {

            return this.lastSign.get(v);

        } else {

            return 0;

        }

    }

    private long getLastBreak(String v) {

        if (this.lastBreak.containsKey(v)) {

            return this.lastBreak.get(v);

        } else {

            return 0;

        }

    }

}
