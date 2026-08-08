package me.moirai.storyengine.core.domain.character;

import java.util.UUID;

import me.moirai.storyengine.common.domain.DomainEvent;

public final class PlayerCharacterDeletedEvent implements DomainEvent {

    private final Long playerCharacterId;
    private final Long playerId;
    private final UUID publicId;
    private final String imageKey;

    PlayerCharacterDeletedEvent(Long playerCharacterId, Long playerId, UUID publicId, String imageKey) {
        this.playerCharacterId = playerCharacterId;
        this.playerId = playerId;
        this.publicId = publicId;
        this.imageKey = imageKey;
    }

    public Long getPlayerCharacterId() {
        return playerCharacterId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public String getImageKey() {
        return imageKey;
    }
}
