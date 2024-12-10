package me.moirai.discordbot.infrastructure.outbound.persistence.adventure;

import static me.moirai.discordbot.infrastructure.outbound.persistence.SearchPredicates.canUserRead;
import static me.moirai.discordbot.infrastructure.outbound.persistence.SearchPredicates.canUserWrite;
import static me.moirai.discordbot.infrastructure.outbound.persistence.SearchPredicates.contains;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.data.domain.Sort.Direction.ASC;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import me.moirai.discordbot.core.application.port.AdventureQueryRepository;
import me.moirai.discordbot.core.application.usecase.adventure.request.SearchAdventuresWithReadAccess;
import me.moirai.discordbot.core.application.usecase.adventure.request.SearchAdventuresWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.adventure.request.SearchFavoriteAdventures;
import me.moirai.discordbot.core.application.usecase.adventure.result.SearchAdventuresResult;
import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteEntity;
import me.moirai.discordbot.infrastructure.outbound.persistence.mapper.AdventurePersistenceMapper;

@Repository
public class AdventureQueryRepositoryImpl implements AdventureQueryRepository {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_ITEMS = 10;

    private static final String ID = "id";
    private static final String ASSET_ID = "assetId";
    private static final String ASSET_TYPE = "assetType";
    private static final String ADVENTURE = "adventure";
    private static final String NAME = "name";
    private static final String GAME_MODE = "gameMode";
    private static final String VISIBILITY = "visibility";
    private static final String PERSONA_ID = "personaId";
    private static final String WORLD_ID = "worldId";
    private static final String MODERATION = "moderation";
    private static final String MODEL_CONFIGURATION = "modelConfiguration";
    private static final String DEFAULT_SORT_BY_FIELD = "creationDate";
    private static final String AI_MODEL = "aiModel";

    private final AdventureJpaRepository jpaRepository;
    private final AdventurePersistenceMapper mapper;

    public AdventureQueryRepositoryImpl(AdventureJpaRepository jpaRepository,
            AdventurePersistenceMapper mapper) {

        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Adventure> findById(String id) {

        return jpaRepository.findById(id)
                .map(mapper::mapFromEntity);
    }

    @Override
    public Optional<Adventure> findByDiscordChannelId(String channelId) {

        return jpaRepository.findByDiscordChannelId(channelId)
                .map(mapper::mapFromEntity);
    }

    @Override
    public SearchAdventuresResult search(SearchAdventuresWithReadAccess request) {

        int page = extractPageNumber(request.getPage());
        int size = extractPageSize(request.getItems());
        String sortByField = extractSortByField(request.getSortByField());
        Direction direction = extractDirection(request.getDirection());

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortByField));
        Specification<AdventureEntity> query = buildSearchQuery(request);
        Page<AdventureEntity> pagedResult = jpaRepository.findAll(query, pageRequest);

