package es.thalesalv.chatrpg.core.application.command.channelconfig;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.cqrs.command.CommandHandler;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfig;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigDomainService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateChannelConfigHandler extends CommandHandler<CreateChannelConfig, CreateChannelConfigResult> {

    private final ChannelConfigDomainService domainService;

    @Override
    public CreateChannelConfigResult handle(CreateChannelConfig command) {

        ChannelConfig channelconfig = domainService.createFrom(command);
        return CreateChannelConfigResult.build(channelconfig.getId());
    }
}
