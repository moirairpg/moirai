// package es.thalesalv.chatrpg.core.application.service;

// import static es.thalesalv.chatrpg.common.fixture.MessageDataFixture.messageData;
// import static es.thalesalv.chatrpg.core.domain.channelconfig.ArtificialIntelligenceModel.GPT35_4K;
// import static org.assertj.core.api.Assertions.assertThat;
// import static org.mockito.AdditionalMatchers.not;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyList;
// import static org.mockito.ArgumentMatchers.anyString;
// import static org.mockito.ArgumentMatchers.contains;
// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;

// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.List;
// import java.util.Map;

// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import discord4j.discordjson.json.MessageData;
// import es.thalesalv.chatrpg.core.application.model.request.TextGenerationRequest;
// import es.thalesalv.chatrpg.core.application.model.result.TextGenerationResultFixture;
// import es.thalesalv.chatrpg.core.application.port.DiscordChannelPort;
// import es.thalesalv.chatrpg.core.application.port.OpenAiPort;
// import es.thalesalv.chatrpg.core.domain.channelconfig.ModelConfiguration;
// import es.thalesalv.chatrpg.core.domain.channelconfig.ModelConfigurationFixture;
// import es.thalesalv.chatrpg.core.domain.port.TokenizerPort;
// import reactor.core.publisher.Mono;
// import reactor.test.StepVerifier;

// @SuppressWarnings("unchecked")
// @ExtendWith(MockitoExtension.class)
// public class ContextSummarizationApplicationServiceImplTest {

//     @Mock
//     private DiscordChannelPort discordChannelOperationsPort;

//     @Mock
//     private OpenAiPort openAiPort;

//     @Mock
//     private TokenizerPort tokenizerPort;

//     @InjectMocks
//     private ContextSummarizationApplicationServiceImpl service;

//     @Test
//     public void summarizeWith_validInput_thenSummaryGenerated() {
//         // Given
//         String authorId = "authorId";
//         String guildId = "validGuildId";
//         String channelId = "validChannelId";
//         String messageId = "validMessageId";
//         String generatedSummary = "Generated summary";
//         ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt3516k().build();

//         when(discordChannelOperationsPort.retrieveEntireHistoryFrom(eq(guildId), eq(channelId),
//                 eq(authorId), eq(messageId), anyList()))
//                 .thenReturn(Mono.just(buildMessageDataListWithItems(3)));

//         when(discordChannelOperationsPort.getMessageById(eq(channelId), eq(messageId)))
//                 .thenReturn(Mono.just(messageData().build()));

//         when(openAiPort.generateTextFrom(any(TextGenerationRequest.class)))
//                 .thenReturn(Mono.just(TextGenerationResultFixture.create()
//                         .outputText(generatedSummary)
//                         .build()));

//         List<String> mentionedUserIds = org.assertj.core.util.Lists.list("4234234", "42344234256");

//         // When
//         Mono<Map<String, Object>> result = service.summarizeWith(guildId, channelId, authorId, messageId, "botName",
//                 modelConfiguration, mentionedUserIds);

//         // Then
//         StepVerifier.create(result)
//                 .expectNextMatches(processedContext -> (processedContext.containsKey("summary")
//                         && processedContext.get("summary").equals(generatedSummary)))
//                 .verifyComplete();
//     }

//     @Test
//     public void summarizeWith_emptyMessageHistory_thenEmptySummaryReturned() {
//         // Given
//         String authorId = "authorId";
//         String guildId = "validGuildId";
//         String channelId = "channelIdWithEmptyHistory";
//         String messageId = "messageId";
//         ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt3516k().build();

//         when(discordChannelOperationsPort.retrieveEntireHistoryFrom(eq(guildId), eq(channelId), eq(authorId),
//                 eq(messageId),
//                 anyList()))
//                 .thenReturn(Mono.just(Collections.emptyList()));

//         when(discordChannelOperationsPort.getMessageById(eq(channelId), eq(messageId)))
//                 .thenReturn(Mono.just(messageData().build()));

//         when(openAiPort.generateTextFrom(any(TextGenerationRequest.class)))
//                 .thenReturn(Mono.just(TextGenerationResultFixture.create()
//                         .outputText("")
//                         .build()));

//         List<String> mentionedUserIds = org.assertj.core.util.Lists.list("4234234", "42344234256");

