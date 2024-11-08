package me.moirai.discordbot.infrastructure.outbound.persistence.world;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.core.domain.world.WorldDomainRepository;
import me.moirai.discordbot.infrastructure.outbound.persistence.mapper.WorldPersistenceMapper;

@Repository
public class WorldDomainRepositoryImpl implements WorldDomainRepository {

    private final WorldJpaRepository jpaRepository;
    private final WorldPersistenceMapper mapper;

    public WorldDomainRepositoryImpl(WorldJpaRepository jpaRepository,
            WorldPersistenceMapper mapper) {

        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public World save(World world) {

        WorldEntity entity = mapper.mapToEntity(world);

        return mapper.mapFromEntity(jpaRepository.save(entity));
    }

    @Override
    public Optional<World> findById(String id) {

        return jpaRepository.findById(id)
                .map(mapper::mapFromEntity);
    }

    @Override
    public void deleteById(String id) {

        jpaRepository.deleteById(id);
    }
}
