package me.moirai.storyengine.core.port.inbound.character;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import me.moirai.storyengine.common.enums.CharacterAttribute;
import me.moirai.storyengine.common.enums.CharacterClass;
import me.moirai.storyengine.common.util.Functions;

public record PlayerCharacterDetails(
        UUID id,
        String ownerUsername,
        String name,
        CharacterClass characterClass,
        String personality,
        String physicalDescription,
        Map<CharacterAttribute, Integer> attributes,
        String imageUrl,
        Double uiImagePositionX,
        Double uiImagePositionY,
        Instant creationDate,
        Instant lastUpdateDate,
        boolean canManage,
        boolean isOwner) {

    public PlayerCharacterDetails {
        attributes = Functions.mapOrDefault(attributes, Map.of(), Map::copyOf);
    }
}
