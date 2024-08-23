package me.moirai.discordbot.infrastructure.outbound.persistence.persona;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import me.moirai.discordbot.AbstractIntegrationTest;
import me.moirai.discordbot.core.domain.persona.Persona;
import me.moirai.discordbot.core.domain.persona.PersonaDomainRepository;
import me.moirai.discordbot.core.domain.persona.PersonaFixture;

public class PersonaDomainRepositoryImplIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PersonaDomainRepository repository;

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
    public void deletePersona() {

        // Given
        Persona persona = repository.save(PersonaFixture.privatePersona()
                .id(null)
                .build());

        // When
        repository.deleteById(persona.getId());

        // Then
        assertThat(jpaRepository.findById(persona.getId())).isNotNull().isEmpty();
    }
}
