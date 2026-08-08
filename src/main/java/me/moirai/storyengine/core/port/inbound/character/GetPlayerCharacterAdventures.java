package me.moirai.storyengine.core.port.inbound.character;

import java.util.List;
import java.util.UUID;

import me.moirai.storyengine.common.cqs.query.Query;
import me.moirai.storyengine.core.port.inbound.adventure.CharacterAdventureSummary;

public record GetPlayerCharacterAdventures(UUID characterId)
        implements Query<List<CharacterAdventureSummary>> {
}
