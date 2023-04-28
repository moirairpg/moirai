package es.thalesalv.chatrpg.application.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.adapters.data.entity.PersonaEntity;
import es.thalesalv.chatrpg.adapters.data.repository.PersonaRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.PersonaDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.chconfig.PersonaEntityToDTO;
import es.thalesalv.chatrpg.domain.enums.Intent;
import es.thalesalv.chatrpg.domain.enums.Visibility;
import es.thalesalv.chatrpg.domain.exception.InsufficientPermissionException;
import es.thalesalv.chatrpg.domain.exception.PersonaNotFoundException;
import es.thalesalv.chatrpg.domain.model.chconf.Bump;
import es.thalesalv.chatrpg.domain.model.chconf.Nudge;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.SelfUser;

@ExtendWith(MockitoExtension.class)
public class PersonaServiceTest {

    @Mock
    private JDA jda;

    @Mock
    private PersonaRepository personaRepository;

    private PersonaDTOToEntity personaDTOToEntity;
    private PersonaEntityToDTO personaEntityToDTO;
    private PersonaService personaService;

    private static final String NANO_ID = "241OZASGM6CESV7";

    @BeforeEach
    public void beforeEach() {

        personaDTOToEntity = new PersonaDTOToEntity();
        personaEntityToDTO = new PersonaEntityToDTO();
        personaService = new PersonaService(jda, personaDTOToEntity, personaEntityToDTO, personaRepository);
    }

    @Test
    public void insertPersonaTest() {

        final PersonaEntity entity = buildSimplePublicPersonaEntity();
        final SelfUser bot = Mockito.mock(SelfUser.class);
        Mockito.when(jda.getSelfUser())
                .thenReturn(bot);

        Mockito.when(personaRepository.save(buildSimplePublicPersonaEntity()))
                .thenReturn(entity);

        final Persona persona = personaService.savePersona(buildSimplePublicPersona());
        Assertions.assertEquals("Test persona", persona.getName());
        Assertions.assertEquals("This is a test persona", persona.getPersonality());
    }

    @Test
    public void retrieveAllPersonasTest() {

        final String userId = "302796314822049793";
        final List<Persona> completeList = buildSimplePublicPersonaList();
        Mockito.when(personaRepository.findAll())
                .thenReturn(buildSimplePublicPersonaEntityList());

        final List<Persona> filteredList = personaService.retrieveAllPersonas(userId);
        Assertions.assertEquals(8, filteredList.size());
        Assertions.assertEquals(10, completeList.size());

        filteredList.forEach(p -> {
            Assertions.assertTrue(hasReadPermissions(p, userId));
        });
    }

    @Test
    public void updatePersonaTest_shouldWork() {

        final String userId = "302796314822049793";
        final Persona persona = buildSimplePublicPersona();
        final PersonaEntity entity = buildSimplePublicPersonaEntity();
        final SelfUser bot = Mockito.mock(SelfUser.class);

        Mockito.when(personaRepository.findById(NANO_ID))
                .thenReturn(Optional.of(entity));

        persona.setOwner(userId);
        entity.setOwner(userId);

        Mockito.when(jda.getSelfUser())
                .thenReturn(bot);

        Mockito.when(personaRepository.save(entity))
                .thenReturn(entity);

        final Persona result = personaService.updatePersona(NANO_ID, persona, userId);
        Assertions.assertEquals(persona, result);
    }

    @Test
    public void updatePersonaTest_insufficientPermissions() {

        final String userId = "302796314822049793";
        final Persona persona = buildSimplePublicPersona();
        final PersonaEntity entity = buildSimplePublicPersonaEntity();

        Mockito.when(personaRepository.findById(NANO_ID))
                .thenReturn(Optional.of(entity));

        final InsufficientPermissionException thrown = Assertions.assertThrows(InsufficientPermissionException.class,
                () -> personaService.updatePersona(NANO_ID, persona, userId));

        Assertions.assertEquals("Not enough permissions to modify this persona", thrown.getMessage());
        Assertions.assertFalse(hasWritePermissions(persona, userId));
    }

