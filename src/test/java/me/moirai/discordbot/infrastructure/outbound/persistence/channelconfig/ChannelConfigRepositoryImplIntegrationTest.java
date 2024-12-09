package me.moirai.discordbot.infrastructure.outbound.persistence.channelconfig;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.transaction.Transactional;
import me.moirai.discordbot.AbstractIntegrationTest;
import me.moirai.discordbot.core.domain.Visibility;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigDomainRepository;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigFixture;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteEntity;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;

public class ChannelConfigRepositoryImplIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ChannelConfigDomainRepository repository;

    @Autowired
    private ChannelConfigJpaRepository jpaRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

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

    @Test
    public void updateChannelConfig() {

        // Given
        ChannelConfig originalChannelConfig = repository.save(ChannelConfigFixture.sample()
                .id(null)
                .build());

        ChannelConfig worldToUbeUpdated = ChannelConfigFixture.sample()
                .id(originalChannelConfig.getId())
                .visibility(Visibility.PUBLIC)
                .version(originalChannelConfig.getVersion())
                .build();

        // When
        ChannelConfig updatedChannelConfig = repository.save(worldToUbeUpdated);

        // Then
        assertThat(originalChannelConfig.getVersion()).isZero();
        assertThat(updatedChannelConfig.getVersion()).isOne();
    }

    @Test
    @Transactional
    public void deleteChannelConfig_whenIsFavorite_thenDeleteFavorites() {

        // Given
        String userId = "1234";
        ChannelConfig channelConfig = repository.save(ChannelConfigFixture.sample()
                .id(null)
                .build());

        FavoriteEntity favorite = favoriteRepository.save(FavoriteEntity.builder()
                .playerDiscordId(userId)
                .assetId(channelConfig.getId())
                .assetType("channel_config")
                .build());

        // When
        repository.deleteById(channelConfig.getId());

        // Then
        assertThat(repository.findById(channelConfig.getId())).isNotNull().isEmpty();
        assertThat(favoriteRepository.existsById(favorite.getId())).isFalse();
    }
}
