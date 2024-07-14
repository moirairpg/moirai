package me.moirai.discordbot.core.application.usecase.channelconfig;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.CreateChannelConfig;
import me.moirai.discordbot.core.application.usecase.channelconfig.result.CreateChannelConfigResult;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigService;

@UseCaseHandler
public class CreateChannelConfigHandler extends AbstractUseCaseHandler<CreateChannelConfig, CreateChannelConfigResult> {

    private final ChannelConfigService domainService;

    public CreateChannelConfigHandler(ChannelConfigService domainService) {
        this.domainService = domainService;
    }

    @Override
    public CreateChannelConfigResult execute(CreateChannelConfig command) {

        ChannelConfig channelconfig = domainService.createFrom(command);
        return CreateChannelConfigResult.build(channelconfig.getId());
    }
}
