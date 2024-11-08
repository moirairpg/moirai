package me.moirai.discordbot.infrastructure.outbound.persistence.channelconfig;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigDomainRepository;
import me.moirai.discordbot.infrastructure.outbound.persistence.mapper.ChannelConfigPersistenceMapper;

@Repository
public class ChannelConfigDomainRepositoryImpl implements ChannelConfigDomainRepository {

    private final ChannelConfigJpaRepository jpaRepository;
    private final ChannelConfigPersistenceMapper mapper;

    public ChannelConfigDomainRepositoryImpl(ChannelConfigJpaRepository jpaRepository,
            ChannelConfigPersistenceMapper mapper) {

        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public ChannelConfig save(ChannelConfig channelConfig) {

        ChannelConfigEntity entity = mapper.mapToEntity(channelConfig);

        return mapper.mapFromEntity(jpaRepository.save(entity));
    }

    @Override
    public Optional<ChannelConfig> findById(String id) {

        return jpaRepository.findById(id)
                .map(mapper::mapFromEntity);
    }

    @Override
    public void deleteById(String id) {

        jpaRepository.deleteById(id);
    }
}
