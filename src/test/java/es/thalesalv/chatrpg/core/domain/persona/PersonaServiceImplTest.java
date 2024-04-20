package es.thalesalv.chatrpg.core.domain.persona;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.common.exception.AssetAccessDeniedException;
import es.thalesalv.chatrpg.common.exception.AssetNotFoundException;
import es.thalesalv.chatrpg.common.exception.BusinessRuleViolationException;
import es.thalesalv.chatrpg.core.application.command.persona.CreatePersona;
import es.thalesalv.chatrpg.core.application.command.persona.DeletePersona;
import es.thalesalv.chatrpg.core.application.command.persona.UpdatePersona;
import es.thalesalv.chatrpg.core.application.query.persona.GetPersonaById;
import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.PermissionsFixture;
import es.thalesalv.chatrpg.core.domain.Visibility;

@ExtendWith(MockitoExtension.class)
public class PersonaServiceImplTest {

    @Mock
    private PersonaRepository repository;

    @InjectMocks
    private PersonaServiceImpl service;

    @Test
    public void createPersona_whenValidData_thenPersonaIsCreatedSuccessfully() {

        // Given
        String name = "ChatRPG";
        String personality = "I am a chatbot";
        String visibility = "PRIVATE";

        Nudge nudge = NudgeFixture.sample().build();
        Bump bump = BumpFixture.sample().build();

        Persona expectedPersona = PersonaFixture.privatePersona()
                .name(name)
                .personality(personality)
                .visibility(Visibility.fromString(visibility))
                .nudge(nudge)
                .bump(bump)
                .build();

        CreatePersona command = CreatePersona.builder()
                .name(name)
                .personality(personality)
                .nudgeContent(nudge.getContent())
                .nudgeRole(nudge.getRole().toString())
                .bumpContent(bump.getContent())
                .bumpRole(bump.getRole().toString())
                .bumpFrequency(bump.getFrequency())
                .gameMode("rpg")
                .visibility(visibility)
                .build();

        when(repository.save(any(Persona.class))).thenReturn(expectedPersona);

        // When
        Persona createdPersona = service.createFrom(command);

        // Then
        assertThat(createdPersona).isNotNull().isEqualTo(expectedPersona);
        assertThat(createdPersona.getName()).isEqualTo(expectedPersona.getName());
        assertThat(createdPersona.getOwnerDiscordId()).isEqualTo(expectedPersona.getOwnerDiscordId());
        assertThat(createdPersona.getUsersAllowedToWrite()).isEqualTo(expectedPersona.getUsersAllowedToWrite());
        assertThat(createdPersona.getUsersAllowedToRead()).isEqualTo(expectedPersona.getUsersAllowedToRead());
        assertThat(createdPersona.getPersonality()).isEqualTo(expectedPersona.getPersonality());
        assertThat(createdPersona.getVisibility()).isEqualTo(expectedPersona.getVisibility());
    }

    @Test
    public void createPersona_whenBumpIsNull_thenCreatePersonaSuccessfully() {

        // Given
        String name = "ChatRPG";
        String personality = "I am a chatbot";
        String visibility = "PRIVATE";

        Nudge nudge = NudgeFixture.sample().build();

        Persona expectedPersona = PersonaFixture.privatePersona()
                .name(name)
                .personality(personality)
                .visibility(Visibility.fromString(visibility))
                .nudge(nudge)
                .build();

        CreatePersona command = CreatePersona.builder()
                .name(name)
                .personality(personality)
                .nudgeContent(nudge.getContent())
                .nudgeRole(nudge.getRole().toString())
                .gameMode("rpg")
                .visibility(visibility)
                .build();

        when(repository.save(any(Persona.class))).thenReturn(expectedPersona);

        // When
        Persona createdPersona = service.createFrom(command);

        // Then
        assertThat(createdPersona).isNotNull().isEqualTo(expectedPersona);
        assertThat(createdPersona.getName()).isEqualTo(expectedPersona.getName());
        assertThat(createdPersona.getOwnerDiscordId()).isEqualTo(expectedPersona.getOwnerDiscordId());
        assertThat(createdPersona.getUsersAllowedToWrite()).isEqualTo(expectedPersona.getUsersAllowedToWrite());
        assertThat(createdPersona.getUsersAllowedToRead()).isEqualTo(expectedPersona.getUsersAllowedToRead());
        assertThat(createdPersona.getPersonality()).isEqualTo(expectedPersona.getPersonality());
        assertThat(createdPersona.getVisibility()).isEqualTo(expectedPersona.getVisibility());
    }

