package me.moirai.storyengine.core.application.service;

import static me.moirai.storyengine.common.enums.ArtificialIntelligenceModel.GPT54_MINI;
import static me.moirai.storyengine.common.enums.MessagePrompt.PLAYER_CHARACTER_HEADING;
import static me.moirai.storyengine.common.enums.MessagePrompt.RAG_QUERY_EXTRACTOR;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import me.moirai.storyengine.common.exception.BusinessRuleViolationException;
import me.moirai.storyengine.core.domain.adventure.Adventure;
import me.moirai.storyengine.core.domain.adventure.AdventureLorebookEntry;
import me.moirai.storyengine.core.domain.adventure.ChronicleSegment;
import me.moirai.storyengine.core.domain.adventure.ContextAttributes;
import me.moirai.storyengine.core.domain.character.PlayerCharacter;
import me.moirai.storyengine.core.domain.message.Message;
import me.moirai.storyengine.core.port.outbound.adventure.ChronicleVectorSearchPort;
import me.moirai.storyengine.core.port.outbound.adventure.LorebookVectorSearchPort;
import me.moirai.storyengine.core.port.outbound.character.PlayerCharacterRepository;
import me.moirai.storyengine.core.port.outbound.character.PlayerCharacterVectorSearchPort;
import me.moirai.storyengine.core.port.outbound.generation.ChatMessage;
import me.moirai.storyengine.core.port.outbound.generation.EmbeddingPort;
import me.moirai.storyengine.core.port.outbound.generation.TextCompletionPort;
import me.moirai.storyengine.core.port.outbound.generation.TextGenerationRequest;
import me.moirai.storyengine.core.port.outbound.message.MessageRepository;

@Service
public class StoryContextService {

    private static final int RECENT_HISTORY_SIZE = 10;

    private final MessageRepository messageRepository;
    private final PlayerCharacterRepository playerCharacterRepository;
    private final EmbeddingPort embeddingPort;
    private final TextCompletionPort textCompletionPort;
    private final LorebookVectorSearchPort lorebookVectorSearchPort;
    private final ChronicleVectorSearchPort chronicleVectorSearchPort;
    private final PlayerCharacterVectorSearchPort playerCharacterVectorSearchPort;
    private final int messageWindowSize;
    private final int lorebookTopK;
    private final int chronicleTopK;
    private final int playerCharacterTopK;

    public StoryContextService(
            MessageRepository messageRepository,
            PlayerCharacterRepository playerCharacterRepository,
            EmbeddingPort embeddingPort,
            TextCompletionPort textCompletionPort,
            LorebookVectorSearchPort lorebookVectorSearchPort,
            ChronicleVectorSearchPort chronicleVectorSearchPort,
            PlayerCharacterVectorSearchPort playerCharacterVectorSearchPort,
            @Value("${moirai.adventure.message-window-size}") int messageWindowSize,
            @Value("${moirai.rag.lorebook.top-k}") int lorebookTopK,
            @Value("${moirai.rag.chronicle.top-k}") int chronicleTopK,
            @Value("${moirai.rag.player-character.top-k}") int playerCharacterTopK) {

        this.messageRepository = messageRepository;
        this.playerCharacterRepository = playerCharacterRepository;
        this.embeddingPort = embeddingPort;
        this.textCompletionPort = textCompletionPort;
        this.lorebookVectorSearchPort = lorebookVectorSearchPort;
        this.chronicleVectorSearchPort = chronicleVectorSearchPort;
        this.playerCharacterVectorSearchPort = playerCharacterVectorSearchPort;
        this.messageWindowSize = messageWindowSize;
        this.lorebookTopK = lorebookTopK;
        this.chronicleTopK = chronicleTopK;
        this.playerCharacterTopK = playerCharacterTopK;
    }

    public StoryContext build(Adventure adventure) {

        var activeHistory = messageRepository.findAllActiveByAdventureId(adventure.getId());
        var history = topUpHistory(adventure.getId(), activeHistory);
        var enrolledCharacters = loadEnrolledCharacters(adventure);
        var queryVector = embeddingPort.embed(buildRagQuery(history));

        var lorebookHits = lorebookVectorSearchPort.search(
                adventure.getPublicId(), queryVector, lorebookTopK);

        var chronicleHits = chronicleVectorSearchPort.search(
                adventure.getPublicId(), queryVector, chronicleTopK);

        var messages = new ArrayList<ChatMessage>();

        messages.addAll(toLorebookMessages(adventure.getLorebookEntriesByIds(lorebookHits)));
        messages.addAll(toChronicleMessages(adventure.getChronicleSegmentsByIds(chronicleHits)));
        messages.addAll(toPlayerCharacterMessages(enrolledCharacters, queryVector));

        messages.addAll(interleaveBumps(
                history.stream().map(this::toChatMessage).toList(),
                adventure.getContextAttributes()));

        adventure.getContextAttributes().asText().stream()
                .map(ChatMessage::asSystem)
                .forEach(messages::add);

        var playerCharacterNames = enrolledCharacters.stream()
                .map(PlayerCharacter::getName)
                .toList();

        return new StoryContext(messages, playerCharacterNames, activeHistory);
    }

