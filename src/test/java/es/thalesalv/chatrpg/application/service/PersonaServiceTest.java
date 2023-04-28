package es.thalesalv.chatrpg.application.service;

import static es.thalesalv.chatrpg.testutils.PersonaTestUtils.buildSimplePublicPersona;
import static es.thalesalv.chatrpg.testutils.PersonaTestUtils.buildSimplePublicPersonaEntity;
import static es.thalesalv.chatrpg.testutils.PersonaTestUtils.buildSimplePublicPersonaEntityList;
import static es.thalesalv.chatrpg.testutils.PersonaTestUtils.buildSimplePublicPersonaList;
import static es.thalesalv.chatrpg.testutils.PersonaTestUtils.hasReadPermissions;
import static es.thalesalv.chatrpg.testutils.PersonaTestUtils.hasWritePermissions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.adapters.data.entity.PersonaEntity;
import es.thalesalv.chatrpg.adapters.data.repository.PersonaRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.PersonaDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.chconfig.PersonaEntityToDTO;
import es.thalesalv.chatrpg.domain.exception.InsufficientPermissionException;
import es.thalesalv.chatrpg.domain.exception.PersonaNotFoundException;
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
        final SelfUser bot = mock(SelfUser.class);

        when(jda.getSelfUser()).thenReturn(bot);
        when(personaRepository.save(buildSimplePublicPersonaEntity())).thenReturn(entity);

        final Persona persona = personaService.savePersona(buildSimplePublicPersona());
        assertEquals("Test persona", persona.getName());
        assertEquals("This is a test persona", persona.getPersonality());
    }

    @Test
    public void retrieveAllPersonasTest() {

        final String userId = "302796314822049793";
        final List<Persona> completeList = buildSimplePublicPersonaList();

        when(personaRepository.findAll()).thenReturn(buildSimplePublicPersonaEntityList());

        final List<Persona> filteredList = personaService.retrieveAllPersonas(userId);
        assertEquals(8, filteredList.size());
        assertEquals(10, completeList.size());

        filteredList.forEach(p -> {
            assertTrue(hasReadPermissions(p, userId));
        });
    }

    @Test
    public void updatePersonaTest_shouldWork() {

        final String userId = "302796314822049793";
        final Persona persona = buildSimplePublicPersona();
        final PersonaEntity entity = buildSimplePublicPersonaEntity();
        final SelfUser bot = mock(SelfUser.class);

        persona.setOwner(userId);
        entity.setOwner(userId);

        when(personaRepository.findById(NANO_ID)).thenReturn(Optional.of(entity));
        when(jda.getSelfUser()).thenReturn(bot);
        when(personaRepository.save(entity)).thenReturn(entity);

        final Persona result = personaService.updatePersona(NANO_ID, persona, userId);
        assertEquals(persona, result);
    }

    @Test
    public void updatePersonaTest_insufficientPermissions() {

        final String userId = "302796314822049793";
        final Persona persona = buildSimplePublicPersona();
        final PersonaEntity entity = buildSimplePublicPersonaEntity();

        when(personaRepository.findById(NANO_ID)).thenReturn(Optional.of(entity));

        final InsufficientPermissionException thrown = assertThrows(InsufficientPermissionException.class,
                () -> personaService.updatePersona(NANO_ID, persona, userId));

        assertEquals("Not enough permissions to modify this persona", thrown.getMessage());
        assertFalse(hasWritePermissions(persona, userId));
    }

    @Test
    public void updatePersonaTest_notFound() {

        final String userId = "302796314822049793";
        final Persona persona = buildSimplePublicPersona();
        final PersonaNotFoundException thrown = assertThrows(PersonaNotFoundException.class,
                () -> personaService.updatePersona(NANO_ID, persona, userId));

        assertEquals("Error updating persona: persona with id 241OZASGM6CESV7 could not be found in database.",
                thrown.getMessage());
    }

    @Test
    public void deletePersonaTest_shouldWork() {

        final String userId = "302796314822049793";
        final Persona persona = buildSimplePublicPersona();
        final PersonaEntity entity = buildSimplePublicPersonaEntity();

        persona.setOwner(userId);
        entity.setOwner(userId);

        when(personaRepository.findById(NANO_ID)).thenReturn(Optional.of(entity));
        doNothing().when(personaRepository)
                .delete(entity);

        personaService.deletePersona(NANO_ID, userId);
    }

    @Test
    public void deletePersonaTest_insufficientPermissions() {

        final String userId = "302796314822049793";
        final Persona persona = buildSimplePublicPersona();
        final PersonaEntity entity = buildSimplePublicPersonaEntity();

        when(personaRepository.findById(NANO_ID)).thenReturn(Optional.of(entity));

        final InsufficientPermissionException thrown = assertThrows(InsufficientPermissionException.class,
                () -> personaService.deletePersona(NANO_ID, userId));

        assertEquals("Not enough permissions to delete this persona", thrown.getMessage());
        assertFalse(hasWritePermissions(persona, userId));
    }

    @Test
    public void deletePersonaTest_personaNotFound() {

        final String userId = "302796314822049793";
        final Persona persona = buildSimplePublicPersona();

        when(personaRepository.findById(NANO_ID)).thenReturn(Optional.empty());

        final PersonaNotFoundException thrown = assertThrows(PersonaNotFoundException.class,
                () -> personaService.deletePersona(NANO_ID, userId));

        assertEquals("Error deleting persona: persona with id 241OZASGM6CESV7 could not be found in database.",
                thrown.getMessage());

        assertFalse(hasWritePermissions(persona, userId));
    }
}
