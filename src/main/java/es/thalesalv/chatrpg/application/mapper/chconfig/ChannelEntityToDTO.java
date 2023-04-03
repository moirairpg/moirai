package es.thalesalv.chatrpg.application.mapper.chconfig;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.ChannelEntity;
import es.thalesalv.chatrpg.domain.model.chconf.Channel;
import es.thalesalv.chatrpg.domain.model.chconf.ChannelConfig;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChannelEntityToDTO implements Function<ChannelEntity, Channel> {

    private final ChannelConfigEntityToDTO channelConfigEntityToDTO;

    @Override
    public Channel apply(ChannelEntity channelEntity) {

        final ChannelConfig channelConfig = channelConfigEntityToDTO.apply(channelEntity.getChannelConfig());
        return Channel.builder()
                .id(channelEntity.getId())
                .channelConfig(channelConfig)
                .build();
    }
}