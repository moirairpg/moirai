package me.moirai.storyengine.core.application.query.character;

import java.util.Arrays;
import java.util.List;

import me.moirai.storyengine.common.annotation.QueryHandler;
import me.moirai.storyengine.common.cqs.query.AbstractQueryHandler;
import me.moirai.storyengine.common.enums.CharacterClass;
import me.moirai.storyengine.core.port.inbound.character.CharacterClassResult;
import me.moirai.storyengine.core.port.inbound.character.CharacterSkillResult;
import me.moirai.storyengine.core.port.inbound.character.GetCharacterClasses;

@QueryHandler
public class GetCharacterClassesHandler extends AbstractQueryHandler<GetCharacterClasses, List<CharacterClassResult>> {

    @Override
    public List<CharacterClassResult> execute(GetCharacterClasses query) {

        return Arrays.stream(CharacterClass.values())
                .map(this::toResult)
                .toList();
    }

    private CharacterClassResult toResult(CharacterClass characterClass) {

        var signature = characterClass.getSignature();
        var favoredSkills = characterClass.getFavoredSkills().stream()
                .map(Enum::name)
                .toList();

        return new CharacterClassResult(
                characterClass.name(),
                characterClass.getLabel(),
                new CharacterSkillResult(signature.name(), signature.getLabel(),
                        signature.getAttribute().name()),
                favoredSkills);
    }
}
