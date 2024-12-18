package me.moirai.discordbot.core.application.usecase.adventure;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.AdventureQueryRepository;
import me.moirai.discordbot.core.application.usecase.adventure.request.AddFavoriteAdventure;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteEntity;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;

@UseCaseHandler
public class AddFavoriteAdventureHandler extends AbstractUseCaseHandler<AddFavoriteAdventure, Void> {

    private static final String ASSET_TYPE = "adventure";

    private final AdventureQueryRepository adventureQueryRepository;
    private final FavoriteRepository favoriteRepository;

    public AddFavoriteAdventureHandler(AdventureQueryRepository adventureQueryRepository,
            FavoriteRepository favoriteRepository) {
        this.adventureQueryRepository = adventureQueryRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public Void execute(AddFavoriteAdventure command) {

        adventureQueryRepository.findById(command.getAssetId())
                .orElseThrow(() -> new AssetNotFoundException("The adventure to be favorited could not be found"));

        favoriteRepository.save(FavoriteEntity.builder()
                .assetType(ASSET_TYPE)
                .assetId(command.getAssetId())
                .playerDiscordId(command.getPlayerDiscordId())
                .build());

        return null;
    }
}
