package me.moirai.discordbot.core.application.usecase.persona;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.persona.request.UpdatePersona;
import me.moirai.discordbot.core.application.usecase.persona.result.UpdatePersonaResult;
import me.moirai.discordbot.core.domain.persona.PersonaService;
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
