package es.thalesalv.chatrpg.core.application.service;

import static es.thalesalv.chatrpg.core.domain.channelconfig.ArtificialIntelligenceModel.GPT35_16K;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

import es.thalesalv.chatrpg.core.application.model.request.TextGenerationRequest;
import es.thalesalv.chatrpg.core.application.model.result.TextGenerationResultFixture;
import es.thalesalv.chatrpg.core.application.port.DiscordChannelPort;
import es.thalesalv.chatrpg.core.application.port.OpenAiPort;
import es.thalesalv.chatrpg.core.domain.channelconfig.ModelConfiguration;
import es.thalesalv.chatrpg.core.domain.channelconfig.ModelConfigurationFixture;
import es.thalesalv.chatrpg.core.domain.port.TokenizerPort;
import es.thalesalv.chatrpg.infrastructure.outbound.adapter.response.ChatMessageData;
import es.thalesalv.chatrpg.infrastructure.outbound.adapter.response.ChatMessageDataFixture;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
public class ContextSummarizationServiceImplTest {

    @Mock
    private DiscordChannelPort discordChannelOperationsPort;

    @Mock
    private OpenAiPort openAiPort;

    @Mock
    private TokenizerPort tokenizerPort;

    @Mock
    private ChatMessageService chatMessageService;

    @InjectMocks
    private StorySummarizationServiceImpl service;

    @Test
    public void summarizeWith_validInput_thenSummaryGenerated() {

        // Given
        String guildId = "validGuildId";
        String channelId = "validChannelId";
        String messageId = "validMessageId";
        String generatedSummary = "Generated summary";
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt3516k().build();

        Map<String, Object> context = createContextWithMessageNumber(3);
        List<ChatMessageData> messages = (List<ChatMessageData>) context.get("retrievedMessages");

        when(discordChannelOperationsPort.retrieveEntireHistoryFrom(eq(guildId), eq(channelId),
                eq(messageId), anyList()))
                .thenReturn(Mono.just(messages));

        when(openAiPort.generateTextFrom(any(TextGenerationRequest.class)))
                .thenReturn(Mono.just(TextGenerationResultFixture.create()
                        .outputText(generatedSummary)
                        .build()));

        when(chatMessageService.addMessagesToContext(anyMap(), anyInt(), anyInt()))
                .thenReturn(context);

        List<String> mentionedUserIds = org.assertj.core.util.Lists.list("4234234", "42344234256");

        // When
        Mono<Map<String, Object>> result = service.summarizeWith(guildId, channelId, messageId, "botName",
                modelConfiguration, mentionedUserIds);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(processedContext -> (processedContext.containsKey("summary")
                        && processedContext.get("summary").equals(generatedSummary)))
                .verifyComplete();
    }

    @Test
    public void summarizeWith_emptyMessageHistory_thenEmptySummaryReturned() {

        // Given
        String guildId = "validGuildId";
        String channelId = "channelIdWithEmptyHistory";
        String messageId = "messageId";
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt3516k().build();
        Map<String, Object> context = createContextWithMessageNumber(3);

        when(discordChannelOperationsPort.retrieveEntireHistoryFrom(eq(guildId), eq(channelId),
                eq(messageId), anyList()))
                .thenReturn(Mono.just(Collections.emptyList()));

        when(openAiPort.generateTextFrom(any(TextGenerationRequest.class)))
                .thenReturn(Mono.just(TextGenerationResultFixture.create()
                        .outputText("")
                        .build()));

        when(chatMessageService.addMessagesToContext(anyMap(), anyInt(), anyInt()))
                .thenReturn(context);

        List<String> mentionedUserIds = org.assertj.core.util.Lists.list("4234234", "42344234256");

        // When
        Mono<Map<String, Object>> result = service.summarizeWith(guildId, channelId, messageId, "botName",
                modelConfiguration, mentionedUserIds);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(processedContext -> processedContext.containsKey("summary")
                        && ((String) processedContext.get("summary")).isEmpty())
                .verifyComplete();
    }

