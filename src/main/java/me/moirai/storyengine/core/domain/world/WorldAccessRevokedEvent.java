package me.moirai.storyengine.core.domain.world;

import java.util.UUID;

import me.moirai.storyengine.common.domain.DomainEvent;

public final class WorldAccessRevokedEvent implements DomainEvent {

    private final UUID worldPublicId;
    private final String worldName;
    private final Long userId;

    WorldAccessRevokedEvent(UUID worldPublicId, String worldName, Long userId) {

        this.worldPublicId = worldPublicId;
        this.worldName = worldName;
        this.userId = userId;
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
}
