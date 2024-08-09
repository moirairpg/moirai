package me.moirai.discordbot.core.application.usecase.world;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.core.application.usecase.world.request.GetWorldLorebookEntryById;
import me.moirai.discordbot.core.application.usecase.world.result.GetWorldLorebookEntryResult;
import me.moirai.discordbot.core.domain.world.WorldLorebookEntry;
import me.moirai.discordbot.core.domain.world.WorldLorebookEntryFixture;
import me.moirai.discordbot.core.domain.world.WorldService;

@ExtendWith(MockitoExtension.class)
public class GetWorldLorebookEntryByIdHandlerTest {

    @Mock
    private WorldService domainService;

    @InjectMocks
    private GetWorldLorebookEntryByIdHandler handler;

    @Test
    public void errorWhenQueryIsNull() {

        // Given
        GetWorldLorebookEntryById query = null;

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(query));
    }

    @Test
    public void errorWhenEntryIdIsNull() {

        // Given
        GetWorldLorebookEntryById query = GetWorldLorebookEntryById.builder().build();

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(query));
    }

    @Test
    public void errorWhenWorldIdIsNull() {

        // Given
        GetWorldLorebookEntryById query = GetWorldLorebookEntryById.builder()
                .entryId("ENTRID")
                .build();

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(query));
    }

    @Test
    public void getWorldLorebookEntryById() {

        // Given
        String id = "HAUDHUAHD";
        String worldId = "WRLDID";
        String requesterId = "4314324";
        WorldLorebookEntry entry = WorldLorebookEntryFixture.sampleLorebookEntry().id(id).build();
        GetWorldLorebookEntryById query = GetWorldLorebookEntryById.builder()
                .entryId(id)
                .worldId(worldId)
                .requesterDiscordId(requesterId)
                .build();

        when(domainService.findLorebookEntryById(any(GetWorldLorebookEntryById.class))).thenReturn(entry);

        // When
        GetWorldLorebookEntryResult result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }
}
