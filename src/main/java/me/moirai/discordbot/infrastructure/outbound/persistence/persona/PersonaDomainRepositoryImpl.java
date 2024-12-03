package me.moirai.discordbot.infrastructure.outbound.persistence.persona;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import me.moirai.discordbot.core.domain.persona.Persona;
import me.moirai.discordbot.core.domain.persona.PersonaDomainRepository;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;
import me.moirai.discordbot.infrastructure.outbound.persistence.mapper.PersonaPersistenceMapper;

@Repository
public class PersonaDomainRepositoryImpl implements PersonaDomainRepository {

    private final PersonaJpaRepository jpaRepository;
    private final PersonaPersistenceMapper mapper;
    private final FavoriteRepository favoriteRepository;

    public PersonaDomainRepositoryImpl(
            PersonaJpaRepository jpaRepository,
            PersonaPersistenceMapper mapper,
            FavoriteRepository favoriteRepository) {

        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public Persona save(Persona persona) {

        PersonaEntity entity = mapper.mapToEntity(persona);

        return mapper.mapFromEntity(jpaRepository.save(entity));
    }

    @Override
    public Optional<Persona> findById(String id) {

        return jpaRepository.findById(id)
                .map(mapper::mapFromEntity);
    }

    @Override
    public void deleteById(String id) {

        favoriteRepository.deleteAllByAssetId(id);

        jpaRepository.deleteById(id);
    }
}
