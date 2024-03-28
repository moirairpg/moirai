package es.thalesalv.chatrpg.infrastructure.inbound.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import es.thalesalv.chatrpg.AbstractRestWebTest;
import es.thalesalv.chatrpg.core.application.command.world.CreateWorldLorebookEntry;
import es.thalesalv.chatrpg.core.application.command.world.CreateWorldLorebookEntryResult;
import es.thalesalv.chatrpg.core.application.command.world.DeleteWorldLorebookEntry;
import es.thalesalv.chatrpg.core.application.command.world.UpdateWorldLorebookEntry;
import es.thalesalv.chatrpg.core.application.command.world.UpdateWorldLorebookEntryResult;
import es.thalesalv.chatrpg.core.application.query.world.GetWorldLorebookEntryById;
import es.thalesalv.chatrpg.core.application.query.world.GetWorldLorebookEntryResult;
import es.thalesalv.chatrpg.core.application.query.world.SearchWorldLorebookEntries;
import es.thalesalv.chatrpg.core.application.query.world.SearchWorldLorebookEntriesResult;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.CreateWorldLorebookEntryRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.UpdateWorldLorebookEntryRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.CreateLorebookEntryResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.CreateWorldResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.LorebookEntryResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.SearchLorebookEntriesResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.SearchWorldsResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.UpdateWorldLorebookEntryResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.UpdateWorldResponse;

public class WorldLorebookControllerTest extends AbstractRestWebTest {

    @Test
    public void http200WhenSearchLorebookEntries() {

        // Given
        List<LorebookEntryResponse> results = Lists.list(LorebookEntryResponse.builder()
                .id("ID")
                .name("NAME")
                .description("DESC")
                .regex("regex")
                .creationDate(OffsetDateTime.now())
                .lastUpdateDate(OffsetDateTime.now())
                .build());

        SearchLorebookEntriesResponse expectedResponse = SearchLorebookEntriesResponse.builder()
                .page(1)
                .totalPages(2)
                .totalResults(20)
                .resultsInPage(10)
                .results(results)
                .build();

        when(useCaseRunner.run(any(SearchWorldLorebookEntries.class)))
                .thenReturn(mock(SearchWorldLorebookEntriesResult.class));

        when(worldLorebookEntryResponseMapper.toResponse(any(SearchWorldLorebookEntriesResult.class)))
                .thenReturn(expectedResponse);

        // Then
        webTestClient.get()
                .uri("/world/1234/lorebook/search")
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
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
    public void http200WhenGetLorebookEntryById() {

        // Given
        LorebookEntryResponse expectedResponse = LorebookEntryResponse.builder()
                .id("ID")
                .name("NAME")
                .description("DESC")
                .regex("regex")
                .creationDate(OffsetDateTime.now())
                .lastUpdateDate(OffsetDateTime.now())
                .build();

        when(useCaseRunner.run(any(GetWorldLorebookEntryById.class)))
                .thenReturn(mock(GetWorldLorebookEntryResult.class));

        when(worldLorebookEntryResponseMapper.toResponse(any(GetWorldLorebookEntryResult.class)))
                .thenReturn(expectedResponse);

        // Then
        webTestClient.get()
                .uri("/world/1234/lorebook/ID")
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(LorebookEntryResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getId()).isEqualTo(expectedResponse.getId());
                    assertThat(response.getName()).isEqualTo(expectedResponse.getName());
                    assertThat(response.getRegex()).isEqualTo(expectedResponse.getRegex());
                    assertThat(response.getDescription()).isEqualTo(expectedResponse.getDescription());
                    assertThat(response.getCreationDate()).isEqualTo(expectedResponse.getCreationDate());
                    assertThat(response.getLastUpdateDate()).isEqualTo(expectedResponse.getLastUpdateDate());
                });
    }

    @Test
    public void http201WhenCreateLorebookEntry() {

        // Given
        CreateWorldLorebookEntryRequest request = CreateWorldLorebookEntryRequest.builder()
                .name("NAME")
                .description("DESC")
                .regex("regex")
                .build();

        CreateLorebookEntryResponse expectedResponse = CreateLorebookEntryResponse.build("ID");

        when(worldLorebookEntryRequestMapper.toCommand(any(CreateWorldLorebookEntryRequest.class),
                anyString(), anyString())).thenReturn(mock(CreateWorldLorebookEntry.class));

        when(useCaseRunner.run(any(CreateWorldLorebookEntry.class)))
                .thenReturn(mock(CreateWorldLorebookEntryResult.class));

        when(worldLorebookEntryResponseMapper.toResponse(any(CreateWorldLorebookEntryResult.class)))
                .thenReturn(expectedResponse);

        // Then
        webTestClient.post()
                .uri("/world/1234/lorebook")
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
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
    public void http200WhenUpdateLorebookEntry() {

        // Given
        UpdateWorldLorebookEntryRequest request = UpdateWorldLorebookEntryRequest.builder()
                .name("NAME")
                .description("DESC")
                .regex("regex")
                .build();

        UpdateWorldLorebookEntryResponse expectedResponse = UpdateWorldLorebookEntryResponse
                .build(OffsetDateTime.now());

        when(worldLorebookEntryRequestMapper.toCommand(any(UpdateWorldLorebookEntryRequest.class),
                anyString(), anyString(), anyString())).thenReturn(mock(UpdateWorldLorebookEntry.class));

        when(useCaseRunner.run(any(UpdateWorldLorebookEntry.class)))
                .thenReturn(mock(UpdateWorldLorebookEntryResult.class));

        when(worldLorebookEntryResponseMapper.toResponse(any(UpdateWorldLorebookEntryResult.class)))
                .thenReturn(expectedResponse);

        // Then
        webTestClient.put()
                .uri("/world/1234/lorebook/1234")
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
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
        when(worldLorebookEntryRequestMapper.toCommand(anyString(), anyString(), anyString()))
                .thenReturn(mock(DeleteWorldLorebookEntry.class));

        when(useCaseRunner.run(any(DeleteWorldLorebookEntry.class))).thenReturn(null);

        // Then
        webTestClient.delete()
                .uri("/world/1234/lorebook/1234")
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }
}
