package me.johnnywoof;

import me.johnnywoof.check.CheckType;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Settings {

    private static String nofallmessage = "";
    private static String timermes = "";
    private static String impossiblemovemes = "";
    private static String verticalspeedmes = "";
    private static String horizontalspeedmes = "";
    private static String flymes = "";
    private static String godmodemes = "";
    private static String fightspeed = "";
    private static String fightreach = "";
    private static String impossibleclick = "";
    private static String glidemes = "";
    private static String speedclick = "";
    private static String noswingmes = "";
    private static String autosoupmes = "";
    private static String speedbreakmes = "";
    private static String fightknock = "";
    private static String impossiblechat = "";
    private static String blockvisiblemes = "";
    private static String fightvisiblemes = "";
    private static String impossiblefightmes = "";
    private static String fasteatmes = "";
    private static String format = "";
    public static long noswingblock;
    public static long attackspeed;

    public static boolean debug = true;
    public static long noswingfight;
    public static long fcs;
    public static int maxpacket;

    public static boolean killaura = true;
    public static boolean npcinvis = true;
    public static boolean killnoannoy = true;
    public static List<String> commands = new ArrayList<String>();

    public static void reload(FileConfiguration fc) {

        Settings.impossiblechat = fc.getString("chat-impossible-message");
        Settings.nofallmessage = fc.getString("nofall-message");
        Settings.timermes = fc.getString("timer-message");
        Settings.impossiblemovemes = fc.getString("impossible-move-message");
        Settings.verticalspeedmes = fc.getString("vertical-speed-message");
        Settings.horizontalspeedmes = fc.getString("horizontal-speed-message");
        Settings.flymes = fc.getString("fly-message");
        Settings.glidemes = fc.getString("glide-message");

        Settings.blockvisiblemes = fc.getString("block-visible-break-message");
        Settings.speedbreakmes = fc.getString("speed-break-message");
        Settings.godmodemes = fc.getString("godmode-message");
        Settings.fightknock = fc.getString("fight-knockback-message");
        Settings.impossiblefightmes = fc.getString("fight-impossible-message");
        Settings.fightvisiblemes = fc.getString("fight-visible-message");
        Settings.fightspeed = fc.getString("fight-speed-message");
        Settings.fightreach = fc.getString("fight-reach-message");
        Settings.autosoupmes = fc.getString("autosoup-message");

        Settings.impossibleclick = fc.getString("inventory-impossible-message");
        Settings.speedclick = fc.getString("inventory-click-speed");
        Settings.noswingmes = fc.getString("noswing-message");
        Settings.fasteatmes = fc.getString("fast-eat-message");

        Settings.noswingblock = fc.getLong("noswing-block-difference");
        Settings.noswingfight = fc.getLong("noswing-fight-difference");
        Settings.attackspeed = fc.getLong("fight-attack-speed");
        Settings.fcs = fc.getLong("inventory-click-max-speed");
        Settings.maxpacket = fc.getInt("max-packets");

        Settings.format = fc.getString("format");

        Settings.killaura = fc.getBoolean("check-killaura");
        Settings.npcinvis = fc.getBoolean("npc-invisible");
        Settings.commands = fc.getStringList("check-killaura-commands");
        Settings.killnoannoy = fc.getBoolean("check-killaura-no-annoy");

    }

    public static String getFormatted(String name, CheckType ct, int vl) {

        String mes = Settings.format;

        mes = mes.replaceAll(".vl.", vl + "");
        mes = mes.replaceAll(".type.", ct.toString().toLowerCase().replaceAll("_", " "));
        mes = mes.replaceAll(".name.", name);
        mes = mes.replaceAll(".message.", getUnFormatMessage(ct));
        mes = mes.replaceAll(".tps.", NoHack.tps + "");

        mes = mes.replaceAll("&", ChatColor.COLOR_CHAR + "");

        return mes;

    }

    public static String getUnFormatMessage(CheckType ct) {

        switch (ct) {
            case ATTACK_REACH:
                return Settings.fightreach;
            case FAST_BREAK:
                return "";
            case ATTACK_SPEED:
                return Settings.fightspeed;
            case AUTOSIGN:
                return "";
            case AUTOSOUP:
                return Settings.autosoupmes;
            case CRITICAL:
                return "";
            case FAST_BOW:
                return "";
            case FAST_EAT:
                return Settings.fasteatmes;
            case FAST_INTERACT:
                return "";
            case FAST_THROW:
                return "";
            case FIGHT_KNOCKBACK:
                return Settings.fightknock;
            case FLY:
                return Settings.flymes;
            case FULLBRIGHT:
                return "";
            case GLIDE:
                return Settings.glidemes;
            case GOD_MODE:
                return Settings.godmodemes;
            case HORIZONTAL_SPEED:
                return Settings.horizontalspeedmes;
            case IMPOSSIBLE_MOVE:
                return Settings.impossiblemovemes;
            case NOFALL:
                return Settings.nofallmessage;
            case NOKNOCKBACK:
                return "";
            case NOSWING:
                return Settings.noswingmes;
            case SPAM:
                return "";
            case SPEED_BREAK:
                return Settings.speedbreakmes;
            case SPEED_CLICK:
                return Settings.speedclick;
            case TIMER:
                return Settings.timermes;
            case VERTICAL_SPEED:
                return Settings.verticalspeedmes;
            case IMPOSSIBLE_CLICK:
                return Settings.impossibleclick;
            case IMPOSSIBLE_CHAT:
                return Settings.impossiblechat;
            case IMPOSSIBLE_FIGHT:
                return Settings.impossiblefightmes;
            case BLOCK_VISIBLE:
                return Settings.blockvisiblemes;
            case FIGHT_VISIBLE:
                return Settings.fightvisiblemes;

        }

        return "MissingNo";

    }

}
