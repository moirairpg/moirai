package es.thalesalv.chatrpg.application.service;

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

import es.thalesalv.chatrpg.adapters.data.entity.WorldEntity;
import es.thalesalv.chatrpg.adapters.data.repository.WorldRepository;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntryDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntryEntityToDTO;
import es.thalesalv.chatrpg.application.mapper.world.WorldDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.world.WorldEntityToDTO;
import es.thalesalv.chatrpg.domain.exception.InsufficientPermissionException;
import es.thalesalv.chatrpg.domain.exception.WorldNotFoundException;
import es.thalesalv.chatrpg.domain.model.chconf.World;

@ExtendWith(MockitoExtension.class)
public class WorldServiceTest {

    @Mock
    private WorldRepository worldRepository;

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
        worldService = new WorldService(worldDTOToEntity, worldEntityToDTO, worldRepository);
    }

    @Test
    public void insertWorldTest() {

        final World world = buildSimplePublicWorld();
        final WorldEntity entity = buildSimplePublicWorldEntity();

        when(worldRepository.save(any(WorldEntity.class))).thenReturn(entity);

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

        world.setOwner(userId);
        entity.setOwner(userId);

        when(worldRepository.findById(NANO_ID)).thenReturn(Optional.of(entity));
        when(worldRepository.save(any(WorldEntity.class))).thenReturn(entity);

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
}
