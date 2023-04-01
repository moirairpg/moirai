package es.thalesalv.chatrpg.adapters.rest.controller;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import es.thalesalv.chatrpg.adapters.data.repository.PersonaRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.PersonaDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.chconfig.PersonaEntityToDTO;
import es.thalesalv.chatrpg.domain.model.api.ApiResponse;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/persona")
public class PersonaController {

    private final PersonaRepository personaRepository;
    private final PersonaEntityToDTO personaEntityToDTO;
    private final PersonaDTOToEntity personaDTOToEntity;

    private static final String RETRIEVE_ALL_PERSONAS_REQUEST = "Received request for listing all personas";
    private static final String RETRIEVE_ALL_PERSONAS_RESPONSE = "Returning response for listing all personas request -> {}";
    private static final String RETRIEVE_PERSONA_BY_ID_REQUEST = "Received request for retrieving persona with id {}";
    private static final String RETRIEVE_PERSONA_BY_ID_RESPONSE = "Returning response for listing persona with id {} request -> {}";
    private static final String SAVE_PERSONA_REQUEST = "Received request for saving persona -> {}";
    private static final String SAVE_PERSONA_RESPONSE = "Returning response for saving persona request -> {}";
    private static final String UPDATE_PERSONA_REQUEST = "Received request for updating persona with ID {} -> {}";
    private static final String UPDATE_PERSONA_RESPONSE = "Returning response for updating persona with id {} request -> {}";
    private static final String DELETE_PERSONA_REQUEST = "Received request for deleting persona with ID {}";
    private static final String DELETE_PERSONA_RESPONSE = "Returning response for deleting persona with ID {}";

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonaController.class);

    @GetMapping
    public Mono<ResponseEntity<ApiResponse>> getAllPersonas() {

        LOGGER.info(RETRIEVE_ALL_PERSONAS_REQUEST);
        return Mono.just(personaRepository.findAll())
                .map(p -> p.stream()
                        .map(personaEntityToDTO)
                        .collect(Collectors.toList()))
                .map(p -> ApiResponse.builder()
                        .personas(p)
                        .build())
                .map(p -> {
                    LOGGER.info(RETRIEVE_ALL_PERSONAS_RESPONSE, p);
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(p);
                });
    }

    @GetMapping("{persona-id}")
    public Mono<ResponseEntity<ApiResponse>> getPersonaById(
            @PathVariable(value = "persona-id") final String personaId) {

        LOGGER.info(RETRIEVE_PERSONA_BY_ID_REQUEST, personaId);
        return Mono.just(personaRepository.findById(personaId))
                .map(p -> p.map(personaEntityToDTO)
                        .orElseThrow(() -> new RuntimeException()))
                .map(p -> Stream.of(p)
                        .collect(Collectors.toList()))
                .map(p -> ApiResponse.builder()
                        .personas(p)
                        .build())
                .map(p -> {
                    LOGGER.info(RETRIEVE_PERSONA_BY_ID_RESPONSE, personaId, p);
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(p);
                });
    }

    @PutMapping
    public Mono<ResponseEntity<ApiResponse>> saveLorebook(final Persona persona) {

        LOGGER.info(SAVE_PERSONA_REQUEST, persona);
        return Mono.just(personaDTOToEntity.apply(persona))
                .map(personaRepository::save)
                .map(p -> Stream.of(p)
                        .map(personaEntityToDTO)
                        .collect(Collectors.toList()))
                .map(p -> ApiResponse.builder()
                        .personas(p)
                        .build())
                .map(p -> {
                    LOGGER.info(SAVE_PERSONA_RESPONSE, p);
                    return ResponseEntity.ok()
                            .body(p);
                });
    }

    @PatchMapping("{persona-id}")
    public Mono<ResponseEntity<ApiResponse>> updateLorebookById(
            @PathVariable(value = "persona-id") final String personaId, final Persona persona) {

        LOGGER.info(UPDATE_PERSONA_REQUEST, personaId, persona);
        return Mono.just(personaDTOToEntity.apply(persona))
                .map(p -> {
                    p.setId(personaId);
                    return personaRepository.save(p);
                })
                .map(p -> Stream.of(p)
                        .map(personaEntityToDTO)
                        .collect(Collectors.toList()))
                .map(p -> ApiResponse.builder()
                        .personas(p)
                        .build())
                .map(p -> {
                    LOGGER.info(UPDATE_PERSONA_RESPONSE, personaId, p);
                    return ResponseEntity.ok()
                            .body(p);
                });
    }

    @DeleteMapping("{persona-id}")
    public Mono<ResponseEntity<?>> deleteLorebookById(@PathVariable(value = "persona-id") final String personaId) {

        LOGGER.info(DELETE_PERSONA_REQUEST, personaId);
        return Mono.just(personaId)
                .map(id -> {
                    personaRepository.deleteById(id);
                    LOGGER.info(DELETE_PERSONA_RESPONSE, personaId);
                    return ResponseEntity.ok()
                            .build();
                });
    }
}
