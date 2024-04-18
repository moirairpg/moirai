package es.thalesalv.chatrpg.core.application.command.world;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.usecases.UseCaseHandler;
import es.thalesalv.chatrpg.core.domain.world.World;
import es.thalesalv.chatrpg.core.domain.world.WorldService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateWorldHandler extends UseCaseHandler<CreateWorld, CreateWorldResult> {

    private final WorldService domainService;

    @Override
    public CreateWorldResult execute(CreateWorld command) {

        World world = domainService.createFrom(command);
        return CreateWorldResult.build(world.getId());
    }
}
