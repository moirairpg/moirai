package me.moirai.discordbot.core.application.usecase.adventure;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.AdventureQueryRepository;
import me.moirai.discordbot.core.application.usecase.adventure.request.SearchAdventures;
import me.moirai.discordbot.core.application.usecase.adventure.result.SearchAdventuresResult;

@UseCaseHandler
public class SearchAdventuresHandler extends AbstractUseCaseHandler<SearchAdventures, SearchAdventuresResult> {

    private final AdventureQueryRepository repository;

    public SearchAdventuresHandler(AdventureQueryRepository repository) {
        this.repository = repository;
    }

    @Override
    public SearchAdventuresResult execute(SearchAdventures query) {

        return repository.search(query);
    }
}
