package me.moirai.storyengine.core.domain.adventure;

import java.util.UUID;

import me.moirai.storyengine.common.domain.DomainEvent;

public final class EnrolledCharacterDeletedEvent implements DomainEvent {

    private final Long adventureId;
    private final UUID adventurePublicId;
    private final String adventureName;
    private final Long playerId;
    private final Long playerCharacterId;

    EnrolledCharacterDeletedEvent(
            Long adventureId,
            UUID adventurePublicId,
            String adventureName,
            Long playerId,
            Long playerCharacterId) {

        this.adventureId = adventureId;
        this.adventurePublicId = adventurePublicId;
        this.adventureName = adventureName;
        this.playerId = playerId;
        this.playerCharacterId = playerCharacterId;
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

    public Long getPlayerId() {
        return playerId;
    }

    public Long getPlayerCharacterId() {
        return playerCharacterId;
    }
}
