package me.moirai.discordbot.core.application.usecase.discord.messagereceived;

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

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.ModerationException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.common.util.DefaultStringProcessors;
import me.moirai.discordbot.common.util.StringProcessor;
import me.moirai.discordbot.core.application.model.request.ChatMessage;
import me.moirai.discordbot.core.application.model.request.TextGenerationRequest;
import me.moirai.discordbot.core.application.model.result.TextGenerationResult;
import me.moirai.discordbot.core.application.port.DiscordChannelPort;
import me.moirai.discordbot.core.application.port.TextCompletionPort;
import me.moirai.discordbot.core.application.port.TextModerationPort;
import me.moirai.discordbot.core.application.service.LorebookEnrichmentService;
import me.moirai.discordbot.core.application.service.PersonaEnrichmentService;
import me.moirai.discordbot.core.application.service.StorySummarizationService;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigRepository;
import me.moirai.discordbot.core.domain.channelconfig.Moderation;
import reactor.core.publisher.Mono;

@UseCaseHandler
public class MessageReceivedHandler extends AbstractUseCaseHandler<MessageReceived, Mono<Void>> {

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

    private final ChannelConfigRepository channelConfigRepository;
    private final DiscordChannelPort discordChannelOperationsPort;
    private final StorySummarizationService summarizationService;
    private final LorebookEnrichmentService lorebookEnrichmentService;
    private final PersonaEnrichmentService personaEnrichmentService;
    private final TextCompletionPort textCompletionPort;
    private final TextModerationPort textModerationPort;

    public MessageReceivedHandler(ChannelConfigRepository channelConfigRepository,
            DiscordChannelPort discordChannelOperationsPort, StorySummarizationService summarizationService,
            LorebookEnrichmentService lorebookEnrichmentService, PersonaEnrichmentService personaEnrichmentService,
            TextCompletionPort textCompletionPort, TextModerationPort textModerationPort) {

        this.channelConfigRepository = channelConfigRepository;
        this.discordChannelOperationsPort = discordChannelOperationsPort;
        this.summarizationService = summarizationService;
        this.lorebookEnrichmentService = lorebookEnrichmentService;
        this.personaEnrichmentService = personaEnrichmentService;
        this.textCompletionPort = textCompletionPort;
        this.textModerationPort = textModerationPort;
    }

    @Override
    public Mono<Void> execute(MessageReceived query) {

        return channelConfigRepository.findByDiscordChannelId(query.getMessageChannelId())
                .filter(channelConfig -> channelConfig.getDiscordChannelId().equals(query.getMessageChannelId()))
                .map(channelConfig -> discordChannelOperationsPort
                        .retrieveEntireHistoryFrom(query.getMessageGuildId(), query.getMessageChannelId(),
                                query.getMessageId(), query.getMentionedUsersIds())
                        .map(messageHistory -> lorebookEnrichmentService.enrichContextWithLorebook(messageHistory,
                                channelConfig.getWorldId(), channelConfig.getModelConfiguration()))
                        .flatMap(contextWithLorebook -> summarizationService.summarizeContextWith(contextWithLorebook,
                                channelConfig.getModelConfiguration()))
                        .flatMap(contextWithSummary -> personaEnrichmentService.enrichContextWithPersona(
                                contextWithSummary, channelConfig.getPersonaId(),
                                channelConfig.getModelConfiguration()))
                        .map(contextWithPersona -> processEnrichedContext(contextWithPersona, query.getBotName()))
                        .flatMap(processedContext -> moderateInput(processedContext, query.getMessageChannelId(),
                                channelConfig.getModeration()))
                        .flatMap(processedContext -> generateAiOutput(channelConfig, processedContext))
                        .flatMap(aiOutput -> moderateOutput(aiOutput, query.getMessageChannelId(),
                                channelConfig.getModeration()))
                        .flatMap(generatedOutput -> sendOutputTo(query.getMessageChannelId(),
                                query.getBotName(), generatedOutput)))
                .orElseGet(() -> Mono.empty());
    }

    private Mono<? extends TextGenerationResult> generateAiOutput(ChannelConfig channelConfig,
            List<ChatMessage> processedContext) {
        TextGenerationRequest textGenerationRequest = buildTextGenerationRequest(channelConfig,
                processedContext);

        return textCompletionPort.generateTextFrom(textGenerationRequest);
    }

