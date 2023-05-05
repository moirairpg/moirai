package es.thalesalv.chatrpg.application.service;

import static es.thalesalv.chatrpg.testutils.LorebookTestUtils.buildSimpleLorebookEntry;
import static es.thalesalv.chatrpg.testutils.LorebookTestUtils.buildSimpleLorebookEntryEntity;
import static es.thalesalv.chatrpg.testutils.LorebookTestUtils.buildSimplePublicLorebook;
import static es.thalesalv.chatrpg.testutils.LorebookTestUtils.buildSimplePublicLorebookEntity;
import static es.thalesalv.chatrpg.testutils.LorebookTestUtils.buildSimplePublicLorebookEntityList;
import static es.thalesalv.chatrpg.testutils.LorebookTestUtils.buildSimplePublicLorebookList;
import static es.thalesalv.chatrpg.testutils.LorebookTestUtils.hasReadPermissions;
import static es.thalesalv.chatrpg.testutils.LorebookTestUtils.hasWritePermissions;
import static es.thalesalv.chatrpg.testutils.WorldTestUtils.buildSimplePublicWorldEntityList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntity;
import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryEntity;
import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryRegexEntity;
import es.thalesalv.chatrpg.adapters.data.repository.LorebookEntryRegexRepository;
import es.thalesalv.chatrpg.adapters.data.repository.LorebookEntryRepository;
import es.thalesalv.chatrpg.adapters.data.repository.LorebookRepository;
import es.thalesalv.chatrpg.adapters.data.repository.WorldRepository;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntityToDTO;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntryDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntryEntityToDTO;
import es.thalesalv.chatrpg.domain.exception.InsufficientPermissionException;
import es.thalesalv.chatrpg.domain.exception.LorebookEntryNotFoundException;
import es.thalesalv.chatrpg.domain.exception.LorebookNotFoundException;
import es.thalesalv.chatrpg.domain.model.chconf.Lorebook;
import es.thalesalv.chatrpg.domain.model.chconf.LorebookEntry;
import net.dv8tion.jda.api.JDA;

@ExtendWith(MockitoExtension.class)
public class LorebookServiceTest {

    @Mock
    private JDA jda;

    @Mock
    private LorebookRepository lorebookRepository;

    @Mock
    private LorebookEntryRepository lorebookEntryRepository;

    @Mock
    private LorebookEntryRegexRepository lorebookEntryRegexRepository;

    @Mock
    private WorldRepository worldRepository;

    private LorebookEntryEntityToDTO lorebookEntryEntityToDTO;
    private LorebookEntryDTOToEntity lorebookEntryDTOToEntity;
    private LorebookDTOToEntity lorebookDTOToEntity;
    private LorebookEntityToDTO lorebookEntityToDTO;
    private LorebookService lorebookService;

    private static final String NANO_ID = "241OZASGM6CESV7";

    @BeforeEach
    public void beforeEach() {

        lorebookEntryDTOToEntity = new LorebookEntryDTOToEntity();
        lorebookDTOToEntity = new LorebookDTOToEntity(lorebookEntryDTOToEntity);
        lorebookEntryEntityToDTO = new LorebookEntryEntityToDTO();
        lorebookEntityToDTO = new LorebookEntityToDTO(lorebookEntryEntityToDTO);
        lorebookService = new LorebookService(lorebookDTOToEntity, lorebookEntityToDTO, lorebookEntryDTOToEntity,
                lorebookEntryEntityToDTO, worldRepository, lorebookRepository, lorebookEntryRepository,
                lorebookEntryRegexRepository);
    }

    @Test
    public void insertLorebookTest() {

        final Lorebook lorebook = buildSimplePublicLorebook();
        final LorebookEntity entity = lorebookDTOToEntity.apply(lorebook);

        when(lorebookRepository.save(any(LorebookEntity.class))).thenReturn(entity);

        final Lorebook result = lorebookService.saveLorebook(lorebook);
        assertEquals("Test lorebook", result.getName());
        assertEquals("This is a test lorebook", result.getDescription());
    }

