package me.moirai.storyengine.core.application.query.character;

import java.util.Arrays;

import me.moirai.storyengine.common.annotation.QueryHandler;
import me.moirai.storyengine.common.cqs.query.AbstractQueryHandler;
import me.moirai.storyengine.common.enums.CharacterAttribute;
import me.moirai.storyengine.common.rules.CharacterSheetRules;
import me.moirai.storyengine.core.port.inbound.character.AttributeCreationRulesResult;
import me.moirai.storyengine.core.port.inbound.character.CharacterAttributeResult;
import me.moirai.storyengine.core.port.inbound.character.CharacterAttributesResult;
import me.moirai.storyengine.core.port.inbound.character.GetCharacterAttributes;

@QueryHandler
public class GetCharacterAttributesHandler
        extends AbstractQueryHandler<GetCharacterAttributes, CharacterAttributesResult> {

    @Override
    public CharacterAttributesResult execute(GetCharacterAttributes query) {

        var attributes = Arrays.stream(CharacterAttribute.values())
                .map(attribute -> new CharacterAttributeResult(attribute.name(), attribute.getLabel()))
                .toList();

        var creation = new AttributeCreationRulesResult(
                CharacterSheetRules.ATTRIBUTE_CREATION_POINTS,
                CharacterSheetRules.ATTRIBUTE_CREATION_LEVEL_CAP);

        return new CharacterAttributesResult(attributes, CharacterSheetRules.ATTRIBUTE_MAX_LEVEL, creation);
    }
}
