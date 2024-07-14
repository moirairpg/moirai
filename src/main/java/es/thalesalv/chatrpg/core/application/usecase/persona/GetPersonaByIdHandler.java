package es.thalesalv.chatrpg.core.application.usecase.persona;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.application.usecase.persona.request.GetPersonaById;
import es.thalesalv.chatrpg.core.application.usecase.persona.result.GetPersonaResult;
import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaService;

@UseCaseHandler
public class GetPersonaByIdHandler extends AbstractUseCaseHandler<GetPersonaById, GetPersonaResult> {

    private final PersonaService domainService;

    public GetPersonaByIdHandler(PersonaService domainService) {
        this.domainService = domainService;
    }

    @Override
    public GetPersonaResult execute(GetPersonaById query) {

        Persona persona = domainService.getPersonaById(query);

        return mapResult(persona);
    }

    private GetPersonaResult mapResult(Persona persona) {

        return GetPersonaResult.builder()
                .id(persona.getId())
                .name(persona.getName())
                .personality(persona.getName())
                .visibility(persona.getVisibility().name())
                .bumpContent(persona.getBump().getContent())
                .bumpRole(persona.getBump().getRole().name())
                .bumpFrequency(persona.getBump().getFrequency())
                .nudgeContent(persona.getNudge().getContent())
                .nudgeRole(persona.getNudge().getRole().name())
                .creationDate(persona.getCreationDate())
                .lastUpdateDate(persona.getLastUpdateDate())
                .ownerDiscordId(persona.getOwnerDiscordId())
                .usersAllowedToRead(persona.getUsersAllowedToRead())
                .usersAllowedToWrite(persona.getUsersAllowedToWrite())
                .build();
    }
}
