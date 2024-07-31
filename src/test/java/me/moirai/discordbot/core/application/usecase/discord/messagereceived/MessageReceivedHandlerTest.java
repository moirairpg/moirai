package me.moirai.discordbot.core.application.usecase.discord.messagereceived;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.discordbot.core.application.model.request.TextGenerationRequest;
import me.moirai.discordbot.core.application.port.DiscordChannelPort;
import me.moirai.discordbot.core.application.port.LorebookEnrichmentPort;
import me.moirai.discordbot.core.application.port.PersonaEnrichmentPort;
import me.moirai.discordbot.core.application.port.StorySummarizationPort;
import me.moirai.discordbot.core.application.port.TextCompletionPort;
import me.moirai.discordbot.core.application.port.TextModerationPort;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigRepository;
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
    void givenChannelConfigDoesNotExist_whenExecute_thenShouldNotSendResponse() {

        // Given
        String channelId = "CHNLID";
        MessageReceived query = MessageReceived.builder()
                .authordDiscordId("John")
                .botUsername("TestBot")
                .isBotMentioned(false)
                .mentionedUsersIds(Collections.emptyList())
                .channelId(channelId)
                .guildId("GLDID")
                .messageId("MSGID")
                .build();

        when(channelConfigRepository.findByDiscordChannelId(channelId)).thenReturn(Optional.empty());

        // When
        Mono<Void> result = messageReceivedHandler.execute(query);

        // Then
        StepVerifier.create(result).verifyComplete();
        verify(discordChannelOperationsPort, never()).sendMessageTo(anyString(), anyString());
    }
}
