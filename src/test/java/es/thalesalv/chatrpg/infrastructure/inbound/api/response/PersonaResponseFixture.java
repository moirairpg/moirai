package es.thalesalv.chatrpg.infrastructure.inbound.api.response;

import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaFixture;

public class PersonaResponseFixture {

    public static PersonaResponse.Builder publicPersona() {

        Persona persona = PersonaFixture.publicPersona().build();

        PersonaResponse.Builder builder = PersonaResponse.builder();
        builder.id(persona.getId());
        builder.name(persona.getName());
        builder.personality(persona.getPersonality());
        builder.visibility(persona.getVisibility().toString());
        builder.gameMode(persona.getGameMode().name());
        builder.ownerDiscordId(persona.getOwnerDiscordId());
        builder.readerUsers(persona.getReaderUsers());
        builder.writerUsers(persona.getWriterUsers());

        builder.nudgeContent(persona.getNudge().getContent());
        builder.nudgeRole(persona.getNudge().getRole().toString());

        builder.bumpContent(persona.getBump().getContent());
        builder.bumpRole(persona.getBump().getRole().toString());
        builder.bumpFrequency(persona.getBump().getFrequency());

        return builder;
    }

    public static PersonaResponse.Builder privatePersona() {

        Persona persona = PersonaFixture.privatePersona().build();

        PersonaResponse.Builder builder = PersonaResponse.builder();
        builder.id(persona.getId());
        builder.name(persona.getName());
        builder.personality(persona.getPersonality());
        builder.visibility(persona.getVisibility().toString());
        builder.gameMode(persona.getGameMode().name());
        builder.ownerDiscordId(persona.getOwnerDiscordId());
        builder.readerUsers(persona.getReaderUsers());
        builder.writerUsers(persona.getWriterUsers());

        builder.nudgeContent(persona.getNudge().getContent());
        builder.nudgeRole(persona.getNudge().getRole().toString());

        builder.bumpContent(persona.getBump().getContent());
        builder.bumpRole(persona.getBump().getRole().toString());
        builder.bumpFrequency(persona.getBump().getFrequency());

        return builder;
    }
}
