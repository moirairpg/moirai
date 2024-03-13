package es.thalesalv.chatrpg.infrastructure.outbound.persistence.persona;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import es.thalesalv.chatrpg.AbstractIntegrationTest;
import es.thalesalv.chatrpg.core.application.query.persona.GetPersonaResult;
import es.thalesalv.chatrpg.core.application.query.persona.SearchPersonas;
import es.thalesalv.chatrpg.core.application.query.persona.SearchPersonasResult;
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
        assertThat(createdPersona.getWriterUsers()).hasSameElementsAs(persona.getWriterUsers());
        assertThat(createdPersona.getReaderUsers()).hasSameElementsAs(persona.getReaderUsers());

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
    public void returnAllPersonasWhenSearchingWithoutParametersAsc() {

        // Given
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

        SearchPersonas query = SearchPersonas.builder().build();

        // When
        SearchPersonasResult result = repository.searchPersonas(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty();

        List<GetPersonaResult> personas = result.getResults();
        assertThat(personas.get(0).getName()).isEqualTo(gpt4128k.getName());
        assertThat(personas.get(1).getName()).isEqualTo(gpt3516k.getName());
        assertThat(personas.get(2).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void returnAllPersonasWhenSearchingWithoutParametersDesc() {

        // Given
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

        SearchPersonas query = SearchPersonas.builder()
                .direction("DESC")
                .page(1)
                .items(10)
                .build();

        // When
        SearchPersonasResult result = repository.searchPersonas(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty();

        List<GetPersonaResult> personas = result.getResults();
        assertThat(personas.get(0).getName()).isEqualTo(gpt354k.getName());
        assertThat(personas.get(1).getName()).isEqualTo(gpt3516k.getName());
        assertThat(personas.get(2).getName()).isEqualTo(gpt4128k.getName());
    }

    @Test
    public void searchPersonaOrderByNameAsc() {

        // Given
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

        SearchPersonas query = SearchPersonas.builder()
                .sortByField("name")
                .build();

        // When
        SearchPersonasResult result = repository.searchPersonas(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty();

        List<GetPersonaResult> personas = result.getResults();
        assertThat(personas.get(0).getName()).isEqualTo(gpt3516k.getName());
        assertThat(personas.get(1).getName()).isEqualTo(gpt4128k.getName());
        assertThat(personas.get(2).getName()).isEqualTo(gpt354k.getName());
    }

    @Test
    public void searchPersonaOrderByNameDesc() {

        // Given
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

        SearchPersonas query = SearchPersonas.builder()
                .sortByField("name")
                .direction("DESC")
                .build();

        // When
        SearchPersonasResult result = repository.searchPersonas(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty();

        List<GetPersonaResult> personas = result.getResults();
        assertThat(personas.get(0).getName()).isEqualTo(gpt354k.getName());
        assertThat(personas.get(1).getName()).isEqualTo(gpt4128k.getName());
        assertThat(personas.get(2).getName()).isEqualTo(gpt3516k.getName());
    }

    @Test
    public void searchPersonaFilterByName() {

        // Given
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

        SearchPersonas query = SearchPersonas.builder()
                .name("Number 2")
                .build();

        // When
        SearchPersonasResult result = repository.searchPersonas(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getResults()).isNotNull().isNotEmpty().hasSize(1);

        List<GetPersonaResult> personas = result.getResults();
        assertThat(personas.get(0).getName()).isEqualTo(gpt3516k.getName());
    }
}