    @Test
    public void updateLorebookTest_shouldWork() {

        final String userId = "1083867535658725536"; // owner
        final Lorebook lorebook = buildSimplePublicLorebook();
        final LorebookEntity entity = buildSimplePublicLorebookEntity();
        final LorebookEntity updatedEntity = buildSimplePublicLorebookEntity();

        lorebook.setName("Updated name");
        updatedEntity.setName("Updated name");

        when(lorebookRepository.findById(NANO_ID)).thenReturn(Optional.of(entity));
        when(lorebookRepository.save(any(LorebookEntity.class))).thenReturn(updatedEntity);

        final Lorebook result = lorebookService.updateLorebook(NANO_ID, lorebook, userId);
        assertEquals("Updated name", result.getName());
        assertEquals("This is a test lorebook", result.getDescription());
    }

    @Test
    public void updateLorebookTest_lorebookNotFound() {

        final String userId = "1083867535658725536"; // owner
        final Lorebook lorebook = buildSimplePublicLorebook();

        when(lorebookRepository.findById(NANO_ID)).thenReturn(Optional.empty());

        final LorebookNotFoundException thrown = assertThrows(LorebookNotFoundException.class,
                () -> lorebookService.updateLorebook(NANO_ID, lorebook, userId));

        assertEquals("The requested lorebook for update could not be found", thrown.getMessage());
    }

    @Test
    public void updateLorebookTest_notEnoughPermissions() {

        final String userId = "195963508251164672"; // not owner or allowed
        final Lorebook lorebook = buildSimplePublicLorebook();
        final LorebookEntity entity = buildSimplePublicLorebookEntity();

        lorebook.setName("Updated name");

        when(lorebookRepository.findById(NANO_ID)).thenReturn(Optional.of(entity));

        final InsufficientPermissionException thrown = assertThrows(InsufficientPermissionException.class,
                () -> lorebookService.updateLorebook(NANO_ID, lorebook, userId));

        assertEquals("Not enough permissions to modify this lorebook", thrown.getMessage());
        assertFalse(hasWritePermissions(lorebook, userId));
    }

    @Test
    public void retrieveAllLorebooks() {

        final String userId = "302796314822049793"; // not owner, can see only some
        final List<Lorebook> completeList = buildSimplePublicLorebookList();
        final List<LorebookEntity> lorebookEntities = buildSimplePublicLorebookEntityList();

        when(lorebookRepository.findAll()).thenReturn(lorebookEntities);

        final List<Lorebook> filteredList = lorebookService.retrieveAllLorebooks(userId);
        assertEquals(8, filteredList.size());
        assertEquals(10, completeList.size());

        filteredList.forEach(l -> {
            assertTrue(hasReadPermissions(l, userId));
        });
    }

    @Test
    public void deleteLorebookTest_shouldWork() {

        final String userId = "302796314822049793"; // not owner or allowed
        final Lorebook lorebook = buildSimplePublicLorebook();
        final LorebookEntity entity = buildSimplePublicLorebookEntity();

        lorebook.setOwner(userId); // is made owner
        entity.setOwner(userId); // is made owner

        when(lorebookRepository.findById(NANO_ID)).thenReturn(Optional.of(entity));
        doReturn(buildSimplePublicWorldEntityList()).when(worldRepository)
                .findByLorebook(entity);

        doNothing().when(lorebookRepository)
                .delete(entity);

        lorebookService.deleteLorebook(NANO_ID, userId);
    }

    @Test
    public void deleteLorebookTest_insufficientPermissions() {

        final String userId = "302796314822049793"; // not owner or allowed
        final Lorebook lorebook = buildSimplePublicLorebook();
        final LorebookEntity entity = buildSimplePublicLorebookEntity();

        when(lorebookRepository.findById(NANO_ID)).thenReturn(Optional.of(entity));

        final InsufficientPermissionException thrown = assertThrows(InsufficientPermissionException.class,
                () -> lorebookService.deleteLorebook(NANO_ID, userId));

        assertEquals("Not enough permissions to delete this lorebook", thrown.getMessage());
        assertFalse(hasWritePermissions(lorebook, userId));
    }

