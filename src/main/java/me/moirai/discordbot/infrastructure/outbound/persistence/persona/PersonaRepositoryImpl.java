package me.moirai.discordbot.infrastructure.outbound.persistence.persona;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import me.moirai.discordbot.core.application.usecase.persona.request.SearchPersonasWithReadAccess;
import me.moirai.discordbot.core.application.usecase.persona.request.SearchPersonasWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.persona.result.SearchPersonasResult;
import me.moirai.discordbot.core.domain.persona.Persona;
import me.moirai.discordbot.core.domain.persona.PersonaRepository;
import me.moirai.discordbot.infrastructure.outbound.persistence.mapper.PersonaPersistenceMapper;
import jakarta.persistence.criteria.Predicate;

@Repository
public class PersonaRepositoryImpl implements PersonaRepository {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_ITEMS = 10;
    private static final String DEFAULT_SORT_BY_FIELD = "name";

    private final PersonaJpaRepository jpaRepository;
    private final PersonaPersistenceMapper mapper;

    public PersonaRepositoryImpl(PersonaJpaRepository jpaRepository, PersonaPersistenceMapper mapper) {

        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
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

        jpaRepository.deleteById(id);
    }

    @Override
    public SearchPersonasResult searchPersonasWithReadAccess(SearchPersonasWithReadAccess query) {

        int page = query.getPage() == null ? DEFAULT_PAGE : query.getPage() - 1;
        int items = query.getItems() == null ? DEFAULT_ITEMS : query.getItems();
        String sortByField = isBlank(query.getSortByField()) ? DEFAULT_SORT_BY_FIELD : query.getSortByField();
        Direction direction = isBlank(query.getDirection()) ? Direction.ASC
                : Direction.fromString(query.getDirection());

        PageRequest pageRequest = PageRequest.of(page, items, Sort.by(direction, sortByField));
        Specification<PersonaEntity> filters = readAccessSpecificationFrom(query);
        Page<PersonaEntity> pagedResult = jpaRepository.findAll(filters, pageRequest);

        return mapper.mapToResult(pagedResult);
    }

    @Override
    public SearchPersonasResult searchPersonasWithWriteAccess(SearchPersonasWithWriteAccess query) {

        int page = query.getPage() == null ? DEFAULT_PAGE : query.getPage() - 1;
        int items = query.getItems() == null ? DEFAULT_ITEMS : query.getItems();
        String sortByField = isBlank(query.getSortByField()) ? DEFAULT_SORT_BY_FIELD : query.getSortByField();
        Direction direction = isBlank(query.getDirection()) ? Direction.ASC
                : Direction.fromString(query.getDirection());

        PageRequest pageRequest = PageRequest.of(page, items, Sort.by(direction, sortByField));
        Specification<PersonaEntity> filters = writeAccessSpecificationFrom(query);
        Page<PersonaEntity> pagedResult = jpaRepository.findAll(filters, pageRequest);

        return mapper.mapToResult(pagedResult);
    }

    private Specification<PersonaEntity> readAccessSpecificationFrom(SearchPersonasWithReadAccess query) {

        return (root, cq, cb) -> {
            final List<Predicate> predicates = new ArrayList<>();

            Predicate isOwner = cb.equal(root.get("ownerDiscordId"), query.getRequesterDiscordId());
            Predicate isAllowedToRead = cb.like(root.get("usersAllowedToReadString"),
                    "%" + query.getRequesterDiscordId() + "%");

            Predicate isAllowedToWrite = cb.like(root.get("usersAllowedToWriteString"),
                    "%" + query.getRequesterDiscordId() + "%");

            predicates.add(cb.or(isOwner, isAllowedToRead, isAllowedToWrite));

            if (StringUtils.isNotBlank(query.getName())) {
                predicates.add(cb.like(cb.upper(root.get("name")),
                        "%" + query.getName().toUpperCase() + "%"));
            }

            if (StringUtils.isNotBlank(query.getGameMode())) {
                predicates.add(cb.like(cb.upper(root.get("gameMode")),
                        "%" + query.getGameMode().toUpperCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    private Specification<PersonaEntity> writeAccessSpecificationFrom(SearchPersonasWithWriteAccess query) {

        return (root, cq, cb) -> {
            final List<Predicate> predicates = new ArrayList<>();

            Predicate isOwner = cb.equal(root.get("ownerDiscordId"), query.getRequesterDiscordId());
            Predicate isAllowedToWrite = cb.like(root.get("usersAllowedToWriteString"),
                    "%" + query.getRequesterDiscordId() + "%");

            predicates.add(cb.or(isOwner, isAllowedToWrite));

            if (StringUtils.isNotBlank(query.getName())) {
                predicates.add(cb.like(cb.upper(root.get("name")),
                        "%" + query.getName().toUpperCase() + "%"));
            }

            if (StringUtils.isNotBlank(query.getGameMode())) {
                predicates.add(cb.like(cb.upper(root.get("gameMode")),
                        "%" + query.getGameMode().toUpperCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
