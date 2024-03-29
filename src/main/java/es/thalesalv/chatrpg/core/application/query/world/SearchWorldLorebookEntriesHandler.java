package es.thalesalv.chatrpg.core.application.query.world;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.usecases.UseCaseHandler;
import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchWorldLorebookEntriesHandler extends UseCaseHandler<SearchWorldLorebookEntries, SearchWorldLorebookEntriesResult> {

    private final WorldLorebookEntryRepository repository;

    @Override
    public SearchWorldLorebookEntriesResult execute(SearchWorldLorebookEntries query) {

        return repository.searchWorldLorebookEntriesByWorldId(query);
    }
}
