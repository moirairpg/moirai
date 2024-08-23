package me.moirai.discordbot.core.domain.channelconfig;

import me.moirai.discordbot.core.application.usecase.channelconfig.request.CreateChannelConfig;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.DeleteChannelConfig;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.UpdateChannelConfig;

public interface ChannelConfigService {

    ChannelConfig createFrom(CreateChannelConfig command);

    ChannelConfig update(UpdateChannelConfig command);

    ChannelConfig getById(String channelConfigId);

    void delete(DeleteChannelConfig command);
}
