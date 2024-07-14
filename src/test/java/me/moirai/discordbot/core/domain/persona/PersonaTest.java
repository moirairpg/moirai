package me.moirai.discordbot.core.domain.persona;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import me.moirai.discordbot.common.exception.BusinessRuleViolationException;
import me.moirai.discordbot.core.domain.CompletionRole;
import me.moirai.discordbot.core.domain.Permissions;
import me.moirai.discordbot.core.domain.PermissionsFixture;

public class PersonaTest {

    @Test
    public void updateVisibility_whenPrivate_thenMakePublic() {

        // Given
        Persona persona = PersonaFixture.privatePersona().build();

        // When
        persona.makePublic();

        // Then
        assertThat(persona.isPublic()).isTrue();
    }

    @Test
    public void updateVisibility_whenPublic_thenMakePrivate() {

        // Given
        Persona persona = PersonaFixture.publicPersona().build();

        // When
        persona.makePrivate();

        // Then
        assertThat(persona.isPublic()).isFalse();
    }

    @Test
    public void updatePersona_whenNewNameProvided_thenUpdatePersona() {

        // Given
        String name = "New Name";
        Persona persona = PersonaFixture.publicPersona().build();

        // When
        persona.updateName(name);

        // Then
        assertThat(persona.getName()).isEqualTo(name);
    }

    @Test
    public void updatePersona_whenNewPersonality_thenUpdatePersona() {

        // Given
        String personality = "New Personality";
        Persona persona = PersonaFixture.publicPersona().build();

        // When
        persona.updatePersonality(personality);

        // Then
        assertThat(persona.getPersonality()).isEqualTo(personality);
    }

    @Test
    public void createPersona_whenNullName_thenThrowException() {

        // Given
        Persona.Builder personaBuilder = PersonaFixture.publicPersona().name(null);

        // Then
        assertThrows(BusinessRuleViolationException.class, personaBuilder::build);
    }

    @Test
    public void createPersona_whenNullGameMode_thenThrowException() {

        // Given
        Persona.Builder personaBuilder = PersonaFixture.publicPersona().gameMode(null);

        // Then
        assertThrows(BusinessRuleViolationException.class, personaBuilder::build);
    }

    @Test
    public void createPersona_whenEmptyName_thenThrowException() {

        // Given
        Persona.Builder personaBuilder = PersonaFixture.publicPersona().name(EMPTY);

        // Then
        assertThrows(BusinessRuleViolationException.class, personaBuilder::build);
    }

    @Test
    public void createPersona_whenNullPersonality_thenThrowException() {

        // Given
        Persona.Builder personaBuilder = PersonaFixture.publicPersona().personality(null);

        // Then
        assertThrows(BusinessRuleViolationException.class, personaBuilder::build);
    }

    @Test
    public void createPersona_whenEmptyPersonality_thenThrowException() {

        // Given
        Persona.Builder personaBuilder = PersonaFixture.publicPersona().personality(EMPTY);

        // Then
        assertThrows(BusinessRuleViolationException.class, personaBuilder::build);
    }

    @Test
    public void createPersona_whenNullPermissions_thenThrowException() {

        // Given
        Persona.Builder personaBuilder = PersonaFixture.publicPersona().permissions(null);

        // Then
        assertThrows(BusinessRuleViolationException.class, personaBuilder::build);
    }

    @Test
    public void createPersona_whenEmptyVisibility_thenThrowException() {

        // Given
        Persona.Builder personaBuilder = PersonaFixture.publicPersona().visibility(null);

        // Then
        assertThrows(BusinessRuleViolationException.class, personaBuilder::build);
    }

    @Test
    public void updatePersona_whenNewWriterUserAdded_thenTheyShouldHaveReadAndWritePermission() {

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
        assertThat(persona.getUsersAllowedToWrite()).contains(userId);
        assertThat(persona.canUserWrite(userId)).isTrue();
        assertThat(persona.canUserRead(userId)).isTrue();
    }

    @Test
    public void updatePersona_whenNewReaderUserAdded_thenTheyShouldHaveOnlyReadPermission() {

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
        assertThat(persona.getUsersAllowedToRead()).contains(userId);
        assertThat(persona.canUserWrite(userId)).isFalse();
        assertThat(persona.canUserRead(userId)).isTrue();
    }

    @Test
    public void updatePersona_whenReaderUserRemoved_thenReadPermissionShouldBeRevoked() {

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
        assertThat(persona.getUsersAllowedToRead()).doesNotContain(userId);
        assertThat(persona.canUserWrite(userId)).isFalse();
        assertThat(persona.canUserRead(userId)).isFalse();
    }

    @Test
    public void updatePersona_whenWriterUserRemoved_thenReadAndWritePermissionShouldBeRevoked() {

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
        assertThat(persona.getUsersAllowedToWrite()).doesNotContain(userId);
        assertThat(persona.canUserWrite(userId)).isFalse();
        assertThat(persona.canUserRead(userId)).isFalse();
    }

    @Test
    public void updatePersona_whenNewBumpContent_thenUpdatePersona() {

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
    public void updatePersona_whenNewBumpFrequency_thenUpdatePersona() {

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
    public void updatePersona_whenNewBumpRole_thenUpdatePersona() {

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
    public void updatePersona_whenNewNudgeContent_thenUpdatePersona() {

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
    public void updatePersona_whenNewNudgeRole_thenUpdatePersona() {

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
