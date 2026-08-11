package me.moirai.storyengine.core.port.inbound.adventure;

import java.util.List;
import java.util.UUID;

import me.moirai.storyengine.common.cqs.command.Command;
import me.moirai.storyengine.common.enums.Visibility;
import me.moirai.storyengine.common.util.Functions;
import me.moirai.storyengine.core.port.inbound.AssetMember;
import me.moirai.storyengine.core.port.inbound.AssetMemberInput;

public record UpdateAdventurePermissions(
        UUID adventureId,
        Visibility visibility,
        List<AssetMemberInput> members)
        implements Command<List<AssetMember>> {

    public UpdateAdventurePermissions {
        members = Functions.mapOrDefault(members, List.of(), List::copyOf);
    }
}
