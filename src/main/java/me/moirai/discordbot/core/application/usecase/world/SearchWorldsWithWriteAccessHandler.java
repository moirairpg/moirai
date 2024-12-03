package me.moirai.discordbot.core.application.usecase.world;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.WorldQueryRepository;
import me.moirai.discordbot.core.application.usecase.world.request.SearchWorldsWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.world.result.SearchWorldsResult;

@UseCaseHandler
public class SearchWorldsWithWriteAccessHandler extends AbstractUseCaseHandler<SearchWorldsWithWriteAccess, SearchWorldsResult> {

    private final WorldQueryRepository repository;

    public SearchWorldsWithWriteAccessHandler(WorldQueryRepository repository) {
        this.repository = repository;
    }

    @Override
    public SearchWorldsResult execute(SearchWorldsWithWriteAccess query) {

        return repository.search(query);
    }
}
