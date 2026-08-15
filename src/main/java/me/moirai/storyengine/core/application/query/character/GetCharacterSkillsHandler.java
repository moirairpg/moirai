package me.moirai.storyengine.core.application.query.character;

import java.util.Arrays;

import me.moirai.storyengine.common.annotation.QueryHandler;
import me.moirai.storyengine.common.cqs.query.AbstractQueryHandler;
import me.moirai.storyengine.common.enums.CharacterSkill;
import me.moirai.storyengine.common.rules.CharacterSheetRules;
import me.moirai.storyengine.core.port.inbound.character.CharacterSkillResult;
import me.moirai.storyengine.core.port.inbound.character.CharacterSkillsResult;
import me.moirai.storyengine.core.port.inbound.character.GetCharacterSkills;
import me.moirai.storyengine.core.port.inbound.character.SkillCreationRulesResult;

@QueryHandler
public class GetCharacterSkillsHandler
        extends AbstractQueryHandler<GetCharacterSkills, CharacterSkillsResult> {

    @Override
    public CharacterSkillsResult execute(GetCharacterSkills query) {

        var skills = Arrays.stream(CharacterSkill.values())
                .map(skill -> new CharacterSkillResult(skill.name(), skill.getLabel(),
                        skill.getAttribute().name()))
                .toList();

        var creation = new SkillCreationRulesResult(
                CharacterSheetRules.SKILL_CREATION_POINTS,
                CharacterSheetRules.SKILL_CREATION_LEVEL_CAP,
                CharacterSheetRules.FAVORED_SKILL_COST,
                CharacterSheetRules.OFF_CLASS_SKILL_COST,
                CharacterSheetRules.SIGNATURE_STARTING_LEVEL);

        return new CharacterSkillsResult(skills, CharacterSheetRules.SKILL_MAX_LEVEL, creation);
    }
}