    @Test
    public void summarizeWith_whenSummaryExceedsTokenLimit_thenSummaryShouldBeTrimmed() {

        // Given
        String guildId = "validGuildId";
        String channelId = "testChannelId";
        String messageId = "testMessageId";
        String botName = "testBot";
        String longSummary = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam egestas dignissim velit, ut pellentesque ipsum. Ut auctor ipsum suscipit sapien tristique suscipit. Donec bibendum lectus neque, nec porttitor turpis commodo at. Nulla facilisi. Nulla gravida interdum tempor. Mauris iaculis pharetra leo.";
        String trimmedSummary = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam egestas dignissim velit, ut pellentesque ipsum. Ut auctor ipsum suscipit sapien tristique suscipit. Donec bibendum lectus neque, nec porttitor turpis commodo at. Nulla facilisi. Nulla gravida interdum tempor.";
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt3516k()
                .aiModel(GPT35_16K)
                .build();

        Map<String, Object> context = createContextWithMessageNumber(3);
        List<ChatMessageData> messages = (List<ChatMessageData>) context.get("retrievedMessages");

        List<String> mentionedUserIds = org.assertj.core.util.Lists.list("4234234", "42344234256");

        when(discordChannelOperationsPort.retrieveEntireHistoryFrom(eq(guildId), eq(channelId),
                eq(messageId), anyList()))
                .thenReturn(Mono.just(messages));

        when(openAiPort.generateTextFrom(any(TextGenerationRequest.class)))
                .thenReturn(Mono.just(TextGenerationResultFixture.create()
                        .outputText(longSummary)
                        .build()));

        when(tokenizerPort.getTokenCountFrom(not(eq(longSummary))))
                .thenReturn(1000)
                .thenReturn(200);

        when(tokenizerPort.getTokenCountFrom(contains("Message")))
                .thenReturn(20)
                .thenReturn(20)
                .thenReturn(1000)
                .thenReturn(20)
                .thenReturn(1000);

        when(tokenizerPort.getTokenCountFrom(eq(longSummary)))
                .thenReturn(20000);

        when(chatMessageService.addMessagesToContext(anyMap(), anyInt(), anyInt()))
                .thenReturn(context);

        // When
        Mono<Map<String, Object>> result = service.summarizeWith(guildId, channelId, messageId,
                botName, modelConfiguration, mentionedUserIds);

        // Then
        StepVerifier.create(result)
                .assertNext(processedContext -> {
                    assertThat(processedContext).containsKey("summary");
                    assertThat(processedContext).containsKey("messageHistory");

                    String summary = (String) processedContext.get("summary");
                    assertThat(summary).isNotBlank().isEqualTo(trimmedSummary);

                    List<String> messageHistory = (List<String>) processedContext.get("messageHistory");
                    assertThat(messageHistory).isNotNull().isNotEmpty().hasSize(3);
                })
                .verifyComplete();

        verify(openAiPort, times(1)).generateTextFrom(any(TextGenerationRequest.class));
    }

    @Test
    public void summarizeWith_whenSingleSentenceSummaryExceedsTokenLimit_thenSummaryShouldBeTrimmedToNothing() {

        // Given
        String guildId = "testGuildId";
        String channelId = "testChannelId";
        String messageId = "testMessageId";
        String botName = "testBot";
        String longSummary = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt3516k()
                .aiModel(GPT35_16K)
                .build();

        Map<String, Object> context = createContextWithMessageNumber(3);
        List<ChatMessageData> messages = (List<ChatMessageData>) context.get("retrievedMessages");

        List<String> mentionedUserIds = org.assertj.core.util.Lists.list("4234234", "42344234256");

        when(discordChannelOperationsPort.retrieveEntireHistoryFrom(eq(guildId), eq(channelId),
                eq(messageId),
                anyList()))
                .thenReturn(Mono.just(messages));

        when(openAiPort.generateTextFrom(any(TextGenerationRequest.class)))
                .thenReturn(Mono.just(TextGenerationResultFixture.create()
                        .outputText(longSummary)
                        .build()));

        when(tokenizerPort.getTokenCountFrom(not(eq(longSummary))))
                .thenReturn(1000)
                .thenReturn(200);

        when(tokenizerPort.getTokenCountFrom(contains("Message")))
                .thenReturn(20)
                .thenReturn(20)
                .thenReturn(1000)
                .thenReturn(20)
                .thenReturn(1000);

        when(tokenizerPort.getTokenCountFrom(eq(longSummary)))
                .thenReturn(20000);

        when(chatMessageService.addMessagesToContext(anyMap(), anyInt(), anyInt()))
                .thenReturn(context);

        // When
        Mono<Map<String, Object>> result = service.summarizeWith(guildId, channelId, messageId,
                botName, modelConfiguration, mentionedUserIds);

        // Then
        StepVerifier.create(result)
                .assertNext(processedContext -> {
                    assertThat(processedContext).containsKey("summary");
                    assertThat(processedContext).containsKey("messageHistory");

                    String summary = (String) processedContext.get("summary");
                    assertThat(summary).isBlank();

                    List<String> messageHistory = (List<String>) processedContext.get("messageHistory");
                    assertThat(messageHistory).isNotNull().isNotEmpty().hasSize(3);
                })
                .verifyComplete();

        verify(openAiPort, times(1)).generateTextFrom(any(TextGenerationRequest.class));
    }

