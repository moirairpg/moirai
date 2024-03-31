package es.thalesalv.chatrpg.core.application.query.persona;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.usecases.UseCaseHandler;
import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaDomainService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GetPersonaByIdHandler extends UseCaseHandler<GetPersonaById, GetPersonaResult> {

    private final PersonaDomainService domainService;

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
