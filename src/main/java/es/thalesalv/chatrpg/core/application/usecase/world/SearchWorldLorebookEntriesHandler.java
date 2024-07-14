package es.thalesalv.chatrpg.core.application.usecase.world;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.application.usecase.world.request.SearchWorldLorebookEntries;
import es.thalesalv.chatrpg.core.application.usecase.world.result.SearchWorldLorebookEntriesResult;
import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntryRepository;

@UseCaseHandler
public class SearchWorldLorebookEntriesHandler extends AbstractUseCaseHandler<SearchWorldLorebookEntries, SearchWorldLorebookEntriesResult> {

    private final WorldLorebookEntryRepository repository;

    public SearchWorldLorebookEntriesHandler(WorldLorebookEntryRepository repository) {
        this.repository = repository;
    }

    @Override
    public SearchWorldLorebookEntriesResult execute(SearchWorldLorebookEntries query) {

        return repository.searchWorldLorebookEntriesByWorldId(query);
    }
}
