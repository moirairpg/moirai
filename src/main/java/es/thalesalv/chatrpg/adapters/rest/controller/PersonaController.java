package es.thalesalv.chatrpg.adapters.rest.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.thalesalv.chatrpg.application.service.PersonaService;
import es.thalesalv.chatrpg.domain.model.api.ApiErrorResponse;
import es.thalesalv.chatrpg.domain.model.api.ApiResponse;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/persona")
public class PersonaController {

    private final PersonaService personaService;

    private static final String RETRIEVE_ALL_PERSONAS_REQUEST = "Received request for listing all personas";
    private static final String SAVE_PERSONA_REQUEST = "Received request for saving persona -> {}";
    private static final String UPDATE_PERSONA_REQUEST = "Received request for updating persona with ID {} -> {}";
    private static final String DELETE_PERSONA_REQUEST = "Received request for deleting persona with ID {}";
    private static final String DELETE_PERSONA_RESPONSE = "Returning response for deleting persona with ID {}";
    private static final String GENERAL_ERROR_MESSAGE = "An error occurred processing the request";
    private static final String ITEM_INSERTED_CANNOT_BE_NULL = "The item to be inserted cannot be null";

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonaController.class);

    @GetMapping
    public Mono<ResponseEntity<ApiResponse>> getAllPersonas(@RequestHeader("requester") String requesterUserId) {

        LOGGER.info(RETRIEVE_ALL_PERSONAS_REQUEST);
        return Mono.just(personaService.retrieveAllPersonas(requesterUserId))
                .map(this::buildResponse)
                .onErrorResume(e -> {
                    LOGGER.error("Error retrieving all personas", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
                });
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse>> savePersona(@RequestBody final Persona persona) {

        LOGGER.info(SAVE_PERSONA_REQUEST, persona);
        return Mono.just(personaService.savePersona(persona))
                .map(this::buildResponse)
                .onErrorResume(IllegalArgumentException.class, e -> {
                    LOGGER.error(ITEM_INSERTED_CANNOT_BE_NULL, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.BAD_REQUEST, ITEM_INSERTED_CANNOT_BE_NULL)));
                })
                .onErrorResume(e -> {
                    LOGGER.error(GENERAL_ERROR_MESSAGE, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
                });
    }

    @PutMapping("{persona-id}")
    public Mono<ResponseEntity<ApiResponse>> updatePersona(@RequestHeader("requester") String requesterUserId,
            @PathVariable(value = "persona-id") final String personaId, @RequestBody final Persona persona) {

        LOGGER.info(UPDATE_PERSONA_REQUEST, personaId, persona);
        return Mono.just(personaService.updatePersona(personaId, persona, requesterUserId))
                .map(this::buildResponse)
                .onErrorResume(IllegalArgumentException.class, e -> {
                    LOGGER.error(ITEM_INSERTED_CANNOT_BE_NULL, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.BAD_REQUEST, ITEM_INSERTED_CANNOT_BE_NULL)));
                })
                .onErrorResume(e -> {
                    LOGGER.error(GENERAL_ERROR_MESSAGE, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
                });
    }

    @DeleteMapping("{persona-id}")
    public Mono<ResponseEntity<ApiResponse>> deletePersona(@RequestHeader("requester") String requesterUserId,
            @PathVariable(value = "persona-id") final String personaId) {

        LOGGER.info(DELETE_PERSONA_REQUEST, personaId);
        return Mono.just(personaId)
                .map(id -> {
                    personaService.deletePersona(personaId, requesterUserId);
                    LOGGER.info(DELETE_PERSONA_RESPONSE, personaId);
                    return ResponseEntity.ok()
                            .body(ApiResponse.empty());
                })
                .onErrorResume(IllegalArgumentException.class, e -> {
                    LOGGER.error(ITEM_INSERTED_CANNOT_BE_NULL, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.BAD_REQUEST, ITEM_INSERTED_CANNOT_BE_NULL)));
                })
                .onErrorResume(e -> {
                    LOGGER.error(GENERAL_ERROR_MESSAGE, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
                });
    }

    private ResponseEntity<ApiResponse> buildResponse(List<Persona> personas) {

        LOGGER.info("Sending response for personas -> {}", personas);
        final ApiResponse respose = ApiResponse.builder()
                .personas(personas)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(respose);
    }

    private ResponseEntity<ApiResponse> buildResponse(Persona persona) {

        LOGGER.info("Sending response for personas -> {}", persona);
        final ApiResponse respose = ApiResponse.builder()
                .persona(persona)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(respose);
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
}
