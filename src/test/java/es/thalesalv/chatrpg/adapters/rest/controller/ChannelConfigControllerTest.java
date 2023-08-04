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

import es.thalesalv.chatrpg.application.service.ChannelConfigService;
import es.thalesalv.chatrpg.domain.exception.ChannelConfigNotFoundException;
import es.thalesalv.chatrpg.domain.exception.InsufficientPermissionException;
import es.thalesalv.chatrpg.domain.model.bot.ChannelConfig;
import es.thalesalv.chatrpg.testutils.ChannelConfigTestUtils;

@RunWith(SpringRunner.class)
@WebFluxTest(ChannelConfigController.class)
public class ChannelConfigControllerTest {

    @MockBean
    private ChannelConfigService channelConfigService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testRetrieveAllChannelConfigs_shouldReturnOk() {

        final ChannelConfig channelConfig = ChannelConfigTestUtils.buildChannelConfig();
        final List<ChannelConfig> channelConfigs = new ArrayList<>();
        channelConfigs.add(channelConfig);

        Mockito.when(channelConfigService.retrieveAllChannelConfigs("123456"))
                .thenReturn(channelConfigs);

        webTestClient.get()
                .uri("/channel-config")
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void testRetrieveAllChannelConfigs_missingHeader_shouldReturnBadRequest() {

        webTestClient.get()
                .uri("/channel-config")
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void testRetrieveAllChannelConfigs_generalError_shouldReturnInternalServerError() {

        final ChannelConfig channelConfig = ChannelConfigTestUtils.buildChannelConfig();
        final List<ChannelConfig> channelConfigs = new ArrayList<>();
        channelConfigs.add(channelConfig);

        Mockito.when(channelConfigService.retrieveAllChannelConfigs(Mockito.anyString()))
                .thenThrow(RuntimeException.class);

        webTestClient.get()
                .uri("/channel-config")
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }

    @Test
    public void testUpdateChannelConfig_success() {

        final ChannelConfig channelConfig = ChannelConfigTestUtils.buildChannelConfig();

        Mockito.when(channelConfigService.updateChannelConfig(channelConfig.getId(), channelConfig, "123456"))
                .thenReturn(channelConfig);

        webTestClient.put()
                .uri("/channel-config/" + channelConfig.getId())
                .bodyValue(channelConfig)
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void testUpdateChannelConfig_notFound_shouldThrowNotFound() {

        final ChannelConfig channelConfig = ChannelConfigTestUtils.buildChannelConfig();

        Mockito.when(channelConfigService.updateChannelConfig(channelConfig.getId(), channelConfig, "123456"))
                .thenThrow(ChannelConfigNotFoundException.class);

        webTestClient.put()
                .uri("/channel-config/" + channelConfig.getId())
                .bodyValue(channelConfig)
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void testUpdateChannelConfig_wrongUser_shouldReturnForbidden() {

        final ChannelConfig channelConfig = ChannelConfigTestUtils.buildChannelConfig();

        Mockito.when(channelConfigService.updateChannelConfig(channelConfig.getId(), channelConfig, "123456"))
                .thenThrow(InsufficientPermissionException.class);

        webTestClient.put()
                .uri("/channel-config/" + channelConfig.getId())
                .bodyValue(channelConfig)
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    public void testUpdateChannelConfig_generalError_shouldReturnInternalServerError() {

        final ChannelConfig channelConfig = ChannelConfigTestUtils.buildChannelConfig();
        final List<ChannelConfig> channelConfigs = new ArrayList<>();
        channelConfigs.add(channelConfig);

        Mockito.when(channelConfigService.updateChannelConfig(channelConfig.getId(), channelConfig, "123456"))
                .thenThrow(RuntimeException.class);

        webTestClient.put()
                .uri("/channel-config/" + channelConfig.getId())
                .bodyValue(channelConfig)
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }

    @Test
    public void testSaveChannelConfig_success() {

        final ChannelConfig channelConfig = ChannelConfigTestUtils.buildChannelConfig();
        final List<ChannelConfig> channelConfigs = new ArrayList<>();
        channelConfigs.add(channelConfig);

        Mockito.when(channelConfigService.saveChannelConfig(channelConfig))
                .thenReturn(channelConfig);

        webTestClient.post()
                .uri("/channel-config")
                .bodyValue(channelConfig)
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void testSaveChannelConfig_generalError_shouldReturnInternalServerError() {

        final ChannelConfig channelConfig = ChannelConfigTestUtils.buildChannelConfig();
        final List<ChannelConfig> channelConfigs = new ArrayList<>();
        channelConfigs.add(channelConfig);

        Mockito.when(channelConfigService.saveChannelConfig(channelConfig))
                .thenThrow(RuntimeException.class);

        webTestClient.post()
                .uri("/channel-config")
                .bodyValue(channelConfig)
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }

    @Test
    public void testDeleteChannelConfig_success() {

        final ChannelConfig channelConfig = ChannelConfigTestUtils.buildChannelConfig();
        final List<ChannelConfig> channelConfigs = new ArrayList<>();
        channelConfigs.add(channelConfig);

        webTestClient.delete()
                .uri("/channel-config/" + channelConfig.getId())
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void testDeleteChannelConfig_notFound_shouldThrowNotFound() {

        final ChannelConfig channelConfig = ChannelConfigTestUtils.buildChannelConfig();
        final List<ChannelConfig> channelConfigs = new ArrayList<>();
        channelConfigs.add(channelConfig);

        Mockito.doThrow(ChannelConfigNotFoundException.class)
                .when(channelConfigService)
                .deleteChannelConfig(channelConfig.getId(), "123456");

        webTestClient.delete()
                .uri("/channel-config/" + channelConfig.getId())
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void testDeleteChannelConfig_notFound_shouldThrowForbidden() {

        final ChannelConfig channelConfig = ChannelConfigTestUtils.buildChannelConfig();
        final List<ChannelConfig> channelConfigs = new ArrayList<>();
        channelConfigs.add(channelConfig);

        Mockito.doThrow(InsufficientPermissionException.class)
                .when(channelConfigService)
                .deleteChannelConfig(channelConfig.getId(), "123456");

        webTestClient.delete()
                .uri("/channel-config/" + channelConfig.getId())
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    public void testDeleteChannelConfig_generalError_shouldThrowInternalServerError() {

        final ChannelConfig channelConfig = ChannelConfigTestUtils.buildChannelConfig();
        final List<ChannelConfig> channelConfigs = new ArrayList<>();
        channelConfigs.add(channelConfig);

        Mockito.doThrow(RuntimeException.class)
                .when(channelConfigService)
                .deleteChannelConfig(channelConfig.getId(), "123456");

        webTestClient.delete()
                .uri("/channel-config/" + channelConfig.getId())
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }
}
