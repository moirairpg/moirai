package es.thalesalv.chatrpg.core.application.query.persona;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.domain.persona.PersonaRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@UseCaseHandler
@RequiredArgsConstructor
public class SearchPersonasWithReadAccessHandler extends AbstractUseCaseHandler<SearchPersonasWithReadAccess, SearchPersonasResult> {

    private final PersonaRepository repository;

    @Override
    public SearchPersonasResult execute(SearchPersonasWithReadAccess query) {

        return repository.searchPersonasWithReadAccess(query);
    }
}
