package me.moirai.discordbot.infrastructure.outbound.adapter;

import static me.moirai.discordbot.common.util.DefaultStringProcessors.replaceTemplateWithValueIgnoreCase;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.LF;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import io.micrometer.common.util.StringUtils;
import me.moirai.discordbot.common.util.StringProcessor;
import me.moirai.discordbot.core.application.port.ChatMessagePort;
import me.moirai.discordbot.core.application.port.LorebookEnrichmentPort;
import me.moirai.discordbot.core.application.usecase.discord.DiscordMessageData;
import me.moirai.discordbot.core.application.usecase.discord.DiscordUserDetails;
import me.moirai.discordbot.core.domain.port.TokenizerPort;
import me.moirai.discordbot.core.domain.world.WorldLorebookEntry;
import me.moirai.discordbot.core.domain.world.WorldService;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.ModelConfigurationRequest;

@Component("rpgLorebookEnrichmentPort")
public class RpgLorebookEnrichmentAdapter implements LorebookEnrichmentPort {

    private static final String ENTRY_DESCRIPTION = "[ Description of %s: %s ]";
    private static final String RETRIEVED_MESSAGES = "retrievedMessages";
    private static final String LOREBOOK = "lorebook";

    private final TokenizerPort tokenizerPort;
    private final WorldService worldService;
    private final ChatMessagePort chatMessageService;

    public RpgLorebookEnrichmentAdapter(
            TokenizerPort tokenizerPort,
            WorldService worldService,
            ChatMessagePort chatMessageService) {

        this.tokenizerPort = tokenizerPort;
        this.worldService = worldService;
        this.chatMessageService = chatMessageService;
    }

    @Override
    public Map<String, Object> enrichContextWithLorebook(List<DiscordMessageData> rawMessageHistory, String worldId,
            ModelConfigurationRequest modelConfiguration) {

        int totalTokens = modelConfiguration.getAiModel().getHardTokenLimit();
        int reservedTokensForLorebook = (int) Math.floor(totalTokens * 0.30);

        List<WorldLorebookEntry> entriesFound = findLorebookEntries(worldId, rawMessageHistory);
        List<DiscordMessageData> formattedHistory = enrichMessagesWithLorebook(rawMessageHistory, entriesFound);
        List<String> formattedEntries = formatEntriesForContext(entriesFound, reservedTokensForLorebook);

        Map<String, Object> context = new HashMap<>();
        context.put(RETRIEVED_MESSAGES, new ArrayList<>(formattedHistory));

        String stringifiedLorebook = stringifyList(formattedEntries);
        if (StringUtils.isNotBlank(stringifiedLorebook)) {
            context.put(LOREBOOK, stringifiedLorebook);
        }

        return chatMessageService.addMessagesToContext(context, reservedTokensForLorebook);
    }

    private List<WorldLorebookEntry> findLorebookEntries(String worldId, List<DiscordMessageData> rawMessageHistory) {

        List<WorldLorebookEntry> entriesInHistory = findLorebookEntriesInHistory(worldId, rawMessageHistory);
        List<WorldLorebookEntry> entriesByMention = findLorebookEntriesByMention(worldId, rawMessageHistory);
        List<WorldLorebookEntry> entriesByAuthor = findLorebookEntriesByAuthor(worldId, rawMessageHistory);

        Set<String> entryIdsNotDuplicated = new HashSet<>();
        return Stream.of(entriesInHistory, entriesByMention, entriesByAuthor)
                .flatMap(Collection::stream)
                .filter(entry -> entryIdsNotDuplicated.add(entry.getId()))
                .toList();
    }

    private List<WorldLorebookEntry> findLorebookEntriesInHistory(
            String worldId, List<DiscordMessageData> rawMessageHistory) {

        List<String> messageHistory = rawMessageHistory.stream()
                .map(DiscordMessageData::getContent)
                .toList();

        return worldService.findAllLorebookEntriesByRegex(stringifyList(messageHistory), worldId);
    }

