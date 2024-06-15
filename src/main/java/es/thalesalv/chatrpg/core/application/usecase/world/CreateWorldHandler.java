package es.thalesalv.chatrpg.core.application.usecase.world;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.application.usecase.world.request.CreateWorld;
import es.thalesalv.chatrpg.core.application.usecase.world.result.CreateWorldResult;
import es.thalesalv.chatrpg.core.domain.world.World;
import es.thalesalv.chatrpg.core.domain.world.WorldService;
import lombok.RequiredArgsConstructor;

@UseCaseHandler
@RequiredArgsConstructor
public class CreateWorldHandler extends AbstractUseCaseHandler<CreateWorld, CreateWorldResult> {

    private final WorldService domainService;

    @Override
    public CreateWorldResult execute(CreateWorld command) {

        World world = domainService.createFrom(command);
        return CreateWorldResult.build(world.getId());
    }
}
