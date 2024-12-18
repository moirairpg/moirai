package me.moirai.discordbot.core.application.usecase.adventure.request;

import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.adventure.AdventureFixture;

public class CreateAdventureFixture {

    public static CreateAdventure.Builder sample() {

        Adventure adventure = AdventureFixture.privateMultiplayerAdventure().build();
        return CreateAdventure.builder()
                .name(adventure.getName())
                .personaId(adventure.getPersonaId())
                .worldId(adventure.getWorldId())
                .discordChannelId(adventure.getDiscordChannelId())
                .aiModel(adventure.getModelConfiguration().getAiModel().toString())
                .logitBias(adventure.getModelConfiguration().getLogitBias())
                .stopSequences(adventure.getModelConfiguration().getStopSequences())
                .frequencyPenalty(adventure.getModelConfiguration().getFrequencyPenalty())
                .presencePenalty(adventure.getModelConfiguration().getPresencePenalty())
                .maxTokenLimit(adventure.getModelConfiguration().getMaxTokenLimit())
                .moderation("strict")
                .usersAllowedToRead(adventure.getUsersAllowedToRead())
                .usersAllowedToWrite(adventure.getUsersAllowedToWrite())
                .temperature(1.7)
                .gameMode(adventure.getGameMode().name())
                .description(adventure.getDescription())
                .nudge(adventure.getContextAttributes().getNudge())
                .authorsNote(adventure.getContextAttributes().getAuthorsNote())
                .remember(adventure.getContextAttributes().getRemember())
                .bump(adventure.getContextAttributes().getBump())
                .bumpFrequency(adventure.getContextAttributes().getBumpFrequency())
                .isMultiplayer(adventure.isMultiplayer())
                .visibility("private")
                .requesterDiscordId("RQSTRID");
    }
}
