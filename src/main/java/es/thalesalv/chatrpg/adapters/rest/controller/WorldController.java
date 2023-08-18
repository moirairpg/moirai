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

import es.thalesalv.chatrpg.application.service.WorldService;
import es.thalesalv.chatrpg.domain.exception.InsufficientPermissionException;
import es.thalesalv.chatrpg.domain.exception.WorldNotFoundException;
import es.thalesalv.chatrpg.domain.model.api.ApiErrorResponse;
import es.thalesalv.chatrpg.domain.model.api.ApiResponse;
import es.thalesalv.chatrpg.domain.model.api.PagedResponse;
import es.thalesalv.chatrpg.domain.model.bot.World;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/world")

public class WorldController {

    private final WorldService worldService;

    private static final String RETRIEVE_ALL_WORLDS_REQUEST = "Received request for listing all worlds";
    private static final String SAVE_WORLD_REQUEST = "Received request for saving world -> {}";
    private static final String UPDATE_WORLD_REQUEST = "Received request for updating world with ID {} -> {}";
    private static final String DELETE_WORLD_REQUEST = "Received request for deleting world with ID {}";
    private static final String ERROR_RETRIEVING_WITH_ID = "Error retrieving world with id {}";
    private static final String GENERAL_ERROR_MESSAGE = "An error occurred processing the request";
    private static final String REQUESTED_NOT_FOUND = "The requested world was not found";
    private static final String WORLD_WITH_ID_NOT_FOUND = "Couldn't find requested world with ID {}";
    private static final String NOT_ENOUGH_PERMISSION = "Not enough permissions to modify this world";

    private static final Logger LOGGER = LoggerFactory.getLogger(WorldController.class);

    @GetMapping
    public Mono<ResponseEntity<ApiResponse>> getAllWorlds(@RequestHeader("requester") String requesterUserId) {

        try {
            LOGGER.info(RETRIEVE_ALL_WORLDS_REQUEST);
            final List<World> worlds = worldService.retrieveAllWorlds(requesterUserId);
            return Mono.just(buildResponse(worlds));
        } catch (Exception e) {
            LOGGER.error("Error retrieving all worlds", e);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));

        }
    }

    @GetMapping("paged")
    public Mono<ResponseEntity<PagedResponse>> getAllWorldsByPageWithSearchCriteria(
            @RequestHeader("requester") String requesterUserId,
            @RequestParam(value = "pagenumber") final int pageNumber,
            @RequestParam(value = "itemamount") final int amountOfItems,
            @RequestParam(value = "searchfield") final String searchField,
            @RequestParam(value = "criteria") final String searchCriteria,
            @RequestParam(value = "sortby") final String sortBy) {

        try {
            LOGGER.info("Retrieving {} personas in page {}", amountOfItems, pageNumber);
            final PagedResponse worldPaginationResponse = worldService.retrieveAllWithPagination(requesterUserId,
                    searchCriteria, searchField, pageNumber, amountOfItems, sortBy);

            return Mono.just(ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(worldPaginationResponse));
        } catch (Exception e) {
            LOGGER.error("Error retrieving filtered worlds", e);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponseForPagination(HttpStatus.INTERNAL_SERVER_ERROR,
                            GENERAL_ERROR_MESSAGE)));
        }
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse>> saveWorld(@RequestBody final World world) {

        try {
            LOGGER.info(SAVE_WORLD_REQUEST, world);
            final World createdWorld = worldService.saveWorld(world);
            return Mono.just(buildResponse(createdWorld));
        } catch (Exception e) {
            LOGGER.error("Error creating new world", e);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));

        }
    }

    @PutMapping("{world-id}")
    public Mono<ResponseEntity<ApiResponse>> updateWorld(@RequestHeader("requester") String requesterUserId,
            @PathVariable(value = "world-id") final String worldId, @RequestBody final World world) {

        try {
            LOGGER.info(UPDATE_WORLD_REQUEST, worldId, world);
            final World updatedWorld = worldService.updateWorld(worldId, world, requesterUserId);
            return Mono.just(buildResponse(updatedWorld));
        } catch (InsufficientPermissionException e) {
            LOGGER.error(NOT_ENOUGH_PERMISSION, e);
            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.FORBIDDEN, NOT_ENOUGH_PERMISSION)));
        } catch (WorldNotFoundException e) {
            LOGGER.error(WORLD_WITH_ID_NOT_FOUND, worldId, e);
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.NOT_FOUND, REQUESTED_NOT_FOUND)));
        } catch (Exception e) {
            LOGGER.error(ERROR_RETRIEVING_WITH_ID, worldId, e);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
        }
    }

    @DeleteMapping("{world-id}")
    public Mono<ResponseEntity<ApiResponse>> deleteWorld(@RequestHeader("requester") String requesterUserId,
            @PathVariable(value = "world-id") final String worldId) {

        try {
            LOGGER.info(DELETE_WORLD_REQUEST, worldId);
            worldService.deleteWorld(worldId, requesterUserId);
            return Mono.just(ResponseEntity.ok()
                    .body(ApiResponse.empty()));
        } catch (InsufficientPermissionException e) {
            LOGGER.error(NOT_ENOUGH_PERMISSION, e);
            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.FORBIDDEN, NOT_ENOUGH_PERMISSION)));
        } catch (WorldNotFoundException e) {
            LOGGER.error(WORLD_WITH_ID_NOT_FOUND, worldId, e);
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.NOT_FOUND, REQUESTED_NOT_FOUND)));
        } catch (Exception e) {
            LOGGER.error(ERROR_RETRIEVING_WITH_ID, worldId, e);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
        }
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
