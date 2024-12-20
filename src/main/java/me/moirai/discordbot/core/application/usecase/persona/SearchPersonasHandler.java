package me.moirai.discordbot.core.application.usecase.persona;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.PersonaQueryRepository;
import me.moirai.discordbot.core.application.usecase.persona.request.SearchPersonas;
import me.moirai.discordbot.core.application.usecase.persona.result.SearchPersonasResult;

@UseCaseHandler
public class SearchPersonasHandler extends AbstractUseCaseHandler<SearchPersonas, SearchPersonasResult> {

    private final PersonaQueryRepository repository;

    public SearchPersonasHandler(PersonaQueryRepository repository) {
        this.repository = repository;
    }

    @Override
    public SearchPersonasResult execute(SearchPersonas query) {

        return repository.search(query);
    }
}
