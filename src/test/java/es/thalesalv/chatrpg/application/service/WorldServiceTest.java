package es.thalesalv.chatrpg.application.service;

import static es.thalesalv.chatrpg.testutils.WorldTestUtils.buildSimpleLorebookEntry;
import static es.thalesalv.chatrpg.testutils.WorldTestUtils.buildSimpleLorebookEntryEntity;
import static es.thalesalv.chatrpg.testutils.WorldTestUtils.buildSimplePublicWorld;
import static es.thalesalv.chatrpg.testutils.WorldTestUtils.buildSimplePublicWorldEntity;
import static es.thalesalv.chatrpg.testutils.WorldTestUtils.buildSimplePublicWorldEntityList;
import static es.thalesalv.chatrpg.testutils.WorldTestUtils.buildSimplePublicWorldList;
import static es.thalesalv.chatrpg.testutils.WorldTestUtils.hasReadPermissions;
import static es.thalesalv.chatrpg.testutils.WorldTestUtils.hasWritePermissions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryEntity;
import es.thalesalv.chatrpg.adapters.data.entity.WorldEntity;
import es.thalesalv.chatrpg.adapters.data.repository.LorebookEntryRepository;
import es.thalesalv.chatrpg.adapters.data.repository.WorldRepository;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntryDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntryEntityToDTO;
import es.thalesalv.chatrpg.application.mapper.world.WorldDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.world.WorldEntityToDTO;
import es.thalesalv.chatrpg.domain.exception.InsufficientPermissionException;
import es.thalesalv.chatrpg.domain.exception.LorebookEntryNotFoundException;
import es.thalesalv.chatrpg.domain.exception.WorldNotFoundException;
import es.thalesalv.chatrpg.domain.model.bot.LorebookEntry;
import es.thalesalv.chatrpg.domain.model.bot.World;

@ExtendWith(MockitoExtension.class)
public class WorldServiceTest {

    @Mock
    private WorldRepository worldRepository;

    @Mock
    private LorebookEntryRepository lorebookEntryRepository;

    private LorebookEntryEntityToDTO lorebookEntryEntityToDTO;
    private LorebookEntryDTOToEntity lorebookEntryDTOToEntity;
    private WorldDTOToEntity worldDTOToEntity;
    private WorldEntityToDTO worldEntityToDTO;
    private WorldService worldService;

    private static final String NANO_ID = "241OZASGM6CESV7";

    @BeforeEach
    public void beforeEach() {

        lorebookEntryDTOToEntity = new LorebookEntryDTOToEntity();
        lorebookEntryEntityToDTO = new LorebookEntryEntityToDTO();
        worldDTOToEntity = new WorldDTOToEntity(lorebookEntryDTOToEntity);
        worldEntityToDTO = new WorldEntityToDTO(lorebookEntryEntityToDTO);
        worldService = new WorldService(worldDTOToEntity, worldEntityToDTO, lorebookEntryDTOToEntity,
                lorebookEntryEntityToDTO, worldRepository, lorebookEntryRepository);
    }

    @Test
    public void insertWorldTest() {

        final World world = buildSimplePublicWorld();
        final WorldEntity entity = buildSimplePublicWorldEntity();
        final LorebookEntryEntity entryEntity = buildSimpleLorebookEntryEntity();

        when(worldRepository.save(any(WorldEntity.class))).thenReturn(entity);
        when(lorebookEntryRepository.save(any(LorebookEntryEntity.class))).thenReturn(entryEntity);

        final World savedWorld = worldService.saveWorld(world);
        assertEquals("Test world", savedWorld.getName());
        assertEquals("This is a test world", savedWorld.getDescription());
    }

    @Test
    public void retrieveAllWorldsTest() {

        final String userId = "302796314822049793";
        final List<World> completeList = buildSimplePublicWorldList();

        when(worldRepository.findAll()).thenReturn(buildSimplePublicWorldEntityList());

        final List<World> filteredList = worldService.retrieveAllWorlds(userId);
        assertEquals(8, filteredList.size());
        assertEquals(10, completeList.size());

        filteredList.forEach(p -> {
            assertTrue(hasReadPermissions(p, userId));
        });
    }

