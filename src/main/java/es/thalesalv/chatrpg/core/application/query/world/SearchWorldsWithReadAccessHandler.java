package es.thalesalv.chatrpg.core.application.query.world;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.usecases.UseCaseHandler;
import es.thalesalv.chatrpg.core.domain.world.WorldRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchWorldsWithReadAccessHandler extends UseCaseHandler<SearchWorldsWithReadAccess, SearchWorldsResult> {

    private final WorldRepository repository;

    @Override
    public SearchWorldsResult execute(SearchWorldsWithReadAccess query) {

        // TODO extract real ID from principal when API is ready
        return repository.searchWorldsWithReadAccess(query);
    }
}
