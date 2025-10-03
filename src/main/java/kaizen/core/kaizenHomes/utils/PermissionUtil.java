package kaizen.core.kaizenHomes.utils;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class PermissionUtil {

    // Permission nodes
    public static final String PERMISSION_SET = "kaizenhomes.set";
    public static final String PERMISSION_DELETE = "kaizenhomes.delete";
    public static final String PERMISSION_TELEPORT = "kaizenhomes.teleport";
    public static final String PERMISSION_LIST = "kaizenhomes.list";
    public static final String PERMISSION_SHARE = "kaizenhomes.share";
    public static final String PERMISSION_PUBLIC = "kaizenhomes.public";
    public static final String PERMISSION_ADMIN = "kaizenhomes.admin";
    public static final String PERMISSION_BYPASS_COOLDOWN = "kaizenhomes.bypass.cooldown";
    public static final String PERMISSION_BYPASS_WARMUP = "kaizenhomes.bypass.warmup";
    public static final String PERMISSION_BYPASS_LIMIT = "kaizenhomes.bypass.limit";
    public static final String PERMISSION_LIMIT_PREFIX = "kaizenhomes.limit.";

    /**
     * Check if a player has a specific permission
     */
    public static boolean hasPermission(Player player, String permission) {
        return player.hasPermission(permission) || player.hasPermission(PERMISSION_ADMIN);
    }

    /**
     * Get the maximum number of homes a player can have based on permissions
     */
    public static int getHomeLimit(Player player, int defaultLimit) {
        if (hasPermission(player, PERMISSION_BYPASS_LIMIT)) {
            return Integer.MAX_VALUE;
        }

        int maxLimit = defaultLimit;

        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            String permission = info.getPermission();
            if (permission.startsWith(PERMISSION_LIMIT_PREFIX)) {
                try {
                    String limitStr = permission.substring(PERMISSION_LIMIT_PREFIX.length());
                    int limit = Integer.parseInt(limitStr);
                    if (limit > maxLimit) {
                        maxLimit = limit;
                    }
                } catch (NumberFormatException ignored) {
                    // Invalid limit format, skip
                }
            }
        }

        return maxLimit;
    }

    /**
     * Check if a player can bypass cooldowns
     */
    public static boolean canBypassCooldown(Player player) {
        return hasPermission(player, PERMISSION_BYPASS_COOLDOWN);
    }

    /**
     * Check if a player can bypass warmup timers
     */
    public static boolean canBypassWarmup(Player player) {
        return hasPermission(player, PERMISSION_BYPASS_WARMUP);
    }

    /**
     * Check if a player is an admin
     */
    public static boolean isAdmin(Player player) {
        return player.hasPermission(PERMISSION_ADMIN);
    }
}
