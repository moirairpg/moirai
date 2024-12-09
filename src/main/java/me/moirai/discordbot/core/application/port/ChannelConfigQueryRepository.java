package me.moirai.discordbot.core.application.port;

import java.util.Optional;

import me.moirai.discordbot.core.application.usecase.channelconfig.request.SearchChannelConfigsWithReadAccess;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.SearchChannelConfigsWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.SearchFavoriteChannelConfigs;
import me.moirai.discordbot.core.application.usecase.channelconfig.result.SearchChannelConfigsResult;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;

public interface ChannelConfigQueryRepository {

    Optional<ChannelConfig> findById(String id);

    SearchChannelConfigsResult search(SearchChannelConfigsWithReadAccess request);

    SearchChannelConfigsResult search(SearchChannelConfigsWithWriteAccess request);

    SearchChannelConfigsResult search(SearchFavoriteChannelConfigs request);

    Optional<ChannelConfig> findByDiscordChannelId(String channelId);

    String getGameModeByDiscordChannelId(String discordChannelId);
}
