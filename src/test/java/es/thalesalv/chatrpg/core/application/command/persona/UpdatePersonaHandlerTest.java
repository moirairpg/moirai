package es.thalesalv.chatrpg.core.application.command.persona;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaDomainServiceImpl;
import es.thalesalv.chatrpg.core.domain.persona.PersonaFixture;

@ExtendWith(MockitoExtension.class)
public class UpdatePersonaHandlerTest {

    @Mock
    private PersonaDomainServiceImpl service;

    @InjectMocks
    private UpdatePersonaHandler handler;

    @Test
    public void updatePersona() {

        // Given
        String id = "PRSNID";

        UpdatePersona command = UpdatePersona.builder()
                .id(id)
                .name("ChatRPG")
                .personality("I am a Discord chatbot")
                .visibility("PUBLIC")
                .requesterDiscordId("CRTID")
                .build();

        Persona expectedUpdatedPersona = PersonaFixture.privatePersona().build();

        when(service.update(any(UpdatePersona.class)))
                .thenReturn(expectedUpdatedPersona);

        // When
        UpdatePersonaResult result = handler.handle(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLastUpdatedDateTime()).isEqualTo(expectedUpdatedPersona.getLastUpdateDate());
    }
}
