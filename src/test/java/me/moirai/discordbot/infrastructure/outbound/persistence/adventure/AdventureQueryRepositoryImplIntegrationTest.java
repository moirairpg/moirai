package me.moirai.discordbot.infrastructure.outbound.persistence.adventure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import me.moirai.discordbot.AbstractIntegrationTest;
import me.moirai.discordbot.core.application.port.AdventureQueryRepository;
import me.moirai.discordbot.core.application.usecase.adventure.request.SearchAdventures;
import me.moirai.discordbot.core.application.usecase.adventure.result.GetAdventureResult;
import me.moirai.discordbot.core.application.usecase.adventure.result.SearchAdventuresResult;
import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteEntity;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;

public class AdventureQueryRepositoryImplIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AdventureQueryRepository repository;

    @Autowired
    private AdventureJpaRepository jpaRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @BeforeEach
    public void before() {
        jpaRepository.deleteAllInBatch();
    }

    @Test
    public void retrieveAdventureById() {

        // Given
        String adventureId = "234234";
        AdventureEntity adventure = jpaRepository.save(AdventureEntityFixture.sample()
                .id(adventureId)
                .discordChannelId(adventureId)
                .build());

        // When
        Optional<Adventure> retrievedAdventureOptional = repository.findById(adventureId);

        // Then
        assertThat(retrievedAdventureOptional).isNotNull().isNotEmpty();

        Adventure retrievedAdventure = retrievedAdventureOptional.get();
        assertThat(retrievedAdventure.getId()).isEqualTo(adventure.getId());
        assertThat(retrievedAdventure.getAdventureStart()).isEqualTo(adventure.getAdventureStart());
        assertThat(retrievedAdventure.getDescription()).isEqualTo(adventure.getDescription());
        assertThat(retrievedAdventure.getDiscordChannelId()).isEqualTo(adventure.getDiscordChannelId());
        assertThat(retrievedAdventure.getGameMode().name()).isEqualTo(adventure.getGameMode());
        assertThat(retrievedAdventure.getName()).isEqualTo(adventure.getName());
        assertThat(retrievedAdventure.getOwnerDiscordId()).isEqualTo(adventure.getOwnerDiscordId());
        assertThat(retrievedAdventure.getPersonaId()).isEqualTo(adventure.getPersonaId());
        assertThat(retrievedAdventure.getVisibility().name()).isEqualTo(adventure.getVisibility());
        assertThat(retrievedAdventure.getModeration().name()).isEqualTo(adventure.getModeration());
        assertThat(retrievedAdventure.getWorldId()).isEqualTo(adventure.getWorldId());

        assertThat(retrievedAdventure.getModelConfiguration().getAiModel().toString())
                .isEqualTo(adventure.getModelConfiguration().getAiModel());
        assertThat(retrievedAdventure.getModelConfiguration().getFrequencyPenalty())
                .isEqualTo(adventure.getModelConfiguration().getFrequencyPenalty());
        assertThat(retrievedAdventure.getModelConfiguration().getLogitBias())
                .isEqualTo(adventure.getModelConfiguration().getLogitBias());
        assertThat(retrievedAdventure.getModelConfiguration().getMaxTokenLimit())
                .isEqualTo(adventure.getModelConfiguration().getMaxTokenLimit());
        assertThat(retrievedAdventure.getModelConfiguration().getPresencePenalty())
                .isEqualTo(adventure.getModelConfiguration().getPresencePenalty());
        assertThat(retrievedAdventure.getModelConfiguration().getStopSequences())
                .isEqualTo(adventure.getModelConfiguration().getStopSequences());
        assertThat(retrievedAdventure.getModelConfiguration().getTemperature())
                .isEqualTo(adventure.getModelConfiguration().getTemperature());

        assertThat(retrievedAdventure.getUsersAllowedToRead()).hasSameElementsAs(adventure.getUsersAllowedToRead());
        assertThat(retrievedAdventure.getUsersAllowedToWrite()).hasSameElementsAs(adventure.getUsersAllowedToWrite());
    }

    @Test
    public void retrieveAdventureByChannelId() {

        // Given
        String discordChannelId = "234234";
        AdventureEntity adventure = jpaRepository.save(AdventureEntityFixture.sample()
                .id(null)
                .discordChannelId(discordChannelId)
                .build());

        // When
        Optional<Adventure> retrievedAdventureOptional = repository.findByDiscordChannelId(discordChannelId);

        // Then
        assertThat(retrievedAdventureOptional).isNotNull().isNotEmpty();

        Adventure retrievedAdventure = retrievedAdventureOptional.get();
        assertThat(retrievedAdventure.getId()).isEqualTo(adventure.getId());
        assertThat(retrievedAdventure.getAdventureStart()).isEqualTo(adventure.getAdventureStart());
        assertThat(retrievedAdventure.getDescription()).isEqualTo(adventure.getDescription());
        assertThat(retrievedAdventure.getDiscordChannelId()).isEqualTo(adventure.getDiscordChannelId());
        assertThat(retrievedAdventure.getGameMode().name()).isEqualTo(adventure.getGameMode());
        assertThat(retrievedAdventure.getName()).isEqualTo(adventure.getName());
        assertThat(retrievedAdventure.getOwnerDiscordId()).isEqualTo(adventure.getOwnerDiscordId());
        assertThat(retrievedAdventure.getPersonaId()).isEqualTo(adventure.getPersonaId());
        assertThat(retrievedAdventure.getVisibility().name()).isEqualTo(adventure.getVisibility());
        assertThat(retrievedAdventure.getModeration().name()).isEqualTo(adventure.getModeration());
        assertThat(retrievedAdventure.getWorldId()).isEqualTo(adventure.getWorldId());

        assertThat(retrievedAdventure.getModelConfiguration().getAiModel().toString())
                .isEqualTo(adventure.getModelConfiguration().getAiModel());
        assertThat(retrievedAdventure.getModelConfiguration().getFrequencyPenalty())
                .isEqualTo(adventure.getModelConfiguration().getFrequencyPenalty());
        assertThat(retrievedAdventure.getModelConfiguration().getLogitBias())
                .isEqualTo(adventure.getModelConfiguration().getLogitBias());
        assertThat(retrievedAdventure.getModelConfiguration().getMaxTokenLimit())
                .isEqualTo(adventure.getModelConfiguration().getMaxTokenLimit());
        assertThat(retrievedAdventure.getModelConfiguration().getPresencePenalty())
                .isEqualTo(adventure.getModelConfiguration().getPresencePenalty());
        assertThat(retrievedAdventure.getModelConfiguration().getStopSequences())
                .isEqualTo(adventure.getModelConfiguration().getStopSequences());
        assertThat(retrievedAdventure.getModelConfiguration().getTemperature())
                .isEqualTo(adventure.getModelConfiguration().getTemperature());

        assertThat(retrievedAdventure.getUsersAllowedToRead()).hasSameElementsAs(adventure.getUsersAllowedToRead());
        assertThat(retrievedAdventure.getUsersAllowedToWrite()).hasSameElementsAs(adventure.getUsersAllowedToWrite());
    }

    @Test
    public void emptyResultWhenAssetDoesntExistGettingByChannelId() {

        // Given
        String adventureId = "WRLDID";

        // When
        Optional<Adventure> retrievedAdventureOptional = repository.findByDiscordChannelId(adventureId);

        // Then
        assertThat(retrievedAdventureOptional).isNotNull().isEmpty();
    }

    @Test
    public void returnAllAdventuresWhenSearchingWithoutParameters() {

        // Given
        String ownerDiscordId = "586678721356875";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .ownerDiscordId(ownerDiscordId)
                .discordChannelId("CHNLID1")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .ownerDiscordId("580485734")
                .usersAllowedToRead(Collections.singletonList(ownerDiscordId))
                .discordChannelId("CHNLID2")
                .build();

        AdventureEntity gpt354k = AdventureEntityFixture.sample()
                .id(null)
                .ownerDiscordId("580485734")
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void returnAllAdventuresWhenSearchingWithoutParametersAsc() {

        // Given
        String ownerDiscordId = "586678721356875";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        AdventureEntity gpt354k = AdventureEntityFixture.sample()
                .id(null)
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini()
                        .aiModel("gpt35-16k")
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(adventures.get(2).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void returnAllAdventuresWhenSearchingWithoutParametersDesc() {

        // Given
        String ownerDiscordId = "586678721356875";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        AdventureEntity gpt354k = AdventureEntityFixture.sample()
                .id(null)
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini()
                        .aiModel("gpt35-16k")
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchAdventures query = SearchAdventures.builder()
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt354k.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(adventures.get(2).getName()).isEqualTo(gpt4Omni.getName());
    }

    @Test
    public void searchAdventureOrderByNameAsc() {

        // Given
        String ownerDiscordId = "586678721356875";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        AdventureEntity gpt354k = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 3")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini()
                        .aiModel("gpt35-16k")
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventures query = SearchAdventures.builder()
                .sortingField("name")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(adventures.get(2).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void searchAdventureOrderByNameDesc() {

        // Given
        String ownerDiscordId = "586678721356875";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        AdventureEntity gpt354k = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 3")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini()
                        .aiModel("gpt35-16k")
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventures query = SearchAdventures.builder()
                .sortingField("name")
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt354k.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(adventures.get(2).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventureOrderByAiModelAsc() {

        // Given
        String ownerDiscordId = "586678721356875";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini));

        SearchAdventures query = SearchAdventures.builder()
                .sortingField("modelConfiguration.aiModel")
                .direction("ASC")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt4Omni.getName());
    }

    @Test
    public void searchAdventureOrderByAiModelDesc() {

        // Given
        String ownerDiscordId = "586678721356875";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini));

        SearchAdventures query = SearchAdventures.builder()
                .sortingField("modelConfiguration.aiModel")
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventureOrderByModerationAsc() {

        // Given
        String ownerDiscordId = "586678721356875";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .moderation("STRICT")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .moderation("PERMISSIVE")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        AdventureEntity gpt354k = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 3")
                .moderation("PERMISSIVE")
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventures query = SearchAdventures.builder()
                .sortingField("moderation")
                .direction("ASC")
                .page(1)
                .size(10)
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt354k.getName());
        assertThat(adventures.get(2).getName()).isEqualTo(gpt4Omni.getName());
    }

    @Test
    public void searchAdventureOrderByModerationDesc() {

        // Given
        String ownerDiscordId = "586678721356875";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini));

        SearchAdventures query = SearchAdventures.builder()
                .sortingField("modelConfiguration.aiModel")
                .direction("DESC")
                .page(1)
                .size(10)
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventureFilterByAiModel() {

        // Given
        String ownerDiscordId = "586678721356875";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini));

        SearchAdventures query = SearchAdventures.builder()
                .model("gpt4-mini")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventure_whenReadAccess_andFilterByVisibility_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .visibility("public")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .visibility("private")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini));

        SearchAdventures query = SearchAdventures.builder()
                .visibility("private")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventure_whenWriteAccess_andFilterByVisibility_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .visibility("public")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .visibility("private")
                .ownerDiscordId(ownerDiscordId)
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini));

        SearchAdventures query = SearchAdventures.builder()
                .visibility("private")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventureFilterByName() {

        // Given
        String ownerDiscordId = "586678721356875";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        AdventureEntity gpt354k = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 3")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini()
                        .aiModel("gpt35-16k")
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventures query = SearchAdventures.builder()
                .name("Number 2")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventureFilterByModeration() {

        // Given
        String ownerDiscordId = "586678721356875";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .moderation("STRICT")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .moderation("PERMISSIVE")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        AdventureEntity gpt354k = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 3")
                .moderation("PERMISSIVE")
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventures query = SearchAdventures.builder()
                .moderation("PERMISSIVE")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void searchAdventures_whenFilterByWorldId_andReaderOnly_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        String worldId = "WRLD";
        AdventureEntity gpt4Omni = jpaRepository.save(AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .worldId(worldId)
                .build());

        AdventureEntity gpt4Mini = jpaRepository.save(AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .worldId("AAAA")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .world(worldId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();
    }

    @Test
    public void searchAdventures_whenFilterByPersonaId_andReaderOnly_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        String personaId = "strict";
        AdventureEntity gpt4Omni = jpaRepository.save(AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .personaId(personaId)
                .build());

        AdventureEntity gpt4Mini = jpaRepository.save(AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .personaId("AAAA")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .persona(personaId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();
    }

    @Test
    public void returnAllAdventuresWhenSearchingWithoutParametersShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .ownerDiscordId(ownerDiscordId)
                .discordChannelId("CHNLID1")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .ownerDiscordId("580485734")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .discordChannelId("CHNLID2")
                .build();

        AdventureEntity gpt354k = AdventureEntityFixture.sample()
                .id(null)
                .ownerDiscordId("580485734")
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void returnAllAdventuresWhenSearchingWithoutParametersAscShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .ownerDiscordId(ownerDiscordId)
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        AdventureEntity gpt354k = AdventureEntityFixture.sample()
                .id(null)
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini()
                        .aiModel("gpt35-16k")
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void returnAllAdventuresWhenSearchingWithoutParametersDescShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .ownerDiscordId(ownerDiscordId)
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        AdventureEntity gpt354k = AdventureEntityFixture.sample()
                .id(null)
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini()
                        .aiModel("gpt35-16k")
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchAdventures query = SearchAdventures.builder()
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt4Omni.getName());
    }

    @Test
    public void searchAdventureOrderByNameAscShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .ownerDiscordId(ownerDiscordId)
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        AdventureEntity gpt354k = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 3")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini()
                        .aiModel("gpt35-16k")
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventures query = SearchAdventures.builder()
                .sortingField("name")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt4Omni.getName());
    }

    @Test
    public void searchAdventureOrderByNameDescShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .ownerDiscordId(ownerDiscordId)
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        AdventureEntity gpt354k = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 3")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini()
                        .aiModel("gpt35-16k")
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventures query = SearchAdventures.builder()
                .sortingField("name")
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventureOrderByAiModelAscShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini));

        SearchAdventures query = SearchAdventures.builder()
                .sortingField("modelConfiguration.aiModel")
                .direction("ASC")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventureOrderByAiModelDescShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini));

        SearchAdventures query = SearchAdventures.builder()
                .sortingField("modelConfiguration.aiModel")
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventureOrderByModerationAscShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .moderation("STRICT")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .moderation("PERMISSIVE")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        AdventureEntity gpt354k = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 3")
                .ownerDiscordId(ownerDiscordId)
                .moderation("PERMISSIVE")
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventures query = SearchAdventures.builder()
                .sortingField("moderation")
                .direction("ASC")
                .page(1)
                .size(10)
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void searchAdventureOrderByModerationDescShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini));

        SearchAdventures query = SearchAdventures.builder()
                .sortingField("modelConfiguration.aiModel")
                .direction("DESC")
                .page(1)
                .size(10)
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventureFilterByAiModelShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        AdventureEntity gpt354k = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 3")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini()
                        .aiModel("gpt35-16k")
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventures query = SearchAdventures.builder()
                .model("gpt35-16k")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void searchAdventureFilterByNameShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        AdventureEntity gpt354k = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 3")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini()
                        .aiModel("gpt35-16k")
                        .build())
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventures query = SearchAdventures.builder()
                .name("Number 2")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventureFilterByModerationShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .moderation("STRICT")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .ownerDiscordId(ownerDiscordId)
                .moderation("PERMISSIVE")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .build();

        AdventureEntity gpt354k = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 3")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .moderation("PERMISSIVE")
                .discordChannelId("CHNLID3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventures query = SearchAdventures.builder()
                .moderation("PERMISSIVE")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(adventures.get(1).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void searchAdventureFilterByGameMode() {

        // Given
        String ownerDiscordId = "586678721356875";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .moderation("STRICT")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .gameMode("CHAT")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .ownerDiscordId(ownerDiscordId)
                .moderation("PERMISSIVE")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .gameMode("RPG")
                .build();

        AdventureEntity gpt354k = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 3")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .moderation("PERMISSIVE")
                .discordChannelId("CHNLID3")
                .gameMode("AUTHOR")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventures query = SearchAdventures.builder()
                .gameMode("RPG")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventureFilterByGameModeShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        AdventureEntity gpt4Omni = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .moderation("STRICT")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .gameMode("AUTHOR")
                .build();

        AdventureEntity gpt4Mini = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .ownerDiscordId(ownerDiscordId)
                .moderation("PERMISSIVE")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .gameMode("CHAT")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .build();

        AdventureEntity gpt354k = AdventureEntityFixture.sample()
                .id(null)
                .name("Number 3")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .moderation("PERMISSIVE")
                .discordChannelId("CHNLID3")
                .gameMode("RPG")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventures query = SearchAdventures.builder()
                .gameMode("CHAT")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();

        List<GetAdventureResult> adventures = result.getResults();
        assertThat(adventures.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchAdventures_whenFilterByWorldId_andWriterOnly_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        String worldId = "WRLD";
        AdventureEntity gpt4Omni = jpaRepository.save(AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .worldId(worldId)
                .build());

        AdventureEntity gpt4Mini = jpaRepository.save(AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .worldId("AAAA")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .world(worldId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();
    }

    @Test
    public void searchAdventures_whenFilterByPersonaId_andWriterOnly_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        String personaId = "strict";
        AdventureEntity gpt4Omni = jpaRepository.save(AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .personaId(personaId)
                .build());

        AdventureEntity gpt4Mini = jpaRepository.save(AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .personaId("AAAA")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .persona(personaId)
                .operation("WRITE")
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();
    }

    @Test
    public void searchFavoriteAdventures_whenNoFilters_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";

        AdventureEntity gpt4Omni = jpaRepository.save(AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .visibility("public")
                .build());

        AdventureEntity gpt4Mini = jpaRepository.save(AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .visibility("public")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .favorites(true)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);
    }

    @Test
    public void searchFavoriteAdventures_whenFilterByName_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        String nameToSearch = "nameToBeSearched";
        AdventureEntity gpt4Omni = jpaRepository.save(AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .visibility("public")
                .build());

        AdventureEntity gpt4Mini = jpaRepository.save(AdventureEntityFixture.sample()
                .id(null)
                .name(nameToSearch)
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .visibility("public")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .name(nameToSearch)
                .favorites(true)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();
    }

    @Test
    public void searchFavoriteAdventures_whenFilterByVisibility_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        String visibility = "public";
        AdventureEntity gpt4Omni = jpaRepository.save(AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .visibility(visibility)
                .build());

        AdventureEntity gpt4Mini = jpaRepository.save(AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .visibility("private")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .visibility(visibility)
                .favorites(true)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();
    }

    @Test
    public void searchFavoriteAdventures_whenFilterByGameMode_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        String gameMode = "chat";
        AdventureEntity gpt4Omni = jpaRepository.save(AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .gameMode(gameMode)
                .build());

        AdventureEntity gpt4Mini = jpaRepository.save(AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .visibility("private")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .gameMode(gameMode)
                .favorites(true)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();
    }

    @Test
    public void searchFavoriteAdventures_whenFilterByAiModel_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        String model = "gpt4-omni";
        AdventureEntity gpt4Omni = jpaRepository.save(AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .build());

        AdventureEntity gpt4Mini = jpaRepository.save(AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .visibility("private")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .model(model)
                .favorites(true)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();
    }

    @Test
    public void searchFavoriteAdventures_whenFilterByModeration_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        String moderation = "strict";
        AdventureEntity gpt4Omni = jpaRepository.save(AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .moderation(moderation)
                .build());

        AdventureEntity gpt4Mini = jpaRepository.save(AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .moderation("disabled")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .moderation(moderation)
                .favorites(true)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();
    }

    @Test
    public void searchFavoriteAdventures_whenFilterByWorldId_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        String worldId = "WRLD";
        AdventureEntity gpt4Omni = jpaRepository.save(AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .worldId(worldId)
                .build());

        AdventureEntity gpt4Mini = jpaRepository.save(AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .worldId("AAAA")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .world(worldId)
                .favorites(true)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();
    }

    @Test
    public void searchFavoriteAdventures_whenFilterByPersonaId_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721356875";
        String personaId = "strict";
        AdventureEntity gpt4Omni = jpaRepository.save(AdventureEntityFixture.sample()
                .id(null)
                .name("Number 1")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Omni().build())
                .discordChannelId("CHNLID1")
                .personaId(personaId)
                .build());

        AdventureEntity gpt4Mini = jpaRepository.save(AdventureEntityFixture.sample()
                .id(null)
                .name("Number 2")
                .modelConfiguration(ModelConfigurationEntityFixture.gpt4Mini().build())
                .discordChannelId("CHNLID2")
                .personaId("AAAA")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Omni.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(ownerDiscordId)
                .assetType("adventure")
                .assetId(gpt4Mini.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2));

        SearchAdventures query = SearchAdventures.builder()
                .requesterDiscordId(ownerDiscordId)
                .persona(personaId)
                .favorites(true)
                .build();

        // When
        SearchAdventuresResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result.getTotalItems()).isOne();
        assertThat(result.getTotalPages()).isOne();
    }

    @Test
    public void adventure_whenChannelIdIsProvided_thenReturnGameMode() {

        // Given
        String discordChannelId = "1234";
        AdventureEntity adventure = jpaRepository.save(AdventureEntityFixture.sample()
                .discordChannelId(discordChannelId)
                .build());

        // When
        String gameMode = repository.getGameModeByDiscordChannelId(discordChannelId);

        // Then
        assertThat(gameMode).isNotNull()
                .isNotEmpty()
                .isEqualTo(adventure.getGameMode());
    }
}
