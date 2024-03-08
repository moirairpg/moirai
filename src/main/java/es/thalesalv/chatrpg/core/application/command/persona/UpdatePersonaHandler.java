package es.thalesalv.chatrpg.core.application.command.persona;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.cqrs.command.CommandHandler;
import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaDomainService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdatePersonaHandler extends CommandHandler<UpdatePersona, UpdatePersonaResult> {

    private final PersonaDomainService domainService;

    @Override
    public UpdatePersonaResult handle(UpdatePersona command) {

        return mapResult(domainService.update(command));
    }

    private UpdatePersonaResult mapResult(Persona savedPersona) {

        return UpdatePersonaResult.build(savedPersona.getLastUpdateDate());
    }
}
