package me.moirai.discordbot.core.application.usecase.discord.slashcommands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import me.moirai.discordbot.AbstractDiscordTest;
import me.moirai.discordbot.core.application.helper.StoryGenerationHelper;
import me.moirai.discordbot.core.application.port.DiscordChannelPort;
import me.moirai.discordbot.core.application.usecase.discord.DiscordMessageData;
import me.moirai.discordbot.core.application.usecase.discord.DiscordMessageDataFixture;
import me.moirai.discordbot.core.application.usecase.discord.DiscordUserDetailsFixture;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigFixture;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigRepository;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.StoryGenerationRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class RetryGenerationHandlerTest extends AbstractDiscordTest {

    @Mock
    private DiscordChannelPort discordChannelPort;

    @Mock
    private ChannelConfigRepository channelConfigRepository;

    @Mock
    private StoryGenerationHelper storyGenerationPort;

    @InjectMocks
    private RetryGenerationHandler handler;

    @Test
    public void retryCommand_whenLastMessageElegible_thenShouldDeleteItAndRegenerateOutput() {

        // Given
        String botId = "BOTID";
        String channelId = "CHID";

        ChannelConfig channelConfig = ChannelConfigFixture.sample()
                .discordChannelId(channelId)
                .build();

        RetryGeneration useCase = RetryGeneration.builder()
                .botId(botId)
                .botNickname("nickname")
                .botUsername("user.name")
                .channelId(channelId)
                .guildId("GDID")
                .build();

        DiscordMessageData chatMessageData = DiscordMessageDataFixture.messageData()
                .author(DiscordUserDetailsFixture.create()
                        .id(botId)
                        .build())
                .build();

        ArgumentCaptor<StoryGenerationRequest> generationRequestCaptor = ArgumentCaptor
                .forClass(StoryGenerationRequest.class);

        when(channelConfigRepository.findByDiscordChannelId(anyString())).thenReturn(Optional.of(channelConfig));
        when(discordChannelPort.getLastMessageIn(anyString())).thenReturn(Optional.of(chatMessageData));

        // When
        Mono<Void> result = handler.execute(useCase);

        // Then
        StepVerifier.create(result).verifyComplete();

        verify(storyGenerationPort, times(1)).continueStory(generationRequestCaptor.capture());

        StoryGenerationRequest generationRequest = generationRequestCaptor.getValue();
        assertThat(generationRequest).isNotNull();
        assertThat(generationRequest.getBotId()).isEqualTo(useCase.getBotId());
        assertThat(generationRequest.getBotNickname()).isEqualTo(useCase.getBotNickname());
        assertThat(generationRequest.getBotUsername()).isEqualTo(useCase.getBotUsername());
        assertThat(generationRequest.getChannelId()).isEqualTo(useCase.getChannelId());
        assertThat(generationRequest.getGuildId()).isEqualTo(useCase.getGuildId());
        assertThat(generationRequest.getPersonaId()).isEqualTo(channelConfig.getPersonaId());
        assertThat(generationRequest.getWorldId()).isEqualTo(channelConfig.getWorldId());
    }

    @Test
    public void retryCommand_whenLastMessageAuthorIsNotBot_thenThrowException() {

        // Given
        String botId = "BOTID";
        String channelId = "CHID";
        String expectedErrorMessage = "This command can only be used if the last message in channel was sent by the bot.";

        RetryGeneration useCase = RetryGeneration.builder()
                .botId(botId)
                .botNickname("nickname")
                .botUsername("user.name")
                .channelId(channelId)
                .guildId("GDID")
                .build();

        DiscordMessageData chatMessageData = DiscordMessageDataFixture.messageData()
                .author(DiscordUserDetailsFixture.create()
                        .id("SMID")
                        .build())
                .build();

        when(discordChannelPort.getLastMessageIn(anyString())).thenReturn(Optional.of(chatMessageData));

        // When
        Mono<Void> result = handler.execute(useCase);

        // Then
        StepVerifier.create(result).verifyErrorMessage(expectedErrorMessage);
    }

    @Test
    public void retryCommand_whenNoMessagesInChannel_thenThrowException() {

        // Given
        String botId = "BOTID";
        String channelId = "CHID";
        String expectedErrorMessage = "Channel has no messages";

        RetryGeneration useCase = RetryGeneration.builder()
                .botId(botId)
                .botNickname("nickname")
                .botUsername("user.name")
                .channelId(channelId)
                .guildId("GDID")
                .build();

        when(discordChannelPort.getLastMessageIn(anyString())).thenReturn(Optional.empty());

        // When
        Mono<Void> result = handler.execute(useCase);

        // Then
        StepVerifier.create(result).verifyErrorMessage(expectedErrorMessage);
    }

    @Test
    public void retryCommand_whenUnknownError_thenThrowException() {

        // Given
        String botId = "BOTID";
        String channelId = "CHID";
        String expectedErrorMessage = "An error occurred while retrying generation of output";

        RetryGeneration useCase = RetryGeneration.builder()
                .botId(botId)
                .botNickname("nickname")
                .botUsername("user.name")
                .channelId(channelId)
                .guildId("GDID")
                .build();

        when(discordChannelPort.getLastMessageIn(anyString())).thenThrow(RuntimeException.class);

        // When
        Mono<Void> result = handler.execute(useCase);

        // Then
        StepVerifier.create(result).verifyErrorMessage(expectedErrorMessage);
    }

    @Test
    public void retryCommand_whenRetrieveUserMessageAndChannelIsEmpty_thenThrowException() {

        // Given
        String botId = "BOTID";
        String channelId = "CHID";
        String expectedErrorMessage = "Channel has no messages";

        ChannelConfig channelConfig = ChannelConfigFixture.sample()
                .discordChannelId(channelId)
                .build();

        RetryGeneration useCase = RetryGeneration.builder()
                .botId(botId)
                .botNickname("nickname")
                .botUsername("user.name")
                .channelId(channelId)
                .guildId("GDID")
                .build();

        DiscordMessageData chatMessageData = DiscordMessageDataFixture.messageData()
                .author(DiscordUserDetailsFixture.create()
                        .id(botId)
                        .build())
                .build();

        when(channelConfigRepository.findByDiscordChannelId(anyString())).thenReturn(Optional.of(channelConfig));

        when(discordChannelPort.getLastMessageIn(anyString()))
                .thenReturn(Optional.of(chatMessageData))
                .thenReturn(Optional.empty());

        // When
        Mono<Void> result = handler.execute(useCase);

        // Then
        StepVerifier.create(result).verifyErrorMessage(expectedErrorMessage);
    }

    @Test
    public void retryCommand_whenRetrieveLastMessageAndChannelIsEmpty_thenThrowException() {

        // Given
        String botId = "BOTID";
        String channelId = "CHID";
        String expectedErrorMessage = "Channel has no messages";

        RetryGeneration useCase = RetryGeneration.builder()
                .botId(botId)
                .botNickname("nickname")
                .botUsername("user.name")
                .channelId(channelId)
                .guildId("GDID")
                .build();

        when(discordChannelPort.getLastMessageIn(anyString())).thenReturn(Optional.empty());

        // When
        Mono<Void> result = handler.execute(useCase);

        // Then
        StepVerifier.create(result).verifyErrorMessage(expectedErrorMessage);
    }
}
