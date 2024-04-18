package es.thalesalv.chatrpg.core.application.command.world;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntry;
import es.thalesalv.chatrpg.core.domain.world.WorldService;
import lombok.RequiredArgsConstructor;

@UseCaseHandler
@RequiredArgsConstructor
public class UpdateWorldLorebookEntryHandler
        extends AbstractUseCaseHandler<UpdateWorldLorebookEntry, UpdateWorldLorebookEntryResult> {

    private final WorldService service;

    @Override
    public UpdateWorldLorebookEntryResult execute(UpdateWorldLorebookEntry command) {

        return mapResult(service.updateLorebookEntry(command));
    }

    private UpdateWorldLorebookEntryResult mapResult(WorldLorebookEntry savedEntry) {

        return UpdateWorldLorebookEntryResult.build(savedEntry.getLastUpdateDate());
    }
}
