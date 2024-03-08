package es.thalesalv.chatrpg.core.application.query.world;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.cqrs.query.QueryHandler;
import es.thalesalv.chatrpg.core.domain.world.WorldRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchWorldsHandler extends QueryHandler<SearchWorlds, SearchWorldsResult> {

    private final WorldRepository repository;

    @Override
    public SearchWorldsResult handle(SearchWorlds query) {

        return repository.searchWorlds(query);
    }
}
