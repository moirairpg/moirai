package me.moirai.discordbot.infrastructure.outbound.persistence.channelconfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import me.moirai.discordbot.AbstractIntegrationTest;
import me.moirai.discordbot.core.application.port.ChannelConfigQueryRepository;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.SearchChannelConfigsWithReadAccess;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.SearchChannelConfigsWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.SearchFavoriteChannelConfigs;
import me.moirai.discordbot.core.application.usecase.channelconfig.result.GetChannelConfigResult;
import me.moirai.discordbot.core.application.usecase.channelconfig.result.SearchChannelConfigsResult;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteEntity;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;

public class ChannelConfigQueryRepositoryImplTest extends AbstractIntegrationTest {

    @Autowired
    private ChannelConfigQueryRepository repository;

    @Autowired
    private ChannelConfigJpaRepository jpaRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @BeforeEach
    public void before() {
        jpaRepository.deleteAllInBatch();
    }

    @Test
    public void retrieveChannelConfigByChannelId() {

        // Given
        String discordChannelId = "234234";
        ChannelConfigEntity channelConfig = jpaRepository.save(ChannelConfigEntityFixture.sample()
                .id(null)
                .discordChannelId(discordChannelId)
                .build());

        // When
        Optional<ChannelConfig> retrievedChannelConfigOptional = repository.findByDiscordChannelId(discordChannelId);

        // Then
        assertThat(retrievedChannelConfigOptional).isNotNull().isNotEmpty();

        ChannelConfig retrievedChannelConfig = retrievedChannelConfigOptional.get();
        assertThat(retrievedChannelConfig.getId()).isEqualTo(channelConfig.getId());
    }

    @Test
    public void emptyResultWhenAssetDoesntExistGettingByChannelId() {

        // Given
        String channelConfigId = "WRLDID";

        // When
        Optional<ChannelConfig> retrievedChannelConfigOptional = repository.findByDiscordChannelId(channelConfigId);

        // Then
        assertThat(retrievedChannelConfigOptional).isNotNull().isEmpty();
    }

