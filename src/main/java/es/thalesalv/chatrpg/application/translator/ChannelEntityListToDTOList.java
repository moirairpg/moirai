package es.thalesalv.chatrpg.application.translator;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.db.entity.ChannelEntity;
import es.thalesalv.chatrpg.domain.model.openai.dto.Channel;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChannelEntityListToDTOList implements Function<List<ChannelEntity>, List<Channel>> {

    private final ChannelEntityToDTO channelEntityToDTO;

    @Override
    public List<Channel> apply(List<ChannelEntity> t) {

        return t.stream()
                .map(channel -> {
                    return channelEntityToDTO.apply(channel);
                })
                .collect(Collectors.toList());
    }
}
