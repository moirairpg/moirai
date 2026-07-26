package me.moirai.storyengine.core.domain.adventure;

import java.util.UUID;

import me.moirai.storyengine.common.domain.DomainEvent;

public final class PlayerRemovedFromAdventureEvent implements DomainEvent {

    private final Long adventureId;
    private final UUID adventurePublicId;
    private final String adventureName;
    private final Long removedUserId;

    PlayerRemovedFromAdventureEvent(
            Long adventureId,
            UUID adventurePublicId,
            String adventureName,
            Long removedUserId) {

        this.adventureId = adventureId;
        this.adventurePublicId = adventurePublicId;
        this.adventureName = adventureName;
        this.removedUserId = removedUserId;
    }

    public Long getAdventureId() {
        return adventureId;
    }

    public UUID getAdventurePublicId() {
        return adventurePublicId;
    }

    public String getAdventureName() {
        return adventureName;
    }

    public Long getRemovedUserId() {
        return removedUserId;
    }
}
