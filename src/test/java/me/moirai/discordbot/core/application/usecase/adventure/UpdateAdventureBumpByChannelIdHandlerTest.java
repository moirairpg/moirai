package me.moirai.discordbot.core.application.usecase.adventure;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.core.application.usecase.adventure.request.UpdateAdventureBumpByChannelId;
import me.moirai.discordbot.core.domain.adventure.AdventureDomainRepository;

@ExtendWith(MockitoExtension.class)
public class UpdateAdventureBumpByChannelIdHandlerTest {

    @Mock
    private AdventureDomainRepository repository;

    @InjectMocks
    private UpdateAdventureBumpByChannelIdHandler handler;

    @Test
    public void updateBump_whenCalled_thenUpdateAdventureBump() {

        // Given
        UpdateAdventureBumpByChannelId command = UpdateAdventureBumpByChannelId.builder()
                .bump("Bump")
                .bumpFrequency(5)
                .channelId("1234123")
                .build();

        // When
        handler.execute(command);

        // Then
        verify(repository, times(1))
                .updateBumpByChannelId(anyString(), anyInt(), anyString());
    }
}
