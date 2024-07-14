package me.moirai.discordbot.core.application.usecase.world.request;

import java.util.List;

import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.core.domain.world.WorldFixture;

public class CreateWorldFixture {

    public static CreateWorld.Builder createPrivateWorld() {

        World world = WorldFixture.privateWorld().build();

        List<CreateWorldLorebookEntry> lorebookEntries = world.getLorebook()
                .stream()
                .map(entry -> CreateWorldLorebookEntry.builder()
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
                .requesterDiscordId(world.getOwnerDiscordId())
                .usersAllowedToWrite(world.getUsersAllowedToWrite())
                .usersAllowedToRead(world.getUsersAllowedToRead())
                .lorebookEntries(lorebookEntries);
    }
}
