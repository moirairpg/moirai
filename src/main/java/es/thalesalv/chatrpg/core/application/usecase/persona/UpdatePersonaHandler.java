package es.thalesalv.chatrpg.core.application.usecase.persona;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.application.usecase.persona.request.UpdatePersona;
import es.thalesalv.chatrpg.core.application.usecase.persona.result.UpdatePersonaResult;
import es.thalesalv.chatrpg.core.domain.persona.PersonaService;
import reactor.core.publisher.Mono;

@UseCaseHandler
public class UpdatePersonaHandler extends AbstractUseCaseHandler<UpdatePersona, Mono<UpdatePersonaResult>> {

    private final PersonaService domainService;

    public UpdatePersonaHandler(PersonaService domainService) {
        this.domainService = domainService;
    }

    @Override
    public Mono<UpdatePersonaResult> execute(UpdatePersona command) {

        return domainService.update(command)
                .map(personaUpdated -> UpdatePersonaResult.build(personaUpdated.getLastUpdateDate()));
    }
}
