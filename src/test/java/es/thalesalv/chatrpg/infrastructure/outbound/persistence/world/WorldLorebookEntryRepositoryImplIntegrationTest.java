package es.thalesalv.chatrpg.infrastructure.outbound.persistence.world;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.thalesalv.chatrpg.AbstractIntegrationTest;
import es.thalesalv.chatrpg.core.application.query.world.GetWorldLorebookEntryResult;
import es.thalesalv.chatrpg.core.application.query.world.SearchWorldLorebookEntries;
import es.thalesalv.chatrpg.core.application.query.world.SearchWorldLorebookEntriesResult;
import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntry;
import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntryFixture;
import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntryRepository;

// TODO add tests based on world permission
public class WorldLorebookEntryRepositoryImplIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private WorldLorebookEntryRepository repository;

    @Autowired
    private WorldLorebookEntryJpaRepository jpaRepository;

    @BeforeEach
    public void before() {
        jpaRepository.deleteAllInBatch();
    }

    @Test
    public void createWorldLorebookEntry() {

        // Given
        WorldLorebookEntry entry = WorldLorebookEntryFixture.sampleLorebookEntry()
                .id(null)
                .build();

        // When
        WorldLorebookEntry createdWorldLorebookEntry = repository.save(entry);

        // Then
        assertThat(createdWorldLorebookEntry).isNotNull();

        assertThat(createdWorldLorebookEntry.getCreationDate()).isNotNull();
        assertThat(createdWorldLorebookEntry.getLastUpdateDate()).isNotNull();

        assertThat(createdWorldLorebookEntry.getName()).isEqualTo(entry.getName());
    }

    @Test
    public void retrieveWorldLorebookEntryById() {

        // Given
        WorldLorebookEntry entry = repository.save(WorldLorebookEntryFixture.sampleLorebookEntry()
                .id(null)
                .build());

        // When
        Optional<WorldLorebookEntry> retrievedWorldLorebookEntryOptional = repository.findById(entry.getId());

        // Then
        assertThat(retrievedWorldLorebookEntryOptional).isNotNull().isNotEmpty();

        WorldLorebookEntry retrievedWorldLorebookEntry = retrievedWorldLorebookEntryOptional.get();
        assertThat(retrievedWorldLorebookEntry.getId()).isEqualTo(entry.getId());
    }

    @Test
    public void emptyResultWhenAssetDoesntExist() {

        // Given
        String entryId = "WRLDID";

        // When
        Optional<WorldLorebookEntry> retrievedWorldLorebookEntryOptional = repository.findById(entryId);

        // Then
        assertThat(retrievedWorldLorebookEntryOptional).isNotNull().isEmpty();
    }

    @Test
    public void deleteWorldLorebookEntry() {

        // Given
        WorldLorebookEntry entry = repository.save(WorldLorebookEntryFixture.sampleLorebookEntry()
                .id(null)
                .build());

        // When
        repository.deleteById(entry.getId());

        // Then
        assertThat(repository.findById(entry.getId())).isNotNull().isEmpty();
    }

    @Test
    public void returnAllWorldLorebookEntriesWhenSearchingWithoutParameters() {

        // Given
        WorldLorebookEntryEntity gpt4128k = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .build();

        WorldLorebookEntryEntity gpt3516k = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .build();

        WorldLorebookEntryEntity gpt354k = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .build();

        jpaRepository.save(gpt4128k);
        jpaRepository.save(gpt3516k);
        jpaRepository.save(gpt354k);

        SearchWorldLorebookEntries query = SearchWorldLorebookEntries.builder()
                .worldId("WRLDID")
                .build();

        // When
        SearchWorldLorebookEntriesResult result = repository.searchWorldLorebookEntriesByWorldId(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetWorldLorebookEntryResult> entries = result.getResults();
        assertThat(entries.get(0).getName()).isEqualTo(gpt4128k.getName());
        assertThat(entries.get(1).getName()).isEqualTo(gpt3516k.getName());
        assertThat(entries.get(2).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void returnOnlyWorldLorebookEntriesWithReadAccessWhenSearchingWithoutParametersAsc() {

        // Given

        WorldLorebookEntryEntity gpt4128k = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .build();

        WorldLorebookEntryEntity gpt3516k = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .build();

        WorldLorebookEntryEntity gpt354k = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .build();

        jpaRepository.save(gpt4128k);
        jpaRepository.save(gpt3516k);
        jpaRepository.save(gpt354k);

        SearchWorldLorebookEntries query = SearchWorldLorebookEntries.builder()
                .worldId("WRLDID")
                .build();

        // When
        SearchWorldLorebookEntriesResult result = repository.searchWorldLorebookEntriesByWorldId(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetWorldLorebookEntryResult> entries = result.getResults();
        assertThat(entries.get(0).getName()).isEqualTo(gpt4128k.getName());
        assertThat(entries.get(1).getName()).isEqualTo(gpt3516k.getName());
        assertThat(entries.get(2).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void returnAllWorldLorebookEntriesWhenSearchingWithoutParametersDesc() {

        // Given

        WorldLorebookEntryEntity gpt4128k = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .build();

        WorldLorebookEntryEntity gpt3516k = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .build();

        WorldLorebookEntryEntity gpt354k = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .build();

        jpaRepository.save(gpt4128k);
        jpaRepository.save(gpt3516k);
        jpaRepository.save(gpt354k);

        SearchWorldLorebookEntries query = SearchWorldLorebookEntries.builder()
                .worldId("WRLDID")
                .direction("DESC")
                .build();

        // When
        SearchWorldLorebookEntriesResult result = repository.searchWorldLorebookEntriesByWorldId(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetWorldLorebookEntryResult> entries = result.getResults();
        assertThat(entries.get(0).getName()).isEqualTo(gpt354k.getName());
        assertThat(entries.get(1).getName()).isEqualTo(gpt3516k.getName());
        assertThat(entries.get(2).getName()).isEqualTo(gpt4128k.getName());
    }

    @Test
    public void searchWorldLorebookEntryOrderByNameAsc() {

        // Given

        WorldLorebookEntryEntity gpt4128k = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Number 2")
                .build();

        WorldLorebookEntryEntity gpt3516k = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Number 1")
                .build();

        WorldLorebookEntryEntity gpt354k = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4128k, gpt3516k, gpt354k));

        SearchWorldLorebookEntries query = SearchWorldLorebookEntries.builder()
                .worldId("WRLDID")
                .sortByField("name")
                .page(1)
                .items(10)
                .build();

        // When
        SearchWorldLorebookEntriesResult result = repository.searchWorldLorebookEntriesByWorldId(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetWorldLorebookEntryResult> entries = result.getResults();
        assertThat(entries.get(0).getName()).isEqualTo(gpt3516k.getName());
        assertThat(entries.get(1).getName()).isEqualTo(gpt4128k.getName());
        assertThat(entries.get(2).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void searchWorldLorebookEntryOrderByNameDesc() {

        // Given

        WorldLorebookEntryEntity gpt4128k = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Number 2")
                .build();

        WorldLorebookEntryEntity gpt3516k = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Number 1")
                .build();

        WorldLorebookEntryEntity gpt354k = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4128k, gpt3516k, gpt354k));

        SearchWorldLorebookEntries query = SearchWorldLorebookEntries.builder()
                .worldId("WRLDID")
                .sortByField("name")
                .direction("DESC")
                .build();

        // When
        SearchWorldLorebookEntriesResult result = repository.searchWorldLorebookEntriesByWorldId(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetWorldLorebookEntryResult> entries = result.getResults();
        assertThat(entries.get(0).getName()).isEqualTo(gpt354k.getName());
        assertThat(entries.get(1).getName()).isEqualTo(gpt4128k.getName());
        assertThat(entries.get(2).getName()).isEqualTo(gpt3516k.getName());
    }

    @Test
    public void searchWorldLorebookEntryFilterByName() {

        // Given

        WorldLorebookEntryEntity gpt4128k = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Number 1")
                .build();

        WorldLorebookEntryEntity gpt3516k = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Number 2")
                .build();

        WorldLorebookEntryEntity gpt354k = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4128k, gpt3516k, gpt354k));

        SearchWorldLorebookEntries query = SearchWorldLorebookEntries.builder()
                .worldId("WRLDID")
                .name("Number 2")
                .build();

        // When
        SearchWorldLorebookEntriesResult result = repository.searchWorldLorebookEntriesByWorldId(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);

        List<GetWorldLorebookEntryResult> entries = result.getResults();
        assertThat(entries.get(0).getName()).isEqualTo(gpt3516k.getName());
    }
}
