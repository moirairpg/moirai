package es.thalesalv.chatrpg.infrastructure.outbound.persistence.world;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.thalesalv.chatrpg.AbstractIntegrationTest;
import es.thalesalv.chatrpg.core.application.query.world.GetWorldResult;
import es.thalesalv.chatrpg.core.application.query.world.SearchWorlds;
import es.thalesalv.chatrpg.core.application.query.world.SearchWorldsResult;
import es.thalesalv.chatrpg.core.domain.world.World;
import es.thalesalv.chatrpg.core.domain.world.WorldFixture;
import es.thalesalv.chatrpg.core.domain.world.WorldRepository;

public class WorldRepositoryImplIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private WorldRepository repository;

    @Autowired
    private WorldJpaRepository jpaRepository;

    @BeforeEach
    public void before() {
        jpaRepository.deleteAllInBatch();
    }

    @Test
    public void createWorld() {

        // Given
        World world = WorldFixture.privateWorld()
                .id(null)
                .build();

        // When
        World createdWorld = repository.save(world);

        // Then
        assertThat(createdWorld).isNotNull();

        assertThat(createdWorld.getCreationDate()).isNotNull();
        assertThat(createdWorld.getLastUpdateDate()).isNotNull();

        assertThat(createdWorld.getName()).isEqualTo(world.getName());
        assertThat(createdWorld.getVisibility()).isEqualTo(world.getVisibility());
        assertThat(createdWorld.getWriterUsers()).hasSameElementsAs(world.getWriterUsers());
        assertThat(createdWorld.getReaderUsers()).hasSameElementsAs(world.getReaderUsers());
    }

    @Test
    public void retrieveWorldById() {

        // Given
        String ownerDiscordId = "586678721356875";
        World world = repository.save(WorldFixture.privateWorld()
                .id(null)
                .build());

        // When
        Optional<World> retrievedWorldOptional = repository.findById(world.getId(), ownerDiscordId);

        // Then
        assertThat(retrievedWorldOptional).isNotNull().isNotEmpty();

        World retrievedWorld = retrievedWorldOptional.get();
        assertThat(retrievedWorld.getId()).isEqualTo(world.getId());
    }

    @Test
    public void deleteWorld() {

        // Given
        String ownerDiscordId = "586678721356875";
        World world = repository.save(WorldFixture.privateWorld()
                .id(null)
                .build());

        // When
        repository.deleteById(world.getId());

        // Then
        assertThat(repository.findById(world.getId(), ownerDiscordId)).isNotNull().isEmpty();
    }

    @Test
    public void returnAllWorldsWhenSearchingWithoutParameters() {

        // Given
        String ownerDiscordId = "586678721356875";

        WorldEntity gpt4128k = WorldEntityFixture.privateWorld()
                .id(null)
                .ownerDiscordId(ownerDiscordId)
                .build();

        WorldEntity gpt3516k = WorldEntityFixture.privateWorld()
                .id(null)
                .ownerDiscordId("580485734")
                .usersAllowedToRead(Collections.singletonList(ownerDiscordId))
                .build();

        WorldEntity gpt354k = WorldEntityFixture.privateWorld()
                .id(null)
                .ownerDiscordId("580485734")
                .build();

        jpaRepository.save(gpt4128k);
        jpaRepository.save(gpt3516k);
        jpaRepository.save(gpt354k);

        SearchWorlds query = SearchWorlds.builder().build();

        // When
        SearchWorldsResult result = repository.searchWorlds(query, ownerDiscordId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt4128k.getName());
        assertThat(worlds.get(1).getName()).isEqualTo(gpt3516k.getName());
    }

    @Test
    public void returnOnlyWorldsWithReadAccessWhenSearchingWithoutParametersAsc() {

        // Given
        String ownerDiscordId = "586678721356875";

        WorldEntity gpt4128k = WorldEntityFixture.privateWorld()
                .id(null)
                .build();

        WorldEntity gpt3516k = WorldEntityFixture.privateWorld()
                .id(null)
                .build();

        WorldEntity gpt354k = WorldEntityFixture.privateWorld()
                .id(null)
                .build();

        jpaRepository.save(gpt4128k);
        jpaRepository.save(gpt3516k);
        jpaRepository.save(gpt354k);

        SearchWorlds query = SearchWorlds.builder().build();

        // When
        SearchWorldsResult result = repository.searchWorlds(query, ownerDiscordId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt4128k.getName());
        assertThat(worlds.get(1).getName()).isEqualTo(gpt3516k.getName());
        assertThat(worlds.get(2).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void returnAllWorldsWhenSearchingWithoutParametersDesc() {

        // Given
        String ownerDiscordId = "586678721356875";

        WorldEntity gpt4128k = WorldEntityFixture.privateWorld()
                .id(null)
                .build();

        WorldEntity gpt3516k = WorldEntityFixture.privateWorld()
                .id(null)
                .build();

        WorldEntity gpt354k = WorldEntityFixture.privateWorld()
                .id(null)
                .build();

        jpaRepository.save(gpt4128k);
        jpaRepository.save(gpt3516k);
        jpaRepository.save(gpt354k);

        SearchWorlds query = SearchWorlds.builder()
                .direction("DESC")
                .build();

        // When
        SearchWorldsResult result = repository.searchWorlds(query, ownerDiscordId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt354k.getName());
        assertThat(worlds.get(1).getName()).isEqualTo(gpt3516k.getName());
        assertThat(worlds.get(2).getName()).isEqualTo(gpt4128k.getName());
    }

    @Test
    public void searchWorldOrderByNameAsc() {

        // Given
        String ownerDiscordId = "586678721356875";

        WorldEntity gpt4128k = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 2")
                .build();

        WorldEntity gpt3516k = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 1")
                .build();

        WorldEntity gpt354k = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4128k, gpt3516k, gpt354k));

        SearchWorlds query = SearchWorlds.builder()
                .sortByField("name")
                .page(1)
                .items(10)
                .build();

        // When
        SearchWorldsResult result = repository.searchWorlds(query, ownerDiscordId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt3516k.getName());
        assertThat(worlds.get(1).getName()).isEqualTo(gpt4128k.getName());
        assertThat(worlds.get(2).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void searchWorldOrderByNameDesc() {

        // Given
        String ownerDiscordId = "586678721356875";

        WorldEntity gpt4128k = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 2")
                .build();

        WorldEntity gpt3516k = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 1")
                .build();

        WorldEntity gpt354k = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4128k, gpt3516k, gpt354k));

        SearchWorlds query = SearchWorlds.builder()
                .sortByField("name")
                .direction("DESC")
                .build();

        // When
        SearchWorldsResult result = repository.searchWorlds(query, ownerDiscordId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt354k.getName());
        assertThat(worlds.get(1).getName()).isEqualTo(gpt4128k.getName());
        assertThat(worlds.get(2).getName()).isEqualTo(gpt3516k.getName());
    }

    @Test
    public void searchWorldFilterByName() {

        // Given
        String ownerDiscordId = "586678721356875";

        WorldEntity gpt4128k = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 1")
                .build();

        WorldEntity gpt3516k = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 2")
                .build();

        WorldEntity gpt354k = WorldEntityFixture.privateWorld()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4128k, gpt3516k, gpt354k));

        SearchWorlds query = SearchWorlds.builder()
                .name("Number 2")
                .build();

        // When
        SearchWorldsResult result = repository.searchWorlds(query, ownerDiscordId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);

        List<GetWorldResult> worlds = result.getResults();
        assertThat(worlds.get(0).getName()).isEqualTo(gpt3516k.getName());
    }
}