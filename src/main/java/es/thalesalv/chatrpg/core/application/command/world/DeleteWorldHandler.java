package es.thalesalv.chatrpg.core.application.command.world;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.usecases.UseCaseHandler;
import es.thalesalv.chatrpg.core.domain.world.WorldService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DeleteWorldHandler extends UseCaseHandler<DeleteWorld, Void> {

    private static final String ID_CANNOT_BE_NULL_OR_EMPTY = "World ID cannot be null or empty";

    private final WorldService domainService;

    @Override
    public void validate(DeleteWorld command) {

        if (StringUtils.isBlank(command.getId())) {
            throw new IllegalArgumentException(ID_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    @Override
    public Void execute(DeleteWorld command) {

        domainService.deleteWorld(command);

        return null;
    }
}
