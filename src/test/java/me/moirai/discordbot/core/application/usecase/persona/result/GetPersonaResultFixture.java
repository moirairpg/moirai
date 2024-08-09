package me.moirai.discordbot.core.application.usecase.persona.result;

import me.moirai.discordbot.core.domain.persona.Persona;
import me.moirai.discordbot.core.domain.persona.PersonaFixture;

public class GetPersonaResultFixture {

    public static GetPersonaResult.Builder privatePersona() {

        Persona persona = PersonaFixture.privatePersona().build();
        return GetPersonaResult.builder()
                .id(persona.getId())
                .name(persona.getName())
                .personality(persona.getPersonality())
                .visibility(persona.getVisibility().name())
                .usersAllowedToRead(persona.getUsersAllowedToRead())
                .usersAllowedToWrite(persona.getUsersAllowedToWrite())
                .creationDate(persona.getCreationDate())
                .lastUpdateDate(persona.getLastUpdateDate())
                .ownerDiscordId(persona.getOwnerDiscordId())
                .nudgeContent(persona.getNudge().getContent())
                .nudgeRole(persona.getNudge().getRole().toString())
                .bumpContent(persona.getBump().getContent())
                .bumpRole(persona.getBump().getRole().toString())
                .bumpFrequency(persona.getBump().getFrequency());
    }
}
