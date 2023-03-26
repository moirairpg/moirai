package es.thalesalv.chatrpg.application.mapper.chconfig;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.ChannelConfigEntity;
import es.thalesalv.chatrpg.adapters.data.entity.ChannelEntity;
import es.thalesalv.chatrpg.domain.model.chconf.Channel;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChannelDTOToEntity implements Function<Channel, ChannelEntity> {

    private final ChannelConfigDTOToEntity channelConfigDTOToEntity;

    @Override
    public ChannelEntity apply(Channel t) {

        final ChannelConfigEntity channelConfig = channelConfigDTOToEntity.apply(t.getChannelConfig());
        return ChannelEntity.builder()
                .id(t.getId())
                .channelId(t.getChannelId())
                .channelConfig(channelConfig)
                .build();
    }
}