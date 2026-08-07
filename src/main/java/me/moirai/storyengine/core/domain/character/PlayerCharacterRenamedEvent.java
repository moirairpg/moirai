package me.moirai.storyengine.core.domain.character;

import me.moirai.storyengine.common.domain.DomainEvent;

public final class PlayerCharacterRenamedEvent implements DomainEvent {

    private final Long playerCharacterId;
    private final String name;

    PlayerCharacterRenamedEvent(Long playerCharacterId, String name) {
        this.playerCharacterId = playerCharacterId;
        this.name = name;
    }

    public Long getPlayerCharacterId() {
        return playerCharacterId;
    }

    public String getName() {
        return name;
    }
}
