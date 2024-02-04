package es.thalesalv.chatrpg.core.domain.world;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import es.thalesalv.chatrpg.common.exception.BusinessRuleViolationException;
import es.thalesalv.chatrpg.core.application.command.channelconfig.CreateLorebookEntryFixture;
import es.thalesalv.chatrpg.core.application.command.world.CreateWorld;
import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.PermissionsFixture;
import es.thalesalv.chatrpg.core.domain.Visibility;
import es.thalesalv.chatrpg.core.domain.port.TokenizerPort;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
public class WorldDomainServiceImplTest {

    @Mock
    private WorldRepository repository;

    @Mock
    private TokenizerPort tokenizerPort;

    @InjectMocks
    private WorldDomainServiceImpl service;

    @Test
    public void createWorldSuccessfully() {

        // Given
        String name = "Eldrida";
        String description = "Eldrida is a fantasy world";
        String adventureStart = "You have arrived at the world of Eldrida.";
        Permissions permissions = PermissionsFixture.samplePermissions().build();
        String visibility = "PRIVATE";

        World expectedWorld = WorldFixture.publicWorld()
                .name(name)
                .description(description)
                .adventureStart(adventureStart)
                .visibility(Visibility.fromString(visibility))
                .permissions(permissions)
                .lorebook(Collections.singletonList(LorebookEntryFixture.sampleLorebookEntry().build()))
                .build();

        CreateWorld command = CreateWorld.builder()
                .name(name)
                .adventureStart(adventureStart)
                .description(description)
                .visibility(visibility)
                .creatorDiscordId(permissions.getOwnerDiscordId())
                .readerUsers(permissions.getUsersAllowedToRead())
                .writerUsers(permissions.getUsersAllowedToWrite())
                .lorebookEntries(Collections.singletonList(CreateLorebookEntryFixture.sampleLorebookEntry().build()))
                .build();

        when(repository.save(any(World.class))).thenReturn(expectedWorld);

        // When
        World createdWorld = service.createFrom(command);

        // Then
        assertThat(createdWorld).isNotNull().isEqualTo(expectedWorld);
        assertThat(createdWorld.getName()).isEqualTo(expectedWorld.getName());
        assertThat(createdWorld.getOwnerDiscordId()).isEqualTo(expectedWorld.getOwnerDiscordId());
        assertThat(createdWorld.getWriterUsers()).isEqualTo(expectedWorld.getWriterUsers());
        assertThat(createdWorld.getReaderUsers()).isEqualTo(expectedWorld.getReaderUsers());
        assertThat(createdWorld.getDescription()).isEqualTo(expectedWorld.getDescription());
        assertThat(createdWorld.getAdventureStart()).isEqualTo(expectedWorld.getAdventureStart());
        assertThat(createdWorld.getVisibility()).isEqualTo(expectedWorld.getVisibility());
    }

    @Test
    public void createWorldWithNullLorebookSuccessfully() {

        // Given
        String name = "Eldrida";
        String description = "Eldrida is a fantasy world";
        String adventureStart = "You have arrived at the world of Eldrida.";
        Permissions permissions = PermissionsFixture.samplePermissions().build();
        String visibility = "PRIVATE";

        World expectedWorld = WorldFixture.publicWorld()
                .name(name)
                .description(description)
                .adventureStart(adventureStart)
                .visibility(Visibility.fromString(visibility))
                .permissions(permissions)
                .build();

        CreateWorld command = CreateWorld.builder()
                .name(name)
                .adventureStart(adventureStart)
                .description(description)
                .visibility(visibility)
                .creatorDiscordId(permissions.getOwnerDiscordId())
                .readerUsers(permissions.getUsersAllowedToRead())
                .writerUsers(permissions.getUsersAllowedToWrite())
                .build();

        when(repository.save(any(World.class))).thenReturn(expectedWorld);

        // When
        World createdWorld = service.createFrom(command);

        // Then
        assertThat(createdWorld).isNotNull().isEqualTo(expectedWorld);
        assertThat(createdWorld.getName()).isEqualTo(expectedWorld.getName());
        assertThat(createdWorld.getOwnerDiscordId()).isEqualTo(expectedWorld.getOwnerDiscordId());
        assertThat(createdWorld.getWriterUsers()).isEqualTo(expectedWorld.getWriterUsers());
        assertThat(createdWorld.getReaderUsers()).isEqualTo(expectedWorld.getReaderUsers());
        assertThat(createdWorld.getDescription()).isEqualTo(expectedWorld.getDescription());
        assertThat(createdWorld.getAdventureStart()).isEqualTo(expectedWorld.getAdventureStart());
        assertThat(createdWorld.getVisibility()).isEqualTo(expectedWorld.getVisibility());
    }

    @Test
    public void errorWhenInitialPromptTokenLimitIsSurpassed() {

        // Given
        String name = "Eldrida";
        String description = "Eldrida is a fantasy world";
        String adventureStart = "You have arrived at the world of Eldrida.";
        Permissions permissions = PermissionsFixture.samplePermissions().build();
        String visibility = "PRIVATE";

        CreateWorld command = CreateWorld.builder()
                .name(name)
                .adventureStart(adventureStart)
                .description(description)
                .visibility(visibility)
                .creatorDiscordId(permissions.getOwnerDiscordId())
                .readerUsers(permissions.getUsersAllowedToRead())
                .writerUsers(permissions.getUsersAllowedToWrite())
                .build();

        ReflectionTestUtils.setField(service, "adventureStartTokenLimit", 2);
        when(tokenizerPort.getTokenCountFrom(anyString())).thenReturn(10);

        // Then
        assertThrows(BusinessRuleViolationException.class, () -> service.createFrom(command));
    }
}
