package me.moirai.discordbot.infrastructure.outbound.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import me.moirai.discordbot.core.application.usecase.adventure.result.GetAdventureResult;
import me.moirai.discordbot.core.application.usecase.adventure.result.SearchAdventuresResult;
import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.adventure.AdventureFixture;
import me.moirai.discordbot.infrastructure.outbound.persistence.adventure.AdventureEntity;
import me.moirai.discordbot.infrastructure.outbound.persistence.adventure.AdventureEntityFixture;

@ExtendWith(MockitoExtension.class)
public class AdventurePersistenceMapperTest {

    @InjectMocks
    private AdventurePersistenceMapper mapper;

    @Test
    public void mapAdventureDomainToPersistence_whenCreatorIdProvided_thenAdventureIsCreatedWithCreatorId() {

        // Given
        String creatorDiscordId = "CRTRID";
        Adventure adventure = AdventureFixture.privateMultiplayerAdventure()
                .creatorDiscordId(creatorDiscordId)
                .build();

        // When
        AdventureEntity entity = mapper.mapToEntity(adventure);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo(adventure.getName());
        assertThat(entity.getOwnerDiscordId()).isEqualTo(adventure.getOwnerDiscordId());
        assertThat(entity.getCreatorDiscordId()).isEqualTo(adventure.getCreatorDiscordId());
        assertThat(entity.getCreationDate()).isEqualTo(adventure.getCreationDate());
        assertThat(entity.getLastUpdateDate()).isEqualTo(adventure.getLastUpdateDate());
        assertThat(entity.getUsersAllowedToRead()).hasSameElementsAs(adventure.getUsersAllowedToRead());
        assertThat(entity.getVisibility()).isEqualTo(adventure.getVisibility().name());
        assertThat(entity.getDiscordChannelId()).isEqualTo(adventure.getDiscordChannelId());
        assertThat(entity.getWorldId()).isEqualTo(adventure.getWorldId());
        assertThat(entity.getPersonaId()).isEqualTo(adventure.getPersonaId());
        assertThat(entity.getGameMode().toLowerCase()).isEqualTo(adventure.getGameMode().toString().toLowerCase());
    }

    @Test
    public void mapAdventureDomainToPersistence_whenCreatorIdNull_thenAdventureIsCreatedWithOwnerId() {

        // Given
        Adventure adventure = AdventureFixture.privateMultiplayerAdventure()
                .creatorDiscordId(null)
                .build();

        // When
        AdventureEntity entity = mapper.mapToEntity(adventure);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo(adventure.getName());
        assertThat(entity.getOwnerDiscordId()).isEqualTo(adventure.getOwnerDiscordId());
        assertThat(entity.getCreatorDiscordId()).isEqualTo(adventure.getOwnerDiscordId());
        assertThat(entity.getCreationDate()).isEqualTo(adventure.getCreationDate());
        assertThat(entity.getLastUpdateDate()).isEqualTo(adventure.getLastUpdateDate());
        assertThat(entity.getUsersAllowedToRead()).hasSameElementsAs(adventure.getUsersAllowedToRead());
        assertThat(entity.getVisibility()).isEqualTo(adventure.getVisibility().name());
        assertThat(entity.getDiscordChannelId()).isEqualTo(adventure.getDiscordChannelId());
        assertThat(entity.getWorldId()).isEqualTo(adventure.getWorldId());
        assertThat(entity.getPersonaId()).isEqualTo(adventure.getPersonaId());
        assertThat(entity.getGameMode().toLowerCase()).isEqualTo(adventure.getGameMode().toString().toLowerCase());
    }

    @Test
    public void mapAdventurePersistenceToDomain_whenPersistenceEntityProvided_thenAdventureIsCreated() {

        // Given
        String creatorDiscordId = "CRTRID";
        AdventureEntity adventure = AdventureEntityFixture.sample()
                .creatorDiscordId(creatorDiscordId)
                .build();

        // When
        Adventure entity = mapper.mapFromEntity(adventure);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo(adventure.getName());
        assertThat(entity.getOwnerDiscordId()).isEqualTo(adventure.getOwnerDiscordId());
        assertThat(entity.getCreatorDiscordId()).isEqualTo(adventure.getCreatorDiscordId());
        assertThat(entity.getCreationDate()).isEqualTo(adventure.getCreationDate());
        assertThat(entity.getLastUpdateDate()).isEqualTo(adventure.getLastUpdateDate());
        assertThat(entity.getUsersAllowedToRead()).hasSameElementsAs(adventure.getUsersAllowedToRead());
        assertThat(entity.getUsersAllowedToWrite()).hasSameElementsAs(adventure.getUsersAllowedToWrite());
        assertThat(entity.getGameMode().toString().toLowerCase()).isEqualTo(adventure.getGameMode().toLowerCase());
    }

    @Test
    public void mapAdventureDomain_whenGetOperation_thenMapToGetResult() {

        // Given
        AdventureEntity adventure = AdventureEntityFixture.sample().build();

        // When
        GetAdventureResult result = mapper.mapToResult(adventure);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(adventure.getId());
        assertThat(result.getName()).isEqualTo(adventure.getName());
        assertThat(result.getVisibility()).isEqualTo(adventure.getVisibility());
        assertThat(result.getUsersAllowedToRead()).hasSameElementsAs(adventure.getUsersAllowedToRead());
        assertThat(result.getUsersAllowedToWrite()).hasSameElementsAs(adventure.getUsersAllowedToWrite());
        assertThat(result.getCreationDate()).isEqualTo(adventure.getCreationDate());
        assertThat(result.getLastUpdateDate()).isEqualTo(adventure.getLastUpdateDate());
        assertThat(result.getOwnerDiscordId()).isEqualTo(adventure.getOwnerDiscordId());
        assertThat(result.getGameMode()).isEqualTo(adventure.getGameMode());
    }

    @Test
    public void mapAdventureDomain_whenSearchAdventure_thenMapToServer() {

        // Given
        List<AdventureEntity> adventures = IntStream.range(0, 20)
                .mapToObj(op -> AdventureEntityFixture.sample()
                        .id(String.valueOf(op + 1))
                        .build())
                .toList();

        Pageable pageable = Pageable.ofSize(10);
        Page<AdventureEntity> page = new PageImpl<>(adventures, pageable, 20);

        // When
        SearchAdventuresResult result = mapper.mapToResult(page);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPage()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getItems()).isEqualTo(20);
    }
}
