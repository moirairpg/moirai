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
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.request.CreateChannelConfig;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.request.DeleteChannelConfig;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.request.GetChannelConfigById;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.request.SearchChannelConfigsWithReadAccess;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.request.SearchChannelConfigsWithWriteAccess;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.request.UpdateChannelConfig;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.result.CreateChannelConfigResult;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.result.GetChannelConfigResult;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.result.SearchChannelConfigsResult;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.result.UpdateChannelConfigResult;
import es.thalesalv.chatrpg.infrastructure.inbound.api.mapper.ChannelConfigRequestMapper;
import es.thalesalv.chatrpg.infrastructure.inbound.api.mapper.ChannelConfigResponseMapper;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.CreateChannelConfigRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.CreateChannelConfigRequestFixture;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.UpdateChannelConfigRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.UpdateChannelConfigRequestFixture;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.ChannelConfigResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.ChannelConfigResponseFixture;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.CreateChannelConfigResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.SearchChannelConfigsResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.UpdateChannelConfigResponse;
import es.thalesalv.chatrpg.infrastructure.security.authentication.config.AuthenticationSecurityConfig;

@WebFluxTest(controllers = {
        ChannelConfigController.class
}, excludeAutoConfiguration = {
        ReactiveSecurityAutoConfiguration.class,
        AuthenticationSecurityConfig.class
})
public class ChannelConfigControllerTest extends AbstractRestWebTest {

    private static final String CHANNEL_CONFIG_BASE_URL = "/channel-config";
    private static final String CHANNEL_CONFIG_ID_BASE_URL = "/channel-config/%s";

    @MockBean
    private ChannelConfigRequestMapper channelConfigRequestMapper;

    @MockBean
    private ChannelConfigResponseMapper channelConfigResponseMapper;

    @Test
    public void http200WhenSearchChannelConfigs() {

        // Given
        List<ChannelConfigResponse> results = Lists.list(ChannelConfigResponseFixture.sample().build(),
                ChannelConfigResponseFixture.sample().build());

        SearchChannelConfigsResponse expectedResponse = SearchChannelConfigsResponse.builder()
                .page(1)
                .totalPages(2)
                .totalResults(20)
                .resultsInPage(10)
                .results(results)
                .build();

        when(useCaseRunner.run(any(SearchChannelConfigsWithReadAccess.class)))
                .thenReturn(mock(SearchChannelConfigsResult.class));

        when(channelConfigResponseMapper.toResponse(any(SearchChannelConfigsResult.class)))
                .thenReturn(expectedResponse);

        // Then
        webTestClient.get()
                .uri(String.format(CHANNEL_CONFIG_ID_BASE_URL, "search"))
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(SearchChannelConfigsResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getTotalPages()).isEqualTo(2);
                    assertThat(response.getTotalResults()).isEqualTo(20);
                    assertThat(response.getResultsInPage()).isEqualTo(10);
                    assertThat(response.getResults()).hasSameSizeAs(results);
                });
    }

    @Test
    public void http200WhenSearchChannelConfigsWithWritePermission() {

        // Given
        List<ChannelConfigResponse> results = Lists.list(ChannelConfigResponseFixture.sample().build(),
                ChannelConfigResponseFixture.sample().build());

        SearchChannelConfigsResponse expectedResponse = SearchChannelConfigsResponse.builder()
                .page(1)
                .totalPages(2)
                .totalResults(20)
                .resultsInPage(10)
                .results(results)
                .build();

        when(useCaseRunner.run(any(SearchChannelConfigsWithWriteAccess.class)))
                .thenReturn(mock(SearchChannelConfigsResult.class));
        when(channelConfigResponseMapper.toResponse(any(SearchChannelConfigsResult.class)))
                .thenReturn(expectedResponse);

        // Then
        webTestClient.get()
                .uri(String.format(CHANNEL_CONFIG_ID_BASE_URL, "search/own"))
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(SearchChannelConfigsResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getTotalPages()).isEqualTo(2);
                    assertThat(response.getTotalResults()).isEqualTo(20);
                    assertThat(response.getResultsInPage()).isEqualTo(10);
                    assertThat(response.getResults()).hasSameSizeAs(results);
                });
    }

    @Test
    public void http200WhenGetChannelConfigById() {

        // Given
        String channelConfigId = "WRLDID";

        ChannelConfigResponse expectedResponse = ChannelConfigResponseFixture.sample().build();

        when(useCaseRunner.run(any(GetChannelConfigById.class))).thenReturn(mock(GetChannelConfigResult.class));
        when(channelConfigResponseMapper.toResponse(any(GetChannelConfigResult.class))).thenReturn(expectedResponse);

        // Then
        webTestClient.get()
                .uri(String.format(CHANNEL_CONFIG_ID_BASE_URL, channelConfigId))
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(ChannelConfigResponse.class)
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
    public void http201WhenCreateChannelConfig() {

        // Given
        CreateChannelConfigRequest request = CreateChannelConfigRequestFixture.sample();
        CreateChannelConfigResponse expectedResponse = CreateChannelConfigResponse.build("WRLDID");

        when(channelConfigRequestMapper.toCommand(any(CreateChannelConfigRequest.class), anyString()))
                .thenReturn(mock(CreateChannelConfig.class));

        when(useCaseRunner.run(any(CreateChannelConfig.class))).thenReturn(mock(CreateChannelConfigResult.class));
        when(channelConfigResponseMapper.toResponse(any(CreateChannelConfigResult.class))).thenReturn(expectedResponse);

        // Then
        webTestClient.post()
                .uri(CHANNEL_CONFIG_BASE_URL)
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
                .bodyValue(request)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(CreateChannelConfigResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getId()).isEqualTo(expectedResponse.getId());
                });
    }

    @Test
    public void http200WhenUpdateChannelConfig() {

        // Given
        String channelConfigId = "WRLDID";
        UpdateChannelConfigRequest request = UpdateChannelConfigRequestFixture.sample();
        UpdateChannelConfigResponse expectedResponse = UpdateChannelConfigResponse.build(OffsetDateTime.now());

        when(channelConfigRequestMapper.toCommand(any(UpdateChannelConfigRequest.class), anyString(), anyString()))
                .thenReturn(mock(UpdateChannelConfig.class));

        when(useCaseRunner.run(any(UpdateChannelConfig.class))).thenReturn(mock(UpdateChannelConfigResult.class));
        when(channelConfigResponseMapper.toResponse(any(UpdateChannelConfigResult.class))).thenReturn(expectedResponse);

        // Then
        webTestClient.put()
                .uri(String.format(CHANNEL_CONFIG_ID_BASE_URL, channelConfigId))
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
                .bodyValue(request)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(UpdateChannelConfigResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getLastUpdateDate()).isEqualTo(expectedResponse.getLastUpdateDate());
                });
    }

    @Test
    public void http200WhenDeleteChannelConfig() {

        // Given
        String channelConfigId = "WRLDID";

        when(channelConfigRequestMapper.toCommand(anyString(), anyString()))
                .thenReturn(mock(DeleteChannelConfig.class));
        when(useCaseRunner.run(any(DeleteChannelConfig.class))).thenReturn(null);

        // Then
        webTestClient.delete()
                .uri(String.format(CHANNEL_CONFIG_ID_BASE_URL, channelConfigId))
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }
}
