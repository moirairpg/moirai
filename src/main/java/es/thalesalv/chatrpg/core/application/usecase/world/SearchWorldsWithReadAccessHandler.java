package es.thalesalv.chatrpg.core.application.usecase.world;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.application.usecase.world.request.SearchWorldsWithReadAccess;
import es.thalesalv.chatrpg.core.application.usecase.world.result.SearchWorldsResult;
import es.thalesalv.chatrpg.core.domain.world.WorldRepository;

@UseCaseHandler
public class SearchWorldsWithReadAccessHandler extends AbstractUseCaseHandler<SearchWorldsWithReadAccess, SearchWorldsResult> {

    private final WorldRepository repository;

    public SearchWorldsWithReadAccessHandler(WorldRepository repository) {
        this.repository = repository;
    }

    @Override
    public SearchWorldsResult execute(SearchWorldsWithReadAccess query) {

        return repository.searchWorldsWithReadAccess(query);
    }
}
