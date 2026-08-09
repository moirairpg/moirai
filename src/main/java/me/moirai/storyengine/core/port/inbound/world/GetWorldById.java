package me.moirai.storyengine.core.port.inbound.world;

import java.util.UUID;

import me.moirai.storyengine.common.cqs.query.Query;

public record GetWorldById(
        UUID worldId,
        UUID requesterId)
        implements Query<WorldDetails> {
}
