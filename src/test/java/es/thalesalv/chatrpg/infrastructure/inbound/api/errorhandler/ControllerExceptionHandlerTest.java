package es.thalesalv.chatrpg.infrastructure.inbound.api.errorhandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.support.WebExchangeBindException;

import es.thalesalv.chatrpg.AbstractRestControllerTest;
import es.thalesalv.chatrpg.common.exception.AssetAccessDeniedException;
import es.thalesalv.chatrpg.common.exception.AssetNotFoundException;
import es.thalesalv.chatrpg.common.exception.AuthenticationFailedException;
import es.thalesalv.chatrpg.common.exception.BusinessRuleViolationException;
import es.thalesalv.chatrpg.core.application.query.world.GetWorldById;
import es.thalesalv.chatrpg.infrastructure.inbound.api.mapper.WorldRequestMapper;
import es.thalesalv.chatrpg.infrastructure.inbound.api.mapper.WorldResponseMapper;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.CreateWorldRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.ErrorResponse;

public class ControllerExceptionHandlerTest extends AbstractRestControllerTest {

    @MockBean
    private WorldResponseMapper worldResponseMapper;

    @MockBean
    private WorldRequestMapper worldRequestMapper;

    @Test
    public void http404WhenAssetNotFound() {

        // Given
        String worldId = "WRLDID";

        when(useCaseRunner.run(any(GetWorldById.class))).thenThrow(AssetNotFoundException.class);

        // Then
        webTestClient.get()
                .uri("/world/" + worldId)
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(response.getMessage()).isEqualTo("The asset requested could not be found.");
                });
    }

    @Test
    public void http422WhenBusinessRuleException() {

        // Given
        String worldId = "WRLDID";

        when(useCaseRunner.run(any(GetWorldById.class))).thenThrow(BusinessRuleViolationException.class);

        // Then
        webTestClient.get()
                .uri("/world/" + worldId)
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                });
    }

    @Test
    public void http400WhenValidationInvalid() {

        // Given
        CreateWorldRequest request = CreateWorldRequest.builder().build();

        when(useCaseRunner.run(any(GetWorldById.class))).thenThrow(WebExchangeBindException.class);

        // Then
        webTestClient.post()
                .uri("/world")
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
                .bodyValue(request)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                });
    }

    @Test
    public void http403WhenAccessDenied() {

        // Given
        String worldId = "WRLDID";

        when(useCaseRunner.run(any(GetWorldById.class))).thenThrow(AssetAccessDeniedException.class);

        // Then
        webTestClient.get()
                .uri("/world/" + worldId)
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getCode()).isEqualTo(HttpStatus.FORBIDDEN);
                });
    }

    @Test
    public void http401WhenUnauthorizedAccess() {

        // Given
        String worldId = "WRLDID";

        when(useCaseRunner.run(any(GetWorldById.class))).thenThrow(AuthenticationFailedException.class);

        // Then
        webTestClient.get()
                .uri("/world/" + worldId)
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
                });
    }

    @Test
    public void http401WhenNoAuthorizationHeader() {

        // Given
        String worldId = "WRLDID";

        // Then
        webTestClient.get()
                .uri("/world/" + worldId)
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }

    @Test
    public void http500WhenUnknownError() {

        // Given
        String worldId = "WRLDID";

        when(useCaseRunner.run(any(GetWorldById.class))).thenThrow(RuntimeException.class);

        // Then
        webTestClient.get()
                .uri("/world/" + worldId)
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }
}
