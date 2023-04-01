package es.thalesalv.chatrpg.adapters.rest.controller;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import es.thalesalv.chatrpg.adapters.data.repository.ChannelRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelEntityToDTO;
import es.thalesalv.chatrpg.domain.model.api.ApiResponse;
import es.thalesalv.chatrpg.domain.model.chconf.Channel;
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
@RequestMapping("/channel-config")
public class ChannelConfigController {

    private final ChannelDTOToEntity channelDTOToEntity;
    private final ChannelEntityToDTO channelEntityToDTO;
    private final ChannelRepository channelRepository;

    private static final String RETRIEVE_ALL_CHANNEL_CONFIGS_REQUEST = "Received request for listing all channel configs";
    private static final String RETRIEVE_ALL_CHANNEL_CONFIGS_RESPONSE = "Returning response for listing all channel configs request -> {}";
    private static final String RETRIEVE_CHANNEL_CONFIG_BY_ID_REQUEST = "Received request for retrieving channel config with Discord channel id {}";
    private static final String RETRIEVE_CHANNEL_CONFIG_BY_ID_RESPONSE = "Returning response for retrieving channel config with id {} request -> {}";
    private static final String SAVE_CHANNEL_CONFIG_REQUEST = "Received request for saving channel config -> {}";
    private static final String SAVE_CHANNEL_CONFIG_RESPONSE = "Returning response for saving channel config request -> {}";
    private static final String UPDATE_CHANNEL_CONFIG_REQUEST = "Received request for updating channel config with ID {} -> {}";
    private static final String UPDATE_CHANNEL_CONFIG_RESPONSE = "Returning response for updating channel config with id {} request -> {}";
    private static final String DELETE_CHANNEL_CONFIG_REQUEST = "Received request for deleting channel config with ID {}";
    private static final String DELETE_CHANNEL_CONFIG_RESPONSE = "Returning response for deleting lorebook with ID {}";

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelConfigController.class);

    @GetMapping
    public Mono<ResponseEntity<ApiResponse>> getAllChannelConfigs() {

        LOGGER.info(RETRIEVE_ALL_CHANNEL_CONFIGS_REQUEST);
        return Mono.just(channelRepository.findAll())
                .map(c -> c.stream()
                        .map(channelEntityToDTO)
                        .collect(Collectors.toList()))
                .map(c -> ApiResponse.builder()
                        .channels(c)
                        .build())
                .map(c -> {
                    LOGGER.info(RETRIEVE_ALL_CHANNEL_CONFIGS_RESPONSE, c);
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(c);
                });
    }

    @GetMapping("{channel-id}")
    public Mono<ResponseEntity<ApiResponse>> getChannelConfigById(
            @PathVariable(value = "channel-id") final String channelId) {

        LOGGER.info(RETRIEVE_CHANNEL_CONFIG_BY_ID_REQUEST, channelId);
        return Mono.just(channelRepository.findById(channelId))
                .map(configEntity -> configEntity.map(channelEntityToDTO)
                        .orElseThrow(() -> new RuntimeException()))
                .map(c -> Stream.of(c)
                        .collect(Collectors.toList()))
                .map(c -> ApiResponse.builder()
                        .channels(c)
                        .build())
                .map(c -> {
                    LOGGER.info(RETRIEVE_CHANNEL_CONFIG_BY_ID_RESPONSE, channelId, c);
                    return ResponseEntity.ok()
                            .body(c);
                });
    }

    @PutMapping
    public Mono<ResponseEntity<ApiResponse>> saveChannelConfig(final Channel channel) {

        LOGGER.info(SAVE_CHANNEL_CONFIG_REQUEST, channel);
        return Mono.just(channelDTOToEntity.apply(channel))
                .map(channelRepository::save)
                .map(c -> Stream.of(c)
                        .map(channelEntityToDTO)
                        .collect(Collectors.toList()))
                .map(c -> ApiResponse.builder()
                        .channels(c)
                        .build())
                .map(c -> {
                    LOGGER.info(SAVE_CHANNEL_CONFIG_RESPONSE, c);
                    return ResponseEntity.ok()
                            .body(c);
                });
    }

    @PatchMapping("{channel-id}")
    public Mono<ResponseEntity<ApiResponse>> updateChannelConfigById(
            @PathVariable(value = "channel-id") final String channelId, final Channel channel) {

        LOGGER.info(UPDATE_CHANNEL_CONFIG_REQUEST, channelId, channel);
        return Mono.just(channelDTOToEntity.apply(channel))
                .map(c -> {
                    c.setId(channelId);
                    return channelRepository.save(c);
                })
                .map(c -> Stream.of(c)
                        .map(channelEntityToDTO)
                        .collect(Collectors.toList()))
                .map(c -> ApiResponse.builder()
                        .channels(c)
                        .build())
                .map(c -> {
                    LOGGER.info(UPDATE_CHANNEL_CONFIG_RESPONSE, channelId, c);
                    return ResponseEntity.ok()
                            .body(c);
                });
    }

    @DeleteMapping("{channel-id}")
    public Mono<ResponseEntity<?>> deleteChannelConfigById(@PathVariable(value = "channel-id") final String channelId) {

        LOGGER.info(DELETE_CHANNEL_CONFIG_REQUEST, channelId);
        return Mono.just(channelId)
                .map(id -> {
                    channelRepository.deleteById(id);
                    LOGGER.info(DELETE_CHANNEL_CONFIG_RESPONSE, channelId);
                    return ResponseEntity.ok()
                            .build();
                });
    }
}
