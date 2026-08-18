package me.moirai.storyengine.core.port.inbound.character;

import java.util.Map;
import java.util.UUID;

import me.moirai.storyengine.common.cqs.command.Command;
import me.moirai.storyengine.common.enums.CharacterAttribute;
import me.moirai.storyengine.common.enums.CharacterClass;
import me.moirai.storyengine.common.enums.CharacterSkill;
import me.moirai.storyengine.common.enums.SignatureSkill;
import me.moirai.storyengine.common.util.Functions;

public record UpdateCharacterSheet(
        UUID characterId,
        CharacterClass characterClass,
        Map<CharacterAttribute, Integer> attributes,
        Map<CharacterSkill, Integer> skills,
        Map<SignatureSkill, Integer> signatureSkill,
        String requesterUsername)
        implements Command<PlayerCharacterDetails> {

    public UpdateCharacterSheet {

        attributes = Functions.mapOrDefault(attributes, Map.of(), Map::copyOf);
        skills = Functions.mapOrDefault(skills, Map.of(), Map::copyOf);
        signatureSkill = Functions.mapOrDefault(signatureSkill, Map.of(), Map::copyOf);
    }
}
