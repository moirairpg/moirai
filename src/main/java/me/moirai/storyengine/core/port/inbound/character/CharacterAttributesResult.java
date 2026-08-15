package me.moirai.storyengine.core.port.inbound.character;

import java.util.List;

import me.moirai.storyengine.common.util.Functions;

public record CharacterAttributesResult(
        List<CharacterAttributeResult> attributes,
        int maxLevel,
        AttributeCreationRulesResult creation) {

    public CharacterAttributesResult {
        attributes = Functions.mapOrDefault(attributes, List.of(), List::copyOf);
    }
}
