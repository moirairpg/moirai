package me.moirai.discordbot.core.application.usecase.persona;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.persona.request.RemoveFavoritePersona;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;

@UseCaseHandler
public class RemoveFavoritePersonaHandler extends AbstractUseCaseHandler<RemoveFavoritePersona, Void> {

    private static final String ASSET_TYPE = "persona";

    private final FavoriteRepository favoriteRepository;

    public RemoveFavoritePersonaHandler(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public Void execute(RemoveFavoritePersona command) {

        favoriteRepository.deleteByPlayerDiscordIdAndAssetIdAndAssetType(
                command.getPlayerDiscordId(), command.getAssetId(), ASSET_TYPE);

        return null;
    }
}
