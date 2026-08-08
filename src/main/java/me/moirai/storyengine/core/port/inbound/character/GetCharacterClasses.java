package me.moirai.storyengine.core.port.inbound.character;

import java.util.List;

import me.moirai.storyengine.common.cqs.query.Query;

public record GetCharacterClasses() implements Query<List<CharacterClassResult>> {
}