    @Test
    public void updateWorldTest_shouldWork() {

        final String userId = "302796314822049793";
        final World world = buildSimplePublicWorld();
        final WorldEntity entity = buildSimplePublicWorldEntity();
        final LorebookEntryEntity entryEntity = buildSimpleLorebookEntryEntity();

        world.setOwner(userId);
        entity.setOwner(userId);

        when(worldRepository.findById(NANO_ID)).thenReturn(Optional.of(entity));
        when(worldRepository.save(any(WorldEntity.class))).thenReturn(entity);
        when(lorebookEntryRepository.save(any(LorebookEntryEntity.class))).thenReturn(entryEntity);

        final World result = worldService.updateWorld(NANO_ID, world, userId);
        assertEquals(world, result);
    }

    @Test
    public void updateWorldTest_insufficientPermissions() {

        final String userId = "302796314822049793";
        final World world = buildSimplePublicWorld();
        final WorldEntity entity = buildSimplePublicWorldEntity();

        when(worldRepository.findById(NANO_ID)).thenReturn(Optional.of(entity));

        final InsufficientPermissionException thrown = assertThrows(InsufficientPermissionException.class,
                () -> worldService.updateWorld(NANO_ID, world, userId));

        assertEquals("Not enough permissions to modify this world", thrown.getMessage());
        assertFalse(hasWritePermissions(world, userId));
    }

    @Test
    public void updateWorldTest_notFound() {

        final String userId = "302796314822049793";
        final World world = buildSimplePublicWorld();
        final WorldNotFoundException thrown = assertThrows(WorldNotFoundException.class,
                () -> worldService.updateWorld(NANO_ID, world, userId));

        assertEquals("Error updating world: world with id 241OZASGM6CESV7 could not be found in database.",
                thrown.getMessage());
    }

    @Test
    public void deleteWorldTest_shouldWork() {

        final String userId = "302796314822049793";
        final World world = buildSimplePublicWorld();
        final WorldEntity entity = buildSimplePublicWorldEntity();

        world.setOwner(userId);
        entity.setOwner(userId);

        when(worldRepository.findById(NANO_ID)).thenReturn(Optional.of(entity));
        doNothing().when(worldRepository)
                .delete(entity);

        worldService.deleteWorld(NANO_ID, userId);
        assertTrue(hasWritePermissions(world, userId));
    }

    @Test
    public void deleteWorldTest_insufficientPermissions() {

        final String userId = "302796314822049793";
        final World world = buildSimplePublicWorld();
        final WorldEntity entity = buildSimplePublicWorldEntity();

        when(worldRepository.findById(NANO_ID)).thenReturn(Optional.of(entity));

        final InsufficientPermissionException thrown = assertThrows(InsufficientPermissionException.class,
                () -> worldService.deleteWorld(NANO_ID, userId));

        assertEquals("Not enough permissions to delete this world", thrown.getMessage());
        assertFalse(hasWritePermissions(world, userId));
    }

    @Test
    public void deleteWorldTest_worldNotFound() {

        final String userId = "302796314822049793";
        final World world = buildSimplePublicWorld();

        when(worldRepository.findById(anyString())).thenReturn(Optional.empty());

        final WorldNotFoundException thrown = assertThrows(WorldNotFoundException.class,
                () -> worldService.deleteWorld(NANO_ID, userId));

        assertEquals("Error deleting world: world with id 241OZASGM6CESV7 could not be found in database.",
                thrown.getMessage());

        assertFalse(hasWritePermissions(world, userId));
    }

    @Test
    public void insertLorebookEntryTest() {

        final String userId = "1083867535658725536"; // owner
        final LorebookEntry entry = buildSimpleLorebookEntry();
        final LorebookEntryEntity entryEntity = buildSimpleLorebookEntryEntity();
        final WorldEntity worldEntity = buildSimplePublicWorldEntity();

        when(worldRepository.findById(anyString())).thenReturn(Optional.of(worldEntity));
        when(lorebookEntryRepository.save(any(LorebookEntryEntity.class))).thenReturn(entryEntity);

        final LorebookEntry result = worldService.saveLorebookEntry(entry, NANO_ID, userId);
        assertEquals(entry, result);
    }

    @Test
    public void insertLorebookEntryTest_notEnoughPermissions() {

        final String userId = "302796314822049793"; // not owner or allowed
        final LorebookEntry entry = buildSimpleLorebookEntry();
        final WorldEntity worldEntity = buildSimplePublicWorldEntity();

        when(worldRepository.findById(NANO_ID)).thenReturn(Optional.of(worldEntity));

        final InsufficientPermissionException thrown = assertThrows(InsufficientPermissionException.class,
                () -> worldService.saveLorebookEntry(entry, NANO_ID, userId));

        assertEquals("Not enough permissions to add entries to this lorebook", thrown.getMessage());
    }

