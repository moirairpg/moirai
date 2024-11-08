package me.moirai.discordbot.core.application.port;

import java.util.Optional;

import me.moirai.discordbot.core.application.usecase.channelconfig.request.SearchChannelConfigsWithReadAccess;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.SearchChannelConfigsWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.channelconfig.result.SearchChannelConfigsResult;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;

public interface ChannelConfigQueryRepository {

    Optional<ChannelConfig> findById(String id);

    SearchChannelConfigsResult searchChannelConfigsWithReadAccess(SearchChannelConfigsWithReadAccess query);

    SearchChannelConfigsResult searchChannelConfigsWithWriteAccess(SearchChannelConfigsWithWriteAccess query);

    Optional<ChannelConfig> findByDiscordChannelId(String channelId);

    String getGameModeByDiscordChannelId(String discordChannelId);
}
