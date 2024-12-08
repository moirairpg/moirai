package me.moirai.discordbot.core.application.usecase.adventure;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.AdventureQueryRepository;
import me.moirai.discordbot.core.application.usecase.adventure.request.SearchFavoriteAdventures;
import me.moirai.discordbot.core.application.usecase.adventure.result.SearchAdventuresResult;

@UseCaseHandler
public class SearchFavoriteAdventuresHandler extends AbstractUseCaseHandler<SearchFavoriteAdventures, SearchAdventuresResult> {

    private final AdventureQueryRepository repository;

    public SearchFavoriteAdventuresHandler(AdventureQueryRepository repository) {
        this.repository = repository;
    }

    @Override
    public SearchAdventuresResult execute(SearchFavoriteAdventures query) {

        return repository.search(query);
    }
}
