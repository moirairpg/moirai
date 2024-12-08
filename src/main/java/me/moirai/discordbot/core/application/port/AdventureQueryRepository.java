package me.moirai.discordbot.core.application.port;

import java.util.Optional;

import me.moirai.discordbot.core.application.usecase.adventure.request.SearchAdventuresWithReadAccess;
import me.moirai.discordbot.core.application.usecase.adventure.request.SearchAdventuresWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.adventure.request.SearchFavoriteAdventures;
import me.moirai.discordbot.core.application.usecase.adventure.result.SearchAdventuresResult;
import me.moirai.discordbot.core.domain.adventure.Adventure;

public interface AdventureQueryRepository {

    Optional<Adventure> findById(String id);

    SearchAdventuresResult search(SearchAdventuresWithReadAccess request);

    SearchAdventuresResult search(SearchAdventuresWithWriteAccess request);

    SearchAdventuresResult search(SearchFavoriteAdventures request);

    Optional<Adventure> findByDiscordChannelId(String channelId);

    String getGameModeByDiscordChannelId(String discordChannelId);
}
