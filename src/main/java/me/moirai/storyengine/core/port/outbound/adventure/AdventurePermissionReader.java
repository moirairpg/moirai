package me.moirai.storyengine.core.port.outbound.adventure;

import java.util.List;
import java.util.UUID;

import me.moirai.storyengine.core.port.inbound.AssetMember;

public interface AdventurePermissionReader {

    List<AssetMember> getAllByAdventurePublicId(UUID adventurePublicId);
}
