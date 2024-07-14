package me.moirai.discordbot.core.domain.channelconfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.CreateChannelConfig;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.CreateChannelConfigFixture;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.DeleteChannelConfig;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.GetChannelConfigById;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.UpdateChannelConfig;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.UpdateChannelConfigFixture;
import me.moirai.discordbot.core.domain.PermissionsFixture;
import me.moirai.discordbot.core.domain.Visibility;

@ExtendWith(MockitoExtension.class)
public class ChannelConfigServiceImplTest {

    @Mock
    private ChannelConfigRepository repository;

    @InjectMocks
    private ChannelConfigServiceImpl service;

    @Test
    public void createChannelConfig_whenValidData_thenChannelConfigIsCreated() {

        // Given
        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();
        CreateChannelConfig createChannelConfig = CreateChannelConfigFixture.sample().build();

        when(repository.save(any(ChannelConfig.class))).thenReturn(channelConfig);

        // When
        ChannelConfig createdChannelConfig = service.createFrom(createChannelConfig);

        // Then
        assertThat(createdChannelConfig).isNotNull();
        assertThat(createdChannelConfig.getName()).isEqualTo(channelConfig.getName());
        assertThat(createdChannelConfig.getPersonaId()).isEqualTo(channelConfig.getPersonaId());
        assertThat(createdChannelConfig.getWorldId()).isEqualTo(channelConfig.getWorldId());
        assertThat(createdChannelConfig.getModeration()).isEqualTo(channelConfig.getModeration());
        assertThat(createdChannelConfig.getVisibility()).isEqualTo(channelConfig.getVisibility());

        ModelConfiguration modelConfiguration = channelConfig.getModelConfiguration();
        assertThat(modelConfiguration.getAiModel()).isEqualTo(modelConfiguration.getAiModel());
        assertThat(modelConfiguration.getFrequencyPenalty()).isEqualTo(modelConfiguration.getFrequencyPenalty());
        assertThat(modelConfiguration.getMaxTokenLimit()).isEqualTo(modelConfiguration.getMaxTokenLimit());
        assertThat(modelConfiguration.getPresencePenalty()).isEqualTo(modelConfiguration.getPresencePenalty());
        assertThat(modelConfiguration.getTemperature()).isEqualTo(modelConfiguration.getTemperature());
        assertThat(modelConfiguration.getLogitBias()).isEqualTo(modelConfiguration.getLogitBias());
        assertThat(modelConfiguration.getStopSequences()).isEqualTo(modelConfiguration.getStopSequences());
    }

    @Test
    public void updateChannelConfig_whenValidData_thenThrowException() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        UpdateChannelConfig command = UpdateChannelConfigFixture.sample()
                .requesterDiscordId(requesterId)
                .build();

