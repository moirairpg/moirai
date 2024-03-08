package es.thalesalv.chatrpg.core.application.command.channelconfig;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.usecases.UseCaseHandler;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfig;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigDomainService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateChannelConfigHandler extends UseCaseHandler<CreateChannelConfig, CreateChannelConfigResult> {

    private final ChannelConfigDomainService domainService;

    @Override
    public CreateChannelConfigResult execute(CreateChannelConfig command) {

        ChannelConfig channelconfig = domainService.createFrom(command);
        return CreateChannelConfigResult.build(channelconfig.getId());
    }
}
