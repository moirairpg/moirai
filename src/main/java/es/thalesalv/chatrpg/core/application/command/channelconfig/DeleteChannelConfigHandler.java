package es.thalesalv.chatrpg.core.application.command.channelconfig;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.cqrs.command.CommandHandler;
import es.thalesalv.chatrpg.common.exception.AssetNotFoundException;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DeleteChannelConfigHandler extends CommandHandler<DeleteChannelConfig, Void> {

    private static final String ID_CANNOT_BE_NULL_OR_EMPTY = "Channel config ID cannot be null or empty";
    private static final String CANNOT_DELETE_NOT_FOUND = "Cannot delete non-existing channel config";

    private final ChannelConfigRepository repository;

    @Override
    public void validate(DeleteChannelConfig command) {

        if (StringUtils.isBlank(command.getId())) {
            throw new IllegalArgumentException(ID_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    @Override
    public Void handle(DeleteChannelConfig command) {

        // TODO add ownership check
        repository.findById(command.getId())
                .orElseThrow(() -> new AssetNotFoundException(CANNOT_DELETE_NOT_FOUND));

        repository.deleteById(command.getId());

        return null;
    }
}
