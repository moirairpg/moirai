package es.thalesalv.chatrpg.core.application.query.discord.messagereceived;

import static es.thalesalv.chatrpg.core.application.model.request.ChatMessage.Role.SYSTEM;
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

import es.thalesalv.chatrpg.core.application.model.request.ChatMessage;
import es.thalesalv.chatrpg.core.application.model.request.TextGenerationRequest;
import es.thalesalv.chatrpg.core.application.model.result.TextGenerationResult;
import es.thalesalv.chatrpg.core.application.model.result.TextGenerationResultFixture;
import es.thalesalv.chatrpg.core.application.port.DiscordChannelPort;
import es.thalesalv.chatrpg.core.application.port.OpenAiPort;
import es.thalesalv.chatrpg.core.application.service.LorebookEnrichmentService;
import es.thalesalv.chatrpg.core.application.service.PersonaEnrichmentService;
import es.thalesalv.chatrpg.core.application.service.StorySummarizationService;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfig;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigFixture;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigRepository;
import es.thalesalv.chatrpg.infrastructure.outbound.adapter.response.ChatMessageDataFixture;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class MessageReceivedHandlerTest {

    @Mock
    private LorebookEnrichmentService lorebookEnrichmentService;

    @Mock
    private StorySummarizationService summarizationService;

    @Mock
    private PersonaEnrichmentService enrichmentService;

    @Mock
    private ChannelConfigRepository channelConfigRepository;

    @Mock
    private DiscordChannelPort discordChannelOperationsPort;

    @Mock
    private OpenAiPort openAiPort;

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

        when(discordChannelOperationsPort.retrieveEntireHistoryFrom(anyString(), anyString(), anyString(), anyList()))
                .thenReturn(Mono.just(ChatMessageDataFixture.messageList(5)));

        when(summarizationService.summarizeContextWith(anyList(), any()))
                .thenReturn(Mono.just(context));

        when(channelConfigRepository.findByDiscordChannelId(channelId))
                .thenReturn(Optional.of(channelConfig));

        when(lorebookEnrichmentService.enrichContextWith(anyMap(), anyString(), any()))
                .thenReturn(Mono.just(context));

        when(enrichmentService.enrichContextWith(anyMap(), anyString(), any()))
                .thenReturn(Mono.just(context));

        when(openAiPort.generateTextFrom(any()))
                .thenReturn(Mono.just(generationResult));

        when(discordChannelOperationsPort.sendMessage(eq(channelId), anyString()))
                .thenReturn(Mono.empty());

        // When
        Mono<Void> result = messageReceivedHandler.execute(query);

        // Then
        StepVerifier.create(result).verifyComplete();
        verify(openAiPort).generateTextFrom(textGenerationRequestCaptor.capture());
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
