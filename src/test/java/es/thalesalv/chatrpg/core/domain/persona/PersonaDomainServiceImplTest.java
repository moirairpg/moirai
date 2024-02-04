package es.thalesalv.chatrpg.core.domain.persona;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import es.thalesalv.chatrpg.common.exception.BusinessRuleViolationException;
import es.thalesalv.chatrpg.core.application.command.persona.CreatePersona;
import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.PermissionsFixture;
import es.thalesalv.chatrpg.core.domain.Visibility;
import es.thalesalv.chatrpg.core.domain.port.TokenizerPort;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
public class PersonaDomainServiceImplTest {

    @Mock
    private PersonaRepository repository;

    @Mock
    private TokenizerPort tokenizerPort;

    @InjectMocks
    private PersonaDomainServiceImpl service;

    @Test
    public void createPersonaSuccessfully() {

        // Given
        String name = "ChatRPG";
        String personality = "I am a chatbot";
        String visibility = "PRIVATE";
        String role = "SYSTEM";
        String content = "This is content";
        Integer bumpFrequency = 5;
        Permissions permissions = PermissionsFixture.samplePermissions().build();

        Nudge nudge = Nudge.builder()
                .content(content)
                .role(role)
                .build();

        Bump bump = Bump.builder()
                .content(content)
                .role(role)
                .frequency(bumpFrequency)
                .build();

        CreatePersona command = CreatePersona.builder()
                .name(name)
                .personality(personality)
                .nudgeContent(content)
                .nudgeRole(role)
                .bumpContent(content)
                .bumpRole(role)
                .bumpFrequency(bumpFrequency)
                .visibility(visibility)
                .creatorDiscordId(permissions.getOwnerDiscordId())
                .readerUsers(permissions.getUsersAllowedToRead())
                .writerUsers(permissions.getUsersAllowedToWrite())
                .build();

        Persona expectedPersona = PersonaFixture.privatePersona()
                .name(name)
                .permissions(permissions)
                .personality(personality)
                .visibility(Visibility.fromString(visibility))
                .nudge(nudge)
                .bump(bump)
                .build();

        when(repository.save(any(Persona.class))).thenReturn(expectedPersona);

        // When
        Persona createdPersona = service.createFrom(command);

        // Then
        assertThat(createdPersona).isNotNull().isEqualTo(expectedPersona);
        assertThat(createdPersona.getName()).isEqualTo(expectedPersona.getName());
        assertThat(createdPersona.getPermissions()).isEqualTo(expectedPersona.getPermissions());
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
                .creatorDiscordId(permissions.getOwnerDiscordId())
                .readerUsers(permissions.getUsersAllowedToRead())
                .writerUsers(permissions.getUsersAllowedToWrite())
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
                .creatorDiscordId(permissions.getOwnerDiscordId())
                .readerUsers(permissions.getUsersAllowedToRead())
                .writerUsers(permissions.getUsersAllowedToWrite())
                .build();

        ReflectionTestUtils.setField(service, "personalityTokenLimit", 20);
        when(tokenizerPort.getTokenCountFrom(anyString())).thenReturn(10);

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> service.createFrom(command));
    }
}
