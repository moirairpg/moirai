package me.moirai.storyengine.core.application.command.message;

import org.springframework.context.ApplicationEventPublisher;

import me.moirai.storyengine.common.annotation.Authorize;
import me.moirai.storyengine.common.annotation.CommandHandler;
import me.moirai.storyengine.common.cqs.command.AbstractCommandHandler;
import me.moirai.storyengine.common.security.authorization.AuthorizationOperation;
import me.moirai.storyengine.core.application.event.message.MessageTranscriptChangedEvent;
import me.moirai.storyengine.core.port.inbound.message.DeleteMessage;
import me.moirai.storyengine.core.port.outbound.message.AdventureMessageUpdate;
import me.moirai.storyengine.core.port.outbound.message.MessageRepository;

@CommandHandler
@Authorize(operation = AuthorizationOperation.UPDATE_ADVENTURE, fields = "#request.adventureId")
public class DeleteMessageHandler extends AbstractCommandHandler<DeleteMessage, Void> {

    private final MessageRepository messageRepository;
    private final ApplicationEventPublisher eventPublisher;

    public DeleteMessageHandler(
            MessageRepository messageRepository,
            ApplicationEventPublisher eventPublisher) {

        this.messageRepository = messageRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void validate(DeleteMessage command) {

        if (command.adventureId() == null) {
            throw new IllegalArgumentException("Adventure ID cannot be null");
        }

        if (command.messageId() == null) {
            throw new IllegalArgumentException("Message ID cannot be null");
        }
    }

    @Override
    public Void execute(DeleteMessage command) {

        messageRepository.deleteByPublicId(command.adventureId(), command.messageId());

        eventPublisher.publishEvent(new MessageTranscriptChangedEvent(
                command.adventureId(),
                AdventureMessageUpdate.messageRemoved(command.messageId(), false)));

        return null;
    }
}
