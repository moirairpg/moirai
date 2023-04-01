package es.thalesalv.chatrpg.adapters.rest.controller;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import es.thalesalv.chatrpg.adapters.data.repository.WorldRepository;
import es.thalesalv.chatrpg.application.mapper.world.WorldDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.world.WorldEntityToDTO;
import es.thalesalv.chatrpg.domain.model.api.ApiResponse;
import es.thalesalv.chatrpg.domain.model.chconf.World;
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
@RequestMapping("/world")
public class WorldController {

    private final WorldDTOToEntity worldDTOToEntity;
    private final WorldEntityToDTO worldEntityToDTO;
    private final WorldRepository worldRepository;

    private static final String RETRIEVE_ALL_WORLDS_REQUEST = "Received request for listing all worlds";
    private static final String RETRIEVE_ALL_WORLDS_RESPONSE = "Returning response for listing all worlds request -> {}";
    private static final String RETRIEVE_WORLD_BY_ID_REQUEST = "Received request for retrieving world with id {}";
    private static final String RETRIEVE_WORLD_BY_ID_RESPONSE = "Returning response for listing world with id {} request -> {}";
    private static final String SAVE_WORLD_REQUEST = "Received request for saving world -> {}";
    private static final String SAVE_WORLD_RESPONSE = "Returning response for saving world request -> {}";
    private static final String UPDATE_WORLD_REQUEST = "Received request for updating world with ID {} -> {}";
    private static final String UPDATE_WORLD_RESPONSE = "Returning response for updating world with id {} request -> {}";
    private static final String DELETE_WORLD_REQUEST = "Received request for deleting world with ID {}";
    private static final String DELETE_WORLD_RESPONSE = "Returning response for deleting world with ID {}";

    private static final Logger LOGGER = LoggerFactory.getLogger(WorldController.class);

    @GetMapping
    public Mono<ResponseEntity<ApiResponse>> getAllSettings() {

        LOGGER.info(RETRIEVE_ALL_WORLDS_REQUEST);
        return Mono.just(worldRepository.findAll())
                .map(p -> p.stream()
                        .map(worldEntityToDTO)
                        .collect(Collectors.toList()))
                .map(p -> ApiResponse.builder()
                        .worlds(p)
                        .build())
                .map(p -> {
                    LOGGER.info(RETRIEVE_ALL_WORLDS_RESPONSE, p);
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(p);
                });
    }

    @GetMapping("{model-settings-id}")
    public Mono<ResponseEntity<ApiResponse>> getWorldById(
            @PathVariable(value = "model-settings-id") final String worldId) {

        LOGGER.info(RETRIEVE_WORLD_BY_ID_REQUEST, worldId);
        return Mono.just(worldRepository.findById(worldId))
                .map(p -> p.map(worldEntityToDTO)
                        .orElseThrow(() -> new RuntimeException()))
                .map(p -> Stream.of(p)
                        .collect(Collectors.toList()))
                .map(p -> ApiResponse.builder()
                        .worlds(p)
                        .build())
                .map(p -> {
                    LOGGER.info(RETRIEVE_WORLD_BY_ID_RESPONSE, worldId, p);
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(p);
                });
    }

    @PutMapping
    public Mono<ResponseEntity<ApiResponse>> saveWorld(final World world) {

        LOGGER.info(SAVE_WORLD_REQUEST, world);
        return Mono.just(worldDTOToEntity.apply(world))
                .map(worldRepository::save)
                .map(p -> Stream.of(p)
                        .map(worldEntityToDTO)
                        .collect(Collectors.toList()))
                .map(p -> ApiResponse.builder()
                        .worlds(p)
                        .build())
                .map(p -> {
                    LOGGER.info(SAVE_WORLD_RESPONSE, p);
                    return ResponseEntity.ok()
                            .body(p);
                });
    }

    @PatchMapping("{model-settings-id}")
    public Mono<ResponseEntity<ApiResponse>> updateWorldById(
            @PathVariable(value = "model-settings-id") final String worldId, final World world) {

        LOGGER.info(UPDATE_WORLD_REQUEST, worldId, world);
        return Mono.just(worldDTOToEntity.apply(world))
                .map(p -> {
                    p.setId(worldId);
                    return worldRepository.save(p);
                })
                .map(p -> Stream.of(p)
                        .map(worldEntityToDTO)
                        .collect(Collectors.toList()))
                .map(p -> ApiResponse.builder()
                        .worlds(p)
                        .build())
                .map(p -> {
                    LOGGER.info(UPDATE_WORLD_RESPONSE, worldId, p);
                    return ResponseEntity.ok()
                            .body(p);
                });
    }

    @DeleteMapping("{model-settings-id}")
    public Mono<ResponseEntity<?>> deleteWorldById(@PathVariable(value = "model-settings-id") final String worldId) {

        LOGGER.info(DELETE_WORLD_REQUEST, worldId);
        return Mono.just(worldId)
                .map(id -> {
                    worldRepository.deleteById(id);
                    LOGGER.info(DELETE_WORLD_RESPONSE, worldId);
                    return ResponseEntity.ok()
                            .build();
                });
    }
}
