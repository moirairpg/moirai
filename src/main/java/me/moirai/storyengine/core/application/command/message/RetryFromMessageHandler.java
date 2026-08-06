package me.moirai.storyengine.core.application.command.message;

import org.springframework.context.ApplicationEventPublisher;

import me.moirai.storyengine.common.annotation.Authorize;
import me.moirai.storyengine.common.annotation.CommandHandler;
import me.moirai.storyengine.common.cqs.command.AbstractCommandHandler;
import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.common.security.authorization.AuthorizationOperation;
import me.moirai.storyengine.core.application.event.message.MessageTranscriptChangedEvent;
import me.moirai.storyengine.core.application.event.message.NarrationRetriedFromMessageEvent;
import me.moirai.storyengine.core.port.inbound.message.RetryFromMessage;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;
import me.moirai.storyengine.core.port.outbound.message.AdventureMessageUpdate;
import me.moirai.storyengine.core.port.outbound.message.MessageRepository;

@CommandHandler
@Authorize(operation = AuthorizationOperation.UPDATE_ADVENTURE, fields = "#request.adventureId")
public class RetryFromMessageHandler extends AbstractCommandHandler<RetryFromMessage, Void> {

    private final AdventureRepository adventureRepository;
    private final MessageRepository messageRepository;
    private final ApplicationEventPublisher eventPublisher;

    public RetryFromMessageHandler(
            AdventureRepository adventureRepository,
            MessageRepository messageRepository,
            ApplicationEventPublisher eventPublisher) {

        this.adventureRepository = adventureRepository;
        this.messageRepository = messageRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void validate(RetryFromMessage command) {

        if (command.adventureId() == null) {
            throw new IllegalArgumentException("Adventure ID cannot be null");
        }

        if (command.messageId() == null) {
            throw new IllegalArgumentException("Message ID cannot be null");
        }
    }

    @Override
    public Void execute(RetryFromMessage command) {

        var adventure = adventureRepository.findByPublicId(command.adventureId())
                .orElseThrow(() -> new NotFoundException("Adventure not found"));

        messageRepository.deleteNewerThanByPublicId(command.adventureId(), command.messageId());
        messageRepository.deleteByPublicId(command.adventureId(), command.messageId());

        eventPublisher.publishEvent(new MessageTranscriptChangedEvent(
                adventure.getPublicId(),
                AdventureMessageUpdate.messagesRemovedFrom(command.messageId(), true)));

        eventPublisher.publishEvent(new NarrationRetriedFromMessageEvent(adventure.getPublicId()));

        return null;
    }
}
