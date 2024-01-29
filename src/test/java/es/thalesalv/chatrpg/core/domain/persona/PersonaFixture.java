package es.thalesalv.chatrpg.core.domain.persona;

import static es.thalesalv.chatrpg.core.domain.Visibility.PRIVATE;
import static es.thalesalv.chatrpg.core.domain.Visibility.PUBLIC;

import es.thalesalv.chatrpg.core.domain.PermissionsFixture;
import es.thalesalv.chatrpg.core.domain.Permissions;

public class PersonaFixture {

    public static Persona.Builder publicPersona() {

        Persona.Builder builder = Persona.builder();
        builder.id("857345HAA");
        builder.name("ChatRPG");
        builder.personality("I am a Discord chatbot");
        builder.visibility(PUBLIC);

        Permissions permissions = PermissionsFixture.samplePermissions().build();
        builder.permissions(permissions);

        return builder;
    }

    public static Persona.Builder privatePersona() {

        Persona.Builder builder = Persona.builder();
        builder.id("857345HAA");
        builder.name("ChatRPG");
        builder.personality("I am a Discord chatbot");
        builder.visibility(PRIVATE);

        Permissions permissions = PermissionsFixture.samplePermissions().build();
        builder.permissions(permissions);

        return builder;
    }
}
