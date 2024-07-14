package me.moirai.discordbot.core.domain.channelconfig;

import java.util.Optional;

import me.moirai.discordbot.core.application.usecase.channelconfig.request.SearchChannelConfigsWithReadAccess;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.SearchChannelConfigsWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.channelconfig.result.SearchChannelConfigsResult;

public interface ChannelConfigRepository {

    Optional<ChannelConfig> findById(String id);

    ChannelConfig save(ChannelConfig channelConfig);

    void deleteById(String id);

    SearchChannelConfigsResult searchChannelConfigsWithReadAccess(SearchChannelConfigsWithReadAccess query);

    SearchChannelConfigsResult searchChannelConfigsWithWriteAccess(SearchChannelConfigsWithWriteAccess query);

    Optional<ChannelConfig> findByDiscordChannelId(String channelId);
}
