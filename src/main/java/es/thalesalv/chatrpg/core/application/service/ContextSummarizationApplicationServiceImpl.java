package es.thalesalv.chatrpg.core.application.service;

import static es.thalesalv.chatrpg.core.application.model.request.ChatMessage.Role.SYSTEM;
import static es.thalesalv.chatrpg.core.application.model.request.ChatMessage.Role.USER;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.LF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.core.application.model.request.ChatMessage;
import es.thalesalv.chatrpg.core.application.model.request.TextGenerationRequest;
import es.thalesalv.chatrpg.core.application.port.DiscordChannelPort;
import es.thalesalv.chatrpg.core.application.port.OpenAiPort;
import es.thalesalv.chatrpg.core.domain.channelconfig.ModelConfiguration;
import es.thalesalv.chatrpg.core.domain.port.TokenizerPort;
import es.thalesalv.chatrpg.infrastructure.outbound.adapter.response.ChatMessageData;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class ContextSummarizationApplicationServiceImpl implements ContextSummarizationApplicationService {

    private static final String PERIOD = ".";
    private static final String SENTENCE_EXPRESSION = "((\\. |))(?:[ A-Za-z0-9-\"'&(),:;<>\\/\\\\]|\\.(?! ))+[\\?\\.\\!\\;'\"]$";
    private static final String SUMMARY = "summary";
    private static final String RETRIEVED_MESSAGES = "retrievedMessages";
    private static final String MESSAGE_HISTORY = "messageHistory";

    private final DiscordChannelPort discordChannelOperationsPort;
    private final OpenAiPort openAiPort;
    private final TokenizerPort tokenizerPort;

    @Override
    public Mono<Map<String, Object>> summarizeWith(String guildId, String authorId, String channelId, String messageId,
            String botName,
            ModelConfiguration modelConfiguration, List<String> mentionedUserIds) {

        int totalTokens = modelConfiguration.getAiModel().getHardTokenLimit();
        int reservedTokensForStory = (int) Math.floor(totalTokens * 0.30);

        return discordChannelOperationsPort.retrieveEntireHistoryFrom(guildId, channelId, messageId, mentionedUserIds)
                .flatMap(messagesExtracted -> generateSummary(messagesExtracted, modelConfiguration))
                .map(processedContext -> {
                    processedContext.putAll(addInitialMessagesToContext(processedContext, reservedTokensForStory));
                    processedContext.putAll(addSummaryToContext(processedContext, reservedTokensForStory));
                    processedContext.putAll(addExtraMessagesToContext(processedContext,
                            channelId, reservedTokensForStory));

                    return processedContext;
                });
    }

    private Mono<? extends Map<String, Object>> generateSummary(List<ChatMessageData> messagesExtracted,
            ModelConfiguration modelConfiguration) {

        TextGenerationRequest request = createSummarizationRequest(messagesExtracted, modelConfiguration);
        return openAiPort.generateTextFrom(request)
                .map(summarizationResponse -> {
                    String summary = summarizationResponse.getOutputText();
                    Map<String, Object> processedContext = new HashMap<>();

                    processedContext.put(RETRIEVED_MESSAGES, messagesExtracted);
                    processedContext.put(SUMMARY, summary.trim());

                    return processedContext;
                });
    }

    private Map<String, Object> addInitialMessagesToContext(Map<String, Object> processedContext,
            int reservedTokensForStory) {

        List<ChatMessageData> retrievedMessages = (List<ChatMessageData>) processedContext.get(RETRIEVED_MESSAGES);
        List<String> messageHistory = new ArrayList<>();

        IntStream.range(0, retrievedMessages.size())
                .takeWhile(index -> index < 5)
                .mapToObj(retrievedMessages::get)
                .takeWhile(messageData -> {
                    int tokensInMessage = tokenizerPort.getTokenCountFrom(messageData.getContent());
                    int tokensInContext = tokenizerPort.getTokenCountFrom(stringifyList(messageHistory));

                    int tokensLeftInContext = reservedTokensForStory - tokensInContext;

                    return tokensInMessage <= tokensLeftInContext;
                })
                .map(ChatMessageData::getContent)
                .forEach(messageHistory::addFirst);

        retrievedMessages.removeIf(messageData -> messageHistory.contains(messageData.getContent()));

        processedContext.put(RETRIEVED_MESSAGES, retrievedMessages);
        processedContext.put(MESSAGE_HISTORY, messageHistory);

        return processedContext;
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

    private Map<String, Object> addExtraMessagesToContext(Map<String, Object> processedContext,
            String channelId, int reservedTokensForStory) {

        String summary = (String) processedContext.get(SUMMARY);
        List<String> messageHistory = (List<String>) processedContext.get(MESSAGE_HISTORY);
        List<ChatMessageData> retrievedMessages = (List<ChatMessageData>) processedContext.get(RETRIEVED_MESSAGES);

        int tokensInSummary = tokenizerPort.getTokenCountFrom(summary);

        retrievedMessages.stream()
                .takeWhile(messageData -> {
                    int tokensInMessage = tokenizerPort.getTokenCountFrom(messageData.getContent());
                    int tokensInContext = tokenizerPort.getTokenCountFrom(
                            stringifyList(messageHistory)) + tokensInSummary;

                    int tokensLeftInContext = reservedTokensForStory - tokensInContext;

                    return tokensInMessage <= tokensLeftInContext;
                })
                .map(ChatMessageData::getContent)
                .forEach(messageHistory::addFirst);

        retrievedMessages.removeIf(messageData -> messageHistory.contains(messageData.getContent()));

        processedContext.put(MESSAGE_HISTORY, messageHistory);
        processedContext.put(RETRIEVED_MESSAGES, retrievedMessages);

        return processedContext;
    }

    private String stringifyList(List<String> list) {

        return list.stream().collect(Collectors.joining(LF));
    }

    private TextGenerationRequest createSummarizationRequest(List<ChatMessageData> messagesExtracted,
            ModelConfiguration modelConfiguration) {

        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessages.addAll(messagesExtracted.stream()
                .map(this::mapToMessage)
                .collect(Collectors.toCollection(ArrayList::new)));

        chatMessages.addFirst(ChatMessage.build(SYSTEM, "Summarize the contents of the conversation in an "
                + "understandable and complete way, preferrably in a single paragraph. The summary can be long "
                + "and detailed, a slong as it fits a single paragraph. Don't acknowledge the instruction, "
                + "just generate the summary and nothing else"));

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

    private ChatMessage mapToMessage(ChatMessageData messageData) {

        return ChatMessage.build(USER, messageData.getContent());
    }
}
