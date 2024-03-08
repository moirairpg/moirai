package es.thalesalv.chatrpg.core.domain.channelconfig;

import es.thalesalv.chatrpg.core.application.command.channelconfig.CreateChannelConfig;
import es.thalesalv.chatrpg.core.application.command.channelconfig.UpdateChannelConfig;

public interface ChannelConfigDomainService {

    ChannelConfig createFrom(CreateChannelConfig command);

    ChannelConfig update(UpdateChannelConfig command);
}
