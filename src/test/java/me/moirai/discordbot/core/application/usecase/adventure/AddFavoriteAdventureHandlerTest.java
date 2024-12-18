package me.moirai.discordbot.core.application.usecase.adventure;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.core.application.port.AdventureQueryRepository;
import me.moirai.discordbot.core.application.usecase.adventure.request.AddFavoriteAdventure;
import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.adventure.AdventureFixture;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;

@ExtendWith(MockitoExtension.class)
public class AddFavoriteAdventureHandlerTest {

    @Mock
    private AdventureQueryRepository adventureQueryRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    @InjectMocks
    private AddFavoriteAdventureHandler handler;

    @Test
    public void addFavorite_whenValidAsset_thenCreateFavorite() {

        // Given
        AddFavoriteAdventure command = AddFavoriteAdventure.builder()
                .assetId("1234")
                .playerDiscordId("1234")
                .build();

        Adventure adventure = AdventureFixture.privateMultiplayerAdventure().build();

        when(adventureQueryRepository.findById(anyString())).thenReturn(Optional.of(adventure));

        // When
        handler.handle(command);

        // Then
        verify(favoriteRepository, times(1)).save(any());
    }

    @Test
    public void addFavorite_whenAssetNotFound_thenThrowException() {

        // Given
        AddFavoriteAdventure command = AddFavoriteAdventure.builder()
                .assetId("1234")
                .playerDiscordId("1234")
                .build();

        when(adventureQueryRepository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> handler.handle(command));
    }
}
