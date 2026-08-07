package me.moirai.storyengine.core.application.event.message;

import static me.moirai.storyengine.common.enums.MessagePrompt.CONTINUE_GENERATION;
import static me.moirai.storyengine.common.enums.MessagePrompt.NARRATION_SCOPE;
import static me.moirai.storyengine.common.enums.MessagePrompt.PLAYER_CHARACTER_HEADING;
import static me.moirai.storyengine.common.util.DefaultStringProcessors.addChatPrefix;
import static me.moirai.storyengine.common.util.DefaultStringProcessors.stripAsNamePrefix;
import static me.moirai.storyengine.common.util.DefaultStringProcessors.stripAsNamePrefixForLowercase;
import static me.moirai.storyengine.common.util.DefaultStringProcessors.stripChatPrefix;
import static me.moirai.storyengine.common.util.DefaultStringProcessors.stripTrailingFragment;
import static me.moirai.storyengine.common.util.DefaultStringProcessors.truncateAtPlayerCharacterLine;
import static org.apache.commons.lang3.StringUtils.defaultString;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import me.moirai.storyengine.common.enums.MessageAuthorRole;
import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.common.util.StringProcessor;
import me.moirai.storyengine.core.application.service.StoryContextService;
import me.moirai.storyengine.core.domain.adventure.Adventure;
import me.moirai.storyengine.core.domain.adventure.AdventureDeletedEvent;
import me.moirai.storyengine.core.domain.character.PlayerCharacterRenamedEvent;
import me.moirai.storyengine.core.domain.message.Message;
import me.moirai.storyengine.core.port.inbound.message.MessageResult;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;
import me.moirai.storyengine.core.port.outbound.generation.TextCompletionPort;
import me.moirai.storyengine.core.port.outbound.generation.TextGenerationRequest;
import me.moirai.storyengine.core.port.outbound.message.AdventureMessageUpdate;
import me.moirai.storyengine.core.port.outbound.message.MessageRepository;

@Component
public class MessageDomainEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(MessageDomainEventListener.class);

    private static final String NARRATION_FAILED = "Narration failed for adventure {}";

    private final MessageRepository messageRepository;
    private final AdventureRepository adventureRepository;
    private final TextCompletionPort textCompletionPort;
    private final StoryContextService storyContextService;
    private final ApplicationEventPublisher eventPublisher;
    private final int messageWindowSize;

    public MessageDomainEventListener(
            MessageRepository messageRepository,
            AdventureRepository adventureRepository,
            TextCompletionPort textCompletionPort,
            StoryContextService storyContextService,
            ApplicationEventPublisher eventPublisher,
            @Value("${moirai.adventure.message-window-size}") int messageWindowSize) {

        this.messageRepository = messageRepository;
        this.adventureRepository = adventureRepository;
        this.textCompletionPort = textCompletionPort;
        this.storyContextService = storyContextService;
        this.eventPublisher = eventPublisher;
        this.messageWindowSize = messageWindowSize;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onAdventureDeleted(AdventureDeletedEvent event) {

        messageRepository.deleteAllByAdventureId(event.getAdventureId());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @EventListener
    public void onPlayerCharacterRenamed(PlayerCharacterRenamedEvent event) {

        messageRepository.updateAuthorCharacterName(event.getPlayerCharacterId(), event.getName());
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageSent(MessageSentEvent event) {

        narrate(event.adventurePublicId(), "");
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAdventureStarted(AdventureStartedEvent event) {

        narrate(event.adventurePublicId(), CONTINUE_GENERATION.getText());
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageEdited(MessageEditedEvent event) {

        narrate(event.adventurePublicId(), CONTINUE_GENERATION.getText());
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onStoryContinued(StoryContinuedEvent event) {

        narrate(event.adventurePublicId(), CONTINUE_GENERATION.getText());
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onNarrationRetried(NarrationRetriedEvent event) {

        narrate(event.adventurePublicId(), CONTINUE_GENERATION.getText());
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onNarrationRetriedFromMessage(NarrationRetriedFromMessageEvent event) {

        narrate(event.adventurePublicId(), CONTINUE_GENERATION.getText());
    }

    private void narrate(UUID adventurePublicId, String additionalPrompt) {

        try {
            var adventure = adventureRepository.findByPublicId(adventurePublicId)
                    .orElseThrow(() -> new NotFoundException("Adventure not found"));

            var storyContext = storyContextService.build(adventure);
            var modelConfiguration = adventure.getModelConfiguration();

            var instructions = defaultString(adventure.getNarratorPersonality())
                    + additionalPrompt
                    + NARRATION_SCOPE.formatted(PLAYER_CHARACTER_HEADING.getText());

            var generationResult = textCompletionPort.generateTextFrom(new TextGenerationRequest(
                    modelConfiguration.getAiModel().getOfficialModelName(),
                    instructions,
                    storyContext.messages(),
                    modelConfiguration.getMaxTokenLimit(),
                    modelConfiguration.getTemperature()));

            var cleanedResponse = cleanUp(
                    generationResult.getOutputText(),
                    adventure.getNarratorName(),
                    storyContext.playerCharacterNames());

            var narratorMessage = messageRepository.save(Message.builder()
                    .adventureId(adventure.getId())
                    .role(MessageAuthorRole.ASSISTANT)
                    .content(addChatPrefix(adventure.getNarratorName()).apply(cleanedResponse))
                    .build());

            chronicleIfWindowOverflowed(adventure, storyContext.activeHistory(), narratorMessage);

            eventPublisher.publishEvent(new MessageTranscriptChangedEvent(
                    adventurePublicId,
                    AdventureMessageUpdate.messageAdded(new MessageResult(
                            narratorMessage.getPublicId(),
                            cleanedResponse,
                            narratorMessage.getRole(),
                            null,
                            narratorMessage.getCreationDate()), false)));

        } catch (RuntimeException e) {
            LOG.error(NARRATION_FAILED, adventurePublicId, e);

            eventPublisher.publishEvent(new MessageTranscriptChangedEvent(
                    adventurePublicId,
                    AdventureMessageUpdate.narrationFailed()));
        }
    }

    private String cleanUp(String generatedText, String narratorName, List<String> playerCharacterNames) {

        var processor = new StringProcessor();

        processor.addRule(stripChatPrefix());
        processor.addRule(stripAsNamePrefix(narratorName));
        processor.addRule(stripAsNamePrefixForLowercase(narratorName));
        processor.addRule(stripTrailingFragment());
        processor.addRule(truncateAtPlayerCharacterLine(playerCharacterNames));

        return processor.process(generatedText);
    }

    private void chronicleIfWindowOverflowed(
            Adventure adventure,
            List<Message> activeHistory,
            Message narratorMessage) {

        if (activeHistory.size() < messageWindowSize) {
            return;
        }

        activeHistory.forEach(Message::markAsChronicled);
        messageRepository.saveAll(activeHistory);

        narratorMessage.markAsChronicled();
        messageRepository.save(narratorMessage);

        narratorMessage.communicateChatWindowOverflow(adventure.getPublicId());
        narratorMessage.drainEvents().forEach(eventPublisher::publishEvent);
    }
}
