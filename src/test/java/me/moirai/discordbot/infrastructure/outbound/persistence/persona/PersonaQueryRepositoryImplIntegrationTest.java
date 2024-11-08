package me.moirai.discordbot.infrastructure.outbound.persistence.persona;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import me.moirai.discordbot.AbstractIntegrationTest;
import me.moirai.discordbot.core.application.port.PersonaQueryRepository;
import me.moirai.discordbot.core.application.usecase.persona.request.SearchPersonasWithReadAccess;
import me.moirai.discordbot.core.application.usecase.persona.request.SearchPersonasWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.persona.result.GetPersonaResult;
import me.moirai.discordbot.core.application.usecase.persona.result.SearchPersonasResult;
import me.moirai.discordbot.core.domain.persona.Persona;

public class PersonaQueryRepositoryImplIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PersonaQueryRepository repository;

    @Autowired
    private PersonaJpaRepository jpaRepository;

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
                .usersAllowedToRead(Collections.singletonList(ownerDiscordId))
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .ownerDiscordId("580485734")
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchPersonasWithReadAccess query = SearchPersonasWithReadAccess.builder()
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchPersonasResult result = repository.searchPersonasWithReadAccess(query);

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

        SearchPersonasWithReadAccess query = SearchPersonasWithReadAccess.builder()
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchPersonasResult result = repository.searchPersonasWithReadAccess(query);

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

        SearchPersonasWithReadAccess query = SearchPersonasWithReadAccess.builder()
                .direction("DESC")
                .page(1)
                .items(10)
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchPersonasResult result = repository.searchPersonasWithReadAccess(query);

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

        jpaRepository.saveAll(Lists.list(gpt4Omni, gpt4Mini, gpt354k));

        SearchPersonasWithReadAccess query = SearchPersonasWithReadAccess.builder()
                .sortByField("name")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchPersonasResult result = repository.searchPersonasWithReadAccess(query);

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

        jpaRepository.saveAll(Lists.list(gpt4Omni, gpt4Mini, gpt354k));

        SearchPersonasWithReadAccess query = SearchPersonasWithReadAccess.builder()
                .sortByField("name")
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchPersonasResult result = repository.searchPersonasWithReadAccess(query);

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

        jpaRepository.saveAll(Lists.list(gpt4Omni, gpt4Mini, gpt354k));

        SearchPersonasWithReadAccess query = SearchPersonasWithReadAccess.builder()
                .name("Number 2")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchPersonasResult result = repository.searchPersonasWithReadAccess(query);

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
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .ownerDiscordId("580485734")
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchPersonasWithWriteAccess query = SearchPersonasWithWriteAccess.builder()
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchPersonasResult result = repository.searchPersonasWithWriteAccess(query);

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
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchPersonasWithWriteAccess query = SearchPersonasWithWriteAccess.builder()
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchPersonasResult result = repository.searchPersonasWithWriteAccess(query);

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
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .build();

        jpaRepository.save(gpt4Omni);
        jpaRepository.save(gpt4Mini);
        jpaRepository.save(gpt354k);

        SearchPersonasWithWriteAccess query = SearchPersonasWithWriteAccess.builder()
                .direction("DESC")
                .page(1)
                .items(10)
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchPersonasResult result = repository.searchPersonasWithWriteAccess(query);

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
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4Omni, gpt4Mini, gpt354k));

        SearchPersonasWithWriteAccess query = SearchPersonasWithWriteAccess.builder()
                .sortByField("name")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchPersonasResult result = repository.searchPersonasWithWriteAccess(query);

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
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4Omni, gpt4Mini, gpt354k));

        SearchPersonasWithWriteAccess query = SearchPersonasWithWriteAccess.builder()
                .sortByField("name")
                .direction("DESC")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchPersonasResult result = repository.searchPersonasWithWriteAccess(query);

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
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4Omni, gpt4Mini, gpt354k));

        SearchPersonasWithWriteAccess query = SearchPersonasWithWriteAccess.builder()
                .name("Number 2")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchPersonasResult result = repository.searchPersonasWithWriteAccess(query);

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

        jpaRepository.saveAll(Lists.list(gpt4Omni, gpt4Mini, gpt354k));

        SearchPersonasWithWriteAccess query = SearchPersonasWithWriteAccess.builder()
                .name("Number 2")
                .requesterDiscordId(ownerDiscordId)
                .build();

        // When
        SearchPersonasResult result = repository.searchPersonasWithWriteAccess(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isEmpty();
    }
}
