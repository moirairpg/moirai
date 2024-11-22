package me.moirai.discordbot.core.application.usecase.world;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.core.application.usecase.world.request.DeleteWorld;
import me.moirai.discordbot.core.domain.PermissionsFixture;
import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.core.domain.world.WorldFixture;
import me.moirai.discordbot.core.domain.world.WorldService;

@ExtendWith(MockitoExtension.class)
public class DeleteWorldHandlerTest {

    @Mock
    private WorldService domainService;

    @InjectMocks
    private DeleteWorldHandler handler;

    @Test
    public void errorWhenIdIsNull() {

        // Given
        String requesterDiscordId = "84REAC";
        String id = null;

        DeleteWorld config = DeleteWorld.build(id, requesterDiscordId);

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(config));
    }

    @Test
    public void deleteWorld() {

        // Given
        String requesterDiscordId = "84REAC";
        String id = "WRDID";

        World world = WorldFixture.publicWorld()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterDiscordId)
                        .usersAllowedToRead(Collections.emptyList())
                        .build())
                .build();

        DeleteWorld command = DeleteWorld.build(id, requesterDiscordId);

        when(domainService.getWorldById(anyString())).thenReturn(world);
        doNothing().when(domainService).deleteWorld(any(DeleteWorld.class));

        // When
        handler.handle(command);

        // Then
        verify(domainService, times(1)).deleteWorld(any());
    }
}
