package es.thalesalv.chatrpg.core.application.service;

import static org.apache.commons.lang3.StringUtils.LF;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import discord4j.discordjson.json.MessageData;
import es.thalesalv.chatrpg.core.domain.channelconfig.ModelConfiguration;
import es.thalesalv.chatrpg.core.domain.port.TokenizerPort;
import es.thalesalv.chatrpg.core.domain.world.WorldDomainService;
import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntry;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class LorebookEnrichmentServiceImpl implements LorebookEnrichmentService {

    private static final String SAYS = "%s says: %s";
    private static final String ENTRY_DESCRIPTION = "[ Description of %s: %s ]";
    private static final String MESSAGE_HISTORY = "messageHistory";
    private static final String LOREBOOK = "lorebook";
    private static final String RETRIEVED_MESSAGES = "retrievedMessages";

    private final TokenizerPort tokenizerPort;
    private final WorldDomainService worldDomainService;

    @Override
    public Mono<Map<String, Object>> enrich(String worldId, String botName, Map<String, Object> contextWithSummary,
            ModelConfiguration modelConfiguration) {

        int totalTokens = modelConfiguration.getAiModel().getHardTokenLimit();
        int reservedTokensForLorebook = (int) Math.floor(totalTokens * 0.30);

        String summary = (String) contextWithSummary.get("summary");
        List<String> messageHistory = (List<String>) contextWithSummary.get("messageHistory");
        String context = summary + LF + stringifyList(messageHistory);

        return Mono.just(worldDomainService.findAllEntriesByRegex(worldId, context))
                .map(entries -> addEntriesFoundToContext(entries, contextWithSummary, reservedTokensForLorebook))
                .map(processedContext -> addExtraMessagesToContext(processedContext, reservedTokensForLorebook));
    }

    private Map<String, Object> addEntriesFoundToContext(List<WorldLorebookEntry> entries,
            Map<String, Object> processedContext, int reservedTokensForLorebook) {

        List<String> lorebook = new ArrayList<>();

        entries.stream()
                .takeWhile(entryData -> {
                    String entry = String.format(ENTRY_DESCRIPTION, entryData.getName(), entryData.getDescription());
                    String stringifiedLorebook = stringifyList(lorebook);

                    int tokensInEntry = tokenizerPort.getTokenCountFrom(entry);
                    int tokensInContext = tokenizerPort.getTokenCountFrom(stringifiedLorebook);

                    int tokensLeftInContext = reservedTokensForLorebook - tokensInContext;

                    return tokensInEntry <= tokensLeftInContext;
                })
                .map(entryData -> String.format(ENTRY_DESCRIPTION, entryData.getName(), entryData.getDescription()))
                .forEach(lorebook::add);

        processedContext.put(LOREBOOK, stringifyList(lorebook));

        return processedContext;
    }

    private Map<String, Object> addExtraMessagesToContext(Map<String, Object> contextWithLorebook,
            int reservedTokensForStory) {

        String lorebook = (String) contextWithLorebook.get(LOREBOOK);
        List<String> messageHistory = (List<String>) contextWithLorebook.get(MESSAGE_HISTORY);
        List<MessageData> retrievedMessages = (List<MessageData>) contextWithLorebook.get(RETRIEVED_MESSAGES);

        int tokensInLorebook = tokenizerPort.getTokenCountFrom(lorebook);

        retrievedMessages.stream()
                .takeWhile(messageData -> {
                    String message = String.format(SAYS, messageData.author().username(),
                            messageData.content());

                    int tokensInMessage = tokenizerPort.getTokenCountFrom(message);
                    int tokensInContext = tokenizerPort.getTokenCountFrom(stringifyList(messageHistory))
                            + tokensInLorebook;

                    int tokensLeftInContext = reservedTokensForStory - tokensInContext;

                    return tokensInMessage <= tokensLeftInContext;
                })
                .map(messageData -> String.format(SAYS, messageData.author().username(), messageData.content()))
                .forEach(messageHistory::addFirst);

        retrievedMessages.removeIf(message -> messageHistory.contains(formatMessage(message)));

        contextWithLorebook.put(MESSAGE_HISTORY, messageHistory);
        contextWithLorebook.put(RETRIEVED_MESSAGES, retrievedMessages);

        return contextWithLorebook;
    }

    private String stringifyList(List<String> list) {

        return list.stream().collect(Collectors.joining(LF));
    }

    private String formatMessage(MessageData message) {
        return String.format(SAYS, message.author().username(), message.content());
    }
}
