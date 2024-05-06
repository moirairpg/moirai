package es.thalesalv.chatrpg.core.application.service;

import static org.apache.commons.lang3.StringUtils.LF;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import es.thalesalv.chatrpg.common.annotation.ApplicationService;
import es.thalesalv.chatrpg.core.domain.channelconfig.ModelConfiguration;
import es.thalesalv.chatrpg.core.domain.port.TokenizerPort;
import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntry;
import es.thalesalv.chatrpg.core.domain.world.WorldService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@ApplicationService
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class LorebookEnrichmentServiceImpl implements LorebookEnrichmentService {

    private static final String ENTRY_DESCRIPTION = "[ Description of %s: %s ]";
    private static final String MESSAGE_HISTORY = "messageHistory";
    private static final String LOREBOOK = "lorebook";
    private static final String SUMMARY = "summary";

    private final TokenizerPort tokenizerPort;
    private final WorldService worldService;
    private final ChatMessageService chatMessageService;

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
