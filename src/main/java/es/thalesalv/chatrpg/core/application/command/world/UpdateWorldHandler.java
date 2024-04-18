package es.thalesalv.chatrpg.core.application.command.world;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.domain.world.World;
import es.thalesalv.chatrpg.core.domain.world.WorldService;
import lombok.RequiredArgsConstructor;

@UseCaseHandler
@RequiredArgsConstructor
public class UpdateWorldHandler extends AbstractUseCaseHandler<UpdateWorld, UpdateWorldResult> {

    private final WorldService service;

    @Override
    public UpdateWorldResult execute(UpdateWorld command) {

        return mapResult(service.update(command));
    }

    private UpdateWorldResult mapResult(World savedWorld) {

        return UpdateWorldResult.build(savedWorld.getLastUpdateDate());
    }
}
