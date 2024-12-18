package me.moirai.discordbot.core.application.usecase.adventure;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.core.application.usecase.adventure.request.UpdateAdventureRememberByChannelId;
import me.moirai.discordbot.core.domain.adventure.AdventureDomainRepository;

@ExtendWith(MockitoExtension.class)
public class UpdateAdventureRememberByChannelIdHandlerTest {

    @Mock
    private AdventureDomainRepository repository;

    @InjectMocks
    private UpdateAdventureRememberByChannelIdHandler handler;

    @Test
    public void updateRemember_whenCalled_thenUpdateAdventureRemember() {

        // Given
        UpdateAdventureRememberByChannelId command = UpdateAdventureRememberByChannelId.build("Remember","1234123");

        // When
        handler.execute(command);

        // Then
        verify(repository, times(1))
                .updateRememberByChannelId(anyString(), anyString());
    }
}
