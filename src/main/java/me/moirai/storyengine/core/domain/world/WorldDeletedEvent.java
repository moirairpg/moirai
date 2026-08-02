package me.moirai.storyengine.core.domain.world;

import java.util.UUID;

import me.moirai.storyengine.common.domain.DomainEvent;

public final class WorldDeletedEvent implements DomainEvent {

    private final Long worldId;
    private final UUID publicId;
    private final String imageKey;

    WorldDeletedEvent(Long worldId, UUID publicId, String imageKey) {

        this.worldId = worldId;
        this.publicId = publicId;
        this.imageKey = imageKey;
    }

    public Long getWorldId() {
        return worldId;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public String getImageKey() {
        return imageKey;
    }
}
