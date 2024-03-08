package es.thalesalv.chatrpg.core.application.command.persona;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.exception.AssetNotFoundException;
import es.thalesalv.chatrpg.common.usecases.UseCaseHandler;
import es.thalesalv.chatrpg.core.domain.persona.PersonaRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DeletePersonaHandler extends UseCaseHandler<DeletePersona, Void> {

    private static final String ID_CANNOT_BE_NULL_OR_EMPTY = "Persona ID cannot be null or empty";
    private static final String CANNOT_DELETE_NOT_FOUND = "Cannot delete non-existing persona";

    private final PersonaRepository repository;

    @Override
    public void validate(DeletePersona command) {

        if (StringUtils.isBlank(command.getId())) {
            throw new IllegalArgumentException(ID_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    @Override
    public Void execute(DeletePersona command) {

        // TODO add ownership check
        repository.findById(command.getId())
                .orElseThrow(() -> new AssetNotFoundException(CANNOT_DELETE_NOT_FOUND));

        repository.deleteById(command.getId());

        return null;
    }
}
