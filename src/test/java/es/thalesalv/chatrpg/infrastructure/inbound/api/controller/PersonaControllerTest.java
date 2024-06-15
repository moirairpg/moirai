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
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;

import es.thalesalv.chatrpg.AbstractRestWebTest;
import es.thalesalv.chatrpg.core.application.usecase.persona.request.CreatePersona;
import es.thalesalv.chatrpg.core.application.usecase.persona.request.DeletePersona;
import es.thalesalv.chatrpg.core.application.usecase.persona.request.GetPersonaById;
import es.thalesalv.chatrpg.core.application.usecase.persona.request.SearchPersonasWithReadAccess;
import es.thalesalv.chatrpg.core.application.usecase.persona.request.SearchPersonasWithWriteAccess;
import es.thalesalv.chatrpg.core.application.usecase.persona.request.UpdatePersona;
import es.thalesalv.chatrpg.core.application.usecase.persona.result.CreatePersonaResult;
import es.thalesalv.chatrpg.core.application.usecase.persona.result.GetPersonaResult;
import es.thalesalv.chatrpg.core.application.usecase.persona.result.SearchPersonasResult;
import es.thalesalv.chatrpg.core.application.usecase.persona.result.UpdatePersonaResult;
import es.thalesalv.chatrpg.infrastructure.inbound.api.mapper.PersonaRequestMapper;
import es.thalesalv.chatrpg.infrastructure.inbound.api.mapper.PersonaResponseMapper;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.CreatePersonaRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.CreatePersonaRequestFixture;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.UpdatePersonaRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.UpdatePersonaRequestFixture;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.CreatePersonaResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.PersonaResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.PersonaResponseFixture;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.SearchPersonasResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.UpdatePersonaResponse;
import es.thalesalv.chatrpg.infrastructure.security.authentication.config.AuthenticationSecurityConfig;

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
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
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
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
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
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
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
        CreatePersonaRequest request = CreatePersonaRequestFixture.createPrivatePersona().build();
        CreatePersonaResponse expectedResponse = CreatePersonaResponse.build("WRLDID");

        when(personaRequestMapper.toCommand(any(CreatePersonaRequest.class), anyString()))
                .thenReturn(mock(CreatePersona.class));

        when(useCaseRunner.run(any(CreatePersona.class))).thenReturn(mock(CreatePersonaResult.class));
        when(personaResponseMapper.toResponse(any(CreatePersonaResult.class))).thenReturn(expectedResponse);

        // Then
        webTestClient.post()
                .uri(PERSONA_BASE_URL)
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
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
        UpdatePersonaRequest request = UpdatePersonaRequestFixture.privatePersona().build();
        UpdatePersonaResponse expectedResponse = UpdatePersonaResponse.build(OffsetDateTime.now());

        when(personaRequestMapper.toCommand(any(UpdatePersonaRequest.class), anyString(), anyString()))
                .thenReturn(mock(UpdatePersona.class));

        when(useCaseRunner.run(any(UpdatePersona.class))).thenReturn(mock(UpdatePersonaResult.class));
        when(personaResponseMapper.toResponse(any(UpdatePersonaResult.class))).thenReturn(expectedResponse);

        // Then
        webTestClient.put()
                .uri(String.format(PERSONA_ID_BASE_URL, personaId))
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
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
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }
}