    @Test
    public void insertLorebookEntryTest_lorebookNotFound() {

        final String userId = "302796314822049793"; // not owner or allowed
        final LorebookEntry entry = buildSimpleLorebookEntry();

        when(worldRepository.findById(NANO_ID)).thenReturn(Optional.empty());

        final WorldNotFoundException thrown = assertThrows(WorldNotFoundException.class,
                () -> worldService.saveLorebookEntry(entry, NANO_ID, userId));

        assertEquals(
                "Error saving lorebook entry to lorebook: lorebook with id 241OZASGM6CESV7 could not be found in database.",
                thrown.getMessage());
    }

    @Test
    public void updateLorebookEntryTest() {

        final String userId = "1083867535658725536"; // owner
        final LorebookEntry entry = buildSimpleLorebookEntry();
        final LorebookEntryEntity entryEntity = buildSimpleLorebookEntryEntity();
        final WorldEntity worldEntity = buildSimplePublicWorldEntity();

        entryEntity.setWorld(worldEntity);
        when(lorebookEntryRepository.findById(anyString())).thenReturn(Optional.of(entryEntity));

        final LorebookEntry result = worldService.updateLorebookEntry(NANO_ID, entry, userId);
        assertEquals(entry, result);
    }

    @Test
    public void updateLorebookEntryTest_entryNotFound() {

        final String userId = "1083867535658725536"; // owner
        final LorebookEntry entry = buildSimpleLorebookEntry();
        final LorebookEntryEntity entryEntity = buildSimpleLorebookEntryEntity();
        final WorldEntity worldEntity = buildSimplePublicWorldEntity();

        entryEntity.setWorld(worldEntity);

        final LorebookEntryNotFoundException thrown = assertThrows(LorebookEntryNotFoundException.class,
                () -> worldService.updateLorebookEntry(NANO_ID, entry, userId));

        assertEquals(
                "Error updating lorebook entry: lorebook entry with id 241OZASGM6CESV7 could not be found in database.",
                thrown.getMessage());
    }

    @Test
    public void updateLorebookEntryTest_notEnoughPermissions() {

        final String userId = "302796314822049793"; // not owner or allowed
        final LorebookEntry entry = buildSimpleLorebookEntry();
        final LorebookEntryEntity entryEntity = buildSimpleLorebookEntryEntity();
        final WorldEntity worldEntity = buildSimplePublicWorldEntity();

        entryEntity.setWorld(worldEntity);

        when(lorebookEntryRepository.findById(anyString())).thenReturn(Optional.of(entryEntity));

        final InsufficientPermissionException thrown = assertThrows(InsufficientPermissionException.class,
                () -> worldService.updateLorebookEntry(NANO_ID, entry, userId));

        assertEquals("Not enough permissions to modify entries in this lorebook", thrown.getMessage());
    }

    @Test
    public void listAllLorebookEntriesTest() {

        final String userId = "1083867535658725536"; // owner
        final WorldEntity worldEntity = buildSimplePublicWorldEntity();

        when(worldRepository.findById(NANO_ID)).thenReturn(Optional.of(worldEntity));

        final List<LorebookEntry> result = worldService.retrieveAllLorebookEntriesInLorebook(NANO_ID, userId);
        assertEquals(4, result.size());
        assertEquals(worldEntity.getLorebook()
                .size(), result.size());
    }

    @Test
    public void listAllLorebookEntriesTest_notEnoughPermissions() {

        final String userId = "302796314822049793"; // not owner or allowed
        final WorldEntity worldEntity = buildSimplePublicWorldEntity();

        worldEntity.setVisibility("private");

        when(worldRepository.findById(NANO_ID)).thenReturn(Optional.of(worldEntity));

        final InsufficientPermissionException thrown = assertThrows(InsufficientPermissionException.class,
                () -> worldService.retrieveAllLorebookEntriesInLorebook(NANO_ID, userId));

        assertEquals("Not enough permissions to retrieve entries in this lorebook", thrown.getMessage());
    }

    @Test
    public void listAllLorebookEntriesTest_lorebookNotFound() {

        final String userId = "302796314822049793"; // not owner or allowed

        when(worldRepository.findById(NANO_ID)).thenReturn(Optional.empty());

        final WorldNotFoundException thrown = assertThrows(WorldNotFoundException.class,
                () -> worldService.retrieveAllLorebookEntriesInLorebook(NANO_ID, userId));

        assertEquals("The lorebook requested could not be found", thrown.getMessage());
    }

