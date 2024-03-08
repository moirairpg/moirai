package es.thalesalv.chatrpg.core.application.query.persona;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.cqrs.query.QueryHandler;
import es.thalesalv.chatrpg.core.domain.persona.PersonaRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchPersonasHandler extends QueryHandler<SearchPersonas, SearchPersonasResult> {

    private final PersonaRepository repository;

    @Override
    public SearchPersonasResult handle(SearchPersonas query) {

        return repository.searchPersonas(query);
    }
}
