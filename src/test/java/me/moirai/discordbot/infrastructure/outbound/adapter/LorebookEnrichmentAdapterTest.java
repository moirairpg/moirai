package me.moirai.discordbot.infrastructure.outbound.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.core.application.port.ChatMessagePort;
import me.moirai.discordbot.core.application.usecase.discord.DiscordMessageData;
import me.moirai.discordbot.core.application.usecase.discord.DiscordMessageDataFixture;
import me.moirai.discordbot.core.domain.port.TokenizerPort;
import me.moirai.discordbot.core.domain.world.WorldLorebookEntry;
import me.moirai.discordbot.core.domain.world.WorldService;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.ModelConfigurationRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.ModelConfigurationRequestFixture;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
public class LorebookEnrichmentAdapterTest {

    @Mock
    private TokenizerPort tokenizerPort;

    @Mock
    private WorldService worldService;

    @InjectMocks
    private LorebookEnrichmentAdapter service;

    @Mock
    private ChatMessagePort chatMessageService;

    private static final String LF = "\n";
    private static final String ENTRY_DESCRIPTION = "[ Description of %s: %s ]";

    @Test
    public void enrich_withValidInput_thenLorebookAndMessagesAdded() {
        // Given
        String worldId = "worldId";
        Map<String, Object> context = contextWithSummaryAndMessages(5);
        List<DiscordMessageData> messages = (List<DiscordMessageData>) context.get("retrievedMessages");

        List<WorldLorebookEntry> lorebook = lorebookEntriesNumber(5);
        Map<String, Object> contextWithLorebook = new HashMap<>(context);
        contextWithLorebook.put("lorebook", stringifyList(lorebook
                .stream()
                .map(entryData -> String.format(ENTRY_DESCRIPTION, entryData.getName(), entryData.getDescription()))
                .toList()));

        ModelConfigurationRequest modelConfiguration = ModelConfigurationRequestFixture.gpt4Mini().build();

        when(worldService.findAllEntriesByRegex(eq(worldId), anyString()))
                .thenReturn(lorebook);

        when(tokenizerPort.getTokenCountFrom(anyString())).thenReturn(10);

        when(chatMessageService.addMessagesToContext(anyMap(), anyInt()))
                .thenReturn(contextWithLorebook);

        // When
        Map<String, Object> processedContext = service.enrichContextWithLorebook(messages, worldId, modelConfiguration);

        // Then
        assertThat(processedContext).containsKey("lorebook");

        String lorebookExtracted = (String) processedContext.get("lorebook");
        List<String> messageHistory = (List<String>) processedContext.get("messageHistory");
        List<DiscordMessageData> retrievedMessages = (List<DiscordMessageData>) processedContext
                .get("retrievedMessages");

        assertThat(messageHistory).hasSize(5);
        assertThat(retrievedMessages).isEmpty();

        assertThat(lorebookExtracted)
                .isEqualTo("[ Description of Entry 1: Description 1 ]\n"
                        + "[ Description of Entry 2: Description 2 ]\n"
                        + "[ Description of Entry 3: Description 3 ]\n"
                        + "[ Description of Entry 4: Description 4 ]\n"
                        + "[ Description of Entry 5: Description 5 ]");
    }

    @Test
    public void enrich_withNoEntriesFound_thenOnlyMessagesAdded() {
        // Given
        String worldId = "worldId";
        Map<String, Object> context = contextWithSummaryAndMessages(5);
        ModelConfigurationRequest modelConfiguration = ModelConfigurationRequestFixture.gpt4Mini().build();
        List<DiscordMessageData> messages = (List<DiscordMessageData>) context.get("retrievedMessages");

        when(worldService.findAllEntriesByRegex(eq(worldId), anyString()))
                .thenReturn(Collections.emptyList());

        when(chatMessageService.addMessagesToContext(anyMap(), anyInt()))
                .thenReturn(context);

        // When
        Map<String, Object> processedContext = service.enrichContextWithLorebook(messages, worldId, modelConfiguration);

        // Then
        assertThat(processedContext).doesNotContainKey("lorebook");

        List<String> messageHistory = (List<String>) processedContext.get("messageHistory");
        List<DiscordMessageData> retrievedMessages = (List<DiscordMessageData>) processedContext
                .get("retrievedMessages");
        assertThat(messageHistory).hasSize(5);
        assertThat(retrievedMessages).isEmpty();
    }

