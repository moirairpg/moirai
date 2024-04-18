package es.thalesalv.chatrpg.core.application.command.world;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.usecases.UseCaseHandler;
import es.thalesalv.chatrpg.core.domain.world.WorldService;
import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateWorldLorebookEntryHandler
        extends UseCaseHandler<UpdateWorldLorebookEntry, UpdateWorldLorebookEntryResult> {

    private final WorldService service;

    @Override
    public UpdateWorldLorebookEntryResult execute(UpdateWorldLorebookEntry command) {

        return mapResult(service.updateLorebookEntry(command));
    }

    private UpdateWorldLorebookEntryResult mapResult(WorldLorebookEntry savedEntry) {

        return UpdateWorldLorebookEntryResult.build(savedEntry.getLastUpdateDate());
    }
}
