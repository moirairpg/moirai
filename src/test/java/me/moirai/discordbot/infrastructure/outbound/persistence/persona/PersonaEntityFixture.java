package me.moirai.discordbot.infrastructure.outbound.persistence.persona;

import me.moirai.discordbot.core.domain.persona.Persona;
import me.moirai.discordbot.core.domain.persona.PersonaFixture;

public class PersonaEntityFixture {

    public static PersonaEntity.Builder publicPersona() {

        Persona persona = PersonaFixture.publicPersona().build();
        return PersonaEntity.builder()
                .id(persona.getId())
                .name(persona.getName())
                .personality(persona.getPersonality())
                .visibility(persona.getVisibility().toString())
                .ownerDiscordId(persona.getOwnerDiscordId())
                .creatorDiscordId(persona.getCreatorDiscordId())
                .usersAllowedToRead(persona.getUsersAllowedToRead())
                .usersAllowedToWrite(persona.getUsersAllowedToWrite());
    }

    public static PersonaEntity.Builder privatePersona() {

        Persona persona = PersonaFixture.privatePersona().build();
        return PersonaEntity.builder()
                .id(persona.getId())
                .name(persona.getName())
                .personality(persona.getPersonality())
                .visibility(persona.getVisibility().toString())
                .ownerDiscordId(persona.getOwnerDiscordId())
                .creatorDiscordId(persona.getCreatorDiscordId())
                .usersAllowedToRead(persona.getUsersAllowedToRead())
                .usersAllowedToWrite(persona.getUsersAllowedToWrite());
    }
}
