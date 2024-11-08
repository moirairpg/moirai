package me.moirai.discordbot.core.application.usecase.persona;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.PersonaQueryRepository;
import me.moirai.discordbot.core.application.usecase.persona.request.SearchPersonasWithReadAccess;
import me.moirai.discordbot.core.application.usecase.persona.result.SearchPersonasResult;

@UseCaseHandler
public class SearchPersonasWithReadAccessHandler extends AbstractUseCaseHandler<SearchPersonasWithReadAccess, SearchPersonasResult> {

    private final PersonaQueryRepository repository;

    public SearchPersonasWithReadAccessHandler(PersonaQueryRepository repository) {
        this.repository = repository;
    }

    @Override
    public SearchPersonasResult execute(SearchPersonasWithReadAccess query) {

        return repository.searchPersonasWithReadAccess(query);
    }
}
