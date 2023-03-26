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
    public Channel apply(ChannelEntity t) {

        final ChannelConfig channelConfig = channelConfigEntityToDTO.apply(t.getChannelConfig());
        return Channel.builder()
                .id(t.getId())
                .channelId(t.getChannelId())
                .channelConfig(channelConfig)
                .build();
    }
}