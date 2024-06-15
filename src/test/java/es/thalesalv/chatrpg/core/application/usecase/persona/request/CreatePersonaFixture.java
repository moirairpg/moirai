package es.thalesalv.chatrpg.core.application.usecase.persona.request;

import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaFixture;

public class CreatePersonaFixture {

    public static CreatePersona.Builder createPrivatePersona() {

        Persona persona = PersonaFixture.privatePersona().build();
        return CreatePersona.builder()
                .name(persona.getName())
                .personality(persona.getPersonality())
                .visibility(persona.getVisibility().toString())
                .requesterDiscordId(persona.getOwnerDiscordId())
                .usersAllowedToWrite(persona.getUsersAllowedToRead())
                .usersAllowedToRead(persona.getUsersAllowedToWrite())
                .nudgeContent(persona.getNudge().getContent())
                .nudgeRole(persona.getNudge().getRole().toString())
                .bumpContent(persona.getBump().getContent())
                .bumpRole(persona.getBump().getRole().toString())
                .bumpFrequency(persona.getBump().getFrequency());
    }
}
