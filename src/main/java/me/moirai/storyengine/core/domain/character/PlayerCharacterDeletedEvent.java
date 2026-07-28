package me.moirai.storyengine.core.domain.character;

import me.moirai.storyengine.common.domain.DomainEvent;

public final class PlayerCharacterDeletedEvent implements DomainEvent {

    private final Long playerCharacterId;
    private final Long playerId;

    PlayerCharacterDeletedEvent(Long playerCharacterId, Long playerId) {
        this.playerCharacterId = playerCharacterId;
        this.playerId = playerId;
    }

    public Long getPlayerCharacterId() {
        return playerCharacterId;
    }

    public Long getPlayerId() {
        return playerId;
    }
}
