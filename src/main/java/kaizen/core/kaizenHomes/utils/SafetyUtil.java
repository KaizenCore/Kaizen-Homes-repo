package kaizen.core.kaizenHomes.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class SafetyUtil {

    /**
     * Check if a location is safe for teleportation
     */
    public static boolean isSafeLocation(Location location) {
        if (location == null || location.getWorld() == null) {
            return false;
        }

        Block block = location.getBlock();
        Block above = block.getRelative(BlockFace.UP);
        Block below = block.getRelative(BlockFace.DOWN);

        // Check if the blocks at foot and head level are passable
        if (!block.isPassable() || !above.isPassable()) {
            return false;
        }

        // Check if there's a solid block below (not falling into void)
        if (below.getType().isAir() && below.getRelative(BlockFace.DOWN).getType().isAir()) {
            return false;
        }

        // Check for dangerous blocks
        if (isDangerous(block) || isDangerous(above) || isDangerous(below)) {
            return false;
        }

        return true;
    }

    /**
     * Find a safe location near the given location
     */
    public static Location findSafeLocation(Location location) {
        if (isSafeLocation(location)) {
            return location;
        }

        // Try to find a safe location nearby (within 5 blocks radius)
        for (int x = -5; x <= 5; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -5; z <= 5; z++) {
                    Location test = location.clone().add(x, y, z);
                    if (isSafeLocation(test)) {
                        return test;
                    }
                }
            }
        }

        // If no safe location found, return the original (caller should handle)
        return null;
    }

    /**
     * Check if a block is dangerous
     */
    private static boolean isDangerous(Block block) {
        Material type = block.getType();

        return type == Material.LAVA ||
                type == Material.FIRE ||
                type == Material.SOUL_FIRE ||
                type == Material.MAGMA_BLOCK ||
                type == Material.CACTUS ||
                type == Material.SWEET_BERRY_BUSH ||
                type == Material.WITHER_ROSE ||
                type == Material.POWDER_SNOW ||
                type.name().contains("CAMPFIRE");
    }

    /**
     * Check if a location is in the void
     */
    public static boolean isVoid(Location location) {
        return location.getY() < location.getWorld().getMinHeight();
    }

    /**
     * Adjust location to center of block for cleaner teleportation
     */
    public static Location centerLocation(Location location) {
        Location centered = location.clone();
        centered.setX(location.getBlockX() + 0.5);
        centered.setY(location.getBlockY());
        centered.setZ(location.getBlockZ() + 0.5);
        return centered;
    }
}
