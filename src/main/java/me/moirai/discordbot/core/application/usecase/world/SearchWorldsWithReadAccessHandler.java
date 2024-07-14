package me.moirai.discordbot.core.application.usecase.world;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.world.request.SearchWorldsWithReadAccess;
import me.moirai.discordbot.core.application.usecase.world.result.SearchWorldsResult;
import me.moirai.discordbot.core.domain.world.WorldRepository;

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
