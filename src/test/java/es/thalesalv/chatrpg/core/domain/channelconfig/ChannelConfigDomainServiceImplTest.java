package es.thalesalv.chatrpg.core.domain.channelconfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.core.application.command.channelconfig.CreateChannelConfig;

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

        CreateChannelConfig createChannelConfig = CreateChannelConfig.builder()
                .name(channelConfig.getName())
                .personaId(channelConfig.getPersonaId())
                .worldId(channelConfig.getWorldId())
                .aiModel(channelConfig.getModelConfiguration().getAiModel().toString())
                .logitBias(channelConfig.getModelConfiguration().getLogitBias())
                .stopSequences(channelConfig.getModelConfiguration().getStopSequences())
                .frequencyPenalty(channelConfig.getModelConfiguration().getFrequencyPenalty())
                .maxTokenLimit(channelConfig.getModelConfiguration().getMaxTokenLimit())
                .messageHistorySize(channelConfig.getModelConfiguration().getMessageHistorySize())
                .moderation("strict")
                .creatorDiscordId(channelConfig.getPermissions().getOwnerDiscordId())
                .readerUsers(channelConfig.getPermissions().getUsersAllowedToRead())
                .writerUsers(channelConfig.getPermissions().getUsersAllowedToWrite())
                .temperature(1.7)
                .visibility("private")
                .build();

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
}
