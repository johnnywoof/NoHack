package me.johnnywoof.util;

import net.minecraft.server.v1_7_R4.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Utils {

    public static boolean canSee(Player player, Location loc2) {
        return ((CraftWorld) player.getLocation().getWorld()).getHandle().a(Vec3D.a(player.getLocation().getX(), player.getLocation().getY() + player.getEyeHeight(), player.getLocation().getZ()), Vec3D.a(loc2.getX(), loc2.getY(), loc2.getZ())) == null;
    }

    public static boolean isFood(Material m) {

        return (m == Material.COOKED_BEEF || m == Material.COOKED_CHICKEN || m == Material.COOKED_FISH
                || m == Material.GRILLED_PORK || m == Material.PORK || m == Material.MUSHROOM_SOUP
                || m == Material.RAW_BEEF || m == Material.RAW_CHICKEN || m == Material.RAW_FISH
                || m == Material.APPLE || m == Material.GOLDEN_APPLE || m == Material.MELON
                || m == Material.COOKIE || m == Material.BREAD || m == Material.SPIDER_EYE
                || m == Material.ROTTEN_FLESH || m == Material.POTATO_ITEM);

    }

    /**
     * Calculate the time in milliseconds that it should take to break the given block with the given tool
     *
     * @param tool  tool to check
     * @param block block to check
     * @return time in milliseconds to break
     */
    public static long calcSurvivalFastBreak(ItemStack tool, Material block) {
        if (isInstantBreak(block) || (tool.getType() == Material.SHEARS && block == Material.LEAVES)) {
            return 0;
        }
        double bhardness = BlockHardness.getBlockHardness(block);
        double thardness = ToolHardness.getToolHardness(tool.getType());
        long enchantlvl = (long) tool.getEnchantmentLevel(Enchantment.DIG_SPEED);

        long result = Math.round((bhardness * thardness) * 0.10 * 10000);

        if (enchantlvl > 0) {
            result /= enchantlvl * enchantlvl + 1L;
        }

        result = result > 25000 ? 25000 : result < 0 ? 0 : result;

        if (isQuickCombo(tool, block)) {
            result = result / 2;
        }

        return result;
    }

    private static boolean isQuickCombo(ItemStack tool, Material m) {

        if (tool.getType() == Material.DIAMOND_SWORD || tool.getType() == Material.IRON_SWORD || tool.getType() == Material.STONE_SWORD || tool.getType() == Material.GOLD_SWORD || tool.getType() == Material.WOOD_SWORD) {

            return m == Material.WEB;

        } else if (tool.getType() == Material.SHEARS) {

            return m == Material.WOOL;

        }

        return false;

    }

    public static boolean isInstantBreak(Material m) {

        return m == Material.TORCH || m == Material.FLOWER_POT || m == Material.RED_ROSE || m == Material.YELLOW_FLOWER || m == Material.LONG_GRASS
                || m == Material.RED_MUSHROOM || m == Material.BROWN_MUSHROOM || m == Material.TRIPWIRE || m == Material.TRIPWIRE_HOOK ||
                m == Material.DEAD_BUSH || m == Material.DIODE_BLOCK_OFF || m == Material.DIODE_BLOCK_ON || m == Material.REDSTONE_COMPARATOR_OFF
                || m == Material.REDSTONE_COMPARATOR_OFF || m == Material.REDSTONE_WIRE || m == Material.REDSTONE_TORCH_OFF ||
                m == Material.REDSTONE_TORCH_ON || m == Material.DOUBLE_PLANT || m == Material.SUGAR_CANE_BLOCK;

    }

    public static boolean canReallySeeEntity(Player p, LivingEntity e) {

        BlockIterator bl = new BlockIterator(p, 7);

        boolean found = false;

        double md = 1;

        if (e.getType() == EntityType.WITHER) {//Withers are a bit trippy on distance calculations

            md = 9;

        } else if (e.getType() == EntityType.ENDERMAN) {

            md = 5;

        } else {

            md = md + e.getEyeHeight();

        }

        while (bl.hasNext()) {

            found = true;

            double d = bl.next().getLocation().distanceSquared(e.getLocation());

            if (d <= md) {

                return true;

            }

        }

        bl = null;

        if (!found) {

            return true;//So close to the entity block paths were not generated!

        }

        return false;

    }

    /**
     * @deprecated Inaccurate on some entities
     */
    @Deprecated
    public static LivingEntity getTarget(Player player) {
        int range = 8;
        ArrayList<LivingEntity> livingE = new ArrayList<LivingEntity>();

        for (Entity e : player.getNearbyEntities(range, range, range)) {
            if (e instanceof LivingEntity) {
                livingE.add((LivingEntity) e);
            }
        }

        LivingEntity target = null;
        BlockIterator bItr = new BlockIterator(player, range);
        Block block;
        Location loc;
        int bx, by, bz;
        double ex, ey, ez, md = Double.MAX_VALUE, d;
        // loop through player's line of sight
        while (bItr.hasNext()) {
            block = bItr.next();
            bx = block.getX();
            by = block.getY();
            bz = block.getZ();
            // check for entities near this block in the line of sight
            for (LivingEntity e : livingE) {
                loc = e.getLocation();
                ex = loc.getX();
                ey = loc.getY();
                ez = loc.getZ();
                d = loc.distanceSquared(player.getLocation());
                if (e.getType() == EntityType.HORSE) {

                    if ((bx - 1.2 <= ex && ex <= bx + 2.2)
                            && (bz - 1.2 <= ez && ez <= bz + 2.2)
                            && (by - 2.5 <= ey && ey <= by + 4.5)) {
                        if (d < md) {
                            md = d;
                            target = e;
                        }

                    }

                } else {
                    if ((bx - .80 <= ex && ex <= bx + 1.85)
                            && (bz - .80 <= ez && ez <= bz + 1.85)
                            && (by - 2.5 <= ey && ey <= by + 4.5)) {
                        if (d < md) {
                            md = d;
                            target = e;
                        }
                    }
                }
            }
        }
        livingE.clear();
        return target;

    }

    public static boolean instantBreak(Material m) {

        return m == Material.TORCH || m == Material.FLOWER_POT || m == Material.RED_ROSE || m == Material.YELLOW_FLOWER || m == Material.LONG_GRASS
                || m == Material.RED_MUSHROOM || m == Material.BROWN_MUSHROOM || m == Material.TRIPWIRE || m == Material.TRIPWIRE_HOOK ||
                m == Material.DEAD_BUSH || m == Material.DIODE_BLOCK_OFF || m == Material.DIODE_BLOCK_ON || m == Material.REDSTONE_COMPARATOR_OFF
                || m == Material.REDSTONE_COMPARATOR_OFF || m == Material.REDSTONE_WIRE || m == Material.REDSTONE_TORCH_OFF ||
                m == Material.REDSTONE_TORCH_ON || m == Material.DOUBLE_PLANT || m == Material.SUGAR_CANE_BLOCK;

    }

    @SuppressWarnings("deprecation")//Depercated for "magic value" :/
    public static boolean canSeeBlock(Player p, Block b) {

    	/*HashSet<Byte> igb = new HashSet<Byte>();

    	igb.add((byte) Material.TORCH.getId());
    	igb.add((byte) Material.AIR.getId());
    	igb.add((byte) Material.FLOWER_POT.getId());
    	igb.add((byte) Material.RED_ROSE.getId());
    	igb.add((byte) Material.YELLOW_FLOWER.getId());
    	igb.add((byte) Material.LONG_GRASS.getId());
    	igb.add((byte) Material.RED_MUSHROOM.getId());
    	igb.add((byte) Material.BROWN_MUSHROOM.getId());
    	igb.add((byte) Material.STONE_PLATE.getId());
    	igb.add((byte) Material.WOOD_PLATE.getId());
    	igb.add((byte) Material.WOOD_STEP.getId());
    	igb.add((byte) Material.STEP.getId());
    	igb.add((byte) Material.ANVIL.getId());
    	igb.add((byte) Material.VINE.getId());
    	igb.add((byte) Material.LADDER.getId());
    	igb.add((byte) Material.CAKE_BLOCK.getId());
    	igb.add((byte) Material.WATER.getId());
    	igb.add((byte) Material.LAVA.getId());
    	igb.add((byte) Material.CACTUS.getId());
    	igb.add((byte) Material.COCOA.getId());
    	igb.add((byte) Material.CARPET.getId());
    	igb.add((byte) Material.COBBLE_WALL.getId());
    	igb.add((byte) Material.NETHER_FENCE.getId());
    	igb.add((byte) Material.FENCE.getId());
    	igb.add((byte) Material.FENCE_GATE.getId());
    	igb.add((byte) Material.TRAP_DOOR.getId());
    	igb.add((byte) Material.TRIPWIRE_HOOK.getId());
    	igb.add((byte) Material.THIN_GLASS.getId());
    	igb.add((byte) Material.STAINED_GLASS_PANE.getId());
    	igb.add((byte) Material.STATIONARY_WATER.getId());
    	igb.add((byte) Material.STATIONARY_LAVA.getId());
    	igb.add((byte) Material.DAYLIGHT_DETECTOR.getId());
    	igb.add((byte) Material.WOODEN_DOOR.getId());
    	igb.add((byte) Material.IRON_DOOR_BLOCK.getId());
    	igb.add((byte) Material.SKULL.getId());
    	igb.add((byte) Material.DETECTOR_RAIL.getId());
    	igb.add((byte) Material.RAILS.getId());
    	igb.add((byte) Material.POWERED_RAIL.getId());
    	igb.add((byte) Material.SNOW.getId());
    	igb.add((byte) Material.SIGN_POST.getId());
    	igb.add((byte) Material.SIGN.getId());
    	igb.add((byte) Material.DEAD_BUSH.getId());
    	igb.add((byte) Material.DETECTOR_RAIL.getId());
    	igb.add((byte) Material.DIODE_BLOCK_ON.getId());
    	igb.add((byte) Material.DIODE_BLOCK_OFF.getId());
    	igb.add((byte) Material.REDSTONE_COMPARATOR_OFF.getId());
    	igb.add((byte) Material.REDSTONE_COMPARATOR_ON.getId());
    	igb.add((byte) Material.HOPPER.getId());
    	igb.add((byte) Material.REDSTONE_WIRE.getId());
    	igb.add((byte) Material.WOOD_BUTTON.getId());
    	igb.add((byte) Material.STONE_BUTTON.getId());
    	igb.add((byte) Material.LEVER.getId());
    	
    	Iterator<Byte> it = igb.iterator();
    	
    	while(it.hasNext()){
    		
    		Byte bt = it.next();
    		
    		if(bt.intValue() == b.getTypeId()){
    			
    			it.remove();
    			
    		}
    		
    	}
    	
    			/*new LocationIterator(p.getWorld(), p.getLocation().toVector(), 
    			new Vector(b.getX()-p.getLocation().getBlockX(), b.getY()-p.getLocation().getBlockY(),
    					b.getZ()-p.getLocation().getBlockZ()), 0, ((p.getGameMode() == GameMode.CREATIVE) ? 8 : 6));
    	//new BlockIterator(p.getEyeLocation(), ((p.getGameMode() == GameMode.CREATIVE) ? 8 : 6));
    	
    	Block s = p.getTargetBlock(igb, ((p.getGameMode() == GameMode.CREATIVE) ? 8 : 6));
    		
    	//Pretty sure it's the one :3
    	*/

        RayTrace rt = RayTrace.eyeTrace(p, ((p.getGameMode() == GameMode.CREATIVE) ? 8 : 6));

        Block s = rt.getBlock();

        if (s != null) {

            if (s.getX() == b.getX() && s.getY() == b.getY() && s.getZ() == b.getZ() && s.getType() == b.getType() && s.getData() == b.getData()) {

                return true;

            }

        }

        return false;

    }

    public static int getPing(Player p) {

        return ((CraftPlayer) p).getHandle().ping;

    }

    public static double getXZDistance(double x1, double x2, double z1, double z2) {

        double a1 = (x2 - x1), a2 = (z2 - z1);

        return ((a1 * (a1)) + (a2 * a2));

    }

    public static boolean isOnLadder(Player p) {

        return ((CraftPlayer) p).getHandle().h_();

    }

    public static boolean inWater(Player e) {

        boolean inWater = false;

        try {
            Field f = Class.forName("net.minecraft.server.v1_7_R4.Entity").getDeclaredField("inWater");
            f.setAccessible(true);

            inWater = f.getBoolean(((CraftPlayer) e).getHandle());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return inWater;

    }

    public static int getPotionEffectLevel(Player p, PotionEffectType pet) {

        for (PotionEffect pe : p.getActivePotionEffects()) {

            if (pe.getType().getName().equals(pet.getName())) {

                return pe.getAmplifier() + 1;

            }

        }

        return 0;

    }

    public static String getIP(Player p) {

        return ((CraftPlayer) p).getHandle().getName();

    }

    public static void messageStaff(String message) {

        for (Player p : Bukkit.getOnlinePlayers()) {

            if (p.hasPermission("nohack.notification") || p.isOp()) {

                p.sendMessage(message);

            }

        }

    }

}