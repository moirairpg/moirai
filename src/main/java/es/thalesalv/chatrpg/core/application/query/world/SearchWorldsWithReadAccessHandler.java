package es.thalesalv.chatrpg.core.application.query.world;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.domain.world.WorldRepository;
import lombok.RequiredArgsConstructor;

@UseCaseHandler
@RequiredArgsConstructor
public class SearchWorldsWithReadAccessHandler extends AbstractUseCaseHandler<SearchWorldsWithReadAccess, SearchWorldsResult> {

    private final WorldRepository repository;

    @Override
    public SearchWorldsResult execute(SearchWorldsWithReadAccess query) {

        return repository.searchWorldsWithReadAccess(query);
    }
}