    private List<Message> topUpHistory(Long adventureId, List<Message> active) {

        var deficit = messageWindowSize - active.size();

        if (deficit <= 0) {
            return active;
        }

        var backfill = messageRepository.findLatestChronicledByAdventureId(adventureId, deficit).reversed();
        var combined = new ArrayList<Message>(backfill.size() + active.size());

        combined.addAll(backfill);
        combined.addAll(active);

        return Collections.unmodifiableList(combined);
    }

    private List<PlayerCharacter> loadEnrolledCharacters(Adventure adventure) {

        var enrolledCharacterIds = adventure.getEnrolledCharacterIds();

        if (enrolledCharacterIds.isEmpty()) {
            return List.of();
        }

        return playerCharacterRepository.findAllByIdIn(enrolledCharacterIds);
    }

    private List<ChatMessage> toLorebookMessages(List<AdventureLorebookEntry> entries) {

        return entries.stream()
                .map(entry -> ChatMessage.asSystem(entry.getName() + ": " + entry.getDescription()))
                .toList();
    }

    private List<ChatMessage> toChronicleMessages(List<ChronicleSegment> segments) {

        return segments.stream()
                .map(segment -> ChatMessage.asSystem(segment.getContent()))
                .toList();
    }

    private List<ChatMessage> toPlayerCharacterMessages(
            List<PlayerCharacter> candidates,
            float[] queryVector) {

        if (candidates.isEmpty()) {
            return List.of();
        }

        var candidatePublicIds = candidates.stream()
                .map(PlayerCharacter::getPublicId)
                .toList();

        var matched = Set.copyOf(
                playerCharacterVectorSearchPort.search(candidatePublicIds, queryVector, playerCharacterTopK));

        var cast = candidates.stream()
                .filter(character -> matched.contains(character.getPublicId()))
                .map(PlayerCharacter::narrativeDescription)
                .collect(Collectors.joining(System.lineSeparator()));

        if (isBlank(cast)) {
            return List.of();
        }

        return List.of(ChatMessage.asSystem(
                PLAYER_CHARACTER_HEADING.getText() + ":" + System.lineSeparator() + cast));
    }

    private List<ChatMessage> interleaveBumps(List<ChatMessage> messages, ContextAttributes contextAttributes) {

        var bump = contextAttributes.bump();
        var bumpFrequency = contextAttributes.bumpFrequency();

        if (isBlank(bump) || bumpFrequency == null || bumpFrequency <= 0) {
            return messages;
        }

        var result = new ArrayList<ChatMessage>();
        var size = messages.size();

        for (var index = 0; index < size; index++) {
            result.add(messages.get(index));

            if ((size - index) % bumpFrequency == 0) {
                result.add(ChatMessage.asSystem(bump));
            }
        }

        return Collections.unmodifiableList(result);
    }

    private ChatMessage toChatMessage(Message message) {

        return switch (message.getRole()) {
            case USER -> ChatMessage.asUser(message.getContent());
            case ASSISTANT -> ChatMessage.asAssistant(message.getContent());
            default ->
                throw new BusinessRuleViolationException("Unexpected role in message history: " + message.getRole());
        };
    }

    private String buildRagQuery(List<Message> history) {

        if (history.isEmpty()) {
            throw new BusinessRuleViolationException("Cannot narrate: adventure has no messages");
        }

        var recentHistory = history.stream()
                .skip(Math.max(0, history.size() - RECENT_HISTORY_SIZE))
                .map(this::toChatMessage)
                .toList();

        var primedMessages = new ArrayList<>(recentHistory);

        primedMessages.add(ChatMessage.asUser(history.getLast().getContent()));
        primedMessages.add(ChatMessage.asAssistant("Location:"));

        var ragQueryRequest = new TextGenerationRequest(
                GPT54_MINI.getOfficialModelName(),
                RAG_QUERY_EXTRACTOR.getText(),
                primedMessages,
                100,
                0.1);

        return textCompletionPort.generateTextFrom(ragQueryRequest).getOutputText();
    }
}
