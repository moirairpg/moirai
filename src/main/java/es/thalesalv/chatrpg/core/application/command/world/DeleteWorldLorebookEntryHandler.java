package es.thalesalv.chatrpg.core.application.command.world;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.usecases.UseCaseHandler;
import es.thalesalv.chatrpg.core.domain.world.WorldDomainService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DeleteWorldLorebookEntryHandler extends UseCaseHandler<DeleteWorldLorebookEntry, Void> {

    private static final String ENTRY_ID_CANNOT_BE_NULL_OR_EMPTY = "Lorebook entry ID cannot be null or empty";
    private static final String WORLD_ID_CANNOT_BE_NULL_OR_EMPTY = "World ID cannot be null or empty";

    private final WorldDomainService domainService;

    @Override
    public void validate(DeleteWorldLorebookEntry command) {

        if (StringUtils.isBlank(command.getLorebookEntryId())) {
            throw new IllegalArgumentException(ENTRY_ID_CANNOT_BE_NULL_OR_EMPTY);
        }

        if (StringUtils.isBlank(command.getWorldId())) {
            throw new IllegalArgumentException(WORLD_ID_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    @Override
    public Void execute(DeleteWorldLorebookEntry command) {

        domainService.deleteLorebookEntry(command);

        return null;
    }
}
