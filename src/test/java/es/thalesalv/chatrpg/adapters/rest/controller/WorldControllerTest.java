package es.thalesalv.chatrpg.adapters.rest.controller;

import java.util.ArrayList;
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
import es.thalesalv.chatrpg.domain.exception.WorldNotFoundException;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import es.thalesalv.chatrpg.testutils.WorldTestUtils;

@RunWith(SpringRunner.class)
@WebFluxTest(WorldController.class)
public class WorldControllerTest {

    @MockBean
    private WorldService worldService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testRetrieveAllWorlds_shouldReturnOk() {

        final World world = WorldTestUtils.buildSimplePublicWorld();
        final List<World> worlds = new ArrayList<>();
        worlds.add(world);

        Mockito.when(worldService.retrieveAllWorlds("123456"))
                .thenReturn(worlds);

        webTestClient.get()
                .uri("/world")
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void testRetrieveAllWorlds_missingHeader_shouldReturnBadRequest() {

        webTestClient.get()
                .uri("/world")
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void testRetrieveAllWorlds_generalError_shouldReturnInternalServerError() {

        final World world = WorldTestUtils.buildSimplePublicWorld();
        final List<World> worlds = new ArrayList<>();
        worlds.add(world);

        Mockito.when(worldService.retrieveAllWorlds(Mockito.anyString()))
                .thenThrow(RuntimeException.class);

        webTestClient.get()
                .uri("/world")
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }

    @Test
    public void testUpdateWorld_success() {

        final World world = WorldTestUtils.buildSimplePublicWorld();

        Mockito.when(worldService.updateWorld(world.getId(), world, "123456"))
                .thenReturn(world);

        webTestClient.put()
                .uri("/world/" + world.getId())
                .bodyValue(world)
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void testUpdateWorld_notFound_shouldThrowNotFound() {

        final World world = WorldTestUtils.buildSimplePublicWorld();

        Mockito.when(worldService.updateWorld(world.getId(), world, "123456"))
                .thenThrow(WorldNotFoundException.class);

        webTestClient.put()
                .uri("/world/" + world.getId())
                .bodyValue(world)
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void testUpdateWorld_wrongUser_shouldReturnForbidden() {

        final World world = WorldTestUtils.buildSimplePublicWorld();

        Mockito.when(worldService.updateWorld(world.getId(), world, "123456"))
                .thenThrow(InsufficientPermissionException.class);

        webTestClient.put()
                .uri("/world/" + world.getId())
                .bodyValue(world)
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    public void testUpdateWorld_generalError_shouldReturnInternalServerError() {

        final World world = WorldTestUtils.buildSimplePublicWorld();
        final List<World> worlds = new ArrayList<>();
        worlds.add(world);

        Mockito.when(worldService.updateWorld(world.getId(), world, "123456"))
                .thenThrow(RuntimeException.class);

        webTestClient.put()
                .uri("/world/" + world.getId())
                .bodyValue(world)
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }

    @Test
    public void testSaveWorld_success() {

        final World world = WorldTestUtils.buildSimplePublicWorld();
        final List<World> worlds = new ArrayList<>();
        worlds.add(world);

        Mockito.when(worldService.saveWorld(world))
                .thenReturn(world);

        webTestClient.post()
                .uri("/world")
                .bodyValue(world)
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void testSaveWorld_generalError_shouldReturnInternalServerError() {

        final World world = WorldTestUtils.buildSimplePublicWorld();
        final List<World> worlds = new ArrayList<>();
        worlds.add(world);

        Mockito.when(worldService.saveWorld(world))
                .thenThrow(RuntimeException.class);

        webTestClient.post()
                .uri("/world")
                .bodyValue(world)
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }

    @Test
    public void testDeleteWorld_success() {

        final World world = WorldTestUtils.buildSimplePublicWorld();
        final List<World> worlds = new ArrayList<>();
        worlds.add(world);

        webTestClient.delete()
                .uri("/world/" + world.getId())
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void testDeleteWorld_notFound_shouldThrowNotFound() {

        final World world = WorldTestUtils.buildSimplePublicWorld();
        final List<World> worlds = new ArrayList<>();
        worlds.add(world);

        Mockito.doThrow(WorldNotFoundException.class)
                .when(worldService)
                .deleteWorld(world.getId(), "123456");

        webTestClient.delete()
                .uri("/world/" + world.getId())
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void testDeleteWorld_notFound_shouldThrowForbidden() {

        final World world = WorldTestUtils.buildSimplePublicWorld();
        final List<World> worlds = new ArrayList<>();
        worlds.add(world);

        Mockito.doThrow(InsufficientPermissionException.class)
                .when(worldService)
                .deleteWorld(world.getId(), "123456");

        webTestClient.delete()
                .uri("/world/" + world.getId())
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    public void testDeleteWorld_generalError_shouldThrowInternalServerError() {

        final World world = WorldTestUtils.buildSimplePublicWorld();
        final List<World> worlds = new ArrayList<>();
        worlds.add(world);

        Mockito.doThrow(RuntimeException.class)
                .when(worldService)
                .deleteWorld(world.getId(), "123456");

        webTestClient.delete()
                .uri("/world/" + world.getId())
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }
}
