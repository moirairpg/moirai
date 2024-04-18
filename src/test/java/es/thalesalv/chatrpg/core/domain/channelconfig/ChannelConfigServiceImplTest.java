package es.thalesalv.chatrpg.core.domain.channelconfig;

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

import es.thalesalv.chatrpg.common.exception.AssetAccessDeniedException;
import es.thalesalv.chatrpg.common.exception.AssetNotFoundException;
import es.thalesalv.chatrpg.core.application.command.channelconfig.CreateChannelConfig;
import es.thalesalv.chatrpg.core.application.command.channelconfig.CreateChannelConfigFixture;
import es.thalesalv.chatrpg.core.application.command.channelconfig.DeleteChannelConfig;
import es.thalesalv.chatrpg.core.application.command.channelconfig.UpdateChannelConfig;
import es.thalesalv.chatrpg.core.application.command.channelconfig.UpdateChannelConfigFixture;
import es.thalesalv.chatrpg.core.application.query.channelconfig.GetChannelConfigById;
import es.thalesalv.chatrpg.core.domain.PermissionsFixture;

@ExtendWith(MockitoExtension.class)
public class ChannelConfigServiceImplTest {

    @Mock
    private ChannelConfigRepository repository;

    @InjectMocks
    private ChannelConfigServiceImpl service;

    @Test
    public void createChannelConfig() {

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
        assertThat(modelConfiguration.getMessageHistorySize()).isEqualTo(modelConfiguration.getMessageHistorySize());
        assertThat(modelConfiguration.getPresencePenalty()).isEqualTo(modelConfiguration.getPresencePenalty());
        assertThat(modelConfiguration.getTemperature()).isEqualTo(modelConfiguration.getTemperature());
        assertThat(modelConfiguration.getLogitBias()).isEqualTo(modelConfiguration.getLogitBias());
        assertThat(modelConfiguration.getStopSequences()).isEqualTo(modelConfiguration.getStopSequences());
    }

    @Test
    public void errorWhenUpdateChannelConfigNotFound() {

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
    public void updateChannelConfig() {

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
    public void errorWhenUpdateChannelConfigAccessDenied() {

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
    public void errorWhenDeleteChannelConfigNotFound() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        DeleteChannelConfig command = DeleteChannelConfig.build(id, requesterId);

        when(repository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> service.deleteChannelConfig(command));
    }

    @Test
    public void errorWhenDeleteChannelConfigAccessDenied() {

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
    public void deleteChannelConfig() {

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
    public void findChannelConfigById() {

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
    public void errorWhenFindChannelConfigNotFound() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        GetChannelConfigById query = GetChannelConfigById.build(id, requesterId);

        when(repository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> service.getChannelConfigById(query));
    }

    @Test
    public void errorWhenFindChannelConfigAccessDenied() {

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
