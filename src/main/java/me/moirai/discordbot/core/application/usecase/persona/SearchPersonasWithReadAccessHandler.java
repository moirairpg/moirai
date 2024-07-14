package me.moirai.discordbot.core.application.usecase.persona;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.persona.request.SearchPersonasWithReadAccess;
import me.moirai.discordbot.core.application.usecase.persona.result.SearchPersonasResult;
import me.moirai.discordbot.core.domain.persona.PersonaRepository;

@UseCaseHandler
public class SearchPersonasWithReadAccessHandler extends AbstractUseCaseHandler<SearchPersonasWithReadAccess, SearchPersonasResult> {

    private final PersonaRepository repository;

    public SearchPersonasWithReadAccessHandler(PersonaRepository repository) {
        this.repository = repository;
    }

    @Override
    public SearchPersonasResult execute(SearchPersonasWithReadAccess query) {

        return repository.searchPersonasWithReadAccess(query);
    }
}
