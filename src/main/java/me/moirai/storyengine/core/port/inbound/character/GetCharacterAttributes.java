package me.moirai.storyengine.core.port.inbound.character;

import me.moirai.storyengine.common.cqs.query.Query;

public record GetCharacterAttributes() implements Query<CharacterAttributesResult> {
}
