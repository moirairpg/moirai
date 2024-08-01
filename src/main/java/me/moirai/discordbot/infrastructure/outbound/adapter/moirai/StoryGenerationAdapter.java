package me.moirai.discordbot.infrastructure.outbound.adapter.moirai;

import static me.moirai.discordbot.core.application.model.request.ChatMessage.Role.ASSISTANT;
import static me.moirai.discordbot.core.application.model.request.ChatMessage.Role.USER;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import me.moirai.discordbot.common.exception.ModerationException;
import me.moirai.discordbot.common.util.DefaultStringProcessors;
import me.moirai.discordbot.common.util.StringProcessor;
import me.moirai.discordbot.core.application.model.request.ChatMessage;
import me.moirai.discordbot.core.application.model.request.TextGenerationRequest;
import me.moirai.discordbot.core.application.model.result.TextGenerationResult;
import me.moirai.discordbot.core.application.port.DiscordChannelPort;
import me.moirai.discordbot.core.application.port.LorebookEnrichmentPort;
import me.moirai.discordbot.core.application.port.PersonaEnrichmentPort;
import me.moirai.discordbot.core.application.port.StoryGenerationPort;
import me.moirai.discordbot.core.application.port.StorySummarizationPort;
import me.moirai.discordbot.core.application.port.TextCompletionPort;
import me.moirai.discordbot.core.application.port.TextModerationPort;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.ModerationConfigurationRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.StoryGenerationRequest;
import reactor.core.publisher.Mono;

@Component
public class StoryGenerationAdapter implements StoryGenerationPort {

    private static final String BUMP = "bump";
    private static final String LOREBOOK_ENTRIES = "lorebook";
    private static final String STORY_SUMMARY = "summary";
    private static final String NUDGE = "nudge";
    private static final String PERSONA = "persona";
    private static final String PERSONA_NAME = "personaName";
    private static final String MESSAGE_HISTORY = "messageHistory";
    private static final String SAID = " said: ";
    private static final String SENTENCE_EXPRESSION = "((\\. |))(?:[ A-Za-z0-9-\"'&(),:;<>\\/\\\\]|\\.(?! ))+[\\?\\.\\!\\;'\"]$";
    private static final String PERIOD = ".";
    private static final int DISCORD_MAX_LENGTH = 2000;

    private final DiscordChannelPort discordChannelPort;
    private final StorySummarizationPort summarizationPort;
    private final LorebookEnrichmentPort lorebookEnrichmentPort;
    private final PersonaEnrichmentPort personaEnrichmentPort;
    private final TextCompletionPort textCompletionPort;
    private final TextModerationPort textModerationPort;

    public StoryGenerationAdapter(StorySummarizationPort summarizationPort, DiscordChannelPort discordChannelPort,
            LorebookEnrichmentPort lorebookEnrichmentPort, PersonaEnrichmentPort personaEnrichmentPort,
            TextCompletionPort textCompletionPort, TextModerationPort textModerationPort) {

        this.discordChannelPort = discordChannelPort;
        this.summarizationPort = summarizationPort;
        this.lorebookEnrichmentPort = lorebookEnrichmentPort;
        this.personaEnrichmentPort = personaEnrichmentPort;
        this.textCompletionPort = textCompletionPort;
        this.textModerationPort = textModerationPort;
    }

