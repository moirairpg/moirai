package es.thalesalv.chatrpg.core.application.command.persona;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaService;
import lombok.RequiredArgsConstructor;

@UseCaseHandler
@RequiredArgsConstructor
public class CreatePersonaHandler extends AbstractUseCaseHandler<CreatePersona, CreatePersonaResult> {

    private final PersonaService domainService;

    @Override
    public CreatePersonaResult execute(CreatePersona command) {

        Persona world = domainService.createFrom(command);
        return CreatePersonaResult.build(world.getId());
    }
}