    private List<WorldLorebookEntry> findLorebookEntriesByMention(String worldId,
            List<DiscordMessageData> rawMessageHistory) {

        return rawMessageHistory.stream()
                .flatMap(message -> message.getMentionedUsers().stream())
                .map(user -> worldService.findLorebookEntryByPlayerDiscordId(user.getId(), worldId))
                .toList();
    }

    private List<WorldLorebookEntry> findLorebookEntriesByAuthor(String worldId,
            List<DiscordMessageData> rawMessageHistory) {
        return rawMessageHistory.stream()
                .map(message -> worldService.findLorebookEntryByPlayerDiscordId(message.getAuthor().getId(), worldId))
                .toList();
    }

    private List<DiscordMessageData> enrichMessagesWithLorebook(List<DiscordMessageData> rawMessageHistory,
            List<WorldLorebookEntry> lorebook) {

        List<DiscordMessageData> messagesFormattedForMentions = formatMessagesWithMentions(
                rawMessageHistory, lorebook);

        return formatMessagesWithAuthor(messagesFormattedForMentions, lorebook);
    }

    private List<DiscordMessageData> formatMessagesWithMentions(
            List<DiscordMessageData> messageHistory, List<WorldLorebookEntry> lorebook) {

        return messageHistory.stream()
                .flatMap(message -> {
                    List<DiscordMessageData> messages = new ArrayList<>();

                    if (isEmpty(message.getMentionedUsers())) {
                        messages.add(message);
                    }

                    for (DiscordUserDetails mentionedUser : message.getMentionedUsers()) {
                        messages.add(lorebook.stream()
                                .filter(entry -> mentionedUser.getId().equals(entry.getPlayerDiscordId()))
                                .findFirst()
                                .map(entry -> {
                                    StringProcessor processor = new StringProcessor();
                                    processor.addRule(replaceTemplateWithValueIgnoreCase(
                                            entry.getName(), mentionedUser.getNickname()));

                                    processor.addRule(replaceTemplateWithValueIgnoreCase(
                                            entry.getName(), mentionedUser.getMention()));

                                    processor.addRule(replaceTemplateWithValueIgnoreCase(
                                            entry.getName(), mentionedUser.getUsername()));

                                    return DiscordMessageData.builder()
                                            .id(message.getId())
                                            .author(message.getAuthor())
                                            .content(processor.process(message.getContent()))
                                            .build();
                                })
                                .orElse(message));
                    }

                    return messages.stream();
                })
                .toList();
    }

    private List<DiscordMessageData> formatMessagesWithAuthor(
            List<DiscordMessageData> rawMessageHistory, List<WorldLorebookEntry> lorebook) {

        return rawMessageHistory.stream()
                .map(message -> lorebook.stream()
                        .filter(entry -> message.getAuthor().getId().equals(entry.getPlayerDiscordId()))
                        .findFirst()
                        .map(entry -> {
                            DiscordUserDetails author = DiscordUserDetails.builder()
                                    .id(message.getAuthor().getId())
                                    .mention(message.getAuthor().getMention())
                                    .username(message.getAuthor().getUsername())
                                    .nickname(entry.getName())
                                    .build();

                            StringProcessor processor = new StringProcessor();
                            processor.addRule(replaceTemplateWithValueIgnoreCase(
                                    entry.getName(), message.getAuthor().getNickname()));

                            processor.addRule(replaceTemplateWithValueIgnoreCase(
                                    entry.getName(), message.getAuthor().getMention()));

                            processor.addRule(replaceTemplateWithValueIgnoreCase(
                                    entry.getName(), message.getAuthor().getUsername()));

                            return DiscordMessageData.builder()
                                    .id(message.getId())
                                    .author(author)
                                    .content(processor.process(message.getContent()))
                                    .build();
                        })
                        .orElse(message))
                .toList();
    }

    private List<String> formatEntriesForContext(List<WorldLorebookEntry> entries, int reservedTokensForLorebook) {

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

        return lorebook;
    }

    private String stringifyList(List<String> list) {

        return list.stream().collect(Collectors.joining(LF));
    }
}
