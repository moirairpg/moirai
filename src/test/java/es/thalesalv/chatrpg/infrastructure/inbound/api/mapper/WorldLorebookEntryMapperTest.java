package es.thalesalv.chatrpg.infrastructure.inbound.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.core.application.command.world.CreateWorldLorebookEntry;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.WorldLorebookEntryRequest;

@ExtendWith(MockitoExtension.class)
public class WorldLorebookEntryMapperTest {

    @InjectMocks
    private WorldLorebookEntryMapper mapper;

    @Test
    public void mapCreationRequestToCommand() {

        // Given
        WorldLorebookEntryRequest request = WorldLorebookEntryRequest.builder()
                .name("Volin Habar")
                .description("Volin Habar is a warrior that fights with a sword.")
                .regex("[Vv]olin [Hh]abar|[Vv]oha")
                .playerDiscordId("2423423423423")
                .isPlayerCharacter(true)
                .worldId("WRLDID")
                .build();

        // When
        CreateWorldLorebookEntry command = mapper.toCommand(request);

        // Then
        assertThat(command).isNotNull();
        assertThat(command.getName()).isEqualTo(request.getName());
        assertThat(command.getDescription()).isEqualTo(request.getDescription());
        assertThat(command.getRegex()).isEqualTo(request.getRegex());
        assertThat(command.getPlayerDiscordId()).isEqualTo(request.getPlayerDiscordId());
        assertThat(command.getWorldId()).isEqualTo(request.getWorldId());
    }
}
