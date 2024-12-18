package me.moirai.discordbot.core.application.usecase.adventure;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.adventure.request.RemoveFavoriteAdventure;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;

@UseCaseHandler
public class RemoveFavoriteAdventureHandler extends AbstractUseCaseHandler<RemoveFavoriteAdventure, Void> {

    private static final String ASSET_TYPE = "adventure";

    private final FavoriteRepository favoriteRepository;

    public RemoveFavoriteAdventureHandler(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public Void execute(RemoveFavoriteAdventure command) {

        favoriteRepository.deleteByPlayerDiscordIdAndAssetIdAndAssetType(
                command.getPlayerDiscordId(), command.getAssetId(), ASSET_TYPE);

        return null;
    }
}
