package me.moirai.discordbot.infrastructure.outbound.adapter;

import static me.moirai.discordbot.core.application.model.request.ChatMessage.Role.SYSTEM;
import static me.moirai.discordbot.core.application.model.request.ChatMessage.Role.USER;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.LF;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import me.moirai.discordbot.common.util.DefaultStringProcessors;
import me.moirai.discordbot.common.util.StringProcessor;
import me.moirai.discordbot.core.application.helper.ChatMessageHelper;
import me.moirai.discordbot.core.application.model.request.ChatMessage;
import me.moirai.discordbot.core.application.model.request.TextGenerationRequest;
import me.moirai.discordbot.core.application.port.StorySummarizationPort;
import me.moirai.discordbot.core.application.port.TextCompletionPort;
import me.moirai.discordbot.core.application.usecase.discord.DiscordMessageData;
import me.moirai.discordbot.core.domain.port.TokenizerPort;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.ModelConfigurationRequest;
import reactor.core.publisher.Mono;

@Component
@SuppressWarnings("unchecked")
public class StorySummarizationAdapter implements StorySummarizationPort {

    private static final String PERIOD = ".";
    private static final String SENTENCE_EXPRESSION = "((\\. |))(?:[ A-ZÀ-ÿa-z0-9-\"'&(),:;<>\\/\\\\]|\\.(?! ))+[\\?\\.\\!\\;'\"]$";
    private static final String SUMMARY = "summary";
    private static final Object LOREBOOK = "lorebook";
    private static final String RETRIEVED_MESSAGES = "retrievedMessages";
    private static final String MESSAGE_HISTORY = "messageHistory";
    private static final String SUMMARIZATION_INSTRUCTION = "Write a detailed summary of this converation. The summary needs to be detailed and explain the conversation so far, as best as possible, so more context on what has happened is available.";

    private final TextCompletionPort openAiPort;
    private final TokenizerPort tokenizerPort;
    private final ChatMessageHelper chatMessageService;

    public StorySummarizationAdapter(TextCompletionPort openAiPort, TokenizerPort tokenizerPort,
            ChatMessageHelper chatMessageService) {

        this.openAiPort = openAiPort;
        this.tokenizerPort = tokenizerPort;
        this.chatMessageService = chatMessageService;
    }

    @Override
    public Mono<Map<String, Object>> summarizeContextWith(Map<String, Object> context,
            ModelConfigurationRequest modelConfiguration) {

        int totalTokens = modelConfiguration.getAiModel().getHardTokenLimit();
        int reservedTokensForStory = (int) Math.floor(totalTokens * 0.30);

        return generateSummary(context, modelConfiguration)
                .map(contextWithSummary -> {
                    contextWithSummary.putAll(
                            chatMessageService.addMessagesToContext(contextWithSummary, reservedTokensForStory, 5));

                    contextWithSummary.putAll(addSummaryToContext(contextWithSummary, reservedTokensForStory));

                    contextWithSummary.putAll(chatMessageService.addMessagesToContext(contextWithSummary,
                            reservedTokensForStory, SUMMARY));

                    return contextWithSummary;
                });
    }

    private Mono<? extends Map<String, Object>> generateSummary(Map<String, Object> context,
            ModelConfigurationRequest modelConfiguration) {

        List<DiscordMessageData> rawMessageHistory = (List<DiscordMessageData>) context.get(RETRIEVED_MESSAGES);
        String lorebook = (String) context.get(LOREBOOK);

        TextGenerationRequest request = createSummarizationRequest(rawMessageHistory, lorebook, modelConfiguration);
        return openAiPort.generateTextFrom(request)
                .map(summaryGenerated -> {
                    StringProcessor processor = new StringProcessor();
                    String summary = summaryGenerated.getOutputText().trim();

                    processor.addRule(DefaultStringProcessors.stripChatPrefix());
                    processor.addRule(DefaultStringProcessors.stripTrailingFragment());
                    processor.addRule(DefaultStringProcessors.replaceTemplateWithValue(EMPTY, LF));

                    summary = processor.process(summary);

                    context.put(RETRIEVED_MESSAGES, rawMessageHistory);
                    context.put(SUMMARY, summary.trim());

                    return context;
                });
    }

    private Map<String, Object> addSummaryToContext(Map<String, Object> processedContext, int reservedTokensForStory) {

        String summary = (String) processedContext.get(SUMMARY);
        List<String> messageHistory = (List<String>) processedContext.get(MESSAGE_HISTORY);
        String messagesCollected = stringifyList(messageHistory);

        int tokensInSummary = tokenizerPort.getTokenCountFrom(summary);
        int tokensInContext = tokenizerPort.getTokenCountFrom(messagesCollected);
        int tokensLeftInContext = reservedTokensForStory - tokensInContext;

        while (tokensInSummary > tokensLeftInContext) {
            summary = summary.trim().replaceAll(SENTENCE_EXPRESSION, PERIOD).trim();
            summary = summary.equals(PERIOD) ? EMPTY : summary;
            tokensInSummary = tokenizerPort.getTokenCountFrom(summary);
        }

        processedContext.put(SUMMARY, summary);

        return processedContext;
    }

    private TextGenerationRequest createSummarizationRequest(List<DiscordMessageData> messagesExtracted,
            String lorebook, ModelConfigurationRequest modelConfiguration) {

        List<ChatMessage> chatMessages = new ArrayList<>();

        chatMessages.addAll(messagesExtracted.stream()
                .map(messageData -> ChatMessage.build(USER, messageData.getContent()))
                .collect(Collectors.toCollection(ArrayList::new))
                .reversed());

        if (StringUtils.isNotBlank(lorebook)) {
            chatMessages.addFirst(ChatMessage.build(SYSTEM, lorebook));
        }

        chatMessages.addFirst(ChatMessage.build(SYSTEM, SUMMARIZATION_INSTRUCTION));

        return TextGenerationRequest.builder()
                .presencePenalty(modelConfiguration.getPresencePenalty())
                .frequencyPenalty(modelConfiguration.getFrequencyPenalty())
                .logitBias(modelConfiguration.getLogitBias())
                .maxTokens(modelConfiguration.getMaxTokenLimit())
                .model(modelConfiguration.getAiModel().getOfficialModelName())
                .stopSequences(modelConfiguration.getStopSequences())
                .temperature(modelConfiguration.getTemperature())
                .messages(chatMessages)
                .build();
    }

    private String stringifyList(List<String> list) {

        return list.stream().collect(Collectors.joining(LF));
    }
}
