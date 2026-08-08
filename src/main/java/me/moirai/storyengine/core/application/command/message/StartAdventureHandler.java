package me.moirai.storyengine.core.application.command.message;

import org.springframework.context.ApplicationEventPublisher;

import me.moirai.storyengine.common.annotation.Authorize;
import me.moirai.storyengine.common.annotation.CommandHandler;
import me.moirai.storyengine.common.cqs.command.AbstractCommandHandler;
import me.moirai.storyengine.common.dto.MessageSummary;
import me.moirai.storyengine.common.enums.MessageAuthorRole;
import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.common.security.authorization.AuthorizationOperation;
import me.moirai.storyengine.core.application.event.message.AdventureStartedEvent;
import me.moirai.storyengine.core.application.event.message.MessageTranscriptChangedEvent;
import me.moirai.storyengine.core.domain.message.Message;
import me.moirai.storyengine.core.port.inbound.message.StartAdventure;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;
import me.moirai.storyengine.core.port.outbound.message.AdventureMessageUpdate;
import me.moirai.storyengine.core.port.outbound.message.MessageRepository;

@CommandHandler
@Authorize(operation = AuthorizationOperation.PLAY_ADVENTURE, fields = "#request.adventureId")
public class StartAdventureHandler extends AbstractCommandHandler<StartAdventure, Void> {

    private final AdventureRepository adventureRepository;
    private final MessageRepository messageRepository;
    private final ApplicationEventPublisher eventPublisher;

    public StartAdventureHandler(
            AdventureRepository adventureRepository,
            MessageRepository messageRepository,
            ApplicationEventPublisher eventPublisher) {

        this.adventureRepository = adventureRepository;
        this.messageRepository = messageRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void validate(StartAdventure command) {

        if (command.adventureId() == null) {
            throw new IllegalArgumentException("Adventure ID cannot be null");
        }
    }

    @Override
    public Void execute(StartAdventure command) {

        var adventure = adventureRepository.findByPublicId(command.adventureId())
                .orElseThrow(() -> new NotFoundException("Adventure not found"));

        var openingMessage = messageRepository.save(Message.builder()
                .adventureId(adventure.getId())
                .role(MessageAuthorRole.ASSISTANT)
                .content(adventure.getAdventureStart())
                .authorCharacterName(adventure.getNarratorName())
                .build());

        eventPublisher.publishEvent(new MessageTranscriptChangedEvent(
                adventure.getPublicId(),
                AdventureMessageUpdate.messageAdded(new MessageSummary(
                        openingMessage.getPublicId(),
                        openingMessage.getRole(),
                        openingMessage.getContent(),
                        openingMessage.getStatus(),
                        null,
                        openingMessage.getAuthorCharacterName(),
                        openingMessage.getCreationDate()), true)));

        eventPublisher.publishEvent(new AdventureStartedEvent(adventure.getPublicId()));

        return null;
    }
}
