package es.thalesalv.chatrpg.core.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.core.domain.channelconfig.ModelConfiguration;
import es.thalesalv.chatrpg.core.domain.channelconfig.ModelConfigurationFixture;
import es.thalesalv.chatrpg.core.domain.port.TokenizerPort;
import es.thalesalv.chatrpg.core.domain.world.WorldService;
import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntry;
import es.thalesalv.chatrpg.infrastructure.outbound.adapter.response.ChatMessageData;
import es.thalesalv.chatrpg.infrastructure.outbound.adapter.response.ChatMessageDataFixture;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
public class LorebookEnrichmentServiceImplTest {

    @Mock
    private TokenizerPort tokenizerPort;

    @Mock
    private WorldService worldService;

    @InjectMocks
    private LorebookEnrichmentServiceImpl service;

    @Test
    public void enrich_withValidInput_thenLorebookAndMessagesAdded() {
        // Given
        String worldId = "worldId";
        String botName = "botName";
        Map<String, Object> contextWithSummary = contextWithSummaryAndMessages(5);

        ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt3516k().build();

        when(worldService.findAllEntriesByRegex(eq(worldId), anyString()))
                .thenReturn(lorebookEntriesNumber(5));

        when(tokenizerPort.getTokenCountFrom(anyString())).thenReturn(10);

        // When
        Mono<Map<String, Object>> result = service.enrich(worldId, botName, contextWithSummary, modelConfiguration);

        // Then
        StepVerifier.create(result)
                .assertNext(processedContext -> {
                    assertThat(processedContext).containsKey("lorebook");

                    String lorebook = (String) processedContext.get("lorebook");
                    List<String> messageHistory = (List<String>) processedContext.get("messageHistory");
                    List<ChatMessageData> retrievedMessages = (List<ChatMessageData>) processedContext
                            .get("retrievedMessages");

                    assertThat(messageHistory).hasSize(5);
                    assertThat(retrievedMessages).isEmpty();

                    assertThat(lorebook)
                            .isEqualTo("[ Description of Entry 1: Description 1 ]\n"
                                    + "[ Description of Entry 2: Description 2 ]\n"
                                    + "[ Description of Entry 3: Description 3 ]\n"
                                    + "[ Description of Entry 4: Description 4 ]\n"
                                    + "[ Description of Entry 5: Description 5 ]");
                })
                .verifyComplete();
    }

    @Test
    public void enrich_withNoEntriesFound_thenOnlyMessagesAdded() {
        // Given
        String worldId = "worldId";
        String botName = "botName";
        Map<String, Object> contextWithSummary = contextWithSummaryAndMessages(5);
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt3516k().build();

        when(worldService.findAllEntriesByRegex(eq(worldId), anyString()))
                .thenReturn(Collections.emptyList());

        // When
        Mono<Map<String, Object>> result = service.enrich(worldId, botName, contextWithSummary, modelConfiguration);

        // Then
        StepVerifier.create(result)
                .assertNext(processedContext -> {
                    assertThat(processedContext).doesNotContainKey("lorebook");

                    List<String> messageHistory = (List<String>) processedContext.get("messageHistory");
                    List<ChatMessageData> retrievedMessages = (List<ChatMessageData>) processedContext
                            .get("retrievedMessages");
                    assertThat(messageHistory).hasSize(5);
                    assertThat(retrievedMessages).isEmpty();
                })
                .verifyComplete();
    }

    @Test
    public void enrich_withNoExtraMessages_thenOnlyLorebookAdded() {
        // Given
        String worldId = "worldId";
        String botName = "botName";
        Map<String, Object> contextWithSummary = contextWithSummaryAndMessages(5);
        contextWithSummary.put("retrievedMessages", Collections.emptyList());

        ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt3516k().build();

        when(worldService.findAllEntriesByRegex(eq(worldId), anyString()))
                .thenReturn(lorebookEntriesNumber(5));

        when(tokenizerPort.getTokenCountFrom(anyString())).thenReturn(10);

        // When
        Mono<Map<String, Object>> result = service.enrich(worldId, botName, contextWithSummary, modelConfiguration);

        // Then
        StepVerifier.create(result)
                .assertNext(processedContext -> {
                    assertThat(processedContext).containsKey("lorebook");

                    String lorebook = (String) processedContext.get("lorebook");
                    List<String> messageHistory = (List<String>) processedContext.get("messageHistory");
                    List<ChatMessageData> retrievedMessages = (List<ChatMessageData>) processedContext
                            .get("retrievedMessages");

                    assertThat(messageHistory).hasSize(5);
                    assertThat(retrievedMessages).isEmpty();

                    assertThat(lorebook)
                            .isEqualTo("[ Description of Entry 1: Description 1 ]\n"
                                    + "[ Description of Entry 2: Description 2 ]\n"
                                    + "[ Description of Entry 3: Description 3 ]\n"
                                    + "[ Description of Entry 4: Description 4 ]\n"
                                    + "[ Description of Entry 5: Description 5 ]");
                })
                .verifyComplete();
    }

    @Test
    public void enrich_whenReservedTokensAreReached_thenNoExtraMessagesAreAdded() {
        // Given
        String worldId = "worldId";
        String botName = "botName";
        Map<String, Object> contextWithSummary = contextWithSummaryAndMessages(5);
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt3516k().build();

        when(worldService.findAllEntriesByRegex(eq(worldId), anyString()))
                .thenReturn(lorebookEntriesNumber(5));

        when(tokenizerPort.getTokenCountFrom(anyString())).thenReturn(10);

        // When
        Mono<Map<String, Object>> result = service.enrich(worldId, botName, contextWithSummary, modelConfiguration);

        // Then
        StepVerifier.create(result)
                .assertNext(processedContext -> {
                    assertThat(processedContext).containsKey("lorebook");

                    String lorebook = (String) processedContext.get("lorebook");
                    List<String> messageHistory = (List<String>) processedContext.get("messageHistory");
                    List<ChatMessageData> retrievedMessages = (List<ChatMessageData>) processedContext
                            .get("retrievedMessages");

                    assertThat(messageHistory).hasSize(5);
                    assertThat(retrievedMessages).isEmpty();

                    assertThat(lorebook)
                            .isEqualTo("[ Description of Entry 1: Description 1 ]\n"
                                    + "[ Description of Entry 2: Description 2 ]\n"
                                    + "[ Description of Entry 3: Description 3 ]\n"
                                    + "[ Description of Entry 4: Description 4 ]\n"
                                    + "[ Description of Entry 5: Description 5 ]");
                })
                .verifyComplete();
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

        List<ChatMessageData> messageDataList = new ArrayList<>();
        for (int i = 0; i < items; i++) {
            messageDataList.add(ChatMessageDataFixture.messageData()
                    .id(String.valueOf(i + 1))
                    .content(String.format("Message %s", i + 1))
                    .build());
        }

        List<String> textMessages = new ArrayList<>(messageDataList.stream()
                .map(ChatMessageData::getContent)
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
}
