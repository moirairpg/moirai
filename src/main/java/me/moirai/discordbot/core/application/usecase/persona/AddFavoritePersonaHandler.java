package me.moirai.discordbot.core.application.usecase.persona;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.PersonaQueryRepository;
import me.moirai.discordbot.core.application.usecase.persona.request.AddFavoritePersona;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteEntity;
import me.moirai.discordbot.infrastructure.outbound.persistence.FavoriteRepository;

@UseCaseHandler
public class AddFavoritePersonaHandler extends AbstractUseCaseHandler<AddFavoritePersona, Void> {

    private static final String ASSET_TYPE = "persona";

    private final PersonaQueryRepository personaQueryRepository;
    private final FavoriteRepository favoriteRepository;

    public AddFavoritePersonaHandler(PersonaQueryRepository personaQueryRepository,
            FavoriteRepository favoriteRepository) {
        this.personaQueryRepository = personaQueryRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public Void execute(AddFavoritePersona command) {

        personaQueryRepository.findById(command.getAssetId())
                .orElseThrow(() -> new AssetNotFoundException("The persona to be favorited could not be found"));

        favoriteRepository.save(FavoriteEntity.builder()
                .assetType(ASSET_TYPE)
                .assetId(command.getAssetId())
                .playerDiscordId(command.getPlayerDiscordId())
                .build());

        return null;
    }
}
