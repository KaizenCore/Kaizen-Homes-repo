package kaizen.core.kaizenHomes.storage;

import kaizen.core.kaizenHomes.models.Home;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface StorageAdapter {

    /**
     * Initialize the storage system
     */
    CompletableFuture<Void> initialize();

    /**
     * Save a home to storage
     */
    CompletableFuture<Void> saveHome(Home home);

    /**
     * Delete a home from storage
     */
    CompletableFuture<Void> deleteHome(UUID owner, String name);

    /**
     * Get a specific home
     */
    CompletableFuture<Home> getHome(UUID owner, String name);

    /**
     * Get all homes for a player
     */
    CompletableFuture<List<Home>> getHomes(UUID owner);

    /**
     * Get all public homes
     */
    CompletableFuture<List<Home>> getPublicHomes();

    /**
     * Get homes shared with a player
     */
    CompletableFuture<List<Home>> getSharedHomes(UUID playerId);

    /**
     * Check if a home exists
     */
    CompletableFuture<Boolean> homeExists(UUID owner, String name);

    /**
     * Get the total number of homes for a player
     */
    CompletableFuture<Integer> getHomeCount(UUID owner);

    /**
     * Close the storage connection
     */
    void close();

    /**
     * Reload data from storage
     */
    CompletableFuture<Void> reload();
}
