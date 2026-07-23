package me.moirai.storyengine.core.domain.character;

import me.moirai.storyengine.common.domain.DomainEvent;

public final class PlayerCharacterDeletedEvent implements DomainEvent {

    private final Long playerCharacterId;

    PlayerCharacterDeletedEvent(Long playerCharacterId) {
        this.playerCharacterId = playerCharacterId;
    }

    public Long getPlayerCharacterId() {
        return playerCharacterId;
    }
}
