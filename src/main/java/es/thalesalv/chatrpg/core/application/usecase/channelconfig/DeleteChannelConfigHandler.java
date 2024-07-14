package es.thalesalv.chatrpg.core.application.usecase.channelconfig;

import org.apache.commons.lang3.StringUtils;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.request.DeleteChannelConfig;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigService;

@UseCaseHandler
public class DeleteChannelConfigHandler extends AbstractUseCaseHandler<DeleteChannelConfig, Void> {

    private static final String ID_CANNOT_BE_NULL_OR_EMPTY = "Channel config ID cannot be null or empty";

    private final ChannelConfigService domainService;

    public DeleteChannelConfigHandler(ChannelConfigService domainService) {
        this.domainService = domainService;
    }

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
