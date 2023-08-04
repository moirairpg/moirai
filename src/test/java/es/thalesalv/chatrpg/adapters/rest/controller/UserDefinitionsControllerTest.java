package es.thalesalv.chatrpg.adapters.rest.controller;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import es.thalesalv.chatrpg.application.service.UserDefinitionsService;
import es.thalesalv.chatrpg.domain.exception.NotFoundException;
import es.thalesalv.chatrpg.domain.model.bot.UserDefinitions;

@RunWith(SpringRunner.class)
@WebFluxTest(UserDefinitionsController.class)
public class UserDefinitionsControllerTest {

    @MockBean
    private UserDefinitionsService userDefinitionsService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testRetrieveUserDefinitionById_shouldReturnOk() {

        final UserDefinitions userDefinitions = UserDefinitions.builder()
                .id("4234234")
                .build();

        Mockito.when(userDefinitionsService.retrieveUserDefinitions("4234234"))
                .thenReturn(userDefinitions);

        webTestClient.get()
                .uri("/userdef")
                .header("requester", "4234234")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void testRetrieveUserDefinitionById_definitionNotFound() {

        Mockito.when(userDefinitionsService.retrieveUserDefinitions("4234234"))
                .thenThrow(NotFoundException.class);

        webTestClient.get()
                .uri("/userdef")
                .header("requester", "4234234")
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }

    @Test
    public void testRetrieveUserDefinitionById_unknownError() {

        Mockito.when(userDefinitionsService.retrieveUserDefinitions("4234234"))
                .thenThrow(RuntimeException.class);

        webTestClient.get()
                .uri("/userdef")
                .header("requester", "4234234")
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }
}
