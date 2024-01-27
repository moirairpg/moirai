package es.thalesalv.chatrpg.core.domain.model.persona;

import static es.thalesalv.chatrpg.core.domain.model.Visibility.PRIVATE;
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

import es.thalesalv.chatrpg.common.exception.BusinessException;
import es.thalesalv.chatrpg.core.application.port.TokenizerPort;
import es.thalesalv.chatrpg.core.domain.model.PermissionFixture;
import es.thalesalv.chatrpg.core.domain.model.Permissions;
import es.thalesalv.chatrpg.core.domain.model.Visibility;

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
        Visibility visibility = PRIVATE;
        Permissions permissions = PermissionFixture.samplePermissions().build();
        Persona expectedPersona = PersonaFixture.privatePersona()
                .name(name)
                .permissions(permissions)
                .personality(personality)
                .visibility(visibility)
                .build();

        when(repository.save(any(Persona.class))).thenReturn(expectedPersona);

        // When
        Persona createdPersona = service.createPersona(name, personality, permissions, visibility);

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
        Visibility visibility = PRIVATE;
        Permissions permissions = PermissionFixture.samplePermissions().build();

        ReflectionTestUtils.setField(service, "personalityTokenLimit", 2);
        when(tokenizerPort.getTokenCountFrom(anyString())).thenReturn(10);

        // Then
        assertThrows(BusinessException.class, () -> service.createPersona(name, personality, permissions, visibility));
    }
}
