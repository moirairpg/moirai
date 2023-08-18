package es.thalesalv.chatrpg.adapters.rest.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.thalesalv.chatrpg.application.service.PersonaService;
import es.thalesalv.chatrpg.domain.exception.InsufficientPermissionException;
import es.thalesalv.chatrpg.domain.exception.PersonaNotFoundException;
import es.thalesalv.chatrpg.domain.model.api.ApiErrorResponse;
import es.thalesalv.chatrpg.domain.model.api.ApiResponse;
import es.thalesalv.chatrpg.domain.model.api.PagedResponse;
import es.thalesalv.chatrpg.domain.model.bot.Persona;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/persona")

public class PersonaController {

    private final PersonaService personaService;

    private static final String RETRIEVE_ALL_PERSONAS_REQUEST = "Received request for listing all personas";
    private static final String SAVE_PERSONA_REQUEST = "Received request for saving persona -> {}";
    private static final String UPDATE_PERSONA_REQUEST = "Received request for updating persona with ID {} -> {}";
    private static final String DELETE_PERSONA_REQUEST = "Received request for deleting persona with ID {}";
    private static final String GENERAL_ERROR_MESSAGE = "An error occurred processing the request";
    private static final String PERSONA_NOT_FOUND = "The requested persona could not be found";
    private static final String NOT_ENOUGH_PERMISSION = "Not enough permissions to modify this persona";

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonaController.class);

    @GetMapping
    public Mono<ResponseEntity<ApiResponse>> getAllPersonas(@RequestHeader("requester") final String requesterUserId) {

        try {
            LOGGER.info(RETRIEVE_ALL_PERSONAS_REQUEST);
            final List<Persona> personasRetrieved = personaService.retrieveAllPersonas(requesterUserId);
            return Mono.just(buildResponse(personasRetrieved));
        } catch (Exception e) {
            LOGGER.error("Error retrieving all personas", e);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
        }
    }

    @GetMapping("paged")
    public Mono<ResponseEntity<PagedResponse>> getAllPersonasByPageWithSearchCriteria(
            @RequestHeader("requester") String requesterUserId,
            @RequestParam(value = "pagenumber") final int pageNumber,
            @RequestParam(value = "itemamount") final int amountOfItems,
            @RequestParam(value = "searchfield") final String searchField,
            @RequestParam(value = "criteria") final String searchCriteria,
            @RequestParam(value = "sortby") final String sortBy) {

        try {
            LOGGER.info("Retrieving {} channel configurations in page {}", amountOfItems, pageNumber);
            final PagedResponse channelConfigPaginationResponse = personaService.retrieveAllWithPagination(
                    requesterUserId, searchCriteria, searchField, pageNumber, amountOfItems, sortBy);

            return Mono.just(ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(channelConfigPaginationResponse));
        } catch (Exception e) {
            LOGGER.error("Error retrieving filtered channel configurations", e);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponseForPagination(HttpStatus.INTERNAL_SERVER_ERROR,
                            GENERAL_ERROR_MESSAGE)));
        }
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse>> savePersona(@RequestBody final Persona persona) {

        try {
            LOGGER.info(SAVE_PERSONA_REQUEST, persona);
            final Persona savedPersona = personaService.savePersona(persona);
            return Mono.just(buildResponse(savedPersona));
        } catch (Exception e) {
            LOGGER.error("Error creating new persona", e);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
        }
    }

    @PutMapping("{persona-id}")
    public Mono<ResponseEntity<ApiResponse>> updatePersona(@RequestHeader("requester") final String requesterUserId,
            @PathVariable(value = "persona-id") final String personaId, @RequestBody final Persona persona) {

        try {
            LOGGER.info(UPDATE_PERSONA_REQUEST, personaId, persona);
            final Persona updatedPersona = personaService.updatePersona(personaId, persona, requesterUserId);
            return Mono.just(buildResponse(updatedPersona));
        } catch (PersonaNotFoundException e) {
            LOGGER.error(PERSONA_NOT_FOUND, e);
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.NOT_FOUND, PERSONA_NOT_FOUND)));
        } catch (InsufficientPermissionException e) {
            LOGGER.error(NOT_ENOUGH_PERMISSION, e);
            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.FORBIDDEN, NOT_ENOUGH_PERMISSION)));
        } catch (Exception e) {
            LOGGER.error("Error updating persona", e);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
        }
    }

    @DeleteMapping("{persona-id}")
    public Mono<ResponseEntity<ApiResponse>> deletePersona(@RequestHeader("requester") final String requesterUserId,
            @PathVariable(value = "persona-id") final String personaId) {

        try {
            LOGGER.info(DELETE_PERSONA_REQUEST, personaId);
            personaService.deletePersona(personaId, requesterUserId);
            return Mono.just(ResponseEntity.ok()
                    .body(ApiResponse.empty()));
        } catch (PersonaNotFoundException e) {
            LOGGER.error(PERSONA_NOT_FOUND, e);
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.NOT_FOUND, PERSONA_NOT_FOUND)));
        } catch (InsufficientPermissionException e) {
            LOGGER.error(NOT_ENOUGH_PERMISSION, e);
            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.FORBIDDEN, NOT_ENOUGH_PERMISSION)));
        } catch (Exception e) {
            LOGGER.error("Error updating persona", e);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
        }
    }

    private ResponseEntity<ApiResponse> buildResponse(List<Persona> personas) {

        LOGGER.info("Sending response for personas -> {}", personas);
        final ApiResponse response = ApiResponse.builder()
                .personas(personas)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    private ResponseEntity<ApiResponse> buildResponse(Persona persona) {

        LOGGER.info("Sending response for personas -> {}", persona);
        final ApiResponse response = ApiResponse.builder()
                .persona(persona)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    private ApiResponse buildErrorResponse(HttpStatus status, String message) {

        LOGGER.debug("Building error response object for personas");
        return ApiResponse.builder()
                .error(ApiErrorResponse.builder()
                        .message(message)
                        .status(status)
                        .build())
                .build();
    }

    private PagedResponse buildErrorResponseForPagination(HttpStatus status, String message) {

        LOGGER.debug("Building error response object for channel configs");
        return PagedResponse.builder()
                .error(ApiErrorResponse.builder()
                        .message(message)
                        .status(status)
                        .build())
                .build();
    }
}
