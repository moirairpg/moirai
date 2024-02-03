package es.thalesalv.chatrpg.infrastructure.outbound.repository.channelconfig;

import org.springframework.stereotype.Repository;

import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfig;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ChannelConfigRepositoryImpl implements ChannelConfigRepository {

    @Override
    public ChannelConfig save(ChannelConfig channelConfig) {

        return channelConfig;
    }
}
