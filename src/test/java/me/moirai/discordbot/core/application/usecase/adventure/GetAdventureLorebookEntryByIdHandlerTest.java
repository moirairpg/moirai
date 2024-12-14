package me.moirai.discordbot.core.application.usecase.adventure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.core.application.usecase.adventure.request.GetAdventureLorebookEntryById;
import me.moirai.discordbot.core.application.usecase.adventure.result.GetAdventureLorebookEntryResult;
import me.moirai.discordbot.core.domain.adventure.AdventureLorebookEntry;
import me.moirai.discordbot.core.domain.adventure.AdventureLorebookEntryFixture;
import me.moirai.discordbot.core.domain.adventure.AdventureService;

@ExtendWith(MockitoExtension.class)
public class GetAdventureLorebookEntryByIdHandlerTest {

    @Mock
    private AdventureService domainService;

    @InjectMocks
    private GetAdventureLorebookEntryByIdHandler handler;

    @Test
    public void errorWhenQueryIsNull() {

        // Given
        GetAdventureLorebookEntryById query = null;

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(query));
    }

    @Test
    public void errorWhenEntryIdIsNull() {

        // Given
        GetAdventureLorebookEntryById query = GetAdventureLorebookEntryById.builder().build();

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(query));
    }

    @Test
    public void errorWhenAdventureIdIsNull() {

        // Given
        GetAdventureLorebookEntryById query = GetAdventureLorebookEntryById.builder()
                .entryId("ENTRID")
                .build();

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(query));
    }

    @Test
    public void getAdventureLorebookEntryById() {

        // Given
        String id = "HAUDHUAHD";
        String adventureId = "WRLDID";
        String requesterId = "4314324";
        AdventureLorebookEntry entry = AdventureLorebookEntryFixture.sampleLorebookEntry().id(id).build();
        GetAdventureLorebookEntryById query = GetAdventureLorebookEntryById.builder()
                .entryId(id)
                .adventureId(adventureId)
                .requesterDiscordId(requesterId)
                .build();

        when(domainService.findLorebookEntryById(any(GetAdventureLorebookEntryById.class))).thenReturn(entry);

        // When
        GetAdventureLorebookEntryResult result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(entry.getId());
        assertThat(result.getName()).isEqualTo(entry.getName());
        assertThat(result.getRegex()).isEqualTo(entry.getRegex());
        assertThat(result.getDescription()).isEqualTo(entry.getDescription());
        assertThat(result.getPlayerDiscordId()).isEqualTo(entry.getPlayerDiscordId());
        assertThat(result.isPlayerCharacter()).isEqualTo(entry.isPlayerCharacter());
        assertThat(result.getCreationDate()).isNotNull();
        assertThat(result.getLastUpdateDate()).isNotNull();
    }
}
