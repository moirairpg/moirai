package es.thalesalv.chatrpg.adapters.rest.controller;

import java.util.List;

import es.thalesalv.chatrpg.application.service.WorldService;
import es.thalesalv.chatrpg.domain.exception.WorldNotFoundException;
import es.thalesalv.chatrpg.domain.model.api.ApiErrorResponse;
import es.thalesalv.chatrpg.domain.model.api.ApiResponse;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/world")
public class WorldController {

    private final WorldService worldService;

    private static final String RETRIEVE_ALL_WORLDS_REQUEST = "Received request for listing all worlds";
    private static final String RETRIEVE_WORLD_BY_ID_REQUEST = "Received request for retrieving world with id {}";
    private static final String SAVE_WORLD_REQUEST = "Received request for saving world -> {}";
    private static final String UPDATE_WORLD_REQUEST = "Received request for updating world with ID {} -> {}";
    private static final String DELETE_WORLD_REQUEST = "Received request for deleting world with ID {}";
    private static final String DELETE_WORLD_RESPONSE = "Returning response for deleting world with ID {}";
    private static final String ID_CANNOT_BE_NULL = "World ID cannot be null";
    private static final String ERROR_RETRIEVING_WITH_ID = "Error retrieving world with id {}";
    private static final String GENERAL_ERROR_MESSAGE = "An error occurred processing the request";
    private static final String REQUESTED_NOT_FOUND = "The requested world was not found";
    private static final String WORLD_WITH_ID_NOT_FOUND = "Couldn't find requested world with ID {}";
    private static final String ITEM_INSERTED_CANNOT_BE_NULL = "The item to be inserted cannot be null";

    private static final Logger LOGGER = LoggerFactory.getLogger(WorldController.class);

    @GetMapping
    public Mono<ResponseEntity<ApiResponse>> getAllWorlds() {

        LOGGER.info(RETRIEVE_ALL_WORLDS_REQUEST);
        return Mono.just(worldService.retrieveAllWorlds())
                .map(this::buildResponse)
                .onErrorResume(e -> {
                    LOGGER.error("Error retrieving all worlds", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
                });
    }

    @GetMapping("{world-id}")
    public Mono<ResponseEntity<ApiResponse>> getWorldById(@PathVariable(value = "world-id") final String worldId) {

        LOGGER.info(RETRIEVE_WORLD_BY_ID_REQUEST, worldId);
        return Mono.just(worldService.retrieveWorldById(worldId))
                .map(this::buildResponse)
                .onErrorResume(WorldNotFoundException.class, e -> {
                    LOGGER.error(WORLD_WITH_ID_NOT_FOUND, worldId, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.NOT_FOUND, REQUESTED_NOT_FOUND)));
                })
                .onErrorResume(IllegalArgumentException.class, e -> {
                    LOGGER.error(ID_CANNOT_BE_NULL, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.BAD_REQUEST, ID_CANNOT_BE_NULL)));
                })
                .onErrorResume(e -> {
                    LOGGER.error(ERROR_RETRIEVING_WITH_ID, worldId, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
                });
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse>> saveWorld(@RequestBody final World world) {

        LOGGER.info(SAVE_WORLD_REQUEST, world);
        return Mono.just(worldService.saveWorld(world))
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

    @PutMapping("{world-id}")
    public Mono<ResponseEntity<ApiResponse>> updateWorld(@PathVariable(value = "world-id") final String worldId,
            @RequestBody final World world) {

        LOGGER.info(UPDATE_WORLD_REQUEST, worldId, world);
        return Mono.just(worldService.updateWorld(worldId, world))
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

    @DeleteMapping("{world-id}")
    public Mono<ResponseEntity<ApiResponse>> deleteWorld(@PathVariable(value = "world-id") final String worldId) {

        LOGGER.info(DELETE_WORLD_REQUEST, worldId);
        return Mono.just(worldId)
                .map(id -> {
                    worldService.deleteWorld(worldId);
                    LOGGER.info(DELETE_WORLD_RESPONSE, worldId);
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

    private ResponseEntity<ApiResponse> buildResponse(List<World> worlds) {

        LOGGER.info("Sending response for worlds -> {}", worlds);
        final ApiResponse respose = ApiResponse.builder()
                .worlds(worlds)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(respose);
    }

    private ResponseEntity<ApiResponse> buildResponse(World world) {

        LOGGER.info("Sending response for worlds -> {}", world);
        final ApiResponse respose = ApiResponse.builder()
                .world(world)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(respose);
    }

    private ApiResponse buildErrorResponse(HttpStatus status, String message) {

        LOGGER.debug("Building error response object for worlds");
        return ApiResponse.builder()
                .error(ApiErrorResponse.builder()
                        .message(message)
                        .status(status)
                        .build())
                .build();
    }
}
