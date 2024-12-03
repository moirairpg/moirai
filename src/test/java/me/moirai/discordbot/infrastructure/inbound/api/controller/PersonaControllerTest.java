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
import me.moirai.discordbot.core.application.usecase.persona.request.AddFavoritePersona;
import me.moirai.discordbot.core.application.usecase.persona.request.CreatePersona;
import me.moirai.discordbot.core.application.usecase.persona.request.DeletePersona;
import me.moirai.discordbot.core.application.usecase.persona.request.GetPersonaById;
import me.moirai.discordbot.core.application.usecase.persona.request.SearchFavoritePersonas;
import me.moirai.discordbot.core.application.usecase.persona.request.SearchPersonasWithReadAccess;
import me.moirai.discordbot.core.application.usecase.persona.request.SearchPersonasWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.persona.request.UpdatePersona;
import me.moirai.discordbot.core.application.usecase.persona.result.CreatePersonaResult;
import me.moirai.discordbot.core.application.usecase.persona.result.GetPersonaResult;
import me.moirai.discordbot.core.application.usecase.persona.result.SearchPersonasResult;
import me.moirai.discordbot.core.application.usecase.persona.result.UpdatePersonaResult;
import me.moirai.discordbot.infrastructure.inbound.api.mapper.PersonaRequestMapper;
import me.moirai.discordbot.infrastructure.inbound.api.mapper.PersonaResponseMapper;
import me.moirai.discordbot.infrastructure.inbound.api.request.CreatePersonaRequest;
import me.moirai.discordbot.infrastructure.inbound.api.request.CreatePersonaRequestFixture;
import me.moirai.discordbot.infrastructure.inbound.api.request.FavoriteRequest;
import me.moirai.discordbot.infrastructure.inbound.api.request.UpdatePersonaRequest;
import me.moirai.discordbot.infrastructure.inbound.api.request.UpdatePersonaRequestFixture;
import me.moirai.discordbot.infrastructure.inbound.api.response.CreatePersonaResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.PersonaResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.PersonaResponseFixture;
import me.moirai.discordbot.infrastructure.inbound.api.response.SearchPersonasResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.UpdatePersonaResponse;
import me.moirai.discordbot.infrastructure.security.authentication.config.AuthenticationSecurityConfig;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = {
        PersonaController.class
}, excludeAutoConfiguration = {
        ReactiveSecurityAutoConfiguration.class,
        AuthenticationSecurityConfig.class
})
public class PersonaControllerTest extends AbstractRestWebTest {

    private static final String PERSONA_BASE_URL = "/persona";
    private static final String PERSONA_ID_BASE_URL = "/persona/%s";

    @MockBean
    private PersonaRequestMapper personaRequestMapper;

    @MockBean
    private PersonaResponseMapper personaResponseMapper;

