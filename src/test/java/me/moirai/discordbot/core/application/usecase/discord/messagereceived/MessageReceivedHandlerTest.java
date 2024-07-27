package me.moirai.discordbot.core.application.usecase.discord.messagereceived;

import static me.moirai.discordbot.core.application.model.request.ChatMessage.Role.SYSTEM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.common.exception.ModerationException;
import me.moirai.discordbot.core.application.model.request.ChatMessage;
import me.moirai.discordbot.core.application.model.request.TextGenerationRequest;
import me.moirai.discordbot.core.application.model.result.TextGenerationResult;
import me.moirai.discordbot.core.application.model.result.TextGenerationResultFixture;
import me.moirai.discordbot.core.application.model.result.TextModerationResult;
import me.moirai.discordbot.core.application.model.result.TextModerationResultFixture;
import me.moirai.discordbot.core.application.port.DiscordChannelPort;
import me.moirai.discordbot.core.application.port.LorebookEnrichmentPort;
import me.moirai.discordbot.core.application.port.PersonaEnrichmentPort;
import me.moirai.discordbot.core.application.port.StorySummarizationPort;
import me.moirai.discordbot.core.application.port.TextCompletionPort;
import me.moirai.discordbot.core.application.port.TextModerationPort;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigFixture;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigRepository;
import me.moirai.discordbot.core.domain.channelconfig.ModelConfiguration;
import me.moirai.discordbot.infrastructure.outbound.adapter.response.ChatMessageDataFixture;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class MessageReceivedHandlerTest {

    @Mock
    private LorebookEnrichmentPort lorebookEnrichmentPort;

    @Mock
    private StorySummarizationPort summarizationPort;

    @Mock
    private PersonaEnrichmentPort personaEnrichmentPort;

    @Mock
    private ChannelConfigRepository channelConfigRepository;

    @Mock
    private DiscordChannelPort discordChannelOperationsPort;

    @Mock
    private TextCompletionPort textCompletionPort;

    @Mock
    private TextModerationPort textModerationPort;

    @Captor
    private ArgumentCaptor<TextGenerationRequest> textGenerationRequestCaptor;

    @InjectMocks
    private MessageReceivedHandler messageReceivedHandler;

    @Test
    void givenValidMessage_whenExecute_thenShouldProcessAndSendResponse() {

        // Given
        String personaDescription = "This is a persona";
        String lorebook = "This is a lorebook";
        String summary = "This is a story summary";
        String bump = "This is a bump";
        String nudge = "This is a nudge";

        String channelId = "CHNLID";
        ChannelConfig channelConfig = ChannelConfigFixture.sample()
                .discordChannelId(channelId)
                .build();

        MessageReceived query = MessageReceivedFixture.create()
                .messageChannelId(channelId)
                .build();

        Map<String, Object> context = new HashMap<>();
        context.put("messageHistory", List.of("TestUser said: history",
                "AnotherUser said: another message",
                "TheOtherUser said: another message",
                "Cherokee said: another message",
                "YetAnotherUser said: yet another message"));

        context.put("lorebook", lorebook);
        context.put("persona", personaDescription);
        context.put("summary", summary);
        context.put("nudge", nudge);
        context.put("bump", bump);

        TextGenerationResult generationResult = TextGenerationResultFixture.create().build();
        TextModerationResult moderationResult = TextModerationResultFixture.withoutFlags().build();

        when(discordChannelOperationsPort.retrieveEntireHistoryFrom(anyString(), anyString(), anyString(), anyList()))
                .thenReturn(Mono.just(ChatMessageDataFixture.messageList(5)));

        when(summarizationPort.summarizeContextWith(anyMap(), any(ModelConfiguration.class)))
                .thenReturn(Mono.just(context));

        when(channelConfigRepository.findByDiscordChannelId(channelId))
                .thenReturn(Optional.of(channelConfig));

        when(lorebookEnrichmentPort.enrichContextWithLorebook(anyList(), anyString(), any(ModelConfiguration.class)))
                .thenReturn(context);

        when(personaEnrichmentPort.enrichContextWithPersona(anyMap(), anyString(), any(ModelConfiguration.class)))
                .thenReturn(Mono.just(context));

        when(textCompletionPort.generateTextFrom(any(TextGenerationRequest.class)))
                .thenReturn(Mono.just(generationResult));

        when(textModerationPort.moderate(anyString()))
                .thenReturn(Mono.just(moderationResult));

        when(discordChannelOperationsPort.sendMessage(eq(channelId), anyString()))
                .thenReturn(Mono.empty());

        // When
        Mono<Void> result = messageReceivedHandler.execute(query);

        // Then
        StepVerifier.create(result).verifyComplete();
        verify(textCompletionPort).generateTextFrom(textGenerationRequestCaptor.capture());
        verify(discordChannelOperationsPort).sendMessage(eq(channelId), anyString());

        List<ChatMessage> messagesSentToAi = textGenerationRequestCaptor.getValue().getMessages();
        assertThat(messagesSentToAi).isNotNull().isNotEmpty().hasSize(10);

        assertThat(messagesSentToAi)
                .element(0)
                .matches(element -> element.getRole().equals(SYSTEM))
                .matches(element -> element.getContent().equals(personaDescription));

        assertThat(messagesSentToAi)
                .element(1)
                .matches(element -> element.getRole().equals(SYSTEM))
                .matches(element -> element.getContent().equals(lorebook));

        assertThat(messagesSentToAi)
                .element(2)
                .matches(element -> element.getRole().equals(SYSTEM))
                .matches(element -> element.getContent().equals(summary));

        assertThat(messagesSentToAi)
                .element(3)
                .matches(element -> element.getRole().equals(SYSTEM))
                .matches(element -> element.getContent().equals(bump));

        assertThat(messagesSentToAi)
                .element(9)
                .matches(element -> element.getRole().equals(SYSTEM))
                .matches(element -> element.getContent().equals(nudge));
    }

    @Test
    void givenInappropriateInput_whenExecute_thenShouldThrowModerationException() {

        // Given
        String personaDescription = "This is a persona";
        String lorebook = "This is a lorebook";
        String summary = "This is a story summary";
        String bump = "This is a bump";
        String nudge = "This is a nudge";

        String channelId = "CHNLID";
        ChannelConfig channelConfig = ChannelConfigFixture.sample()
                .discordChannelId(channelId)
                .build();

        MessageReceived query = MessageReceivedFixture.create()
                .messageChannelId(channelId)
                .build();

        Map<String, Object> context = new HashMap<>();
        context.put("messageHistory", List.of("TestUser said: history",
                "AnotherUser said: another message",
                "TheOtherUser said: another message",
                "Cherokee said: another message",
                "YetAnotherUser said: yet another message"));

        context.put("lorebook", lorebook);
        context.put("persona", personaDescription);
        context.put("summary", summary);
        context.put("nudge", nudge);
        context.put("bump", bump);

        TextModerationResult moderationResult = TextModerationResultFixture.withFlags().build();

        when(discordChannelOperationsPort.retrieveEntireHistoryFrom(anyString(), anyString(), anyString(), anyList()))
                .thenReturn(Mono.just(ChatMessageDataFixture.messageList(5)));

        when(summarizationPort.summarizeContextWith(anyMap(), any(ModelConfiguration.class)))
                .thenReturn(Mono.just(context));

        when(channelConfigRepository.findByDiscordChannelId(channelId))
                .thenReturn(Optional.of(channelConfig));

        when(lorebookEnrichmentPort.enrichContextWithLorebook(anyList(), anyString(), any(ModelConfiguration.class)))
                .thenReturn(context);

        when(personaEnrichmentPort.enrichContextWithPersona(anyMap(), anyString(), any(ModelConfiguration.class)))
                .thenReturn(Mono.just(context));

        when(textModerationPort.moderate(anyString()))
                .thenReturn(Mono.just(moderationResult));

        // When
        Mono<Void> result = messageReceivedHandler.execute(query);

        // Then
        StepVerifier.create(result)
                .verifyErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(ModerationException.class);
                    assertThat(((ModerationException) error).getFlaggedTopics()).hasSize(1);
                });
    }

    @Test
    void givenInappropriateAiOutput_whenExecute_thenShouldThrowModerationException() {

        // Given
        String personaDescription = "This is a persona";
        String lorebook = "This is a lorebook";
        String summary = "This is a story summary";
        String bump = "This is a bump";
        String nudge = "This is a nudge";

        String channelId = "CHNLID";
        ChannelConfig channelConfig = ChannelConfigFixture.sample()
                .discordChannelId(channelId)
                .build();

        MessageReceived query = MessageReceivedFixture.create()
                .messageChannelId(channelId)
                .build();

        Map<String, Object> context = new HashMap<>();
        context.put("messageHistory", List.of("TestUser said: history",
                "AnotherUser said: another message",
                "TheOtherUser said: another message",
                "Cherokee said: another message",
                "YetAnotherUser said: yet another message"));

        context.put("lorebook", lorebook);
        context.put("persona", personaDescription);
        context.put("summary", summary);
        context.put("nudge", nudge);
        context.put("bump", bump);

        TextGenerationResult generationResult = TextGenerationResultFixture.create().build();
        TextModerationResult goodModerationResult = TextModerationResultFixture.withoutFlags().build();
        TextModerationResult badModerationResult = TextModerationResultFixture.withFlags().build();

        when(discordChannelOperationsPort.retrieveEntireHistoryFrom(anyString(), anyString(), anyString(), anyList()))
                .thenReturn(Mono.just(ChatMessageDataFixture.messageList(5)));

        when(summarizationPort.summarizeContextWith(anyMap(), any(ModelConfiguration.class)))
                .thenReturn(Mono.just(context));

        when(channelConfigRepository.findByDiscordChannelId(channelId))
                .thenReturn(Optional.of(channelConfig));

        when(lorebookEnrichmentPort.enrichContextWithLorebook(anyList(), anyString(), any(ModelConfiguration.class)))
                .thenReturn(context);

        when(personaEnrichmentPort.enrichContextWithPersona(anyMap(), anyString(), any(ModelConfiguration.class)))
                .thenReturn(Mono.just(context));

        when(textModerationPort.moderate(anyString()))
                .thenReturn(Mono.just(goodModerationResult))
                .thenReturn(Mono.just(badModerationResult));

        when(textCompletionPort.generateTextFrom(any(TextGenerationRequest.class)))
                .thenReturn(Mono.just(generationResult));

        // When
        Mono<Void> result = messageReceivedHandler.execute(query);

        // Then
        StepVerifier.create(result)
                .verifyErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(ModerationException.class);
                    assertThat(((ModerationException) error).getFlaggedTopics()).hasSize(1);
                });
    }

    @Test
    void givenChannelConfigDoesNotExist_whenExecute_thenShouldNotSendResponse() {

        // Given
        String channelId = "CHNLID";
        MessageReceived query = MessageReceived.builder()
                .authordDiscordId("John")
                .botName("TestBot")
                .isBotMentioned(false)
                .mentionedUsersIds(Collections.emptyList())
                .messageChannelId(channelId)
                .messageGuildId("GLDID")
                .messageId("MSGID")
                .build();

        when(channelConfigRepository.findByDiscordChannelId(channelId)).thenReturn(Optional.empty());

        // When
        Mono<Void> result = messageReceivedHandler.execute(query);

        // Then
        StepVerifier.create(result).verifyComplete();
        verify(discordChannelOperationsPort, never()).sendMessage(anyString(), anyString());
    }
}
