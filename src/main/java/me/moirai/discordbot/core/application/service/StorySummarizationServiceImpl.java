package me.moirai.discordbot.core.application.service;

import static me.moirai.discordbot.core.application.model.request.ChatMessage.Role.SYSTEM;
import static me.moirai.discordbot.core.application.model.request.ChatMessage.Role.USER;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.LF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import me.moirai.discordbot.common.annotation.ApplicationService;
import me.moirai.discordbot.core.application.model.request.ChatMessage;
import me.moirai.discordbot.core.application.model.request.TextGenerationRequest;
import me.moirai.discordbot.core.application.port.TextCompletionPort;
import me.moirai.discordbot.core.domain.channelconfig.ModelConfiguration;
import me.moirai.discordbot.core.domain.port.TokenizerPort;
import me.moirai.discordbot.infrastructure.outbound.adapter.response.ChatMessageData;
import reactor.core.publisher.Mono;

@ApplicationService
@SuppressWarnings("unchecked")
public class StorySummarizationServiceImpl implements StorySummarizationService {

    private static final String PERIOD = ".";
    private static final String SENTENCE_EXPRESSION = "((\\. |))(?:[ A-Za-z0-9-\"'&(),:;<>\\/\\\\]|\\.(?! ))+[\\?\\.\\!\\;'\"]$";
    private static final String SUMMARY = "summary";
    private static final String RETRIEVED_MESSAGES = "retrievedMessages";
    private static final String MESSAGE_HISTORY = "messageHistory";
    private static final String SUMMARIZATION_INSTRUCTION = "Write a detailed summary of this converation. The summary needs to be detailed and explain the conversation so far, as best as possible, so more context on what has happened is available.";

    private final TextCompletionPort openAiPort;
    private final TokenizerPort tokenizerPort;
    private final ChatMessageService chatMessageService;

    public StorySummarizationServiceImpl(TextCompletionPort openAiPort, TokenizerPort tokenizerPort,
            ChatMessageService chatMessageService) {

        this.openAiPort = openAiPort;
        this.tokenizerPort = tokenizerPort;
        this.chatMessageService = chatMessageService;
    }

    @Override
    public Mono<Map<String, Object>> summarizeContextWith(List<ChatMessageData> messagesExtracted,
            ModelConfiguration modelConfiguration) {

        int totalTokens = modelConfiguration.getAiModel().getHardTokenLimit();
        int reservedTokensForStory = (int) Math.floor(totalTokens * 0.30);

        return generateSummary(messagesExtracted, modelConfiguration)
                .map(context -> {
                    context.putAll(chatMessageService.addMessagesToContext(context, reservedTokensForStory, 5));
                    context.putAll(addSummaryToContext(context, reservedTokensForStory));
                    context.putAll(chatMessageService.addMessagesToContext(context, reservedTokensForStory, SUMMARY));

                    return context;
                });
    }

    private Mono<? extends Map<String, Object>> generateSummary(List<ChatMessageData> messagesExtracted,
            ModelConfiguration modelConfiguration) {

        TextGenerationRequest request = createSummarizationRequest(messagesExtracted, modelConfiguration);
        return openAiPort.generateTextFrom(request)
                .map(summaryGenerated -> {
                    String summary = summaryGenerated.getOutputText().trim();
                    Map<String, Object> processedContext = new HashMap<>();

                    processedContext.put(RETRIEVED_MESSAGES, messagesExtracted);
                    processedContext.put(SUMMARY, summary.trim());

                    return processedContext;
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
            summary = summary.replaceAll(SENTENCE_EXPRESSION, PERIOD).trim();
            summary = summary.equals(PERIOD) ? EMPTY : summary;
            tokensInSummary = tokenizerPort.getTokenCountFrom(summary);
        }

        processedContext.put(SUMMARY, summary);

        return processedContext;
    }

    private TextGenerationRequest createSummarizationRequest(List<ChatMessageData> messagesExtracted,
            ModelConfiguration modelConfiguration) {

        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessages.addAll(messagesExtracted.stream()
                .map(messageData -> ChatMessage.build(USER, messageData.getContent()))
                .collect(Collectors.toCollection(ArrayList::new))
                .reversed());

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
