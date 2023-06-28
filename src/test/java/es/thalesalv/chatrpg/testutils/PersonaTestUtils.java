package es.thalesalv.chatrpg.testutils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import es.thalesalv.chatrpg.adapters.data.entity.PersonaEntity;
import es.thalesalv.chatrpg.application.mapper.chconfig.PersonaDTOToEntity;
import es.thalesalv.chatrpg.domain.enums.Intent;
import es.thalesalv.chatrpg.domain.enums.Visibility;
import es.thalesalv.chatrpg.domain.model.chconf.Bump;
import es.thalesalv.chatrpg.domain.model.chconf.Nudge;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;

public class PersonaTestUtils {

    private static final PersonaDTOToEntity personaDTOToEntity = new PersonaDTOToEntity();
    private static final String NANO_ID = "241OZASGM6CESV7";

    public static Persona buildSimplePublicPersona() {

        final List<String> emptyPermissions = new ArrayList<>();
        return Persona.builder()
                .id(NANO_ID)
                .name("ChatRPG")
                .personality("This is a test persona. My name is {0}")
                .intent(Intent.CHAT)
                .owner("1083867535658725536")
                .visibility("public")
                .writePermissions(emptyPermissions)
                .readPermissions(emptyPermissions)
                .nudge(Nudge.builder()
                        .role("system")
                        .content("My name is {0} and this is a nudge")
                        .build())
                .bump(Bump.builder()
                        .frequency(3)
                        .role("system")
                        .content("this is a bump")
                        .build())
                .build();
    }

    public static List<Persona> buildSimplePublicPersonaList() {

        final List<String> permissions = new ArrayList<>();
        permissions.add("302796314822049793");

        final Persona privatePersona = buildSimplePublicPersona();
        final Persona someonesElsesPublicPersona = buildSimplePublicPersona();
        final Persona someonesElsesPrivatePersona = buildSimplePublicPersona();
        final Persona someonesElsesPrivatePersonaWithWritePersmissions = buildSimplePublicPersona();
        final Persona someonesElsesPrivatePersonaWithReadPermissions = buildSimplePublicPersona();
        final Persona someonesElsesPrivatePersonaWithAllPermissions = buildSimplePublicPersona();
        final Persona someonesElsesPublicPersonaWithAllPermissions = buildSimplePublicPersona();
        final Persona ownPublicPersonaWithAllPermissions = buildSimplePublicPersona();
        final Persona ownPrivatePersonaWithAllPermissions = buildSimplePublicPersona();

        privatePersona.setVisibility("private");
        someonesElsesPrivatePersona.setVisibility("private");
        someonesElsesPublicPersona.setOwner("463004243411075072");
        someonesElsesPrivatePersona.setOwner("463004243411075072");

        ownPublicPersonaWithAllPermissions.setWritePermissions(permissions);
        ownPublicPersonaWithAllPermissions.setReadPermissions(permissions);
        ownPublicPersonaWithAllPermissions.setOwner("302796314822049793");
        ownPublicPersonaWithAllPermissions.setVisibility("private");

        ownPrivatePersonaWithAllPermissions.setWritePermissions(permissions);
        ownPrivatePersonaWithAllPermissions.setReadPermissions(permissions);
        ownPrivatePersonaWithAllPermissions.setOwner("302796314822049793");
        ownPrivatePersonaWithAllPermissions.setVisibility("private");

        someonesElsesPrivatePersonaWithAllPermissions.setWritePermissions(permissions);
        someonesElsesPrivatePersonaWithAllPermissions.setReadPermissions(permissions);
        someonesElsesPrivatePersonaWithAllPermissions.setOwner("463004243411075072");
        someonesElsesPrivatePersonaWithAllPermissions.setVisibility("private");

        someonesElsesPublicPersonaWithAllPermissions.setWritePermissions(permissions);
        someonesElsesPublicPersonaWithAllPermissions.setReadPermissions(permissions);
        someonesElsesPublicPersonaWithAllPermissions.setOwner("463004243411075072");
        someonesElsesPublicPersonaWithAllPermissions.setVisibility("public");

        someonesElsesPrivatePersonaWithWritePersmissions.setVisibility("private");
        someonesElsesPrivatePersonaWithReadPermissions.setVisibility("private");
        someonesElsesPrivatePersonaWithWritePersmissions.setOwner("463004243411075072");
        someonesElsesPrivatePersonaWithReadPermissions.setOwner("463004243411075072");
        someonesElsesPrivatePersonaWithWritePersmissions.setWritePermissions(permissions);
        someonesElsesPrivatePersonaWithReadPermissions.setReadPermissions(permissions);

        final List<Persona> personas = new ArrayList<>();
        personas.add(buildSimplePublicPersona());
        personas.add(privatePersona);
        personas.add(someonesElsesPublicPersona);
        personas.add(someonesElsesPrivatePersona);
        personas.add(someonesElsesPrivatePersonaWithWritePersmissions);
        personas.add(someonesElsesPrivatePersonaWithReadPermissions);
        personas.add(someonesElsesPrivatePersonaWithAllPermissions);
        personas.add(someonesElsesPublicPersonaWithAllPermissions);
        personas.add(ownPublicPersonaWithAllPermissions);
        personas.add(ownPrivatePersonaWithAllPermissions);

        return personas;
    }

    public static List<PersonaEntity> buildSimplePublicPersonaEntityList() {

        return buildSimplePublicPersonaList().stream()
                .map(personaDTOToEntity)
                .collect(Collectors.toList());
    }

    public static PersonaEntity buildSimplePublicPersonaEntity() {

        return personaDTOToEntity.apply(buildSimplePublicPersona());
    }

    public static boolean hasReadPermissions(final Persona persona, final String userId) {

        final boolean isPublic = Visibility.isPublic(persona.getVisibility());
        final boolean isOwner = persona.getOwner()
                .equals(userId);

        final List<String> readPermissions = Optional.ofNullable(persona.getReadPermissions())
                .orElse(Collections.emptyList());

        final List<String> writePermissions = Optional.ofNullable(persona.getWritePermissions())
                .orElse(Collections.emptyList());

        final boolean canRead = readPermissions.contains(userId) || writePermissions.contains(userId);
        return isPublic || (isOwner || canRead);
    }

    public static boolean hasWritePermissions(final Persona persona, final String userId) {

        final List<String> writePermissions = Optional.ofNullable(persona.getWritePermissions())
                .orElse(Collections.emptyList());

        final boolean isOwner = persona.getOwner()
                .equals(userId);

        final boolean canWrite = writePermissions.contains(userId);
        return isOwner || canWrite;
    }
}
