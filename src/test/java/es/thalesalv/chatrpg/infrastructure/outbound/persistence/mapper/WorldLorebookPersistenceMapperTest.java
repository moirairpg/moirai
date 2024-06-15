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

import es.thalesalv.chatrpg.core.application.usecase.world.result.GetWorldLorebookEntryResult;
import es.thalesalv.chatrpg.core.application.usecase.world.result.SearchWorldLorebookEntriesResult;
import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntry;
import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntryFixture;
import es.thalesalv.chatrpg.infrastructure.outbound.persistence.world.WorldLorebookEntryEntity;
import es.thalesalv.chatrpg.infrastructure.outbound.persistence.world.WorldLorebookEntryEntityFixture;

@ExtendWith(MockitoExtension.class)
public class WorldLorebookPersistenceMapperTest {

    @InjectMocks
    private WorldLorebookPersistenceMapper mapper;

    @Test
    public void mapWorldLorebookEntryDomainToPersistence_whenCreatorIdProvided_thenWorldLorebookEntryIsCreatedWithCreatorId() {

        // Given
        String creatorDiscordId = "CRTRID";
        WorldLorebookEntry worldLorebookEntry = WorldLorebookEntryFixture.sampleLorebookEntry()
                .creatorDiscordId(creatorDiscordId)
                .build();

        // When
        WorldLorebookEntryEntity entity = mapper.mapToEntity(worldLorebookEntry);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo(worldLorebookEntry.getName());
        assertThat(entity.getRegex()).isEqualTo(worldLorebookEntry.getRegex());
        assertThat(entity.getDescription()).isEqualTo(worldLorebookEntry.getDescription());
        assertThat(entity.getCreatorDiscordId()).isEqualTo(worldLorebookEntry.getCreatorDiscordId());
        assertThat(entity.getCreationDate()).isEqualTo(worldLorebookEntry.getCreationDate());
        assertThat(entity.getLastUpdateDate()).isEqualTo(worldLorebookEntry.getLastUpdateDate());
    }

    @Test
    public void mapWorldLorebookEntryDomainToPersistence_whenCreatorIdNull_thenWorldLorebookEntryIsCreatedWithOwnerId() {

        // Given
        WorldLorebookEntry worldLorebookEntry = WorldLorebookEntryFixture.sampleLorebookEntry()
                .creatorDiscordId(null)
                .build();

        // When
        WorldLorebookEntryEntity entity = mapper.mapToEntity(worldLorebookEntry);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo(worldLorebookEntry.getName());
        assertThat(entity.getRegex()).isEqualTo(worldLorebookEntry.getRegex());
        assertThat(entity.getDescription()).isEqualTo(worldLorebookEntry.getDescription());
        assertThat(entity.getCreatorDiscordId()).isEqualTo(worldLorebookEntry.getCreatorDiscordId());
        assertThat(entity.getCreationDate()).isEqualTo(worldLorebookEntry.getCreationDate());
        assertThat(entity.getLastUpdateDate()).isEqualTo(worldLorebookEntry.getLastUpdateDate());
    }

    @Test
    public void mapWorldLorebookEntryPersistenceToDomain_whenPersistenceEntityProvided_thenWorldLorebookEntryIsCreated() {

        // Given
        String creatorDiscordId = "CRTRID";
        WorldLorebookEntryEntity worldLorebookEntry = WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                .creatorDiscordId(creatorDiscordId)
                .build();

        // When
        WorldLorebookEntry entity = mapper.mapFromEntity(worldLorebookEntry);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo(worldLorebookEntry.getName());
        assertThat(entity.getRegex()).isEqualTo(worldLorebookEntry.getRegex());
        assertThat(entity.getDescription()).isEqualTo(worldLorebookEntry.getDescription());
        assertThat(entity.getCreatorDiscordId()).isEqualTo(worldLorebookEntry.getCreatorDiscordId());
        assertThat(entity.getCreationDate()).isEqualTo(worldLorebookEntry.getCreationDate());
        assertThat(entity.getLastUpdateDate()).isEqualTo(worldLorebookEntry.getLastUpdateDate());
    }

    @Test
    public void mapWorldLorebookEntryDomain_whenGetOperation_thenMapToGetResult() {

        // Given
        WorldLorebookEntryEntity worldLorebookEntry = WorldLorebookEntryEntityFixture.sampleLorebookEntry().build();

        // When
        GetWorldLorebookEntryResult result = mapper.mapToResult(worldLorebookEntry);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(worldLorebookEntry.getName());
        assertThat(result.getRegex()).isEqualTo(worldLorebookEntry.getRegex());
        assertThat(result.getDescription()).isEqualTo(worldLorebookEntry.getDescription());
    }

    @Test
    public void mapWorldLorebookEntryDomain_whenSearchWorldLorebookEntry_thenMapToServer() {

        // Given
        List<WorldLorebookEntryEntity> worldLorebookEntries = IntStream.range(0, 20)
                .mapToObj(op -> WorldLorebookEntryEntityFixture.sampleLorebookEntry()
                        .id(String.valueOf(op + 1))
                        .build())
                .collect(Collectors.toList());

        Pageable pageable = Pageable.ofSize(10);
        Page<WorldLorebookEntryEntity> page = new PageImpl<>(worldLorebookEntries, pageable, 20);

        // When
        SearchWorldLorebookEntriesResult result = mapper.mapToResult(page);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPage()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getItems()).isEqualTo(20);
    }
}
