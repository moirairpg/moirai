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
import org.springframework.test.util.ReflectionTestUtils;

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
import es.thalesalv.chatrpg.core.domain.port.TokenizerPort;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
public class PersonaServiceImplTest {

    @Mock
    private PersonaRepository repository;

    @Mock
    private TokenizerPort tokenizerPort;

    @InjectMocks
    private PersonaServiceImpl service;

    @Test
    public void createPersonaSuccessfully() {

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
    public void errorWhenPersonalityTokenLimitIsSurpassed() {

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
                .bumpFrequency(5)
                .visibility(visibility)
                .requesterDiscordId(permissions.getOwnerDiscordId())
                .usersAllowedToRead(permissions.getUsersAllowedToRead())
                .usersAllowedToWrite(permissions.getUsersAllowedToWrite())
                .build();

        ReflectionTestUtils.setField(service, "personalityTokenLimit", 2);
        when(tokenizerPort.getTokenCountFrom(anyString())).thenReturn(10);

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> service.createFrom(command));
    }

    @Test
    public void errorWhenBumpFrequencyIsLowerThanOne() {

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

        ReflectionTestUtils.setField(service, "personalityTokenLimit", 20);
        when(tokenizerPort.getTokenCountFrom(anyString())).thenReturn(10);

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> service.createFrom(command));
    }

    @Test
    public void errorWhenUpdatePersonaNotFound() {

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
    public void errorWhenUpdatePersonaAccessDenied() {

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
    public void findPersonaById() {

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
    public void errorWhenFindPersonaNotFound() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        GetPersonaById query = GetPersonaById.build(id, requesterId);

        when(repository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> service.getPersonaById(query));
    }

    @Test
    public void errorWhenFindPersonaAccessDenied() {

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
    public void errorWhenDeletePersonaNotFound() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        DeletePersona command = DeletePersona.build(id, requesterId);

        when(repository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> service.deletePersona(command));
    }

    @Test
    public void errorWhenDeletePersonaAccessDenied() {

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
    public void deletePersona() {

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
    public void updatePersona() {

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
}
