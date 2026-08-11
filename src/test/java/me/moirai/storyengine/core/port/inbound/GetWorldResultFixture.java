package me.moirai.storyengine.core.port.inbound;

import java.util.Set;

import me.moirai.storyengine.core.domain.world.WorldFixture;
import me.moirai.storyengine.core.port.inbound.world.WorldDetails;

public class GetWorldResultFixture {

    public static WorldDetails publicWorld() {

        var world = WorldFixture.publicWorldWithIdAndPermissions();

        return new WorldDetails(
                world.getPublicId(),
                world.getName(),
                world.getDescription(),
                world.getAdventureStart(),
                world.getNarratorName(),
                world.getNarratorPersonality(),
                world.getVisibility().name(),
                null,
                true,
                true,
                Set.of(),
                world.getCreationDate(),
                world.getLastUpdateDate(),
                null,
                null);
    }

    public static WorldDetails privateWorld() {

        var world = WorldFixture.privateWorldWithIdAndPermissions();

        return new WorldDetails(
                world.getPublicId(),
                world.getName(),
                world.getDescription(),
                world.getAdventureStart(),
                world.getNarratorName(),
                world.getNarratorPersonality(),
                world.getVisibility().name(),
                null,
                true,
                true,
                Set.of(),
                world.getCreationDate(),
                world.getLastUpdateDate(),
                null,
                null);
    }
}
