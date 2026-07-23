package me.moirai.storyengine.core.port.outbound.character;

import java.util.List;

import me.moirai.storyengine.common.util.Functions;
import me.moirai.storyengine.core.port.inbound.AssetPermissionsData;

public record PlayerCharacterVisibilityData(
        String ownerUsername,
        List<AssetPermissionsData> registeredAdventurePermissions) {

    public PlayerCharacterVisibilityData {
        registeredAdventurePermissions = Functions.mapOrDefault(
                registeredAdventurePermissions, List.of(), List::copyOf);
    }
}
