package es.thalesalv.chatrpg.core.application.query.channelconfig;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.cqrs.query.QueryHandler;
import es.thalesalv.chatrpg.common.exception.AssetNotFoundException;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfig;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GetChannelConfigByIdHandler extends QueryHandler<GetChannelConfigById, GetChannelConfigResult> {

    private final ChannelConfigRepository repository;

    @Override
    public GetChannelConfigResult handle(GetChannelConfigById query) {

        ChannelConfig channelConfig = repository.findById(query.getId())
                .orElseThrow(() -> new AssetNotFoundException("ChannelConfig not found"));

        return mapResult(channelConfig);
    }

    private GetChannelConfigResult mapResult(ChannelConfig channelConfig) {

        return GetChannelConfigResult.builder()
                .id(channelConfig.getId())
                .build();
    }
}