//         // When
//         Mono<Map<String, Object>> result = service.summarizeWith(guildId, channelId, authorId, messageId, "botName",
//                 modelConfiguration, mentionedUserIds);

//         // Then
//         StepVerifier.create(result)
//                 .expectNextMatches(processedContext -> processedContext.containsKey("summary")
//                         && ((String) processedContext.get("summary")).isEmpty())
//                 .verifyComplete();
//     }

//     @Test
//     public void summarizeWith_whenSummaryExceedsTokenLimit_thenSummaryShouldBeTrimmed() {
//         // Given
//         String authorId = "authorId";
//         String guildId = "validGuildId";
//         String channelId = "testChannelId";
//         String messageId = "testMessageId";
//         String botName = "testBot";
//         String longSummary = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam egestas dignissim velit, ut pellentesque ipsum. Ut auctor ipsum suscipit sapien tristique suscipit. Donec bibendum lectus neque, nec porttitor turpis commodo at. Nulla facilisi. Nulla gravida interdum tempor. Mauris iaculis pharetra leo.";
//         String trimmedSummary = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam egestas dignissim velit, ut pellentesque ipsum. Ut auctor ipsum suscipit sapien tristique suscipit. Donec bibendum lectus neque, nec porttitor turpis commodo at. Nulla facilisi. Nulla gravida interdum tempor.";
//         ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt3516k()
//                 .aiModel(GPT35_4K)
//                 .build();

//         List<MessageData> messages = buildMessageDataListWithItems(3);
//         MessageData lastMessage = messageData().content("Last test message").build();

//         List<String> mentionedUserIds = org.assertj.core.util.Lists.list("4234234", "42344234256");

//         when(discordChannelOperationsPort.retrieveEntireHistoryFrom(eq(guildId), eq(channelId), eq(authorId),
//                 eq(messageId),
//                 anyList()))
//                 .thenReturn(Mono.just(messages));

//         when(discordChannelOperationsPort.getMessageById(anyString(), anyString()))
//                 .thenReturn(Mono.just(lastMessage));

//         when(openAiPort.generateTextFrom(any(TextGenerationRequest.class)))
//                 .thenReturn(Mono.just(TextGenerationResultFixture.create()
//                         .outputText(longSummary)
//                         .build()));

//         when(tokenizerPort.getTokenCountFrom(not(eq(longSummary))))
//                 .thenReturn(1000)
//                 .thenReturn(200);

//         when(tokenizerPort.getTokenCountFrom(contains("Message")))
//                 .thenReturn(20)
//                 .thenReturn(20)
//                 .thenReturn(1000)
//                 .thenReturn(20)
//                 .thenReturn(1000);

//         when(tokenizerPort.getTokenCountFrom(eq(longSummary)))
//                 .thenReturn(2000);

//         // When
//         Mono<Map<String, Object>> result = service.summarizeWith(guildId, channelId, authorId, messageId,
//                 botName, modelConfiguration, mentionedUserIds);

//         // Then
//         StepVerifier.create(result)
//                 .assertNext(processedContext -> {
//                     assertThat(processedContext).containsKey("summary");
//                     assertThat(processedContext).containsKey("messageHistory");

//                     String summary = (String) processedContext.get("summary");
//                     assertThat(summary).isNotBlank().isEqualTo(trimmedSummary);

//                     List<String> messageHistory = (List<String>) processedContext.get("messageHistory");
//                     assertThat(messageHistory).isNotNull().isNotEmpty().hasSize(3);
//                 })
//                 .verifyComplete();

//         verify(openAiPort, times(1)).generateTextFrom(any(TextGenerationRequest.class));
//     }

//     @Test
//     public void summarizeWith_whenSingleSentenceSummaryExceedsTokenLimit_thenSummaryShouldBeTrimmedToNothing() {
//         // Given
//         String authorId = "authorId";
//         String guildId = "testGuildId";
//         String channelId = "testChannelId";
//         String messageId = "testMessageId";
//         String botName = "testBot";
//         String longSummary = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";
//         ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt3516k()
//                 .aiModel(GPT35_4K)
//                 .build();

//         List<MessageData> messages = buildMessageDataListWithItems(3);
//         MessageData lastMessage = messageData().content("Last test message").build();

//         List<String> mentionedUserIds = org.assertj.core.util.Lists.list("4234234", "42344234256");

