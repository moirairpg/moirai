package es.thalesalv.chatrpg.infrastructure.outbound.repository.channelconfig;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import es.thalesalv.chatrpg.core.application.query.channelconfig.SearchChannelConfigs;
import es.thalesalv.chatrpg.core.application.query.channelconfig.SearchChannelConfigsResult;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfig;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ChannelConfigRepositoryImpl implements ChannelConfigRepository {

    @Override
    public ChannelConfig save(ChannelConfig channelConfig) {

        return channelConfig;
    }

    @Override
    public Optional<ChannelConfig> findById(String id) {

        return Optional.empty();
    }

    @Override
    public void deleteById(String id) {

    }

    @Override
    public SearchChannelConfigsResult searchChannelConfigs(SearchChannelConfigs query) {

        return null;
    }
}
