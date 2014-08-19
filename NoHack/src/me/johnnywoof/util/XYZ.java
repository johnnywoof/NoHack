package me.johnnywoof.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class XYZ {

    public double x, y, z;

    public String world;

    public XYZ(String world, double x, double y, double z) {

        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;

    }

    public boolean equalsLoc(XYZ loc) {

        return this.x == loc.x && this.y == loc.y && this.z == loc.z && this.world.equals(loc.world);

    }

    public double getDistanceSqrd(XYZ loc) {

        double a1 = (this.x - loc.x), a2 = (this.z - loc.z);

        return ((a1 * (a1)) + (a2 * a2));

    }

    public XYZ(Location loc) {

        this.world = loc.getWorld().getName();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();

    }

    @Override
    public String toString() {

        return "[" + this.world + ", " + Math.floor(this.x) + ", " + Math.floor(this.y) + ", " + Math.floor(this.z) + "]";

    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    public Location toLocation(float pitch, float yaw) {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

}
