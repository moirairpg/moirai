package es.thalesalv.chatrpg.core.domain.persona;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import es.thalesalv.chatrpg.common.exception.BusinessRuleViolationException;
import es.thalesalv.chatrpg.core.domain.CompletionRole;
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

    @Test
    public void updateBumpContent() {

        // Given
        String newBumpContent = "This is the new bump content";

        String oldBumpContent = "This is the old bump content";
        CompletionRole role = CompletionRole.ASSISTANT;
        int frequency = 20;

        Bump bump = BumpFixture.sample()
                .content(oldBumpContent)
                .role(role)
                .frequency(frequency)
                .build();

        Persona persona = PersonaFixture.privatePersona()
                .bump(bump)
                .build();

        // When
        persona.updateBumpContent(newBumpContent);

        // When
        assertThat(persona.getBump()).isNotNull();
        assertThat(persona.getBump().getContent()).isEqualTo(newBumpContent);
        assertThat(persona.getBump().getContent()).isNotEqualTo(oldBumpContent);
        assertThat(persona.getBump().getFrequency()).isEqualTo(frequency);
        assertThat(persona.getBump().getRole()).isEqualTo(role);
    }

    @Test
    public void updateBumpFrequency() {

        // Given
        int newFrequency = 50;

        String oldBumpContent = "This is the old bump content";
        CompletionRole role = CompletionRole.ASSISTANT;
        int oldFrequency = 20;

        Bump bump = BumpFixture.sample()
                .content(oldBumpContent)
                .role(role)
                .frequency(oldFrequency)
                .build();

        Persona persona = PersonaFixture.privatePersona()
                .bump(bump)
                .build();

        // When
        persona.updateBumpFrequency(newFrequency);

        // When
        assertThat(persona.getBump()).isNotNull();
        assertThat(persona.getBump().getContent()).isEqualTo(oldBumpContent);
        assertThat(persona.getBump().getFrequency()).isNotEqualTo(oldFrequency);
        assertThat(persona.getBump().getFrequency()).isEqualTo(newFrequency);
        assertThat(persona.getBump().getRole()).isEqualTo(role);
    }

    @Test
    public void updateBumpRole() {

        // Given
        CompletionRole newRole = CompletionRole.USER;

        String oldBumpContent = "This is the old bump content";
        CompletionRole oldRole = CompletionRole.ASSISTANT;
        int frequency = 20;

        Bump bump = BumpFixture.sample()
                .content(oldBumpContent)
                .role(oldRole)
                .frequency(frequency)
                .build();

        Persona persona = PersonaFixture.privatePersona()
                .bump(bump)
                .build();

        // When
        persona.updateBumpRole(newRole);

        // When
        assertThat(persona.getBump()).isNotNull();
        assertThat(persona.getBump().getFrequency()).isEqualTo(frequency);
        assertThat(persona.getBump().getRole()).isNotEqualTo(oldRole);
        assertThat(persona.getBump().getRole()).isEqualTo(newRole);
    }

    @Test
    public void updateNudgeContent() {

        // Given
        String newNudgeContent = "This is the new nudge content";

        String oldNudgeContent = "This is the old nudge content";
        CompletionRole role = CompletionRole.ASSISTANT;

        Nudge nudge = NudgeFixture.sample()
                .content(oldNudgeContent)
                .role(role)
                .build();

        Persona persona = PersonaFixture.privatePersona()
                .nudge(nudge)
                .build();

        // When
        persona.updateNudgeContent(newNudgeContent);

        // When
        assertThat(persona.getNudge()).isNotNull();
        assertThat(persona.getNudge().getContent()).isEqualTo(newNudgeContent);
        assertThat(persona.getNudge().getContent()).isNotEqualTo(oldNudgeContent);
        assertThat(persona.getNudge().getRole()).isEqualTo(role);
    }

    @Test
    public void updateNudgeRole() {

        // Given
        CompletionRole newRole = CompletionRole.USER;

        String oldNudgeContent = "This is the old nudge content";
        CompletionRole oldRole = CompletionRole.ASSISTANT;

        Nudge nudge = NudgeFixture.sample()
                .content(oldNudgeContent)
                .role(oldRole)
                .build();

        Persona persona = PersonaFixture.privatePersona()
                .nudge(nudge)
                .build();

        // When
        persona.updateNudgeRole(newRole);

        // When
        assertThat(persona.getNudge()).isNotNull();
        assertThat(persona.getNudge().getRole()).isNotEqualTo(oldRole);
        assertThat(persona.getNudge().getRole()).isEqualTo(newRole);
    }
}
