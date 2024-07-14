package es.thalesalv.chatrpg.core.application.usecase.world;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.application.usecase.world.request.CreateWorld;
import es.thalesalv.chatrpg.core.application.usecase.world.result.CreateWorldResult;
import es.thalesalv.chatrpg.core.domain.world.WorldService;
import reactor.core.publisher.Mono;

@UseCaseHandler
public class CreateWorldHandler extends AbstractUseCaseHandler<CreateWorld, Mono<CreateWorldResult>> {

    private final WorldService domainService;

    public CreateWorldHandler(WorldService domainService) {
        this.domainService = domainService;
    }

    @Override
    public Mono<CreateWorldResult> execute(CreateWorld command) {

        return domainService.createFrom(command)
                .map(worldCreated -> CreateWorldResult.build(worldCreated.getId()));
    }
}
