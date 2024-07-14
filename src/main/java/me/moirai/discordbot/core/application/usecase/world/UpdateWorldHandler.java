package me.moirai.discordbot.core.application.usecase.world;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.world.request.UpdateWorld;
import me.moirai.discordbot.core.application.usecase.world.result.UpdateWorldResult;
import me.moirai.discordbot.core.domain.world.WorldService;
import reactor.core.publisher.Mono;

@UseCaseHandler
public class UpdateWorldHandler extends AbstractUseCaseHandler<UpdateWorld, Mono<UpdateWorldResult>> {

    private final WorldService service;

    public UpdateWorldHandler(WorldService service) {
        this.service = service;
    }

    @Override
    public Mono<UpdateWorldResult> execute(UpdateWorld command) {

        return service.update(command)
                .map(worldUpdated -> UpdateWorldResult.build(worldUpdated.getLastUpdateDate()));
    }
}
