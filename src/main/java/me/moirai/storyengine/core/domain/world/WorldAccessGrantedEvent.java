package me.moirai.storyengine.core.domain.world;

import java.util.UUID;

import me.moirai.storyengine.common.domain.DomainEvent;
import me.moirai.storyengine.common.enums.PermissionLevel;

public final class WorldAccessGrantedEvent implements DomainEvent {

    private final UUID worldPublicId;
    private final String worldName;
    private final Long userId;
    private final PermissionLevel level;

    WorldAccessGrantedEvent(UUID worldPublicId, String worldName, Long userId, PermissionLevel level) {

        this.worldPublicId = worldPublicId;
        this.worldName = worldName;
        this.userId = userId;
        this.level = level;
    }

    public UUID getWorldPublicId() {
        return worldPublicId;
    }

    public String getWorldName() {
        return worldName;
    }

    public Long getUserId() {
        return userId;
    }

    public PermissionLevel getLevel() {
        return level;
    }
}
