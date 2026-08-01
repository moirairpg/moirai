package me.moirai.storyengine.core.domain.adventure;

import java.util.UUID;

import me.moirai.storyengine.common.domain.DomainEvent;

public final class AdventureDeletedEvent implements DomainEvent {

    private final Long adventureId;
    private final UUID publicId;
    private final String imageKey;

    AdventureDeletedEvent(Long adventureId, UUID publicId, String imageKey) {

        this.adventureId = adventureId;
        this.publicId = publicId;
        this.imageKey = imageKey;
    }

    public Long getAdventureId() {
        return adventureId;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public String getImageKey() {
        return imageKey;
    }
}
