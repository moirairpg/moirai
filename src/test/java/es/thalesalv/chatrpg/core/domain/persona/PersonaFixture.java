package es.thalesalv.chatrpg.core.domain.persona;

import java.time.OffsetDateTime;

import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.PermissionsFixture;
import es.thalesalv.chatrpg.core.domain.Visibility;

public class PersonaFixture {

    private static final String OWNER_DISCORD_ID = "586678721356875";

    public static Persona.Builder publicPersona() {

        Persona.Builder builder = Persona.builder();
        builder.id("857345HAA");
        builder.name("ChatRPG");
        builder.personality("I am a Discord chatbot");
        builder.visibility(Visibility.fromString("PUBLIC"));
        builder.gameMode(GameMode.fromString("RPG"));
        builder.creatorDiscordId(OWNER_DISCORD_ID);
        builder.creationDate(OffsetDateTime.now());
        builder.lastUpdateDate(OffsetDateTime.now());
        builder.gameMode(GameMode.RPG);

        Permissions permissions = PermissionsFixture.samplePermissions().build();
        builder.permissions(permissions);

        Nudge nudge = NudgeFixture.sample().build();
        builder.nudge(nudge);

        Bump bump = BumpFixture.sample().build();
        builder.bump(bump);

        return builder;
    }

    public static Persona.Builder privatePersona() {

        Persona.Builder builder = Persona.builder();
        builder.id("857345HAA");
        builder.name("ChatRPG");
        builder.personality("I am a Discord chatbot");
        builder.visibility(Visibility.fromString("PRIVATE"));
        builder.gameMode(GameMode.fromString("RPG"));
        builder.creatorDiscordId(OWNER_DISCORD_ID);
        builder.creationDate(OffsetDateTime.now());
        builder.lastUpdateDate(OffsetDateTime.now());
        builder.gameMode(GameMode.RPG);

        Permissions permissions = PermissionsFixture.samplePermissions().build();
        builder.permissions(permissions);

        Nudge nudge = NudgeFixture.sample().build();
        builder.nudge(nudge);

        Bump bump = BumpFixture.sample().build();
        builder.bump(bump);

        return builder;
    }
}
