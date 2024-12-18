package me.moirai.discordbot.core.application.usecase.adventure;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.adventure.request.UpdateAdventureAuthorsNoteByChannelId;
import me.moirai.discordbot.core.domain.adventure.AdventureDomainRepository;

@UseCaseHandler
public class UpdateAdventureAuthorsNoteByChannelIdHandler
        extends AbstractUseCaseHandler<UpdateAdventureAuthorsNoteByChannelId, Void> {

    private AdventureDomainRepository repository;

    public UpdateAdventureAuthorsNoteByChannelIdHandler(AdventureDomainRepository repository) {
        this.repository = repository;
    }

    @Override
    public Void execute(UpdateAdventureAuthorsNoteByChannelId useCase) {

        repository.updateAuthorsNoteByChannelId(useCase.getAuthorsNote(), useCase.getChannelId());
        return null;
    }
}
