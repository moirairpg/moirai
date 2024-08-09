package me.moirai.discordbot.infrastructure.outbound.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import me.moirai.discordbot.core.application.usecase.persona.result.GetPersonaResult;
import me.moirai.discordbot.core.application.usecase.persona.result.SearchPersonasResult;
import me.moirai.discordbot.core.domain.persona.Persona;
import me.moirai.discordbot.core.domain.persona.PersonaFixture;
import me.moirai.discordbot.infrastructure.outbound.persistence.persona.PersonaEntity;
import me.moirai.discordbot.infrastructure.outbound.persistence.persona.PersonaEntityFixture;

@ExtendWith(MockitoExtension.class)
public class PersonaPersistenceMapperTest {

    @InjectMocks
    private PersonaPersistenceMapper mapper;

    @Test
    public void mapPersonaDomainToPersistence_whenCreatorIdProvided_thenPersonaIsCreatedWithCreatorId() {

        // Given
        String creatorDiscordId = "CRTRID";
        Persona persona = PersonaFixture.privatePersona()
                .creatorDiscordId(creatorDiscordId)
                .build();

        // When
        PersonaEntity entity = mapper.mapToEntity(persona);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo(persona.getName());
        assertThat(entity.getPersonality()).isEqualTo(persona.getPersonality());
        assertThat(entity.getOwnerDiscordId()).isEqualTo(persona.getOwnerDiscordId());
        assertThat(entity.getCreatorDiscordId()).isEqualTo(persona.getCreatorDiscordId());
        assertThat(entity.getCreationDate()).isEqualTo(persona.getCreationDate());
        assertThat(entity.getLastUpdateDate()).isEqualTo(persona.getLastUpdateDate());
        assertThat(entity.getUsersAllowedToRead()).hasSameElementsAs(persona.getUsersAllowedToRead());
        assertThat(entity.getUsersAllowedToWrite()).hasSameElementsAs(persona.getUsersAllowedToWrite());
        assertThat(entity.getNudge().getContent()).isEqualTo(persona.getNudge().getContent());
        assertThat(entity.getNudge().getRole().toLowerCase())
                .isEqualTo(persona.getNudge().getRole().name().toLowerCase());
        assertThat(entity.getBump().getContent()).isEqualTo(persona.getBump().getContent());
        assertThat(entity.getBump().getFrequency()).isEqualTo(persona.getBump().getFrequency());
        assertThat(entity.getBump().getRole().toLowerCase())
                .isEqualTo(persona.getBump().getRole().name().toLowerCase());
    }

    @Test
    public void mapPersonaDomainToPersistence_whenCreatorIdNull_thenPersonaIsCreatedWithOwnerId() {

        // Given
        Persona persona = PersonaFixture.privatePersona()
                .creatorDiscordId(null)
                .build();

        // When
        PersonaEntity entity = mapper.mapToEntity(persona);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo(persona.getName());
        assertThat(entity.getPersonality()).isEqualTo(persona.getPersonality());
        assertThat(entity.getOwnerDiscordId()).isEqualTo(persona.getOwnerDiscordId());
        assertThat(entity.getCreatorDiscordId()).isEqualTo(persona.getOwnerDiscordId());
        assertThat(entity.getCreationDate()).isEqualTo(persona.getCreationDate());
        assertThat(entity.getLastUpdateDate()).isEqualTo(persona.getLastUpdateDate());
        assertThat(entity.getUsersAllowedToRead()).hasSameElementsAs(persona.getUsersAllowedToRead());
        assertThat(entity.getUsersAllowedToWrite()).hasSameElementsAs(persona.getUsersAllowedToWrite());
        assertThat(entity.getNudge().getContent()).isEqualTo(persona.getNudge().getContent());
        assertThat(entity.getNudge().getRole().toLowerCase())
                .isEqualTo(persona.getNudge().getRole().name().toLowerCase());
        assertThat(entity.getBump().getContent()).isEqualTo(persona.getBump().getContent());
        assertThat(entity.getBump().getFrequency()).isEqualTo(persona.getBump().getFrequency());
        assertThat(entity.getBump().getRole().toLowerCase())
                .isEqualTo(persona.getBump().getRole().name().toLowerCase());
    }

