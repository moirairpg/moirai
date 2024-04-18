package es.thalesalv.chatrpg.core.domain.world;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import es.thalesalv.chatrpg.common.exception.AssetAccessDeniedException;
import es.thalesalv.chatrpg.common.exception.AssetNotFoundException;
import es.thalesalv.chatrpg.common.exception.BusinessRuleViolationException;
import es.thalesalv.chatrpg.core.application.command.channelconfig.CreateLorebookEntryFixture;
import es.thalesalv.chatrpg.core.application.command.world.CreateWorld;
import es.thalesalv.chatrpg.core.application.command.world.CreateWorldLorebookEntry;
import es.thalesalv.chatrpg.core.application.command.world.DeleteWorld;
import es.thalesalv.chatrpg.core.application.command.world.DeleteWorldLorebookEntry;
import es.thalesalv.chatrpg.core.application.command.world.UpdateWorld;
import es.thalesalv.chatrpg.core.application.command.world.UpdateWorldLorebookEntry;
import es.thalesalv.chatrpg.core.application.query.world.GetWorldById;
import es.thalesalv.chatrpg.core.application.query.world.GetWorldLorebookEntryById;
import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.PermissionsFixture;
import es.thalesalv.chatrpg.core.domain.Visibility;
import es.thalesalv.chatrpg.core.domain.port.TokenizerPort;

@ExtendWith(MockitoExtension.class)
public class WorldServiceImplTest {

    @Mock
    private WorldLorebookEntryRepository lorebookEntryRepository;

    @Mock
    private WorldRepository worldRepository;

    @Mock
    private TokenizerPort tokenizerPort;

    @InjectMocks
    private WorldServiceImpl service;

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
                .lorebook(Collections.singletonList(WorldLorebookEntryFixture.sampleLorebookEntry().build()))
                .build();

        CreateWorld command = CreateWorld.builder()
                .name(name)
                .adventureStart(adventureStart)
                .description(description)
                .visibility(visibility)
                .requesterDiscordId(permissions.getOwnerDiscordId())
                .usersAllowedToRead(permissions.getUsersAllowedToRead())
                .usersAllowedToWrite(permissions.getUsersAllowedToWrite())
                .lorebookEntries(Collections.singletonList(CreateLorebookEntryFixture.sampleLorebookEntry().build()))
                .build();

        when(worldRepository.save(any(World.class))).thenReturn(expectedWorld);

        // When
        World createdWorld = service.createFrom(command);

        // Then
        assertThat(createdWorld).isNotNull().isEqualTo(expectedWorld);
        assertThat(createdWorld.getName()).isEqualTo(expectedWorld.getName());
        assertThat(createdWorld.getOwnerDiscordId()).isEqualTo(expectedWorld.getOwnerDiscordId());
        assertThat(createdWorld.getUsersAllowedToWrite()).isEqualTo(expectedWorld.getUsersAllowedToWrite());
        assertThat(createdWorld.getUsersAllowedToRead()).isEqualTo(expectedWorld.getUsersAllowedToRead());
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
                .requesterDiscordId(permissions.getOwnerDiscordId())
                .usersAllowedToRead(permissions.getUsersAllowedToRead())
                .usersAllowedToWrite(permissions.getUsersAllowedToWrite())
                .build();

        when(worldRepository.save(any(World.class))).thenReturn(expectedWorld);

        // When
        World createdWorld = service.createFrom(command);

        // Then
        assertThat(createdWorld).isNotNull().isEqualTo(expectedWorld);
        assertThat(createdWorld.getName()).isEqualTo(expectedWorld.getName());
        assertThat(createdWorld.getOwnerDiscordId()).isEqualTo(expectedWorld.getOwnerDiscordId());
        assertThat(createdWorld.getUsersAllowedToWrite()).isEqualTo(expectedWorld.getUsersAllowedToWrite());
        assertThat(createdWorld.getUsersAllowedToRead()).isEqualTo(expectedWorld.getUsersAllowedToRead());
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
                .requesterDiscordId(permissions.getOwnerDiscordId())
                .usersAllowedToRead(permissions.getUsersAllowedToRead())
                .usersAllowedToWrite(permissions.getUsersAllowedToWrite())
                .build();

