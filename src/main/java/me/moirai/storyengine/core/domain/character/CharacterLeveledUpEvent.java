package me.moirai.storyengine.core.domain.character;

import java.util.UUID;

import me.moirai.storyengine.common.domain.DomainEvent;

public final class CharacterLeveledUpEvent implements DomainEvent {

    private final Long characterId;
    private final UUID characterPublicId;
    private final Long playerId;
    private final String characterName;
    private final int newLevel;

    CharacterLeveledUpEvent(Long characterId, UUID characterPublicId, Long playerId, String characterName,
            int newLevel) {

        this.characterId = characterId;
        this.characterPublicId = characterPublicId;
        this.playerId = playerId;
        this.characterName = characterName;
        this.newLevel = newLevel;
    }

    public Long getCharacterId() {
        return characterId;
    }

    public UUID getCharacterPublicId() {
        return characterPublicId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public String getCharacterName() {
        return characterName;
    }

    public int getNewLevel() {
        return newLevel;
    }
}
