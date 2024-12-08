package me.moirai.discordbot.infrastructure.outbound.persistence.adventure;

import static me.moirai.discordbot.infrastructure.outbound.persistence.SearchPredicates.contains;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.data.domain.Sort.Direction.ASC;

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

import jakarta.persistence.criteria.Predicate;
import me.moirai.discordbot.core.application.usecase.adventure.request.SearchAdventureLorebookEntries;
import me.moirai.discordbot.core.application.usecase.adventure.result.SearchAdventureLorebookEntriesResult;
import me.moirai.discordbot.core.domain.adventure.AdventureLorebookEntry;
import me.moirai.discordbot.core.domain.adventure.AdventureLorebookEntryRepository;
import me.moirai.discordbot.infrastructure.outbound.persistence.mapper.AdventureLorebookPersistenceMapper;

@Repository
public class AdventureLorebookEntryRepositoryImpl implements AdventureLorebookEntryRepository {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_ITEMS = 10;

    private static final String NAME = "name";
    private static final String DEFAULT_SORT_BY_FIELD = NAME;

    private final AdventureLorebookEntryJpaRepository jpaRepository;
    private final AdventureLorebookPersistenceMapper mapper;

    public AdventureLorebookEntryRepositoryImpl(AdventureLorebookEntryJpaRepository jpaRepository,
            AdventureLorebookPersistenceMapper mapper) {

        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public AdventureLorebookEntry save(AdventureLorebookEntry adventure) {

        AdventureLorebookEntryEntity entity = mapper.mapToEntity(adventure);

        return mapper.mapFromEntity(jpaRepository.save(entity));
    }

    @Override
    public Optional<AdventureLorebookEntry> findById(String lorebookEntryId) {

        return jpaRepository.findById(lorebookEntryId)
                .map(mapper::mapFromEntity);
    }

    @Override
    public List<AdventureLorebookEntry> findAllByRegex(String valueToSearch, String adventureId) {

        return jpaRepository.findAllByNameRegex(valueToSearch, adventureId)
                .stream()
                .map(mapper::mapFromEntity)
                .toList();
    }

    @Override
    public Optional<AdventureLorebookEntry> findByPlayerDiscordId(String playerDiscordId, String adventureId) {

        return jpaRepository.findByPlayerDiscordId(playerDiscordId, adventureId)
                .map(mapper::mapFromEntity);
    }

    @Override
    public void deleteById(String id) {

        jpaRepository.deleteById(id);
    }

    @Override
    public SearchAdventureLorebookEntriesResult searchBy(SearchAdventureLorebookEntries query) {

        int page = query.getPage() == null ? DEFAULT_PAGE : query.getPage() - 1;
        int items = query.getItems() == null ? DEFAULT_ITEMS : query.getItems();
        String sortByField = isBlank(query.getSortByField()) ? DEFAULT_SORT_BY_FIELD : query.getSortByField();
        Direction direction = isBlank(query.getDirection()) ? ASC : Direction.fromString(query.getDirection());

        PageRequest pageRequest = PageRequest.of(page, items, Sort.by(direction, sortByField));
        Specification<AdventureLorebookEntryEntity> filters = buildSearchQuery(query);
        Page<AdventureLorebookEntryEntity> pagedResult = jpaRepository.findAll(filters, pageRequest);

        return mapper.mapToResult(pagedResult);
    }

    private Specification<AdventureLorebookEntryEntity> buildSearchQuery(SearchAdventureLorebookEntries query) {

        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("adventureId"), query.getAdventureId()));

            if (StringUtils.isNotBlank(query.getName())) {
                predicates.add(contains(cb, root, NAME, query.getName()));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
