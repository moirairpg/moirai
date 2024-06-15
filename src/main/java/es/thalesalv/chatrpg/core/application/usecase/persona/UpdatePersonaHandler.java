package es.thalesalv.chatrpg.core.application.usecase.persona;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.application.usecase.persona.request.UpdatePersona;
import es.thalesalv.chatrpg.core.application.usecase.persona.result.UpdatePersonaResult;
import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaService;
import lombok.RequiredArgsConstructor;

@UseCaseHandler
@RequiredArgsConstructor
public class UpdatePersonaHandler extends AbstractUseCaseHandler<UpdatePersona, UpdatePersonaResult> {

    private final PersonaService domainService;

    @Override
    public UpdatePersonaResult execute(UpdatePersona command) {

        return mapResult(domainService.update(command));
    }

    private UpdatePersonaResult mapResult(Persona savedPersona) {

        return UpdatePersonaResult.build(savedPersona.getLastUpdateDate());
    }
}
