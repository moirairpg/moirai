package es.thalesalv.chatrpg.infrastructure.outbound.persistence.world;

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

import es.thalesalv.chatrpg.core.application.query.world.GetWorldResult;
import es.thalesalv.chatrpg.core.application.query.world.SearchWorldsWithReadAccess;
import es.thalesalv.chatrpg.core.application.query.world.SearchWorldsResult;
import es.thalesalv.chatrpg.core.application.query.world.SearchWorldsWithWriteAccess;
import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.Visibility;
import es.thalesalv.chatrpg.core.domain.world.World;
import es.thalesalv.chatrpg.core.domain.world.WorldRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorldRepositoryImpl implements WorldRepository {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_ITEMS = 10;
    private static final String DEFAULT_SORT_BY_FIELD = "name";

    private final WorldJpaRepository jpaRepository;

    @Override
    public World save(World world) {

        WorldEntity entity = mapToEntity(world);

        return mapFromEntity(jpaRepository.save(entity));
    }

    @Override
    public Optional<World> findById(String id, String requesterDiscordId) {

        return jpaRepository.findById(id, requesterDiscordId)
                .map(this::mapFromEntity);
    }

    @Override
    public void deleteById(String id) {

        jpaRepository.deleteById(id);
    }

    @Override
    public SearchWorldsResult searchWorldsWithReadAccess(SearchWorldsWithReadAccess query, String requesterDiscordId) {

        int page = query.getPage() == null ? DEFAULT_PAGE : query.getPage() - 1;
        int items = query.getItems() == null ? DEFAULT_ITEMS : query.getItems();
        String sortByField = isBlank(query.getSortByField()) ? DEFAULT_SORT_BY_FIELD : query.getSortByField();
        Direction direction = isBlank(query.getDirection()) ? Direction.ASC
                : Direction.fromString(query.getDirection());

        PageRequest pageRequest = PageRequest.of(page, items, Sort.by(direction, sortByField));
        Specification<WorldEntity> filters = readAccessSpecificationFrom(query, requesterDiscordId);
        Page<WorldEntity> pagedResult = jpaRepository.findAll(filters, pageRequest);

        return SearchWorldsResult.builder()
                .results(pagedResult.getContent()
                        .stream()
                        .map(this::mapToResult)
                        .toList())
                .page(page)
                .items(pagedResult.getNumberOfElements())
                .totalItems(pagedResult.getTotalElements())
                .totalPages(pagedResult.getTotalPages())
                .build();
    }

    @Override
    public SearchWorldsResult searchWorldsWithWriteAccess(SearchWorldsWithWriteAccess query,
            String requesterDiscordId) {

        int page = query.getPage() == null ? DEFAULT_PAGE : query.getPage() - 1;
        int items = query.getItems() == null ? DEFAULT_ITEMS : query.getItems();
        String sortByField = isBlank(query.getSortByField()) ? DEFAULT_SORT_BY_FIELD : query.getSortByField();
        Direction direction = isBlank(query.getDirection()) ? Direction.ASC
                : Direction.fromString(query.getDirection());

        PageRequest pageRequest = PageRequest.of(page, items, Sort.by(direction, sortByField));
        Specification<WorldEntity> filters = writeAccessSpecificationFrom(query, requesterDiscordId);
        Page<WorldEntity> pagedResult = jpaRepository.findAll(filters, pageRequest);

        return SearchWorldsResult.builder()
                .results(pagedResult.getContent()
                        .stream()
                        .map(this::mapToResult)
                        .toList())
                .page(page)
                .items(pagedResult.getNumberOfElements())
                .totalItems(pagedResult.getTotalElements())
                .totalPages(pagedResult.getTotalPages())
                .build();
    }

    private WorldEntity mapToEntity(World world) {

        return WorldEntity.builder()
                .id(world.getId())
                .name(world.getName())
                .description(world.getDescription())
                .adventureStart(world.getAdventureStart())
                .visibility(world.getVisibility().toString())
                .ownerDiscordId(world.getOwnerDiscordId())
                .usersAllowedToRead(world.getReaderUsers())
                .usersAllowedToWrite(world.getWriterUsers())
                .build();
    }

    private World mapFromEntity(WorldEntity world) {

        Permissions permissions = Permissions.builder()
                .ownerDiscordId(world.getOwnerDiscordId())
                .usersAllowedToRead(world.getUsersAllowedToRead())
                .usersAllowedToWrite(world.getUsersAllowedToWrite())
                .build();

        return World.builder()
                .id(world.getId())
                .name(world.getName())
                .description(world.getDescription())
                .adventureStart(world.getAdventureStart())
                .visibility(Visibility.fromString(world.getVisibility()))
                .permissions(permissions)
                .creationDate(world.getCreationDate())
                .lastUpdateDate(world.getLastUpdateDate())
                .creatorDiscordId(world.getCreatorDiscordId())
                .build();
    }

    private GetWorldResult mapToResult(WorldEntity world) {

        return GetWorldResult.builder()
                .id(world.getId())
                .name(world.getName())
                .description(world.getDescription())
                .adventureStart(world.getAdventureStart())
                .visibility(world.getVisibility().toString())
                .ownerDiscordId(world.getOwnerDiscordId())
                .creationDate(world.getCreationDate())
                .lastUpdateDate(world.getLastUpdateDate())
                .build();
    }

    private Specification<WorldEntity> readAccessSpecificationFrom(SearchWorldsWithReadAccess query,
            String requesterDiscordId) {

        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Predicate isOwner = cb.equal(root.get("ownerDiscordId"), requesterDiscordId);
            Predicate isAllowedToRead = cb.like(root.get("usersAllowedToReadString"),
                    "%" + requesterDiscordId + "%");

            Predicate isAllowedToWrite = cb.like(root.get("usersAllowedToWriteString"),
                    "%" + requesterDiscordId + "%");

            predicates.add(cb.or(isOwner, isAllowedToRead, isAllowedToWrite));

            if (StringUtils.isNotBlank(query.getName())) {
                predicates.add(cb.and(cb.like(cb.upper(root.get("name")),
                        "%" + query.getName().toUpperCase() + "%")));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    private Specification<WorldEntity> writeAccessSpecificationFrom(SearchWorldsWithWriteAccess query,
            String requesterDiscordId) {

        return (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Predicate isOwner = cb.equal(root.get("ownerDiscordId"), requesterDiscordId);
            Predicate isAllowedToWrite = cb.like(root.get("usersAllowedToWriteString"),
                    "%" + requesterDiscordId + "%");

            predicates.add(cb.or(isOwner, isAllowedToWrite));

            if (StringUtils.isNotBlank(query.getName())) {
                predicates.add(cb.and(cb.like(cb.upper(root.get("name")),
                        "%" + query.getName().toUpperCase() + "%")));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
