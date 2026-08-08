package me.moirai.storyengine.core.port.inbound.character;

import java.util.List;

import me.moirai.storyengine.common.cqs.query.Query;

public record ListPlayerCharactersByName(
        String name,
        Long requesterId)
        implements Query<List<PlayerCharacterSummary>> {
}
