package es.thalesalv.chatrpg.core.application.command.world;

import java.util.List;

import es.thalesalv.chatrpg.core.domain.world.World;
import es.thalesalv.chatrpg.core.domain.world.WorldFixture;

public class CreateWorldFixture {

    public static CreateWorld.Builder createPrivateWorld() {

        World world = WorldFixture.privateWorld().build();

        List<WorldLorebookEntry> lorebookEntries = world.getLorebook()
                .stream()
                .map(entry -> WorldLorebookEntry.builder()
                        .name(entry.getName())
                        .description(entry.getDescription())
                        .regex(entry.getRegex())
                        .playerDiscordId(entry.getPlayerDiscordId())
                        .build())
                .toList();

        return CreateWorld.builder()
                .name(world.getName())
                .description(world.getDescription())
                .adventureStart(world.getAdventureStart())
                .visibility(world.getVisibility().toString())
                .creatorDiscordId(world.getOwnerDiscordId())
                .writerUsers(world.getWriterUsers())
                .readerUsers(world.getReaderUsers())
                .lorebookEntries(lorebookEntries);
    }
}
