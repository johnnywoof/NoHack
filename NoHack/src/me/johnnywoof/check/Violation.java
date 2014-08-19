package me.johnnywoof.check;

import me.johnnywoof.event.ViolationChangedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Violation {

    private int fly, fab, vs, hs, sb, bv, fv, autos, fkb, fe, ns, fbow, gm, crit, fr, fspeed, fp, timer, spam, fc, as, nf, is, gl, nkb, fullb,
            imc, imclick, imf, imm;
    private long lastnotification = 0;

    public boolean resetLevel(CheckType ct, Player p) {

        int old = this.getLevel(ct);

        this.setLevel(0, p, ct);

        if (old == this.getLevel(ct)) {

            return true;

        } else {

            return false;

        }

    }

    public int setLevel(int level, Player p, CheckType ct) {

        ViolationChangedEvent vce = new ViolationChangedEvent(level, this.getLevel(ct), ct, p);

        Bukkit.getPluginManager().callEvent(vce);

        if (vce.isCancelled()) {
            return vce.getOldLevel();
        }

        switch (ct) {
            case ATTACK_REACH:
                this.fr = vce.getNewLevel();
                break;
            case FAST_BREAK:
                this.fab = vce.getNewLevel();
                break;
            case ATTACK_SPEED:
                this.fspeed = vce.getNewLevel();
                break;
            case CRITICAL:
                this.crit = vce.getNewLevel();
                break;
            case FAST_THROW:
                this.fp = vce.getNewLevel();
                break;
            case FLY:
                this.fly = vce.getNewLevel();
                break;
            case GOD_MODE:
                this.gm = vce.getNewLevel();
                break;
            case HORIZONTAL_SPEED:
                this.hs = vce.getNewLevel();
                break;
            case NOSWING:
                this.ns = vce.getNewLevel();
                break;
            case SPEED_BREAK:
                this.sb = vce.getNewLevel();
                break;
            case VERTICAL_SPEED:
                this.vs = vce.getNewLevel();
                break;
            case TIMER:
                this.timer = vce.getNewLevel();
                break;
            case SPAM:
                this.spam = vce.getNewLevel();
                break;
            case SPEED_CLICK:
                this.fc = vce.getNewLevel();
                break;
            case AUTOSOUP:
                this.as = vce.getNewLevel();
                break;
            case NOFALL:
                this.nf = vce.getNewLevel();
                break;
            case FAST_INTERACT:
                this.is = vce.getNewLevel();
                break;
            case GLIDE:
                this.gl = vce.getNewLevel();
                break;
            case NOKNOCKBACK:
                this.nkb = vce.getNewLevel();
                break;
            case FAST_BOW:
                this.fbow = vce.getNewLevel();
                break;
            case FIGHT_KNOCKBACK:
                this.fkb = vce.getNewLevel();
                break;
            case FAST_EAT:
                this.fe = vce.getNewLevel();
                break;
            case AUTOSIGN:
                this.autos = vce.getNewLevel();
                break;
            case BLOCK_VISIBLE:
                this.bv = vce.getNewLevel();
                break;
            case FIGHT_VISIBLE:
                this.fv = vce.getNewLevel();
                break;
            case FULLBRIGHT:
                this.fullb = vce.getNewLevel();
                break;
            case IMPOSSIBLE_CHAT:
                this.imc = vce.getNewLevel();
                break;
            case IMPOSSIBLE_CLICK:
                this.imclick = vce.getNewLevel();
                break;
            case IMPOSSIBLE_FIGHT:
                this.imf = vce.getNewLevel();
                break;
            case IMPOSSIBLE_MOVE:
                this.imm = vce.getNewLevel();
                break;
        }

        return vce.getOldLevel();

    }

    public int getLevel(CheckType ct) {

        switch (ct) {
            case ATTACK_REACH:
                return this.fr;
            case ATTACK_SPEED:
                return this.fspeed;
            case CRITICAL:
                return this.crit;
            case FAST_THROW:
                return this.fp;
            case FLY:
                return this.fly;
            case GOD_MODE:
                return this.gm;
            case HORIZONTAL_SPEED:
                return this.hs;
            case NOSWING:
                return this.ns;
            case SPEED_BREAK:
                return this.sb;
            case VERTICAL_SPEED:
                return this.vs;
            case TIMER:
                return this.timer;
            case SPAM:
                return this.spam;
            case SPEED_CLICK:
                return this.fc;
            case AUTOSOUP:
                return this.as;
            case NOFALL:
                return this.nf;
            case FAST_INTERACT:
                return this.is;
            case GLIDE:
                return this.gl;
            case NOKNOCKBACK:
                return this.nkb;
            case FAST_BOW:
                return this.fbow;
            case FIGHT_KNOCKBACK:
                return this.fkb;
            case FAST_EAT:
                return this.fe;
            case AUTOSIGN:
                return this.autos;
            case BLOCK_VISIBLE:
                return this.bv;
            case FIGHT_VISIBLE:
                return this.fv;
            case FULLBRIGHT:
                return this.fullb;
            case IMPOSSIBLE_CHAT:
                return this.imc;
            case IMPOSSIBLE_CLICK:
                return this.imclick;
            case IMPOSSIBLE_FIGHT:
                return this.imf;
            case IMPOSSIBLE_MOVE:
                return this.imm;
            case FAST_BREAK:
                return this.fab;
        }

        return 0;

    }

    public boolean raiseLevel(CheckType ct, Player p) {

        int old = this.getLevel(ct);

        this.setLevel(old + 1, p, ct);

        if (old == this.getLevel(ct)) {

            return true;

        } else {

            return false;

        }

    }

    public void updateNotify() {
        this.lastnotification = System.currentTimeMillis();
    }

    public boolean shouldNotify() {

        return (System.currentTimeMillis() - this.lastnotification) >= 5000;

    }

}
