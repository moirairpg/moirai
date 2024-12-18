package me.moirai.discordbot.core.application.usecase.adventure;

import static org.junit.jupiter.api.Assertions.assertThrows;
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

import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.core.application.usecase.adventure.request.DeleteAdventure;
import me.moirai.discordbot.core.domain.PermissionsFixture;
import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.adventure.AdventureDomainRepository;
import me.moirai.discordbot.core.domain.adventure.AdventureFixture;

@ExtendWith(MockitoExtension.class)
public class DeleteAdventureHandlerTest {

    @Mock
    private AdventureDomainRepository repository;

    @InjectMocks
    private DeleteAdventureHandler handler;

    @Test
    public void errorWhenIdIsNull() {

        // Given
        String id = null;
        String requesterId = "RQSTRID";
        DeleteAdventure command = DeleteAdventure.build(id, requesterId);

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
    }

    @Test
    public void deleteAdventure_whenAdventureNotFound_thenThrowException() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        DeleteAdventure command = DeleteAdventure.build(id, requesterId);

        when(repository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> handler.handle(command));
    }

    @Test
    public void deleteAdventure_whenInvalidPermission_thenThrowException() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        DeleteAdventure command = DeleteAdventure.build(id, requesterId);

        Adventure adventure = AdventureFixture.privateMultiplayerAdventure()
                .id(id)
                .name("New name")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId("ANTHRUSR")
                        .build())
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(adventure));

        // Then
        assertThrows(AssetAccessDeniedException.class, () -> handler.handle(command));
    }

    @Test
    public void deleteAdventure_whenProperPermission_thenAdventureIsDeleted() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        DeleteAdventure command = DeleteAdventure.build(id, requesterId);

        Adventure adventure = AdventureFixture.privateMultiplayerAdventure()
                .id(id)
                .name("New name")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(adventure));

        // When
        handler.handle(command);

        // Then
        verify(repository, times(1)).findById(anyString());
        verify(repository, times(1)).deleteById(anyString());
    }
}
