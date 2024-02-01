package es.thalesalv.chatrpg.core.domain.channelconfig;

import es.thalesalv.chatrpg.core.application.command.channelconfig.CreateChannelConfig;

public interface ChannelConfigDomainService {

    ChannelConfig createFrom(CreateChannelConfig command);
}
