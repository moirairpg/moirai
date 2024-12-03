package me.moirai.discordbot.core.application.usecase.persona;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.PersonaQueryRepository;
import me.moirai.discordbot.core.application.usecase.persona.request.SearchPersonasWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.persona.result.SearchPersonasResult;

@UseCaseHandler
public class SearchPersonasWithWriteAccessHandler extends AbstractUseCaseHandler<SearchPersonasWithWriteAccess, SearchPersonasResult> {

    private final PersonaQueryRepository repository;

    public SearchPersonasWithWriteAccessHandler(PersonaQueryRepository repository) {
        this.repository = repository;
    }

    @Override
    public SearchPersonasResult execute(SearchPersonasWithWriteAccess query) {

        return repository.search(query);
    }
}
