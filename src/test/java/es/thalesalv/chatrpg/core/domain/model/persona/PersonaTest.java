package es.thalesalv.chatrpg.core.domain.model.persona;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import es.thalesalv.chatrpg.common.exception.BusinessException;

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
        Persona persona = PersonaFixture.publicPersona().build();

        // When
        persona.updateName("New Name");

        // Then
        assertThat(persona.getName()).isEqualTo("New Name");
    }

    @Test
    public void updatePersonaPersonality() {

        // Given
        Persona persona = PersonaFixture.publicPersona().build();

        // When
        persona.updatePersonality("New Personality");

        // Then
        assertThat(persona.getPersonality()).isEqualTo("New Personality");
    }

    @Test
    public void errorWhenCreatingPersonaWithNullName() {

        // Given
        Persona.Builder personaBuilder = PersonaFixture.publicPersona().name(null);

        // Then
        assertThrows(BusinessException.class, personaBuilder::build);
    }

    @Test
    public void errorWhenCreatingPersonaWithEmptyName() {

        // Given
        Persona.Builder personaBuilder = PersonaFixture.publicPersona().name(EMPTY);

        // Then
        assertThrows(BusinessException.class, personaBuilder::build);
    }

    @Test
    public void errorWhenCreatingPersonaWithNullPersonality() {

        // Given
        Persona.Builder personaBuilder = PersonaFixture.publicPersona().personality(null);

        // Then
        assertThrows(BusinessException.class, personaBuilder::build);
    }

    @Test
    public void errorWhenCreatingPersonaWithEmptyPersonality() {

        // Given
        Persona.Builder personaBuilder = PersonaFixture.publicPersona().personality(EMPTY);

        // Then
        assertThrows(BusinessException.class, personaBuilder::build);
    }

    @Test
    public void errorWhenCreatingPersonaWithNullVisibility() {

        // Given
        Persona.Builder personaBuilder = PersonaFixture.publicPersona().permissions(null);

        // Then
        assertThrows(BusinessException.class, personaBuilder::build);
    }

    @Test
    public void errorWhenCreatingPersonaWithNullPermissions() {

        // Given
        Persona.Builder personaBuilder = PersonaFixture.publicPersona().visibility(null);

        // Then
        assertThrows(BusinessException.class, personaBuilder::build);
    }
}
