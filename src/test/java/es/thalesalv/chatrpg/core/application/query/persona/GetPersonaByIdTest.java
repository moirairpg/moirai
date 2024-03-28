package es.thalesalv.chatrpg.core.application.query.persona;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaDomainService;
import es.thalesalv.chatrpg.core.domain.persona.PersonaFixture;

@ExtendWith(MockitoExtension.class)
public class GetPersonaByIdTest {

    @Mock
    private PersonaDomainService domainService;

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
        String requesterId = "RQSTRID";
        Persona persona = PersonaFixture.privatePersona().id(id).build();
        GetPersonaById query = GetPersonaById.build(id, requesterId);

        when(domainService.getPersonaById(any(GetPersonaById.class))).thenReturn(persona);

        // When
        GetPersonaResult result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }
}