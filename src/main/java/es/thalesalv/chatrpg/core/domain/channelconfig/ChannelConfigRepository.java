package es.thalesalv.chatrpg.core.domain.channelconfig;

import java.util.Optional;

import es.thalesalv.chatrpg.core.application.query.channelconfig.SearchChannelConfigsResult;
import es.thalesalv.chatrpg.core.application.query.channelconfig.SearchChannelConfigsWithReadAccess;
import es.thalesalv.chatrpg.core.application.query.channelconfig.SearchChannelConfigsWithWriteAccess;

public interface ChannelConfigRepository {

    Optional<ChannelConfig> findById(String id);

    ChannelConfig save(ChannelConfig channelConfig);

    void deleteById(String id);

    SearchChannelConfigsResult searchChannelConfigsWithReadAccess(SearchChannelConfigsWithReadAccess query);

    SearchChannelConfigsResult searchChannelConfigsWithWriteAccess(SearchChannelConfigsWithWriteAccess query);

    Optional<ChannelConfig> findByDiscordChannelId(String channelId);
}
