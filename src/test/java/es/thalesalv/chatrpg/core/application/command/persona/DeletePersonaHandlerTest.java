package es.thalesalv.chatrpg.core.application.command.persona;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.common.exception.AssetNotFoundException;
import es.thalesalv.chatrpg.core.domain.persona.PersonaFixture;
import es.thalesalv.chatrpg.core.domain.persona.PersonaRepository;

@ExtendWith(MockitoExtension.class)
public class DeletePersonaHandlerTest {

    @Mock
    private PersonaRepository repository;

    @InjectMocks
    private DeletePersonaHandler handler;

    @Test
    public void errorWhenPersonaIsNotFound() {

        // Given
        String id = "WRDID";

        DeletePersona command = DeletePersona.build(id);

        when(repository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> handler.handle(command));
    }

    @Test
    public void errorWhenIdIsNull() {

        // Given
        String id = null;

        DeletePersona config = DeletePersona.build(id);

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(config));
    }

    @Test
    public void deletePersona() {

        // TODO make this an integration test that actually tests deletion

        // Given
        String id = "WRDID";

        DeletePersona command = DeletePersona.build(id);

        when(repository.findById(anyString()))
                .thenReturn(Optional.of(PersonaFixture.privatePersona().build()));

        // When
        handler.handle(command);
    }
}
