package kaizen.core.kaizenHomes.managers;

import kaizen.core.kaizenHomes.KaizenHomes;
import kaizen.core.kaizenHomes.config.ConfigManager;
import kaizen.core.kaizenHomes.models.Home;
import kaizen.core.kaizenHomes.storage.StorageAdapter;
import kaizen.core.kaizenHomes.utils.PermissionUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class HomeManager {

    private final KaizenHomes plugin;
    private final StorageAdapter storage;
    private final ConfigManager configManager;

    public HomeManager(KaizenHomes plugin, StorageAdapter storage, ConfigManager configManager) {
        this.plugin = plugin;
        this.storage = storage;
        this.configManager = configManager;
    }

    /**
     * Create a new home for a player
     */
    public CompletableFuture<Boolean> createHome(Player player, String name, Location location) {
        return CompletableFuture.supplyAsync(() -> {
            UUID playerId = player.getUniqueId();

            // Check if home already exists
            if (storage.homeExists(playerId, name).join()) {
                return false;
            }

            // Check home limit
            int currentHomes = storage.getHomeCount(playerId).join();
            int limit = PermissionUtil.getHomeLimit(player, configManager.getDefaultHomeLimit());

            if (currentHomes >= limit && !PermissionUtil.hasPermission(player, PermissionUtil.PERMISSION_BYPASS_LIMIT)) {
                return false;
            }

            // Create and save the home
            Home home = new Home(playerId, name, location);

            // If this is the first home, make it default
            if (currentHomes == 0) {
                home.setDefault(true);
            }

            storage.saveHome(home).join();
            return true;
        });
    }

    /**
     * Delete a home
     */
    public CompletableFuture<Boolean> deleteHome(UUID playerId, String name) {
        return CompletableFuture.supplyAsync(() -> {
            if (!storage.homeExists(playerId, name).join()) {
                return false;
            }

            storage.deleteHome(playerId, name).join();

            // If deleted home was default, set a new default
            List<Home> homes = storage.getHomes(playerId).join();
            if (!homes.isEmpty()) {
                boolean hasDefault = homes.stream().anyMatch(Home::isDefault);
                if (!hasDefault) {
                    homes.get(0).setDefault(true);
                    storage.saveHome(homes.get(0)).join();
                }
            }

            return true;
        });
    }

    /**
     * Get a specific home
     */
    public CompletableFuture<Home> getHome(UUID playerId, String name) {
        return storage.getHome(playerId, name);
    }

    /**
     * Get all homes for a player
     */
    public CompletableFuture<List<Home>> getHomes(UUID playerId) {
        return storage.getHomes(playerId);
    }

    /**
     * Get all homes for a player synchronously from cache (for tab completion)
     * This method does NOT block and returns immediately from in-memory cache
     */
    public List<Home> getHomesCached(UUID playerId) {
        return storage.getHomesCached(playerId);
    }

    /**
     * Get the default home for a player
     */
    public CompletableFuture<Home> getDefaultHome(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> {
            List<Home> homes = storage.getHomes(playerId).join();
            return homes.stream()
                    .filter(Home::isDefault)
                    .findFirst()
                    .orElse(homes.isEmpty() ? null : homes.get(0));
        });
    }

    /**
     * Set a home as default
     */
    public CompletableFuture<Void> setDefaultHome(UUID playerId, String name) {
        return CompletableFuture.runAsync(() -> {
            List<Home> homes = storage.getHomes(playerId).join();

            for (Home home : homes) {
                if (home.getName().equals(name)) {
                    home.setDefault(true);
                } else if (home.isDefault()) {
                    home.setDefault(false);
                }
                storage.saveHome(home).join();
            }
        });
    }

    /**
     * Rename a home
     */
    public CompletableFuture<Boolean> renameHome(UUID playerId, String oldName, String newName) {
        return CompletableFuture.supplyAsync(() -> {
            if (!storage.homeExists(playerId, oldName).join()) {
                return false;
            }

            if (storage.homeExists(playerId, newName).join()) {
                return false;
            }

            Home home = storage.getHome(playerId, oldName).join();
            if (home == null) return false;

            storage.deleteHome(playerId, oldName).join();
            home.setName(newName);
            storage.saveHome(home).join();

            return true;
        });
    }

    /**
     * Update home location
     */
    public CompletableFuture<Boolean> updateHomeLocation(UUID playerId, String name, Location location) {
        return CompletableFuture.supplyAsync(() -> {
            Home home = storage.getHome(playerId, name).join();
            if (home == null) return false;

            home.setLocation(location);
            storage.saveHome(home).join();
            return true;
        });
    }

    /**
     * Update home description
     */
    public CompletableFuture<Void> updateHomeDescription(Home home, String description) {
        return CompletableFuture.runAsync(() -> {
            home.setDescription(description);
            storage.saveHome(home).join();
        });
    }

    /**
     * Update home category
     */
    public CompletableFuture<Void> updateHomeCategory(Home home, String category) {
        return CompletableFuture.runAsync(() -> {
            home.setCategory(category);
            storage.saveHome(home).join();
        });
    }

    /**
     * Update home privacy mode
     */
    public CompletableFuture<Void> updateHomePrivacy(Home home, Home.PrivacyMode privacyMode) {
        return CompletableFuture.runAsync(() -> {
            home.setPrivacyMode(privacyMode);
            storage.saveHome(home).join();
        });
    }

    /**
     * Update home icon
     */
    public CompletableFuture<Boolean> updateHomeIcon(Home home, org.bukkit.Material icon) {
        return CompletableFuture.supplyAsync(() -> {
            home.setIcon(icon);
            storage.saveHome(home).join();
            return true;
        });
    }

    /**
     * Share a home with another player
     */
    public CompletableFuture<Boolean> shareHome(Home home, UUID targetPlayerId) {
        return CompletableFuture.supplyAsync(() -> {
            if (home.getSharedWith().contains(targetPlayerId)) {
                return false;
            }

            if (home.getSharedWith().size() >= configManager.getMaxSharedPlayers()) {
                return false;
            }

            home.addSharedPlayer(targetPlayerId);
            if (home.getPrivacyMode() == Home.PrivacyMode.PRIVATE) {
                home.setPrivacyMode(Home.PrivacyMode.SHARED);
            }
            storage.saveHome(home).join();
            return true;
        });
    }

    /**
     * Unshare a home from a player
     */
    public CompletableFuture<Boolean> unshareHome(Home home, UUID targetPlayerId) {
        return CompletableFuture.supplyAsync(() -> {
            if (!home.getSharedWith().contains(targetPlayerId)) {
                return false;
            }

            home.removeSharedPlayer(targetPlayerId);
            if (home.getSharedWith().isEmpty() && home.getPrivacyMode() == Home.PrivacyMode.SHARED) {
                home.setPrivacyMode(Home.PrivacyMode.PRIVATE);
            }
            storage.saveHome(home).join();
            return true;
        });
    }

    /**
     * Get all public homes
     */
    public CompletableFuture<List<Home>> getPublicHomes() {
        return storage.getPublicHomes();
    }

    /**
     * Get homes shared with a player
     */
    public CompletableFuture<List<Home>> getSharedHomes(UUID playerId) {
        return storage.getSharedHomes(playerId);
    }

    /**
     * Update last used timestamp
     */
    public CompletableFuture<Void> updateLastUsed(Home home) {
        return CompletableFuture.runAsync(() -> {
            home.setLastUsed(System.currentTimeMillis());
            storage.saveHome(home).join();
        });
    }

    /**
     * Check if a player has access to a home
     */
    public boolean hasAccess(UUID playerId, Home home) {
        return home.hasAccess(playerId);
    }

    /**
     * Get home count for a player
     */
    public CompletableFuture<Integer> getHomeCount(UUID playerId) {
        return storage.getHomeCount(playerId);
    }

    /**
     * Check if a home exists
     */
    public CompletableFuture<Boolean> homeExists(UUID playerId, String name) {
        return storage.homeExists(playerId, name);
    }
}
