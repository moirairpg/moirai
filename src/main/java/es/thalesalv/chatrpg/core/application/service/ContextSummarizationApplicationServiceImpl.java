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

import discord4j.discordjson.json.MessageData;
import es.thalesalv.chatrpg.core.application.model.request.ChatMessage;
import es.thalesalv.chatrpg.core.application.model.request.TextGenerationRequest;
import es.thalesalv.chatrpg.core.application.port.DiscordChannelOperationsPort;
import es.thalesalv.chatrpg.core.application.port.OpenAiPort;
import es.thalesalv.chatrpg.core.domain.channelconfig.ModelConfiguration;
import es.thalesalv.chatrpg.core.domain.port.TokenizerPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class ContextSummarizationApplicationServiceImpl implements ContextSummarizationApplicationService {

    private static final String SAYS = "%s says: %s";
    private static final String PERIOD = ".";
    private static final String SENTENCE_EXPRESSION = "((\\. |))(?:[ A-Za-z0-9-\"'&(),:;<>\\/\\\\]|\\.(?! ))+[\\?\\.\\!\\;'\"]$";
    private static final String SUMMARY = "summary";
    private static final String RETRIEVED_MESSAGES = "retrievedMessages";
    private static final String MESSAGE_HISTORY = "messageHistory";

    private final DiscordChannelOperationsPort discordChannelOperationsPort;
    private final OpenAiPort openAiPort;
    private final TokenizerPort tokenizerPort;

    @Override
    public Mono<Map<String, Object>> summarizeWith(String channelId, String messageId, String botName,
            ModelConfiguration modelConfiguration) {

        int totalTokens = modelConfiguration.getAiModel().getHardTokenLimit();
        int reservedTokensForStory = (int) Math.floor(totalTokens * 0.30);

        return discordChannelOperationsPort.retrieveLastMessagesInclusiveFrom(channelId, messageId, 100)
                .zipWith(discordChannelOperationsPort.getMessageById(channelId, messageId))
                .flatMap(zipped -> generateSummary(zipped, modelConfiguration))
                .map(processedContext -> {
                    processedContext.putAll(addInitialMessagesToContext(processedContext, reservedTokensForStory));
                    processedContext.putAll(addSummaryToContext(processedContext, reservedTokensForStory));
                    processedContext.putAll(addExtraMessagesToContext(processedContext,
                            channelId, reservedTokensForStory));

                    return processedContext;
                });
    }

    private Mono<? extends Map<String, Object>> generateSummary(Tuple2<List<MessageData>, MessageData> zipped,
            ModelConfiguration modelConfiguration) {

        TextGenerationRequest request = createSummarizationRequest(zipped.getT2(), zipped.getT1(), modelConfiguration);
        return openAiPort.generateTextFrom(request)
                .map(summarizationResponse -> {
                    String summary = summarizationResponse.getOutputText();
                    Map<String, Object> processedContext = new HashMap<>();

                    processedContext.put(RETRIEVED_MESSAGES, zipped.getT1());
                    processedContext.put(SUMMARY, summary.trim());

                    return processedContext;
                });
    }

    private Map<String, Object> addInitialMessagesToContext(Map<String, Object> processedContext,
            int reservedTokensForStory) {

        List<MessageData> retrievedMessages = (List<MessageData>) processedContext.get(RETRIEVED_MESSAGES);
        List<String> messageHistory = new ArrayList<>();

        IntStream.range(0, retrievedMessages.size())
                .takeWhile(index -> index < 5)
                .mapToObj(retrievedMessages::get)
                .takeWhile(messageData -> {
                    String message = formatMessage(messageData);

                    int tokensInMessage = tokenizerPort.getTokenCountFrom(message);
                    int tokensInContext = tokenizerPort.getTokenCountFrom(stringifyList(messageHistory));

                    int tokensLeftInContext = reservedTokensForStory - tokensInContext;

                    return tokensInMessage <= tokensLeftInContext;
                })
                .map(messageData -> formatMessage(messageData))
                .forEach(messageHistory::addFirst);

        retrievedMessages.removeIf(message -> messageHistory.contains(formatMessage(message)));

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
        List<MessageData> retrievedMessages = (List<MessageData>) processedContext.get(RETRIEVED_MESSAGES);

        int tokensInSummary = tokenizerPort.getTokenCountFrom(summary);

        retrievedMessages.stream()
                .takeWhile(messageData -> {
                    String message = formatMessage(messageData);

                    int tokensInMessage = tokenizerPort.getTokenCountFrom(message);
                    int tokensInContext = tokenizerPort.getTokenCountFrom(
                            stringifyList(messageHistory)) + tokensInSummary;

                    int tokensLeftInContext = reservedTokensForStory - tokensInContext;

                    return tokensInMessage <= tokensLeftInContext;
                })
                .map(messageData -> formatMessage(messageData))
                .forEach(messageHistory::addFirst);

        retrievedMessages.removeIf(message -> messageHistory.contains(formatMessage(message)));

        processedContext.put(MESSAGE_HISTORY, messageHistory);
        processedContext.put(RETRIEVED_MESSAGES, retrievedMessages);

        return processedContext;
    }

    private String stringifyList(List<String> list) {

        return list.stream().collect(Collectors.joining(LF));
    }

    private String formatMessage(MessageData message) {
        return String.format(SAYS, message.author().username(), message.content());
    }

    private TextGenerationRequest createSummarizationRequest(MessageData lastMessage, List<MessageData> messages,
            ModelConfiguration modelConfiguration) {

        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessages.addAll(messages.stream()
                .map(this::mapToMessage)
                .collect(Collectors.toCollection(ArrayList::new)));

        chatMessages.addFirst(ChatMessage.build(SYSTEM, "Summarize the contents of the story in a single paragraph"));

        chatMessages.addFirst(mapToMessage(lastMessage));

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

    private ChatMessage mapToMessage(MessageData messageData) {

        return ChatMessage.build(USER, messageData.content());
    }
}
