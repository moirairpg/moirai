package es.thalesalv.chatrpg.infrastructure.outbound.persistence.persona;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.thalesalv.chatrpg.AbstractIntegrationTest;
import es.thalesalv.chatrpg.core.application.query.persona.GetPersonaResult;
import es.thalesalv.chatrpg.core.application.query.persona.SearchPersonasResult;
import es.thalesalv.chatrpg.core.application.query.persona.SearchPersonasWithReadAccess;
import es.thalesalv.chatrpg.core.application.query.persona.SearchPersonasWithWriteAccess;
import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaFixture;
import es.thalesalv.chatrpg.core.domain.persona.PersonaRepository;

public class PersonaRepositoryImplIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PersonaRepository repository;

    @Autowired
    private PersonaJpaRepository jpaRepository;

    @BeforeEach
    public void before() {
        jpaRepository.deleteAllInBatch();
    }

    @Test
    public void createPersona() {

        // Given
        Persona persona = PersonaFixture.privatePersona()
                .id(null)
                .build();

        // When
        Persona createdPersona = repository.save(persona);

        // Then
        assertThat(createdPersona).isNotNull();

        assertThat(createdPersona.getCreationDate()).isNotNull();
        assertThat(createdPersona.getLastUpdateDate()).isNotNull();

        assertThat(createdPersona.getName()).isEqualTo(persona.getName());
        assertThat(createdPersona.getPersonality()).isEqualTo(persona.getPersonality());
        assertThat(createdPersona.getVisibility()).isEqualTo(persona.getVisibility());
        assertThat(createdPersona.getUsersAllowedToWrite()).hasSameElementsAs(persona.getUsersAllowedToWrite());
        assertThat(createdPersona.getUsersAllowedToRead()).hasSameElementsAs(persona.getUsersAllowedToRead());

        assertThat(createdPersona.getBump().getContent()).isEqualTo(persona.getBump().getContent());
        assertThat(createdPersona.getBump().getRole()).isEqualTo(persona.getBump().getRole());
        assertThat(createdPersona.getBump().getFrequency()).isEqualTo(persona.getBump().getFrequency());

        assertThat(createdPersona.getNudge().getContent()).isEqualTo(persona.getNudge().getContent());
        assertThat(createdPersona.getNudge().getRole()).isEqualTo(persona.getNudge().getRole());
    }

    @Test
    public void retrievePersonaById() {

        // Given
        Persona persona = repository.save(PersonaFixture.privatePersona()
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
    public void deletePersona() {

        // Given
        Persona persona = repository.save(PersonaFixture.privatePersona()
                .id(null)
                .build());

        // When
        repository.deleteById(persona.getId());

        // Then
        assertThat(repository.findById(persona.getId())).isNotNull().isEmpty();
    }

    @Test
    public void returnAllPersonasWhenSearchingWithoutParameters() {

        // Given
        String ownerDiscordId = "586678721356875";

        PersonaEntity gpt4128k = PersonaEntityFixture.privatePersona()
                .id(null)
                .ownerDiscordId(ownerDiscordId)
                .build();

        PersonaEntity gpt3516k = PersonaEntityFixture.privatePersona()
                .id(null)
                .ownerDiscordId("580485734")
                .usersAllowedToRead(Collections.singletonList(ownerDiscordId))
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .ownerDiscordId("580485734")
                .build();

        jpaRepository.save(gpt4128k);
        jpaRepository.save(gpt3516k);
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
        assertThat(personas.get(0).getName()).isEqualTo(gpt4128k.getName());
        assertThat(personas.get(1).getName()).isEqualTo(gpt3516k.getName());
    }

    @Test
    public void returnAllPersonasWhenSearchingWithoutParametersAsc() {

        // Given
        String ownerDiscordId = "586678721356875";

        PersonaEntity gpt4128k = PersonaEntityFixture.privatePersona()
                .id(null)
                .build();

        PersonaEntity gpt3516k = PersonaEntityFixture.privatePersona()
                .id(null)
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .build();

        jpaRepository.save(gpt4128k);
        jpaRepository.save(gpt3516k);
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
        assertThat(personas.get(0).getName()).isEqualTo(gpt4128k.getName());
        assertThat(personas.get(1).getName()).isEqualTo(gpt3516k.getName());
        assertThat(personas.get(2).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void returnAllPersonasWhenSearchingWithoutParametersDesc() {

        // Given
        String ownerDiscordId = "586678721356875";

        PersonaEntity gpt4128k = PersonaEntityFixture.privatePersona()
                .id(null)
                .build();

        PersonaEntity gpt3516k = PersonaEntityFixture.privatePersona()
                .id(null)
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .build();

        jpaRepository.save(gpt4128k);
        jpaRepository.save(gpt3516k);
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
        assertThat(personas.get(1).getName()).isEqualTo(gpt3516k.getName());
        assertThat(personas.get(2).getName()).isEqualTo(gpt4128k.getName());
    }

    @Test
    public void searchPersonaOrderByNameAsc() {

        // Given
        String ownerDiscordId = "586678721356875";

        PersonaEntity gpt4128k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 2")
                .build();

        PersonaEntity gpt3516k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 1")
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4128k, gpt3516k, gpt354k));

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
        assertThat(personas.get(0).getName()).isEqualTo(gpt3516k.getName());
        assertThat(personas.get(1).getName()).isEqualTo(gpt4128k.getName());
        assertThat(personas.get(2).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void searchPersonaOrderByNameDesc() {

        // Given
        String ownerDiscordId = "586678721356875";

        PersonaEntity gpt4128k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 2")
                .build();

        PersonaEntity gpt3516k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 1")
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4128k, gpt3516k, gpt354k));

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
        assertThat(personas.get(1).getName()).isEqualTo(gpt4128k.getName());
        assertThat(personas.get(2).getName()).isEqualTo(gpt3516k.getName());
    }

    @Test
    public void searchPersonaFilterByName() {

        // Given
        String ownerDiscordId = "586678721356875";

        PersonaEntity gpt4128k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 1")
                .build();

        PersonaEntity gpt3516k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 2")
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4128k, gpt3516k, gpt354k));

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
        assertThat(personas.get(0).getName()).isEqualTo(gpt3516k.getName());
    }

    @Test
    public void returnAllPersonasWhenSearchingWithoutParametersShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        PersonaEntity gpt4128k = PersonaEntityFixture.privatePersona()
                .id(null)
                .ownerDiscordId(ownerDiscordId)
                .build();

        PersonaEntity gpt3516k = PersonaEntityFixture.privatePersona()
                .id(null)
                .ownerDiscordId("580485734")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .ownerDiscordId("580485734")
                .build();

        jpaRepository.save(gpt4128k);
        jpaRepository.save(gpt3516k);
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
        assertThat(personas.get(0).getName()).isEqualTo(gpt4128k.getName());
        assertThat(personas.get(1).getName()).isEqualTo(gpt3516k.getName());
    }

    @Test
    public void returnAllPersonasWhenSearchingWithoutParametersAscShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        PersonaEntity gpt4128k = PersonaEntityFixture.privatePersona()
                .id(null)
                .ownerDiscordId(ownerDiscordId)
                .build();

        PersonaEntity gpt3516k = PersonaEntityFixture.privatePersona()
                .id(null)
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .build();

        jpaRepository.save(gpt4128k);
        jpaRepository.save(gpt3516k);
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
        assertThat(personas.get(0).getName()).isEqualTo(gpt4128k.getName());
        assertThat(personas.get(1).getName()).isEqualTo(gpt3516k.getName());
    }

    @Test
    public void returnAllPersonasWhenSearchingWithoutParametersDescShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        PersonaEntity gpt4128k = PersonaEntityFixture.privatePersona()
                .id(null)
                .ownerDiscordId(ownerDiscordId)
                .build();

        PersonaEntity gpt3516k = PersonaEntityFixture.privatePersona()
                .id(null)
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .build();

        jpaRepository.save(gpt4128k);
        jpaRepository.save(gpt3516k);
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
        assertThat(personas.get(1).getName()).isEqualTo(gpt3516k.getName());
    }

    @Test
    public void searchPersonaOrderByNameAscShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        PersonaEntity gpt4128k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 2")
                .ownerDiscordId(ownerDiscordId)
                .build();

        PersonaEntity gpt3516k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 1")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4128k, gpt3516k, gpt354k));

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
        assertThat(personas.get(0).getName()).isEqualTo(gpt3516k.getName());
        assertThat(personas.get(1).getName()).isEqualTo(gpt4128k.getName());
    }

    @Test
    public void searchPersonaOrderByNameDescShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        PersonaEntity gpt4128k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 2")
                .ownerDiscordId(ownerDiscordId)
                .build();

        PersonaEntity gpt3516k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 1")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4128k, gpt3516k, gpt354k));

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
        assertThat(personas.get(0).getName()).isEqualTo(gpt4128k.getName());
        assertThat(personas.get(1).getName()).isEqualTo(gpt3516k.getName());
    }

    @Test
    public void searchPersonaFilterByNameShowOnlyWithWriteAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        PersonaEntity gpt4128k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 1")
                .build();

        PersonaEntity gpt3516k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 2")
                .usersAllowedToWrite(Collections.singletonList(ownerDiscordId))
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4128k, gpt3516k, gpt354k));

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
        assertThat(personas.get(0).getName()).isEqualTo(gpt3516k.getName());
    }

    @Test
    public void emptyResultWhenSearchingForPersonaWithWriteAccessIfUserHasNoAccess() {

        // Given
        String ownerDiscordId = "586678721358363";

        PersonaEntity gpt4128k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 1")
                .build();

        PersonaEntity gpt3516k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 2")
                .build();

        PersonaEntity gpt354k = PersonaEntityFixture.privatePersona()
                .id(null)
                .name("Number 3")
                .build();

        jpaRepository.saveAll(Lists.list(gpt4128k, gpt3516k, gpt354k));

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
