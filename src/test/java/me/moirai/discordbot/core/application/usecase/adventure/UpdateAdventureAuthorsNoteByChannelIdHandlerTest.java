package me.moirai.discordbot.core.application.usecase.adventure;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.core.application.usecase.adventure.request.UpdateAdventureAuthorsNoteByChannelId;
import me.moirai.discordbot.core.domain.adventure.AdventureDomainRepository;

@ExtendWith(MockitoExtension.class)
public class UpdateAdventureAuthorsNoteByChannelIdHandlerTest {

    @Mock
    private AdventureDomainRepository repository;

    @InjectMocks
    private UpdateAdventureAuthorsNoteByChannelIdHandler handler;

    @Test
    public void updateAuthorsNote_whenCalled_thenUpdateAdventureAuthorsNote() {

        // Given
        UpdateAdventureAuthorsNoteByChannelId command = UpdateAdventureAuthorsNoteByChannelId.build("AuthorsNote","1234123");

        // When
        handler.execute(command);

        // Then
        verify(repository, times(1))
                .updateAuthorsNoteByChannelId(anyString(), anyString());
    }
}