    @Test
    public void deleteLorebookTest_lorebookNotFound() {

        final String userId = "302796314822049793"; // not owner or allowed
        final Lorebook lorebook = buildSimplePublicLorebook();

        when(lorebookRepository.findById(NANO_ID)).thenReturn(Optional.empty());

        final LorebookNotFoundException thrown = assertThrows(LorebookNotFoundException.class,
                () -> lorebookService.deleteLorebook(NANO_ID, userId));

        assertEquals("Error deleting lorebook: lorebook with id 241OZASGM6CESV7 could not be found in database.",
                thrown.getMessage());

        assertFalse(hasWritePermissions(lorebook, userId));
    }

    @Test
    public void insertLorebookEntryTest() {

        final String userId = "1083867535658725536"; // owner
        final LorebookEntry entry = buildSimpleLorebookEntry();
        final LorebookEntryRegexEntity entryEntity = buildSimpleLorebookEntryEntity();
        final LorebookEntity lorebookEntity = buildSimplePublicLorebookEntity();

        when(lorebookRepository.findById(anyString())).thenReturn(Optional.of(lorebookEntity));
        when(lorebookEntryRegexRepository.save(any(LorebookEntryRegexEntity.class))).thenReturn(entryEntity);

        final LorebookEntry result = lorebookService.saveLorebookEntry(entry, NANO_ID, userId);
        assertEquals(entry, result);
    }

    @Test
    public void insertLorebookEntryTest_notEnoughPermissions() {

        final String userId = "302796314822049793"; // not owner or allowed
        final LorebookEntry entry = buildSimpleLorebookEntry();
        final LorebookEntity lorebookEntity = buildSimplePublicLorebookEntity();

        when(lorebookRepository.findById(NANO_ID)).thenReturn(Optional.of(lorebookEntity));

        final InsufficientPermissionException thrown = assertThrows(InsufficientPermissionException.class,
                () -> lorebookService.saveLorebookEntry(entry, NANO_ID, userId));

        assertEquals("Not enough permissions to add entries to this lorebook", thrown.getMessage());
    }

    @Test
    public void insertLorebookEntryTest_lorebookNotFound() {

        final String userId = "302796314822049793"; // not owner or allowed
        final LorebookEntry entry = buildSimpleLorebookEntry();

        when(lorebookRepository.findById(NANO_ID)).thenReturn(Optional.empty());

        final LorebookNotFoundException thrown = assertThrows(LorebookNotFoundException.class,
                () -> lorebookService.saveLorebookEntry(entry, NANO_ID, userId));

        assertEquals(
                "Error saving lorebook entry to lorebook: lorebook with id 241OZASGM6CESV7 could not be found in database.",
                thrown.getMessage());
    }

    @Test
    public void updateLorebookEntryTest() {

        final String userId = "1083867535658725536"; // owner
        final LorebookEntry entry = buildSimpleLorebookEntry();
        final LorebookEntryRegexEntity entryEntity = buildSimpleLorebookEntryEntity();
        final LorebookEntity lorebookEntity = buildSimplePublicLorebookEntity();

        entryEntity.setLorebook(lorebookEntity);
        when(lorebookEntryRepository.findById(anyString())).thenReturn(Optional.of(entryEntity.getLorebookEntry()));
        when(lorebookEntryRegexRepository.findByLorebookEntry(any(LorebookEntryEntity.class)))
                .thenReturn(Optional.of(entryEntity));

        final LorebookEntry result = lorebookService.updateLorebookEntry(NANO_ID, entry, userId);
        assertEquals(entry, result);
    }

