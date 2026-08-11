package me.moirai.storyengine.core.port.inbound.adventure;

import java.util.List;
import java.util.UUID;

import me.moirai.storyengine.common.cqs.query.Query;
import me.moirai.storyengine.core.port.inbound.AssetMember;

public record GetAdventureMembers(UUID adventureId) implements Query<List<AssetMember>> {
}
