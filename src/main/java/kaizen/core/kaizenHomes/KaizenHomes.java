package kaizen.core.kaizenHomes;

import kaizen.core.kaizenHomes.commands.GradientTestCommand;
import kaizen.core.kaizenHomes.commands.HomeCommand;
import kaizen.core.kaizenHomes.commands.HomesCommand;
import kaizen.core.kaizenHomes.commands.KhomesCommand;
import kaizen.core.kaizenHomes.commands.PublicHomesCommand;
import kaizen.core.kaizenHomes.config.ConfigManager;
import kaizen.core.kaizenHomes.listeners.BedSyncListener;
import kaizen.core.kaizenHomes.listeners.DeathHomeListener;
import kaizen.core.kaizenHomes.managers.HomeManager;
import kaizen.core.kaizenHomes.managers.TeleportManager;
import kaizen.core.kaizenHomes.storage.StorageAdapter;
import kaizen.core.kaizenHomes.storage.YamlStorage;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class KaizenHomes extends JavaPlugin {

    private ConfigManager configManager;
    private StorageAdapter storage;
    private HomeManager homeManager;
    private TeleportManager teleportManager;
    private File customDataFolder;

    @Override
    public void onEnable() {
        // Setup custom data folder: plugins/kaizen/kaizenhomes/
        customDataFolder = new File(getServer().getPluginsFolder(), "kaizen/kaizenhomes");
        if (!customDataFolder.exists()) {
            customDataFolder.mkdirs();
        }

        // Startup banner
        getLogger().info("╔════════════════════════════════════════╗");
        getLogger().info("║     KaizenHomes - Modern Home Plugin   ║");
        getLogger().info("║        With Gradient Text Support      ║");
        getLogger().info("╚════════════════════════════════════════╝");
        getLogger().info("Data folder: " + customDataFolder.getAbsolutePath());

        // Initialize config
        configManager = new ConfigManager(this);

        // Initialize storage
        storage = new YamlStorage(this);
        storage.initialize().join();

        // Initialize managers
        homeManager = new HomeManager(this, storage, configManager);
        teleportManager = new TeleportManager(this, configManager, homeManager);

        // Register commands
        registerCommands();

        // Register listeners
        registerListeners();

        getLogger().info("✓ KaizenHomes enabled successfully!");
        getLogger().info("✓ Gradient text support active");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling KaizenHomes...");

        // Shutdown managers
        if (teleportManager != null) {
            teleportManager.shutdown();
        }

        // Close storage
        if (storage != null) {
            storage.close();
        }

        getLogger().info("KaizenHomes disabled successfully!");
    }

    private void registerCommands() {
        HomeCommand homeCommand = new HomeCommand(this, homeManager, teleportManager);
        HomesCommand homesCommand = new HomesCommand(this, homeManager);
        PublicHomesCommand publicHomesCommand = new PublicHomesCommand(this, homeManager);
        GradientTestCommand gradientTestCommand = new GradientTestCommand();
        KhomesCommand khomesCommand = new KhomesCommand(this);

        getCommand("home").setExecutor(homeCommand);
        getCommand("home").setTabCompleter(homeCommand);
        getCommand("homes").setExecutor(homesCommand);
        getCommand("publichomes").setExecutor(publicHomesCommand);
        getCommand("gradienttest").setExecutor(gradientTestCommand);
        getCommand("khomes").setExecutor(khomesCommand);
        getCommand("khomes").setTabCompleter(khomesCommand);
    }

    private void registerListeners() {
        if (configManager.isDeathHomeEnabled()) {
            getServer().getPluginManager().registerEvents(new DeathHomeListener(this, homeManager), this);
        }

        if (configManager.isBedSyncEnabled()) {
            getServer().getPluginManager().registerEvents(new BedSyncListener(this, homeManager), this);
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public StorageAdapter getStorage() {
        return storage;
    }

    /**
     * Get the custom Kaizen data folder
     * @return plugins/kaizen/kaizenhomes/
     */
    public File getKaizenDataFolder() {
        return customDataFolder;
    }
}
