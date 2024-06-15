package es.thalesalv.chatrpg.core.application.usecase.persona;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.core.application.usecase.persona.request.CreatePersona;
import es.thalesalv.chatrpg.core.application.usecase.persona.request.CreatePersonaFixture;
import es.thalesalv.chatrpg.core.application.usecase.persona.result.CreatePersonaResult;
import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaFixture;
import es.thalesalv.chatrpg.core.domain.persona.PersonaService;

@ExtendWith(MockitoExtension.class)
public class CreatePersonaHandlerTest {

    @Mock
    private PersonaService domainService;

    @InjectMocks
    private CreatePersonaHandler handler;

    @Test
    public void errorWhenCommandIsNull() {

        // Given
        CreatePersona command = null;

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
    }

    @Test
    public void createPersona() {

        // Given
        String id = "HAUDHUAHD";
        Persona persona = PersonaFixture.privatePersona().id(id).build();
        CreatePersona command = CreatePersonaFixture.createPrivatePersona().build();

        when(domainService.createFrom(any(CreatePersona.class)))
                .thenReturn(persona);

        // When
        CreatePersonaResult result = handler.handle(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }
}
