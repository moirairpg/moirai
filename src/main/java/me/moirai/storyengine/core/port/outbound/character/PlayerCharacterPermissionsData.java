package me.moirai.storyengine.core.port.outbound.character;

import java.util.List;

import me.moirai.storyengine.common.util.Functions;
import me.moirai.storyengine.core.port.inbound.AssetPermissionsData;

public record PlayerCharacterPermissionsData(
        String ownerUsername,
        List<AssetPermissionsData> enrolledAdventurePermissions) {

    public PlayerCharacterPermissionsData {
        enrolledAdventurePermissions = Functions.mapOrDefault(
                enrolledAdventurePermissions, List.of(), List::copyOf);
    }
}
