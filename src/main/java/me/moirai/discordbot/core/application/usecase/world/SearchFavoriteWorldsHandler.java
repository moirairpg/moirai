package me.moirai.discordbot.core.application.usecase.world;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.WorldQueryRepository;
import me.moirai.discordbot.core.application.usecase.world.request.SearchFavoriteWorlds;
import me.moirai.discordbot.core.application.usecase.world.result.SearchWorldsResult;

@UseCaseHandler
public class SearchFavoriteWorldsHandler extends AbstractUseCaseHandler<SearchFavoriteWorlds, SearchWorldsResult> {

    private final WorldQueryRepository repository;

    public SearchFavoriteWorldsHandler(WorldQueryRepository repository) {
        this.repository = repository;
    }

    @Override
    public SearchWorldsResult execute(SearchFavoriteWorlds query) {

        return repository.search(query);
    }
}