    @Test
    public void enrich_withNoExtraMessages_thenOnlyLorebookAdded() {
        // Given
        String worldId = "worldId";
        Map<String, Object> context = contextWithSummaryAndMessages(5);
        List<DiscordMessageData> messages = (List<DiscordMessageData>) context.get("retrievedMessages");

        List<WorldLorebookEntry> lorebook = lorebookEntriesNumber(5);
        Map<String, Object> contextWithLorebook = new HashMap<>(context);
        contextWithLorebook.put("lorebook", stringifyList(lorebook
                .stream()
                .map(entryData -> String.format(ENTRY_DESCRIPTION, entryData.getName(), entryData.getDescription()))
                .toList()));

        context.put("retrievedMessages", Collections.emptyList());

        ModelConfigurationRequest modelConfiguration = ModelConfigurationRequestFixture.gpt4Mini().build();

        when(worldService.findAllEntriesByRegex(eq(worldId), anyString()))
                .thenReturn(lorebook);

        when(tokenizerPort.getTokenCountFrom(anyString())).thenReturn(10);

        when(chatMessageService.addMessagesToContext(anyMap(), anyInt()))
                .thenReturn(contextWithLorebook);

        // When
        Map<String, Object> processedContext = service.enrichContextWithLorebook(messages, worldId, modelConfiguration);

        // Then
        assertThat(processedContext).containsKey("lorebook");

        String lorebookExtracted = (String) processedContext.get("lorebook");
        List<String> messageHistory = (List<String>) processedContext.get("messageHistory");
        List<DiscordMessageData> retrievedMessages = (List<DiscordMessageData>) processedContext
                .get("retrievedMessages");

        assertThat(messageHistory).hasSize(5);
        assertThat(retrievedMessages).isEmpty();

        assertThat(lorebookExtracted)
                .isEqualTo("[ Description of Entry 1: Description 1 ]\n"
                        + "[ Description of Entry 2: Description 2 ]\n"
                        + "[ Description of Entry 3: Description 3 ]\n"
                        + "[ Description of Entry 4: Description 4 ]\n"
                        + "[ Description of Entry 5: Description 5 ]");
    }

    @Test
    public void enrich_whenReservedTokensAreReached_thenNoExtraMessagesAreAdded() {
        // Given
        String worldId = "worldId";
        Map<String, Object> context = contextWithSummaryAndMessages(5);
        ModelConfigurationRequest modelConfiguration = ModelConfigurationRequestFixture.gpt4Mini().build();
        List<DiscordMessageData> messages = (List<DiscordMessageData>) context.get("retrievedMessages");

        List<WorldLorebookEntry> lorebook = lorebookEntriesNumber(5);
        Map<String, Object> contextWithLorebook = new HashMap<>(context);
        contextWithLorebook.put("lorebook", stringifyList(lorebook
                .stream()
                .map(entryData -> String.format(ENTRY_DESCRIPTION, entryData.getName(), entryData.getDescription()))
                .toList()));

        when(worldService.findAllEntriesByRegex(eq(worldId), anyString()))
                .thenReturn(lorebook);

        when(tokenizerPort.getTokenCountFrom(anyString())).thenReturn(10);

        when(chatMessageService.addMessagesToContext(anyMap(), anyInt()))
                .thenReturn(contextWithLorebook);

        // When
        Map<String, Object> processedContext = service.enrichContextWithLorebook(messages, worldId, modelConfiguration);

        // Then
        assertThat(processedContext).containsKey("lorebook");

        String lorebookExtracted = (String) processedContext.get("lorebook");
        List<String> messageHistory = (List<String>) processedContext.get("messageHistory");
        List<DiscordMessageData> retrievedMessages = (List<DiscordMessageData>) processedContext
                .get("retrievedMessages");

        assertThat(messageHistory).hasSize(5);
        assertThat(retrievedMessages).isEmpty();

        assertThat(lorebookExtracted)
                .isEqualTo("[ Description of Entry 1: Description 1 ]\n"
                        + "[ Description of Entry 2: Description 2 ]\n"
                        + "[ Description of Entry 3: Description 3 ]\n"
                        + "[ Description of Entry 4: Description 4 ]\n"
                        + "[ Description of Entry 5: Description 5 ]");
    }

    private List<WorldLorebookEntry> lorebookEntriesNumber(int number) {

        List<WorldLorebookEntry> entries = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            entries.add(WorldLorebookEntry.builder()
                    .name(String.format("Entry %s", i + 1))
                    .regex(String.format("[Ee]ntry %s", i + 1))
                    .description(String.format("Description %s", i + 1))
                    .build());
        }

        return entries;
    }

    private Map<String, Object> contextWithSummaryAndMessages(int items) {

        List<DiscordMessageData> messageDataList = new ArrayList<>();
        for (int i = 0; i < items; i++) {
            messageDataList.add(DiscordMessageDataFixture.messageData()
                    .id(String.valueOf(i + 1))
                    .content(String.format("Message %s", i + 1))
                    .build());
        }

        List<String> textMessages = new ArrayList<>(messageDataList.stream()
                .map(DiscordMessageData::getContent)
                .map(content -> String.format("User said before test said: %s", content))
                .toList());

        messageDataList
                .removeIf(message -> textMessages.stream().anyMatch(content -> content.endsWith(message.getContent())));

        Map<String, Object> context = new HashMap<>();
        context.put("summary", "This is the summary");
        context.put("retrievedMessages", messageDataList);
        context.put("messageHistory", textMessages);

        return context;
    }

    private String stringifyList(List<String> list) {

        return list.stream().collect(Collectors.joining(LF));
    }
}
