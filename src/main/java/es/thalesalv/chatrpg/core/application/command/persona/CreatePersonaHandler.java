package es.thalesalv.chatrpg.core.application.command.persona;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.cqrs.command.CommandHandler;
import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaDomainService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CreatePersonaHandler extends CommandHandler<CreatePersona, CreatePersonaResult> {

    private final PersonaDomainService domainService;

    @Override
    public CreatePersonaResult handle(CreatePersona command) {

        Persona world = domainService.createFrom(command);
        return CreatePersonaResult.build(world.getId());
    }
}
