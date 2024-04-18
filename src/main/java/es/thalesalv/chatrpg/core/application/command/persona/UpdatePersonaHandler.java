package es.thalesalv.chatrpg.core.application.command.persona;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.usecases.UseCaseHandler;
import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdatePersonaHandler extends UseCaseHandler<UpdatePersona, UpdatePersonaResult> {

    private final PersonaService domainService;

    @Override
    public UpdatePersonaResult execute(UpdatePersona command) {

        return mapResult(domainService.update(command));
    }

    private UpdatePersonaResult mapResult(Persona savedPersona) {

        return UpdatePersonaResult.build(savedPersona.getLastUpdateDate());
    }
}