    @Override
    public Mono<Void> continueStory(StoryGenerationRequest request) {

        return Mono
                .just(discordChannelPort.retrieveEntireHistoryFrom(request.getChannelId()))
                .map(messageHistory -> lorebookEnrichmentPort.enrichContextWithLorebook(messageHistory,
                        request.getWorldId(), request.getModelConfiguration()))
                .flatMap(contextWithLorebook -> summarizationPort.summarizeContextWith(contextWithLorebook,
                        request.getModelConfiguration()))
                .flatMap(contextWithSummary -> personaEnrichmentPort.enrichContextWithPersona(
                        contextWithSummary, request.getPersonaId(), request.getModelConfiguration()))
                .map(contextWithPersona -> processEnrichedContext(contextWithPersona, request.getBotUsername(),
                        request.getBotNickname()))
                .flatMap(processedContext -> moderateInput(processedContext, request.getChannelId(),
                        request.getModeration()))
                .flatMap(processedContext -> generateAiOutput(request, processedContext))
                .flatMap(aiOutput -> moderateOutput(aiOutput, request.getChannelId(), request.getModeration()))
                .doOnNext(generatedOutput -> sendOutputTo(request.getChannelId(),
                        request.getBotUsername(), request.getBotNickname(), generatedOutput))
                .then();
    }

    private Mono<? extends TextGenerationResult> generateAiOutput(StoryGenerationRequest query,
            List<ChatMessage> processedContext) {
        TextGenerationRequest textGenerationRequest = buildTextGenerationRequest(query,
                processedContext);

        return textCompletionPort.generateTextFrom(textGenerationRequest);
    }

    private TextGenerationRequest buildTextGenerationRequest(StoryGenerationRequest query,
            List<ChatMessage> processedContext) {

        return TextGenerationRequest.builder()
                .frequencyPenalty(query.getModelConfiguration().getFrequencyPenalty())
                .presencePenalty(query.getModelConfiguration().getPresencePenalty())
                .temperature(query.getModelConfiguration().getTemperature())
                .model(query.getModelConfiguration().getAiModel().getOfficialModelName())
                .logitBias(query.getModelConfiguration().getLogitBias())
                .maxTokens(query.getModelConfiguration().getMaxTokenLimit())
                .stopSequences(query.getModelConfiguration().getStopSequences())
                .messages(processedContext)
                .build();
    }

    private List<ChatMessage> processEnrichedContext(Map<String, Object> unsortedContext,
            String botName, String botNickname) {

        String persona = (String) unsortedContext.get(PERSONA);
        String personaName = (String) unsortedContext.get(PERSONA_NAME);
        String nudge = (String) unsortedContext.get(NUDGE);
        String storySummary = (String) unsortedContext.get(STORY_SUMMARY);
        String lorebookEntries = (String) unsortedContext.get(LOREBOOK_ENTRIES);

        List<ChatMessage> processedContext = new ArrayList<>();
        processedContext.add(
                ChatMessage.build(ChatMessage.Role.SYSTEM,
                        replacePlaceholders(storySummary, botName, botNickname, personaName)));

        processedContext.addAll(buildContextForGeneration(unsortedContext, botName, botNickname, personaName));

        if (StringUtils.isNotBlank(lorebookEntries)) {
            processedContext.add(0, ChatMessage.build(ChatMessage.Role.SYSTEM, lorebookEntries));
        }

        processedContext.add(0, ChatMessage.build(ChatMessage.Role.SYSTEM, persona));

        if (StringUtils.isNotBlank(nudge)) {
            processedContext.add(ChatMessage.build(ChatMessage.Role.SYSTEM, nudge));
        }

        return extractBumpFrom(unsortedContext, processedContext);
    }

    private String replacePlaceholders(String summary, String botName, String botNickname, String personaName) {

        StringProcessor processor = new StringProcessor();
        processor.addRule(DefaultStringProcessors.stripChatPrefix());
        processor.addRule(DefaultStringProcessors.stripTrailingFragment());
        processor.addRule(DefaultStringProcessors.replaceBotNameWithPersonaName(personaName, botName));
        processor.addRule(DefaultStringProcessors.replaceBotNameWithPersonaName(personaName, botNickname));

        return processor.process(summary);
    }

