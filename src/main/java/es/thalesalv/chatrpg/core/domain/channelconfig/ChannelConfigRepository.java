package es.thalesalv.chatrpg.core.domain.channelconfig;

import java.util.Optional;

import es.thalesalv.chatrpg.core.application.query.channelconfig.SearchChannelConfigs;
import es.thalesalv.chatrpg.core.application.query.channelconfig.SearchChannelConfigsResult;

public interface ChannelConfigRepository {

    Optional<ChannelConfig> findById(String id);

    ChannelConfig save(ChannelConfig channelConfig);

    void deleteById(String id);

    SearchChannelConfigsResult searchChannelConfigs(SearchChannelConfigs query, String requesterDiscordId);
}