    @Test
    public void createPersona_whenNudgeIsNull_thenCreatePersonaSuccessfully() {

        // Given
        String name = "ChatRPG";
        String personality = "I am a chatbot";
        String visibility = "PRIVATE";

        Bump bump = BumpFixture.sample().build();

        Persona expectedPersona = PersonaFixture.privatePersona()
                .name(name)
                .personality(personality)
                .visibility(Visibility.fromString(visibility))
                .bump(bump)
                .build();

        CreatePersona command = CreatePersona.builder()
                .name(name)
                .personality(personality)
                .bumpContent(bump.getContent())
                .bumpRole(bump.getRole().toString())
                .bumpFrequency(bump.getFrequency())
                .gameMode("rpg")
                .visibility(visibility)
                .build();

        when(repository.save(any(Persona.class))).thenReturn(expectedPersona);

        // When
        Persona createdPersona = service.createFrom(command);

        // Then
        assertThat(createdPersona).isNotNull().isEqualTo(expectedPersona);
        assertThat(createdPersona.getName()).isEqualTo(expectedPersona.getName());
        assertThat(createdPersona.getOwnerDiscordId()).isEqualTo(expectedPersona.getOwnerDiscordId());
        assertThat(createdPersona.getUsersAllowedToWrite()).isEqualTo(expectedPersona.getUsersAllowedToWrite());
        assertThat(createdPersona.getUsersAllowedToRead()).isEqualTo(expectedPersona.getUsersAllowedToRead());
        assertThat(createdPersona.getPersonality()).isEqualTo(expectedPersona.getPersonality());
        assertThat(createdPersona.getVisibility()).isEqualTo(expectedPersona.getVisibility());
    }

