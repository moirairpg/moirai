package me.moirai.discordbot.infrastructure.outbound.persistence.world;

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
import me.moirai.discordbot.core.application.port.WorldQueryRepository;
import me.moirai.discordbot.core.application.usecase.world.request.SearchFavoriteWorlds;
import me.moirai.discordbot.core.application.usecase.world.request.SearchWorldsWithReadAccess;
import me.moirai.discordbot.core.application.usecase.world.request.SearchWorldsWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.world.result.SearchWorldsResult;
import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteEntity;
import me.moirai.discordbot.infrastructure.outbound.persistence.mapper.WorldPersistenceMapper;

@Repository
public class WorldQueryRepositoryImpl implements WorldQueryRepository {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_ITEMS = 10;

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String ASSET_ID = "assetId";
    private static final String ASSET_TYPE = "assetType";
    private static final String WORLD = "world";
    private static final String VISIBILITY = "visibility";
    private static final String DEFAULT_SORT_BY_FIELD = "name";

    private final WorldJpaRepository jpaRepository;
    private final WorldPersistenceMapper mapper;

    public WorldQueryRepositoryImpl(WorldJpaRepository jpaRepository,
            WorldPersistenceMapper mapper) {

        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<World> findById(String id) {

        return jpaRepository.findById(id)
                .map(mapper::mapFromEntity);
    }

    @Override
    public SearchWorldsResult search(SearchWorldsWithReadAccess request) {

        int page = extractPageNumber(request.getPage());
        int size = extractPageSize(request.getItems());
        String sortByField = extractSortByField(request.getSortByField());
        Direction direction = extractDirection(request.getDirection());

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortByField));
        Specification<WorldEntity> query = buildSearchQuery(request);
        Page<WorldEntity> pagedResult = jpaRepository.findAll(query, pageRequest);

        return mapper.mapToResult(pagedResult);
    }

    @Override
    public SearchWorldsResult search(SearchWorldsWithWriteAccess request) {

        int page = extractPageNumber(request.getPage());
        int size = extractPageSize(request.getItems());
        String sortByField = extractSortByField(request.getSortByField());
        Direction direction = extractDirection(request.getDirection());

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortByField));
        Specification<WorldEntity> query = buildSearchQuery(request);
        Page<WorldEntity> pagedResult = jpaRepository.findAll(query, pageRequest);

        return mapper.mapToResult(pagedResult);
    }

    @Override
    public SearchWorldsResult search(SearchFavoriteWorlds request) {

        int page = extractPageNumber(request.getPage());
        int size = extractPageSize(request.getItems());
        String sortByField = extractSortByField(request.getSortByField());
        Direction direction = extractDirection(request.getDirection());

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortByField));
        Specification<WorldEntity> query = buildSearchQuery(request);
        Page<WorldEntity> pagedResult = jpaRepository.findAll(query, pageRequest);

        return mapper.mapToResult(pagedResult);
    }

    private Specification<WorldEntity> buildSearchQuery(SearchWorldsWithReadAccess query) {

        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(canUserRead(cb, root, query.getRequesterDiscordId()));

            if (isNotBlank(query.getName())) {
                predicates.add(contains(cb, root, NAME, query.getName()));
            }

            if (isNotBlank(query.getVisibility())) {
                predicates.add(contains(cb, root, VISIBILITY, query.getVisibility()));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    private Specification<WorldEntity> buildSearchQuery(SearchWorldsWithWriteAccess query) {

        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(canUserWrite(cb, root, query.getRequesterDiscordId()));

            if (isNotBlank(query.getName())) {
                predicates.add(contains(cb, root, NAME, query.getName()));
            }

            if (isNotBlank(query.getVisibility())) {
                predicates.add(contains(cb, root, VISIBILITY, query.getVisibility()));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    private Specification<WorldEntity> buildSearchQuery(SearchFavoriteWorlds query) {

        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(canUserWrite(cb, root, query.getRequesterDiscordId()));

            if (isNotBlank(query.getName())) {
                predicates.add(contains(cb, root, NAME, query.getName()));
            }

            if (isNotBlank(query.getVisibility())) {
                predicates.add(contains(cb, root, VISIBILITY, query.getVisibility()));
            }

            Subquery<String> subquery = cq.subquery(String.class);
            Root<FavoriteEntity> favoriteRoot = subquery.from(FavoriteEntity.class);

            subquery.select(favoriteRoot.get(ASSET_ID))
                    .where(cb.equal(favoriteRoot.get(ASSET_TYPE), WORLD));

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
