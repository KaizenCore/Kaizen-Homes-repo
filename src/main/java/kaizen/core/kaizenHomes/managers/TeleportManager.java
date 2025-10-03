package kaizen.core.kaizenHomes.managers;

import kaizen.core.kaizenHomes.KaizenHomes;
import kaizen.core.kaizenHomes.config.ConfigManager;
import kaizen.core.kaizenHomes.models.Home;
import kaizen.core.kaizenHomes.models.TeleportRequest;
import kaizen.core.kaizenHomes.utils.MessageUtil;
import kaizen.core.kaizenHomes.utils.PermissionUtil;
import kaizen.core.kaizenHomes.utils.SafetyUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound.Source;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportManager {

    private final KaizenHomes plugin;
    private final ConfigManager configManager;
    private final HomeManager homeManager;
    private final Map<UUID, Long> cooldowns;
    private final Map<UUID, TeleportRequest> pendingTeleports;

    public TeleportManager(KaizenHomes plugin, ConfigManager configManager, HomeManager homeManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.homeManager = homeManager;
        this.cooldowns = new ConcurrentHashMap<>();
        this.pendingTeleports = new ConcurrentHashMap<>();

        // Start the warmup checker task
        startWarmupChecker();
    }

    /**
     * Teleport a player to a home
     */
    public void teleportToHome(Player player, Home home) {
        // Check cooldown
        if (hasCooldown(player)) {
            int remaining = getRemainingCooldown(player);
            MessageUtil.sendCooldownActive(player, remaining);
            return;
        }

        // Check safety
        Location teleportLocation = home.getLocation();
        if (configManager.isSafetyCheckEnabled()) {
            if (!SafetyUtil.isSafeLocation(teleportLocation)) {
                if (configManager.isSafetyCheckAutoFix()) {
                    Location safeLoc = SafetyUtil.findSafeLocation(teleportLocation);
                    if (safeLoc != null) {
                        teleportLocation = safeLoc;
                    } else {
                        MessageUtil.sendUnsafeLocation(player);
                        return;
                    }
                } else {
                    MessageUtil.sendUnsafeLocation(player);
                    return;
                }
            }
        }

        // Center the location
        teleportLocation = SafetyUtil.centerLocation(teleportLocation);

        // Check if warmup is required
        int warmupSeconds = configManager.getTeleportWarmup();
        if (warmupSeconds > 0 && !PermissionUtil.canBypassWarmup(player)) {
            startWarmup(player, teleportLocation, home.getName(), warmupSeconds);
        } else {
            executeTeleport(player, teleportLocation, home);
        }
    }

    /**
     * Start warmup timer
     */
    private void startWarmup(Player player, Location location, String homeName, int warmupSeconds) {
        TeleportRequest request = new TeleportRequest(player, location, homeName, warmupSeconds);
        pendingTeleports.put(player.getUniqueId(), request);
        MessageUtil.sendWarmupStarted(player, warmupSeconds);
    }

    /**
     * Execute the actual teleportation
     */
    private void executeTeleport(Player player, Location location, Home home) {
        // Teleport the player
        player.teleportAsync(location).thenAccept(success -> {
            if (success) {
                // Play effects
                playTeleportEffects(player, location, home.getName());

                // Send message
                MessageUtil.sendHomeTeleport(player, home.getName());

                // Update last used
                homeManager.updateLastUsed(home);

                // Set cooldown
                if (!PermissionUtil.canBypassCooldown(player)) {
                    setCooldown(player);
                }
            }
        });
    }

    /**
     * Play teleport effects (particles, sounds, titles)
     */
    private void playTeleportEffects(Player player, Location location, String homeName) {
        // Particles
        if (configManager.isParticlesEnabled()) {
            try {
                Particle particle = Particle.valueOf(configManager.getParticleType());
                int amount = configManager.getParticleAmount();
                location.getWorld().spawnParticle(particle, location, amount, 0.5, 1, 0.5);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid particle type in config: " + configManager.getParticleType());
            }
        }

        // Sounds
        if (configManager.isSoundsEnabled()) {
            try {
                String soundName = configManager.getTeleportSound().toLowerCase();
                Key soundKey = Key.key("minecraft", soundName.replace("entity_", "entity.").replace("_", ".").toLowerCase());
                net.kyori.adventure.sound.Sound sound = net.kyori.adventure.sound.Sound.sound(soundKey, Source.PLAYER, 1.0f, 1.0f);
                player.playSound(sound, location.getX(), location.getY(), location.getZ());
            } catch (Exception e) {
                plugin.getLogger().warning("Invalid sound type in config: " + configManager.getTeleportSound());
            }
        }

        // Titles
        if (configManager.isTitlesEnabled()) {
            String title = configManager.getTeleportTitle();
            String subtitle = configManager.getTeleportSubtitle();

            // Replace {home} placeholder with actual home name
            title = title.replace("{home}", homeName);
            subtitle = subtitle.replace("{home}", homeName);

            MessageUtil.sendTitle(player, title, subtitle);
        }
    }

    /**
     * Check if a player has an active cooldown
     */
    public boolean hasCooldown(Player player) {
        if (PermissionUtil.canBypassCooldown(player)) {
            return false;
        }

        UUID playerId = player.getUniqueId();
        if (!cooldowns.containsKey(playerId)) {
            return false;
        }

        long cooldownEnd = cooldowns.get(playerId);
        if (System.currentTimeMillis() >= cooldownEnd) {
            cooldowns.remove(playerId);
            return false;
        }

        return true;
    }

    /**
     * Get remaining cooldown time in seconds
     */
    public int getRemainingCooldown(Player player) {
        if (!cooldowns.containsKey(player.getUniqueId())) {
            return 0;
        }

        long cooldownEnd = cooldowns.get(player.getUniqueId());
        long remaining = cooldownEnd - System.currentTimeMillis();
        return Math.max(0, (int) (remaining / 1000));
    }

    /**
     * Set cooldown for a player
     */
    private void setCooldown(Player player) {
        int cooldownSeconds = configManager.getTeleportCooldown();
        if (cooldownSeconds > 0) {
            long cooldownEnd = System.currentTimeMillis() + (cooldownSeconds * 1000L);
            cooldowns.put(player.getUniqueId(), cooldownEnd);
        }
    }

    /**
     * Cancel pending teleport
     */
    public void cancelTeleport(UUID playerId) {
        TeleportRequest request = pendingTeleports.remove(playerId);
        if (request != null) {
            request.cancel();
        }
    }

    /**
     * Start the warmup checker task
     */
    private void startWarmupChecker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID playerId : pendingTeleports.keySet()) {
                    Player player = Bukkit.getPlayer(playerId);
                    if (player == null || !player.isOnline()) {
                        pendingTeleports.remove(playerId);
                        continue;
                    }

                    TeleportRequest request = pendingTeleports.get(playerId);
                    if (request == null || request.isCancelled()) {
                        pendingTeleports.remove(playerId);
                        continue;
                    }

                    // Check if player moved
                    if (configManager.isCancelOnMove() && request.hasPlayerMoved(player.getLocation())) {
                        MessageUtil.sendWarmupCancelled(player);
                        pendingTeleports.remove(playerId);
                        continue;
                    }

                    // Check if warmup is complete
                    if (request.isExpired()) {
                        pendingTeleports.remove(playerId);

                        // Get the home and execute teleport
                        Home home = homeManager.getHome(playerId, request.getHomeName()).join();
                        if (home != null) {
                            executeTeleport(player, request.getToLocation(), home);
                        }
                        continue;
                    }

                    // Send countdown
                    int remaining = request.getRemainingSeconds();
                    if (remaining > 0 && remaining <= 3) {
                        MessageUtil.sendActionBar(player, "Teleporting in " + remaining + "...");
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // Run every second
    }

    /**
     * Clear cooldowns and pending teleports (for plugin reload/disable)
     */
    public void shutdown() {
        cooldowns.clear();
        pendingTeleports.clear();
    }
}
