package me.moirai.storyengine.core.domain.character;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.test.util.ReflectionTestUtils;

import me.moirai.storyengine.common.enums.CharacterAttribute;
import me.moirai.storyengine.common.enums.CharacterClass;
import me.moirai.storyengine.common.enums.CharacterSkill;
import me.moirai.storyengine.common.enums.SignatureSkill;

public class PlayerCharacterFixture {

    public static final UUID PUBLIC_ID = UUID.fromString("857345aa-4444-0000-0000-000000000000");
    public static final Long NUMERIC_ID = 4L;
    public static final Long PLAYER_ID = 1111L;

    public static PlayerCharacter.Builder samplePlayerCharacter() {

        var builder = PlayerCharacter.builder();
        builder.name("Volin Habar");
        builder.playerId(PLAYER_ID);
        builder.personality("Brave, honorable and disciplined.");
        builder.physicalDescription("A tall warrior with long black hair and a scar across his left cheek.");
        builder.background("Raised in a cliffside monastery, he took the oath after his village burned.");
        builder.characterClass(CharacterClass.PALADIN);
        builder.attributes(sampleAttributeAllocation());
        builder.skills(sampleSkillAllocation());
        builder.signatureSkill(sampleSignatureAllocation());

        return builder;
    }

    public static PlayerCharacter samplePlayerCharacterWithId() {

        var character = samplePlayerCharacter().build();
        ReflectionTestUtils.setField(character, "id", NUMERIC_ID);
        ReflectionTestUtils.setField(character, "publicId", PUBLIC_ID);

        return character;
    }

    public static Map<CharacterAttribute, Integer> sampleAttributeAllocation() {

        return Map.of(
                CharacterAttribute.STRENGTH, 3,
                CharacterAttribute.AGILITY, 0,
                CharacterAttribute.VIGOR, 2,
                CharacterAttribute.INTELLIGENCE, 0,
                CharacterAttribute.AWARENESS, 0,
                CharacterAttribute.CHARISMA, 1);
    }

    public static Map<CharacterSkill, Integer> sampleSkillAllocation() {
        return skillAllocationFor(CharacterClass.PALADIN);
    }

    public static Map<SignatureSkill, Integer> sampleSignatureAllocation() {
        return signatureAllocationFor(CharacterClass.PALADIN);
    }

    public static Map<CharacterSkill, Integer> skillAllocationFor(CharacterClass characterClass) {

        var allocation = new EnumMap<CharacterSkill, Integer>(CharacterSkill.class);
        Arrays.stream(CharacterSkill.values()).forEach(skill -> allocation.put(skill, 0));
        allocation.put(characterClass.getFavoredSkills().get(0), 2);
        allocation.put(characterClass.getFavoredSkills().get(1), 2);

        return allocation;
    }

    public static Map<SignatureSkill, Integer> signatureAllocationFor(CharacterClass characterClass) {
        return Map.of(characterClass.getSignature(), 1);
    }
}
