package me.moirai.discordbot.core.application.usecase.world;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.WorldQueryRepository;
import me.moirai.discordbot.core.application.usecase.world.request.GetWorldById;
import me.moirai.discordbot.core.application.usecase.world.result.GetWorldResult;
import me.moirai.discordbot.core.domain.world.World;

@UseCaseHandler
public class GetWorldByIdHandler extends AbstractUseCaseHandler<GetWorldById, GetWorldResult> {

    private static final String WORLD_NOT_FOUND = "World to be deleted was not found";
    private static final String PERMISSION_VIEW_DENIED = "User does not have permission to view this world";

    private final WorldQueryRepository queryRepository;

    public GetWorldByIdHandler(WorldQueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    @Override
    public GetWorldResult execute(GetWorldById query) {

        World world = queryRepository.findById(query.getId())
                .orElseThrow(() -> new AssetNotFoundException(WORLD_NOT_FOUND));

        if (!world.canUserRead(query.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException(PERMISSION_VIEW_DENIED);
        }

        return mapResult(world);
    }

    private GetWorldResult mapResult(World world) {

        return GetWorldResult.builder()
                .id(world.getId())
                .name(world.getName())
                .description(world.getDescription())
                .adventureStart(world.getAdventureStart())
                .visibility(world.getVisibility().name())
                .ownerDiscordId(world.getOwnerDiscordId())
                .creationDate(world.getCreationDate())
                .lastUpdateDate(world.getLastUpdateDate())
                .build();
    }
}