    @Test
    public void updateLorebookEntryTest_entryNotFound() {

        final String userId = "1083867535658725536"; // owner
        final LorebookEntry entry = buildSimpleLorebookEntry();
        final LorebookEntryRegexEntity entryEntity = buildSimpleLorebookEntryEntity();
        final LorebookEntity lorebookEntity = buildSimplePublicLorebookEntity();

        entryEntity.setLorebook(lorebookEntity);

        final LorebookEntryNotFoundException thrown = assertThrows(LorebookEntryNotFoundException.class,
                () -> lorebookService.updateLorebookEntry(NANO_ID, entry, userId));

        assertEquals(
                "Error updating lorebook entry: lorebook entry with id 241OZASGM6CESV7 could not be found in database.",
                thrown.getMessage());
    }

    @Test
    public void updateLorebookEntryTest_entryRegexNotFound() {

        final String userId = "1083867535658725536"; // owner
        final LorebookEntry entry = buildSimpleLorebookEntry();
        final LorebookEntryRegexEntity entryEntity = buildSimpleLorebookEntryEntity();
        final LorebookEntity lorebookEntity = buildSimplePublicLorebookEntity();

        entryEntity.setLorebook(lorebookEntity);

        when(lorebookEntryRepository.findById(anyString())).thenReturn(Optional.of(entryEntity.getLorebookEntry()));
        final LorebookEntryNotFoundException thrown = assertThrows(LorebookEntryNotFoundException.class,
                () -> lorebookService.updateLorebookEntry(NANO_ID, entry, userId));

        assertEquals("The requested entry does not have a regex entry attached to it", thrown.getMessage());
    }

    @Test
    public void updateLorebookEntryTest_notEnoughPermissions() {

        final String userId = "302796314822049793"; // not owner or allowed
        final LorebookEntry entry = buildSimpleLorebookEntry();
        final LorebookEntryRegexEntity entryEntity = buildSimpleLorebookEntryEntity();
        final LorebookEntity lorebookEntity = buildSimplePublicLorebookEntity();

        entryEntity.setLorebook(lorebookEntity);

        when(lorebookEntryRepository.findById(anyString())).thenReturn(Optional.of(entryEntity.getLorebookEntry()));
        when(lorebookEntryRegexRepository.findByLorebookEntry(any(LorebookEntryEntity.class)))
                .thenReturn(Optional.of(entryEntity));

        final InsufficientPermissionException thrown = assertThrows(InsufficientPermissionException.class,
                () -> lorebookService.updateLorebookEntry(NANO_ID, entry, userId));

        assertEquals("Not enough permissions to modify entries in this lorebook", thrown.getMessage());
    }

    @Test
    public void listAllLorebookEntriesTest() {

        final String userId = "1083867535658725536"; // owner
        final LorebookEntity lorebookEntity = buildSimplePublicLorebookEntity();

        lorebookEntity.getEntries()
                .add(buildSimpleLorebookEntryEntity());

        lorebookEntity.getEntries()
                .add(buildSimpleLorebookEntryEntity());

        lorebookEntity.getEntries()
                .add(buildSimpleLorebookEntryEntity());

        lorebookEntity.getEntries()
                .add(buildSimpleLorebookEntryEntity());

        when(lorebookRepository.findById(NANO_ID)).thenReturn(Optional.of(lorebookEntity));

        final List<LorebookEntry> result = lorebookService.retrieveAllLorebookEntriesInLorebook(NANO_ID, userId);
        assertEquals(5, result.size());
        assertEquals(lorebookEntity.getEntries()
                .size(), result.size());
    }

    @Test
    public void listAllLorebookEntriesTest_notEnoughPermissions() {

        final String userId = "302796314822049793"; // not owner or allowed
        final LorebookEntity lorebookEntity = buildSimplePublicLorebookEntity();

        lorebookEntity.setVisibility("private");

        when(lorebookRepository.findById(NANO_ID)).thenReturn(Optional.of(lorebookEntity));

        final InsufficientPermissionException thrown = assertThrows(InsufficientPermissionException.class,
                () -> lorebookService.retrieveAllLorebookEntriesInLorebook(NANO_ID, userId));

        assertEquals("Not enough permissions to retrieve entries in this lorebook", thrown.getMessage());
    }