    @Test
    public void createPersona_whenBumpIsProvidedAndFrequencyIsLowerThanOne_thenExceptionIsThrown() {

        // Given
        String name = "ChatRPG";
        String personality = "I am a chatbot";
        String visibility = "PRIVATE";
        String role = "SYSTEM";
        String content = "This is content";
        Permissions permissions = PermissionsFixture.samplePermissions().build();

        CreatePersona command = CreatePersona.builder()
                .name(name)
                .personality(personality)
                .nudgeContent(content)
                .nudgeRole(role)
                .bumpContent(content)
                .bumpRole(role)
                .bumpFrequency(0)
                .visibility(visibility)
                .requesterDiscordId(permissions.getOwnerDiscordId())
                .usersAllowedToRead(permissions.getUsersAllowedToRead())
                .usersAllowedToWrite(permissions.getUsersAllowedToWrite())
                .build();

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> service.createFrom(command));
    }

    @Test
    public void updatePersona_whenPersonaNotFound_thenThrowException() {

        // Given
        String id = "CHCONFID";

        UpdatePersona command = UpdatePersona.builder()
                .id(id)
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> service.update(command));
    }

    @Test
    public void updatePersona_whenNotEnoughPermissions_thenThrowException() {

        // Given
        String id = "CHCONFID";

        UpdatePersona command = UpdatePersona.builder()
                .id(id)
                .requesterDiscordId("USRID")
                .build();

        Persona persona = PersonaFixture.privatePersona()
                .id(id)
                .name("New name")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId("ANTHRUSR")
                        .build())
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(persona));

        // Then
        assertThrows(AssetAccessDeniedException.class, () -> service.update(command));
    }

    @Test
    public void findPersona_whenValidId_thenReturnPersona() {

        // Given
        String id = "CHCONFID";

        Persona persona = PersonaFixture.privatePersona()
                .id(id)
                .name("New name")
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(persona));

        // When
        Persona result = service.getPersonaById(id);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(persona.getName());
    }

    @Test
    public void findPersona_whenPersonaNotFound_thenThrowException() {

        // Given
        String id = "CHCONFID";

        when(repository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> service.getPersonaById(id));
    }

    @Test
    public void findPersonaWithPermission_whenProperPermission_thenReturnPersona() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        GetPersonaById query = GetPersonaById.build(id, requesterId);

        Persona persona = PersonaFixture.privatePersona()
                .id(id)
                .name("New name")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(persona));

        // When
        Persona result = service.getPersonaById(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(persona.getName());
    }

    @Test
    public void findPersonaWithPermission_whenPersonaNotFound_thenThrowException() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        GetPersonaById query = GetPersonaById.build(id, requesterId);

        when(repository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> service.getPersonaById(query));
    }

    @Test
    public void findPersonaWithPermission_whenInvalidPermission_thenThrowException() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        GetPersonaById query = GetPersonaById.build(id, requesterId);

        Persona persona = PersonaFixture.privatePersona()
                .id(id)
                .name("New name")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId("ANTHRUSR")
                        .usersAllowedToRead(Collections.emptyList())
                        .build())
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(persona));

        // Then
        assertThrows(AssetAccessDeniedException.class, () -> service.getPersonaById(query));
    }

    @Test
    public void deletePersona_whenPersonaNotFound_thenThrowException() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        DeletePersona command = DeletePersona.build(id, requesterId);

        when(repository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> service.deletePersona(command));
    }

    @Test
    public void deletePersona_whenInvalidPermission_thenThrowException() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        DeletePersona command = DeletePersona.build(id, requesterId);

        Persona persona = PersonaFixture.privatePersona()
                .id(id)
                .name("New name")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId("ANTHRUSR")
                        .build())
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(persona));

        // Then
        assertThrows(AssetAccessDeniedException.class, () -> service.deletePersona(command));
    }

    @Test
    public void deletePersona_whenProperIdAndPermission_thenPersonaIsDeleted() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        DeletePersona command = DeletePersona.build(id, requesterId);

        Persona persona = PersonaFixture.privatePersona()
                .id(id)
                .name("New name")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(persona));

        // Then
        service.deletePersona(command);
    }

    @Test
    public void updatePersona_whenValidData_thenPersonaIsUpdated() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        UpdatePersona command = UpdatePersona.builder()
                .id(id)
                .name("ChatRPG")
                .personality("I am a Discord chatbot")
                .visibility("PUBLIC")
                .requesterDiscordId("CRTID")
                .bumpContent("This is a bump")
                .bumpRole("system")
                .bumpFrequency(5)
                .nudgeContent("This is a nudge")
                .nudgeRole("system")
                .gameMode("author")
                .requesterDiscordId(requesterId)
                .build();

        Persona unchangedPersona = PersonaFixture.privatePersona()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        Persona expectedUpdatedPersona = PersonaFixture.privatePersona()
                .id(id)
                .name("New name")
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(unchangedPersona));
        when(repository.save(any(Persona.class))).thenReturn(expectedUpdatedPersona);

        // When
        Persona result = service.update(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(expectedUpdatedPersona.getName());
    }

    @Test
    public void updatePersona_whenUpdateFieldsAreEmpty_thenPersonaIsNotChanged() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        UpdatePersona command = UpdatePersona.builder()
                .id(id)
                .requesterDiscordId(requesterId)
                .build();

        Persona unchangedPersona = PersonaFixture.privatePersona()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(unchangedPersona));
        when(repository.save(any(Persona.class))).thenReturn(unchangedPersona);

        // When
        Persona result = service.update(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(unchangedPersona.getName());
    }

    @Test
    public void updatePersona_whenPublicToMakePrivate_thenPersonaIsMadePrivate() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        UpdatePersona command = UpdatePersona.builder()
                .id(id)
                .requesterDiscordId(requesterId)
                .visibility("private")
                .build();

        Persona unchangedPersona = PersonaFixture.publicPersona()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        Persona expectedUpdatedPersona = PersonaFixture.privatePersona()
                .id(id)
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(unchangedPersona));
        when(repository.save(any(Persona.class))).thenReturn(expectedUpdatedPersona);

        // When
        Persona result = service.update(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isPublic()).isFalse();
        assertThat(result.getVisibility()).isEqualTo(expectedUpdatedPersona.getVisibility());
    }

    @Test
    public void updatePersona_whenInvalidVisibility_thenNothingIsChanged() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        UpdatePersona command = UpdatePersona.builder()
                .id(id)
                .requesterDiscordId(requesterId)
                .visibility("invalid")
                .build();

        Persona unchangedPersona = PersonaFixture.privatePersona()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        Persona expectedUpdatedPersona = PersonaFixture.privatePersona()
                .id(id)
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(unchangedPersona));
        when(repository.save(any(Persona.class))).thenReturn(expectedUpdatedPersona);

        // When
        Persona result = service.update(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isPublic()).isFalse();
        assertThat(result.getVisibility()).isEqualTo(expectedUpdatedPersona.getVisibility());
    }
}
