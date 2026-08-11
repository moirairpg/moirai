package me.moirai.storyengine.core.port.inbound.world;

import java.util.List;
import java.util.UUID;

import me.moirai.storyengine.common.cqs.query.Query;
import me.moirai.storyengine.core.port.inbound.AssetMember;

public record GetWorldMembers(UUID worldId) implements Query<List<AssetMember>> {
}
