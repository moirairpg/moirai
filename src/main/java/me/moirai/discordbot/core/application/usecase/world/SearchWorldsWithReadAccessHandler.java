package me.moirai.discordbot.core.application.usecase.world;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.WorldQueryRepository;
import me.moirai.discordbot.core.application.usecase.world.request.SearchWorldsWithReadAccess;
import me.moirai.discordbot.core.application.usecase.world.result.SearchWorldsResult;

@UseCaseHandler
public class SearchWorldsWithReadAccessHandler extends AbstractUseCaseHandler<SearchWorldsWithReadAccess, SearchWorldsResult> {

    private final WorldQueryRepository repository;

    public SearchWorldsWithReadAccessHandler(WorldQueryRepository repository) {
        this.repository = repository;
    }

    @Override
    public SearchWorldsResult execute(SearchWorldsWithReadAccess query) {

        return repository.searchWorldsWithReadAccess(query);
    }
}
