package me.moirai.discordbot.core.application.usecase.channelconfig;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.ChannelConfigQueryRepository;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.AddFavoriteChannelConfig;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteEntity;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;

@UseCaseHandler
public class AddFavoriteChannelConfigHandler extends AbstractUseCaseHandler<AddFavoriteChannelConfig, Void> {

    private static final String ASSET_TYPE = "channel_config";

    private final ChannelConfigQueryRepository channelConfigQueryRepository;
    private final FavoriteRepository favoriteRepository;

    public AddFavoriteChannelConfigHandler(ChannelConfigQueryRepository channelConfigQueryRepository,
            FavoriteRepository favoriteRepository) {
        this.channelConfigQueryRepository = channelConfigQueryRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public Void execute(AddFavoriteChannelConfig command) {

        channelConfigQueryRepository.findById(command.getAssetId())
                .orElseThrow(() -> new AssetNotFoundException("The channel config to be favorited could not be found"));

        favoriteRepository.save(FavoriteEntity.builder()
                .assetType(ASSET_TYPE)
                .assetId(command.getAssetId())
                .playerDiscordId(command.getPlayerDiscordId())
                .build());

        return null;
    }
}
