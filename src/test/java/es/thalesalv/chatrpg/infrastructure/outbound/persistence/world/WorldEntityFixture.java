package es.thalesalv.chatrpg.infrastructure.outbound.persistence.world;

import es.thalesalv.chatrpg.core.domain.world.World;
import es.thalesalv.chatrpg.core.domain.world.WorldFixture;

public class WorldEntityFixture {

    public static WorldEntity.Builder publicWorld() {

        World world = WorldFixture.publicWorld().build();

        return WorldEntity.builder()
                .id(world.getId())
                .name(world.getName())
                .description(world.getDescription())
                .adventureStart(world.getAdventureStart())
                .visibility(world.getVisibility().toString())
                .ownerDiscordId(world.getOwnerDiscordId())
                .usersAllowedToRead(world.getReaderUsers())
                .usersAllowedToWrite(world.getWriterUsers());
    }

    public static WorldEntity.Builder privateWorld() {

        World world = WorldFixture.privateWorld().build();

        return WorldEntity.builder()
                .id(world.getId())
                .name(world.getName())
                .description(world.getDescription())
                .adventureStart(world.getAdventureStart())
                .visibility(world.getVisibility().toString())
                .ownerDiscordId(world.getOwnerDiscordId())
                .usersAllowedToRead(world.getReaderUsers())
                .usersAllowedToWrite(world.getWriterUsers());
    }
}
