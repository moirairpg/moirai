package es.thalesalv.chatrpg.core.application.query.persona;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.usecases.UseCaseHandler;
import es.thalesalv.chatrpg.core.domain.persona.PersonaRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchPersonasHandler extends UseCaseHandler<SearchPersonas, SearchPersonasResult> {

    private final PersonaRepository repository;

    @Override
    public SearchPersonasResult execute(SearchPersonas query) {

        // TODO extract real ID from principal when API is ready
        return repository.searchPersonas(query, "owner");
    }
}
