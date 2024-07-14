package me.moirai.discordbot.core.application.usecase.world;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.world.request.SearchWorldsWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.world.result.SearchWorldsResult;
import me.moirai.discordbot.core.domain.world.WorldRepository;

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
