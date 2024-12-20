package me.moirai.discordbot.infrastructure.outbound.persistence.world;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import me.moirai.discordbot.AbstractIntegrationTest;
import me.moirai.discordbot.core.application.usecase.world.request.SearchWorldLorebookEntries;
import me.moirai.discordbot.core.application.usecase.world.result.GetWorldLorebookEntryResult;
import me.moirai.discordbot.core.application.usecase.world.result.SearchWorldLorebookEntriesResult;
import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.core.domain.world.WorldFixture;
import me.moirai.discordbot.core.domain.world.WorldLorebookEntry;
import me.moirai.discordbot.core.domain.world.WorldLorebookEntryFixture;
import me.moirai.discordbot.core.domain.world.WorldLorebookEntryRepository;
import me.moirai.discordbot.core.domain.world.WorldDomainRepository;

public class WorldLorebookEntryRepositoryImplIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private WorldLorebookEntryRepository repository;

    @Autowired
    private WorldLorebookEntryJpaRepository jpaRepository;

    @Autowired
    private WorldDomainRepository worldRepository;

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
        WorldLorebookEntryEntity gpt4Omni = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .build();

        WorldLorebookEntryEntity gpt4Mini = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .build();

        WorldLorebookEntryEntity gpt354k = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchWorldLorebookEntries query = SearchWorldLorebookEntries.builder()
                .worldId("WRLDID")
                .build();

        // When
        SearchWorldLorebookEntriesResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetWorldLorebookEntryResult> entries = result.getResults();
        assertThat(entries.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(entries.get(1).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(entries.get(2).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void returnOnlyWorldLorebookEntriesWithReadAccessWhenSearchingWithoutParametersAsc() {

        // Given

        WorldLorebookEntryEntity gpt4Omni = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .build();

        WorldLorebookEntryEntity gpt4Mini = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .build();

        WorldLorebookEntryEntity gpt354k = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchWorldLorebookEntries query = SearchWorldLorebookEntries.builder()
                .worldId("WRLDID")
                .build();

        // When
        SearchWorldLorebookEntriesResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetWorldLorebookEntryResult> entries = result.getResults();
        assertThat(entries.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(entries.get(1).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(entries.get(2).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void returnAllWorldLorebookEntriesWhenSearchingWithoutParametersDesc() {

        // Given

        WorldLorebookEntryEntity gpt4Omni = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .build();

        WorldLorebookEntryEntity gpt4Mini = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .build();

        WorldLorebookEntryEntity gpt354k = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchWorldLorebookEntries query = SearchWorldLorebookEntries.builder()
                .worldId("WRLDID")
                .direction("DESC")
                .build();

        // When
        SearchWorldLorebookEntriesResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetWorldLorebookEntryResult> entries = result.getResults();
        assertThat(entries.get(0).getName()).isEqualTo(gpt354k.getName());
        assertThat(entries.get(1).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(entries.get(2).getName()).isEqualTo(gpt4Omni.getName());
    }

    @Test
    public void searchWorldLorebookEntryOrderByNameAsc() {

        // Given

        WorldLorebookEntryEntity gpt4Omni = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Number 2")
                .build();

        WorldLorebookEntryEntity gpt4Mini = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Number 1")
                .build();

        WorldLorebookEntryEntity gpt354k = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4Omni, gpt4Mini, gpt354k));

        SearchWorldLorebookEntries query = SearchWorldLorebookEntries.builder()
                .worldId("WRLDID")
                .sortingField("name")
                .page(1)
                .size(10)
                .build();

        // When
        SearchWorldLorebookEntriesResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetWorldLorebookEntryResult> entries = result.getResults();
        assertThat(entries.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(entries.get(1).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(entries.get(2).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void searchWorldLorebookEntryOrderByNameDesc() {

        // Given

        WorldLorebookEntryEntity gpt4Omni = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Number 2")
                .build();

        WorldLorebookEntryEntity gpt4Mini = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Number 1")
                .build();

        WorldLorebookEntryEntity gpt354k = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4Omni, gpt4Mini, gpt354k));

        SearchWorldLorebookEntries query = SearchWorldLorebookEntries.builder()
                .worldId("WRLDID")
                .sortingField("name")
                .direction("DESC")
                .build();

        // When
        SearchWorldLorebookEntriesResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetWorldLorebookEntryResult> entries = result.getResults();
        assertThat(entries.get(0).getName()).isEqualTo(gpt354k.getName());
        assertThat(entries.get(1).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(entries.get(2).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchWorldLorebookEntryFilterByName() {

        // Given
        WorldLorebookEntryEntity gpt4Omni = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Number 1")
                .build();

        WorldLorebookEntryEntity gpt4Mini = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Number 2")
                .build();

        WorldLorebookEntryEntity gpt354k = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4Omni, gpt4Mini, gpt354k));

        SearchWorldLorebookEntries query = SearchWorldLorebookEntries.builder()
                .worldId("WRLDID")
                .name("Number 2")
                .build();

        // When
        SearchWorldLorebookEntriesResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);

        List<GetWorldLorebookEntryResult> entries = result.getResults();
        assertThat(entries.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void findAllEntriesByRegex_whenValidRegex_thenReturnMatchingEntries() {

        // Then
        World world = worldRepository.save(WorldFixture.privateWorld().build());
        WorldLorebookEntryEntity gpt4Omni = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("John")
                .regex("[Jj]ohn")
                .worldId(world.getId())
                .build();

        WorldLorebookEntryEntity gpt4Mini = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Immune")
                .regex("[Ii]mmun(e|ity)")
                .worldId(world.getId())
                .build();

        WorldLorebookEntryEntity gpt354k = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Archmage")
                .regex("[Aa]rch(|-|\s)[Mm]age")
                .worldId(world.getId())
                .build();

        jpaRepository.saveAll(Lists.list(gpt4Omni, gpt4Mini, gpt354k));

        String archmageLowerCase = "archmage";
        String archmageDash = "Arch-mage";
        String archmageSpace = "Arch Mage";
        String immunityLowerCase = "immunity";
        String triggerAll = "John, the Arch-Mage, is immune to disease";

        // When
        List<WorldLorebookEntry> archmageLowerCaseResult = repository.findAllByRegex(archmageLowerCase, world.getId());
        List<WorldLorebookEntry> archmageDashResult = repository.findAllByRegex(archmageDash, world.getId());
        List<WorldLorebookEntry> archmageSpaceResult = repository.findAllByRegex(archmageSpace, world.getId());
        List<WorldLorebookEntry> immunityLowerCaseResult = repository.findAllByRegex(immunityLowerCase, world.getId());
        List<WorldLorebookEntry> allEntries = repository.findAllByRegex(triggerAll, world.getId());

        // Then
        assertThat(archmageLowerCaseResult).isNotNull().isNotEmpty().hasSize(1);
        assertThat(archmageDashResult).isNotNull().isNotEmpty().hasSize(1);
        assertThat(archmageSpaceResult).isNotNull().isNotEmpty().hasSize(1);
        assertThat(immunityLowerCaseResult).isNotNull().isNotEmpty().hasSize(1);
        assertThat(allEntries).isNotNull().isNotEmpty().hasSize(3);
    }

    @Test
    public void updateWorld() {

        // Given
        WorldLorebookEntry originalEntry = repository.save(WorldLorebookEntryFixture.sampleLorebookEntry()
                .id(null)
                .build());

        WorldLorebookEntry entryToBeUpdated = WorldLorebookEntryFixture.sampleLorebookEntry()
                .id(originalEntry.getId())
                .name("new name")
                .version(originalEntry.getVersion())
                .build();

        // When
        WorldLorebookEntry updatedWorldLorebookEntry = repository.save(entryToBeUpdated);

        // Then
        assertThat(originalEntry.getVersion()).isZero();
        assertThat(updatedWorldLorebookEntry.getVersion()).isOne();
    }
}
