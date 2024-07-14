package me.moirai.discordbot.core.application.usecase.persona;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.persona.request.SearchPersonasWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.persona.result.SearchPersonasResult;
import me.moirai.discordbot.core.domain.persona.PersonaRepository;

@UseCaseHandler
public class SearchPersonasWithWriteAccessHandler extends AbstractUseCaseHandler<SearchPersonasWithWriteAccess, SearchPersonasResult> {

    private final PersonaRepository repository;

    public SearchPersonasWithWriteAccessHandler(PersonaRepository repository) {
        this.repository = repository;
    }

    @Override
    public SearchPersonasResult execute(SearchPersonasWithWriteAccess query) {

        return repository.searchPersonasWithWriteAccess(query);
    }
}
