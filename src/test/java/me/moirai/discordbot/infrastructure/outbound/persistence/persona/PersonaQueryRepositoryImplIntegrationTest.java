package me.moirai.discordbot.infrastructure.outbound.persistence.persona;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import me.moirai.discordbot.AbstractIntegrationTest;
import me.moirai.discordbot.core.application.port.PersonaQueryRepository;
import me.moirai.discordbot.core.application.usecase.persona.request.SearchPersonas;
import me.moirai.discordbot.core.application.usecase.persona.result.GetPersonaResult;
import me.moirai.discordbot.core.application.usecase.persona.result.SearchPersonasResult;
import me.moirai.discordbot.core.domain.persona.Persona;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteEntity;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;

public class PersonaQueryRepositoryImplIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PersonaQueryRepository repository;

    @Autowired
    private PersonaJpaRepository jpaRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @BeforeEach
    public void before() {
        jpaRepository.deleteAllInBatch();
    }

    @Test
    public void retrievePersonaById() {

        // Given
        PersonaEntity persona = jpaRepository.save(PersonaEntityFixture.privatePersona()
                .id(null)
                .build());

        // When
        Optional<Persona> retrievedPersonaOptional = repository.findById(persona.getId());

        // Then
        assertThat(retrievedPersonaOptional).isNotNull().isNotEmpty();

        Persona retrievedPersona = retrievedPersonaOptional.get();
        assertThat(retrievedPersona.getId()).isEqualTo(persona.getId());
    }

    @Test
    public void emptyResultWhenAssetDoesntExist() {

        // Given
        String personaId = "PRSNDID";

        // When
        Optional<Persona> retrievedPersonaOptional = repository.findById(personaId);

        // Then
        assertThat(retrievedPersonaOptional).isNotNull().isEmpty();
    }

    @Test
    public void returnAllPersonasWhenSearchingWithoutParameters() {

        // Given
        String ownerDiscordId = "586678721356875";

        PersonaEntity gpt4Omni = PersonaEntityFixture.privatePersona()
                .id(null)
                .ownerDiscordId(ownerDiscordId)
                .build();

        PersonaEntity gpt4Mini = PersonaEntityFixture.privatePersona()
                .id(null)
                .ownerDiscordId("580485734")
                .usersAllowedToRead(singletonList(ownerDiscordId))
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .ownerDiscordId("580485734")
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchPersonas query = SearchPersonas.builder()
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchPersonasResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetPersonaResult> personas = result.getResults();
        assertThat(personas.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(personas.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void returnAllPersonasWhenSearchingWithoutParametersAsc() {

        // Given
        String ownerDiscordId = "586678721356875";

        PersonaEntity gpt4Omni = PersonaEntityFixture.privatePersona()
                .id(null)
                .build();

        PersonaEntity gpt4Mini = PersonaEntityFixture.privatePersona()
                .id(null)
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchPersonas query = SearchPersonas.builder()
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchPersonasResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetPersonaResult> personas = result.getResults();
        assertThat(personas.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(personas.get(1).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(personas.get(2).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void returnAllPersonasWhenSearchingWithoutParametersDesc() {

        // Given
        String ownerDiscordId = "586678721356875";

        PersonaEntity gpt4Omni = PersonaEntityFixture.privatePersona()
                .id(null)
                .build();

        PersonaEntity gpt4Mini = PersonaEntityFixture.privatePersona()
                .id(null)
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchPersonas query = SearchPersonas.builder()
                .direction("DESC")
                .page(1)
                .size(10)
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchPersonasResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetPersonaResult> personas = result.getResults();
        assertThat(personas.get(0).getName()).isEqualTo(gpt354k.getName());
        assertThat(personas.get(1).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(personas.get(2).getName()).isEqualTo(gpt4Omni.getName());
    }

    @Test
    public void searchPersonaOrderByNameAsc() {

        // Given
        String ownerDiscordId = "586678721356875";

        PersonaEntity gpt4Omni = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 2")
                .build();

        PersonaEntity gpt4Mini = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 1")
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchPersonas query = SearchPersonas.builder()
                .sortingField("name")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchPersonasResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetPersonaResult> personas = result.getResults();
        assertThat(personas.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(personas.get(1).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(personas.get(2).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void searchPersonaOrderByNameDesc() {

        // Given
        String ownerDiscordId = "586678721356875";

        PersonaEntity gpt4Omni = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 2")
                .build();

        PersonaEntity gpt4Mini = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 1")
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchPersonas query = SearchPersonas.builder()
                .sortingField("name")
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchPersonasResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(3);

        List<GetPersonaResult> personas = result.getResults();
        assertThat(personas.get(0).getName()).isEqualTo(gpt354k.getName());
        assertThat(personas.get(1).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(personas.get(2).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchPersonaFilterByName() {

        // Given
        String ownerDiscordId = "586678721356875";

        PersonaEntity gpt4Omni = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 1")
                .build();

        PersonaEntity gpt4Mini = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 2")
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchPersonas query = SearchPersonas.builder()
                .name("Number 2")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchPersonasResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);

        List<GetPersonaResult> personas = result.getResults();
        assertThat(personas.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void returnAllPersonasWhenSearchingWithoutParametersShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        PersonaEntity gpt4Omni = PersonaEntityFixture.privatePersona()
                .id(null)
                .ownerDiscordId(ownerDiscordId)
                .build();

        PersonaEntity gpt4Mini = PersonaEntityFixture.privatePersona()
                .id(null)
                .ownerDiscordId("580485734")
                .usersAllowedToWrite(singletonList(ownerDiscordId))
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .ownerDiscordId("580485734")
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchPersonas query = SearchPersonas.builder()
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchPersonasResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetPersonaResult> personas = result.getResults();
        assertThat(personas.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(personas.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void returnAllPersonasWhenSearchingWithoutParametersAscShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        PersonaEntity gpt4Omni = PersonaEntityFixture.privatePersona()
                .id(null)
                .ownerDiscordId(ownerDiscordId)
                .build();

        PersonaEntity gpt4Mini = PersonaEntityFixture.privatePersona()
                .id(null)
                .usersAllowedToWrite(singletonList(ownerDiscordId))
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchPersonas query = SearchPersonas.builder()
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchPersonasResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetPersonaResult> personas = result.getResults();
        assertThat(personas.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(personas.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void returnAllPersonasWhenSearchingWithoutParametersDescShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        PersonaEntity gpt4Omni = PersonaEntityFixture.privatePersona()
                .id(null)
                .ownerDiscordId(ownerDiscordId)
                .build();

        PersonaEntity gpt4Mini = PersonaEntityFixture.privatePersona()
                .id(null)
                .usersAllowedToWrite(singletonList(ownerDiscordId))
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchPersonas query = SearchPersonas.builder()
                .direction("DESC")
                .page(1)
                .size(10)
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchPersonasResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetPersonaResult> personas = result.getResults();
        assertThat(personas.get(0).getName()).isEqualTo(gpt354k.getName());
        assertThat(personas.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchPersonaOrderByNameAscShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        PersonaEntity gpt4Omni = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 2")
                .ownerDiscordId(ownerDiscordId)
                .build();

        PersonaEntity gpt4Mini = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 1")
                .usersAllowedToWrite(singletonList(ownerDiscordId))
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchPersonas query = SearchPersonas.builder()
                .sortingField("name")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchPersonasResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetPersonaResult> personas = result.getResults();
        assertThat(personas.get(0).getName()).isEqualTo(gpt4Mini.getName());
        assertThat(personas.get(1).getName()).isEqualTo(gpt4Omni.getName());
    }

    @Test
    public void searchPersonaOrderByNameDescShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        PersonaEntity gpt4Omni = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 2")
                .ownerDiscordId(ownerDiscordId)
                .build();

        PersonaEntity gpt4Mini = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 1")
                .usersAllowedToWrite(singletonList(ownerDiscordId))
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchPersonas query = SearchPersonas.builder()
                .sortingField("name")
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchPersonasResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(2);

        List<GetPersonaResult> personas = result.getResults();
        assertThat(personas.get(0).getName()).isEqualTo(gpt4Omni.getName());
        assertThat(personas.get(1).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchPersonaFilterByNameShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        PersonaEntity gpt4Omni = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 1")
                .build();

        PersonaEntity gpt4Mini = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 2")
                .usersAllowedToWrite(singletonList(ownerDiscordId))
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchPersonas query = SearchPersonas.builder()
                .name("Number 2")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchPersonasResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);

        List<GetPersonaResult> personas = result.getResults();
        assertThat(personas.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchPersonas_whenWritingAccess_andFilterByVisibility_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721358363";

        PersonaEntity gpt4Omni = PersonaEntityFixture.publicPersona()
                .id(null)
                .name("Number 1")
                .usersAllowedToWrite(singletonList(ownerDiscordId))
                .build();

        PersonaEntity gpt4Mini = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 2")
                .usersAllowedToWrite(singletonList(ownerDiscordId))
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchPersonas query = SearchPersonas.builder()
                .requesterDiscordId(ownerDiscordId)
                .visibility("private")
                .operation("WRITE")
                .build();

        // When
        SearchPersonasResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);

        List<GetPersonaResult> personas = result.getResults();
        assertThat(personas.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void searchPersonas_whenReadingAccess_andFilterByVisibility_thenReturnResults() {

        // Given
        String ownerDiscordId = "586678721358363";

        PersonaEntity gpt4Omni = PersonaEntityFixture.publicPersona()
                .id(null)
                .name("Number 1")
                .usersAllowedToRead(singletonList(ownerDiscordId))
                .build();

        PersonaEntity gpt4Mini = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 2")
                .usersAllowedToRead(singletonList(ownerDiscordId))
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchPersonas query = SearchPersonas.builder()
                .requesterDiscordId(ownerDiscordId)
                .visibility("private")
                .operation("READ")
                .build();

        // When
        SearchPersonasResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);

        List<GetPersonaResult> personas = result.getResults();
        assertThat(personas.get(0).getName()).isEqualTo(gpt4Mini.getName());
    }

    @Test
    public void emptyResultWhenSearchingForPersonaWithWriteAccessIfUserHasNoAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        PersonaEntity gpt4Omni = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 1")
                .build();

        PersonaEntity gpt4Mini = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 2")
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(list(gpt4Omni, gpt4Mini, gpt354k));

        SearchPersonas query = SearchPersonas.builder()
                .name("Number 2")
                .requesterDiscordId(ownerDiscordId)
                .operation("WRITE")
                .build();

        // When
        SearchPersonasResult result = repository.search(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isEmpty();
    }

    @Test
    public void getFavorites_whenNoFilters_thenReturnAll() {

        // Given
        String playerDiscordId = "63456456";
        SearchPersonas request = SearchPersonas.builder()
                .requesterDiscordId(playerDiscordId)
                .favorites(true)
                .build();

        PersonaEntity persona1 = jpaRepository.save(PersonaEntityFixture.publicPersona()
                .id(null)
                .name("Number 1")
                .build());

        PersonaEntity persona2 = jpaRepository.save(PersonaEntityFixture.publicPersona()
                .id(null)
                .name("Number 2")
                .build());

        PersonaEntity persona3 = jpaRepository.save(PersonaEntityFixture.publicPersona()
                .id(null)
                .name("Number 3")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(playerDiscordId)
                .assetType("persona")
                .assetId(persona1.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(playerDiscordId)
                .assetType("persona")
                .assetId(persona2.getId())
                .build();

        FavoriteEntity favorite3 = FavoriteEntity.builder()
                .playerDiscordId(playerDiscordId)
                .assetType("persona")
                .assetId(persona3.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2, favorite3));

        // When
        SearchPersonasResult result = repository.search(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().hasSize(3);
    }

    @Test
    public void getFavorites_whenFilterByName_thenReturnAll() {

        // Given
        String nameToSearch = "targetName";
        String playerDiscordId = "63456456";
        SearchPersonas request = SearchPersonas.builder()
                .requesterDiscordId(playerDiscordId)
                .name(nameToSearch)
                .favorites(true)
                .build();

        PersonaEntity persona1 = jpaRepository.save(PersonaEntityFixture.publicPersona()
                .id(null)
                .name(nameToSearch)
                .build());

        PersonaEntity persona2 = jpaRepository.save(PersonaEntityFixture.publicPersona()
                .id(null)
                .name("Number 2")
                .build());

        PersonaEntity persona3 = jpaRepository.save(PersonaEntityFixture.publicPersona()
                .id(null)
                .name("Number 3")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(playerDiscordId)
                .assetType("persona")
                .assetId(persona1.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(playerDiscordId)
                .assetType("persona")
                .assetId(persona2.getId())
                .build();

        FavoriteEntity favorite3 = FavoriteEntity.builder()
                .playerDiscordId(playerDiscordId)
                .assetType("persona")
                .assetId(persona3.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2, favorite3));

        // When
        SearchPersonasResult result = repository.search(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().hasSize(1);
    }

    @Test
    public void getFavorites_whenFilterByVisibility_thenReturnAll() {

        // Given
        String playerDiscordId = "63456456";
        SearchPersonas request = SearchPersonas.builder()
                .requesterDiscordId(playerDiscordId)
                .visibility("public")
                .favorites(true)
                .build();

        PersonaEntity persona1 = jpaRepository.save(PersonaEntityFixture.publicPersona()
                .id(null)
                .name("Number 1")
                .build());

        PersonaEntity persona2 = jpaRepository.save(PersonaEntityFixture.publicPersona()
                .id(null)
                .name("Number 2")
                .build());

        PersonaEntity persona3 = jpaRepository.save(PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 3")
                .build());

        FavoriteEntity favorite1 = FavoriteEntity.builder()
                .playerDiscordId(playerDiscordId)
                .assetType("persona")
                .assetId(persona1.getId())
                .build();

        FavoriteEntity favorite2 = FavoriteEntity.builder()
                .playerDiscordId(playerDiscordId)
                .assetType("persona")
                .assetId(persona2.getId())
                .build();

        FavoriteEntity favorite3 = FavoriteEntity.builder()
                .playerDiscordId(playerDiscordId)
                .assetType("persona")
                .assetId(persona3.getId())
                .build();

        favoriteRepository.saveAll(list(favorite1, favorite2, favorite3));

        // When
        SearchPersonasResult result = repository.search(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().hasSize(2);
    }
}
