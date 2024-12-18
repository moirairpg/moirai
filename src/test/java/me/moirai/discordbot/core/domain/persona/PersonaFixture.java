package me.moirai.discordbot.core.domain.persona;

import java.time.OffsetDateTime;

import me.moirai.discordbot.core.domain.Permissions;
import me.moirai.discordbot.core.domain.PermissionsFixture;
import me.moirai.discordbot.core.domain.Visibility;

public class PersonaFixture {

    private static final String OWNER_DISCORD_ID = "586678721356875";

    public static Persona.Builder publicPersona() {

        Permissions permissions = PermissionsFixture.samplePermissions().build();
        return Persona.builder()
                .id("857345HAA")
                .name("MoirAI")
                .personality("I am a Discord chatbot")
                .visibility(Visibility.fromString("PUBLIC"))
                .creatorDiscordId(OWNER_DISCORD_ID)
                .creationDate(OffsetDateTime.now())
                .lastUpdateDate(OffsetDateTime.now())
                .permissions(permissions);
    }

    public static Persona.Builder privatePersona() {

        Permissions permissions = PermissionsFixture.samplePermissions().build();
        return Persona.builder()
                .id("857345HAA")
                .name("MoirAI")
                .personality("I am a Discord chatbot")
                .visibility(Visibility.fromString("PRIVATE"))
                .creatorDiscordId(OWNER_DISCORD_ID)
                .creationDate(OffsetDateTime.now())
                .lastUpdateDate(OffsetDateTime.now())
                .permissions(permissions);
    }
}
