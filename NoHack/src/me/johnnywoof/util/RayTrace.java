package me.johnnywoof.util;
/*
* RayTrace.java - Utility to do native minecraft server code based RayTraces in CraftBukkit environment
*
* Copyright (C) 2012 David Mentler
*
* Permission is hereby granted, free of charge, to any person obtaining a copy of this software
* and associated documentation files (the "Software"), to deal in the Software without restriction,
* including without limitation the rights to use, copy, modify, merge, publish, distribute,
* sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* - The above copyright notice and this permission notice shall be included in all copies
* or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
* BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*
*/

import net.minecraft.server.v1_7_R4.MovingObjectPosition;
import net.minecraft.server.v1_7_R4.Vec3D;
import net.minecraft.server.v1_7_R4.WorldServer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.block.CraftBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

public class RayTrace {
    public static final double RAY_LENGTH_LIMIT = 100;

    private static Vec3D toVec3D(Vector vec) {
        return Vec3D.a(vec.getX(), vec.getY(), vec.getZ());
    }

    private static WorldServer getHandle(World world) {
        if (world instanceof CraftWorld)
            return ((CraftWorld) world).getHandle();
        throw new IllegalArgumentException("Cannot raytrace in a non CraftBukkit world!");
    }

    public static RayTrace rayTrace(World world, Vector start, Vector direction) {
        return rayTrace(world, start, direction, RAY_LENGTH_LIMIT);
    }

    public static RayTrace rayTrace(World world, Vector start, Vector direction, double length) {
        Vector end = start.clone().add(direction.multiply(length));
        return new RayTrace(world, getHandle(world).rayTrace(toVec3D(start), toVec3D(end), false));
    }

    public static RayTrace eyeTrace(LivingEntity entity) {
        return eyeTrace(entity, RAY_LENGTH_LIMIT);
    }

    public static RayTrace eyeTrace(LivingEntity entity, double length) {
        Location loc = entity.getEyeLocation();

        World world = loc.getWorld();
        Vector end = loc.toVector().add(loc.getDirection().multiply(length));
        return new RayTrace(world, getHandle(world).rayTrace(toVec3D(loc.toVector()), toVec3D(end), false));
    }

    private boolean isHit = false;
    private World hitWorld;
    private BlockVector hitBlock;
    private BlockFace hitFace;
    private Vector hitPos;

    protected RayTrace(World inWorld, MovingObjectPosition traceRes) {
        this.isHit = traceRes != null;
        this.hitWorld = inWorld;
        if (isHit) {
            this.hitBlock = new BlockVector(traceRes.b, traceRes.c, traceRes.d);
            this.hitFace = CraftBlock.notchToBlockFace(traceRes.face);
            this.hitPos = new Vector(traceRes.pos.a, traceRes.pos.b, traceRes.pos.c);
        }
    }

    public boolean isHit() {
        return isHit;
    }

    public BlockVector getBlockVector() {
        return hitBlock;
    }

    public Block getBlock() {
        return isHit ? hitBlock.toLocation(hitWorld).getBlock() : null;
    }

    public BlockFace getFace() {
        return hitFace;
    }

    public Vector getHitPos() {
        return hitPos;
    }

    public String toString() {
        return "[RayTrace:" + (!isHit ? "MISS" : hitBlock + ";" + hitFace + ";" + hitPos) + "]";
    }
}