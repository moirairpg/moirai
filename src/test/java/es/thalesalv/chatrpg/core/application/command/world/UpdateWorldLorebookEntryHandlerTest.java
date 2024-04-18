package es.thalesalv.chatrpg.core.application.command.world;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntryFixture;
import es.thalesalv.chatrpg.core.domain.world.WorldService;
import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntry;

@ExtendWith(MockitoExtension.class)
public class UpdateWorldLorebookEntryHandlerTest {

    @Mock
    private WorldService service;

    @InjectMocks
    private UpdateWorldLorebookEntryHandler handler;

    @Test
    public void updateWorld() {

        // Given
        String id = "WRDID";

        UpdateWorldLorebookEntry command = UpdateWorldLorebookEntry.builder()
                .id(id)
                .name("ChatRPG")
                .regex("ChatRPG")
                .description("This is an RPG world")
                .playerDiscordId("PLAYERID")
                .isPlayerCharacter(true)
                .build();

        WorldLorebookEntry expectedUpdatedEntry = WorldLorebookEntryFixture.sampleLorebookEntry().build();

        when(service.updateLorebookEntry(any(UpdateWorldLorebookEntry.class)))
                .thenReturn(expectedUpdatedEntry);

        // When
        UpdateWorldLorebookEntryResult result = handler.handle(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLastUpdatedDateTime()).isEqualTo(expectedUpdatedEntry.getLastUpdateDate());
    }
}