    @Test
    public void retrieveLorebookEntrieByIdTest() {

        final String userId = "1083867535658725536"; // owner

        final LorebookEntryEntity lorebookEntryEntity = buildSimpleLorebookEntryEntity();
        final WorldEntity worldEntity = buildSimplePublicWorldEntity();
        lorebookEntryEntity.setWorld(worldEntity);

        when(lorebookEntryRepository.findById(NANO_ID)).thenReturn(Optional.of(lorebookEntryEntity));

        final LorebookEntry result = worldService.retrieveLorebookEntryById(NANO_ID, userId);
        assertEquals(buildSimpleLorebookEntry(), result);
    }

    @Test
    public void retrieveLorebookEntrieByIdTest_notEnoughPermissions() {

        final String userId = "302796314822049793"; // not owner or allowed

        final LorebookEntryEntity lorebookEntryEntity = buildSimpleLorebookEntryEntity();
        final WorldEntity worldEntity = buildSimplePublicWorldEntity();
        worldEntity.setVisibility("private");
        lorebookEntryEntity.setWorld(worldEntity);

        when(lorebookEntryRepository.findById(NANO_ID)).thenReturn(Optional.of(lorebookEntryEntity));

        final InsufficientPermissionException thrown = assertThrows(InsufficientPermissionException.class,
                () -> worldService.retrieveLorebookEntryById(NANO_ID, userId));

        assertEquals("Not enough permissions to retrieve entries in this lorebook", thrown.getMessage());
    }

    @Test
    public void retrieveLorebookEntrieByIdTest_lorebookNotFound() {

        final String userId = "1083867535658725536"; // owner
        final LorebookEntryEntity lorebookEntryEntity = buildSimpleLorebookEntryEntity();
        final WorldEntity worldEntity = buildSimplePublicWorldEntity();
        lorebookEntryEntity.setWorld(worldEntity);

        when(lorebookEntryRepository.findById(NANO_ID)).thenReturn(Optional.empty());

        final LorebookEntryNotFoundException thrown = assertThrows(LorebookEntryNotFoundException.class,
                () -> worldService.retrieveLorebookEntryById(NANO_ID, userId));

        assertEquals(
                "Error retrieving lorebook entry: lorebook entry with id 241OZASGM6CESV7 could not be found in database.",
                thrown.getMessage());
    }

    @Test
    public void deleteLorebookEntry() {

        final String userId = "1083867535658725536"; // owner
        final LorebookEntryEntity lorebookEntryEntity = buildSimpleLorebookEntryEntity();
        final WorldEntity worldEntity = buildSimplePublicWorldEntity();
        final World world = buildSimplePublicWorld();
        lorebookEntryEntity.setWorld(worldEntity);

        when(lorebookEntryRepository.findById(NANO_ID)).thenReturn(Optional.of(lorebookEntryEntity));

        worldService.deleteLorebookEntry(NANO_ID, userId);
        assertTrue(hasWritePermissions(world, userId));
    }

    @Test
    public void deleteLorebookEntry_insufficientPermissions() {

        final String userId = "302796314822049793"; // not owner or allowed
        final LorebookEntryEntity lorebookEntryEntity = buildSimpleLorebookEntryEntity();
        final WorldEntity worldEntity = buildSimplePublicWorldEntity();
        final World world = buildSimplePublicWorld();
        lorebookEntryEntity.setWorld(worldEntity);

        when(lorebookEntryRepository.findById(NANO_ID)).thenReturn(Optional.of(lorebookEntryEntity));

        final InsufficientPermissionException thrown = assertThrows(InsufficientPermissionException.class,
                () -> worldService.deleteLorebookEntry(NANO_ID, userId));

        assertEquals("Not enough permissions to delete entries in this lorebook", thrown.getMessage());
        assertFalse(hasWritePermissions(world, userId));
    }

    @Test
    public void deleteLorebookEntry_entryNotFound() {

        final String userId = "302796314822049793"; // not owner or allowed
        final LorebookEntryEntity lorebookEntryEntity = buildSimpleLorebookEntryEntity();
        final WorldEntity worldEntity = buildSimplePublicWorldEntity();
        lorebookEntryEntity.setWorld(worldEntity);

        when(lorebookEntryRepository.findById(NANO_ID)).thenReturn(Optional.empty());

        final LorebookEntryNotFoundException thrown = assertThrows(LorebookEntryNotFoundException.class,
                () -> worldService.deleteLorebookEntry(NANO_ID, userId));

        assertEquals("Error deleting entry: lorebook entry with id 241OZASGM6CESV7 could not be found in database.",
                thrown.getMessage());
    }
}
