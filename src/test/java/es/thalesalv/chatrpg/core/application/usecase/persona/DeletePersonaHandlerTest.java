package es.thalesalv.chatrpg.core.application.usecase.persona;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.core.application.usecase.persona.request.DeletePersona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaService;

@ExtendWith(MockitoExtension.class)
public class DeletePersonaHandlerTest {

    @Mock
    private PersonaService domainService;

    @InjectMocks
    private DeletePersonaHandler handler;

    @Test
    public void errorWhenIdIsNull() {

        // Given
        String id = null;
        String requesterId = "RUEYAHA";

        DeletePersona config = DeletePersona.build(id, requesterId);

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(config));
    }

    @Test
    public void deletePersona() {

        // Given
        String id = "WRDID";
        String requesterId = "RUEYAHA";

        DeletePersona command = DeletePersona.build(id, requesterId);

        doNothing().when(domainService).deletePersona(any(DeletePersona.class));

        // When
        handler.handle(command);
    }
}
