package me.moirai.discordbot.infrastructure.outbound.persistence.adventure;

import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.adventure.AdventureFixture;

public class AdventureEntityFixture {

    public static AdventureEntity.Builder sample() {

        Adventure adventure = AdventureFixture.privateMultiplayerAdventure().build();

        return AdventureEntity.builder()
                .id(adventure.getId())
                .name(adventure.getName())
                .worldId(adventure.getWorldId())
                .personaId(adventure.getPersonaId())
                .gameMode(adventure.getGameMode().name())
                .discordChannelId(adventure.getDiscordChannelId())
                .moderation(adventure.getModeration().toString())
                .visibility(adventure.getVisibility().toString())
                .usersAllowedToRead(adventure.getUsersAllowedToRead())
                .usersAllowedToWrite(adventure.getUsersAllowedToWrite())
                .ownerDiscordId(adventure.getOwnerDiscordId())
                .creatorDiscordId(adventure.getCreatorDiscordId())
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .description(adventure.getDescription())
                .contextAttributes(ContextAttributesEntityFixture.sample().build())
                .adventureStart(adventure.getAdventureStart());
    }
}
