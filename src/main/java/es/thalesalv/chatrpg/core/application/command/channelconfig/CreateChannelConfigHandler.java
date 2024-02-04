package es.thalesalv.chatrpg.core.application.command.channelconfig;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.cqrs.command.CommandHandler;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfig;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigDomainService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateChannelConfigHandler extends CommandHandler<CreateChannelConfig, CreateChannelConfigResult> {

    private final ChannelConfigDomainService domainService;

    @Override
    public CreateChannelConfigResult handle(CreateChannelConfig command) {

        ChannelConfig channelconfig = domainService.createFrom(command);
        return CreateChannelConfigResult.with(channelconfig.getId());
    }
}