        return mapper.mapToResult(pagedResult);
    }

    @Override
    public SearchAdventuresResult search(SearchAdventuresWithWriteAccess request) {

        int page = extractPageNumber(request.getPage());
        int size = extractPageSize(request.getItems());
        String sortByField = extractSortByField(request.getSortByField());
        Direction direction = extractDirection(request.getDirection());

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortByField));
        Specification<AdventureEntity> query = buildSearchQuery(request);
        Page<AdventureEntity> pagedResult = jpaRepository.findAll(query, pageRequest);

        return mapper.mapToResult(pagedResult);
    }

    @Override
    public SearchAdventuresResult search(SearchFavoriteAdventures request) {

        int page = extractPageNumber(request.getPage());
        int size = extractPageSize(request.getItems());
        String sortByField = extractSortByField(request.getSortByField());
        Direction direction = extractDirection(request.getDirection());

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortByField));
        Specification<AdventureEntity> query = buildSearchQuery(request);
        Page<AdventureEntity> pagedResult = jpaRepository.findAll(query, pageRequest);

        return mapper.mapToResult(pagedResult);
    }

    @Override
    public String getGameModeByDiscordChannelId(String discordChannelId) {

        return jpaRepository.getGameModeByDiscordChannelId(discordChannelId);
    }

    private Specification<AdventureEntity> buildSearchQuery(SearchAdventuresWithReadAccess query) {

        return (root, cq, cb) -> {
            final List<Predicate> predicates = new ArrayList<>();

            predicates.add(canUserRead(cb, root, query.getRequesterDiscordId()));

            if (isNotBlank(query.getAiModel())) {
                predicates.add(cb.like(cb.upper(root.get(MODEL_CONFIGURATION)
                        .get(AI_MODEL)), cb.literal(query.getAiModel().toUpperCase())));
            }

            if (isNotBlank(query.getModeration())) {
                predicates.add(contains(cb, root, MODERATION, query.getModeration()));
            }

            if (isNotBlank(query.getName())) {
                predicates.add(contains(cb, root, NAME, query.getName()));
            }

            if (isNotBlank(query.getGameMode())) {
                predicates.add(contains(cb, root, GAME_MODE, query.getGameMode()));
            }

            if (isNotBlank(query.getVisibility())) {
                predicates.add(contains(cb, root, VISIBILITY, query.getVisibility()));
            }

            if (isNotBlank(query.getWorldId())) {
                predicates.add(contains(cb, root, WORLD_ID, query.getWorldId()));
            }

            if (isNotBlank(query.getPersonaId())) {
                predicates.add(contains(cb, root, PERSONA_ID, query.getPersonaId()));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    private Specification<AdventureEntity> buildSearchQuery(SearchAdventuresWithWriteAccess query) {

        return (root, cq, cb) -> {
            final List<Predicate> predicates = new ArrayList<>();

            predicates.add(canUserWrite(cb, root, query.getRequesterDiscordId()));

            if (isNotBlank(query.getAiModel())) {
                predicates.add(cb.like(cb.upper(root.get(MODEL_CONFIGURATION)
                        .get(AI_MODEL)), cb.literal(query.getAiModel().toUpperCase())));
            }

            if (isNotBlank(query.getModeration())) {
                predicates.add(contains(cb, root, MODERATION, query.getModeration()));
            }

            if (isNotBlank(query.getName())) {
                predicates.add(contains(cb, root, NAME, query.getName()));
            }

            if (isNotBlank(query.getGameMode())) {
                predicates.add(contains(cb, root, GAME_MODE, query.getGameMode()));
            }

            if (isNotBlank(query.getVisibility())) {
                predicates.add(contains(cb, root, VISIBILITY, query.getVisibility()));
            }

            if (isNotBlank(query.getWorldId())) {
                predicates.add(contains(cb, root, WORLD_ID, query.getWorldId()));
            }

            if (isNotBlank(query.getPersonaId())) {
                predicates.add(contains(cb, root, PERSONA_ID, query.getPersonaId()));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    private Specification<AdventureEntity> buildSearchQuery(SearchFavoriteAdventures query) {

        return (root, cq, cb) -> {
            final List<Predicate> predicates = new ArrayList<>();

            predicates.add(canUserWrite(cb, root, query.getRequesterDiscordId()));

            if (isNotBlank(query.getAiModel())) {
                predicates.add(cb.like(cb.upper(root.get(MODEL_CONFIGURATION)
                        .get(AI_MODEL)), cb.literal(query.getAiModel().toUpperCase())));
            }

            if (isNotBlank(query.getModeration())) {
                predicates.add(contains(cb, root, MODERATION, query.getModeration()));
            }

            if (isNotBlank(query.getName())) {
                predicates.add(contains(cb, root, NAME, query.getName()));
            }

            if (isNotBlank(query.getGameMode())) {
                predicates.add(contains(cb, root, GAME_MODE, query.getGameMode()));
            }

            if (isNotBlank(query.getVisibility())) {
                predicates.add(contains(cb, root, VISIBILITY, query.getVisibility()));
            }

            if (isNotBlank(query.getWorldId())) {
                predicates.add(contains(cb, root, WORLD_ID, query.getWorldId()));
            }

            if (isNotBlank(query.getPersonaId())) {
                predicates.add(contains(cb, root, PERSONA_ID, query.getPersonaId()));
            }

            Subquery<String> subquery = cq.subquery(String.class);
            Root<FavoriteEntity> favoriteRoot = subquery.from(FavoriteEntity.class);

            subquery.select(favoriteRoot.get(ASSET_ID))
                    .where(cb.equal(favoriteRoot.get(ASSET_TYPE), ADVENTURE));

            predicates.add(root.get(ID).in(subquery));

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    private Direction extractDirection(String direction) {
        return isBlank(direction) ? ASC : Direction.fromString(direction);
    }

    private String extractSortByField(String sortByField) {
        return isBlank(sortByField) ? DEFAULT_SORT_BY_FIELD : sortByField;
    }

    private int extractPageSize(Integer pageSize) {
        return pageSize == null ? DEFAULT_ITEMS : pageSize;
    }

    private int extractPageNumber(Integer page) {
        return page == null ? DEFAULT_PAGE : page - 1;
    }
}
