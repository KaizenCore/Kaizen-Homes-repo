package kaizen.core.kaizenHomes.storage;

import kaizen.core.kaizenHomes.KaizenHomes;
import kaizen.core.kaizenHomes.models.Home;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class YamlStorage implements StorageAdapter {

    private final KaizenHomes plugin;
    private final File dataFolder;
    private final Map<UUID, Map<String, Home>> homeCache;

    public YamlStorage(KaizenHomes plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getKaizenDataFolder(), "homes");
        this.homeCache = new ConcurrentHashMap<>();
    }

    @Override
    public CompletableFuture<Void> initialize() {
        return CompletableFuture.runAsync(() -> {
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
            loadAllHomes();
        });
    }

    @Override
    public CompletableFuture<Void> saveHome(Home home) {
        return CompletableFuture.runAsync(() -> {
            File playerFile = getPlayerFile(home.getOwner());
            YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

            String path = "homes." + home.getName();
            config.set(path + ".location.world", home.getLocation().getWorld().getName());
            config.set(path + ".location.x", home.getLocation().getX());
            config.set(path + ".location.y", home.getLocation().getY());
            config.set(path + ".location.z", home.getLocation().getZ());
            config.set(path + ".location.yaw", home.getLocation().getYaw());
            config.set(path + ".location.pitch", home.getLocation().getPitch());
            config.set(path + ".description", home.getDescription());
            config.set(path + ".icon", home.getIcon().name());
            config.set(path + ".privacy", home.getPrivacyMode().name());
            config.set(path + ".category", home.getCategory());
            config.set(path + ".created", home.getCreatedAt());
            config.set(path + ".lastUsed", home.getLastUsed());
            config.set(path + ".default", home.isDefault());

            // Save shared players
            List<String> sharedUUIDs = home.getSharedWith().stream()
                    .map(UUID::toString)
                    .collect(Collectors.toList());
            config.set(path + ".sharedWith", sharedUUIDs);

            try {
                config.save(playerFile);
                // Update cache
                homeCache.computeIfAbsent(home.getOwner(), k -> new ConcurrentHashMap<>())
                        .put(home.getName(), home);
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to save home: " + e.getMessage());
            }
        });
    }

    @Override
    public CompletableFuture<Void> deleteHome(UUID owner, String name) {
        return CompletableFuture.runAsync(() -> {
            File playerFile = getPlayerFile(owner);
            YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

            config.set("homes." + name, null);

            try {
                config.save(playerFile);
                // Update cache
                Map<String, Home> playerHomes = homeCache.get(owner);
                if (playerHomes != null) {
                    playerHomes.remove(name);
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to delete home: " + e.getMessage());
            }
        });
    }

    @Override
    public CompletableFuture<Home> getHome(UUID owner, String name) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Home> playerHomes = homeCache.get(owner);
            if (playerHomes != null) {
                return playerHomes.get(name);
            }
            return null;
        });
    }

    @Override
    public CompletableFuture<List<Home>> getHomes(UUID owner) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Home> playerHomes = homeCache.get(owner);
            if (playerHomes != null) {
                return new ArrayList<>(playerHomes.values());
            }
            return new ArrayList<>();
        });
    }

    @Override
    public CompletableFuture<List<Home>> getPublicHomes() {
        return CompletableFuture.supplyAsync(() -> {
            List<Home> publicHomes = new ArrayList<>();
            for (Map<String, Home> playerHomes : homeCache.values()) {
                for (Home home : playerHomes.values()) {
                    if (home.getPrivacyMode() == Home.PrivacyMode.PUBLIC) {
                        publicHomes.add(home);
                    }
                }
            }
            return publicHomes;
        });
    }

    @Override
    public CompletableFuture<List<Home>> getSharedHomes(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> {
            List<Home> sharedHomes = new ArrayList<>();
            for (Map<String, Home> playerHomes : homeCache.values()) {
                for (Home home : playerHomes.values()) {
                    if (home.hasAccess(playerId) && !home.getOwner().equals(playerId)) {
                        sharedHomes.add(home);
                    }
                }
            }
            return sharedHomes;
        });
    }

    @Override
    public CompletableFuture<Boolean> homeExists(UUID owner, String name) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Home> playerHomes = homeCache.get(owner);
            return playerHomes != null && playerHomes.containsKey(name);
        });
    }

    @Override
    public CompletableFuture<Integer> getHomeCount(UUID owner) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Home> playerHomes = homeCache.get(owner);
            return playerHomes != null ? playerHomes.size() : 0;
        });
    }

    @Override
    public void close() {
        homeCache.clear();
    }

    @Override
    public CompletableFuture<Void> reload() {
        return CompletableFuture.runAsync(() -> {
            homeCache.clear();
            loadAllHomes();
        });
    }

    private void loadAllHomes() {
        if (!dataFolder.exists()) return;

        File[] playerFiles = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (playerFiles == null) return;

        for (File file : playerFiles) {
            String fileName = file.getName().replace(".yml", "");
            try {
                UUID playerId = UUID.fromString(fileName);
                loadPlayerHomes(playerId);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid player file: " + fileName);
            }
        }
    }

    private void loadPlayerHomes(UUID playerId) {
        File playerFile = getPlayerFile(playerId);
        if (!playerFile.exists()) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        ConfigurationSection homesSection = config.getConfigurationSection("homes");
        if (homesSection == null) return;

        Map<String, Home> playerHomes = new ConcurrentHashMap<>();

        for (String homeName : homesSection.getKeys(false)) {
            try {
                String path = "homes." + homeName;

                String worldName = config.getString(path + ".location.world");
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    plugin.getLogger().warning("World not found for home: " + homeName);
                    continue;
                }

                double x = config.getDouble(path + ".location.x");
                double y = config.getDouble(path + ".location.y");
                double z = config.getDouble(path + ".location.z");
                float yaw = (float) config.getDouble(path + ".location.yaw");
                float pitch = (float) config.getDouble(path + ".location.pitch");

                Location location = new Location(world, x, y, z, yaw, pitch);
                Home home = new Home(playerId, homeName, location);

                home.setDescription(config.getString(path + ".description", ""));
                home.setIcon(Material.valueOf(config.getString(path + ".icon", "RED_BED")));
                home.setPrivacyMode(Home.PrivacyMode.valueOf(config.getString(path + ".privacy", "PRIVATE")));
                home.setCategory(config.getString(path + ".category", "default"));
                home.setCreatedAt(config.getLong(path + ".created"));
                home.setLastUsed(config.getLong(path + ".lastUsed"));
                home.setDefault(config.getBoolean(path + ".default", false));

                // Load shared players
                List<String> sharedUUIDs = config.getStringList(path + ".sharedWith");
                Set<UUID> sharedWith = sharedUUIDs.stream()
                        .map(UUID::fromString)
                        .collect(Collectors.toSet());
                home.setSharedWith(sharedWith);

                playerHomes.put(homeName, home);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load home " + homeName + ": " + e.getMessage());
            }
        }

        homeCache.put(playerId, playerHomes);
    }

    private File getPlayerFile(UUID playerId) {
        return new File(dataFolder, playerId.toString() + ".yml");
    }
}
