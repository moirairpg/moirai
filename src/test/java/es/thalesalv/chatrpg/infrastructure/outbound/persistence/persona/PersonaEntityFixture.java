package es.thalesalv.chatrpg.infrastructure.outbound.persistence.persona;

import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaFixture;

public class PersonaEntityFixture {

    public static PersonaEntity.Builder publicPersona() {

        Persona persona = PersonaFixture.publicPersona().build();

        PersonaEntity.Builder builder = PersonaEntity.builder();
        builder.id(persona.getId());
        builder.name(persona.getName());
        builder.personality(persona.getPersonality());
        builder.visibility(persona.getVisibility().toString());
        builder.gameMode(persona.getGameMode().name());
        builder.ownerDiscordId(persona.getOwnerDiscordId());
        builder.creatorDiscordId(persona.getCreatorDiscordId());
        builder.usersAllowedToRead(persona.getReaderUsers());
        builder.usersAllowedToWrite(persona.getWriterUsers());

        NudgeEntity nudge = NudgeEntity.builder()
                .content(persona.getNudge().getContent())
                .role(persona.getNudge().getRole().toString())
                .build();

        builder.nudge(nudge);

        BumpEntity bump = BumpEntity.builder()
                .content(persona.getBump().getContent())
                .role(persona.getBump().getRole().toString())
                .frequency(persona.getBump().getFrequency())
                .build();

        builder.bump(bump);

        return builder;
    }

    public static PersonaEntity.Builder privatePersona() {

        Persona persona = PersonaFixture.privatePersona().build();

        PersonaEntity.Builder builder = PersonaEntity.builder();
        builder.id(persona.getId());
        builder.name(persona.getName());
        builder.personality(persona.getPersonality());
        builder.visibility(persona.getVisibility().toString());
        builder.gameMode(persona.getGameMode().name());
        builder.ownerDiscordId(persona.getOwnerDiscordId());
        builder.creatorDiscordId(persona.getCreatorDiscordId());
        builder.usersAllowedToRead(persona.getReaderUsers());
        builder.usersAllowedToWrite(persona.getWriterUsers());

        NudgeEntity nudge = NudgeEntity.builder()
                .content(persona.getNudge().getContent())
                .role(persona.getNudge().getRole().toString())
                .build();

        builder.nudge(nudge);

        BumpEntity bump = BumpEntity.builder()
                .content(persona.getBump().getContent())
                .role(persona.getBump().getRole().toString())
                .frequency(persona.getBump().getFrequency())
                .build();

        builder.bump(bump);

        return builder;
    }
}
