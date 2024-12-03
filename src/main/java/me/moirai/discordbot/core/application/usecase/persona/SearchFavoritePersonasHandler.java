package me.moirai.discordbot.core.application.usecase.persona;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.PersonaQueryRepository;
import me.moirai.discordbot.core.application.usecase.persona.request.SearchFavoritePersonas;
import me.moirai.discordbot.core.application.usecase.persona.result.SearchPersonasResult;

@UseCaseHandler
public class SearchFavoritePersonasHandler extends AbstractUseCaseHandler<SearchFavoritePersonas, SearchPersonasResult> {

    private final PersonaQueryRepository repository;

    public SearchFavoritePersonasHandler(PersonaQueryRepository repository) {
        this.repository = repository;
    }

    @Override
    public SearchPersonasResult execute(SearchFavoritePersonas query) {

        return repository.search(query);
    }
}
