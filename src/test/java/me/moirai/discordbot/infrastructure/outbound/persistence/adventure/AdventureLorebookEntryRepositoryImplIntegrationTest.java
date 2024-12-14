package me.moirai.discordbot.infrastructure.outbound.persistence.adventure;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import me.moirai.discordbot.AbstractIntegrationTest;
import me.moirai.discordbot.core.application.usecase.adventure.request.SearchAdventureLorebookEntries;
import me.moirai.discordbot.core.application.usecase.adventure.result.GetAdventureLorebookEntryResult;
import me.moirai.discordbot.core.application.usecase.adventure.result.SearchAdventureLorebookEntriesResult;
import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.adventure.AdventureFixture;
import me.moirai.discordbot.core.domain.adventure.AdventureLorebookEntry;
import me.moirai.discordbot.core.domain.adventure.AdventureLorebookEntryFixture;
import me.moirai.discordbot.core.domain.adventure.AdventureLorebookEntryRepository;
import me.moirai.discordbot.core.domain.adventure.AdventureDomainRepository;

public class AdventureLorebookEntryRepositoryImplIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AdventureLorebookEntryRepository repository;

    @Autowired
    private AdventureLorebookEntryJpaRepository jpaRepository;

    @Autowired
    private AdventureDomainRepository adventureRepository;

    @BeforeEach
    public void before() {
        jpaRepository.deleteAllInBatch();
    }

    @Test
    public void createAdventureLorebookEntry() {

        // Given
        AdventureLorebookEntry entry = AdventureLorebookEntryFixture.sampleLorebookEntry()
                .id(null)
                .build();

        // When
        AdventureLorebookEntry createdAdventureLorebookEntry = repository.save(entry);

        // Then
        assertThat(createdAdventureLorebookEntry).isNotNull();

        assertThat(createdAdventureLorebookEntry.getCreationDate()).isNotNull();
        assertThat(createdAdventureLorebookEntry.getLastUpdateDate()).isNotNull();

        assertThat(createdAdventureLorebookEntry.getName()).isEqualTo(entry.getName());
    }

    @Test
    public void retrieveAdventureLorebookEntryById() {

        // Given
        AdventureLorebookEntry entry = repository.save(AdventureLorebookEntryFixture.sampleLorebookEntry()
                .id(null)
                .build());

        // When
        Optional<AdventureLorebookEntry> retrievedAdventureLorebookEntryOptional = repository.findById(entry.getId());

        // Then
        assertThat(retrievedAdventureLorebookEntryOptional).isNotNull().isNotEmpty();

        AdventureLorebookEntry retrievedAdventureLorebookEntry = retrievedAdventureLorebookEntryOptional.get();
        assertThat(retrievedAdventureLorebookEntry.getId()).isEqualTo(entry.getId());
    }

    @Test
    public void retrieveAdventureLorebookEntryByPlayerId() {

        // Given
        String playerId = "4234234";
        Adventure adventure = adventureRepository.save(AdventureFixture.publicMultiplayerAdventure().build());
        AdventureLorebookEntry entry = repository.save(AdventureLorebookEntryFixture.sampleLorebookEntry()
                .id(null)
                .playerDiscordId(playerId)
                .adventureId(adventure.getId())
                .build());

        // When
        Optional<AdventureLorebookEntry> retrievedAdventureLorebookEntryOptional = repository.findByPlayerDiscordId(playerId, adventure.getId());

        // Then
        assertThat(retrievedAdventureLorebookEntryOptional).isNotNull().isNotEmpty();

        AdventureLorebookEntry retrievedAdventureLorebookEntry = retrievedAdventureLorebookEntryOptional.get();
        assertThat(retrievedAdventureLorebookEntry.getId()).isEqualTo(entry.getId());
    }

    @Test
    public void emptyResultWhenAssetDoesntExist() {

        // Given
        String entryId = "WRLDID";

        // When
        Optional<AdventureLorebookEntry> retrievedAdventureLorebookEntryOptional = repository.findById(entryId);

        // Then
        assertThat(retrievedAdventureLorebookEntryOptional).isNotNull().isEmpty();
    }

    @Test
    public void deleteAdventureLorebookEntry() {

        // Given
        AdventureLorebookEntry entry = repository.save(AdventureLorebookEntryFixture.sampleLorebookEntry()
                .id(null)
                .build());

        // When
        repository.deleteById(entry.getId());

        // Then
        assertThat(repository.findById(entry.getId())).isNotNull().isEmpty();
    }

    @Test
    public void searchEntries_whenNoSearchParameters_thenReturnAllEntriesThatExist() {

        // Given
        AdventureLorebookEntryEntity gpt4Omni = AdventureLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .build();

        AdventureLorebookEntryEntity gpt4Mini = AdventureLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .build();

        AdventureLorebookEntryEntity gpt354k = AdventureLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchAdventureLorebookEntries query = SearchAdventureLorebookEntries.builder()
                .adventureId("WRLDID")
                .build();

        // When
        SearchAdventureLorebookEntriesResult result = repository.searchBy(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetAdventureLorebookEntryResult> entries = result.getResults();
        assertThat(entries.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(entries.get(1).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(entries.get(2).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void searchEntries_whenNoParameters_andOrderingAscending_thenReturnAllEntriesThatExistAscending() {

        // Given

        AdventureLorebookEntryEntity gpt4Omni = AdventureLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .build();

        AdventureLorebookEntryEntity gpt4Mini = AdventureLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .build();

        AdventureLorebookEntryEntity gpt354k = AdventureLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchAdventureLorebookEntries query = SearchAdventureLorebookEntries.builder()
                .adventureId("WRLDID")
                .direction("ASC")
                .build();

        // When
        SearchAdventureLorebookEntriesResult result = repository.searchBy(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetAdventureLorebookEntryResult> entries = result.getResults();
        assertThat(entries.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(entries.get(1).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(entries.get(2).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void searchEntries_whenNoParameters_andOrderingDescending_thenReturnAllEntriesThatExistDescending() {

        // Given

        AdventureLorebookEntryEntity gpt4Omni = AdventureLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .build();

        AdventureLorebookEntryEntity gpt4Mini = AdventureLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .build();

        AdventureLorebookEntryEntity gpt354k = AdventureLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchAdventureLorebookEntries query = SearchAdventureLorebookEntries.builder()
                .adventureId("WRLDID")
                .direction("DESC")
                .build();

        // When
        SearchAdventureLorebookEntriesResult result = repository.searchBy(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetAdventureLorebookEntryResult> entries = result.getResults();
        assertThat(entries.get(0).getName()).isEqualTo(gpt354k.getName());
        assertThat(entries.get(1).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(entries.get(2).getName()).isEqualTo(gpt4Omni.getName());
    }

    @Test
    public void searchEntries_whenNoParameters_andOrderByName_andDirectionAscending_thenReturnAllEntriesThatExistInOrderByNameAscending() {

        // Given

        AdventureLorebookEntryEntity gpt4Omni = AdventureLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Number 2")
                .build();

        AdventureLorebookEntryEntity gpt4Mini = AdventureLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Number 1")
                .build();

        AdventureLorebookEntryEntity gpt354k = AdventureLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventureLorebookEntries query = SearchAdventureLorebookEntries.builder()
                .adventureId("WRLDID")
                .sortByField("name")
                .page(1)
                .items(10)
                .build();

        // When
        SearchAdventureLorebookEntriesResult result = repository.searchBy(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetAdventureLorebookEntryResult> entries = result.getResults();
        assertThat(entries.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(entries.get(1).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(entries.get(2).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void searchEntries_whenNoParameters_andOrderByName_andDirectionDescending_thenReturnAllEntriesThatExistInOrderByNameDescending() {

        // Given

        AdventureLorebookEntryEntity gpt4Omni = AdventureLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Number 2")
                .build();

        AdventureLorebookEntryEntity gpt4Mini = AdventureLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Number 1")
                .build();

        AdventureLorebookEntryEntity gpt354k = AdventureLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventureLorebookEntries query = SearchAdventureLorebookEntries.builder()
                .adventureId("WRLDID")
                .sortByField("name")
                .direction("DESC")
                .build();

        // When
        SearchAdventureLorebookEntriesResult result = repository.searchBy(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetAdventureLorebookEntryResult> entries = result.getResults();
        assertThat(entries.get(0).getName()).isEqualTo(gpt354k.getName());
        assertThat(entries.get(1).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(entries.get(2).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchEntries_whenFilteredByName_thenReturnOnlyEntriesWithThatName() {

        // Given
        AdventureLorebookEntryEntity gpt4Omni = AdventureLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Number 1")
                .build();

        AdventureLorebookEntryEntity gpt4Mini = AdventureLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Number 2")
                .build();

        AdventureLorebookEntryEntity gpt354k = AdventureLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4Omni, gpt4Mini, gpt354k));

        SearchAdventureLorebookEntries query = SearchAdventureLorebookEntries.builder()
                .adventureId("WRLDID")
                .name("Number 2")
                .build();

        // When
        SearchAdventureLorebookEntriesResult result = repository.searchBy(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);

        List<GetAdventureLorebookEntryResult> entries = result.getResults();
        assertThat(entries.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void findAllEntriesByRegex_whenValidRegex_thenReturnMatchingEntries() {

        // Then
        Adventure adventure = adventureRepository.save(AdventureFixture.privateMultiplayerAdventure().build());
        AdventureLorebookEntryEntity gpt4Omni = AdventureLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("John")
                .regex("[Jj]ohn")
                .adventureId(adventure.getId())
                .build();

        AdventureLorebookEntryEntity gpt4Mini = AdventureLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Immune")
                .regex("[Ii]mmun(e|ity)")
                .adventureId(adventure.getId())
                .build();

        AdventureLorebookEntryEntity gpt354k = AdventureLorebookEntryEntityFixture.sampleLorebookEntry()
                .id(null)
                .name("Archmage")
                .regex("[Aa]rch(|-|\s)[Mm]age")
                .adventureId(adventure.getId())
                .build();

        jpaRepository.saveAll(Lists.list(gpt4Omni, gpt4Mini, gpt354k));

        String archmageLowerCase = "archmage";
        String archmageDash = "Arch-mage";
        String archmageSpace = "Arch Mage";
        String immunityLowerCase = "immunity";
        String triggerAll = "John, the Arch-Mage, is immune to disease";

        // When
        List<AdventureLorebookEntry> archmageLowerCaseResult = repository.findAllByRegex(archmageLowerCase, adventure.getId());
        List<AdventureLorebookEntry> archmageDashResult = repository.findAllByRegex(archmageDash, adventure.getId());
        List<AdventureLorebookEntry> archmageSpaceResult = repository.findAllByRegex(archmageSpace, adventure.getId());
        List<AdventureLorebookEntry> immunityLowerCaseResult = repository.findAllByRegex(immunityLowerCase, adventure.getId());
        List<AdventureLorebookEntry> allEntries = repository.findAllByRegex(triggerAll, adventure.getId());

        // Then
        assertThat(archmageLowerCaseResult).isNotNull().isNotEmpty().hasSize(1);
        assertThat(archmageDashResult).isNotNull().isNotEmpty().hasSize(1);
        assertThat(archmageSpaceResult).isNotNull().isNotEmpty().hasSize(1);
        assertThat(immunityLowerCaseResult).isNotNull().isNotEmpty().hasSize(1);
        assertThat(allEntries).isNotNull().isNotEmpty().hasSize(3);
    }

    @Test
    public void updateAdventure() {

        // Given
        AdventureLorebookEntry originalEntry = repository.save(AdventureLorebookEntryFixture.sampleLorebookEntry()
                .id(null)
                .version(0)
                .build());

        AdventureLorebookEntry entryToBeUpdated = AdventureLorebookEntryFixture.sampleLorebookEntry()
                .id(originalEntry.getId())
                .name("new name")
                .version(originalEntry.getVersion())
                .build();

        // When
        AdventureLorebookEntry updatedAdventureLorebookEntry = repository.save(entryToBeUpdated);

        // Then
        assertThat(originalEntry.getVersion()).isZero();
        assertThat(updatedAdventureLorebookEntry.getVersion()).isOne();
    }
}
