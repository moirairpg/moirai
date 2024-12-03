package me.moirai.discordbot.core.application.usecase.world;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.world.request.RemoveFavoriteWorld;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;

@UseCaseHandler
public class RemoveFavoriteWorldHandler extends AbstractUseCaseHandler<RemoveFavoriteWorld, Void> {

    private static final String ASSET_TYPE = "world";

    private final FavoriteRepository favoriteRepository;

    public RemoveFavoriteWorldHandler(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public Void execute(RemoveFavoriteWorld command) {

        favoriteRepository.deleteByPlayerDiscordIdAndAssetIdAndAssetType(
                command.getPlayerDiscordId(), command.getAssetId(), ASSET_TYPE);

        return null;
    }
}
