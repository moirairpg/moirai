package me.moirai.discordbot.infrastructure.outbound.persistence.channelconfig;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import me.moirai.discordbot.AbstractIntegrationTest;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigFixture;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigDomainRepository;

public class ChannelConfigRepositoryImplIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ChannelConfigDomainRepository repository;

    @Autowired
    private ChannelConfigJpaRepository jpaRepository;

    @BeforeEach
    public void before() {
        jpaRepository.deleteAllInBatch();
    }

    @Test
    public void createChannelConfig() {

        // Given
        ChannelConfig channelConfig = ChannelConfigFixture.sample()
                .id(null)
                .build();

        // When
        ChannelConfig createdChannelConfig = repository.save(channelConfig);

        // Then
        assertThat(createdChannelConfig).isNotNull();

        assertThat(createdChannelConfig.getCreationDate()).isNotNull();
        assertThat(createdChannelConfig.getLastUpdateDate()).isNotNull();

        assertThat(createdChannelConfig.getModelConfiguration().getAiModel().getInternalModelName())
                .isEqualTo((channelConfig.getModelConfiguration().getAiModel().getInternalModelName()));

        assertThat(createdChannelConfig.getModelConfiguration().getFrequencyPenalty())
                .isEqualTo((channelConfig.getModelConfiguration().getFrequencyPenalty()));

        assertThat(createdChannelConfig.getModelConfiguration().getPresencePenalty())
                .isEqualTo((channelConfig.getModelConfiguration().getPresencePenalty()));

        assertThat(createdChannelConfig.getModelConfiguration().getTemperature())
                .isEqualTo((channelConfig.getModelConfiguration().getTemperature()));

        assertThat(createdChannelConfig.getModelConfiguration().getLogitBias())
                .isEqualTo((channelConfig.getModelConfiguration().getLogitBias()));

        assertThat(createdChannelConfig.getModelConfiguration().getMaxTokenLimit())
                .isEqualTo((channelConfig.getModelConfiguration().getMaxTokenLimit()));

        assertThat(createdChannelConfig.getModelConfiguration().getStopSequences())
                .isEqualTo((channelConfig.getModelConfiguration().getStopSequences()));

    }

    @Test
    public void retrieveChannelConfigById() {

        // Given
        ChannelConfig channelConfig = repository.save(ChannelConfigFixture.sample()
                .id(null)
                .build());

        // When
        Optional<ChannelConfig> retrievedChannelConfigOptional = repository.findById(channelConfig.getId());

        // Then
        assertThat(retrievedChannelConfigOptional).isNotNull().isNotEmpty();

        ChannelConfig retrievedChannelConfig = retrievedChannelConfigOptional.get();
        assertThat(retrievedChannelConfig.getId()).isEqualTo(channelConfig.getId());
    }

    @Test
    public void emptyResultWhenAssetDoesntExist() {

        // Given
        String channelConfigId = "WRLDID";

        // When
        Optional<ChannelConfig> retrievedChannelConfigOptional = repository.findById(channelConfigId);

        // Then
        assertThat(retrievedChannelConfigOptional).isNotNull().isEmpty();
    }

    @Test
    public void deleteChannelConfig() {

        // Given
        ChannelConfig channelConfig = repository.save(ChannelConfigFixture.sample()
                .id(null)
                .build());

        // When
        repository.deleteById(channelConfig.getId());

        // Then
        assertThat(repository.findById(channelConfig.getId())).isNotNull().isEmpty();
    }
}
