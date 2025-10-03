package kaizen.core.kaizenHomes.models;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.*;

public class Home {
    private final UUID owner;
    private String name;
    private Location location;
    private String description;
    private Material icon;
    private PrivacyMode privacyMode;
    private Set<UUID> sharedWith;
    private String category;
    private long createdAt;
    private long lastUsed;
    private boolean isDefault;

    public Home(UUID owner, String name, Location location) {
        this.owner = owner;
        this.name = name;
        this.location = location;
        this.description = "";
        this.icon = Material.RED_BED;
        this.privacyMode = PrivacyMode.PRIVATE;
        this.sharedWith = new HashSet<>();
        this.category = "default";
        this.createdAt = System.currentTimeMillis();
        this.lastUsed = 0;
        this.isDefault = false;
    }

    // Getters
    public UUID getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public Material getIcon() {
        return icon;
    }

    public PrivacyMode getPrivacyMode() {
        return privacyMode;
    }

    public Set<UUID> getSharedWith() {
        return new HashSet<>(sharedWith);
    }

    public String getCategory() {
        return category;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getLastUsed() {
        return lastUsed;
    }

    public boolean isDefault() {
        return isDefault;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public void setPrivacyMode(PrivacyMode privacyMode) {
        this.privacyMode = privacyMode;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setLastUsed(long lastUsed) {
        this.lastUsed = lastUsed;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setSharedWith(Set<UUID> sharedWith) {
        this.sharedWith = new HashSet<>(sharedWith);
    }

    // Sharing methods
    public void addSharedPlayer(UUID playerId) {
        sharedWith.add(playerId);
    }

    public void removeSharedPlayer(UUID playerId) {
        sharedWith.remove(playerId);
    }

    public boolean hasAccess(UUID playerId) {
        if (owner.equals(playerId)) return true;
        if (privacyMode == PrivacyMode.PUBLIC) return true;
        return privacyMode == PrivacyMode.SHARED && sharedWith.contains(playerId);
    }

    // Utility methods
    public String getFormattedLocation() {
        return String.format("%s (%.0f, %.0f, %.0f)",
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Home home = (Home) o;
        return owner.equals(home.owner) && name.equals(home.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, name);
    }

    public enum PrivacyMode {
        PRIVATE,  // Only owner can access
        SHARED,   // Owner + specific players can access
        PUBLIC    // Anyone can access
    }
}
