package es.thalesalv.chatrpg.core.application.usecase.channelconfig;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.request.CreateChannelConfig;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.result.CreateChannelConfigResult;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfig;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigService;

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
