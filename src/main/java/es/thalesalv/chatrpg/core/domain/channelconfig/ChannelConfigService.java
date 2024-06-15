package es.thalesalv.chatrpg.core.domain.channelconfig;

import es.thalesalv.chatrpg.core.application.usecase.channelconfig.request.CreateChannelConfig;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.request.DeleteChannelConfig;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.request.GetChannelConfigById;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.request.UpdateChannelConfig;

public interface ChannelConfigService {

    ChannelConfig createFrom(CreateChannelConfig command);

    ChannelConfig update(UpdateChannelConfig command);

    ChannelConfig getChannelConfigById(GetChannelConfigById query);

    void deleteChannelConfig(DeleteChannelConfig command);
}
