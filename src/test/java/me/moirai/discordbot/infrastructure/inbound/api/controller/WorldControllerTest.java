package me.moirai.discordbot.infrastructure.inbound.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import me.moirai.discordbot.AbstractRestWebTest;
import me.moirai.discordbot.core.application.usecase.world.request.AddFavoriteWorld;
import me.moirai.discordbot.core.application.usecase.world.request.CreateWorld;
import me.moirai.discordbot.core.application.usecase.world.request.DeleteWorld;
import me.moirai.discordbot.core.application.usecase.world.request.GetWorldById;
import me.moirai.discordbot.core.application.usecase.world.request.RemoveFavoriteWorld;
import me.moirai.discordbot.core.application.usecase.world.request.SearchFavoriteWorlds;
import me.moirai.discordbot.core.application.usecase.world.request.SearchWorldsWithReadAccess;
import me.moirai.discordbot.core.application.usecase.world.request.SearchWorldsWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.world.request.UpdateWorld;
import me.moirai.discordbot.core.application.usecase.world.result.CreateWorldResult;
import me.moirai.discordbot.core.application.usecase.world.result.GetWorldResult;
import me.moirai.discordbot.core.application.usecase.world.result.SearchWorldsResult;
import me.moirai.discordbot.core.application.usecase.world.result.UpdateWorldResult;
import me.moirai.discordbot.infrastructure.inbound.api.mapper.WorldRequestMapper;
import me.moirai.discordbot.infrastructure.inbound.api.mapper.WorldResponseMapper;
import me.moirai.discordbot.infrastructure.inbound.api.request.CreateWorldRequest;
import me.moirai.discordbot.infrastructure.inbound.api.request.CreateWorldRequestFixture;
import me.moirai.discordbot.infrastructure.inbound.api.request.FavoriteRequest;
import me.moirai.discordbot.infrastructure.inbound.api.request.UpdateWorldRequest;
import me.moirai.discordbot.infrastructure.inbound.api.request.UpdateWorldRequestFixture;
import me.moirai.discordbot.infrastructure.inbound.api.response.CreateWorldResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.SearchWorldsResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.UpdateWorldResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.WorldResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.WorldResponseFixture;
import me.moirai.discordbot.infrastructure.security.authentication.config.AuthenticationSecurityConfig;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = {
        WorldController.class
}, excludeAutoConfiguration = {
        ReactiveSecurityAutoConfiguration.class,
        AuthenticationSecurityConfig.class
})
public class WorldControllerTest extends AbstractRestWebTest {

    private static final String WORLD_BASE_URL = "/world";
    private static final String WORLD_ID_BASE_URL = "/world/%s";

    @MockBean
    protected WorldResponseMapper worldResponseMapper;

    @MockBean
    protected WorldRequestMapper worldRequestMapper;

