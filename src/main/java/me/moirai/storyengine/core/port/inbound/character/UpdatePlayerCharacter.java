package me.moirai.storyengine.core.port.inbound.character;

import java.util.Map;
import java.util.UUID;

import me.moirai.storyengine.common.cqs.command.Command;
import me.moirai.storyengine.common.enums.CharacterAttribute;
import me.moirai.storyengine.common.enums.CharacterClass;
import me.moirai.storyengine.common.util.Functions;

public record UpdatePlayerCharacter(
        UUID characterId,
        String name,
        CharacterClass characterClass,
        String personality,
        String physicalDescription,
        Map<CharacterAttribute, Integer> attributes,
        Double uiImagePositionX,
        Double uiImagePositionY,
        String requesterUsername)
        implements Command<PlayerCharacterDetails> {

    public UpdatePlayerCharacter {
        attributes = Functions.mapOrDefault(attributes, Map.of(), Map::copyOf);
    }
}
