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
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.WorldResponseFixture;

public class WorldControllerTest extends AbstractRestControllerTest {

    @MockBean
    private WorldResponseMapper responseMapper;

    @MockBean
    private WorldRequestMapper requestMapper;

    private static final String WORLD_BASE_URL = "/world";
    private static final String WORLD_ID_BASE_URL = "/world/%s";

    @Test
    public void http200WhenGetWorldById() {

        // Given
        String worldId = "WRLDID";

        WorldResponse expectedResponse = WorldResponseFixture.publicWorld().build();

        when(useCaseRunner.run(any(GetWorldById.class))).thenReturn(mock(GetWorldResult.class));
        when(responseMapper.toResponse(any(GetWorldResult.class))).thenReturn(expectedResponse);

        // Then
        webTestClient.get()
                .uri(String.format(WORLD_ID_BASE_URL, worldId))
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(WorldResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getId()).isEqualTo(expectedResponse.getId());
                    assertThat(response.getName()).isEqualTo(expectedResponse.getName());
                    assertThat(response.getDescription()).isEqualTo(expectedResponse.getDescription());
                    assertThat(response.getAdventureStart()).isEqualTo(expectedResponse.getAdventureStart());
                    assertThat(response.getVisibility()).isEqualTo(expectedResponse.getVisibility());
                    assertThat(response.getOwnerDiscordId()).isEqualTo(expectedResponse.getOwnerDiscordId());
                    assertThat(response.getCreationDate()).isEqualTo(expectedResponse.getCreationDate());
                    assertThat(response.getLastUpdateDate()).isEqualTo(expectedResponse.getLastUpdateDate());

                    assertThat(response.getUsersAllowedToRead())
                            .hasSameElementsAs(expectedResponse.getUsersAllowedToRead());

                    assertThat(response.getUsersAllowedToRead())
                            .hasSameElementsAs(expectedResponse.getUsersAllowedToRead());
                });
    }
}
