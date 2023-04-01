package es.thalesalv.chatrpg.adapters.rest.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.thalesalv.chatrpg.adapters.data.repository.ChannelRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelEntityToDTO;
import es.thalesalv.chatrpg.domain.model.chconf.Channel;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/channel-config")
public class ChannelConfigController {

    private final ChannelEntityToDTO channelEntityToDTO;

    private final ChannelRepository channelRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelConfigController.class);

    @GetMapping
    public Mono<List<Channel>> getAllChannelConfigs() {

        LOGGER.debug("Received request for listing all channel configs");
        return Mono.just(channelRepository.findAll())
                .map(configEntities -> configEntities.stream()
                        .map(channelEntityToDTO)
                        .collect(Collectors.toList()));
    }

    @GetMapping("{channel-id}")
    public Mono<Channel> getChannelConfigById(@PathVariable(value = "channel-id") final String channelId) {

        LOGGER.debug("Received request for retrieving channel config with Discord channel id {}", channelId);
        return Mono.just(channelRepository.findById(channelId))
                .map(configEntity -> configEntity.map(channelEntityToDTO)
                        .orElseThrow(() -> new RuntimeException()));
    }
}
