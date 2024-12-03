package me.moirai.discordbot.core.application.usecase.persona;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.core.application.usecase.persona.request.RemoveFavoritePersona;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;

@ExtendWith(MockitoExtension.class)
public class RemoveFavoritePersonaHandlerTest {

    @Mock
    private FavoriteRepository repository;

    @InjectMocks
    private RemoveFavoritePersonaHandler handler;

    @Test
    public void removeFavorite_whenValidData_thenRemove() {

        // Given
        String assetId = "1234";
        String userId = "1234";

        RemoveFavoritePersona request = RemoveFavoritePersona.builder()
                .assetId(assetId)
                .playerDiscordId(userId)
                .build();

        // When
        handler.handle(request);

        // Then
        verify(repository, times(1))
                .deleteByPlayerDiscordIdAndAssetIdAndAssetType(anyString(), anyString(), anyString());
    }
}
