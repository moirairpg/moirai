package me.moirai.storyengine.core.port.inbound.world;

import java.util.List;
import java.util.UUID;

import me.moirai.storyengine.common.cqs.command.Command;
import me.moirai.storyengine.common.util.Functions;
import me.moirai.storyengine.core.port.inbound.AssetMember;
import me.moirai.storyengine.core.port.inbound.AssetMemberInput;

public record UpdateWorldPermissions(
        UUID worldId,
        List<AssetMemberInput> members)
        implements Command<List<AssetMember>> {

    public UpdateWorldPermissions {
        members = Functions.mapOrDefault(members, List.of(), List::copyOf);
    }
}
