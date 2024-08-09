package me.moirai.discordbot.infrastructure.inbound.api.request;

import me.moirai.discordbot.core.domain.persona.Persona;
import me.moirai.discordbot.core.domain.persona.PersonaFixture;

public class CreatePersonaRequestFixture {

    public static CreatePersonaRequest createPrivatePersona() {

        Persona persona = PersonaFixture.privatePersona().build();
        CreatePersonaRequest request = new CreatePersonaRequest();

        request.setName(persona.getName());
        request.setPersonality(persona.getPersonality());
        request.setVisibility(persona.getVisibility().toString());
        request.setUsersAllowedToRead(persona.getUsersAllowedToRead());
        request.setUsersAllowedToWrite(persona.getUsersAllowedToWrite());
        request.setNudgeContent(persona.getNudge().getContent());
        request.setNudgeRole(persona.getNudge().getRole().toString());
        request.setBumpContent(persona.getBump().getContent());
        request.setBumpRole(persona.getBump().getRole().toString());
        request.setBumpFrequency(persona.getBump().getFrequency());

        return request;
    }
}
