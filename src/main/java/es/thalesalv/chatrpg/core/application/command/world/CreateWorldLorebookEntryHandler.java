package es.thalesalv.chatrpg.core.application.command.world;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntry;
import es.thalesalv.chatrpg.core.domain.world.WorldService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@UseCaseHandler
@RequiredArgsConstructor
public class CreateWorldLorebookEntryHandler
        extends AbstractUseCaseHandler<CreateWorldLorebookEntry, CreateWorldLorebookEntryResult> {

    private final WorldService domainService;

    @Override
    public void validate(CreateWorldLorebookEntry command) {

        if (StringUtils.isBlank(command.getWorldId())) {

            throw new IllegalArgumentException("World ID cannot be null");
        }

        if (StringUtils.isBlank(command.getName())) {

            throw new IllegalArgumentException("Name cannot be null");
        }

        if (StringUtils.isBlank(command.getDescription())) {

            throw new IllegalArgumentException("Description cannot be null");
        }
    }

    @Override
    public CreateWorldLorebookEntryResult execute(CreateWorldLorebookEntry command) {

        WorldLorebookEntry entry = domainService.createLorebookEntry(command);
        return CreateWorldLorebookEntryResult.build(entry.getId());
    }
}