    private TextGenerationRequest buildTextGenerationRequest(ChannelConfig channelConfig,
            List<ChatMessage> processedContext) {

        return TextGenerationRequest.builder()
                .frequencyPenalty(channelConfig.getModelConfiguration().getFrequencyPenalty())
                .presencePenalty(channelConfig.getModelConfiguration().getPresencePenalty())
                .temperature(channelConfig.getModelConfiguration().getTemperature())
                .model(channelConfig.getModelConfiguration().getAiModel().getOfficialModelName())
                .logitBias(channelConfig.getModelConfiguration().getLogitBias())
                .maxTokens(channelConfig.getModelConfiguration().getMaxTokenLimit())
                .stopSequences(channelConfig.getModelConfiguration().getStopSequences())
                .messages(processedContext)
                .build();
    }

    private List<ChatMessage> processEnrichedContext(Map<String, Object> unsortedContext, String botName) {

        String persona = (String) unsortedContext.get(PERSONA);
        String personaName = (String) unsortedContext.get(PERSONA_NAME);
        String nudge = (String) unsortedContext.get(NUDGE);
        String storySummary = (String) unsortedContext.get(STORY_SUMMARY);
        String lorebookEntries = (String) unsortedContext.get(LOREBOOK_ENTRIES);

        List<ChatMessage> processedContext = new ArrayList<>();
        processedContext.add(
                ChatMessage.build(ChatMessage.Role.SYSTEM, replacePlaceholders(storySummary, botName, personaName)));

        processedContext.addAll(buildContextForGeneration(unsortedContext, botName, personaName));

        if (StringUtils.isNotBlank(lorebookEntries)) {
            processedContext.add(0, ChatMessage.build(ChatMessage.Role.SYSTEM, lorebookEntries));
        }

        processedContext.add(0, ChatMessage.build(ChatMessage.Role.SYSTEM, persona));

        if (StringUtils.isNotBlank(nudge)) {
            processedContext.add(ChatMessage.build(ChatMessage.Role.SYSTEM, nudge));
        }

        return extractBumpFrom(unsortedContext, processedContext);
    }

    private String replacePlaceholders(String summary, String botName, String personaName) {

        StringProcessor processor = new StringProcessor();
        processor.addRule(DefaultStringProcessors.stripChatPrefix());
        processor.addRule(DefaultStringProcessors.stripTrailingFragment());
        processor.addRule(DefaultStringProcessors.replaceBotNameWithPersonaName(personaName, botName));

        return processor.process(summary);
    }

    @SuppressWarnings("unchecked")
    private List<ChatMessage> buildContextForGeneration(Map<String, Object> unsortedContext,
            String botName, String personaName) {

        List<String> messageHistory = (List<String>) unsortedContext.get(MESSAGE_HISTORY);
        return messageHistory.stream()
                .map(message -> {
                    StringProcessor processor = new StringProcessor();
                    processor.addRule(DefaultStringProcessors.replaceBotNameWithPersonaName(personaName, botName));

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

    private Mono<Void> sendOutputTo(String messageChannelId, String botName, String content) {

        StringProcessor processor = new StringProcessor();
        processor.addRule(DefaultStringProcessors.stripAsNamePrefixForLowercase(botName));
        processor.addRule(DefaultStringProcessors.stripAsNamePrefixForUppercase(botName));
        processor.addRule(DefaultStringProcessors.stripChatPrefix());
        processor.addRule(DefaultStringProcessors.stripTrailingFragment());

        String output = processor.process(content);
        int outputSize = output.length();

        while (outputSize > DISCORD_MAX_LENGTH) {
            output = output.replaceAll(SENTENCE_EXPRESSION, PERIOD).trim();
            output = output.equals(PERIOD) ? EMPTY : output;
            outputSize = output.length();
        }

        return discordChannelOperationsPort.sendMessage(messageChannelId, output);
    }

    private Mono<List<ChatMessage>> moderateInput(List<ChatMessage> messages, String channelId, Moderation moderation) {

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
            String channelId, Moderation moderation) {

        return getTopicsFlaggedByModeration(generationResult.getOutputText(), moderation)
                .map(result -> {
                    if (!result.isEmpty()) {
                        throw new ModerationException("Inappropriate content found on AI's output", channelId, result);
                    }

                    return generationResult.getOutputText();
                });
    }

    private Mono<List<String>> getTopicsFlaggedByModeration(String input, Moderation moderation) {

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

    private boolean isTopicFlagged(Entry<String, Double> entry, Moderation moderation) {
        return entry.getValue() > moderation.getThresholds().get(entry.getKey());
    }
}
