package me.moirai.discordbot.core.application.usecase.adventure;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.adventure.request.UpdateAdventureNudgeByChannelId;
import me.moirai.discordbot.core.domain.adventure.AdventureDomainRepository;

@UseCaseHandler
public class UpdateAdventureNudgeByChannelIdHandler
        extends AbstractUseCaseHandler<UpdateAdventureNudgeByChannelId, Void> {

    private AdventureDomainRepository repository;

    public UpdateAdventureNudgeByChannelIdHandler(AdventureDomainRepository repository) {
        this.repository = repository;
    }

    @Override
    public Void execute(UpdateAdventureNudgeByChannelId useCase) {

        repository.updateNudgeByChannelId(useCase.getNudge(), useCase.getChannelId());
        return null;
    }
}
