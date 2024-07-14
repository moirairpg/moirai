package es.thalesalv.chatrpg.core.application.usecase.world;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.application.usecase.world.request.SearchWorldsWithWriteAccess;
import es.thalesalv.chatrpg.core.application.usecase.world.result.SearchWorldsResult;
import es.thalesalv.chatrpg.core.domain.world.WorldRepository;

@UseCaseHandler
public class SearchWorldsWithWriteAccessHandler extends AbstractUseCaseHandler<SearchWorldsWithWriteAccess, SearchWorldsResult> {

    private final WorldRepository repository;

    public SearchWorldsWithWriteAccessHandler(WorldRepository repository) {
        this.repository = repository;
    }

    @Override
    public SearchWorldsResult execute(SearchWorldsWithWriteAccess query) {

        return repository.searchWorldsWithWriteAccess(query);
    }
}