//         when(discordChannelOperationsPort.retrieveEntireHistoryFrom(eq(guildId), eq(channelId), eq(authorId),
//                 eq(messageId),
//                 anyList()))
//                 .thenReturn(Mono.just(messages));

//         when(discordChannelOperationsPort.getMessageById(anyString(), anyString()))
//                 .thenReturn(Mono.just(lastMessage));

//         when(openAiPort.generateTextFrom(any(TextGenerationRequest.class)))
//                 .thenReturn(Mono.just(TextGenerationResultFixture.create()
//                         .outputText(longSummary)
//                         .build()));

//         when(tokenizerPort.getTokenCountFrom(not(eq(longSummary))))
//                 .thenReturn(1000)
//                 .thenReturn(200);

//         when(tokenizerPort.getTokenCountFrom(contains("Message")))
//                 .thenReturn(20)
//                 .thenReturn(20)
//                 .thenReturn(1000)
//                 .thenReturn(20)
//                 .thenReturn(1000);

//         when(tokenizerPort.getTokenCountFrom(eq(longSummary)))
//                 .thenReturn(2000);

//         // When
//         Mono<Map<String, Object>> result = service.summarizeWith(guildId, channelId, authorId, messageId,
//                 botName, modelConfiguration, mentionedUserIds);

//         // Then
//         StepVerifier.create(result)
//                 .assertNext(processedContext -> {
//                     assertThat(processedContext).containsKey("summary");
//                     assertThat(processedContext).containsKey("messageHistory");

//                     String summary = (String) processedContext.get("summary");
//                     assertThat(summary).isBlank();

//                     List<String> messageHistory = (List<String>) processedContext.get("messageHistory");
//                     assertThat(messageHistory).isNotNull().isNotEmpty().hasSize(3);
//                 })
//                 .verifyComplete();

//         verify(openAiPort, times(1)).generateTextFrom(any(TextGenerationRequest.class));
//     }

//     @Test
//     public void summarizeWith_whenSummaryNotExceedsTokenLimit_thenSummaryShouldNotBeTrimmed() {
//         // Given
//         String authorId = "authorId";
//         String guildId = "testGuildId";
//         String channelId = "testChannelId";
//         String messageId = "testMessageId";
//         String botName = "testBot";
//         String longSummary = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam egestas dignissim velit, ut pellentesque ipsum. Ut auctor ipsum suscipit sapien tristique suscipit. Donec bibendum lectus neque, nec porttitor turpis commodo at. Nulla facilisi. Nulla gravida interdum tempor. Mauris iaculis pharetra leo.";
//         ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt3516k()
//                 .aiModel(GPT35_4K)
//                 .build();

//         List<MessageData> messages = buildMessageDataListWithItems(3);
//         MessageData lastMessage = messageData().content("Last test message").build();

//         List<String> mentionedUserIds = org.assertj.core.util.Lists.list("4234234", "42344234256");

//         when(discordChannelOperationsPort.retrieveEntireHistoryFrom(eq(guildId), eq(channelId), eq(authorId),
//                 eq(messageId),
//                 anyList()))
//                 .thenReturn(Mono.just(messages));

//         when(discordChannelOperationsPort.getMessageById(anyString(), anyString()))
//                 .thenReturn(Mono.just(lastMessage));

//         when(openAiPort.generateTextFrom(any(TextGenerationRequest.class)))
//                 .thenReturn(Mono.just(TextGenerationResultFixture.create()
//                         .outputText(longSummary)
//                         .build()));

//         when(tokenizerPort.getTokenCountFrom(not(eq(longSummary))))
//                 .thenReturn(1000)
//                 .thenReturn(200);

//         when(tokenizerPort.getTokenCountFrom(contains("Message")))
//                 .thenReturn(20)
//                 .thenReturn(20)
//                 .thenReturn(1000)
//                 .thenReturn(20)
//                 .thenReturn(1000);

//         when(tokenizerPort.getTokenCountFrom(eq(longSummary)))
//                 .thenReturn(200);

//         // When
//         Mono<Map<String, Object>> result = service.summarizeWith(guildId, channelId, authorId, messageId,
//                 botName, modelConfiguration, mentionedUserIds);

//         // Then
//         StepVerifier.create(result)
//                 .assertNext(processedContext -> {
//                     assertThat(processedContext).containsKey("summary");
//                     assertThat(processedContext).containsKey("messageHistory");

//                     String summary = (String) processedContext.get("summary");
//                     assertThat(summary).isNotBlank().isEqualTo(longSummary);

