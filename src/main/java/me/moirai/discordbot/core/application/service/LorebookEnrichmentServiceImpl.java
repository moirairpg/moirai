package me.moirai.discordbot.core.application.service;

import static org.apache.commons.lang3.StringUtils.LF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.micrometer.common.util.StringUtils;
import me.moirai.discordbot.common.annotation.ApplicationService;
import me.moirai.discordbot.core.domain.channelconfig.ModelConfiguration;
import me.moirai.discordbot.core.domain.port.TokenizerPort;
import me.moirai.discordbot.core.domain.world.WorldLorebookEntry;
import me.moirai.discordbot.core.domain.world.WorldService;
import me.moirai.discordbot.infrastructure.outbound.adapter.response.ChatMessageData;

@ApplicationService
public class LorebookEnrichmentServiceImpl implements LorebookEnrichmentService {

    private static final String ENTRY_DESCRIPTION = "[ Description of %s: %s ]";
    private static final String RETRIEVED_MESSAGES = "retrievedMessages";
    private static final String LOREBOOK = "lorebook";

    private final TokenizerPort tokenizerPort;
    private final WorldService worldService;
    private final ChatMessageService chatMessageService;

    public LorebookEnrichmentServiceImpl(TokenizerPort tokenizerPort, WorldService worldService,
            ChatMessageService chatMessageService) {

        this.tokenizerPort = tokenizerPort;
        this.worldService = worldService;
        this.chatMessageService = chatMessageService;
    }

    @Override
    public Map<String, Object> enrichContextWithLorebook(List<ChatMessageData> rawMessageHistory, String worldId,
            ModelConfiguration modelConfiguration) {

        int totalTokens = modelConfiguration.getAiModel().getHardTokenLimit();
        int reservedTokensForLorebook = (int) Math.floor(totalTokens * 0.30);

        List<String> messageHistory = rawMessageHistory.stream()
                .map(ChatMessageData::getContent)
                .toList();

        String stringifiedStory = stringifyList(messageHistory);

        Map<String, Object> context = new HashMap<>();
        context.put(RETRIEVED_MESSAGES, rawMessageHistory);

        List<WorldLorebookEntry> entriesFound = worldService.findAllEntriesByRegex(worldId, stringifiedStory);
        Map<String, Object> enrichedContext = addEntriesFoundToContext(entriesFound, context,
                reservedTokensForLorebook);

        return chatMessageService.addMessagesToContext(enrichedContext, reservedTokensForLorebook);
    }

    private Map<String, Object> addEntriesFoundToContext(List<WorldLorebookEntry> entries, Map<String, Object> context,
            int reservedTokensForLorebook) {

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

        String stringifiedLorebook = stringifyList(lorebook);
        if (StringUtils.isNotBlank(stringifiedLorebook)) {
            context.put(LOREBOOK, stringifiedLorebook);
        }

        return context;
    }

    private String stringifyList(List<String> list) {

        return list.stream().collect(Collectors.joining(LF));
    }
}
