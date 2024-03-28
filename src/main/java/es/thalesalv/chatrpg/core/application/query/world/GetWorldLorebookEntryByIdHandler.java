package es.thalesalv.chatrpg.core.application.query.world;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.usecases.UseCaseHandler;
import es.thalesalv.chatrpg.core.domain.world.WorldDomainService;
import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GetWorldLorebookEntryByIdHandler extends UseCaseHandler<GetWorldLorebookEntryById, GetWorldLorebookEntryResult> {

    private final WorldDomainService domainService;

    @Override
    public void validate(GetWorldLorebookEntryById query) {

        if (StringUtils.isBlank(query.getEntryId())) {
            throw new IllegalArgumentException("Lorebook entry ID cannot be null");
        }

        if (StringUtils.isBlank(query.getWorldId())) {
            throw new IllegalArgumentException("World ID cannot be null");
        }
    }

    @Override
    public GetWorldLorebookEntryResult execute(GetWorldLorebookEntryById query) {

        WorldLorebookEntry entry = domainService.findWorldLorebookEntryById(query);

        return mapResult(entry);
    }

    private GetWorldLorebookEntryResult mapResult(WorldLorebookEntry entry) {

        return GetWorldLorebookEntryResult.builder()
                .id(entry.getId())
                .name(entry.getName())
                .regex(entry.getRegex())
                .description(entry.getDescription())
                .playerDiscordId(entry.getPlayerDiscordId())
                .isPlayerCharacter(entry.isPlayerCharacter())
                .creationDate(entry.getCreationDate())
                .lastUpdateDate(entry.getLastUpdateDate())
                .build();
    }
}