    @Test
    public void listAllLorebookEntriesTest_lorebookNotFound() {

        final String userId = "302796314822049793"; // not owner or allowed

        when(lorebookRepository.findById(NANO_ID)).thenReturn(Optional.empty());

        final LorebookNotFoundException thrown = assertThrows(LorebookNotFoundException.class,
                () -> lorebookService.retrieveAllLorebookEntriesInLorebook(NANO_ID, userId));

        assertEquals("The lorebook requested could not be found", thrown.getMessage());
    }

    @Test
    public void retrieveLorebookEntrieByIdTest() {

        final String userId = "1083867535658725536"; // owner
        final LorebookEntryRegexEntity lorebookEntryRegexEntity = buildSimpleLorebookEntryEntity();
        final LorebookEntryEntity lorebookEntryEntity = lorebookEntryRegexEntity.getLorebookEntry();
        final LorebookEntity lorebookEntity = buildSimplePublicLorebookEntity();
        lorebookEntryRegexEntity.setLorebook(lorebookEntity);

        when(lorebookEntryRepository.findById(NANO_ID)).thenReturn(Optional.of(lorebookEntryEntity));
        when(lorebookEntryRegexRepository.findByLorebookEntry(lorebookEntryEntity))
                .thenReturn(Optional.of(lorebookEntryRegexEntity));

        final LorebookEntry result = lorebookService.retrieveLorebookEntryById(NANO_ID, userId);
        assertEquals(buildSimpleLorebookEntry(), result);
    }

    @Test
    public void retrieveLorebookEntrieByIdTest_notEnoughPermissions() {

        final String userId = "302796314822049793"; // not owner or allowed
        final LorebookEntryRegexEntity lorebookEntryRegexEntity = buildSimpleLorebookEntryEntity();
        final LorebookEntryEntity lorebookEntryEntity = lorebookEntryRegexEntity.getLorebookEntry();
        final LorebookEntity lorebookEntity = buildSimplePublicLorebookEntity();
        lorebookEntity.setVisibility("private");
        lorebookEntryRegexEntity.setLorebook(lorebookEntity);

        when(lorebookEntryRepository.findById(NANO_ID)).thenReturn(Optional.of(lorebookEntryEntity));
        when(lorebookEntryRegexRepository.findByLorebookEntry(lorebookEntryEntity))
                .thenReturn(Optional.of(lorebookEntryRegexEntity));

        final InsufficientPermissionException thrown = assertThrows(InsufficientPermissionException.class,
                () -> lorebookService.retrieveLorebookEntryById(NANO_ID, userId));

        assertEquals("Not enough permissions to retrieve entries in this lorebook", thrown.getMessage());
    }

    @Test
    public void retrieveLorebookEntrieByIdTest_lorebookNotFound() {

        final String userId = "1083867535658725536"; // owner
        final LorebookEntryRegexEntity lorebookEntryRegexEntity = buildSimpleLorebookEntryEntity();
        final LorebookEntity lorebookEntity = buildSimplePublicLorebookEntity();
        lorebookEntryRegexEntity.setLorebook(lorebookEntity);

        when(lorebookEntryRepository.findById(NANO_ID)).thenReturn(Optional.empty());

        final LorebookNotFoundException thrown = assertThrows(LorebookNotFoundException.class,
                () -> lorebookService.retrieveLorebookEntryById(NANO_ID, userId));

        assertEquals(
                "Error retrieving lorebook entry: lorebook entry with id 241OZASGM6CESV7 could not be found in database.",
                thrown.getMessage());
    }

