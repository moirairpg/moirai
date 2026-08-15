package me.moirai.storyengine.core.port.outbound.character;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import me.moirai.storyengine.common.enums.CharacterAttribute;
import me.moirai.storyengine.common.enums.CharacterClass;
import me.moirai.storyengine.common.enums.CharacterSkill;
import me.moirai.storyengine.common.util.Functions;

public record PlayerCharacterDetailsRow(
        UUID id,
        String ownerUsername,
        String name,
        CharacterClass characterClass,
        String personality,
        String physicalDescription,
        Map<CharacterAttribute, Integer> attributes,
        Map<CharacterSkill, Integer> skills,
        Integer signatureLevel,
        String imageKey,
        Double uiImagePositionX,
        Double uiImagePositionY,
        Instant creationDate,
        Instant lastUpdateDate) {

    public PlayerCharacterDetailsRow {

        attributes = Functions.mapOrDefault(attributes, Map.of(), Map::copyOf);
        skills = Functions.mapOrDefault(skills, Map.of(), Map::copyOf);
    }
}
