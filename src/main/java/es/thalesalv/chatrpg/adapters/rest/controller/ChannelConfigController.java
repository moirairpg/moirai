package es.thalesalv.chatrpg.adapters.rest.controller;

import java.util.List;

import es.thalesalv.chatrpg.application.service.api.ChannelConfigService;
import es.thalesalv.chatrpg.domain.exception.ChannelConfigNotFoundException;
import es.thalesalv.chatrpg.domain.model.api.ApiError;
import es.thalesalv.chatrpg.domain.model.api.ApiResponse;
import es.thalesalv.chatrpg.domain.model.chconf.ChannelConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/channel-config")
public class ChannelConfigController {

    private final ChannelConfigService channelConfigService;

    private static final String RETRIEVE_ALL_CHANNEL_REQUEST = "Received request for listing all channel configs";
    private static final String RETRIEVE_CHANNEL_CONFIG_BY_ID_REQUEST = "Received request for retrieving channel config with Discord channel id {}";
    private static final String SAVE_CHANNEL_CONFIG_REQUEST = "Received request for saving channel config -> {}";
    private static final String UPDATE_CHANNEL_CONFIG_REQUEST = "Received request for updating channel config with ID {} -> {}";
    private static final String DELETE_CHANNEL_CONFIG_REQUEST = "Received request for deleting channel config with ID {}";
    private static final String DELETE_CHANNEL_CONFIG_RESPONSE = "Returning response for deleting lorebook with ID {}";
    private static final String GENERAL_ERROR_MESSAGE = "An error occurred processing the request";
    private static final String REQUESTED_CONFIG_NOT_FOUND = "The requested channel configuration was not found";
    private static final String CONFIG_WITH_ID_NOT_FOUND = "Couldn't find requested channel configuration with ID {}";
    private static final String ID_CANNOT_BE_NULL = "Channel ID cannot be null";
    private static final String ERROR_RETRIEVING_WITH_ID = "Error retrieving channel configuration with id {}";
    private static final String ITEM_INSERTED_CANNOT_BE_NULL = "The item to be inserted cannot be null";

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelConfigController.class);

    @GetMapping
    public Mono<ResponseEntity<ApiResponse>> getAllChannelConfigs() {

        LOGGER.info(RETRIEVE_ALL_CHANNEL_REQUEST);
        return channelConfigService.retrieveAllChannelConfigs()
                .map(this::buildResponse)
                .onErrorResume(e -> {
                    LOGGER.error("Error retrieving all channel configurations", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
                });
    }

    @GetMapping("{channel-config-id}")
    public Mono<ResponseEntity<ApiResponse>> getChannelConfigById(
            @PathVariable(value = "channel-config-id") final String channelConfigId) {

        LOGGER.info(RETRIEVE_CHANNEL_CONFIG_BY_ID_REQUEST, channelConfigId);
        return channelConfigService.retrieveChannelConfigById(channelConfigId)
                .map(this::buildResponse)
                .onErrorResume(ChannelConfigNotFoundException.class, e -> {
                    LOGGER.error(CONFIG_WITH_ID_NOT_FOUND, channelConfigId, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.NOT_FOUND, REQUESTED_CONFIG_NOT_FOUND)));
                })
                .onErrorResume(IllegalArgumentException.class, e -> {
                    LOGGER.error(ID_CANNOT_BE_NULL, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.BAD_REQUEST, ID_CANNOT_BE_NULL)));
                })
                .onErrorResume(e -> {
                    LOGGER.error(ERROR_RETRIEVING_WITH_ID, channelConfigId, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
                });
    }

    @PutMapping
    public Mono<ResponseEntity<ApiResponse>> saveChannelConfig(@RequestBody final ChannelConfig channelConfig) {

        LOGGER.info(SAVE_CHANNEL_CONFIG_REQUEST, channelConfig);
        return channelConfigService.saveChannelConfig(channelConfig)
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

    @PatchMapping("{channel-config-id}")
    public Mono<ResponseEntity<ApiResponse>> updateChannelConfigById(
            @PathVariable(value = "channel-config-id") final String channelConfigId,
            @RequestBody final ChannelConfig channelConfig) {

        LOGGER.info(UPDATE_CHANNEL_CONFIG_REQUEST, channelConfigId, channelConfig);
        return channelConfigService.updateChannelConfig(channelConfigId, channelConfig)
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

    @DeleteMapping("{channel-config-id}")
    public Mono<ResponseEntity<ApiResponse>> deleteChannelConfigById(
            @PathVariable(value = "channel-config-id") final String channelConfigId) {

        LOGGER.info(DELETE_CHANNEL_CONFIG_REQUEST, channelConfigId);
        return Mono.just(channelConfigId)
                .map(id -> {
                    channelConfigService.deleteChannelConfig(channelConfigId);
                    LOGGER.info(DELETE_CHANNEL_CONFIG_RESPONSE, channelConfigId);
                    return ResponseEntity.ok()
                            .body(ApiResponse.builder()
                                    .build());
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

    private ResponseEntity<ApiResponse> buildResponse(List<ChannelConfig> channelConfigs) {

        LOGGER.info("Sending response for channel configs -> {}", channelConfigs);
        final ApiResponse respose = ApiResponse.builder()
                .channelConfigs(channelConfigs)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(respose);
    }

    private ApiResponse buildErrorResponse(HttpStatus status, String message) {

        LOGGER.debug("Building error response object for channel configs");
        return ApiResponse.builder()
                .error(ApiError.builder()
                        .message(message)
                        .status(status)
                        .build())
                .build();
    }
}