    @Test
    public void updatePersonaTest_notFound() {

        final String userId = "302796314822049793";
        final Persona persona = buildSimplePublicPersona();

        final PersonaNotFoundException thrown = Assertions.assertThrows(PersonaNotFoundException.class,
                () -> personaService.updatePersona(NANO_ID, persona, userId));

        Assertions.assertEquals(
                "Error updating persona: persona with id 241OZASGM6CESV7 could not be found in database.",
                thrown.getMessage());
    }

    @Test
    public void deletePersonaTest_shouldWork() {

        final String userId = "302796314822049793";
        final Persona persona = buildSimplePublicPersona();
        final PersonaEntity entity = buildSimplePublicPersonaEntity();

        persona.setOwner(userId);
        entity.setOwner(userId);

        Mockito.when(personaRepository.findById(NANO_ID))
                .thenReturn(Optional.of(entity));

        Mockito.doNothing()
                .when(personaRepository)
                .delete(entity);

        personaService.deletePersona(NANO_ID, userId);
    }

    @Test
    public void deletePersonaTest_insufficientPermissions() {

        final String userId = "302796314822049793";
        final Persona persona = buildSimplePublicPersona();
        final PersonaEntity entity = buildSimplePublicPersonaEntity();

        Mockito.when(personaRepository.findById(NANO_ID))
                .thenReturn(Optional.of(entity));

        final InsufficientPermissionException thrown = Assertions.assertThrows(InsufficientPermissionException.class,
                () -> personaService.deletePersona(NANO_ID, userId));

        Assertions.assertEquals("Not enough permissions to delete this persona", thrown.getMessage());
        Assertions.assertFalse(hasWritePermissions(persona, userId));
    }

    @Test
    public void deletePersonaTest_personaNotFound() {

        final String userId = "302796314822049793";
        final Persona persona = buildSimplePublicPersona();

        Mockito.when(personaRepository.findById(NANO_ID))
                .thenReturn(Optional.empty());

        final PersonaNotFoundException thrown = Assertions.assertThrows(PersonaNotFoundException.class,
                () -> personaService.deletePersona(NANO_ID, userId));

        Assertions.assertEquals(
                "Error deleting persona: persona with id 241OZASGM6CESV7 could not be found in database.",
                thrown.getMessage());

        Assertions.assertFalse(hasWritePermissions(persona, userId));
    }

    private Persona buildSimplePublicPersona() {

        return Persona.builder()
                .id(NANO_ID)
                .name("Test persona")
                .personality("This is a test persona")
                .intent(Intent.CHAT)
                .owner("1083867535658725536")
                .visibility("public")
                .nudge(Nudge.builder()
                        .role("system")
                        .content("this is a nugde")
                        .build())
                .bump(Bump.builder()
                        .frequency(3)
                        .role("system")
                        .content("this is a bump")
                        .build())
                .build();
    }

    private List<Persona> buildSimplePublicPersonaList() {

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

    private List<PersonaEntity> buildSimplePublicPersonaEntityList() {

        return buildSimplePublicPersonaList().stream()
                .map(personaDTOToEntity)
                .collect(Collectors.toList());
    }

    private PersonaEntity buildSimplePublicPersonaEntity() {

        return personaDTOToEntity.apply(buildSimplePublicPersona());
    }

    private boolean hasReadPermissions(final Persona persona, final String userId) {

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

    private boolean hasWritePermissions(final Persona persona, final String userId) {

        final List<String> writePermissions = Optional.ofNullable(persona.getWritePermissions())
                .orElse(Collections.emptyList());

        final boolean isOwner = persona.getOwner()
                .equals(userId);

        final boolean canWrite = writePermissions.contains(userId);
        return isOwner || canWrite;
    }
}
