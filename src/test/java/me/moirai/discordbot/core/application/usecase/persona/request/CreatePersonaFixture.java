package me.moirai.discordbot.core.application.usecase.persona.request;

import me.moirai.discordbot.core.domain.persona.Persona;
import me.moirai.discordbot.core.domain.persona.PersonaFixture;

public class CreatePersonaFixture {

    public static CreatePersona.Builder createPrivatePersona() {

        Persona persona = PersonaFixture.privatePersona().build();
        return CreatePersona.builder()
                .name(persona.getName())
                .personality(persona.getPersonality())
                .visibility(persona.getVisibility().toString())
                .requesterDiscordId(persona.getOwnerDiscordId())
                .usersAllowedToWrite(persona.getUsersAllowedToRead())
                .usersAllowedToRead(persona.getUsersAllowedToWrite());
    }
}
