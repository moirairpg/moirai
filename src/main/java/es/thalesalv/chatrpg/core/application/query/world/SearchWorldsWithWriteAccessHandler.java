package es.thalesalv.chatrpg.core.application.query.world;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.domain.world.WorldRepository;
import lombok.RequiredArgsConstructor;

@UseCaseHandler
@RequiredArgsConstructor
public class SearchWorldsWithWriteAccessHandler extends AbstractUseCaseHandler<SearchWorldsWithWriteAccess, SearchWorldsResult> {

    private final WorldRepository repository;

    @Override
    public SearchWorldsResult execute(SearchWorldsWithWriteAccess query) {

        return repository.searchWorldsWithWriteAccess(query);
    }
}
