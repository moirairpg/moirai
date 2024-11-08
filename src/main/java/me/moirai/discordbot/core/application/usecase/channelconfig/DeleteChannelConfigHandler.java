package me.moirai.discordbot.core.application.usecase.channelconfig;

import org.apache.commons.lang3.StringUtils;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.DeleteChannelConfig;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigService;

@UseCaseHandler
public class DeleteChannelConfigHandler extends AbstractUseCaseHandler<DeleteChannelConfig, Void> {

    private static final String ID_CANNOT_BE_NULL_OR_EMPTY = "Channel config ID cannot be null or empty";

    private final ChannelConfigService domainService;

    public DeleteChannelConfigHandler(ChannelConfigService domainService) {
        this.domainService = domainService;
    }

    @Override
    public void validate(DeleteChannelConfig command) {

        if (StringUtils.isBlank(command.getId())) {
            throw new IllegalArgumentException(ID_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    @Override
    public Void execute(DeleteChannelConfig command) {

        domainService.delete(command);

        return null;
    }
}