    @Test
    public void mapPersonaDomainToPersistence_whenBumpIsNull_thenPersonaIsCreatedSuccessfully() {

        // Given
        Persona persona = PersonaFixture.privatePersona()
                .bump(null)
                .build();

        // When
        PersonaEntity entity = mapper.mapToEntity(persona);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getBump()).isNull();
    }

    @Test
    public void mapPersonaDomainToPersistence_whenNudgeIsNull_thenPersonaIsCreatedSuccessfully() {

        // Given
        Persona persona = PersonaFixture.privatePersona()
                .nudge(null)
                .build();

        // When
        PersonaEntity entity = mapper.mapToEntity(persona);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getNudge()).isNull();
    }

    @Test
    public void mapPersonaPersistenceToDomain_whenPersistenceEntityProvided_thenPersonaIsCreated() {

        // Given
        String creatorDiscordId = "CRTRID";
        PersonaEntity persona = PersonaEntityFixture.privatePersona()
                .creatorDiscordId(creatorDiscordId)
                .build();

        // When
        Persona entity = mapper.mapFromEntity(persona);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo(persona.getName());
        assertThat(entity.getPersonality()).isEqualTo(persona.getPersonality());
        assertThat(entity.getOwnerDiscordId()).isEqualTo(persona.getOwnerDiscordId());
        assertThat(entity.getCreatorDiscordId()).isEqualTo(persona.getCreatorDiscordId());
        assertThat(entity.getCreationDate()).isEqualTo(persona.getCreationDate());
        assertThat(entity.getLastUpdateDate()).isEqualTo(persona.getLastUpdateDate());
        assertThat(entity.getUsersAllowedToRead()).hasSameElementsAs(persona.getUsersAllowedToRead());
        assertThat(entity.getUsersAllowedToWrite()).hasSameElementsAs(persona.getUsersAllowedToWrite());
        assertThat(entity.getNudge().getContent()).isEqualTo(persona.getNudge().getContent());
        assertThat(entity.getNudge().getRole().name().toLowerCase())
                .isEqualTo(persona.getNudge().getRole().toLowerCase());
        assertThat(entity.getBump().getContent()).isEqualTo(persona.getBump().getContent());
        assertThat(entity.getBump().getFrequency()).isEqualTo(persona.getBump().getFrequency());
        assertThat(entity.getBump().getRole().name().toLowerCase())
                .isEqualTo(persona.getBump().getRole().toLowerCase());
    }

    @Test
    public void mapPersonaPersistenceToDomain_whenBumpIsNull_thenPersonaIsCreatedSuccessfullyWithEmptyBump() {

        // Given
        PersonaEntity persona = PersonaEntityFixture.privatePersona()
                .bump(null)
                .build();

        // When
        Persona entity = mapper.mapFromEntity(persona);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getBump()).isNotNull();
    }

    @Test
    public void mapPersonaPersistenceToDomain_whenNudgeIsNull_thenPersonaIsCreatedSuccessfullyWithEmptyNudge() {

        // Given
        PersonaEntity persona = PersonaEntityFixture.privatePersona()
                .nudge(null)
                .build();

        // When
        Persona entity = mapper.mapFromEntity(persona);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getNudge()).isNotNull();
    }

    @Test
    public void mapPersonaDomain_whenGetOperation_thenMapToGetResult() {

        // Given
        PersonaEntity persona = PersonaEntityFixture.privatePersona().build();

        // When
        GetPersonaResult result = mapper.mapToResult(persona);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(persona.getId());
        assertThat(result.getName()).isEqualTo(persona.getName());
        assertThat(result.getPersonality()).isEqualTo(persona.getPersonality());
        assertThat(result.getVisibility()).isEqualTo(persona.getVisibility());
        assertThat(result.getUsersAllowedToRead()).hasSameElementsAs(persona.getUsersAllowedToRead());
        assertThat(result.getUsersAllowedToWrite()).hasSameElementsAs(persona.getUsersAllowedToWrite());
        assertThat(result.getCreationDate()).isEqualTo(persona.getCreationDate());
        assertThat(result.getLastUpdateDate()).isEqualTo(persona.getLastUpdateDate());
        assertThat(result.getOwnerDiscordId()).isEqualTo(persona.getOwnerDiscordId());
    }

    @Test
    public void mapPersonaDomain_whenSearchPersona_thenMapToServer() {

        // Given
        List<PersonaEntity> personas = IntStream.range(0, 20)
                .mapToObj(op -> PersonaEntityFixture.privatePersona()
                        .id(String.valueOf(op + 1))
                        .build())
                .collect(Collectors.toList());

        Pageable pageable = Pageable.ofSize(10);
        Page<PersonaEntity> page = new PageImpl<>(personas, pageable, 20);

        // When
        SearchPersonasResult result = mapper.mapToResult(page);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPage()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getItems()).isEqualTo(20);
    }
}