    @SuppressWarnings("unchecked")
    private List<ChatMessage> buildContextForGeneration(Map<String, Object> unsortedContext,
            String botName, String botNickname, String personaName) {

        List<String> messageHistory = (List<String>) unsortedContext.get(MESSAGE_HISTORY);
        return messageHistory.stream()
                .map(message -> {
                    StringProcessor processor = new StringProcessor();
                    processor.addRule(DefaultStringProcessors.replaceBotNameWithPersonaName(personaName, botName));
                    processor.addRule(DefaultStringProcessors.replaceBotNameWithPersonaName(personaName, botNickname));

                    String modifiedContent = processor.process(message);
                    String senderName = message.substring(0, message.indexOf(SAID));
                    ChatMessage.Role senderRole = senderName.equals(botName) ? ASSISTANT : USER;

                    return ChatMessage.build(senderRole, modifiedContent);
                })
                .toList();
    }

    private List<ChatMessage> extractBumpFrom(Map<String, Object> unsortedContext, List<ChatMessage> processedContext) {

        String bump = (String) unsortedContext.get(BUMP);
        if (StringUtils.isNotBlank(bump)) {
            int bumpFrequency = 5;
            for (int index = processedContext.size() - 1 - bumpFrequency; index > 0; index = index - bumpFrequency) {
                processedContext.add(index, ChatMessage.build(ChatMessage.Role.SYSTEM, bump));
            }
        }

        return processedContext;
    }

    private void sendOutputTo(String messageChannelId, String botName, String botNickname, String content) {

        StringProcessor processor = new StringProcessor();
        processor.addRule(DefaultStringProcessors.stripAsNamePrefixForLowercase(botName));
        processor.addRule(DefaultStringProcessors.stripAsNamePrefixForUppercase(botName));
        processor.addRule(DefaultStringProcessors.stripAsNamePrefixForLowercase(botNickname));
        processor.addRule(DefaultStringProcessors.stripAsNamePrefixForUppercase(botNickname));
        processor.addRule(DefaultStringProcessors.stripChatPrefix());
        processor.addRule(DefaultStringProcessors.stripTrailingFragment());

        String output = processor.process(content);
        int outputSize = output.length();

        while (outputSize > DISCORD_MAX_LENGTH) {
            output = output.replaceAll(SENTENCE_EXPRESSION, PERIOD).trim();
            output = output.equals(PERIOD) ? EMPTY : output;
            outputSize = output.length();
        }

        discordChannelPort.sendMessageTo(messageChannelId, output);
    }

    private Mono<List<ChatMessage>> moderateInput(List<ChatMessage> messages, String channelId,
            ModerationConfigurationRequest moderation) {

        String messageHistory = messages.stream()
                .map(ChatMessage::getContent)
                .collect(Collectors.joining("\n"));

        return getTopicsFlaggedByModeration(messageHistory, moderation)
                .map(result -> {
                    if (!result.isEmpty()) {
                        throw new ModerationException("Inappropriate content found on user's input", channelId, result);
                    }

                    return messages;
                });
    }

    private Mono<String> moderateOutput(TextGenerationResult generationResult,
            String channelId, ModerationConfigurationRequest moderation) {

        return getTopicsFlaggedByModeration(generationResult.getOutputText(), moderation)
                .map(result -> {
                    if (!result.isEmpty()) {
                        throw new ModerationException("Inappropriate content found on AI's output", channelId, result);
                    }

                    return generationResult.getOutputText();
                });
    }

    private Mono<List<String>> getTopicsFlaggedByModeration(String input, ModerationConfigurationRequest moderation) {

        return textModerationPort.moderate(input)
                .map(result -> {
                    if (moderation.isAbsolute()) {
                        if (result.isContentFlagged()) {
                            return result.getFlaggedTopics();
                        }

                        return Collections.emptyList();
                    }

                    return result.getModerationScores()
                            .entrySet()
                            .stream()
                            .filter(entry -> isTopicFlagged(entry, moderation))
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toList());
                });
    }

    private boolean isTopicFlagged(Entry<String, Double> entry, ModerationConfigurationRequest moderation) {
        return entry.getValue() > moderation.getThresholds().get(entry.getKey());
    }
}