    @Test
    public void http200WhenSearchWorlds() {

        // Given
        List<WorldResponse> results = Lists.list(WorldResponseFixture.publicWorld().build(),
                WorldResponseFixture.privateWorld().build());

        SearchWorldsResponse expectedResponse = SearchWorldsResponse.builder()
                .page(1)
                .totalPages(2)
                .totalResults(20)
                .resultsInPage(10)
                .results(results)
                .build();

        when(useCaseRunner.run(any(SearchWorldsWithReadAccess.class))).thenReturn(mock(SearchWorldsResult.class));
        when(worldResponseMapper.toResponse(any(SearchWorldsResult.class))).thenReturn(expectedResponse);

        // Then
        webTestClient.get()
                .uri(String.format(WORLD_ID_BASE_URL, "search"))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(SearchWorldsResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getTotalPages()).isEqualTo(2);
                    assertThat(response.getTotalResults()).isEqualTo(20);
                    assertThat(response.getResultsInPage()).isEqualTo(10);
                    assertThat(response.getResults()).hasSameSizeAs(results);
                });
    }

    @Test
    public void http200WhenSearchWorldsWithWritePermission() {

        // Given
        List<WorldResponse> results = Lists.list(WorldResponseFixture.publicWorld().build(),
                WorldResponseFixture.privateWorld().build());

        SearchWorldsResponse expectedResponse = SearchWorldsResponse.builder()
                .page(1)
                .totalPages(2)
                .totalResults(20)
                .resultsInPage(10)
                .results(results)
                .build();

        when(useCaseRunner.run(any(SearchWorldsWithWriteAccess.class))).thenReturn(mock(SearchWorldsResult.class));
        when(worldResponseMapper.toResponse(any(SearchWorldsResult.class))).thenReturn(expectedResponse);

        // Then
        webTestClient.get()
                .uri(String.format(WORLD_ID_BASE_URL, "search/own"))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(SearchWorldsResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getTotalPages()).isEqualTo(2);
                    assertThat(response.getTotalResults()).isEqualTo(20);
                    assertThat(response.getResultsInPage()).isEqualTo(10);
                    assertThat(response.getResults()).hasSameSizeAs(results);
                });
    }

    @Test
    public void http200WhenSearchFavoriteWorlds() {

        // Given
        List<WorldResponse> results = Lists.list(WorldResponseFixture.publicWorld().build(),
                WorldResponseFixture.privateWorld().build());

        SearchWorldsResponse expectedResponse = SearchWorldsResponse.builder()
                .page(1)
                .totalPages(2)
                .totalResults(20)
                .resultsInPage(10)
                .results(results)
                .build();

        when(useCaseRunner.run(any(SearchFavoriteWorlds.class))).thenReturn(mock(SearchWorldsResult.class));
        when(worldResponseMapper.toResponse(any(SearchWorldsResult.class))).thenReturn(expectedResponse);

        // Then
        webTestClient.get()
                .uri(String.format(WORLD_ID_BASE_URL, "search/favorites"))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(SearchWorldsResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getTotalPages()).isEqualTo(2);
                    assertThat(response.getTotalResults()).isEqualTo(20);
                    assertThat(response.getResultsInPage()).isEqualTo(10);
                    assertThat(response.getResults()).hasSameSizeAs(results);
                });
    }

    @Test
    public void http200WhenGetWorldById() {

        // Given
        String worldId = "WRLDID";

        WorldResponse expectedResponse = WorldResponseFixture.publicWorld().build();

        when(useCaseRunner.run(any(GetWorldById.class))).thenReturn(mock(GetWorldResult.class));
        when(worldResponseMapper.toResponse(any(GetWorldResult.class))).thenReturn(expectedResponse);

        // Then
        webTestClient.get()
                .uri(String.format(WORLD_ID_BASE_URL, worldId))
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

    @Test
    public void http201WhenCreateWorld() {

        // Given
        CreateWorldRequest request = CreateWorldRequestFixture.createPrivateWorld();
        CreateWorldResponse expectedResponse = CreateWorldResponse.build("WRLDID");

        when(worldRequestMapper.toCommand(any(CreateWorldRequest.class), anyString()))
                .thenReturn(mock(CreateWorld.class));
        when(useCaseRunner.run(any(CreateWorld.class))).thenReturn(Mono.just(mock(CreateWorldResult.class)));
        when(worldResponseMapper.toResponse(any(CreateWorldResult.class))).thenReturn(expectedResponse);

        // Then
        webTestClient.post()
                .uri(WORLD_BASE_URL)
                .bodyValue(request)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(CreateWorldResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getId()).isEqualTo(expectedResponse.getId());
                });
    }

    @Test
    public void http200WhenUpdateWorld() {

        // Given
        String worldId = "WRLDID";
        UpdateWorldRequest request = UpdateWorldRequestFixture.createPrivateWorld();
        UpdateWorldResponse expectedResponse = UpdateWorldResponse.build(OffsetDateTime.now());

        when(worldRequestMapper.toCommand(any(UpdateWorldRequest.class), anyString(), anyString()))
                .thenReturn(mock(UpdateWorld.class));
        when(useCaseRunner.run(any(UpdateWorld.class))).thenReturn(Mono.just(mock(UpdateWorldResult.class)));
        when(worldResponseMapper.toResponse(any(UpdateWorldResult.class))).thenReturn(expectedResponse);

        // Then
        webTestClient.put()
                .uri(String.format(WORLD_ID_BASE_URL, worldId))
                .bodyValue(request)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(UpdateWorldResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getLastUpdateDate()).isEqualTo(expectedResponse.getLastUpdateDate());
                });
    }

    @Test
    public void http200WhenDeleteWorld() {

        // Given
        String worldId = "WRLDID";

        when(worldRequestMapper.toCommand(anyString(), anyString())).thenReturn(mock(DeleteWorld.class));
        when(useCaseRunner.run(any(DeleteWorld.class))).thenReturn(null);

        // Then
        webTestClient.delete()
                .uri(String.format(WORLD_ID_BASE_URL, worldId))
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    public void http201WhenAddFavoriteWorld() {

        // Given
        FavoriteRequest request = new FavoriteRequest();
        request.setAssetId("1234");

        when(useCaseRunner.run(any(AddFavoriteWorld.class))).thenReturn(null);

        // Then
        webTestClient.post()
                .uri(String.format(WORLD_ID_BASE_URL, "favorite"))
                .bodyValue(request)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    public void http201WhenRemoveFavoriteWorld() {

        // Given
        FavoriteRequest request = new FavoriteRequest();
        request.setAssetId("1234");

        when(useCaseRunner.run(any(RemoveFavoriteWorld.class))).thenReturn(null);

        // Then
        webTestClient.delete()
                .uri(String.format(WORLD_ID_BASE_URL, "favorite/1234"))
                .exchange()
                .expectStatus().is2xxSuccessful();
    }
}
