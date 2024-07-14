package es.thalesalv.chatrpg.core.application.usecase.persona;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.application.usecase.persona.request.SearchPersonasWithWriteAccess;
import es.thalesalv.chatrpg.core.application.usecase.persona.result.SearchPersonasResult;
import es.thalesalv.chatrpg.core.domain.persona.PersonaRepository;

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