//                     List<String> messageHistory = (List<String>) processedContext.get("messageHistory");
//                     assertThat(messageHistory).isNotNull().isNotEmpty().hasSize(3);
//                     assertThat(messageHistory).first().isEqualTo("test_user said: Message 3");
//                     assertThat(messageHistory).last().isEqualTo("test_user said: Message 1");
//                 })
//                 .verifyComplete();

//         verify(openAiPort, times(1)).generateTextFrom(any(TextGenerationRequest.class));
//     }

//     @Test
//     public void summarizeWith_relevantMessagesIncludedUpToTokenLimit_thenCorrectMessagesSelected() {
//         // Given
//         String authorId = "authorId";
//         String guildId = "testGuildId";
//         String channelId = "testChannelId";
//         String messageId = "testMessageId";
//         String botName = "testBot";
//         String longSummary = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam egestas dignissim velit, ut pellentesque ipsum. Ut auctor ipsum suscipit sapien tristique suscipit. Donec bibendum lectus neque, nec porttitor turpis commodo at. Nulla facilisi. Nulla gravida interdum tempor. Mauris iaculis pharetra leo.";
//         ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt3516k()
//                 .aiModel(GPT35_4K)
//                 .build();

//         List<MessageData> messages = buildMessageDataListWithItems(10);
//         MessageData lastMessage = messageData().content("Last test message").build();

//         List<String> mentionedUserIds = org.assertj.core.util.Lists.list("4234234", "42344234256");

//         when(discordChannelOperationsPort.retrieveEntireHistoryFrom(eq(guildId), eq(channelId), eq(authorId),
//                 eq(messageId),
//                 anyList()))
//                 .thenReturn(Mono.just(messages));

//         when(discordChannelOperationsPort.getMessageById(anyString(), anyString()))
//                 .thenReturn(Mono.just(lastMessage));

//         when(openAiPort.generateTextFrom(any(TextGenerationRequest.class)))
//                 .thenReturn(Mono.just(TextGenerationResultFixture.create()
//                         .outputText(longSummary)
//                         .build()));

//         when(tokenizerPort.getTokenCountFrom(not(contains("Message"))))
//                 .thenReturn(200)
//                 .thenReturn(10)
//                 .thenReturn(10)
//                 .thenReturn(200);

//         when(tokenizerPort.getTokenCountFrom(contains("Message")))
//                 .thenReturn(20)
//                 .thenReturn(200)
//                 .thenReturn(20)
//                 .thenReturn(200)
//                 .thenReturn(20)
//                 .thenReturn(200)
//                 .thenReturn(20)
//                 .thenReturn(200)
//                 .thenReturn(20)
//                 .thenReturn(200)
//                 .thenReturn(20)
//                 .thenReturn(200)
//                 .thenReturn(20)
//                 .thenReturn(200)
//                 .thenReturn(2000);

//         // When
//         Mono<Map<String, Object>> result = service.summarizeWith(guildId, channelId, authorId, messageId,
//                 botName, modelConfiguration, mentionedUserIds);

//         // Then
//         StepVerifier.create(result)
//                 .assertNext(processedContext -> {
//                     List<MessageData> retrievedMessages = (List<MessageData>) processedContext.get("retrievedMessages");
//                     List<String> messageHistory = (List<String>) processedContext.get("messageHistory");

//                     assertThat(processedContext).containsKey("messageHistory");
//                     assertThat(messageHistory).isNotNull().isNotEmpty().hasSize(7);
//                     assertThat(messageHistory).first().isEqualTo("test_user said: Message 7");
//                     assertThat(messageHistory).last().isEqualTo("test_user said: Message 1");

//                     assertThat(processedContext).containsKey("retrievedMessages");
//                     assertThat(retrievedMessages).isNotNull().isNotEmpty().hasSize(3);
//                 })
//                 .verifyComplete();

//         verify(openAiPort, times(1)).generateTextFrom(any(TextGenerationRequest.class));
//     }

//     private List<MessageData> buildMessageDataListWithItems(int items) {

//         List<MessageData> messageDataList = new ArrayList<>();
//         for (int i = 0; i < items; i++) {
//             messageDataList.add(messageData()
//                     .id(i + 1)
//                     .content(String.format("Message %s", i + 1))
//                     .build());
//         }

//         return messageDataList;
//     }
// }
