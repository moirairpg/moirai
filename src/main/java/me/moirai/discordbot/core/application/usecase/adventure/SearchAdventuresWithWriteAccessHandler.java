package me.moirai.discordbot.core.application.usecase.adventure;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.AdventureQueryRepository;
import me.moirai.discordbot.core.application.usecase.adventure.request.SearchAdventuresWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.adventure.result.SearchAdventuresResult;

@UseCaseHandler
public class SearchAdventuresWithWriteAccessHandler extends AbstractUseCaseHandler<SearchAdventuresWithWriteAccess, SearchAdventuresResult> {

    private final AdventureQueryRepository repository;

    public SearchAdventuresWithWriteAccessHandler(AdventureQueryRepository repository) {
        this.repository = repository;
    }

    @Override
    public SearchAdventuresResult execute(SearchAdventuresWithWriteAccess query) {

        return repository.search(query);
    }
}
