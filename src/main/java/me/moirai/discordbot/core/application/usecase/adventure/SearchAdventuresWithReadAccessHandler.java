package me.moirai.discordbot.core.application.usecase.adventure;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.AdventureQueryRepository;
import me.moirai.discordbot.core.application.usecase.adventure.request.SearchAdventuresWithReadAccess;
import me.moirai.discordbot.core.application.usecase.adventure.result.SearchAdventuresResult;

@UseCaseHandler
public class SearchAdventuresWithReadAccessHandler extends AbstractUseCaseHandler<SearchAdventuresWithReadAccess, SearchAdventuresResult> {

    private final AdventureQueryRepository repository;

    public SearchAdventuresWithReadAccessHandler(AdventureQueryRepository repository) {
        this.repository = repository;
    }

    @Override
    public SearchAdventuresResult execute(SearchAdventuresWithReadAccess query) {

        return repository.search(query);
    }
}
