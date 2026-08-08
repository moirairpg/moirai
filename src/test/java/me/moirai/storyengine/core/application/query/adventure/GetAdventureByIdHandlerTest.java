package me.moirai.storyengine.core.application.query.adventure;

import static me.moirai.storyengine.common.enums.Moderation.STRICT;
import static me.moirai.storyengine.common.enums.Visibility.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.storyengine.common.enums.ArtificialIntelligenceModel;
import me.moirai.storyengine.common.enums.CharacterClass;
import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.core.domain.adventure.AdventureFixture;
import me.moirai.storyengine.core.domain.world.WorldFixture;
import me.moirai.storyengine.core.port.inbound.adventure.AdventureDetails;
import me.moirai.storyengine.core.port.inbound.adventure.ContextAttributesDto;
import me.moirai.storyengine.core.port.inbound.adventure.GetAdventureById;
import me.moirai.storyengine.core.port.inbound.adventure.ModelConfigurationDto;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureDetailsRow;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureReader;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRosterReader;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureMembershipSummaryRow;
import me.moirai.storyengine.core.port.outbound.storage.StoragePort;

@ExtendWith(MockitoExtension.class)
public class GetAdventureByIdHandlerTest {

    @Mock
    private AdventureReader reader;

    @Mock
    private AdventureRosterReader adventureRosterReader;

    @Mock
    private StoragePort storagePort;

    @InjectMocks
    private GetAdventureByIdHandler handler;

    @Test
    public void shouldThrowExceptionWhenIdIsNull() {

        // Given
        var query = new GetAdventureById(null);

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(query));
    }

    @Test
    public void shouldThrowExceptionWhenQueryIsNull() {

        // Given
        GetAdventureById query = null;

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(query));
    }

    @Test
    public void shouldThrowExceptionWhenAdventureIsNotFound() {

        // Given
        var query = new GetAdventureById(AdventureFixture.PUBLIC_ID);

        when(reader.getAdventureById(any(UUID.class))).thenReturn(Optional.empty());

        // Then
        assertThrows(NotFoundException.class, () -> handler.handle(query));
    }

    @Test
    public void shouldReturnAdventureDetailsWhenAdventureIsFound() {

        // Given
        var modelConfiguration = new ModelConfigurationDto(
                ArtificialIntelligenceModel.GPT54_MINI, 2048, 1.0);

        var contextAttributes = new ContextAttributesDto(null, null, null, null, 0);

        var expectedDetails = new AdventureDetailsRow(
                AdventureFixture.PUBLIC_ID,
                "Name",
                "desc",
                "start",
                WorldFixture.PUBLIC_ID,
                "Aria",
                "A helpful guide",
                PRIVATE,
                STRICT,
                null,
                null,
                null,
                modelConfiguration,
                contextAttributes,
                Set.of(),
                Set.of(),
                null,
                null);

        var query = new GetAdventureById(AdventureFixture.PUBLIC_ID);

        when(reader.getAdventureById(any(UUID.class))).thenReturn(Optional.of(expectedDetails));
        when(adventureRosterReader.getAllByAdventurePublicId(any(UUID.class))).thenReturn(List.of());

        // When
        AdventureDetails result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(AdventureFixture.PUBLIC_ID);
        assertThat(result.worldId()).isEqualTo(WorldFixture.PUBLIC_ID);
        assertThat(result.narratorName()).isEqualTo("Aria");
        assertThat(result.narratorPersonality()).isEqualTo("A helpful guide");
    }

    @Test
    public void shouldResolveCharacterImageUrlsWhenRosterHasCharacters() {

        // Given
        var modelConfiguration = new ModelConfigurationDto(
                ArtificialIntelligenceModel.GPT54_MINI, 2048, 1.0);

        var contextAttributes = new ContextAttributesDto(null, null, null, null, 0);

        var expectedDetails = new AdventureDetailsRow(
                AdventureFixture.PUBLIC_ID,
                "Name",
                "desc",
                "start",
                WorldFixture.PUBLIC_ID,
                "Aria",
                "A helpful guide",
                PRIVATE,
                STRICT,
                null,
                null,
                null,
                modelConfiguration,
                contextAttributes,
                Set.of(),
                Set.of(),
                null,
                null);

        var characterId = UUID.randomUUID();
        var playerId = UUID.randomUUID();

        var rosterRow = new AdventureMembershipSummaryRow(
                characterId,
                playerId,
                "john.doe",
                "Volin Habar",
                CharacterClass.PALADIN,
                "characters/image-key.png");

        var query = new GetAdventureById(AdventureFixture.PUBLIC_ID);

        when(reader.getAdventureById(any(UUID.class))).thenReturn(Optional.of(expectedDetails));
        when(adventureRosterReader.getAllByAdventurePublicId(any(UUID.class))).thenReturn(List.of(rosterRow));
        when(storagePort.resolveUrl("characters/image-key.png")).thenReturn("http://image.url");

        // When
        AdventureDetails result = handler.handle(query);

        // Then
        assertThat(result.roster()).hasSize(1);
        assertThat(result.roster().getFirst().playerCharacterId()).isEqualTo(characterId);
        assertThat(result.roster().getFirst().playerUsername()).isEqualTo("john.doe");
        assertThat(result.roster().getFirst().characterClass()).isEqualTo(CharacterClass.PALADIN);
        assertThat(result.roster().getFirst().imageUrl()).isEqualTo("http://image.url");
    }
}
