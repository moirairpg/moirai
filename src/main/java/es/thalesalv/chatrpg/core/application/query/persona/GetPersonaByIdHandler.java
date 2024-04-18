package es.thalesalv.chatrpg.core.application.query.persona;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaService;
import lombok.RequiredArgsConstructor;

@UseCaseHandler
@RequiredArgsConstructor
public class GetPersonaByIdHandler extends AbstractUseCaseHandler<GetPersonaById, GetPersonaResult> {

    private final PersonaService domainService;

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
