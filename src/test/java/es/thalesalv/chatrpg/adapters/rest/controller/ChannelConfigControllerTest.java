package es.thalesalv.chatrpg.adapters.rest.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections4.ListUtils;
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
import es.thalesalv.chatrpg.domain.model.api.ChannelConfigPage;
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

    @Test
    public void testRetrieveAllWithPagination_shouldWork() {

        final int amountOfItems = 20;
        final int pageNumber = 1;
        final ChannelConfig channelConfig = ChannelConfigTestUtils.buildChannelConfig();
        final List<ChannelConfig> channelConfigs = ChannelConfigTestUtils.createList(67);
        channelConfigs.add(channelConfig);

        Collections.sort(channelConfigs, Comparator.comparing(ChannelConfig::getName));
        final int numberOfPages = (int) Math.ceil((double) channelConfigs.size() / amountOfItems);
        final List<ChannelConfig> channelConfigPage = ListUtils.partition(channelConfigs, amountOfItems)
                .get(pageNumber - 1);

        final ChannelConfigPage response = ChannelConfigPage.builder()
                .currentPage(pageNumber)
                .numberOfPages(numberOfPages)
                .channelConfigs(channelConfigPage)
                .totalNumberOfItems(channelConfigs.size())
                .numberOfItemsInPage(channelConfigPage.size())
                .build();

        Mockito.when(channelConfigService.retrieveAllWithPagination("123456", 2, 20))
                .thenReturn(response);

        // TODO improve tests to actually account for body values rather than just
        // status code
        webTestClient.get()
                .uri("/channel-config/paged/1/20")
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
    }

    @Test
    public void testRetrieveAllWithPaginationWithSearchCriteria_shouldWork() {

        final int amountOfItems = 20;
        final int pageNumber = 1;
        final ChannelConfig channelConfig = ChannelConfigTestUtils.buildChannelConfig();
        final List<ChannelConfig> channelConfigs = ChannelConfigTestUtils.createList(67);
        channelConfigs.add(channelConfig);

        Collections.sort(channelConfigs, Comparator.comparing(ChannelConfig::getName));
        final int numberOfPages = (int) Math.ceil((double) channelConfigs.size() / amountOfItems);
        final List<ChannelConfig> channelConfigPage = ListUtils.partition(channelConfigs, amountOfItems)
                .get(pageNumber - 1);

        final ChannelConfigPage response = ChannelConfigPage.builder()
                .currentPage(pageNumber)
                .numberOfPages(numberOfPages)
                .channelConfigs(channelConfigPage)
                .totalNumberOfItems(channelConfigs.size())
                .numberOfItemsInPage(channelConfigPage.size())
                .build();

        Mockito.when(channelConfigService.retrieveAllWithPagination("123456", 2, 20))
                .thenReturn(response);

        // TODO improve tests to actually account for body values rather than just
        // status code
        webTestClient.get()
                .uri("/channel-config/paged/1/20/search/owner/123456")
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
    }
}
