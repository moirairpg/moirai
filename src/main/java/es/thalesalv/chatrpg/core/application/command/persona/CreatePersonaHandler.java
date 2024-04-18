package es.thalesalv.chatrpg.core.application.command.persona;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.usecases.UseCaseHandler;
import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CreatePersonaHandler extends UseCaseHandler<CreatePersona, CreatePersonaResult> {

    private final PersonaService domainService;

    @Override
    public CreatePersonaResult execute(CreatePersona command) {

        Persona world = domainService.createFrom(command);
        return CreatePersonaResult.build(world.getId());
    }
}
