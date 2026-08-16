package me.moirai.storyengine.core.application.service;

import static me.moirai.storyengine.common.util.DefaultStringProcessors.addChatPrefix;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import me.moirai.storyengine.common.enums.MessageAuthorRole;
import me.moirai.storyengine.common.enums.MessagePrompt;
import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.core.domain.message.Message;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;
import me.moirai.storyengine.core.port.outbound.generation.ActionEvaluationPort;
import me.moirai.storyengine.core.port.outbound.generation.ActionEvaluationRequest;
import me.moirai.storyengine.core.port.outbound.generation.ChatMessage;
import me.moirai.storyengine.core.port.outbound.message.MessageRepository;

@Service
public class CheckEvaluationService {

    private static final Logger LOG = LoggerFactory.getLogger(CheckEvaluationService.class);

    private static final int RECENT_HISTORY_SIZE = 10;
    private static final String EVALUATION_FAILED = "Check evaluation failed for adventure {}";

    private final AdventureRepository adventureRepository;
    private final MessageRepository messageRepository;
    private final ActionEvaluationPort actionEvaluationPort;

    public CheckEvaluationService(
            AdventureRepository adventureRepository,
            MessageRepository messageRepository,
            ActionEvaluationPort actionEvaluationPort) {

        this.adventureRepository = adventureRepository;
        this.messageRepository = messageRepository;
        this.actionEvaluationPort = actionEvaluationPort;
    }

    public void evaluateLatestPlayerAction(UUID adventurePublicId) {

        try {
            var adventure = adventureRepository.findByPublicId(adventurePublicId)
                    .orElseThrow(() -> new NotFoundException("Adventure not found"));

            if (!adventure.isRpgMechanicsEnabled()) {
                return;
            }

            var history = messageRepository.findAllActiveByAdventureId(adventure.getId());

            if (history.isEmpty() || history.getLast().getRole() != MessageAuthorRole.USER) {
                return;
            }

            actionEvaluationPort.evaluateAction(new ActionEvaluationRequest(
                    MessagePrompt.ACTION_EVALUATOR.getText(),
                    toRecentContext(history)));

        } catch (RuntimeException e) {
            LOG.error(EVALUATION_FAILED, adventurePublicId, e);
        }
    }

    private List<ChatMessage> toRecentContext(List<Message> history) {

        return history.stream()
                .skip(Math.max(0, history.size() - RECENT_HISTORY_SIZE))
                .map(message -> message.getRole() == MessageAuthorRole.USER
                        ? ChatMessage.asUser(addChatPrefix(message.getAuthorCharacterName()).apply(message.getContent()))
                        : ChatMessage.asAssistant(message.getContent()))
                .toList();
    }
}
