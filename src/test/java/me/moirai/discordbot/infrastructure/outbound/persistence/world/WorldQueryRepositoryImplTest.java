package me.moirai.discordbot.infrastructure.outbound.persistence.world;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import me.moirai.discordbot.AbstractIntegrationTest;
import me.moirai.discordbot.core.application.usecase.world.request.SearchWorldsWithReadAccess;
import me.moirai.discordbot.core.application.usecase.world.request.SearchWorldsWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.world.result.GetWorldResult;
import me.moirai.discordbot.core.application.usecase.world.result.SearchWorldsResult;

public class WorldQueryRepositoryImplTest extends AbstractIntegrationTest {

    @Autowired
    private WorldQueryRepositoryImpl repository;

    @Autowired
    private WorldJpaRepository jpaRepository;

    @BeforeEach
    public void before() {
        jpaRepository.deleteAllInBatch();
    }

    @Test
    public void returnAllWorldsWhenSearchingWithoutParameters() {

        // Given
        String ownerDiscordId = "586678721356875";

        WorldEntity gpt4Omni = WorldEntityFixture.privateWorld()
                .id(null)
                .ownerDiscordId(ownerDiscordId)
                .build();

        WorldEntity gpt4Mini = WorldEntityFixture.privateWorld()
                .id(null)
                .ownerDiscordId("580485734")
                .usersAllowedToRead(Collections.singletonList(ownerDiscordId))
                .build();

        WorldEntity gpt354k = WorldEntityFixture.privateWorld()
                .id(null)
                .ownerDiscordId("580485734")
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchWorldsWithReadAccess query = SearchWorldsWithReadAccess.builder()
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchWorldsResult result = repository.searchWorldsWithReadAccess(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(worlds.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void returnOnlyWorldsWithReadAccessWhenSearchingWithoutParametersAsc() {

        // Given
        String ownerDiscordId = "586678721356875";

        WorldEntity gpt4Omni = WorldEntityFixture.privateWorld()
                .id(null)
                .build();

        WorldEntity gpt4Mini = WorldEntityFixture.privateWorld()
                .id(null)
                .build();

        WorldEntity gpt354k = WorldEntityFixture.privateWorld()
                .id(null)
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchWorldsWithReadAccess query = SearchWorldsWithReadAccess.builder()
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchWorldsResult result = repository.searchWorldsWithReadAccess(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(worlds.get(1).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(worlds.get(2).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void returnAllWorldsWhenSearchingWithoutParametersDesc() {

        // Given
        String ownerDiscordId = "586678721356875";

        WorldEntity gpt4Omni = WorldEntityFixture.privateWorld()
                .id(null)
                .build();

        WorldEntity gpt4Mini = WorldEntityFixture.privateWorld()
                .id(null)
                .build();

        WorldEntity gpt354k = WorldEntityFixture.privateWorld()
                .id(null)
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchWorldsWithReadAccess query = SearchWorldsWithReadAccess.builder()
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchWorldsResult result = repository.searchWorldsWithReadAccess(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt354k.getName());
        assertThat(worlds.get(1).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(worlds.get(2).getName()).isEqualTo(gpt4Omni.getName());
    }

    @Test
    public void searchWorldOrderByNameAsc() {

        // Given
        String ownerDiscordId = "586678721356875";

        WorldEntity gpt4Omni = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 2")
                .build();

        WorldEntity gpt4Mini = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 1")
                .build();

        WorldEntity gpt354k = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4Omni, gpt4Mini, gpt354k));

        SearchWorldsWithReadAccess query = SearchWorldsWithReadAccess.builder()
                .sortByField("name")
                .page(1)
                .items(10)
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchWorldsResult result = repository.searchWorldsWithReadAccess(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(worlds.get(1).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(worlds.get(2).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void searchWorldOrderByNameDesc() {

        // Given
        String ownerDiscordId = "586678721356875";

        WorldEntity gpt4Omni = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 2")
                .build();

        WorldEntity gpt4Mini = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 1")
                .build();

        WorldEntity gpt354k = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4Omni, gpt4Mini, gpt354k));

        SearchWorldsWithReadAccess query = SearchWorldsWithReadAccess.builder()
                .sortByField("name")
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchWorldsResult result = repository.searchWorldsWithReadAccess(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt354k.getName());
        assertThat(worlds.get(1).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(worlds.get(2).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchWorldFilterByName() {

        // Given
        String ownerDiscordId = "586678721356875";

        WorldEntity gpt4Omni = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 1")
                .build();

        WorldEntity gpt4Mini = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 2")
                .build();

        WorldEntity gpt354k = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4Omni, gpt4Mini, gpt354k));

        SearchWorldsWithReadAccess query = SearchWorldsWithReadAccess.builder()
                .name("Number 2")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchWorldsResult result = repository.searchWorldsWithReadAccess(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void returnAllWorldsWhenSearchingWithoutParametersShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        WorldEntity gpt4Omni = WorldEntityFixture.privateWorld()
                .id(null)
                .ownerDiscordId(ownerDiscordId)
                .build();

        WorldEntity gpt4Mini = WorldEntityFixture.privateWorld()
                .id(null)
                .ownerDiscordId("580485734")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .build();

        WorldEntity gpt354k = WorldEntityFixture.privateWorld()
                .id(null)
                .ownerDiscordId("580485734")
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchWorldsWithWriteAccess query = SearchWorldsWithWriteAccess.builder()
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchWorldsResult result = repository.searchWorldsWithWriteAccess(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(worlds.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void returnOnlyWorldsWithWriteAccessWhenSearchingWithoutParametersAsc() {

        // Given
        String ownerDiscordId = "586678721358363";

        WorldEntity gpt4Omni = WorldEntityFixture.privateWorld()
                .id(null)
                .ownerDiscordId(ownerDiscordId)
                .build();

        WorldEntity gpt4Mini = WorldEntityFixture.privateWorld()
                .id(null)
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .build();

        WorldEntity gpt354k = WorldEntityFixture.privateWorld()
                .id(null)
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchWorldsWithWriteAccess query = SearchWorldsWithWriteAccess.builder()
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchWorldsResult result = repository.searchWorldsWithWriteAccess(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(worlds.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void returnAllWorldsWhenSearchingWithoutParametersDescShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        WorldEntity gpt4Omni = WorldEntityFixture.privateWorld()
                .id(null)
                .ownerDiscordId(ownerDiscordId)
                .build();

        WorldEntity gpt4Mini = WorldEntityFixture.privateWorld()
                .id(null)
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .build();

        WorldEntity gpt354k = WorldEntityFixture.privateWorld()
                .id(null)
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchWorldsWithWriteAccess query = SearchWorldsWithWriteAccess.builder()
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchWorldsResult result = repository.searchWorldsWithWriteAccess(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt354k.getName());
        assertThat(worlds.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchWorldOrderByNameAscShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        WorldEntity gpt4Omni = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 2")
                .ownerDiscordId(ownerDiscordId)
                .build();

        WorldEntity gpt4Mini = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 1")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .build();

        WorldEntity gpt354k = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4Omni, gpt4Mini, gpt354k));

        SearchWorldsWithWriteAccess query = SearchWorldsWithWriteAccess.builder()
                .sortByField("name")
                .page(1)
                .items(10)
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchWorldsResult result = repository.searchWorldsWithWriteAccess(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(worlds.get(1).getName()).isEqualTo(gpt4Omni.getName());
    }

    @Test
    public void searchWorldOrderByNameDescShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        WorldEntity gpt4Omni = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 2")
                .ownerDiscordId(ownerDiscordId)
                .build();

        WorldEntity gpt4Mini = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 1")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .build();

        WorldEntity gpt354k = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4Omni, gpt4Mini, gpt354k));

        SearchWorldsWithWriteAccess query = SearchWorldsWithWriteAccess.builder()
                .sortByField("name")
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchWorldsResult result = repository.searchWorldsWithWriteAccess(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(worlds.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchWorldFilterByNameShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        WorldEntity gpt4Omni = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 1")
                .build();

        WorldEntity gpt4Mini = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 2")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .build();

        WorldEntity gpt354k = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4Omni, gpt4Mini, gpt354k));

        SearchWorldsWithWriteAccess query = SearchWorldsWithWriteAccess.builder()
                .name("Number 2")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchWorldsResult result = repository.searchWorldsWithWriteAccess(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void emptyResultWhenSearchingForWorldWithWriteAccessIfUserHasNoAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        WorldEntity gpt4Omni = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 1")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .build();

        WorldEntity gpt4Mini = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 2")
                .build();

        WorldEntity gpt354k = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4Omni, gpt4Mini, gpt354k));

        SearchWorldsWithWriteAccess query = SearchWorldsWithWriteAccess.builder()
                .name("Number 2")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchWorldsResult result = repository.searchWorldsWithWriteAccess(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isEmpty();
    }
}
