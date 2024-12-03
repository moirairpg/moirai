package me.moirai.discordbot.core.application.usecase.channelconfig;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.RemoveFavoriteChannelConfig;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;

@UseCaseHandler
public class RemoveFavoriteChannelConfigHandler extends AbstractUseCaseHandler<RemoveFavoriteChannelConfig, Void> {

    private static final String ASSET_TYPE = "channel_config";

    private final FavoriteRepository favoriteRepository;

    public RemoveFavoriteChannelConfigHandler(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public Void execute(RemoveFavoriteChannelConfig command) {

        favoriteRepository.deleteByPlayerDiscordIdAndAssetIdAndAssetType(
                command.getPlayerDiscordId(), command.getAssetId(), ASSET_TYPE);

        return null;
    }
}
