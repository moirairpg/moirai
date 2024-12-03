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
import me.moirai.discordbot.core.application.usecase.channelconfig.request.AddFavoriteChannelConfig;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.CreateChannelConfig;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.DeleteChannelConfig;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.GetChannelConfigById;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.SearchChannelConfigsWithReadAccess;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.SearchChannelConfigsWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.SearchFavoriteChannelConfigs;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.UpdateChannelConfig;
import me.moirai.discordbot.core.application.usecase.channelconfig.result.CreateChannelConfigResult;
import me.moirai.discordbot.core.application.usecase.channelconfig.result.GetChannelConfigResult;
import me.moirai.discordbot.core.application.usecase.channelconfig.result.SearchChannelConfigsResult;
import me.moirai.discordbot.core.application.usecase.channelconfig.result.UpdateChannelConfigResult;
import me.moirai.discordbot.infrastructure.inbound.api.mapper.ChannelConfigRequestMapper;
import me.moirai.discordbot.infrastructure.inbound.api.mapper.ChannelConfigResponseMapper;
import me.moirai.discordbot.infrastructure.inbound.api.request.CreateChannelConfigRequest;
import me.moirai.discordbot.infrastructure.inbound.api.request.CreateChannelConfigRequestFixture;
import me.moirai.discordbot.infrastructure.inbound.api.request.FavoriteRequest;
import me.moirai.discordbot.infrastructure.inbound.api.request.UpdateChannelConfigRequest;
import me.moirai.discordbot.infrastructure.inbound.api.request.UpdateChannelConfigRequestFixture;
import me.moirai.discordbot.infrastructure.inbound.api.response.ChannelConfigResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.ChannelConfigResponseFixture;
import me.moirai.discordbot.infrastructure.inbound.api.response.CreateChannelConfigResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.SearchChannelConfigsResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.UpdateChannelConfigResponse;
import me.moirai.discordbot.infrastructure.security.authentication.config.AuthenticationSecurityConfig;

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
    public void http200WhenSearchFavoriteChannelConfigs() {

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

        when(useCaseRunner.run(any(SearchFavoriteChannelConfigs.class)))
                .thenReturn(mock(SearchChannelConfigsResult.class));
        when(channelConfigResponseMapper.toResponse(any(SearchChannelConfigsResult.class)))
                .thenReturn(expectedResponse);

        // Then
        webTestClient.get()
                .uri(String.format(CHANNEL_CONFIG_ID_BASE_URL, "search/favorites"))
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
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    public void http201WhenAddFavoriteChannelConfig() {

        // Given
        FavoriteRequest request = new FavoriteRequest();
        request.setAssetId("1234");

        when(useCaseRunner.run(any(AddFavoriteChannelConfig.class))).thenReturn(null);

        // Then
        webTestClient.post()
                .uri(String.format(CHANNEL_CONFIG_ID_BASE_URL, "favorite"))
                .bodyValue(request)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }
}
