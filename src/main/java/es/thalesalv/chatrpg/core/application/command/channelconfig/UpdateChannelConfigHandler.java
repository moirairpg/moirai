package es.thalesalv.chatrpg.core.application.command.channelconfig;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfig;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigService;
import lombok.RequiredArgsConstructor;

@UseCaseHandler
@RequiredArgsConstructor
public class UpdateChannelConfigHandler extends AbstractUseCaseHandler<UpdateChannelConfig, UpdateChannelConfigResult> {

    private final ChannelConfigService service;

    @Override
    public UpdateChannelConfigResult execute(UpdateChannelConfig command) {

        return mapResult(service.update(command));
    }

    private UpdateChannelConfigResult mapResult(ChannelConfig savedChannelConfig) {

        return UpdateChannelConfigResult.build(savedChannelConfig.getLastUpdateDate());
    }
}
