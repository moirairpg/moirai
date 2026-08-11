package me.moirai.storyengine.core.port.outbound.world;

import java.util.List;
import java.util.UUID;

import me.moirai.storyengine.core.port.inbound.AssetMember;

public interface WorldPermissionReader {

    List<AssetMember> getAllByWorldPublicId(UUID worldPublicId);
}
