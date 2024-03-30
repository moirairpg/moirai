package es.thalesalv.chatrpg.core.domain.channelconfig;

import es.thalesalv.chatrpg.core.application.command.channelconfig.CreateChannelConfig;
import es.thalesalv.chatrpg.core.application.command.channelconfig.DeleteChannelConfig;
import es.thalesalv.chatrpg.core.application.command.channelconfig.UpdateChannelConfig;
import es.thalesalv.chatrpg.core.application.query.channelconfig.GetChannelConfigById;

public interface ChannelConfigDomainService {

    ChannelConfig createFrom(CreateChannelConfig command);

    ChannelConfig update(UpdateChannelConfig command);

    ChannelConfig getChannelConfigById(GetChannelConfigById query);

    void deleteChannelConfig(DeleteChannelConfig command);
}
