package me.moirai.storyengine.core.application.command.message;

import static me.moirai.storyengine.common.util.DefaultStringProcessors.addChatPrefix;
import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.context.ApplicationEventPublisher;

import me.moirai.storyengine.common.annotation.Authorize;
import me.moirai.storyengine.common.annotation.CommandHandler;
import me.moirai.storyengine.common.cqs.command.AbstractCommandHandler;
import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.common.security.authorization.AuthorizationOperation;
import me.moirai.storyengine.core.application.event.message.MessageEditedEvent;
import me.moirai.storyengine.core.application.event.message.MessageTranscriptChangedEvent;
import me.moirai.storyengine.core.port.inbound.message.EditMessage;
import me.moirai.storyengine.core.port.inbound.message.MessageResult;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;
import me.moirai.storyengine.core.port.outbound.message.AdventureMessageUpdate;
import me.moirai.storyengine.core.port.outbound.message.MessageRepository;

@CommandHandler
@Authorize(operation = AuthorizationOperation.EDIT_MESSAGE, fields = { "#request.adventureId",
        "#request.messageId" })
public class EditMessageHandler extends AbstractCommandHandler<EditMessage, Void> {

    private final AdventureRepository adventureRepository;
    private final MessageRepository messageRepository;
    private final ApplicationEventPublisher eventPublisher;

    public EditMessageHandler(
            AdventureRepository adventureRepository,
            MessageRepository messageRepository,
            ApplicationEventPublisher eventPublisher) {

        this.adventureRepository = adventureRepository;
        this.messageRepository = messageRepository;
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

        var adventure = adventureRepository.findByPublicId(command.adventureId())
                .orElseThrow(() -> new NotFoundException("Adventure not found"));

        var message = messageRepository.getByPublicId(command.messageId())
                .orElseThrow(() -> new NotFoundException("Message not found"));

        var characterName = adventureRepository
                .findEnrolledCharacterName(adventure.getId(), command.username())
                .orElse(command.username());

        var prefixedContent = addChatPrefix(characterName).apply(command.content());

        messageRepository.updateContent(command.adventureId(), command.messageId(), prefixedContent);
        messageRepository.deleteNewerThanByPublicId(command.adventureId(), command.messageId());

        eventPublisher.publishEvent(new MessageTranscriptChangedEvent(
                command.adventureId(),
                AdventureMessageUpdate.messageEdited(new MessageResult(
                        message.getPublicId(),
                        prefixedContent,
                        message.getRole(),
                        message.getAuthorCharacterName(),
                        message.getCreationDate()), true)));

        eventPublisher.publishEvent(new MessageEditedEvent(command.adventureId()));

        return null;
    }
}
