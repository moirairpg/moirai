package me.moirai.discordbot.infrastructure.inbound.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.core.application.usecase.world.request.CreateWorldLorebookEntry;
import me.moirai.discordbot.core.application.usecase.world.request.DeleteWorldLorebookEntry;
import me.moirai.discordbot.core.application.usecase.world.request.UpdateWorldLorebookEntry;
import me.moirai.discordbot.infrastructure.inbound.api.request.CreateWorldLorebookEntryRequest;
import me.moirai.discordbot.infrastructure.inbound.api.request.UpdateWorldLorebookEntryRequest;

@ExtendWith(MockitoExtension.class)
public class WorldLorebookEntryRequestMapperTest {

    @InjectMocks
    private WorldLorebookEntryRequestMapper mapper;

    @Test
    public void mapCreationRequestToCommand() {

        // Given
        String worldId = "WRLDID";
        String requesterId = "RQSTRID";

        CreateWorldLorebookEntryRequest request = new CreateWorldLorebookEntryRequest();
        request.setName("Volin Habar");
        request.setDescription("Volin Habar is a warrior that fights with a sword.");
        request.setRegex("[Vv]olin [Hh]abar|[Vv]oha");
        request.setPlayerDiscordId("2423423423423");

        // When
        CreateWorldLorebookEntry command = mapper.toCommand(request, worldId, requesterId);

        // Then
        assertThat(command).isNotNull();
        assertThat(command.getName()).isEqualTo(request.getName());
        assertThat(command.getDescription()).isEqualTo(request.getDescription());
        assertThat(command.getRegex()).isEqualTo(request.getRegex());
        assertThat(command.getPlayerDiscordId()).isEqualTo(request.getPlayerDiscordId());
        assertThat(command.getWorldId()).isEqualTo(worldId);
        assertThat(command.getRequesterDiscordId()).isEqualTo(requesterId);
    }

    @Test
    public void mapUpdateRequestToCommand() {

        // Given
        String entryId = "ENTRID";
        String worldId = "WRLDID";
        String requesterId = "RQSTRID";

        UpdateWorldLorebookEntryRequest request = new UpdateWorldLorebookEntryRequest();
        request.setName("Volin Habar");
        request.setDescription("Volin Habar is a warrior that fights with a sword.");
        request.setRegex("[Vv]olin [Hh]abar|[Vv]oha");
        request.setPlayerDiscordId("2423423423423");
        request.setIsPlayerCharacter(true);

        // When
        UpdateWorldLorebookEntry command = mapper.toCommand(request, entryId, worldId, requesterId);

        // Then
        assertThat(command).isNotNull();
        assertThat(command.getId()).isEqualTo(entryId);
        assertThat(command.getWorldId()).isEqualTo(worldId);
        assertThat(command.getRequesterDiscordId()).isEqualTo(requesterId);
        assertThat(command.getName()).isEqualTo(request.getName());
        assertThat(command.getDescription()).isEqualTo(request.getDescription());
        assertThat(command.getRegex()).isEqualTo(request.getRegex());
        assertThat(command.getPlayerDiscordId()).isEqualTo(request.getPlayerDiscordId());
        assertThat(command.getWorldId()).isEqualTo(worldId);
        assertThat(command.getRequesterDiscordId()).isEqualTo(requesterId);
    }

    @Test
    public void mapDeleteRequestToCommand() {

        // Given
        String entryId = "ENTRID";
        String worldId = "WRLDID";
        String requesterId = "RQSTRID";

        // When
        DeleteWorldLorebookEntry command = mapper.toCommand(entryId, worldId, requesterId);

        // Then
        assertThat(command).isNotNull();
        assertThat(command.getLorebookEntryId()).isEqualTo(entryId);
        assertThat(command.getWorldId()).isEqualTo(worldId);
        assertThat(command.getWorldId()).isEqualTo(worldId);
        assertThat(command.getRequesterDiscordId()).isEqualTo(requesterId);
    }
}
