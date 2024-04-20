package es.thalesalv.chatrpg.infrastructure.outbound.persistence.mapper;

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

import es.thalesalv.chatrpg.core.application.query.world.GetWorldResult;
import es.thalesalv.chatrpg.core.application.query.world.SearchWorldsResult;
import es.thalesalv.chatrpg.core.domain.world.World;
import es.thalesalv.chatrpg.core.domain.world.WorldFixture;
import es.thalesalv.chatrpg.infrastructure.outbound.persistence.world.WorldEntity;
import es.thalesalv.chatrpg.infrastructure.outbound.persistence.world.WorldEntityFixture;

@ExtendWith(MockitoExtension.class)
public class WorldPersistenceMapperTest {

    @InjectMocks
    private WorldPersistenceMapper mapper;

    @Test
    public void mapWorldDomainToPersistence_whenCreatorIdProvided_thenWorldIsCreatedWithCreatorId() {

        // Given
        String creatorDiscordId = "CRTRID";
        World world = WorldFixture.privateWorld()
                .creatorDiscordId(creatorDiscordId)
                .build();

        // When
        WorldEntity entity = mapper.mapToEntity(world);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo(world.getName());
        assertThat(entity.getOwnerDiscordId()).isEqualTo(world.getOwnerDiscordId());
        assertThat(entity.getCreatorDiscordId()).isEqualTo(world.getCreatorDiscordId());
        assertThat(entity.getCreationDate()).isEqualTo(world.getCreationDate());
        assertThat(entity.getLastUpdateDate()).isEqualTo(world.getLastUpdateDate());
        assertThat(entity.getUsersAllowedToRead()).hasSameElementsAs(world.getUsersAllowedToRead());
        assertThat(entity.getUsersAllowedToWrite()).hasSameElementsAs(world.getUsersAllowedToWrite());
        assertThat(entity.getDescription()).isEqualTo(world.getDescription());
        assertThat(entity.getAdventureStart()).isEqualTo(world.getAdventureStart());
    }

    @Test
    public void mapWorldDomainToPersistence_whenCreatorIdNull_thenWorldIsCreatedWithOwnerId() {

        // Given
        World world = WorldFixture.privateWorld()
                .creatorDiscordId(null)
                .build();

        // When
        WorldEntity entity = mapper.mapToEntity(world);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo(world.getName());
        assertThat(entity.getOwnerDiscordId()).isEqualTo(world.getOwnerDiscordId());
        assertThat(entity.getCreatorDiscordId()).isEqualTo(world.getOwnerDiscordId());
        assertThat(entity.getCreationDate()).isEqualTo(world.getCreationDate());
        assertThat(entity.getLastUpdateDate()).isEqualTo(world.getLastUpdateDate());
        assertThat(entity.getUsersAllowedToRead()).hasSameElementsAs(world.getUsersAllowedToRead());
        assertThat(entity.getUsersAllowedToWrite()).hasSameElementsAs(world.getUsersAllowedToWrite());
        assertThat(entity.getDescription()).isEqualTo(world.getDescription());
        assertThat(entity.getAdventureStart()).isEqualTo(world.getAdventureStart());
    }

    @Test
    public void mapWorldPersistenceToDomain_whenPersistenceEntityProvided_thenWorldIsCreated() {

        // Given
        String creatorDiscordId = "CRTRID";
        WorldEntity world = WorldEntityFixture.privateWorld()
                .creatorDiscordId(creatorDiscordId)
                .build();

        // When
        World entity = mapper.mapFromEntity(world);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo(world.getName());
        assertThat(entity.getOwnerDiscordId()).isEqualTo(world.getOwnerDiscordId());
        assertThat(entity.getCreatorDiscordId()).isEqualTo(world.getCreatorDiscordId());
        assertThat(entity.getCreationDate()).isEqualTo(world.getCreationDate());
        assertThat(entity.getLastUpdateDate()).isEqualTo(world.getLastUpdateDate());
        assertThat(entity.getUsersAllowedToRead()).hasSameElementsAs(world.getUsersAllowedToRead());
        assertThat(entity.getUsersAllowedToWrite()).hasSameElementsAs(world.getUsersAllowedToWrite());
        assertThat(entity.getDescription()).isEqualTo(world.getDescription());
        assertThat(entity.getAdventureStart()).isEqualTo(world.getAdventureStart());
    }

    @Test
    public void mapWorldDomain_whenGetOperation_thenMapToGetResult() {

        // Given
        WorldEntity world = WorldEntityFixture.privateWorld().build();

        // When
        GetWorldResult result = mapper.mapToResult(world);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(world.getName());
        assertThat(result.getOwnerDiscordId()).isEqualTo(world.getOwnerDiscordId());
        assertThat(result.getCreationDate()).isEqualTo(world.getCreationDate());
        assertThat(result.getLastUpdateDate()).isEqualTo(world.getLastUpdateDate());
        assertThat(result.getUsersAllowedToRead()).hasSameElementsAs(world.getUsersAllowedToRead());
        assertThat(result.getUsersAllowedToWrite()).hasSameElementsAs(world.getUsersAllowedToWrite());
        assertThat(result.getDescription()).isEqualTo(world.getDescription());
        assertThat(result.getAdventureStart()).isEqualTo(world.getAdventureStart());
    }

    @Test
    public void mapWorldDomain_whenSearchWorld_thenMapToServer() {

        // Given
        List<WorldEntity> worlds = IntStream.range(0, 20)
                .mapToObj(op -> WorldEntityFixture.privateWorld()
                        .id(String.valueOf(op + 1))
                        .build())
                .collect(Collectors.toList());

        Pageable pageable = Pageable.ofSize(10);
        Page<WorldEntity> page = new PageImpl<>(worlds, pageable, 20);

        // When
        SearchWorldsResult result = mapper.mapToResult(page);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPage()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getItems()).isEqualTo(20);
    }
}
