package es.thalesalv.chatrpg.core.domain.persona;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import es.thalesalv.chatrpg.common.exception.BusinessRuleViolationException;
import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.PermissionsFixture;

public class PersonaTest {

    @Test
    public void makePersonaPublic() {

        // Given
        Persona persona = PersonaFixture.privatePersona().build();

        // When
        persona.makePublic();

        // Then
        assertThat(persona.isPublic()).isTrue();
    }

    @Test
    public void makePersonaPrivate() {

        // Given
        Persona persona = PersonaFixture.publicPersona().build();

        // When
        persona.makePrivate();

        // Then
        assertThat(persona.isPublic()).isFalse();
    }

    @Test
    public void updatePersonaName() {

        // Given
        String name = "New Name";
        Persona persona = PersonaFixture.publicPersona().build();

        // When
        persona.updateName(name);

        // Then
        assertThat(persona.getName()).isEqualTo(name);
    }

    @Test
    public void updatePersonaPersonality() {

        // Given
        String personality = "New Personality";
        Persona persona = PersonaFixture.publicPersona().build();

        // When
        persona.updatePersonality(personality);

        // Then
        assertThat(persona.getPersonality()).isEqualTo(personality);
    }

    @Test
    public void errorWhenCreatingPersonaWithNullName() {

        // Given
        Persona.Builder personaBuilder = PersonaFixture.publicPersona().name(null);

        // Then
        assertThrows(BusinessRuleViolationException.class, personaBuilder::build);
    }

    @Test
    public void errorWhenCreatingPersonaWithEmptyName() {

        // Given
        Persona.Builder personaBuilder = PersonaFixture.publicPersona().name(EMPTY);

        // Then
        assertThrows(BusinessRuleViolationException.class, personaBuilder::build);
    }

    @Test
    public void errorWhenCreatingPersonaWithNullPersonality() {

        // Given
        Persona.Builder personaBuilder = PersonaFixture.publicPersona().personality(null);

        // Then
        assertThrows(BusinessRuleViolationException.class, personaBuilder::build);
    }

    @Test
    public void errorWhenCreatingPersonaWithEmptyPersonality() {

        // Given
        Persona.Builder personaBuilder = PersonaFixture.publicPersona().personality(EMPTY);

        // Then
        assertThrows(BusinessRuleViolationException.class, personaBuilder::build);
    }

    @Test
    public void errorWhenCreatingPersonaWithNullVisibility() {

        // Given
        Persona.Builder personaBuilder = PersonaFixture.publicPersona().permissions(null);

        // Then
        assertThrows(BusinessRuleViolationException.class, personaBuilder::build);
    }

    @Test
    public void errorWhenCreatingPersonaWithNullPermissions() {

        // Given
        Persona.Builder personaBuilder = PersonaFixture.publicPersona().visibility(null);

        // Then
        assertThrows(BusinessRuleViolationException.class, personaBuilder::build);
    }

    @Test
    public void addWriterToList() {

        // Given
        String userId = "1234567890";
        Persona.Builder personaBuilder = PersonaFixture.publicPersona();
        Permissions permissions = PermissionsFixture.samplePermissions()
                .usersAllowedToWrite(new ArrayList<>()).build();

        personaBuilder.permissions(permissions);

        Persona persona = personaBuilder.build();

        // When
        persona.addWriterUser(userId);

        // Then
        assertThat(persona.getWriterUsers()).contains(userId);
    }

    @Test
    public void addReaderToList() {

        // Given
        String userId = "1234567890";
        Persona.Builder personaBuilder = PersonaFixture.publicPersona();
        Permissions permissions = PermissionsFixture.samplePermissions()
                .usersAllowedToRead(new ArrayList<>()).build();

        personaBuilder.permissions(permissions);

        Persona persona = personaBuilder.build();

        // When
        persona.addReaderUser(userId);

        // Then
        assertThat(persona.getReaderUsers()).contains(userId);
    }

    @Test
    public void removeReaderFromList() {

        // Given
        String userId = "1234567890";
        Persona.Builder personaBuilder = PersonaFixture.publicPersona();

        List<String> usersAllowedToRead = new ArrayList<>();
        usersAllowedToRead.add(userId);

        Permissions permissions = PermissionsFixture.samplePermissions()
                .usersAllowedToRead(usersAllowedToRead).build();

        personaBuilder.permissions(permissions);

        Persona persona = personaBuilder.build();

        // When
        persona.removeReaderUser(userId);

        // Then
        assertThat(persona.getReaderUsers()).doesNotContain(userId);
    }

    @Test
    public void removeWriterFromList() {

        // Given
        String userId = "1234567890";
        Persona.Builder personaBuilder = PersonaFixture.publicPersona();

        List<String> usersAllowedToWrite = new ArrayList<>();
        usersAllowedToWrite.add(userId);

        Permissions permissions = PermissionsFixture.samplePermissions()
                .usersAllowedToWrite(usersAllowedToWrite).build();

        personaBuilder.permissions(permissions);

        Persona persona = personaBuilder.build();

        // When
        persona.removeWriterUser(userId);

        // Then
        assertThat(persona.getWriterUsers()).doesNotContain(userId);
    }
}
