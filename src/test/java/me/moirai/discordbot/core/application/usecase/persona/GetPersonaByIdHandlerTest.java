package me.moirai.discordbot.core.application.usecase.persona;

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

import me.moirai.discordbot.core.application.port.PersonaQueryRepository;
import me.moirai.discordbot.core.application.usecase.persona.request.GetPersonaById;
import me.moirai.discordbot.core.application.usecase.persona.result.GetPersonaResult;
import me.moirai.discordbot.core.domain.PermissionsFixture;
import me.moirai.discordbot.core.domain.persona.Persona;
import me.moirai.discordbot.core.domain.persona.PersonaFixture;

@ExtendWith(MockitoExtension.class)
public class GetPersonaByIdHandlerTest {

    @Mock
    private PersonaQueryRepository queryRepository;

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
        Persona persona = PersonaFixture.privatePersona()
                .id(id)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        GetPersonaById query = GetPersonaById.build(id, requesterId);

        when(queryRepository.findById(anyString())).thenReturn(Optional.of(persona));

        // When
        GetPersonaResult result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }
}