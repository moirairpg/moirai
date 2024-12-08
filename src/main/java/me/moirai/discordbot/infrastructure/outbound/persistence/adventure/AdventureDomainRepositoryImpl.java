package me.moirai.discordbot.infrastructure.outbound.persistence.adventure;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.adventure.AdventureDomainRepository;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;
import me.moirai.discordbot.infrastructure.outbound.persistence.mapper.AdventurePersistenceMapper;

@Repository
public class AdventureDomainRepositoryImpl implements AdventureDomainRepository {

    private final AdventureJpaRepository jpaRepository;
    private final AdventureLorebookEntryJpaRepository lorebookEntryJpaRepository;
    private final AdventurePersistenceMapper mapper;
    private final FavoriteRepository favoriteRepository;

    public AdventureDomainRepositoryImpl(
            AdventureJpaRepository jpaRepository,
            AdventureLorebookEntryJpaRepository adventureLorebookEntryJpaRepository,
            AdventurePersistenceMapper mapper,
            FavoriteRepository favoriteRepository) {

        this.jpaRepository = jpaRepository;
        this.lorebookEntryJpaRepository = adventureLorebookEntryJpaRepository;
        this.mapper = mapper;
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public Adventure save(Adventure adventure) {

        AdventureEntity entity = mapper.mapToEntity(adventure);

        return mapper.mapFromEntity(jpaRepository.save(entity));
    }

    @Override
    public Optional<Adventure> findById(String id) {

        return jpaRepository.findById(id)
                .map(mapper::mapFromEntity);
    }

    @Override
    public void deleteById(String id) {

        lorebookEntryJpaRepository.deleteAllByAdventureId(id);
        favoriteRepository.deleteAllByAssetId(id);
        jpaRepository.deleteById(id);
    }
}
