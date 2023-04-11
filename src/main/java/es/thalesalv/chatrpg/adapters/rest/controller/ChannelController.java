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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.thalesalv.chatrpg.application.service.ChannelConfigService;
import es.thalesalv.chatrpg.domain.exception.ChannelConfigNotFoundException;
import es.thalesalv.chatrpg.domain.model.api.ApiErrorResponse;
import es.thalesalv.chatrpg.domain.model.api.ApiResponse;
import es.thalesalv.chatrpg.domain.model.chconf.Channel;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/channel")
public class ChannelController {

    private final ChannelConfigService channelConfigService;

    private static final String RETRIEVE_ALL_CHANNEL_REQUEST = "Received request for listing all configs";
    private static final String RETRIEVE_CHANNEL_BY_ID_REQUEST = "Received request for retrieving config with Discord id {}";
    private static final String SAVE_CHANNEL_REQUEST = "Received request for saving config -> {}";
    private static final String UPDATE_CHANNEL_REQUEST = "Received request for updating config with ID {} -> {}";
    private static final String DELETE_CHANNEL_REQUEST = "Received request for deleting config with ID {}";
    private static final String DELETE_CHANNEL_RESPONSE = "Returning response for deleting lorebook with ID {}";
    private static final String GENERAL_ERROR_MESSAGE = "An error occurred processing the request";
    private static final String REQUESTED_NOT_FOUND = "The requested configuration was not found";
    private static final String CONFIG_WITH_ID_NOT_FOUND = "Couldn't find requested configuration with ID {}";
    private static final String ID_CANNOT_BE_NULL = "Channel ID cannot be null";
    private static final String ERROR_RETRIEVING_WITH_ID = "Error retrieving configuration with id {}";
    private static final String ITEM_INSERTED_CANNOT_BE_NULL = "The item to be inserted cannot be null";

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelController.class);

    @GetMapping
    public Mono<ResponseEntity<ApiResponse>> getAllChannelConfigs() {

        LOGGER.info(RETRIEVE_ALL_CHANNEL_REQUEST);
        return Mono.just(channelConfigService.retrieveAllChannels())
                .map(this::buildResponse)
                .onErrorResume(e -> {
                    LOGGER.error("Error retrieving all configurations", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
                });
    }

    @GetMapping("{channel-id}")
    public Mono<ResponseEntity<ApiResponse>> getChannelConfigByChannelId(
            @PathVariable(value = "channel-id") final String channelId) {

        LOGGER.info(RETRIEVE_CHANNEL_BY_ID_REQUEST, channelId);
        return Mono.just(channelConfigService.retrieveChannelConfigByChannelId(channelId))
                .map(this::buildResponse)
                .onErrorResume(ChannelConfigNotFoundException.class, e -> {
                    LOGGER.error(CONFIG_WITH_ID_NOT_FOUND, channelId, e);
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
                    LOGGER.error(ERROR_RETRIEVING_WITH_ID, channelId, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
                });
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse>> saveChannelConfig(@RequestBody final Channel channel) {

        LOGGER.info(SAVE_CHANNEL_REQUEST, channel);
        return Mono.just(channelConfigService.saveChannel(channel))
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

    @PutMapping("{channel-id}")
    public Mono<ResponseEntity<ApiResponse>> updateChannelConfig(
            @PathVariable(value = "channel-id") final String channelId, @RequestBody final Channel channel) {

        LOGGER.info(UPDATE_CHANNEL_REQUEST, channelId, channel);
        return Mono.just(channelConfigService.updateChannel(channelId, channel))
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

    @DeleteMapping("{channel-id}")
    public Mono<ResponseEntity<ApiResponse>> deleteChannelConfig(
            @PathVariable(value = "channel-id") final String channelId) {

        LOGGER.info(DELETE_CHANNEL_REQUEST, channelId);
        return Mono.just(channelId)
                .map(id -> {
                    channelConfigService.deleteChannel(channelId);
                    LOGGER.info(DELETE_CHANNEL_RESPONSE, channelId);
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

    private ResponseEntity<ApiResponse> buildResponse(List<Channel> channels) {

        LOGGER.info("Sending response for channels -> {}", channels);
        final ApiResponse respose = ApiResponse.builder()
                .channels(channels)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(respose);
    }

    private ResponseEntity<ApiResponse> buildResponse(Channel channel) {

        LOGGER.info("Sending response for channels -> {}", channel);
        final ApiResponse respose = ApiResponse.builder()
                .channel(channel)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(respose);
    }

    private ApiResponse buildErrorResponse(HttpStatus status, String message) {

        LOGGER.debug("Building error response object for channels");
        return ApiResponse.builder()
                .error(ApiErrorResponse.builder()
                        .message(message)
                        .status(status)
                        .build())
                .build();
    }
}
