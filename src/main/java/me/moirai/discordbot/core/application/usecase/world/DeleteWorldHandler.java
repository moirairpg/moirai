package me.moirai.discordbot.core.application.usecase.world;

import org.apache.commons.lang3.StringUtils;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.world.request.DeleteWorld;
import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.core.domain.world.WorldService;

@UseCaseHandler
public class DeleteWorldHandler extends AbstractUseCaseHandler<DeleteWorld, Void> {

    private static final String ID_CANNOT_BE_NULL_OR_EMPTY = "World ID cannot be null or empty";
    private static final String PERMISSION_MODIFY_DENIED = "User does not have permission to modify this world";

    private final WorldService domainService;

    public DeleteWorldHandler(WorldService domainService) {
        this.domainService = domainService;
    }

    @Override
    public void validate(DeleteWorld command) {

        if (StringUtils.isBlank(command.getId())) {
            throw new IllegalArgumentException(ID_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    @Override
    public Void execute(DeleteWorld command) {

        World world = domainService.getWorldById(command.getId());
        if (!world.canUserWrite(command.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException(PERMISSION_MODIFY_DENIED);
        }

        domainService.deleteWorld(command);

        return null;
    }
}
