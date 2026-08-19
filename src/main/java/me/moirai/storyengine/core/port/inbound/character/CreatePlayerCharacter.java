package me.moirai.storyengine.core.port.inbound.character;

import java.util.Map;

import me.moirai.storyengine.common.cqs.command.Command;
import me.moirai.storyengine.common.enums.CharacterAttribute;
import me.moirai.storyengine.common.enums.CharacterClass;
import me.moirai.storyengine.common.enums.CharacterSkill;
import me.moirai.storyengine.common.enums.SignatureSkill;
import me.moirai.storyengine.common.util.Functions;

public record CreatePlayerCharacter(
        String name,
        CharacterClass characterClass,
        String personality,
        String physicalDescription,
        String background,
        Map<CharacterAttribute, Integer> attributes,
        Map<CharacterSkill, Integer> skills,
        Map<SignatureSkill, Integer> signatureSkill,
        Double uiImagePositionX,
        Double uiImagePositionY,
        Long requesterId)
        implements Command<PlayerCharacterDetails> {

    public CreatePlayerCharacter {

        attributes = Functions.mapOrDefault(attributes, Map.of(), Map::copyOf);
        skills = Functions.mapOrDefault(skills, Map.of(), Map::copyOf);
        signatureSkill = Functions.mapOrDefault(signatureSkill, Map.of(), Map::copyOf);
    }
}
