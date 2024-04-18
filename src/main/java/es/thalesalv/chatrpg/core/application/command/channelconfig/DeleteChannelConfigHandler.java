package es.thalesalv.chatrpg.core.application.command.channelconfig;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.usecases.UseCaseHandler;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DeleteChannelConfigHandler extends UseCaseHandler<DeleteChannelConfig, Void> {

    private static final String ID_CANNOT_BE_NULL_OR_EMPTY = "Channel config ID cannot be null or empty";

    private final ChannelConfigService domainService;

    @Override
    public void validate(DeleteChannelConfig command) {

        if (StringUtils.isBlank(command.getId())) {
            throw new IllegalArgumentException(ID_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    @Override
    public Void execute(DeleteChannelConfig command) {

        domainService.deleteChannelConfig(command);

        return null;
    }
}
