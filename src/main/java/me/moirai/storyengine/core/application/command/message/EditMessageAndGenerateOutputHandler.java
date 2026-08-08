package me.moirai.storyengine.core.application.command.message;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;

import me.moirai.storyengine.common.annotation.Authorize;
import me.moirai.storyengine.common.annotation.CommandHandler;
import me.moirai.storyengine.common.cqs.command.AbstractCommandHandler;
import me.moirai.storyengine.common.dto.MessageSummary;
import me.moirai.storyengine.common.enums.MessageAuthorRole;
import me.moirai.storyengine.common.exception.BusinessRuleViolationException;
import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.common.security.authorization.AuthorizationOperation;
import me.moirai.storyengine.common.util.Functions;
import me.moirai.storyengine.core.application.event.message.MessageEditedEvent;
import me.moirai.storyengine.core.application.event.message.MessageTranscriptChangedEvent;
import me.moirai.storyengine.core.domain.userdetails.User;
import me.moirai.storyengine.core.port.inbound.message.EditMessageAndGenerateOutput;
import me.moirai.storyengine.core.port.outbound.message.AdventureMessageUpdate;
import me.moirai.storyengine.core.port.outbound.message.MessageRepository;
import me.moirai.storyengine.core.port.outbound.userdetails.UserRepository;

@CommandHandler
@Authorize(operation = AuthorizationOperation.EDIT_MESSAGE_AND_GENERATE_OUTPUT, fields = { "#request.adventureId",
        "#request.messageId" })
public class EditMessageAndGenerateOutputHandler extends AbstractCommandHandler<EditMessageAndGenerateOutput, Void> {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public EditMessageAndGenerateOutputHandler(
            MessageRepository messageRepository,
            UserRepository userRepository,
            ApplicationEventPublisher eventPublisher) {

        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void validate(EditMessageAndGenerateOutput command) {

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
    public Void execute(EditMessageAndGenerateOutput command) {

        var message = messageRepository.getByPublicId(command.messageId())
                .orElseThrow(() -> new NotFoundException("Message not found"));

        if (message.getRole() != MessageAuthorRole.USER) {
            throw new BusinessRuleViolationException(
                    "Cannot edit and generate output: the message is not a player message");
        }

        message.updateContent(command.content());

        var savedMessage = messageRepository.save(message);

        messageRepository.deleteNewerThanByPublicId(command.adventureId(), command.messageId());

        eventPublisher.publishEvent(new MessageTranscriptChangedEvent(
                command.adventureId(),
                AdventureMessageUpdate.messageEdited(new MessageSummary(
                        savedMessage.getPublicId(),
                        savedMessage.getRole(),
                        savedMessage.getContent(),
                        savedMessage.getStatus(),
                        resolveAuthorPublicId(savedMessage.getAuthorId()),
                        savedMessage.getAuthorCharacterName(),
                        savedMessage.getCreationDate()), true)));

        eventPublisher.publishEvent(new MessageTranscriptChangedEvent(
                command.adventureId(),
                AdventureMessageUpdate.messagesRemovedAfter(command.messageId(), true)));

        eventPublisher.publishEvent(new MessageEditedEvent(command.adventureId()));

        return null;
    }

    private UUID resolveAuthorPublicId(Long authorId) {

        return Functions.mapOrNull(authorId, id -> userRepository.findById(id)
                .map(User::getPublicId)
                .orElse(null));
    }
}
