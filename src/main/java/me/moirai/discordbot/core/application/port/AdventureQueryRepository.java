package me.moirai.discordbot.core.application.port;

import java.util.Optional;

import me.moirai.discordbot.core.application.usecase.adventure.request.SearchAdventures;
import me.moirai.discordbot.core.application.usecase.adventure.result.SearchAdventuresResult;
import me.moirai.discordbot.core.domain.adventure.Adventure;

public interface AdventureQueryRepository {

    Optional<Adventure> findById(String id);

    SearchAdventuresResult search(SearchAdventures request);

    Optional<Adventure> findByDiscordChannelId(String channelId);

    String getGameModeByDiscordChannelId(String discordChannelId);
}
