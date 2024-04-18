package es.thalesalv.chatrpg.core.application.command.world;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.usecases.UseCaseHandler;
import es.thalesalv.chatrpg.core.domain.world.World;
import es.thalesalv.chatrpg.core.domain.world.WorldService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateWorldHandler extends UseCaseHandler<UpdateWorld, UpdateWorldResult> {

    private final WorldService service;

    @Override
    public UpdateWorldResult execute(UpdateWorld command) {

        return mapResult(service.update(command));
    }

    private UpdateWorldResult mapResult(World savedWorld) {

        return UpdateWorldResult.build(savedWorld.getLastUpdateDate());
    }
}
