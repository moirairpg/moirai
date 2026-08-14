package me.moirai.storyengine.core.domain.character;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import me.moirai.storyengine.common.enums.CharacterAttribute;
import me.moirai.storyengine.common.exception.BusinessRuleViolationException;

public record AttributeLevels(
        int strength,
        int agility,
        int vigor,
        int intelligence,
        int awareness,
        int charisma) {

    private static final int MIN_LEVEL = 0;
    private static final int MAX_LEVEL = 5;

    public AttributeLevels {

        validateRange(strength);
        validateRange(agility);
        validateRange(vigor);
        validateRange(intelligence);
        validateRange(awareness);
        validateRange(charisma);
    }

    public static AttributeLevels of(Map<CharacterAttribute, Integer> levels) {

        return new AttributeLevels(
                levels.get(CharacterAttribute.STRENGTH),
                levels.get(CharacterAttribute.AGILITY),
                levels.get(CharacterAttribute.VIGOR),
                levels.get(CharacterAttribute.INTELLIGENCE),
                levels.get(CharacterAttribute.AWARENESS),
                levels.get(CharacterAttribute.CHARISMA));
    }

    public Map<CharacterAttribute, Integer> asMap() {

        var levels = new EnumMap<CharacterAttribute, Integer>(CharacterAttribute.class);
        levels.put(CharacterAttribute.STRENGTH, strength);
        levels.put(CharacterAttribute.AGILITY, agility);
        levels.put(CharacterAttribute.VIGOR, vigor);
        levels.put(CharacterAttribute.INTELLIGENCE, intelligence);
        levels.put(CharacterAttribute.AWARENESS, awareness);
        levels.put(CharacterAttribute.CHARISMA, charisma);

        return Collections.unmodifiableMap(levels);
    }

    private static void validateRange(int level) {

        if (level < MIN_LEVEL || level > MAX_LEVEL) {
            throw new BusinessRuleViolationException("Attribute levels must be between 0 and 5");
        }
    }
}
