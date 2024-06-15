package es.thalesalv.chatrpg.core.application.usecase.discord.messagereceived;

import static es.thalesalv.chatrpg.core.application.model.request.ChatMessage.Role.ASSISTANT;
import static es.thalesalv.chatrpg.core.application.model.request.ChatMessage.Role.USER;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;
import es.thalesalv.chatrpg.common.exception.ModerationException;
import es.thalesalv.chatrpg.common.usecases.AbstractUseCaseHandler;
import es.thalesalv.chatrpg.common.util.DefaultStringProcessors;
import es.thalesalv.chatrpg.common.util.StringProcessor;
import es.thalesalv.chatrpg.core.application.model.request.ChatMessage;
import es.thalesalv.chatrpg.core.application.model.request.TextGenerationRequest;
import es.thalesalv.chatrpg.core.application.model.result.TextGenerationResult;
import es.thalesalv.chatrpg.core.application.port.DiscordChannelPort;
import es.thalesalv.chatrpg.core.application.port.TextCompletionPort;
import es.thalesalv.chatrpg.core.application.port.TextModerationPort;
import es.thalesalv.chatrpg.core.application.service.LorebookEnrichmentService;
import es.thalesalv.chatrpg.core.application.service.PersonaEnrichmentService;
import es.thalesalv.chatrpg.core.application.service.StorySummarizationService;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfig;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigRepository;
import es.thalesalv.chatrpg.core.domain.channelconfig.Moderation;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@UseCaseHandler
@RequiredArgsConstructor
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

    @Override
    public Mono<Void> execute(MessageReceived query) {

        return channelConfigRepository.findByDiscordChannelId(query.getMessageChannelId())
                .filter(channelConfig -> channelConfig.getDiscordChannelId().equals(query.getMessageChannelId()))
                .map(channelConfig -> discordChannelOperationsPort
                        .retrieveEntireHistoryFrom(query.getMessageGuildId(), query.getMessageChannelId(),
                                query.getMessageId(), query.getMentionedUsersIds())
                        .flatMap(messageHistory -> summarizationService.summarizeContextWith(messageHistory,
                                channelConfig.getModelConfiguration()))
                        .flatMap(contextWithSummary -> lorebookEnrichmentService.enrichContextWith(contextWithSummary,
                                channelConfig.getWorldId(), channelConfig.getModelConfiguration()))
                        .flatMap(contextWithLorebook -> personaEnrichmentService.enrichContextWith(contextWithLorebook,
                                channelConfig.getPersonaId(), channelConfig.getModelConfiguration()))
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
                    if (moderation.isAbsolute() && result.isContentFlagged()) {
                        return result.getFlaggedTopics();
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