        ChannelConfig unchangedChannelConfig = ChannelConfigFixture.sample()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        ChannelConfig expectedUpdatedChannelConfig = ChannelConfigFixture.sample()
                .id(id)
                .name("New name")
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(unchangedChannelConfig));
        when(repository.save(any(ChannelConfig.class))).thenReturn(expectedUpdatedChannelConfig);

        // When
        ChannelConfig result = service.update(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(expectedUpdatedChannelConfig.getName());
    }

    @Test
    public void updateChannelConfig_whenPrivateToBeMadePublic_thenChannelConfigIsMadePublic() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        UpdateChannelConfig command = UpdateChannelConfigFixture.sample()
                .requesterDiscordId(requesterId)
                .visibility("public")
                .build();

        ChannelConfig unchangedChannelConfig = ChannelConfigFixture.sample()
                .visibility(Visibility.PRIVATE)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        ChannelConfig expectedUpdatedChannelConfig = ChannelConfigFixture.sample()
                .id(id)
                .visibility(Visibility.PUBLIC)
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(unchangedChannelConfig));
        when(repository.save(any(ChannelConfig.class))).thenReturn(expectedUpdatedChannelConfig);

        // When
        ChannelConfig result = service.update(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getVisibility()).isEqualTo(expectedUpdatedChannelConfig.getVisibility());
    }

    @Test
    public void updateChannelConfig_whenInvalidVisibility_thenNothingIsChanged() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        UpdateChannelConfig command = UpdateChannelConfigFixture.sample()
                .requesterDiscordId(requesterId)
                .visibility("invalid")
                .build();

        ChannelConfig unchangedChannelConfig = ChannelConfigFixture.sample()
                .visibility(Visibility.PRIVATE)
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        ChannelConfig expectedUpdatedChannelConfig = ChannelConfigFixture.sample()
                .id(id)
                .visibility(Visibility.PRIVATE)
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(unchangedChannelConfig));
        when(repository.save(any(ChannelConfig.class))).thenReturn(expectedUpdatedChannelConfig);

        // When
        ChannelConfig result = service.update(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getVisibility()).isEqualTo(expectedUpdatedChannelConfig.getVisibility());
    }

    @Test
    public void updateChannelConfig_whenEmptyUpdateFields_thenNothingIsChanged() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        UpdateChannelConfig command = UpdateChannelConfigFixture.sample()
                .requesterDiscordId(requesterId)
                .id(id)
                .aiModel(null)
                .discordChannelId(null)
                .frequencyPenalty(null)
                .logitBiasToAdd(null)
                .logitBiasToRemove(null)
                .maxTokenLimit(null)
                .moderation(null)
                .name(null)
                .personaId(null)
                .presencePenalty(null)
                .stopSequencesToAdd(null)
                .stopSequencesToRemove(null)
                .usersAllowedToReadToAdd(null)
                .usersAllowedToReadToRemove(null)
                .usersAllowedToWriteToAdd(null)
                .usersAllowedToWriteToRemove(null)
                .temperature(null)
                .visibility(null)
                .worldId(null)
                .build();

        ChannelConfig unchangedChannelConfig = ChannelConfigFixture.sample()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(unchangedChannelConfig));
        when(repository.save(any(ChannelConfig.class))).thenReturn(unchangedChannelConfig);

        // When
        ChannelConfig result = service.update(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(unchangedChannelConfig.getName());
    }

    @Test
    public void updateChannelConfig_whenChannelConfigNotFound_thenThrowException() {

        // Given
        String id = "CHCONFID";

        UpdateChannelConfig command = UpdateChannelConfig.builder()
                .id(id)
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> service.update(command));
    }

    @Test
    public void updateConfig_whenInvalidPermission_thenThrowException() {

        // Given
        String id = "CHCONFID";

        UpdateChannelConfig command = UpdateChannelConfig.builder()
                .id(id)
                .requesterDiscordId("USRID")
                .build();

        ChannelConfig world = ChannelConfigFixture.sample()
                .id(id)
                .name("New name")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId("ANTHRUSR")
                        .build())
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(world));

        // Then
        assertThrows(AssetAccessDeniedException.class, () -> service.update(command));
    }

    @Test
    public void deleteChannelConfig_whenChannelConfigNotFound_thenThrowException() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        DeleteChannelConfig command = DeleteChannelConfig.build(id, requesterId);

        when(repository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> service.deleteChannelConfig(command));
    }

    @Test
    public void deleteChannelConfig_whenInvalidPermission_thenThrowException() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        DeleteChannelConfig command = DeleteChannelConfig.build(id, requesterId);

        ChannelConfig persona = ChannelConfigFixture.sample()
                .id(id)
                .name("New name")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId("ANTHRUSR")
                        .build())
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(persona));

        // Then
        assertThrows(AssetAccessDeniedException.class, () -> service.deleteChannelConfig(command));
    }

    @Test
    public void deleteChannelConfig_whenProperPermission_thenChannelConfigIsDeleted() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        DeleteChannelConfig command = DeleteChannelConfig.build(id, requesterId);

        ChannelConfig persona = ChannelConfigFixture.sample()
                .id(id)
                .name("New name")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(persona));

        // Then
        service.deleteChannelConfig(command);
    }

    @Test
    public void findChannelConfig_whenValidId_thenChannelConfigIsReturned() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        GetChannelConfigById query = GetChannelConfigById.build(id, requesterId);

        ChannelConfig persona = ChannelConfigFixture.sample()
                .id(id)
                .name("New name")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(persona));

        // When
        ChannelConfig result = service.getChannelConfigById(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(persona.getName());
    }

    @Test
    public void findChannelConfig_whenChannelConfigNotFound_thenThrowException() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        GetChannelConfigById query = GetChannelConfigById.build(id, requesterId);

        when(repository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> service.getChannelConfigById(query));
    }

    @Test
    public void findChannelConfig_whenInvalidPermission_thenThrowException() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        GetChannelConfigById query = GetChannelConfigById.build(id, requesterId);

        ChannelConfig persona = ChannelConfigFixture.sample()
                .id(id)
                .name("New name")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId("ANTHRUSR")
                        .usersAllowedToRead(Collections.emptyList())
                        .build())
                .build();

        when(repository.findById(anyString())).thenReturn(Optional.of(persona));

        // Then
        assertThrows(AssetAccessDeniedException.class, () -> service.getChannelConfigById(query));
    }
}
