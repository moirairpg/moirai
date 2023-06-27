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

import es.thalesalv.chatrpg.application.service.PersonaService;
import es.thalesalv.chatrpg.domain.exception.InsufficientPermissionException;
import es.thalesalv.chatrpg.domain.exception.PersonaNotFoundException;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;
import es.thalesalv.chatrpg.testutils.PersonaTestUtils;

@RunWith(SpringRunner.class)
@WebFluxTest(PersonaController.class)
public class PersonaControllerTest {

    @MockBean
    private PersonaService personaService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testRetrieveAllPersonas_shouldReturnOk() {

        final Persona persona = PersonaTestUtils.buildSimplePublicPersona();
        final List<Persona> personas = new ArrayList<>();
        personas.add(persona);

        Mockito.when(personaService.retrieveAllPersonas("123456"))
                .thenReturn(personas);

        webTestClient.get()
                .uri("/persona")
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void testRetrieveAllPersonas_missingHeader_shouldReturnBadRequest() {

        webTestClient.get()
                .uri("/persona")
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    public void testRetrieveAllPersonas_generalError_shouldReturnInternalServerError() {

        final Persona persona = PersonaTestUtils.buildSimplePublicPersona();
        final List<Persona> personas = new ArrayList<>();
        personas.add(persona);

        Mockito.when(personaService.retrieveAllPersonas(Mockito.anyString()))
                .thenThrow(RuntimeException.class);

        webTestClient.get()
                .uri("/persona")
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }

    @Test
    public void testUpdatePersona_success() {

        final Persona persona = PersonaTestUtils.buildSimplePublicPersona();

        Mockito.when(personaService.updatePersona(persona.getId(), persona, "123456"))
                .thenReturn(persona);

        webTestClient.put()
                .uri("/persona/" + persona.getId())
                .bodyValue(persona)
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void testUpdatePersona_notFound_shouldThrowNotFound() {

        final Persona persona = PersonaTestUtils.buildSimplePublicPersona();

        Mockito.when(personaService.updatePersona(persona.getId(), persona, "123456"))
                .thenThrow(PersonaNotFoundException.class);

        webTestClient.put()
                .uri("/persona/" + persona.getId())
                .bodyValue(persona)
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void testUpdatePersona_wrongUser_shouldReturnForbidden() {

        final Persona persona = PersonaTestUtils.buildSimplePublicPersona();

        Mockito.when(personaService.updatePersona(persona.getId(), persona, "123456"))
                .thenThrow(InsufficientPermissionException.class);

        webTestClient.put()
                .uri("/persona/" + persona.getId())
                .bodyValue(persona)
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    public void testUpdatePersona_generalError_shouldReturnInternalServerError() {

        final Persona persona = PersonaTestUtils.buildSimplePublicPersona();
        final List<Persona> personas = new ArrayList<>();
        personas.add(persona);

        Mockito.when(personaService.updatePersona(persona.getId(), persona, "123456"))
                .thenThrow(RuntimeException.class);

        webTestClient.put()
                .uri("/persona/" + persona.getId())
                .bodyValue(persona)
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }

    @Test
    public void testSavePersona_success() {

        final Persona persona = PersonaTestUtils.buildSimplePublicPersona();
        final List<Persona> personas = new ArrayList<>();
        personas.add(persona);

        Mockito.when(personaService.savePersona(persona))
                .thenReturn(persona);

        webTestClient.post()
                .uri("/persona")
                .bodyValue(persona)
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void testSavePersona_generalError_shouldReturnInternalServerError() {

        final Persona persona = PersonaTestUtils.buildSimplePublicPersona();
        final List<Persona> personas = new ArrayList<>();
        personas.add(persona);

        Mockito.when(personaService.savePersona(persona))
                .thenThrow(RuntimeException.class);

        webTestClient.post()
                .uri("/persona")
                .bodyValue(persona)
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }

    @Test
    public void testDeletePersona_success() {

        final Persona persona = PersonaTestUtils.buildSimplePublicPersona();
        final List<Persona> personas = new ArrayList<>();
        personas.add(persona);

        webTestClient.delete()
                .uri("/persona/" + persona.getId())
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void testDeletePersona_notFound_shouldThrowNotFound() {

        final Persona persona = PersonaTestUtils.buildSimplePublicPersona();
        final List<Persona> personas = new ArrayList<>();
        personas.add(persona);

        Mockito.doThrow(PersonaNotFoundException.class)
                .when(personaService)
                .deletePersona(persona.getId(), "123456");

        webTestClient.delete()
                .uri("/persona/" + persona.getId())
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void testDeletePersona_notFound_shouldThrowForbidden() {

        final Persona persona = PersonaTestUtils.buildSimplePublicPersona();
        final List<Persona> personas = new ArrayList<>();
        personas.add(persona);

        Mockito.doThrow(InsufficientPermissionException.class)
                .when(personaService)
                .deletePersona(persona.getId(), "123456");

        webTestClient.delete()
                .uri("/persona/" + persona.getId())
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    public void testDeletePersona_generalError_shouldThrowInternalServerError() {

        final Persona persona = PersonaTestUtils.buildSimplePublicPersona();
        final List<Persona> personas = new ArrayList<>();
        personas.add(persona);

        Mockito.doThrow(RuntimeException.class)
                .when(personaService)
                .deletePersona(persona.getId(), "123456");

        webTestClient.delete()
                .uri("/persona/" + persona.getId())
                .header("requester", "123456")
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }
}
