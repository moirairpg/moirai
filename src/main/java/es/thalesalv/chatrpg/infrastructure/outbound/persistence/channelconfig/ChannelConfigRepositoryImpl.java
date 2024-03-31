package es.thalesalv.chatrpg.infrastructure.outbound.persistence.channelconfig;

import static es.thalesalv.chatrpg.core.domain.channelconfig.ArtificialIntelligenceModel.findByInternalModelName;
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

import es.thalesalv.chatrpg.core.application.query.channelconfig.GetChannelConfigResult;
import es.thalesalv.chatrpg.core.application.query.channelconfig.SearchChannelConfigsResult;
import es.thalesalv.chatrpg.core.application.query.channelconfig.SearchChannelConfigsWithReadAccess;
import es.thalesalv.chatrpg.core.application.query.channelconfig.SearchChannelConfigsWithWriteAccess;
import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.Visibility;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfig;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigRepository;
import es.thalesalv.chatrpg.core.domain.channelconfig.ModelConfiguration;
import es.thalesalv.chatrpg.core.domain.channelconfig.Moderation;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ChannelConfigRepositoryImpl implements ChannelConfigRepository {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_ITEMS = 10;
    private static final String DEFAULT_SORT_BY_FIELD = "creationDate";

    private final ChannelConfigJpaRepository jpaRepository;

    @Override
    public ChannelConfig save(ChannelConfig channelConfig) {

        ChannelConfigEntity entity = mapToEntity(channelConfig);

        return mapFromEntity(jpaRepository.save(entity));
    }

    @Override
    public Optional<ChannelConfig> findById(String id) {

        return jpaRepository.findById(id)
                .map(this::mapFromEntity);
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

        return SearchChannelConfigsResult.builder()
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
    public SearchChannelConfigsResult searchChannelConfigsWithWriteAccess(SearchChannelConfigsWithWriteAccess query) {

        int page = query.getPage() == null ? DEFAULT_PAGE : query.getPage() - 1;
        int items = query.getItems() == null ? DEFAULT_ITEMS : query.getItems();
        String sortByField = isBlank(query.getSortByField()) ? DEFAULT_SORT_BY_FIELD : query.getSortByField();
        Direction direction = isBlank(query.getDirection()) ? Direction.ASC
                : Direction.fromString(query.getDirection());

        PageRequest pageRequest = PageRequest.of(page, items, Sort.by(direction, sortByField));
        Specification<ChannelConfigEntity> filters = writeAccessSpecificationFrom(query);
        Page<ChannelConfigEntity> pagedResult = jpaRepository.findAll(filters, pageRequest);

        return SearchChannelConfigsResult.builder()
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

    private ChannelConfigEntity mapToEntity(ChannelConfig channelConfig) {

        String creatorOrOwnerDiscordId = isBlank(channelConfig.getCreatorDiscordId())
                ? channelConfig.getOwnerDiscordId()
                : channelConfig.getCreatorDiscordId();

        ModelConfigurationEntity modelConfiguration = ModelConfigurationEntity.builder()
                .aiModel(channelConfig.getModelConfiguration().getAiModel().getInternalModelName())
                .frequencyPenalty(channelConfig.getModelConfiguration().getFrequencyPenalty())
                .presencePenalty(channelConfig.getModelConfiguration().getPresencePenalty())
                .temperature(channelConfig.getModelConfiguration().getTemperature())
                .logitBias(channelConfig.getModelConfiguration().getLogitBias())
                .maxTokenLimit(channelConfig.getModelConfiguration().getMaxTokenLimit())
                .messageHistorySize(channelConfig.getModelConfiguration().getMessageHistorySize())
                .stopSequences(channelConfig.getModelConfiguration().getStopSequences())
                .build();

        return ChannelConfigEntity.builder()
                .id(channelConfig.getId())
                .name(channelConfig.getName())
                .personaId(channelConfig.getPersonaId())
                .worldId(channelConfig.getWorldId())
                .discordChannelId(channelConfig.getDiscordChannelId())
                .modelConfiguration(modelConfiguration)
                .visibility(channelConfig.getVisibility().toString())
                .moderation(channelConfig.getModeration().toString())
                .ownerDiscordId(channelConfig.getOwnerDiscordId())
                .creatorDiscordId(creatorOrOwnerDiscordId)
                .usersAllowedToWrite(channelConfig.getUsersAllowedToWrite())
                .usersAllowedToRead(channelConfig.getUsersAllowedToRead())
                .creationDate(channelConfig.getCreationDate())
                .lastUpdateDate(channelConfig.getLastUpdateDate())
                .build();
    }

    private ChannelConfig mapFromEntity(ChannelConfigEntity channelConfig) {

        ModelConfiguration modelConfiguration = ModelConfiguration.builder()
                .aiModel(findByInternalModelName(channelConfig.getModelConfiguration().getAiModel()))
                .frequencyPenalty(channelConfig.getModelConfiguration().getFrequencyPenalty())
                .presencePenalty(channelConfig.getModelConfiguration().getPresencePenalty())
                .temperature(channelConfig.getModelConfiguration().getTemperature())
                .logitBias(channelConfig.getModelConfiguration().getLogitBias())
                .maxTokenLimit(channelConfig.getModelConfiguration().getMaxTokenLimit())
                .messageHistorySize(channelConfig.getModelConfiguration().getMessageHistorySize())
                .stopSequences(channelConfig.getModelConfiguration().getStopSequences())
                .build();

        Permissions permissions = Permissions.builder()
                .ownerDiscordId(channelConfig.getOwnerDiscordId())
                .usersAllowedToRead(channelConfig.getUsersAllowedToRead())
                .usersAllowedToWrite(channelConfig.getUsersAllowedToWrite())
                .build();

        return ChannelConfig.builder()
                .id(channelConfig.getId())
                .name(channelConfig.getName())
                .personaId(channelConfig.getPersonaId())
                .worldId(channelConfig.getWorldId())
                .discordChannelId(channelConfig.getDiscordChannelId())
                .modelConfiguration(modelConfiguration)
                .permissions(permissions)
                .visibility(Visibility.fromString(channelConfig.getVisibility()))
                .moderation(Moderation.fromString(channelConfig.getModeration()))
                .creatorDiscordId(channelConfig.getCreatorDiscordId())
                .creationDate(channelConfig.getCreationDate())
                .lastUpdateDate(channelConfig.getLastUpdateDate())
                .build();
    }

    private GetChannelConfigResult mapToResult(ChannelConfigEntity channelConfig) {

        return GetChannelConfigResult.builder()
                .id(channelConfig.getId())
                .name(channelConfig.getName())
                .worldId(channelConfig.getWorldId())
                .personaId(channelConfig.getPersonaId())
                .discordChannelId(channelConfig.getDiscordChannelId())
                .visibility(channelConfig.getVisibility())
                .aiModel(channelConfig.getModelConfiguration().getAiModel())
                .moderation(channelConfig.getModeration())
                .maxTokenLimit(channelConfig.getModelConfiguration().getMaxTokenLimit())
                .messageHistorySize(channelConfig.getModelConfiguration().getMessageHistorySize())
                .temperature(channelConfig.getModelConfiguration().getTemperature())
                .frequencyPenalty(channelConfig.getModelConfiguration().getFrequencyPenalty())
                .presencePenalty(channelConfig.getModelConfiguration().getPresencePenalty())
                .stopSequences(channelConfig.getModelConfiguration().getStopSequences())
                .logitBias(channelConfig.getModelConfiguration().getLogitBias())
                .usersAllowedToWrite(channelConfig.getUsersAllowedToWrite())
                .usersAllowedToRead(channelConfig.getUsersAllowedToRead())
                .ownerDiscordId(channelConfig.getOwnerDiscordId())
                .creationDate(channelConfig.getCreationDate())
                .lastUpdateDate(channelConfig.getLastUpdateDate())
                .build();
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