    @Test
    public void summarizeWith_whenSummaryNotExceedsTokenLimit_thenSummaryShouldNotBeTrimmed() {

        // Given
        String guildId = "testGuildId";
        String channelId = "testChannelId";
        String messageId = "testMessageId";
        String botName = "testBot";
        String longSummary = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam egestas dignissim velit, ut pellentesque ipsum. Ut auctor ipsum suscipit sapien tristique suscipit. Donec bibendum lectus neque, nec porttitor turpis commodo at. Nulla facilisi. Nulla gravida interdum tempor. Mauris iaculis pharetra leo.";
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt3516k()
                .aiModel(GPT35_16K)
                .build();

        Map<String, Object> context = createContextWithMessageNumber(3);
        List<ChatMessageData> messages = (List<ChatMessageData>) context.get("retrievedMessages");

        List<String> mentionedUserIds = org.assertj.core.util.Lists.list("4234234", "42344234256");

        when(discordChannelOperationsPort.retrieveEntireHistoryFrom(eq(guildId), eq(channelId),
                eq(messageId), anyList()))
                .thenReturn(Mono.just(messages));

        when(openAiPort.generateTextFrom(any(TextGenerationRequest.class)))
                .thenReturn(Mono.just(TextGenerationResultFixture.create()
                        .outputText(longSummary)
                        .build()));

        when(tokenizerPort.getTokenCountFrom(not(eq(longSummary))))
                .thenReturn(1000)
                .thenReturn(200);

        when(tokenizerPort.getTokenCountFrom(contains("Message")))
                .thenReturn(20)
                .thenReturn(20)
                .thenReturn(1000)
                .thenReturn(20)
                .thenReturn(1000);

        when(tokenizerPort.getTokenCountFrom(eq(longSummary)))
                .thenReturn(200);

        when(chatMessageService.addMessagesToContext(anyMap(), anyInt(), anyInt()))
                .thenReturn(context);

        // When
        Mono<Map<String, Object>> result = service.summarizeWith(guildId, channelId, messageId,
                botName, modelConfiguration, mentionedUserIds);

        // Then
        StepVerifier.create(result)
                .assertNext(processedContext -> {
                    assertThat(processedContext).containsKey("summary");
                    assertThat(processedContext).containsKey("messageHistory");

                    String summary = (String) processedContext.get("summary");
                    assertThat(summary).isNotBlank().isEqualTo(longSummary);

                    List<String> messageHistory = (List<String>) processedContext.get("messageHistory");
                    assertThat(messageHistory).isNotNull().isNotEmpty().hasSize(3);
                    assertThat(messageHistory).first().isEqualTo("Message 1");
                    assertThat(messageHistory).last().isEqualTo("Message 3");
                })
                .verifyComplete();

        verify(openAiPort, times(1)).generateTextFrom(any(TextGenerationRequest.class));
    }

    private Map<String, Object> createContextWithMessageNumber(int items) {

        List<ChatMessageData> messageDataList = new ArrayList<>();
        for (int i = 0; i < items; i++) {
            int messageNumber = i + 1;
            messageDataList.add(ChatMessageDataFixture.messageData()
                    .id(String.valueOf(messageNumber))
                    .content(String.format("Message %s", messageNumber))
                    .build());
        }

        List<String> messageStringList = messageDataList.stream()
                .map(ChatMessageData::getContent)
                .collect(Collectors.toCollection(ArrayList::new));

        Map<String, Object> context = new HashMap<>();
        context.put("retrievedMessages", messageDataList);
        context.put("messageHistory", messageStringList);

        return context;
    }
}
