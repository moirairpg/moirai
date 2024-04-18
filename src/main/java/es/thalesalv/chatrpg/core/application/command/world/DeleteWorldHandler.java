package es.thalesalv.chatrpg.core.application.command.world;

import org.apache.commons.lang3.StringUtils;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.domain.world.WorldService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@UseCaseHandler
@RequiredArgsConstructor
public class DeleteWorldHandler extends AbstractUseCaseHandler<DeleteWorld, Void> {

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
