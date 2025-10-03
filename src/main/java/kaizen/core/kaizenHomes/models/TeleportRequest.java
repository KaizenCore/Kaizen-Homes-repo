package kaizen.core.kaizenHomes.models;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeleportRequest {
    private final UUID playerId;
    private final Location fromLocation;
    private final Location toLocation;
    private final String homeName;
    private final long startTime;
    private final int warmupSeconds;
    private boolean cancelled;

    public TeleportRequest(Player player, Location toLocation, String homeName, int warmupSeconds) {
        this.playerId = player.getUniqueId();
        this.fromLocation = player.getLocation().clone();
        this.toLocation = toLocation;
        this.homeName = homeName;
        this.startTime = System.currentTimeMillis();
        this.warmupSeconds = warmupSeconds;
        this.cancelled = false;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public Location getFromLocation() {
        return fromLocation;
    }

    public Location getToLocation() {
        return toLocation;
    }

    public String getHomeName() {
        return homeName;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getWarmupSeconds() {
        return warmupSeconds;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void cancel() {
        this.cancelled = true;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - startTime > (warmupSeconds * 1000L);
    }

    public int getRemainingSeconds() {
        long elapsed = System.currentTimeMillis() - startTime;
        long remaining = (warmupSeconds * 1000L) - elapsed;
        return Math.max(0, (int) (remaining / 1000));
    }

    public boolean hasPlayerMoved(Location currentLocation) {
        // Check if player has moved more than 0.5 blocks from starting position
        return fromLocation.distance(currentLocation) > 0.5;
    }
}
