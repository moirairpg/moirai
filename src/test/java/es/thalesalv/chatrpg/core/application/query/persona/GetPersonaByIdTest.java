package es.thalesalv.chatrpg.core.application.query.persona;

import static org.assertj.core.api.Assertions.assertThat;
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
import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaFixture;
import es.thalesalv.chatrpg.core.domain.persona.PersonaRepository;

@ExtendWith(MockitoExtension.class)
public class GetPersonaByIdTest {

    @Mock
    private PersonaRepository repository;

    @InjectMocks
    private GetPersonaByIdHandler handler;

    @Test
    public void errorWhenQueryIsNull() {

        // Given
        GetPersonaById query = null;

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(query));
    }

    @Test
    public void getPersonaById() {

        // Given
        String id = "HAUDHUAHD";
        Persona persona = PersonaFixture.privatePersona().id(id).build();
        GetPersonaById query = GetPersonaById.build(id);

        when(repository.findById(anyString(), anyString())).thenReturn(Optional.of(persona));

        // When
        GetPersonaResult result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    public void errorWhenPersonaNotFound() {

        // Given
        String id = "HAUDHUAHD";
        GetPersonaById query = GetPersonaById.build(id);

        when(repository.findById(anyString(), anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> handler.handle(query));
    }
}