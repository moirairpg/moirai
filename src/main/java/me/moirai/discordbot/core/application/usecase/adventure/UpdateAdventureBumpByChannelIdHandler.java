package me.moirai.discordbot.core.application.usecase.adventure;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.adventure.request.UpdateAdventureBumpByChannelId;
import me.moirai.discordbot.core.domain.adventure.AdventureDomainRepository;

@UseCaseHandler
public class UpdateAdventureBumpByChannelIdHandler
        extends AbstractUseCaseHandler<UpdateAdventureBumpByChannelId, Void> {

    private AdventureDomainRepository repository;

    public UpdateAdventureBumpByChannelIdHandler(AdventureDomainRepository repository) {
        this.repository = repository;
    }

    @Override
    public Void execute(UpdateAdventureBumpByChannelId useCase) {

        repository.updateBumpByChannelId(useCase.getBump(), useCase.getBumpFrequency(), useCase.getChannelId());
        return null;
    }
}