        ReflectionTestUtils.setField(service, "adventureStartTokenLimit", 2);
        when(tokenizerPort.getTokenCountFrom(anyString())).thenReturn(10);

        // Then
        assertThrows(BusinessRuleViolationException.class, () -> service.createFrom(command));
    }

    @Test
    public void findWorldById() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        GetWorldById query = GetWorldById.build(id, requesterId);

        World world = WorldFixture.privateWorld()
                .id(id)
                .name("New name")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        when(worldRepository.findById(anyString())).thenReturn(Optional.of(world));

        // When
        World result = service.getWorldById(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(world.getName());
    }

    @Test
    public void errorWhenFindWorldNotFound() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        GetWorldById query = GetWorldById.build(id, requesterId);

        when(worldRepository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> service.getWorldById(query));
    }

    @Test
    public void errorWhenFindWorldAccessDenied() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        GetWorldById query = GetWorldById.build(id, requesterId);

        World world = WorldFixture.privateWorld()
                .id(id)
                .name("New name")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId("ANTHRUSR")
                        .usersAllowedToRead(Collections.emptyList())
                        .build())
                .build();

        when(worldRepository.findById(anyString())).thenReturn(Optional.of(world));

        // Then
        assertThrows(AssetAccessDeniedException.class, () -> service.getWorldById(query));
    }

    @Test
    public void errorWhenDeleteWorldNotFound() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        DeleteWorld command = DeleteWorld.build(id, requesterId);

        when(worldRepository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> service.deleteWorld(command));
    }

    @Test
    public void errorWhenDeleteWorldAccessDenied() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        DeleteWorld command = DeleteWorld.build(id, requesterId);

        World world = WorldFixture.privateWorld()
                .id(id)
                .name("New name")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId("ANTHRUSR")
                        .build())
                .build();

        when(worldRepository.findById(anyString())).thenReturn(Optional.of(world));

        // Then
        assertThrows(AssetAccessDeniedException.class, () -> service.deleteWorld(command));
    }

    @Test
    public void deleteWorld() {

        // Given
        String id = "CHCONFID";
        String requesterId = "RQSTRID";
        DeleteWorld command = DeleteWorld.build(id, requesterId);

        World world = WorldFixture.privateWorld()
                .id(id)
                .name("New name")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        when(worldRepository.findById(anyString())).thenReturn(Optional.of(world));

        // Then
        service.deleteWorld(command);
    }

    @Test
    public void errorWhenUpdateWorldNotFound() {

        // Given
        String id = "CHCONFID";

        UpdateWorld command = UpdateWorld.builder()
                .id(id)
                .build();

        when(worldRepository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> service.update(command));
    }

    @Test
    public void errorWhenUpdateWorldAccessDenied() {

        // Given
        String id = "CHCONFID";

        UpdateWorld command = UpdateWorld.builder()
                .id(id)
                .requesterDiscordId("USRID")
                .build();

        World world = WorldFixture.privateWorld()
                .id(id)
                .name("New name")
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId("ANTHRUSR")
                        .build())
                .build();

        when(worldRepository.findById(anyString())).thenReturn(Optional.of(world));

        // Then
        assertThrows(AssetAccessDeniedException.class, () -> service.update(command));
    }

    @Test
    public void updateWorld() {

        // Given
        String id = "CHCONFID";

        UpdateWorld command = UpdateWorld.builder()
                .id(id)
                .name("ChatRPG")
                .description("This is an RPG world")
                .adventureStart("As you enter the city, people around you start looking at you.")
                .visibility("PUBLIC")
                .requesterDiscordId("586678721356875")
                .build();

        World unchangedWorld = WorldFixture.privateWorld().build();

        World expectedUpdatedWorld = WorldFixture.privateWorld()
                .id(id)
                .name("ChatRPG")
                .description("This is an RPG world")
                .adventureStart("As you enter the city, people around you start looking at you.")
                .visibility(Visibility.PUBLIC)
                .build();

        when(worldRepository.findById(anyString())).thenReturn(Optional.of(unchangedWorld));
        when(worldRepository.save(any(World.class))).thenReturn(expectedUpdatedWorld);

        // When
        World result = service.update(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(expectedUpdatedWorld.getName());
    }

    @Test
    public void errorWhenUnauthorizedUserUpdateWorld() {

        // Given
        String id = "CHCONFID";

        UpdateWorld command = UpdateWorld.builder()
                .id(id)
                .name("ChatRPG")
                .description("This is an RPG world")
                .adventureStart("As you enter the city, people around you start looking at you.")
                .visibility("PUBLIC")
                .requesterDiscordId("INVLDUSR")
                .build();

        World unchangedWorld = WorldFixture.privateWorld().build();

        when(worldRepository.findById(anyString())).thenReturn(Optional.of(unchangedWorld));

        // Then
        assertThrows(AssetAccessDeniedException.class, () -> service.update(command));
    }

    @Test
    public void createLorebookEntrySuccesfully() {

        // Given
        String name = "Eldrida";
        String description = "Eldrida is a kingdom in an empire";
        String regex = "[Ee]ldrida";
        String worldId = "WRLDID";

        WorldLorebookEntry.Builder lorebookEntryBuilder = WorldLorebookEntryFixture.sampleLorebookEntry()
                .name(name)
                .description(description)
                .regex(regex);

        WorldLorebookEntry expectedLorebookEntry = lorebookEntryBuilder.build();

        CreateWorldLorebookEntry command = CreateWorldLorebookEntry.builder()
                .name(name)
                .description(description)
                .regex(regex)
                .worldId(worldId)
                .requesterDiscordId("586678721356875")
                .build();

        World world = WorldFixture.privateWorld().build();

        when(worldRepository.findById(anyString())).thenReturn(Optional.of(world));
        when(lorebookEntryRepository.save(any(WorldLorebookEntry.class))).thenReturn(expectedLorebookEntry);

        // When
        WorldLorebookEntry createdLorebookEntry = service.createLorebookEntry(command);

        // Then
        assertThat(createdLorebookEntry.getName()).isEqualTo(expectedLorebookEntry.getName());
        assertThat(createdLorebookEntry.getDescription()).isEqualTo(expectedLorebookEntry.getDescription());
        assertThat(createdLorebookEntry.getRegex()).isEqualTo(expectedLorebookEntry.getRegex());
    }

    @Test
    public void errorWhenUnauthorizedUserCreateLorebookEntry() {

        // Given
        String name = "Eldrida";
        String description = "Eldrida is a kingdom in an empire";
        String regex = "[Ee]ldrida";
        String worldId = "WRLDID";

        CreateWorldLorebookEntry command = CreateWorldLorebookEntry.builder()
                .name(name)
                .description(description)
                .regex(regex)
                .worldId(worldId)
                .requesterDiscordId("INVLDUSR")
                .build();

        World world = WorldFixture.privateWorld().build();

        when(worldRepository.findById(anyString())).thenReturn(Optional.of(world));

        // Then
        assertThrows(AssetAccessDeniedException.class, () -> service.createLorebookEntry(command));
    }

    @Test
    public void updateLorebookEntrySuccesfully() {

        // Given
        String name = "Eldrida";
        String description = "Eldrida is a kingdom in an empire";
        String regex = "[Ee]ldrida";
        String worldId = "WRLDID";

        WorldLorebookEntry.Builder lorebookEntryBuilder = WorldLorebookEntryFixture.sampleLorebookEntry()
                .name(name)
                .description(description)
                .regex(regex);

        WorldLorebookEntry expectedLorebookEntry = lorebookEntryBuilder.build();

        UpdateWorldLorebookEntry command = UpdateWorldLorebookEntry.builder()
                .id("ENTRID")
                .name(name)
                .description(description)
                .regex(regex)
                .worldId(worldId)
                .requesterDiscordId("586678721356875")
                .build();

        World world = WorldFixture.privateWorld().build();

        when(worldRepository.findById(anyString())).thenReturn(Optional.of(world));
        when(lorebookEntryRepository.findById(anyString())).thenReturn(Optional.of(expectedLorebookEntry));
        when(lorebookEntryRepository.save(any(WorldLorebookEntry.class))).thenReturn(expectedLorebookEntry);

        // When
        WorldLorebookEntry createdLorebookEntry = service.updateLorebookEntry(command);

        // Then
        assertThat(createdLorebookEntry.getName()).isEqualTo(expectedLorebookEntry.getName());
        assertThat(createdLorebookEntry.getDescription()).isEqualTo(expectedLorebookEntry.getDescription());
        assertThat(createdLorebookEntry.getRegex()).isEqualTo(expectedLorebookEntry.getRegex());
    }

    @Test
    public void errorWhenUpdatingLorebookEntryIfWorldNotExists() {

        // Given
        String name = "Eldrida";
        String description = "Eldrida is a kingdom in an empire";
        String regex = "[Ee]ldrida";
        String worldId = "WRLDID";

        UpdateWorldLorebookEntry command = UpdateWorldLorebookEntry.builder()
                .id("ENTRID")
                .name(name)
                .description(description)
                .regex(regex)
                .worldId(worldId)
                .requesterDiscordId("586678721356875")
                .build();

        World world = WorldFixture.privateWorld().build();

        when(worldRepository.findById(anyString())).thenReturn(Optional.of(world));
        when(lorebookEntryRepository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> service.updateLorebookEntry(command));
    }

    @Test
    public void errorWhenUnauthorizedUserUpdatingLorebookEntry() {

        // Given
        String name = "Eldrida";
        String description = "Eldrida is a kingdom in an empire";
        String regex = "[Ee]ldrida";
        String worldId = "WRLDID";

        UpdateWorldLorebookEntry command = UpdateWorldLorebookEntry.builder()
                .id("ENTRID")
                .name(name)
                .description(description)
                .regex(regex)
                .worldId(worldId)
                .requesterDiscordId("INVLDUSR")
                .build();

        World world = WorldFixture.privateWorld().build();

        when(worldRepository.findById(anyString())).thenReturn(Optional.of(world));

        // Then
        assertThrows(AssetAccessDeniedException.class, () -> service.updateLorebookEntry(command));
    }

    @Test
    public void errorWhenUpdatingLorebookEntryIfEntryNotExists() {

        // Given
        String name = "Eldrida";
        String description = "Eldrida is a kingdom in an empire";
        String regex = "[Ee]ldrida";
        String worldId = "WRLDID";

        UpdateWorldLorebookEntry command = UpdateWorldLorebookEntry.builder()
                .id("ENTRID")
                .name(name)
                .description(description)
                .regex(regex)
                .worldId(worldId)
                .build();

        when(worldRepository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> service.updateLorebookEntry(command));
    }

    @Test
    public void errorWhenEntryNameTokenLimitIsSurpassed() {

        // Given
        String name = "Eldrida";
        String description = "Eldrida is a kingdom in an empire";
        String regex = "[Ee]ldrida";
        String worldId = "WRLDID";

        CreateWorldLorebookEntry command = CreateWorldLorebookEntry.builder()
                .name(name)
                .description(description)
                .regex(regex)
                .worldId(worldId)
                .requesterDiscordId("586678721356875")
                .build();

        World world = WorldFixture.privateWorld().build();
        when(worldRepository.findById(anyString())).thenReturn(Optional.of(world));

        ReflectionTestUtils.setField(service, "lorebookEntryNameTokenLimit", 2);
        ReflectionTestUtils.setField(service, "lorebookEntryDescriptionTokenLimit", 20);
        when(tokenizerPort.getTokenCountFrom(anyString())).thenReturn(10);

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> service.createLorebookEntry(command));
    }

    @Test
    public void errorWhenEntryDescriptionTokenLimitIsSurpassed() {

        // Given
        String name = "Eldrida";
        String description = "Eldrida is a kingdom in an empire";
        String regex = "[Ee]ldrida";
        String worldId = "WRLDID";

        CreateWorldLorebookEntry command = CreateWorldLorebookEntry.builder()
                .name(name)
                .description(description)
                .regex(regex)
                .worldId(worldId)
                .requesterDiscordId("586678721356875")
                .build();

        World world = WorldFixture.privateWorld().build();
        when(worldRepository.findById(anyString())).thenReturn(Optional.of(world));

        ReflectionTestUtils.setField(service, "lorebookEntryNameTokenLimit", 20);
        ReflectionTestUtils.setField(service, "lorebookEntryDescriptionTokenLimit", 2);
        when(tokenizerPort.getTokenCountFrom(anyString())).thenReturn(10);

        // Then
        assertThrows(BusinessRuleViolationException.class,
                () -> service.createLorebookEntry(command));
    }

    @Test
    public void errorWhenCreatingLorebookEntryInNonExistingWorld() {

        // Given
        String name = "Eldrida";
        String description = "Eldrida is a kingdom in an empire";
        String regex = "[Ee]ldrida";
        String worldId = "WRLDID";

        CreateWorldLorebookEntry command = CreateWorldLorebookEntry.builder()
                .name(name)
                .description(description)
                .regex(regex)
                .worldId(worldId)
                .build();

        when(worldRepository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class,
                () -> service.createLorebookEntry(command));
    }

    @Test
    public void findLorebookEntryById() {

        // Given
        String requesterId = "RQSTRID";
        GetWorldLorebookEntryById query = GetWorldLorebookEntryById.builder()
                .requesterDiscordId(requesterId)
                .worldId("WRLDID")
                .entryId("ENTRID")
                .build();

        WorldLorebookEntry entry = WorldLorebookEntryFixture.sampleLorebookEntry().build();

        World world = WorldFixture.privateWorld()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        when(worldRepository.findById(anyString())).thenReturn(Optional.of(world));
        when(lorebookEntryRepository.findById(anyString())).thenReturn(Optional.of(entry));

        // When
        WorldLorebookEntry retrievedEntry = service.findWorldLorebookEntryById(query);

        // Then
        assertThat(retrievedEntry).isNotNull();
    }

    @Test
    public void errorWhenFindLorebookEntryByIdWorldNotFound() {

        // Given
        String requesterId = "RQSTRID";
        GetWorldLorebookEntryById query = GetWorldLorebookEntryById.builder()
                .requesterDiscordId(requesterId)
                .worldId("WRLDID")
                .entryId("ENTRID")
                .build();

        when(worldRepository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> service.findWorldLorebookEntryById(query));
    }

    @Test
    public void errorWhenFindLorebookEntryByIdWorldInvalidPermission() {

        // Given
        String requesterId = "RQSTRID";
        GetWorldLorebookEntryById query = GetWorldLorebookEntryById.builder()
                .requesterDiscordId(requesterId)
                .worldId("WRLDID")
                .entryId("ENTRID")
                .build();

        World world = WorldFixture.privateWorld()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId("INVLDID")
                        .build())
                .build();

        when(worldRepository.findById(anyString())).thenReturn(Optional.of(world));

        // Then
        assertThrows(AssetAccessDeniedException.class, () -> service.findWorldLorebookEntryById(query));
    }

    @Test
    public void errorWhenFindLorebookEntryByIdEntryNotFound() {

        // Given
        String requesterId = "RQSTRID";
        GetWorldLorebookEntryById query = GetWorldLorebookEntryById.builder()
                .requesterDiscordId(requesterId)
                .worldId("WRLDID")
                .entryId("ENTRID")
                .build();

        World world = WorldFixture.privateWorld()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        when(worldRepository.findById(anyString())).thenReturn(Optional.of(world));
        when(lorebookEntryRepository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> service.findWorldLorebookEntryById(query));
    }

    @Test
    public void deleteLorebookEntryById() {

        // Given
        String requesterId = "RQSTRID";
        DeleteWorldLorebookEntry query = DeleteWorldLorebookEntry.builder()
                .requesterDiscordId(requesterId)
                .worldId("WRLDID")
                .lorebookEntryId("ENTRID")
                .build();

        WorldLorebookEntry entry = WorldLorebookEntryFixture.sampleLorebookEntry().build();

        World world = WorldFixture.privateWorld()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        when(worldRepository.findById(anyString())).thenReturn(Optional.of(world));
        when(lorebookEntryRepository.findById(anyString())).thenReturn(Optional.of(entry));

        // Then
        service.deleteLorebookEntry(query);
    }

    @Test
    public void errorWhenDeleteLorebookEntryByIdWorldNotFound() {

        // Given
        String requesterId = "RQSTRID";
        DeleteWorldLorebookEntry query = DeleteWorldLorebookEntry.builder()
                .requesterDiscordId(requesterId)
                .worldId("WRLDID")
                .lorebookEntryId("ENTRID")
                .build();

        when(worldRepository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> service.deleteLorebookEntry(query));
    }

    @Test
    public void errorWhenDeleteLorebookEntryByIdWorldInvalidPermission() {

        // Given
        String requesterId = "RQSTRID";
        DeleteWorldLorebookEntry query = DeleteWorldLorebookEntry.builder()
                .requesterDiscordId(requesterId)
                .worldId("WRLDID")
                .lorebookEntryId("ENTRID")
                .build();

        World world = WorldFixture.privateWorld()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId("INVLDID")
                        .build())
                .build();

        when(worldRepository.findById(anyString())).thenReturn(Optional.of(world));

        // Then
        assertThrows(AssetAccessDeniedException.class, () -> service.deleteLorebookEntry(query));
    }

    @Test
    public void errorWhenDeleteLorebookEntryByIdEntryNotFound() {

        // Given
        String requesterId = "RQSTRID";
        DeleteWorldLorebookEntry query = DeleteWorldLorebookEntry.builder()
                .requesterDiscordId(requesterId)
                .worldId("WRLDID")
                .lorebookEntryId("ENTRID")
                .build();

        World world = WorldFixture.privateWorld()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        when(worldRepository.findById(anyString())).thenReturn(Optional.of(world));
        when(lorebookEntryRepository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> service.deleteLorebookEntry(query));
    }

    @Test
    public void findAllEntriesByRegex_WhenWorldNotFound_ShouldThrowAssetNotFoundException() {

        // Given
        String worldId = "worldId";
        String requesterId = "RQSTRID";
        String valueToSearch = "Armando";

        when(worldRepository.findById(worldId)).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class,
                () -> service.findAllEntriesByRegex(requesterId, worldId, valueToSearch));
    }

    @Test
    public void findAllEntriesByRegex_WhenUserCannotRead_ShouldThrowAssetAccessDeniedException() {

        // Given
        String worldId = "worldId";
        String requesterId = "RQSTRID";
        String valueToSearch = "Armando";
        World world = WorldFixture.privateWorld()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId("ANTRUSR")
                        .build())
                .build();

        when(worldRepository.findById(worldId)).thenReturn(Optional.of(world));

        // Then
        assertThrows(AssetAccessDeniedException.class,
                () -> service.findAllEntriesByRegex(requesterId, worldId, valueToSearch));
    }

    @Test
    public void findAllEntriesByRegex_WhenUserCanRead_ShouldReturnEntries() {

        // Given
        String worldId = "worldId";
        String requesterId = "RQSTRID";
        String valueToSearch = "Armando";
        World world = WorldFixture.privateWorld()
                .permissions(PermissionsFixture.samplePermissions()
                        .ownerDiscordId(requesterId)
                        .build())
                .build();

        List<WorldLorebookEntry> expectedEntries = Collections
                .singletonList(WorldLorebookEntryFixture.sampleLorebookEntry()
                        .regex("[Aa]rmando")
                        .build());

        when(worldRepository.findById(worldId)).thenReturn(Optional.of(world));
        when(lorebookEntryRepository.findAllEntriesByRegex(valueToSearch)).thenReturn(expectedEntries);

        // When
        List<WorldLorebookEntry> result = service.findAllEntriesByRegex(requesterId, worldId, valueToSearch);

        // Then
        assertThat(result).isNotNull().isNotEmpty().hasSize(1);
        assertThat(result).isEqualTo(expectedEntries);
    }
}
