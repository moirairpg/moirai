package me.moirai.discordbot.core.application.usecase.channelconfig;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.UpdateChannelConfig;
import me.moirai.discordbot.core.application.usecase.channelconfig.result.UpdateChannelConfigResult;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigService;

@UseCaseHandler
public class UpdateChannelConfigHandler extends AbstractUseCaseHandler<UpdateChannelConfig, UpdateChannelConfigResult> {

    private final ChannelConfigService service;

    public UpdateChannelConfigHandler(ChannelConfigService service) {
        this.service = service;
    }

    @Override
    public UpdateChannelConfigResult execute(UpdateChannelConfig command) {

        return mapResult(service.update(command));
    }

    private UpdateChannelConfigResult mapResult(ChannelConfig savedChannelConfig) {

        return UpdateChannelConfigResult.build(savedChannelConfig.getLastUpdateDate());
    }
}
