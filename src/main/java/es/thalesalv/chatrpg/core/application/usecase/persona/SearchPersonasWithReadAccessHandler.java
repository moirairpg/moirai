package es.thalesalv.chatrpg.core.application.usecase.persona;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.application.usecase.persona.request.SearchPersonasWithReadAccess;
import es.thalesalv.chatrpg.core.application.usecase.persona.result.SearchPersonasResult;
import es.thalesalv.chatrpg.core.domain.persona.PersonaRepository;

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
