package me.johnnywoof.threads;

import com.lenis0012.bukkit.npc.NPC;
import com.lenis0012.bukkit.npc.NPCAnimation;
import com.lenis0012.bukkit.npc.NPCProfile;
import me.johnnywoof.NoHack;
import me.johnnywoof.Settings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Random;

public class KillAuraThread implements Runnable {

    private final NoHack nh;
    private Random rand = new Random();

    //<Integer, Integer> = <player id>, <npc id>
    private final HashMap<Integer, Integer> killcheck = new HashMap<Integer, Integer>();

    public KillAuraThread(NoHack nh) {

        this.nh = nh;

    }

    @Override
    public void run() {

        for (Player p : Bukkit.getOnlinePlayers()) {

            if (!Settings.npcinvis && Settings.killnoannoy) {

                if (!p.getLocation().add(0, -1, 0).getBlock().getType().isSolid()) {

                    continue;

                }

            }

            //Gotta randomize the data!
            NPC npc = nh.factory.spawnHumanNPC(p.getLocation().add(0, -1.5, 0), new NPCProfile("lenis0012"));

            if (!Settings.killnoannoy) {

                npc.playAnimation(NPCAnimation.CROUCH);

            }

            npc.setYaw((float) rand.nextInt(360));
            npc.setGravity(false);

            if (Settings.npcinvis) {

                npc.getBukkitEntity().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 + rand.nextInt(200), 1));

            }

            npc.setInvulnerable(false);
            killcheck.put(p.getEntityId(), npc.getBukkitEntity().getEntityId());

        }

        nh.getServer().getScheduler().runTaskLater(nh, new Runnable() {

            @Override
            public void run() {

                nh.factory.despawnAll();
                killcheck.clear();

            }

        }, 10);

    }

    /*
     * Returns true if to cancel the event
     */
    public boolean onDamaged(NPC npc, Player p) {

        if (killcheck.containsKey(p.getEntityId())) {

            int id = killcheck.get(p.getEntityId());

            if (id == npc.getBukkitEntity().getEntityId()) {

                killcheck.remove(p.getEntityId());
                npc.getBukkitEntity().remove();

                for (String s : Settings.commands) {

                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s.replaceAll(".name.", p.getName()));

                }

                return true;

            }

        }

        return false;

    }

}
