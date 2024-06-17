package es.thalesalv.chatrpg.core.application.usecase.world;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.core.application.usecase.world.request.UpdateWorld;
import es.thalesalv.chatrpg.core.application.usecase.world.result.UpdateWorldResult;
import es.thalesalv.chatrpg.core.domain.world.WorldService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@UseCaseHandler
@RequiredArgsConstructor
public class UpdateWorldHandler extends AbstractUseCaseHandler<UpdateWorld, Mono<UpdateWorldResult>> {

    private final WorldService service;

    @Override
    public Mono<UpdateWorldResult> execute(UpdateWorld command) {

        return service.update(command)
                .map(worldUpdated -> UpdateWorldResult.build(worldUpdated.getLastUpdateDate()));
    }
}
