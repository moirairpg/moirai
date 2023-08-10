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

import es.thalesalv.chatrpg.application.service.ChannelConfigService;
import es.thalesalv.chatrpg.domain.exception.ChannelConfigNotFoundException;
import es.thalesalv.chatrpg.domain.exception.InsufficientPermissionException;
import es.thalesalv.chatrpg.domain.model.api.ApiErrorResponse;
import es.thalesalv.chatrpg.domain.model.api.ApiResponse;
import es.thalesalv.chatrpg.domain.model.api.ChannelConfigPage;
import es.thalesalv.chatrpg.domain.model.bot.ChannelConfig;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/channel-config")

public class ChannelConfigController {

    private final ChannelConfigService channelConfigService;

    private static final String RETRIEVE_ALL_CHANNEL_REQUEST = "Received request for listing all channel configs";
    private static final String SAVE_CHANNEL_CONFIG_REQUEST = "Received request for saving channel config -> {}";
    private static final String UPDATE_CHANNEL_CONFIG_REQUEST = "Received request for updating channel config with ID {} -> {}";
    private static final String DELETE_CHANNEL_CONFIG_REQUEST = "Received request for deleting channel config with ID {}";
    private static final String GENERAL_ERROR_MESSAGE = "An error occurred processing the request";
    private static final String REQUESTED_NOT_FOUND = "The requested channel config was not found";
    private static final String CHCONF_WITH_ID_NOT_FOUND = "Couldn't find requested channel config with ID {}";
    private static final String NOT_ENOUGH_PERMISSION = "Not enough permissions to modify this channel config";

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelConfigController.class);

    @GetMapping
    public Mono<ResponseEntity<ApiResponse>> getAllChannelConfigs(@RequestHeader("requester") String requesterUserId) {

        try {
            LOGGER.info(RETRIEVE_ALL_CHANNEL_REQUEST);
            final List<ChannelConfig> channelConfigs = channelConfigService.retrieveAllChannelConfigs(requesterUserId);
            return Mono.just(buildResponse(channelConfigs));
        } catch (Exception e) {
            LOGGER.error("Error retrieving all channel configurations", e);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
        }
    }

    @GetMapping("paged")
    public Mono<ResponseEntity<ChannelConfigPage>> getAllChannelsByPageWithSearchCriteria(
            @RequestHeader("requester") String requesterUserId,
            @RequestParam(value = "pagenumber") final int pageNumber,
            @RequestParam(value = "itemamount") final int amountOfItems,
            @RequestParam(value = "searchfield") final String searchField,
            @RequestParam(value = "criteria") final String searchCriteria) {

        try {
            LOGGER.info("Retrieving {} channel configurations in page {}", amountOfItems, pageNumber);
            final ChannelConfigPage channelConfigPaginationResponse = channelConfigService
                    .retrieveAllWithPagination(requesterUserId, searchCriteria, searchField, pageNumber, amountOfItems);

            return Mono.just(ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(channelConfigPaginationResponse));
        } catch (Exception e) {
            LOGGER.error("Error retrieving all channel configurations", e);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponseForPagination(HttpStatus.INTERNAL_SERVER_ERROR,
                            GENERAL_ERROR_MESSAGE)));
        }
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse>> saveChannelConfig(@RequestBody final ChannelConfig channelConfig) {

        try {
            LOGGER.info(SAVE_CHANNEL_CONFIG_REQUEST, channelConfig);
            final ChannelConfig newChannelConfig = channelConfigService.saveChannelConfig(channelConfig);
            return Mono.just(buildResponse(newChannelConfig));
        } catch (Exception e) {
            LOGGER.error(GENERAL_ERROR_MESSAGE, e);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
        }
    }

    @PutMapping("{channel-config-id}")
    public Mono<ResponseEntity<ApiResponse>> updateChannelConfigById(@RequestHeader("requester") String requesterUserId,
            @PathVariable(value = "channel-config-id") final String channelConfigId,
            @RequestBody final ChannelConfig channelConfig) {

        try {
            LOGGER.info(UPDATE_CHANNEL_CONFIG_REQUEST, channelConfigId, channelConfig);
            final ChannelConfig updatedChannelConfig = channelConfigService.updateChannelConfig(channelConfigId,
                    channelConfig, requesterUserId);

            return Mono.just(buildResponse(updatedChannelConfig));
        } catch (InsufficientPermissionException e) {
            LOGGER.error(NOT_ENOUGH_PERMISSION, e);
            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.FORBIDDEN, NOT_ENOUGH_PERMISSION)));
        } catch (ChannelConfigNotFoundException e) {
            LOGGER.error(CHCONF_WITH_ID_NOT_FOUND, channelConfigId, e);
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.NOT_FOUND, REQUESTED_NOT_FOUND)));
        } catch (Exception e) {
            LOGGER.error(GENERAL_ERROR_MESSAGE, e);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
        }
    }

    @DeleteMapping("{channel-config-id}")
    public Mono<ResponseEntity<ApiResponse>> deleteChannelConfigById(@RequestHeader("requester") String requesterUserId,
            @PathVariable(value = "channel-config-id") final String channelConfigId) {

        try {
            LOGGER.info(DELETE_CHANNEL_CONFIG_REQUEST, channelConfigId);
            channelConfigService.deleteChannelConfig(channelConfigId, requesterUserId);
            return Mono.just(ResponseEntity.ok()
                    .body(ApiResponse.empty()));
        } catch (InsufficientPermissionException e) {
            LOGGER.error(NOT_ENOUGH_PERMISSION, e);
            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.FORBIDDEN, NOT_ENOUGH_PERMISSION)));
        } catch (ChannelConfigNotFoundException e) {
            LOGGER.error(CHCONF_WITH_ID_NOT_FOUND, channelConfigId, e);
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.NOT_FOUND, REQUESTED_NOT_FOUND)));
        } catch (Exception e) {
            LOGGER.error(GENERAL_ERROR_MESSAGE, e);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
        }
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

    private ResponseEntity<ApiResponse> buildResponse(ChannelConfig channelConfig) {

        LOGGER.info("Sending response for channel configs -> {}", channelConfig);
        final ApiResponse respose = ApiResponse.builder()
                .channelConfig(channelConfig)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(respose);
    }

    private ChannelConfigPage buildErrorResponseForPagination(HttpStatus status, String message) {

        LOGGER.debug("Building error response object for channel configs");
        return ChannelConfigPage.builder()
                .error(ApiErrorResponse.builder()
                        .message(message)
                        .status(status)
                        .build())
                .build();
    }

    private ApiResponse buildErrorResponse(HttpStatus status, String message) {

        LOGGER.debug("Building error response object for channel configs");
        return ApiResponse.builder()
                .error(ApiErrorResponse.builder()
                        .message(message)
                        .status(status)
                        .build())
                .build();
    }
}
