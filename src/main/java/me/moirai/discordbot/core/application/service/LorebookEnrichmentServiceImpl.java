package me.moirai.discordbot.core.application.service;

import static org.apache.commons.lang3.StringUtils.LF;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import me.moirai.discordbot.common.annotation.ApplicationService;
import me.moirai.discordbot.core.domain.channelconfig.ModelConfiguration;
import me.moirai.discordbot.core.domain.port.TokenizerPort;
import me.moirai.discordbot.core.domain.world.WorldLorebookEntry;
import me.moirai.discordbot.core.domain.world.WorldService;
import io.micrometer.common.util.StringUtils;
import reactor.core.publisher.Mono;

@ApplicationService
@SuppressWarnings("unchecked")
public class LorebookEnrichmentServiceImpl implements LorebookEnrichmentService {

    private static final String ENTRY_DESCRIPTION = "[ Description of %s: %s ]";
    private static final String MESSAGE_HISTORY = "messageHistory";
    private static final String LOREBOOK = "lorebook";
    private static final String SUMMARY = "summary";

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
    public Mono<Map<String, Object>> enrichContextWith(Map<String, Object> context, String worldId,
            ModelConfiguration modelConfiguration) {

        int totalTokens = modelConfiguration.getAiModel().getHardTokenLimit();
        int reservedTokensForLorebook = (int) Math.floor(totalTokens * 0.30);

        String summary = (String) context.get(SUMMARY);
        List<String> messageHistory = (List<String>) context.get(MESSAGE_HISTORY);
        String stringifiedStory = summary + LF + stringifyList(messageHistory);

        return Mono.just(worldService.findAllEntriesByRegex(worldId, stringifiedStory))
                .map(entries -> addEntriesFoundToContext(entries, context, reservedTokensForLorebook))
                .map(ctx -> chatMessageService.addMessagesToContext(ctx, reservedTokensForLorebook));
    }

    private Map<String, Object> addEntriesFoundToContext(List<WorldLorebookEntry> entries,
            Map<String, Object> context, int reservedTokensForLorebook) {

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
