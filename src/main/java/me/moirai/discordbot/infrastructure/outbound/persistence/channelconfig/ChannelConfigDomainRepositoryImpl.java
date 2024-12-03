package me.moirai.discordbot.infrastructure.outbound.persistence.channelconfig;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigDomainRepository;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;
import me.moirai.discordbot.infrastructure.outbound.persistence.mapper.ChannelConfigPersistenceMapper;

@Repository
public class ChannelConfigDomainRepositoryImpl implements ChannelConfigDomainRepository {

    private final ChannelConfigJpaRepository jpaRepository;
    private final ChannelConfigPersistenceMapper mapper;
    private final FavoriteRepository favoriteRepository;

    public ChannelConfigDomainRepositoryImpl(
            ChannelConfigJpaRepository jpaRepository,
            ChannelConfigPersistenceMapper mapper,
            FavoriteRepository favoriteRepository) {

        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
        this.favoriteRepository = favoriteRepository;
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

        favoriteRepository.deleteAllByAssetId(id);

        jpaRepository.deleteById(id);
    }
}
