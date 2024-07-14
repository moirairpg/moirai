package me.moirai.discordbot.core.application.service;

import static org.apache.commons.lang3.StringUtils.LF;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.ListUtils;

import me.moirai.discordbot.common.annotation.ApplicationService;
import me.moirai.discordbot.core.domain.port.TokenizerPort;
import me.moirai.discordbot.infrastructure.outbound.adapter.response.ChatMessageData;

@ApplicationService
@SuppressWarnings("unchecked")
public class ChatMessageServiceImpl implements ChatMessageService {

    private static final String MESSAGE_HISTORY = "messageHistory";
    private static final String RETRIEVED_MESSAGES = "retrievedMessages";

    private final TokenizerPort tokenizerPort;

    public ChatMessageServiceImpl(TokenizerPort tokenizerPort) {
        this.tokenizerPort = tokenizerPort;
    }

    @Override
    public Map<String, Object> addMessagesToContext(Map<String, Object> context, int reservedTokens) {

        List<ChatMessageData> retrievedMessages = (List<ChatMessageData>) context.get(RETRIEVED_MESSAGES);
        List<String> messageHistory = ListUtils.defaultIfNull(
                (List<String>) context.get(MESSAGE_HISTORY), new ArrayList<>());

        retrievedMessages.stream()
                .takeWhile(messageData -> {
                    int tokensInMessage = tokenizerPort.getTokenCountFrom(messageData.getContent());
                    int tokensInContext = tokenizerPort.getTokenCountFrom(stringifyList(messageHistory));
                    int tokensLeftInContext = reservedTokens - tokensInContext;

                    return tokensInMessage <= tokensLeftInContext;
                })
                .map(ChatMessageData::getContent)
                .forEach(messageHistory::addFirst);

        retrievedMessages.removeIf(messageData -> messageHistory.contains(messageData.getContent()));

        context.put(RETRIEVED_MESSAGES, retrievedMessages);
        context.put(MESSAGE_HISTORY, messageHistory);

        return context;
    }

    @Override
    public Map<String, Object> addMessagesToContext(Map<String, Object> context,
            int reservedTokens, int amountOfMessage) {

        List<ChatMessageData> retrievedMessages = (List<ChatMessageData>) context.get(RETRIEVED_MESSAGES);
        List<String> messageHistory = ListUtils.defaultIfNull(
                (List<String>) context.get(MESSAGE_HISTORY), new ArrayList<>());

        IntStream.range(0, retrievedMessages.size())
                .takeWhile(index -> index < amountOfMessage)
                .mapToObj(retrievedMessages::get)
                .takeWhile(messageData -> {
                    int tokensInMessage = tokenizerPort.getTokenCountFrom(messageData.getContent());
                    int tokensInContext = tokenizerPort.getTokenCountFrom(stringifyList(messageHistory));
                    int tokensLeftInContext = reservedTokens - tokensInContext;

                    return tokensInMessage <= tokensLeftInContext;
                })
                .map(ChatMessageData::getContent)
                .forEach(messageHistory::addFirst);

        retrievedMessages.removeIf(messageData -> messageHistory.contains(messageData.getContent()));

        context.put(RETRIEVED_MESSAGES, retrievedMessages);
        context.put(MESSAGE_HISTORY, messageHistory);

        return context;
    }

    @Override
    public Map<String, Object> addMessagesToContext(Map<String, Object> context,
            int reservedTokens, String assetManipulated) {

        String stringifiedAsset = (String) context.get(assetManipulated);
        List<ChatMessageData> retrievedMessages = (List<ChatMessageData>) context.get(RETRIEVED_MESSAGES);
        List<String> messageHistory = ListUtils.defaultIfNull(
                (List<String>) context.get(MESSAGE_HISTORY), new ArrayList<>());

        int tokensInAsset = tokenizerPort.getTokenCountFrom(stringifiedAsset);

        retrievedMessages.stream()
                .takeWhile(messageData -> {
                    int tokensInMessage = tokenizerPort.getTokenCountFrom(messageData.getContent());
                    int tokensInContext = tokenizerPort.getTokenCountFrom(stringifyList(messageHistory))
                            + tokensInAsset;

                    int tokensLeftInContext = reservedTokens - tokensInContext;

                    return tokensInMessage <= tokensLeftInContext;
                })
                .map(ChatMessageData::getContent)
                .forEach(messageHistory::addFirst);

        retrievedMessages.removeIf(messageData -> messageHistory.contains(messageData.getContent()));

        context.put(MESSAGE_HISTORY, messageHistory);
        context.put(RETRIEVED_MESSAGES, retrievedMessages);

        return context;
    }

    private String stringifyList(List<String> list) {

        return list.stream().collect(Collectors.joining(LF));
    }
}