    @Test
    public void http200WhenSearchPersonas() {

        // Given
        List<PersonaResponse> results = Lists.list(PersonaResponseFixture.publicPersona().build(),
                PersonaResponseFixture.privatePersona().build());

        SearchPersonasResponse expectedResponse = SearchPersonasResponse.builder()
                .page(1)
                .totalPages(2)
                .totalResults(20)
                .resultsInPage(10)
                .results(results)
                .build();

        when(useCaseRunner.run(any(SearchPersonasWithReadAccess.class))).thenReturn(mock(SearchPersonasResult.class));
        when(personaResponseMapper.toResponse(any(SearchPersonasResult.class))).thenReturn(expectedResponse);

        // Then
        webTestClient.get()
                .uri(String.format(PERSONA_ID_BASE_URL, "search"))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(SearchPersonasResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getTotalPages()).isEqualTo(2);
                    assertThat(response.getTotalResults()).isEqualTo(20);
                    assertThat(response.getResultsInPage()).isEqualTo(10);
                    assertThat(response.getResults()).hasSameSizeAs(results);
                });
    }

    @Test
    public void http200WhenSearchPersonasWithWritePermission() {

        // Given
        List<PersonaResponse> results = Lists.list(PersonaResponseFixture.publicPersona().build(),
                PersonaResponseFixture.privatePersona().build());

        SearchPersonasResponse expectedResponse = SearchPersonasResponse.builder()
                .page(1)
                .totalPages(2)
                .totalResults(20)
                .resultsInPage(10)
                .results(results)
                .build();

        when(useCaseRunner.run(any(SearchPersonasWithWriteAccess.class))).thenReturn(mock(SearchPersonasResult.class));
        when(personaResponseMapper.toResponse(any(SearchPersonasResult.class))).thenReturn(expectedResponse);

        // Then
        webTestClient.get()
                .uri(String.format(PERSONA_ID_BASE_URL, "search/own"))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(SearchPersonasResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getTotalPages()).isEqualTo(2);
                    assertThat(response.getTotalResults()).isEqualTo(20);
                    assertThat(response.getResultsInPage()).isEqualTo(10);
                    assertThat(response.getResults()).hasSameSizeAs(results);
                });
    }

    @Test
    public void http200WhenSearchFavoritePersonas() {

        // Given
        List<PersonaResponse> results = Lists.list(PersonaResponseFixture.publicPersona().build(),
                PersonaResponseFixture.privatePersona().build());

        SearchPersonasResponse expectedResponse = SearchPersonasResponse.builder()
                .page(1)
                .totalPages(2)
                .totalResults(20)
                .resultsInPage(10)
                .results(results)
                .build();

        when(useCaseRunner.run(any(SearchFavoritePersonas.class))).thenReturn(mock(SearchPersonasResult.class));
        when(personaResponseMapper.toResponse(any(SearchPersonasResult.class))).thenReturn(expectedResponse);

        // Then
        webTestClient.get()
                .uri(String.format(PERSONA_ID_BASE_URL, "search/favorites"))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(SearchPersonasResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getTotalPages()).isEqualTo(2);
                    assertThat(response.getTotalResults()).isEqualTo(20);
                    assertThat(response.getResultsInPage()).isEqualTo(10);
                    assertThat(response.getResults()).hasSameSizeAs(results);
                });
    }

    @Test
    public void http200WhenGetPersonaById() {

        // Given
        String personaId = "WRLDID";

        PersonaResponse expectedResponse = PersonaResponseFixture.publicPersona().build();

        when(useCaseRunner.run(any(GetPersonaById.class))).thenReturn(mock(GetPersonaResult.class));
        when(personaResponseMapper.toResponse(any(GetPersonaResult.class))).thenReturn(expectedResponse);

        // Then
        webTestClient.get()
                .uri(String.format(PERSONA_ID_BASE_URL, personaId))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(PersonaResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getId()).isEqualTo(expectedResponse.getId());
                    assertThat(response.getName()).isEqualTo(expectedResponse.getName());
                    assertThat(response.getVisibility()).isEqualTo(expectedResponse.getVisibility());
                    assertThat(response.getOwnerDiscordId()).isEqualTo(expectedResponse.getOwnerDiscordId());
                    assertThat(response.getCreationDate()).isEqualTo(expectedResponse.getCreationDate());
                    assertThat(response.getLastUpdateDate()).isEqualTo(expectedResponse.getLastUpdateDate());

                    assertThat(response.getUsersAllowedToRead())
                            .hasSameElementsAs(expectedResponse.getUsersAllowedToRead());

                    assertThat(response.getUsersAllowedToWrite())
                            .hasSameElementsAs(expectedResponse.getUsersAllowedToWrite());
                });
    }

    @Test
    public void http201WhenCreatePersona() {

        // Given
        CreatePersonaRequest request = CreatePersonaRequestFixture.createPrivatePersona();
        CreatePersonaResponse expectedResponse = CreatePersonaResponse.build("WRLDID");

        when(personaRequestMapper.toCommand(any(CreatePersonaRequest.class), anyString()))
                .thenReturn(mock(CreatePersona.class));

        when(useCaseRunner.run(any(CreatePersona.class))).thenReturn(Mono.just(mock(CreatePersonaResult.class)));
        when(personaResponseMapper.toResponse(any(CreatePersonaResult.class))).thenReturn(expectedResponse);

        // Then
        webTestClient.post()
                .uri(PERSONA_BASE_URL)
                .bodyValue(request)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(CreatePersonaResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getId()).isEqualTo(expectedResponse.getId());
                });
    }

    @Test
    public void http200WhenUpdatePersona() {

        // Given
        String personaId = "WRLDID";
        UpdatePersonaRequest request = UpdatePersonaRequestFixture.privatePersona();
        UpdatePersonaResponse expectedResponse = UpdatePersonaResponse.build(OffsetDateTime.now());

        when(personaRequestMapper.toCommand(any(UpdatePersonaRequest.class), anyString(), anyString()))
                .thenReturn(mock(UpdatePersona.class));

        when(useCaseRunner.run(any(UpdatePersona.class))).thenReturn(Mono.just(mock(UpdatePersonaResult.class)));
        when(personaResponseMapper.toResponse(any(UpdatePersonaResult.class))).thenReturn(expectedResponse);

        // Then
        webTestClient.put()
                .uri(String.format(PERSONA_ID_BASE_URL, personaId))
                .bodyValue(request)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(UpdatePersonaResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getLastUpdateDate()).isEqualTo(expectedResponse.getLastUpdateDate());
                });
    }

    @Test
    public void http200WhenDeletePersona() {

        // Given
        String personaId = "WRLDID";

        when(personaRequestMapper.toCommand(anyString(), anyString())).thenReturn(mock(DeletePersona.class));
        when(useCaseRunner.run(any(DeletePersona.class))).thenReturn(null);

        // Then
        webTestClient.delete()
                .uri(String.format(PERSONA_ID_BASE_URL, personaId))
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    public void http201WhenAddFavoritePersona() {

        // Given
        FavoriteRequest request = new FavoriteRequest();
        request.setAssetId("1234");

        when(useCaseRunner.run(any(AddFavoritePersona.class))).thenReturn(null);

        // Then
        webTestClient.post()
                .uri(String.format(PERSONA_ID_BASE_URL, "favorite"))
                .bodyValue(request)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }
}
