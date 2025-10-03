package kaizen.core.kaizenHomes.config;

import kaizen.core.kaizenHomes.KaizenHomes;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ConfigManager {

    private final KaizenHomes plugin;
    private FileConfiguration config;
    private File configFile;

    public ConfigManager(KaizenHomes plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        configFile = new File(plugin.getKaizenDataFolder(), "config.yml");

        // Create config file from default if it doesn't exist
        if (!configFile.exists()) {
            try {
                configFile.getParentFile().mkdirs();
                InputStream defaultConfig = plugin.getResource("config.yml");
                if (defaultConfig != null) {
                    Files.copy(defaultConfig, configFile.toPath());
                    defaultConfig.close();
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create default config: " + e.getMessage());
            }
        }

        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void reloadConfig() {
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save config: " + e.getMessage());
        }
    }

    // General Settings
    public int getDefaultHomeLimit() {
        return config.getInt("settings.default-home-limit", 3);
    }

    public boolean isDeathHomeEnabled() {
        return config.getBoolean("settings.death-home.enabled", true);
    }

    public boolean isDeathHomeOverwrite() {
        return config.getBoolean("settings.death-home.overwrite-existing", false);
    }

    public boolean isBedSyncEnabled() {
        return config.getBoolean("settings.bed-sync.enabled", false);
    }

    public String getBedSyncHomeName() {
        return config.getString("settings.bed-sync.home-name", "bed");
    }

    // Teleportation Settings
    public boolean isSafetyCheckEnabled() {
        return config.getBoolean("teleport.safety-check.enabled", true);
    }

    public boolean isSafetyCheckAutoFix() {
        return config.getBoolean("teleport.safety-check.auto-find-safe-location", true);
    }

    public int getTeleportWarmup() {
        return config.getInt("teleport.warmup-seconds", 3);
    }

    public int getTeleportCooldown() {
        return config.getInt("teleport.cooldown-seconds", 5);
    }

    public boolean isCancelOnMove() {
        return config.getBoolean("teleport.cancel-on-move", true);
    }

    // Economy Settings
    public boolean isEconomyEnabled() {
        return config.getBoolean("economy.enabled", false);
    }

    public double getSetHomeCost() {
        return config.getDouble("economy.cost.set-home", 0.0);
    }

    public double getTeleportCost() {
        return config.getDouble("economy.cost.teleport", 0.0);
    }

    public double getDeleteHomeRefund() {
        return config.getDouble("economy.refund.delete-home", 0.0);
    }

    // Effects Settings
    public boolean isParticlesEnabled() {
        return config.getBoolean("effects.particles.enabled", true);
    }

    public String getParticleType() {
        return config.getString("effects.particles.type", "PORTAL");
    }

    public int getParticleAmount() {
        return config.getInt("effects.particles.amount", 50);
    }

    public boolean isSoundsEnabled() {
        return config.getBoolean("effects.sounds.enabled", true);
    }

    public String getTeleportSound() {
        return config.getString("effects.sounds.teleport", "ENTITY_ENDERMAN_TELEPORT");
    }

    public boolean isTitlesEnabled() {
        return config.getBoolean("effects.titles.enabled", true);
    }

    public String getTeleportTitle() {
        return config.getString("effects.titles.teleport.title", "Welcome Home");
    }

    public String getTeleportSubtitle() {
        return config.getString("effects.titles.teleport.subtitle", "{home}");
    }

    // GUI Settings
    public String getGUITitle() {
        return config.getString("gui.title", "Your Homes");
    }

    public int getGUISize() {
        return config.getInt("gui.size", 54);
    }

    public boolean isGUIFillEmpty() {
        return config.getBoolean("gui.fill-empty-slots", true);
    }

    public String getGUIFillMaterial() {
        return config.getString("gui.fill-material", "GRAY_STAINED_GLASS_PANE");
    }

    // Sharing Settings
    public boolean isSharingEnabled() {
        return config.getBoolean("sharing.enabled", true);
    }

    public int getMaxSharedPlayers() {
        return config.getInt("sharing.max-shared-players", 10);
    }

    public boolean isPublicHomesEnabled() {
        return config.getBoolean("sharing.public-homes.enabled", true);
    }

    public int getMaxPublicHomes() {
        return config.getInt("sharing.public-homes.max-per-player", 5);
    }

    // Categories Settings
    public boolean isCategoriesEnabled() {
        return config.getBoolean("categories.enabled", true);
    }

    public String getDefaultCategory() {
        return config.getString("categories.default", "Home");
    }

    // Per-World Settings
    public boolean isPerWorldLimitsEnabled() {
        return config.getBoolean("per-world.enabled", false);
    }

    public int getWorldLimit(String worldName) {
        return config.getInt("per-world.limits." + worldName, getDefaultHomeLimit());
    }

    // Messages
    public String getMessage(String key) {
        return config.getString("messages." + key, "Message not found: " + key);
    }

    // === SETTER METHODS FOR GUI EDITING ===

    // General Settings Setters
    public void setDefaultHomeLimit(int limit) {
        config.set("settings.default-home-limit", limit);
        saveConfig();
    }

    public void setDeathHomeEnabled(boolean enabled) {
        config.set("settings.death-home.enabled", enabled);
        saveConfig();
    }

    public void setBedSyncEnabled(boolean enabled) {
        config.set("settings.bed-sync.enabled", enabled);
        saveConfig();
    }

    // Teleportation Settings Setters
    public void setSafetyCheckEnabled(boolean enabled) {
        config.set("teleport.safety-check.enabled", enabled);
        saveConfig();
    }

    public void setTeleportWarmup(int seconds) {
        config.set("teleport.warmup-seconds", seconds);
        saveConfig();
    }

    public void setTeleportCooldown(int seconds) {
        config.set("teleport.cooldown-seconds", seconds);
        saveConfig();
    }

    public void setCancelOnMove(boolean enabled) {
        config.set("teleport.cancel-on-move", enabled);
        saveConfig();
    }

    // Sharing Settings Setters
    public void setSharingEnabled(boolean enabled) {
        config.set("sharing.enabled", enabled);
        saveConfig();
    }

    public void setMaxSharedPlayers(int max) {
        config.set("sharing.max-shared-players", max);
        saveConfig();
    }

    public void setPublicHomesEnabled(boolean enabled) {
        config.set("sharing.public-homes.enabled", enabled);
        saveConfig();
    }

    // Effects Settings Setters
    public void setParticlesEnabled(boolean enabled) {
        config.set("effects.particles.enabled", enabled);
        saveConfig();
    }

    public void setSoundsEnabled(boolean enabled) {
        config.set("effects.sounds.enabled", enabled);
        saveConfig();
    }

    public void setTitlesEnabled(boolean enabled) {
        config.set("effects.titles.enabled", enabled);
        saveConfig();
    }

    // Economy Settings Setters
    public void setEconomyEnabled(boolean enabled) {
        config.set("economy.enabled", enabled);
        saveConfig();
    }

    public void setSetHomeCost(double cost) {
        config.set("economy.cost.set-home", cost);
        saveConfig();
    }

    public void setTeleportCost(double cost) {
        config.set("economy.cost.teleport", cost);
        saveConfig();
    }
}
