package me.moirai.discordbot.infrastructure.inbound.api.request;

import me.moirai.discordbot.core.domain.persona.Persona;
import me.moirai.discordbot.core.domain.persona.PersonaFixture;

public class UpdatePersonaRequestFixture {

    public static UpdatePersonaRequest privatePersona() {

        Persona persona = PersonaFixture.privatePersona().build();
        UpdatePersonaRequest request = new UpdatePersonaRequest();

        request.setName(persona.getName());
        request.setPersonality(persona.getPersonality());
        request.setVisibility(persona.getVisibility().toString());
        request.setUsersAllowedToReadToAdd(persona.getUsersAllowedToRead());
        request.setUsersAllowedToWriteToAdd(persona.getUsersAllowedToWrite());
        request.setNudgeContent(persona.getNudge().getContent());
        request.setNudgeRole(persona.getNudge().getRole().toString());
        request.setBumpContent(persona.getBump().getContent());
        request.setBumpRole(persona.getBump().getRole().toString());
        request.setBumpFrequency(persona.getBump().getFrequency());

        return request;
    }
}
