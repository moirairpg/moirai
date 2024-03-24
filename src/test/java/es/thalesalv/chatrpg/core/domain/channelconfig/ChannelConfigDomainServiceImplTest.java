package es.thalesalv.chatrpg.core.domain.channelconfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.common.exception.AssetNotFoundException;
import es.thalesalv.chatrpg.core.application.command.channelconfig.CreateChannelConfig;
import es.thalesalv.chatrpg.core.application.command.channelconfig.CreateChannelConfigFixture;
import es.thalesalv.chatrpg.core.application.command.channelconfig.UpdateChannelConfig;

@ExtendWith(MockitoExtension.class)
public class ChannelConfigDomainServiceImplTest {

    @Mock
    private ChannelConfigRepository repository;

    @InjectMocks
    private ChannelConfigDomainServiceImpl service;

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

        UpdateChannelConfig command = UpdateChannelConfig.builder()
                .id(id)
                .name("Name")
                .worldId("WRLDID")
                .personaId("PRSNID")
                .moderation("STRICT")
                .visibility("PRIVATE")
                .creatorDiscordId("CRTID")
                .aiModel("gpt35-16k")
                .frequencyPenalty(0.2)
                .presencePenalty(0.2)
                .maxTokenLimit(100)
                .messageHistorySize(25)
                .temperature(1.0)
                .build();

        ChannelConfig unchangedChannelConfig = ChannelConfigFixture.sample().build();

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
}
