package me.moirai.storyengine.core.application.command.message;

import static me.moirai.storyengine.common.util.DefaultStringProcessors.addChatPrefix;
import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.context.ApplicationEventPublisher;

import me.moirai.storyengine.common.annotation.Authorize;
import me.moirai.storyengine.common.annotation.CommandHandler;
import me.moirai.storyengine.common.cqs.command.AbstractCommandHandler;
import me.moirai.storyengine.common.enums.MessageAuthorRole;
import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.common.security.authorization.AuthorizationOperation;
import me.moirai.storyengine.core.application.event.message.MessageTranscriptChangedEvent;
import me.moirai.storyengine.core.domain.message.Message;
import me.moirai.storyengine.core.port.inbound.message.MessageResult;
import me.moirai.storyengine.core.port.inbound.message.Say;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;
import me.moirai.storyengine.core.port.outbound.message.AdventureMessageUpdate;
import me.moirai.storyengine.core.port.outbound.message.MessageRepository;

@CommandHandler
@Authorize(operation = AuthorizationOperation.PLAY_ADVENTURE, fields = "#request.adventureId")
public class SayHandler extends AbstractCommandHandler<Say, Void> {

    private final AdventureRepository adventureRepository;
    private final MessageRepository messageRepository;
    private final ApplicationEventPublisher eventPublisher;

    public SayHandler(
            AdventureRepository adventureRepository,
            MessageRepository messageRepository,
            ApplicationEventPublisher eventPublisher) {

        this.adventureRepository = adventureRepository;
        this.messageRepository = messageRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void validate(Say command) {

        if (command.adventureId() == null) {
            throw new IllegalArgumentException("Adventure ID cannot be null");
        }

        if (isBlank(command.content())) {
            throw new IllegalArgumentException("Message content cannot be blank");
        }
    }

    @Override
    public Void execute(Say command) {

        var adventure = adventureRepository.findByPublicId(command.adventureId())
                .orElseThrow(() -> new NotFoundException("Adventure not found"));

        var message = messageRepository.save(Message.builder()
                .adventureId(adventure.getId())
                .role(MessageAuthorRole.ASSISTANT)
                .content(addChatPrefix(adventure.getNarratorName()).apply(command.content()))
                .build());

        eventPublisher.publishEvent(new MessageTranscriptChangedEvent(
                adventure.getPublicId(),
                AdventureMessageUpdate.messageAdded(new MessageResult(
                        message.getPublicId(),
                        message.getContent(),
                        message.getRole(),
                        message.getCreationDate()), false)));

        return null;
    }
}
