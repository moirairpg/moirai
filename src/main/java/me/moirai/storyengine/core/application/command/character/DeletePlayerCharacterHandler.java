package me.moirai.storyengine.core.application.command.character;

import org.springframework.context.ApplicationEventPublisher;

import me.moirai.storyengine.common.annotation.CommandHandler;
import me.moirai.storyengine.common.cqs.command.AbstractCommandHandler;
import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.core.port.inbound.character.DeletePlayerCharacter;
import me.moirai.storyengine.core.port.outbound.character.PlayerCharacterRepository;
import me.moirai.storyengine.core.port.outbound.character.PlayerCharacterVectorSearchPort;
import me.moirai.storyengine.core.port.outbound.storage.StoragePort;

@CommandHandler
public class DeletePlayerCharacterHandler extends AbstractCommandHandler<DeletePlayerCharacter, Void> {

    private final PlayerCharacterRepository repository;
    private final PlayerCharacterVectorSearchPort vectorSearchPort;
    private final StoragePort storagePort;
    private final ApplicationEventPublisher eventPublisher;

    public DeletePlayerCharacterHandler(
            PlayerCharacterRepository repository,
            PlayerCharacterVectorSearchPort vectorSearchPort,
            StoragePort storagePort,
            ApplicationEventPublisher eventPublisher) {

        this.repository = repository;
        this.vectorSearchPort = vectorSearchPort;
        this.storagePort = storagePort;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Void execute(DeletePlayerCharacter command) {

        var character = repository.findByPublicId(command.characterId())
                .orElseThrow(() -> new NotFoundException("Player character not found"));

        storagePort.delete(character.getImageKey());
        vectorSearchPort.delete(character.getPublicId());

        character.communicateCharacterDeleted();
        character.drainEvents().forEach(eventPublisher::publishEvent);

        repository.deleteByPublicId(command.characterId());

        return null;
    }
}