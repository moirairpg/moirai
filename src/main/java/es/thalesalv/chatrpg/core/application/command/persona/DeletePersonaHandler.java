package es.thalesalv.chatrpg.core.application.command.persona;

import org.apache.commons.lang3.StringUtils;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.domain.persona.PersonaService;
import lombok.RequiredArgsConstructor;

@UseCaseHandler
@RequiredArgsConstructor
public class DeletePersonaHandler extends AbstractUseCaseHandler<DeletePersona, Void> {

    private static final String ID_CANNOT_BE_NULL_OR_EMPTY = "Persona ID cannot be null or empty";

    private final PersonaService domainService;

    @Override
    public void validate(DeletePersona command) {

        if (StringUtils.isBlank(command.getId())) {
            throw new IllegalArgumentException(ID_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    @Override
    public Void execute(DeletePersona command) {

        domainService.deletePersona(command);
        return null;
    }
}
