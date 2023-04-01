package es.thalesalv.chatrpg.application.service.api;

import java.util.List;
import java.util.stream.Stream;

import es.thalesalv.chatrpg.adapters.data.repository.ChannelConfigRepository;
import es.thalesalv.chatrpg.adapters.data.repository.ChannelRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelConfigDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelConfigEntityToDTO;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelEntityToDTO;
import es.thalesalv.chatrpg.domain.exception.ChannelConfigNotFoundException;
import es.thalesalv.chatrpg.domain.model.chconf.Channel;
import es.thalesalv.chatrpg.domain.model.chconf.ChannelConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChannelConfigService {

    private final ChannelDTOToEntity channelDTOToEntity;
    private final ChannelEntityToDTO channelEntityToDTO;
    private final ChannelConfigDTOToEntity channelConfigDTOToEntity;
    private final ChannelConfigEntityToDTO channelConfigEntityToDTO;

    private final ChannelRepository channelRepository;
    private final ChannelConfigRepository channelConfigRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelConfigService.class);

    public Mono<List<Channel>> retrieveAllChannels() {

        LOGGER.debug("Retrieving channel data from request");
        return Mono.just(channelRepository.findAll())
                .map(channels -> channels.stream()
                        .map(channelEntityToDTO)
                        .toList());
    }

    public Mono<List<Channel>> retrieveChannelConfigsByChannelId(final String channelId) {

        LOGGER.debug("Retrieving channel by ID data from request");
        return Mono.just(channelRepository.findById(channelId)
                .orElseThrow(ChannelConfigNotFoundException::new))
                .map(channel -> Stream.of(channel)
                        .map(channelEntityToDTO)
                        .toList());
    }

    public Mono<List<Channel>> saveChannel(final Channel channel) {

        LOGGER.debug("Saving channel data from request");
        return Mono.just(channelDTOToEntity.apply(channel))
                .map(channelRepository::save)
                .map(channelEntityToDTO)
                .map(c -> Stream.of(c)
                        .toList());
    }

    public Mono<List<Channel>> updateChannel(final String channelId, final Channel channel) {

        LOGGER.debug("Updating channel data from request");
        return Mono.just(channelDTOToEntity.apply(channel))
                .map(c -> {
                    c.setId(channelId);
                    return channelRepository.save(c);
                })
                .map(channelEntityToDTO)
                .map(c -> Stream.of(c)
                        .toList());
    }

    public void deleteChannel(final String channelId) {

        LOGGER.debug("Deleting channel data from request");
        channelRepository.deleteById(channelId);
    }

    public Mono<List<ChannelConfig>> retrieveAllChannelConfigs() {

        LOGGER.debug("Retrieving all available channel configs");
        return Mono.just(channelConfigRepository.findAll())
                .map(channels -> channels.stream()
                        .map(channelConfigEntityToDTO)
                        .toList());
    }

    public Mono<List<ChannelConfig>> retrieveChannelConfigById(final String channelConfigId) {

        LOGGER.debug("Retrieving channel config by ID data from request");
        return Mono.just(channelConfigRepository.findById(channelConfigId)
                .orElseThrow(ChannelConfigNotFoundException::new))
                .map(channel -> Stream.of(channel)
                        .map(channelConfigEntityToDTO)
                        .toList());
    }

    public Mono<List<ChannelConfig>> saveChannelConfig(final ChannelConfig channelConfig) {

        LOGGER.debug("Saving channel config data from request");
        return Mono.just(channelConfigDTOToEntity.apply(channelConfig))
                .map(channelConfigRepository::save)
                .map(channelConfigEntityToDTO)
                .map(c -> Stream.of(c)
                        .toList());
    }

    public Mono<List<ChannelConfig>> updateChannelConfig(final String channelConfigId,
            final ChannelConfig channelConfig) {

        LOGGER.debug("Updating channel config data from request");
        return Mono.just(channelConfigDTOToEntity.apply(channelConfig))
                .map(c -> {
                    c.setId(channelConfigId);
                    return channelConfigRepository.save(c);
                })
                .map(channelConfigEntityToDTO)
                .map(c -> Stream.of(c)
                        .toList());
    }

    public void deleteChannelConfig(final String channelConfigId) {

        LOGGER.debug("Deleting channel config data from request");
        channelRepository.deleteById(channelConfigId);
    }
}
