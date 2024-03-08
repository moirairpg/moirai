package es.thalesalv.chatrpg.core.application.command.channelconfig;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.usecases.UseCaseHandler;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfig;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigDomainService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateChannelConfigHandler extends UseCaseHandler<UpdateChannelConfig, UpdateChannelConfigResult> {

    private final ChannelConfigDomainService service;

    @Override
    public UpdateChannelConfigResult execute(UpdateChannelConfig command) {

        return mapResult(service.update(command));
    }

    private UpdateChannelConfigResult mapResult(ChannelConfig savedChannelConfig) {

        return UpdateChannelConfigResult.build(savedChannelConfig.getLastUpdateDate());
    }
}
