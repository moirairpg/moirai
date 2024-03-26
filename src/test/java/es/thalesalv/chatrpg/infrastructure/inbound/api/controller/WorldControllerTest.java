package es.thalesalv.chatrpg.infrastructure.inbound.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;

import es.thalesalv.chatrpg.AbstractRestControllerTest;
import es.thalesalv.chatrpg.core.application.query.world.GetWorldById;
import es.thalesalv.chatrpg.core.application.query.world.GetWorldResult;
import es.thalesalv.chatrpg.infrastructure.inbound.api.mapper.WorldRequestMapper;
import es.thalesalv.chatrpg.infrastructure.inbound.api.mapper.WorldResponseMapper;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.WorldResponse;

public class WorldControllerTest extends AbstractRestControllerTest {

    @MockBean
    private WorldResponseMapper responseMapper;

    @MockBean
    private WorldRequestMapper requestMapper;

    private static final String WORLD_BASE_URL = "/world";

    @Test
    public void http200WhenGetWorldById() {

        // Given
        String worldId = "WRLDID";

        WorldResponse expectedResponse = WorldResponse.builder()
                .build();

        when(useCaseRunner.run(any(GetWorldById.class))).thenReturn(mock(GetWorldResult.class));
        when(responseMapper.toResponse(any(GetWorldResult.class))).thenReturn(expectedResponse);

        // Then
        webTestClient.get()
                .uri(WORLD_BASE_URL + "/" + worldId)
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(WorldResponse.class)
                .consumeWith(response -> {
                    assertThat(response).isNotNull();
                });
    }
}
