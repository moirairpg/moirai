package es.thalesalv.chatrpg.core.domain.persona;

import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.PermissionsFixture;
import es.thalesalv.chatrpg.core.domain.Visibility;

public class PersonaFixture {

    public static Persona.Builder publicPersona() {

        Persona.Builder builder = Persona.builder();
        builder.id("857345HAA");
        builder.name("ChatRPG");
        builder.personality("I am a Discord chatbot");
        builder.visibility(Visibility.fromString("PUBLIC"));

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

        Permissions permissions = PermissionsFixture.samplePermissions().build();
        builder.permissions(permissions);

        Nudge nudge = NudgeFixture.sample().build();
        builder.nudge(nudge);

        Bump bump = BumpFixture.sample().build();
        builder.bump(bump);

        return builder;
    }
}
