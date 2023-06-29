package es.thalesalv.chatrpg.adapters.rest.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import es.thalesalv.chatrpg.application.service.WorldService;
import es.thalesalv.chatrpg.domain.exception.InsufficientPermissionException;
import es.thalesalv.chatrpg.domain.exception.LorebookEntryNotFoundException;
import es.thalesalv.chatrpg.domain.exception.WorldNotFoundException;
import es.thalesalv.chatrpg.domain.model.chconf.LorebookEntry;
import es.thalesalv.chatrpg.testutils.WorldTestUtils;

@RunWith(SpringRunner.class)
@WebFluxTest(LorebookEntryController.class)
public class LorebookEntryControllerTest {

    @MockBean
    private WorldService worldService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testRetrieveAllLorebookEntrys_shouldReturnOk() {

        final List<LorebookEntry> lorebookEntries = WorldTestUtils.buildSimplePublicWorld()
                .getLorebook();

        Mockito.when(worldService.retrieveAllLorebookEntriesInLorebook("123456", "123456"))
                .thenReturn(lorebookEntries);

        webTestClient.get()
                .uri("/lore/entry/lorebook/123456")
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void testRetrieveAllLorebookEntrys_missingHeader_shouldReturnBadRequest() {

        webTestClient.get()
                .uri("/lore/entry/lorebook/123456")
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void testRetrieveAllLorebookEntrys_generalError_shouldReturnInternalServerError() {

        Mockito.when(worldService.retrieveAllLorebookEntriesInLorebook(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(RuntimeException.class);

        webTestClient.get()
                .uri("/lore/entry/lorebook/123456")
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }

    @Test
    public void testUpdateLorebookEntry_success() {

        final List<LorebookEntry> lorebookEntries = WorldTestUtils.buildSimplePublicWorld()
                .getLorebook();

        final LorebookEntry lorebookEntry = lorebookEntries.get(0);

        Mockito.when(worldService.updateLorebookEntry(lorebookEntry.getId(), lorebookEntry, "123456"))
                .thenReturn(lorebookEntry);

        webTestClient.put()
                .uri("/lore/entry/" + lorebookEntry.getId())
                .bodyValue(lorebookEntry)
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void testUpdateLorebookEntry_notFound_shouldThrowNotFound() {

        final List<LorebookEntry> lorebookEntries = WorldTestUtils.buildSimplePublicWorld()
                .getLorebook();

        final LorebookEntry lorebookEntry = lorebookEntries.get(0);

        Mockito.when(worldService.updateLorebookEntry(lorebookEntry.getId(), lorebookEntry, "1083867535658725536"))
                .thenThrow(LorebookEntryNotFoundException.class);

        webTestClient.put()
                .uri("/lore/entry/" + lorebookEntry.getId())
                .bodyValue(lorebookEntry)
                .header("requester", "1083867535658725536")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void testUpdateLorebookEntry_wrongUser_shouldReturnForbidden() {

        final List<LorebookEntry> lorebookEntries = WorldTestUtils.buildSimplePublicWorld()
                .getLorebook();

        final LorebookEntry lorebookEntry = lorebookEntries.get(0);

        Mockito.when(worldService.updateLorebookEntry(lorebookEntry.getId(), lorebookEntry, "123456"))
                .thenThrow(InsufficientPermissionException.class);

        webTestClient.put()
                .uri("/lore/entry/" + lorebookEntry.getId())
                .bodyValue(lorebookEntry)
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    public void testUpdateLorebookEntry_generalError_shouldReturnInternalServerError() {

        final List<LorebookEntry> lorebookEntries = WorldTestUtils.buildSimplePublicWorld()
                .getLorebook();

        final LorebookEntry lorebookEntry = lorebookEntries.get(0);

        Mockito.when(worldService.updateLorebookEntry(lorebookEntry.getId(), lorebookEntry, "123456"))
                .thenThrow(RuntimeException.class);

        webTestClient.put()
                .uri("/lore/entry/" + lorebookEntry.getId())
                .bodyValue(lorebookEntry)
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }

    @Test
    public void testSaveLorebookEntry_success() {

        final List<LorebookEntry> lorebookEntries = WorldTestUtils.buildSimplePublicWorld()
                .getLorebook();

        final LorebookEntry lorebookEntry = lorebookEntries.get(0);

        Mockito.when(worldService.saveLorebookEntry(lorebookEntry, lorebookEntry.getId(), "123456"))
                .thenReturn(lorebookEntry);

        webTestClient.post()
                .uri("/lore/entry/123456")
                .bodyValue(lorebookEntry)
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void testSaveLorebookEntry_generalError_shouldReturnInternalServerError() {

        final List<LorebookEntry> lorebookEntries = WorldTestUtils.buildSimplePublicWorld()
                .getLorebook();

        final LorebookEntry lorebookEntry = lorebookEntries.get(0);

        Mockito.when(worldService.saveLorebookEntry(lorebookEntry, lorebookEntry.getId(), "123456"))
                .thenThrow(RuntimeException.class);

        webTestClient.post()
                .uri("/lore/entry/" + lorebookEntry.getId())
                .bodyValue(lorebookEntry)
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }

    @Test
    public void testSaveLorebookEntry_worldNotFound_shouldReturnNotFound() {

        final List<LorebookEntry> lorebookEntries = WorldTestUtils.buildSimplePublicWorld()
                .getLorebook();

        final LorebookEntry lorebookEntry = lorebookEntries.get(0);

        Mockito.when(worldService.saveLorebookEntry(lorebookEntry, lorebookEntry.getId(), "123456"))
                .thenThrow(WorldNotFoundException.class);

        webTestClient.post()
                .uri("/lore/entry/" + lorebookEntry.getId())
                .bodyValue(lorebookEntry)
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void testSaveLorebookEntry_insufficientPermission() {

        final List<LorebookEntry> lorebookEntries = WorldTestUtils.buildSimplePublicWorld()
                .getLorebook();

        final LorebookEntry lorebookEntry = lorebookEntries.get(0);

        Mockito.when(worldService.saveLorebookEntry(lorebookEntry, lorebookEntry.getId(), "123456"))
                .thenThrow(InsufficientPermissionException.class);

        webTestClient.post()
                .uri("/lore/entry/" + lorebookEntry.getId())
                .bodyValue(lorebookEntry)
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    public void testDeleteLorebookEntry_success() {

        final List<LorebookEntry> lorebookEntries = WorldTestUtils.buildSimplePublicWorld()
                .getLorebook();

        final LorebookEntry lorebookEntry = lorebookEntries.get(0);

        webTestClient.delete()
                .uri("/lore/entry/" + lorebookEntry.getId())
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void testDeleteLorebookEntry_notFound_shouldThrowNotFound() {

        final List<LorebookEntry> lorebookEntries = WorldTestUtils.buildSimplePublicWorld()
                .getLorebook();

        final LorebookEntry lorebookEntry = lorebookEntries.get(0);

        Mockito.doThrow(LorebookEntryNotFoundException.class)
                .when(worldService)
                .deleteLorebookEntry(lorebookEntry.getId(), "123456");

        webTestClient.delete()
                .uri("/lore/entry/" + lorebookEntry.getId())
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void testDeleteLorebookEntry_notFound_shouldThrowForbidden() {

        final List<LorebookEntry> lorebookEntries = WorldTestUtils.buildSimplePublicWorld()
                .getLorebook();

        final LorebookEntry lorebookEntry = lorebookEntries.get(0);

        Mockito.doThrow(InsufficientPermissionException.class)
                .when(worldService)
                .deleteLorebookEntry(lorebookEntry.getId(), "123456");

        webTestClient.delete()
                .uri("/lore/entry/" + lorebookEntry.getId())
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    public void testDeleteLorebookEntry_generalError_shouldThrowInternalServerError() {

        final List<LorebookEntry> lorebookEntries = WorldTestUtils.buildSimplePublicWorld()
                .getLorebook();

        final LorebookEntry lorebookEntry = lorebookEntries.get(0);

        Mockito.doThrow(RuntimeException.class)
                .when(worldService)
                .deleteLorebookEntry(lorebookEntry.getId(), "123456");

        webTestClient.delete()
                .uri("/lore/entry/" + lorebookEntry.getId())
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }
}
