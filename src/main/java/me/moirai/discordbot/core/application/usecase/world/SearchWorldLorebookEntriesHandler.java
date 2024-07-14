package me.moirai.discordbot.core.application.usecase.world;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.world.request.SearchWorldLorebookEntries;
import me.moirai.discordbot.core.application.usecase.world.result.SearchWorldLorebookEntriesResult;
import me.moirai.discordbot.core.domain.world.WorldLorebookEntryRepository;

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
