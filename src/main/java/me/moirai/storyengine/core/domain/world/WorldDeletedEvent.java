package me.moirai.storyengine.core.domain.world;

import java.util.UUID;

import me.moirai.storyengine.common.domain.DomainEvent;

public final class WorldDeletedEvent implements DomainEvent {

    private final UUID publicId;
    private final String imageKey;

    WorldDeletedEvent(UUID publicId, String imageKey) {

        this.publicId = publicId;
        this.imageKey = imageKey;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public String getImageKey() {
        return imageKey;
    }
}
