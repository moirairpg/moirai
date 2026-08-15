package me.moirai.storyengine.core.domain.character;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import me.moirai.storyengine.common.enums.CharacterSkill;
import me.moirai.storyengine.common.exception.BusinessRuleViolationException;
import me.moirai.storyengine.common.rules.CharacterSheetRules;

public record SkillLevels(
        int athletics,
        int acrobatics,
        int stealth,
        int endurance,
        int lore,
        int alchemy,
        int destruction,
        int restoration,
        int illusion,
        int conjuration,
        int alteration,
        int perception,
        int survival,
        int intuition,
        int persuasion,
        int deception,
        int intimidation,
        int performance,
        int signature) {

    private static final int MIN_LEVEL = 0;

    public SkillLevels {

        validateRange(athletics);
        validateRange(acrobatics);
        validateRange(stealth);
        validateRange(endurance);
        validateRange(lore);
        validateRange(alchemy);
        validateRange(destruction);
        validateRange(restoration);
        validateRange(illusion);
        validateRange(conjuration);
        validateRange(alteration);
        validateRange(perception);
        validateRange(survival);
        validateRange(intuition);
        validateRange(persuasion);
        validateRange(deception);
        validateRange(intimidation);
        validateRange(performance);
        validateRange(signature);
    }

    public static SkillLevels of(Map<CharacterSkill, Integer> levels, int signature) {

        return new SkillLevels(
                levels.get(CharacterSkill.ATHLETICS),
                levels.get(CharacterSkill.ACROBATICS),
                levels.get(CharacterSkill.STEALTH),
                levels.get(CharacterSkill.ENDURANCE),
                levels.get(CharacterSkill.LORE),
                levels.get(CharacterSkill.ALCHEMY),
                levels.get(CharacterSkill.DESTRUCTION),
                levels.get(CharacterSkill.RESTORATION),
                levels.get(CharacterSkill.ILLUSION),
                levels.get(CharacterSkill.CONJURATION),
                levels.get(CharacterSkill.ALTERATION),
                levels.get(CharacterSkill.PERCEPTION),
                levels.get(CharacterSkill.SURVIVAL),
                levels.get(CharacterSkill.INTUITION),
                levels.get(CharacterSkill.PERSUASION),
                levels.get(CharacterSkill.DECEPTION),
                levels.get(CharacterSkill.INTIMIDATION),
                levels.get(CharacterSkill.PERFORMANCE),
                signature);
    }

    public Map<CharacterSkill, Integer> asMap() {

        var levels = new EnumMap<CharacterSkill, Integer>(CharacterSkill.class);
        levels.put(CharacterSkill.ATHLETICS, athletics);
        levels.put(CharacterSkill.ACROBATICS, acrobatics);
        levels.put(CharacterSkill.STEALTH, stealth);
        levels.put(CharacterSkill.ENDURANCE, endurance);
        levels.put(CharacterSkill.LORE, lore);
        levels.put(CharacterSkill.ALCHEMY, alchemy);
        levels.put(CharacterSkill.DESTRUCTION, destruction);
        levels.put(CharacterSkill.RESTORATION, restoration);
        levels.put(CharacterSkill.ILLUSION, illusion);
        levels.put(CharacterSkill.CONJURATION, conjuration);
        levels.put(CharacterSkill.ALTERATION, alteration);
        levels.put(CharacterSkill.PERCEPTION, perception);
        levels.put(CharacterSkill.SURVIVAL, survival);
        levels.put(CharacterSkill.INTUITION, intuition);
        levels.put(CharacterSkill.PERSUASION, persuasion);
        levels.put(CharacterSkill.DECEPTION, deception);
        levels.put(CharacterSkill.INTIMIDATION, intimidation);
        levels.put(CharacterSkill.PERFORMANCE, performance);

        return Collections.unmodifiableMap(levels);
    }

    private static void validateRange(int level) {

        if (level < MIN_LEVEL || level > CharacterSheetRules.SKILL_MAX_LEVEL) {
            throw new BusinessRuleViolationException("Skill levels must be between 0 and 4");
        }
    }
}
