package me.moirai.storyengine.core.application.command.message;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;

import me.moirai.storyengine.common.annotation.Authorize;
import me.moirai.storyengine.common.annotation.CommandHandler;
import me.moirai.storyengine.common.cqs.command.AbstractCommandHandler;
import me.moirai.storyengine.common.dto.MessageSummary;
import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.common.security.authorization.AuthorizationOperation;
import me.moirai.storyengine.common.util.Functions;
import me.moirai.storyengine.core.application.event.message.MessageTranscriptChangedEvent;
import me.moirai.storyengine.core.domain.userdetails.User;
import me.moirai.storyengine.core.port.inbound.message.EditMessage;
import me.moirai.storyengine.core.port.outbound.message.AdventureMessageUpdate;
import me.moirai.storyengine.core.port.outbound.message.MessageRepository;
import me.moirai.storyengine.core.port.outbound.userdetails.UserRepository;

@CommandHandler
@Authorize(operation = AuthorizationOperation.EDIT_MESSAGE, fields = { "#request.adventureId",
        "#request.messageId" })
public class EditMessageHandler extends AbstractCommandHandler<EditMessage, Void> {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public EditMessageHandler(
            MessageRepository messageRepository,
            UserRepository userRepository,
            ApplicationEventPublisher eventPublisher) {

        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void validate(EditMessage command) {

        if (command.adventureId() == null) {
            throw new IllegalArgumentException("Adventure ID cannot be null");
        }

        if (command.messageId() == null) {
            throw new IllegalArgumentException("Message ID cannot be null");
        }

        if (isBlank(command.content())) {
            throw new IllegalArgumentException("Content cannot be blank");
        }
    }

    @Override
    public Void execute(EditMessage command) {

        var message = messageRepository.getByPublicId(command.messageId())
                .orElseThrow(() -> new NotFoundException("Message not found"));

        message.updateContent(command.content());

        var savedMessage = messageRepository.save(message);

        eventPublisher.publishEvent(new MessageTranscriptChangedEvent(
                command.adventureId(),
                AdventureMessageUpdate.messageEdited(new MessageSummary(
                        savedMessage.getPublicId(),
                        savedMessage.getRole(),
                        savedMessage.getContent(),
                        savedMessage.getStatus(),
                        resolveAuthorPublicId(savedMessage.getAuthorId()),
                        savedMessage.getAuthorCharacterName(),
                        savedMessage.getCreationDate()), false)));

        return null;
    }

    private UUID resolveAuthorPublicId(Long authorId) {

        return Functions.mapOrNull(authorId, id -> userRepository.findById(id)
                .map(User::getPublicId)
                .orElse(null));
    }
}
