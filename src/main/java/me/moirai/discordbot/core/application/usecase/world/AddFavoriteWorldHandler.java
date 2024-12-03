package me.moirai.discordbot.core.application.usecase.world;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.WorldQueryRepository;
import me.moirai.discordbot.core.application.usecase.world.request.AddFavoriteWorld;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteEntity;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;

@UseCaseHandler
public class AddFavoriteWorldHandler extends AbstractUseCaseHandler<AddFavoriteWorld, Void> {

    private static final String ASSET_TYPE = "world";

    private final WorldQueryRepository worldQueryRepository;
    private final FavoriteRepository favoriteRepository;

    public AddFavoriteWorldHandler(WorldQueryRepository worldQueryRepository,
            FavoriteRepository favoriteRepository) {
        this.worldQueryRepository = worldQueryRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public Void execute(AddFavoriteWorld command) {

        worldQueryRepository.findById(command.getAssetId())
                .orElseThrow(() -> new AssetNotFoundException("The world to be favorited could not be found"));

        favoriteRepository.save(FavoriteEntity.builder()
                .assetType(ASSET_TYPE)
                .assetId(command.getAssetId())
                .playerDiscordId(command.getPlayerDiscordId())
                .build());

        return null;
    }
}
