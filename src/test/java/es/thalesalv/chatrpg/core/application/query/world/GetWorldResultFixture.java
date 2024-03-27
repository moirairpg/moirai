package es.thalesalv.chatrpg.core.application.query.world;

import es.thalesalv.chatrpg.core.domain.world.World;
import es.thalesalv.chatrpg.core.domain.world.WorldFixture;

public class GetWorldResultFixture {

    public static GetWorldResult.Builder publicWorld() {

        World world = WorldFixture.publicWorld().build();

        return GetWorldResult.builder()
                .id(world.getId())
                .name(world.getName())
                .description(world.getDescription())
                .adventureStart(world.getAdventureStart())
                .visibility(world.getVisibility().toString())
                .ownerDiscordId(world.getOwnerDiscordId())
                .creationDate(world.getCreationDate())
                .readerUsers(world.getReaderUsers())
                .writerUsers(world.getWriterUsers());
    }

    public static GetWorldResult.Builder privateWorld() {

        World world = WorldFixture.privateWorld().build();

        return GetWorldResult.builder()
                .id(world.getId())
                .name(world.getName())
                .description(world.getDescription())
                .adventureStart(world.getAdventureStart())
                .visibility(world.getVisibility().toString())
                .ownerDiscordId(world.getOwnerDiscordId())
                .creationDate(world.getCreationDate())
                .readerUsers(world.getReaderUsers())
                .writerUsers(world.getWriterUsers());
    }
}
