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

import es.thalesalv.chatrpg.core.application.query.channelconfig.GetChannelConfigResult;
import es.thalesalv.chatrpg.core.application.query.channelconfig.SearchChannelConfigsResult;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfig;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigFixture;
import es.thalesalv.chatrpg.infrastructure.outbound.persistence.channelconfig.ChannelConfigEntity;
import es.thalesalv.chatrpg.infrastructure.outbound.persistence.channelconfig.ChannelConfigEntityFixture;

@ExtendWith(MockitoExtension.class)
public class ChannelConfigPersistenceMapperTest {

    @InjectMocks
    private ChannelConfigPersistenceMapper mapper;

    @Test
    public void mapChannelConfigDomainToPersistence_whenCreatorIdProvided_thenChannelConfigIsCreatedWithCreatorId() {

        // Given
        String creatorDiscordId = "CRTRID";
        ChannelConfig channelConfig = ChannelConfigFixture.sample()
                .creatorDiscordId(creatorDiscordId)
                .build();

        // When
        ChannelConfigEntity entity = mapper.mapToEntity(channelConfig);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo(channelConfig.getName());
        assertThat(entity.getOwnerDiscordId()).isEqualTo(channelConfig.getOwnerDiscordId());
        assertThat(entity.getCreatorDiscordId()).isEqualTo(channelConfig.getCreatorDiscordId());
        assertThat(entity.getCreationDate()).isEqualTo(channelConfig.getCreationDate());
        assertThat(entity.getLastUpdateDate()).isEqualTo(channelConfig.getLastUpdateDate());
        assertThat(entity.getUsersAllowedToRead()).hasSameElementsAs(channelConfig.getUsersAllowedToRead());
        assertThat(entity.getVisibility()).isEqualTo(channelConfig.getVisibility().name());
        assertThat(entity.getDiscordChannelId()).isEqualTo(channelConfig.getDiscordChannelId());
        assertThat(entity.getWorldId()).isEqualTo(channelConfig.getWorldId());
        assertThat(entity.getPersonaId()).isEqualTo(channelConfig.getPersonaId());
    }

    @Test
    public void mapChannelConfigDomainToPersistence_whenCreatorIdNull_thenChannelConfigIsCreatedWithOwnerId() {

        // Given
        ChannelConfig channelConfig = ChannelConfigFixture.sample()
                .creatorDiscordId(null)
                .build();

        // When
        ChannelConfigEntity entity = mapper.mapToEntity(channelConfig);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo(channelConfig.getName());
        assertThat(entity.getOwnerDiscordId()).isEqualTo(channelConfig.getOwnerDiscordId());
        assertThat(entity.getCreatorDiscordId()).isEqualTo(channelConfig.getOwnerDiscordId());
        assertThat(entity.getCreationDate()).isEqualTo(channelConfig.getCreationDate());
        assertThat(entity.getLastUpdateDate()).isEqualTo(channelConfig.getLastUpdateDate());
        assertThat(entity.getUsersAllowedToRead()).hasSameElementsAs(channelConfig.getUsersAllowedToRead());
        assertThat(entity.getVisibility()).isEqualTo(channelConfig.getVisibility().name());
        assertThat(entity.getDiscordChannelId()).isEqualTo(channelConfig.getDiscordChannelId());
        assertThat(entity.getWorldId()).isEqualTo(channelConfig.getWorldId());
        assertThat(entity.getPersonaId()).isEqualTo(channelConfig.getPersonaId());
    }

    @Test
    public void mapChannelConfigPersistenceToDomain_whenPersistenceEntityProvided_thenChannelConfigIsCreated() {

        // Given
        String creatorDiscordId = "CRTRID";
        ChannelConfigEntity channelConfig = ChannelConfigEntityFixture.sample()
                .creatorDiscordId(creatorDiscordId)
                .build();

        // When
        ChannelConfig entity = mapper.mapFromEntity(channelConfig);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo(channelConfig.getName());
        assertThat(entity.getOwnerDiscordId()).isEqualTo(channelConfig.getOwnerDiscordId());
        assertThat(entity.getCreatorDiscordId()).isEqualTo(channelConfig.getCreatorDiscordId());
        assertThat(entity.getCreationDate()).isEqualTo(channelConfig.getCreationDate());
        assertThat(entity.getLastUpdateDate()).isEqualTo(channelConfig.getLastUpdateDate());
        assertThat(entity.getUsersAllowedToRead()).hasSameElementsAs(channelConfig.getUsersAllowedToRead());
        assertThat(entity.getUsersAllowedToWrite()).hasSameElementsAs(channelConfig.getUsersAllowedToWrite());
    }

    @Test
    public void mapChannelConfigDomain_whenGetOperation_thenMapToGetResult() {

        // Given
        ChannelConfigEntity channelConfig = ChannelConfigEntityFixture.sample().build();

        // When
        GetChannelConfigResult result = mapper.mapToResult(channelConfig);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(channelConfig.getId());
        assertThat(result.getName()).isEqualTo(channelConfig.getName());
        assertThat(result.getVisibility()).isEqualTo(channelConfig.getVisibility());
        assertThat(result.getUsersAllowedToRead()).hasSameElementsAs(channelConfig.getUsersAllowedToRead());
        assertThat(result.getUsersAllowedToWrite()).hasSameElementsAs(channelConfig.getUsersAllowedToWrite());
        assertThat(result.getCreationDate()).isEqualTo(channelConfig.getCreationDate());
        assertThat(result.getLastUpdateDate()).isEqualTo(channelConfig.getLastUpdateDate());
        assertThat(result.getOwnerDiscordId()).isEqualTo(channelConfig.getOwnerDiscordId());
    }

    @Test
    public void mapChannelConfigDomain_whenSearchChannelConfig_thenMapToServer() {

        // Given
        List<ChannelConfigEntity> channelConfigs = IntStream.range(0, 20)
                .mapToObj(op -> ChannelConfigEntityFixture.sample()
                        .id(String.valueOf(op + 1))
                        .build())
                .collect(Collectors.toList());

        Pageable pageable = Pageable.ofSize(10);
        Page<ChannelConfigEntity> page = new PageImpl<>(channelConfigs, pageable, 20);

        // When
        SearchChannelConfigsResult result = mapper.mapToResult(page);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPage()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getItems()).isEqualTo(20);
    }
}