    @Test
    public void deleteLorebookEntry() {

        final String userId = "1083867535658725536"; // owner
        final LorebookEntryRegexEntity lorebookEntryRegexEntity = buildSimpleLorebookEntryEntity();
        final LorebookEntryEntity lorebookEntryEntity = lorebookEntryRegexEntity.getLorebookEntry();
        final LorebookEntity lorebookEntity = buildSimplePublicLorebookEntity();
        final Lorebook lorebook = buildSimplePublicLorebook();
        lorebookEntryRegexEntity.setLorebook(lorebookEntity);

        when(lorebookEntryRepository.findById(NANO_ID)).thenReturn(Optional.of(lorebookEntryEntity));
        when(lorebookEntryRegexRepository.findByLorebookEntry(any(LorebookEntryEntity.class)))
                .thenReturn(Optional.of(lorebookEntryRegexEntity));

        lorebookService.deleteLorebookEntry(NANO_ID, userId);
        assertTrue(hasWritePermissions(lorebook, userId));
    }

    @Test
    public void deleteLorebookEntry_insufficientPermissions() {

        final String userId = "302796314822049793"; // not owner or allowed
        final LorebookEntryRegexEntity lorebookEntryRegexEntity = buildSimpleLorebookEntryEntity();
        final LorebookEntryEntity lorebookEntryEntity = lorebookEntryRegexEntity.getLorebookEntry();
        final LorebookEntity lorebookEntity = buildSimplePublicLorebookEntity();
        final Lorebook lorebook = buildSimplePublicLorebook();
        lorebookEntryRegexEntity.setLorebook(lorebookEntity);

        when(lorebookEntryRepository.findById(NANO_ID)).thenReturn(Optional.of(lorebookEntryEntity));
        when(lorebookEntryRegexRepository.findByLorebookEntry(any(LorebookEntryEntity.class)))
                .thenReturn(Optional.of(lorebookEntryRegexEntity));

        final InsufficientPermissionException thrown = assertThrows(InsufficientPermissionException.class,
                () -> lorebookService.deleteLorebookEntry(NANO_ID, userId));

        assertEquals("Not enough permissions to delete entries in this lorebook", thrown.getMessage());
        assertFalse(hasWritePermissions(lorebook, userId));
    }

    @Test
    public void deleteLorebookEntry_entryNotFound() {

        final String userId = "302796314822049793"; // not owner or allowed
        final LorebookEntryRegexEntity lorebookEntryRegexEntity = buildSimpleLorebookEntryEntity();
        final LorebookEntity lorebookEntity = buildSimplePublicLorebookEntity();
        lorebookEntryRegexEntity.setLorebook(lorebookEntity);

        when(lorebookEntryRepository.findById(NANO_ID)).thenReturn(Optional.empty());

        final LorebookEntryNotFoundException thrown = assertThrows(LorebookEntryNotFoundException.class,
                () -> lorebookService.deleteLorebookEntry(NANO_ID, userId));

        assertEquals("Error deleting entry: lorebook entry with id 241OZASGM6CESV7 could not be found in database.",
                thrown.getMessage());
    }

    @Test
    public void deleteLorebookEntry_noRegex() {

        final String userId = "302796314822049793"; // not owner or allowed
        final LorebookEntryRegexEntity lorebookEntryRegexEntity = buildSimpleLorebookEntryEntity();
        final LorebookEntryEntity lorebookEntryEntity = lorebookEntryRegexEntity.getLorebookEntry();
        final LorebookEntity lorebookEntity = buildSimplePublicLorebookEntity();
        lorebookEntryRegexEntity.setLorebook(lorebookEntity);

        when(lorebookEntryRepository.findById(NANO_ID)).thenReturn(Optional.of(lorebookEntryEntity));
        when(lorebookEntryRegexRepository.findByLorebookEntry(any(LorebookEntryEntity.class)))
                .thenReturn(Optional.empty());

        final LorebookEntryNotFoundException thrown = assertThrows(LorebookEntryNotFoundException.class,
                () -> lorebookService.deleteLorebookEntry(NANO_ID, userId));

        assertEquals("The requested entry does not have a regex entry attached to it", thrown.getMessage());
    }
}
