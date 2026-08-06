package me.moirai.storyengine.core.application.command.message;

import org.springframework.context.ApplicationEventPublisher;

import me.moirai.storyengine.common.annotation.Authorize;
import me.moirai.storyengine.common.annotation.CommandHandler;
import me.moirai.storyengine.common.cqs.command.AbstractCommandHandler;
import me.moirai.storyengine.common.enums.MessageAuthorRole;
import me.moirai.storyengine.common.exception.BusinessRuleViolationException;
import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.common.security.authorization.AuthorizationOperation;
import me.moirai.storyengine.core.application.event.message.MessageTranscriptChangedEvent;
import me.moirai.storyengine.core.application.event.message.NarrationRetriedEvent;
import me.moirai.storyengine.core.port.inbound.message.Retry;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;
import me.moirai.storyengine.core.port.outbound.message.AdventureMessageUpdate;
import me.moirai.storyengine.core.port.outbound.message.MessageRepository;

@CommandHandler
@Authorize(operation = AuthorizationOperation.RETRY_NARRATION, fields = "#request.adventureId")
public class RetryHandler extends AbstractCommandHandler<Retry, Void> {

    private final AdventureRepository adventureRepository;
    private final MessageRepository messageRepository;
    private final ApplicationEventPublisher eventPublisher;

    public RetryHandler(
            AdventureRepository adventureRepository,
            MessageRepository messageRepository,
            ApplicationEventPublisher eventPublisher) {

        this.adventureRepository = adventureRepository;
        this.messageRepository = messageRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void validate(Retry command) {

        if (command.adventureId() == null) {
            throw new IllegalArgumentException("Adventure ID cannot be null");
        }
    }

    @Override
    public Void execute(Retry command) {

        var adventure = adventureRepository.findByPublicId(command.adventureId())
                .orElseThrow(() -> new NotFoundException("Adventure not found"));

        var lastMessage = messageRepository.getLastActive(adventure.getId())
                .orElseThrow(() -> new BusinessRuleViolationException("Cannot retry: adventure has no messages"));

        if (lastMessage.getRole() != MessageAuthorRole.ASSISTANT) {
            throw new BusinessRuleViolationException("Cannot retry: last message is not an AI response");
        }

        messageRepository.deleteLastAssistantMessage(adventure.getId());

        eventPublisher.publishEvent(new MessageTranscriptChangedEvent(
                adventure.getPublicId(),
                AdventureMessageUpdate.messageRemoved(lastMessage.getPublicId(), true)));

        eventPublisher.publishEvent(new NarrationRetriedEvent(adventure.getPublicId()));

        return null;
    }
}