    @Test
    public void returnAllChannelConfigsWhenSearchingWithoutParameters() {

        // Given
        String ownerDiscordId = "586678721356875";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .ownerDiscordId(ownerDiscordId)
                .discordChannelId("CHNLID1")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .ownerDiscordId("580485734")
                .usersAllowedToRead(Collections.singletonList(ownerDiscordId))
                .discordChannelId("CHNLID2")
                .build();

        ChannelConfigEntity gpt354k = ChannelConfigEntityFixture.sample()
                .id(null)
                .ownerDiscordId("580485734")
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchChannelConfigsWithReadAccess query = SearchChannelConfigsWithReadAccess.builder()
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(channelConfigs.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void returnAllChannelConfigsWhenSearchingWithoutParametersAsc() {

        // Given
        String ownerDiscordId = "586678721356875";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        ChannelConfigEntity gpt354k = ChannelConfigEntityFixture.sample()
                .id(null)
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini()
                        .aiModel("gpt35-16k")
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchChannelConfigsWithReadAccess query = SearchChannelConfigsWithReadAccess.builder()
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty();

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(channelConfigs.get(1).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(channelConfigs.get(2).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void returnAllChannelConfigsWhenSearchingWithoutParametersDesc() {

        // Given
        String ownerDiscordId = "586678721356875";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        ChannelConfigEntity gpt354k = ChannelConfigEntityFixture.sample()
                .id(null)
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini()
                        .aiModel("gpt35-16k")
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchChannelConfigsWithReadAccess query = SearchChannelConfigsWithReadAccess.builder()
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty();

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt354k.getName());
        assertThat(channelConfigs.get(1).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(channelConfigs.get(2).getName()).isEqualTo(gpt4Omni.getName());
    }

    @Test
    public void searchChannelConfigOrderByNameAsc() {

        // Given
        String ownerDiscordId = "586678721356875";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        ChannelConfigEntity gpt354k = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 3")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini()
                        .aiModel("gpt35-16k")
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchChannelConfigsWithReadAccess query = SearchChannelConfigsWithReadAccess.builder()
                .sortByField("name")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty();

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(channelConfigs.get(1).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(channelConfigs.get(2).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void searchChannelConfigOrderByNameDesc() {

        // Given
        String ownerDiscordId = "586678721356875";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        ChannelConfigEntity gpt354k = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 3")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini()
                        .aiModel("gpt35-16k")
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchChannelConfigsWithReadAccess query = SearchChannelConfigsWithReadAccess.builder()
                .sortByField("name")
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty();

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt354k.getName());
        assertThat(channelConfigs.get(1).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(channelConfigs.get(2).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchChannelConfigOrderByAiModelAsc() {

        // Given
        String ownerDiscordId = "586678721356875";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini));

        SearchChannelConfigsWithReadAccess query = SearchChannelConfigsWithReadAccess.builder()
                .sortByField("modelConfiguration.aiModel")
                .direction("ASC")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(channelConfigs.get(1).getName()).isEqualTo(gpt4Omni.getName());
    }

    @Test
    public void searchChannelConfigOrderByAiModelDesc() {

        // Given
        String ownerDiscordId = "586678721356875";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini));

        SearchChannelConfigsWithReadAccess query = SearchChannelConfigsWithReadAccess.builder()
                .sortByField("modelConfiguration.aiModel")
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(channelConfigs.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchChannelConfigOrderByModerationAsc() {

        // Given
        String ownerDiscordId = "586678721356875";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .moderation("STRICT")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .moderation("PERMISSIVE")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        ChannelConfigEntity gpt354k = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 3")
                .moderation("PERMISSIVE")
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchChannelConfigsWithReadAccess query = SearchChannelConfigsWithReadAccess.builder()
                .sortByField("moderation")
                .direction("ASC")
                .page(1)
                .items(10)
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty();

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(channelConfigs.get(1).getName()).isEqualTo(gpt354k.getName());
        assertThat(channelConfigs.get(2).getName()).isEqualTo(gpt4Omni.getName());
    }

    @Test
    public void searchChannelConfigOrderByModerationDesc() {

        // Given
        String ownerDiscordId = "586678721356875";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini));

        SearchChannelConfigsWithReadAccess query = SearchChannelConfigsWithReadAccess.builder()
                .sortByField("modelConfiguration.aiModel")
                .direction("DESC")
                .page(1)
                .items(10)
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(channelConfigs.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchChannelConfigFilterByAiModel() {

        // Given
        String ownerDiscordId = "586678721356875";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini));

        SearchChannelConfigsWithReadAccess query = SearchChannelConfigsWithReadAccess.builder()
                .aiModel("gpt4-mini")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchChannelConfig_whenReadAccess_andFilterByVisibility_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .visibility("public")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .visibility("private")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini));

        SearchChannelConfigsWithReadAccess query = SearchChannelConfigsWithReadAccess.builder()
                .visibility("private")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchChannelConfig_whenWriteAccess_andFilterByVisibility_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .visibility("public")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .visibility("private")
                .ownerDiscordId(ownerDiscordId)
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini));

        SearchChannelConfigsWithWriteAccess query = SearchChannelConfigsWithWriteAccess.builder()
                .visibility("private")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchChannelConfigFilterByName() {

        // Given
        String ownerDiscordId = "586678721356875";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        ChannelConfigEntity gpt354k = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 3")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini()
                        .aiModel("gpt35-16k")
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchChannelConfigsWithReadAccess query = SearchChannelConfigsWithReadAccess.builder()
                .name("Number 2")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchChannelConfigFilterByModeration() {

        // Given
        String ownerDiscordId = "586678721356875";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .moderation("STRICT")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .moderation("PERMISSIVE")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        ChannelConfigEntity gpt354k = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 3")
                .moderation("PERMISSIVE")
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchChannelConfigsWithReadAccess query = SearchChannelConfigsWithReadAccess.builder()
                .moderation("PERMISSIVE")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(channelConfigs.get(1).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void returnAllChannelConfigsWhenSearchingWithoutParametersShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .ownerDiscordId(ownerDiscordId)
                .discordChannelId("CHNLID1")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .ownerDiscordId("580485734")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .discordChannelId("CHNLID2")
                .build();

        ChannelConfigEntity gpt354k = ChannelConfigEntityFixture.sample()
                .id(null)
                .ownerDiscordId("580485734")
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchChannelConfigsWithWriteAccess query = SearchChannelConfigsWithWriteAccess.builder()
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(channelConfigs.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void returnAllChannelConfigsWhenSearchingWithoutParametersAscShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .ownerDiscordId(ownerDiscordId)
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        ChannelConfigEntity gpt354k = ChannelConfigEntityFixture.sample()
                .id(null)
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini()
                        .aiModel("gpt35-16k")
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchChannelConfigsWithWriteAccess query = SearchChannelConfigsWithWriteAccess.builder()
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(channelConfigs.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void returnAllChannelConfigsWhenSearchingWithoutParametersDescShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .ownerDiscordId(ownerDiscordId)
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        ChannelConfigEntity gpt354k = ChannelConfigEntityFixture.sample()
                .id(null)
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini()
                        .aiModel("gpt35-16k")
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchChannelConfigsWithWriteAccess query = SearchChannelConfigsWithWriteAccess.builder()
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(channelConfigs.get(1).getName()).isEqualTo(gpt4Omni.getName());
    }

    @Test
    public void searchChannelConfigOrderByNameAscShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .ownerDiscordId(ownerDiscordId)
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        ChannelConfigEntity gpt354k = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 3")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini()
                        .aiModel("gpt35-16k")
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchChannelConfigsWithWriteAccess query = SearchChannelConfigsWithWriteAccess.builder()
                .sortByField("name")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(channelConfigs.get(1).getName()).isEqualTo(gpt4Omni.getName());
    }

    @Test
    public void searchChannelConfigOrderByNameDescShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .ownerDiscordId(ownerDiscordId)
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        ChannelConfigEntity gpt354k = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 3")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini()
                        .aiModel("gpt35-16k")
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchChannelConfigsWithWriteAccess query = SearchChannelConfigsWithWriteAccess.builder()
                .sortByField("name")
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(channelConfigs.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchChannelConfigOrderByAiModelAscShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini));

        SearchChannelConfigsWithWriteAccess query = SearchChannelConfigsWithWriteAccess.builder()
                .sortByField("modelConfiguration.aiModel")
                .direction("ASC")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchChannelConfigOrderByAiModelDescShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini));

        SearchChannelConfigsWithWriteAccess query = SearchChannelConfigsWithWriteAccess.builder()
                .sortByField("modelConfiguration.aiModel")
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchChannelConfigOrderByModerationAscShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .moderation("STRICT")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .moderation("PERMISSIVE")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        ChannelConfigEntity gpt354k = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 3")
                .ownerDiscordId(ownerDiscordId)
                .moderation("PERMISSIVE")
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchChannelConfigsWithWriteAccess query = SearchChannelConfigsWithWriteAccess.builder()
                .sortByField("moderation")
                .direction("ASC")
                .page(1)
                .items(10)
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(channelConfigs.get(1).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void searchChannelConfigOrderByModerationDescShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini));

        SearchChannelConfigsWithWriteAccess query = SearchChannelConfigsWithWriteAccess.builder()
                .sortByField("modelConfiguration.aiModel")
                .direction("DESC")
                .page(1)
                .items(10)
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchChannelConfigFilterByAiModelShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        ChannelConfigEntity gpt354k = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 3")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini()
                        .aiModel("gpt35-16k")
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchChannelConfigsWithWriteAccess query = SearchChannelConfigsWithWriteAccess.builder()
                .aiModel("gpt35-16k")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void searchChannelConfigFilterByNameShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        ChannelConfigEntity gpt354k = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 3")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini()
                        .aiModel("gpt35-16k")
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchChannelConfigsWithWriteAccess query = SearchChannelConfigsWithWriteAccess.builder()
                .name("Number 2")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchChannelConfigFilterByModerationShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .moderation("STRICT")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .ownerDiscordId(ownerDiscordId)
                .moderation("PERMISSIVE")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        ChannelConfigEntity gpt354k = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 3")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .moderation("PERMISSIVE")
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchChannelConfigsWithWriteAccess query = SearchChannelConfigsWithWriteAccess.builder()
                .moderation("PERMISSIVE")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(channelConfigs.get(1).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void searchChannelConfigFilterByGameMode() {

        // Given
        String ownerDiscordId = "586678721356875";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .moderation("STRICT")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .gameMode("CHAT")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .ownerDiscordId(ownerDiscordId)
                .moderation("PERMISSIVE")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .gameMode("RPG")
                .build();

        ChannelConfigEntity gpt354k = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 3")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .moderation("PERMISSIVE")
                .discordChannelId("CHNLID3")
                .gameMode("AUTHOR")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchChannelConfigsWithReadAccess query = SearchChannelConfigsWithReadAccess.builder()
                .gameMode("RPG")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchChannelConfigFilterByGameModeShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        ChannelConfigEntity gpt4Omni = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .moderation("STRICT")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .gameMode("AUTHOR")
                .build();

        ChannelConfigEntity gpt4Mini = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .ownerDiscordId(ownerDiscordId)
                .moderation("PERMISSIVE")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .gameMode("CHAT")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .build();

        ChannelConfigEntity gpt354k = ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 3")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .moderation("PERMISSIVE")
                .discordChannelId("CHNLID3")
                .gameMode("RPG")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchChannelConfigsWithWriteAccess query = SearchChannelConfigsWithWriteAccess.builder()
                .gameMode("CHAT")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);

        List<GetChannelConfigResult> channelConfigs = result.getResults();
        assertThat(channelConfigs.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchFavoriteChannelConfigs_whenNoFilters_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";

        ChannelConfigEntity gpt4Omni = jpaRepository.save(ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .visibility("public")
                .build());

        ChannelConfigEntity gpt4Mini = jpaRepository.save(ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .visibility("public")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("channel_config")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("channel_config")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchFavoriteChannelConfigs query = SearchFavoriteChannelConfigs.builder()
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);
    }

    @Test
    public void searchFavoriteChannelConfigs_whenFilterByName_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        String nameToSearch = "nameToBeSearched";
        ChannelConfigEntity gpt4Omni = jpaRepository.save(ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .visibility("public")
                .build());

        ChannelConfigEntity gpt4Mini = jpaRepository.save(ChannelConfigEntityFixture.sample()
                .id(null)
                .name(nameToSearch)
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .visibility("public")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("channel_config")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("channel_config")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchFavoriteChannelConfigs query = SearchFavoriteChannelConfigs.builder()
                .requesterDiscordId(ownerDiscordId)
                .name(nameToSearch)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
    }

    @Test
    public void searchFavoriteChannelConfigs_whenFilterByVisibility_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        String visibility = "public";
        ChannelConfigEntity gpt4Omni = jpaRepository.save(ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .visibility(visibility)
                .build());

        ChannelConfigEntity gpt4Mini = jpaRepository.save(ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .visibility("private")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("channel_config")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("channel_config")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchFavoriteChannelConfigs query = SearchFavoriteChannelConfigs.builder()
                .requesterDiscordId(ownerDiscordId)
                .visibility(visibility)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
    }

    @Test
    public void searchFavoriteChannelConfigs_whenFilterByGameMode_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        String gameMode = "chat";
        ChannelConfigEntity gpt4Omni = jpaRepository.save(ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .gameMode(gameMode)
                .build());

        ChannelConfigEntity gpt4Mini = jpaRepository.save(ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .visibility("private")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("channel_config")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("channel_config")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchFavoriteChannelConfigs query = SearchFavoriteChannelConfigs.builder()
                .requesterDiscordId(ownerDiscordId)
                .gameMode(gameMode)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
    }

    @Test
    public void searchFavoriteChannelConfigs_whenFilterByAiModel_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        String aiModel = "gpt4-omni";
        ChannelConfigEntity gpt4Omni = jpaRepository.save(ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build());

        ChannelConfigEntity gpt4Mini = jpaRepository.save(ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .visibility("private")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("channel_config")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("channel_config")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchFavoriteChannelConfigs query = SearchFavoriteChannelConfigs.builder()
                .requesterDiscordId(ownerDiscordId)
                .aiModel(aiModel)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
    }

    @Test
    public void searchFavoriteChannelConfigs_whenFilterByModeration_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        String moderation = "strict";
        ChannelConfigEntity gpt4Omni = jpaRepository.save(ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .moderation(moderation)
                .build());

        ChannelConfigEntity gpt4Mini = jpaRepository.save(ChannelConfigEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .moderation("disabled")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("channel_config")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("channel_config")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchFavoriteChannelConfigs query = SearchFavoriteChannelConfigs.builder()
                .requesterDiscordId(ownerDiscordId)
                .moderation(moderation)
                .build();

        // When
        SearchChannelConfigsResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
    }
}
