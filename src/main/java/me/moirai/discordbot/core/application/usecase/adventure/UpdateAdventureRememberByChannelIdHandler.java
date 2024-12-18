package me.moirai.discordbot.core.application.usecase.adventure;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.adventure.request.UpdateAdventureRememberByChannelId;
import me.moirai.discordbot.core.domain.adventure.AdventureDomainRepository;

@UseCaseHandler
public class UpdateAdventureRememberByChannelIdHandler
        extends AbstractUseCaseHandler<UpdateAdventureRememberByChannelId, Void> {

    private AdventureDomainRepository repository;

    public UpdateAdventureRememberByChannelIdHandler(AdventureDomainRepository repository) {
        this.repository = repository;
    }

    @Override
    public Void execute(UpdateAdventureRememberByChannelId useCase) {

        repository.updateRememberByChannelId(useCase.getRemember(), useCase.getChannelId());
        return null;
    }
}
