package me.moirai.storyengine.core.application.query.character;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.storyengine.core.port.inbound.character.GetPlayerCharacterAdventures;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRosterReader;
import me.moirai.storyengine.core.port.outbound.adventure.CharacterAdventureSummaryRow;
import me.moirai.storyengine.core.port.outbound.storage.StoragePort;

@ExtendWith(MockitoExtension.class)
public class GetPlayerCharacterAdventuresHandlerTest {

    @Mock
    private AdventureRosterReader adventureRosterReader;

    @Mock
    private StoragePort storagePort;

    @InjectMocks
    private GetPlayerCharacterAdventuresHandler handler;

    @Test
    void shouldMapEveryReaderRowToASummary() {

        // given
        var characterId = UUID.randomUUID();
        var firstAdventureId = UUID.randomUUID();
        var secondAdventureId = UUID.randomUUID();

        when(adventureRosterReader.getAdventuresByPlayerCharacterPublicId(any(UUID.class)))
                .thenReturn(List.of(
                        new CharacterAdventureSummaryRow(firstAdventureId, "Dragon Hunt", "dragon-key"),
                        new CharacterAdventureSummaryRow(secondAdventureId, "The Sunken City", "sunken-key")));
        when(storagePort.resolveUrl(any())).thenReturn("http://image.url");

        // when
        var result = handler.execute(new GetPlayerCharacterAdventures(characterId));

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).publicId()).isEqualTo(firstAdventureId);
        assertThat(result.get(0).name()).isEqualTo("Dragon Hunt");
        assertThat(result.get(0).imageUrl()).isEqualTo("http://image.url");
        assertThat(result.get(1).publicId()).isEqualTo(secondAdventureId);
        assertThat(result.get(1).name()).isEqualTo("The Sunken City");
    }

    @Test
    void shouldReturnEmptyListWhenCharacterIsNotEnrolledAnywhere() {

        // given
        var characterId = UUID.randomUUID();

        when(adventureRosterReader.getAdventuresByPlayerCharacterPublicId(any(UUID.class)))
                .thenReturn(List.of());

        // when
        var result = handler.execute(new GetPlayerCharacterAdventures(characterId));

        // then
        assertThat(result).isEmpty();
    }
}
