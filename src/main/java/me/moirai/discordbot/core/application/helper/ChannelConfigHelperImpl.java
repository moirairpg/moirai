package me.moirai.discordbot.core.application.helper;

import me.moirai.discordbot.common.annotation.Helper;
import me.moirai.discordbot.core.application.port.ChannelConfigQueryRepository;

@Helper
public class ChannelConfigHelperImpl implements ChannelConfigHelper {

    private final ChannelConfigQueryRepository channelConfigQueryRepository;

    public ChannelConfigHelperImpl(ChannelConfigQueryRepository channelConfigQueryRepository) {
        this.channelConfigQueryRepository = channelConfigQueryRepository;
    }

    @Override
    public String getGameModeByDiscordChannelId(String channelId) {

        return channelConfigQueryRepository.getGameModeByDiscordChannelId(channelId);
    }
}
