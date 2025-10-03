package kaizen.core.kaizenHomes.models;

import java.util.UUID;

public class HomePermission {
    private final UUID homeOwner;
    private final String homeName;
    private final UUID grantedTo;
    private final PermissionType type;
    private final long grantedAt;

    public HomePermission(UUID homeOwner, String homeName, UUID grantedTo, PermissionType type) {
        this.homeOwner = homeOwner;
        this.homeName = homeName;
        this.grantedTo = grantedTo;
        this.type = type;
        this.grantedAt = System.currentTimeMillis();
    }

    public UUID getHomeOwner() {
        return homeOwner;
    }

    public String getHomeName() {
        return homeName;
    }

    public UUID getGrantedTo() {
        return grantedTo;
    }

    public PermissionType getType() {
        return type;
    }

    public long getGrantedAt() {
        return grantedAt;
    }

    public enum PermissionType {
        VISIT,      // Can only teleport to the home
        MANAGE,     // Can modify home settings (for co-owners)
        ADMIN       // Full access (for trusted players)
    }
}
