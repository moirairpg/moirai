package es.thalesalv.chatrpg.infrastructure.outbound.persistence.channelconfig;

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

import es.thalesalv.chatrpg.core.application.usecase.channelconfig.request.SearchChannelConfigsWithReadAccess;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.request.SearchChannelConfigsWithWriteAccess;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.result.SearchChannelConfigsResult;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfig;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigRepository;
import es.thalesalv.chatrpg.infrastructure.outbound.persistence.mapper.ChannelConfigPersistenceMapper;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ChannelConfigRepositoryImpl implements ChannelConfigRepository {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_ITEMS = 10;
    private static final String DEFAULT_SORT_BY_FIELD = "creationDate";

    private final ChannelConfigJpaRepository jpaRepository;
    private final ChannelConfigPersistenceMapper mapper;

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
    public Optional<ChannelConfig> findByDiscordChannelId(String channelId) {

        return jpaRepository.findByDiscordChannelId(channelId)
                .map(mapper::mapFromEntity);
    }

    @Override
    public void deleteById(String id) {

        jpaRepository.deleteById(id);
    }

    @Override
    public SearchChannelConfigsResult searchChannelConfigsWithReadAccess(SearchChannelConfigsWithReadAccess query) {

        int page = query.getPage() == null ? DEFAULT_PAGE : query.getPage() - 1;
        int items = query.getItems() == null ? DEFAULT_ITEMS : query.getItems();
        String sortByField = isBlank(query.getSortByField()) ? DEFAULT_SORT_BY_FIELD : query.getSortByField();
        Direction direction = isBlank(query.getDirection()) ? Direction.ASC
                : Direction.fromString(query.getDirection());

        PageRequest pageRequest = PageRequest.of(page, items, Sort.by(direction, sortByField));
        Specification<ChannelConfigEntity> filters = readAccessSpecificationFrom(query);
        Page<ChannelConfigEntity> pagedResult = jpaRepository.findAll(filters, pageRequest);

        return mapper.mapToResult(pagedResult);
    }

    @Override
    public SearchChannelConfigsResult searchChannelConfigsWithWriteAccess(SearchChannelConfigsWithWriteAccess query) {

        int page = query.getPage() == null ? DEFAULT_PAGE : query.getPage() - 1;
        int items = query.getItems() == null ? DEFAULT_ITEMS : query.getItems();
        String sortByField = isBlank(query.getSortByField()) ? DEFAULT_SORT_BY_FIELD : query.getSortByField();
        Direction direction = isBlank(query.getDirection()) ? Direction.ASC
                : Direction.fromString(query.getDirection());

        PageRequest pageRequest = PageRequest.of(page, items, Sort.by(direction, sortByField));
        Specification<ChannelConfigEntity> filters = writeAccessSpecificationFrom(query);
        Page<ChannelConfigEntity> pagedResult = jpaRepository.findAll(filters, pageRequest);

        return mapper.mapToResult(pagedResult);
    }

    private Specification<ChannelConfigEntity> readAccessSpecificationFrom(SearchChannelConfigsWithReadAccess query) {

        return (root, cq, cb) -> {
            final List<Predicate> predicates = new ArrayList<>();

            Predicate isOwner = cb.equal(root.get("ownerDiscordId"), query.getRequesterDiscordId());
            Predicate isAllowedToRead = cb.like(root.get("usersAllowedToReadString"),
                    "%" + query.getRequesterDiscordId() + "%");

            Predicate isAllowedToWrite = cb.like(root.get("usersAllowedToWriteString"),
                    "%" + query.getRequesterDiscordId() + "%");

            predicates.add(cb.or(isOwner, isAllowedToRead, isAllowedToWrite));

            if (StringUtils.isNotBlank(query.getAiModel())) {
                predicates.add(cb.like(cb.upper(root.get("modelConfiguration")
                        .get("aiModel")), cb.literal(query.getAiModel().toUpperCase())));
            }

            if (StringUtils.isNotBlank(query.getModeration())) {
                predicates.add(cb.like(cb.upper(root.get("moderation")),
                        cb.literal(query.getModeration().toUpperCase())));
            }

            if (StringUtils.isNotBlank(query.getName())) {
                predicates.add(cb.like(cb.upper(root.<String>get("name")),
                        "%" + query.getName().toUpperCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    private Specification<ChannelConfigEntity> writeAccessSpecificationFrom(SearchChannelConfigsWithWriteAccess query) {

        return (root, cq, cb) -> {
            final List<Predicate> predicates = new ArrayList<>();

            Predicate isOwner = cb.equal(root.get("ownerDiscordId"), query.getRequesterDiscordId());
            Predicate isAllowedToWrite = cb.like(root.get("usersAllowedToWriteString"),
                    "%" + query.getRequesterDiscordId() + "%");

            predicates.add(cb.or(isOwner, isAllowedToWrite));

            if (StringUtils.isNotBlank(query.getAiModel())) {
                predicates.add(cb.like(cb.upper(root.get("modelConfiguration")
                        .get("aiModel")), cb.literal(query.getAiModel().toUpperCase())));
            }

            if (StringUtils.isNotBlank(query.getModeration())) {
                predicates.add(cb.like(cb.upper(root.get("moderation")),
                        cb.literal(query.getModeration().toUpperCase())));
            }

            if (StringUtils.isNotBlank(query.getName())) {
                predicates.add(cb.like(cb.upper(root.<String>get("name")),
                        "%" + query.getName().toUpperCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
