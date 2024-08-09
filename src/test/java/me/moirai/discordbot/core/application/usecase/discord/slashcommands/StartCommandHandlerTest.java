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
import me.moirai.discordbot.core.application.usecase.discord.DiscordMessageDataFixture;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigFixture;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigRepository;
import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.core.domain.world.WorldFixture;
import me.moirai.discordbot.core.domain.world.WorldRepository;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.StoryGenerationRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class StartCommandHandlerTest extends AbstractDiscordTest {

    @Mock
    private ChannelConfigRepository channelConfigRepository;

    @Mock
    private WorldRepository worldRepository;

    @Mock
    private StoryGenerationHelper storyGenerationPort;

    @Mock
    private DiscordChannelPort discordChannelPort;

    @InjectMocks
    private StartCommandHandler handler;

    @Test
    public void startCommand_whenIssued_thenSendAdventureStartAndCallGeneration() {

        // Given
        String channelId = "CHID";

        ChannelConfig channelConfig = ChannelConfigFixture.sample()
                .discordChannelId(channelId)
                .build();

        StartCommand useCase = StartCommand.builder()
                .botId("BOTID")
                .botNickname("nickname")
                .botUsername("user.name")
                .channelId(channelId)
                .guildId("GDID")
                .build();

        World world = WorldFixture.privateWorld().build();

        ArgumentCaptor<StoryGenerationRequest> generationRequestCaptor = ArgumentCaptor
                .forClass(StoryGenerationRequest.class);

        when(channelConfigRepository.findByDiscordChannelId(anyString())).thenReturn(Optional.of(channelConfig));

        when(discordChannelPort.retrieveEntireHistoryFrom(anyString()))
                .thenReturn(DiscordMessageDataFixture.messageList(5));

        when(worldRepository.findById(anyString())).thenReturn(Optional.of(world));

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
    public void startCommand_whenUnknownError_thenThrowException() {

        // Given
        String channelId = "CHID";

        StartCommand useCase = StartCommand.builder()
                .botId("BOTID")
                .botNickname("nickname")
                .botUsername("user.name")
                .channelId(channelId)
                .guildId("GDID")
                .build();

        when(channelConfigRepository.findByDiscordChannelId(anyString())).thenThrow(RuntimeException.class);

        when(discordChannelPort.retrieveEntireHistoryFrom(anyString()))
                .thenReturn(DiscordMessageDataFixture.messageList(5));

        // When
        Mono<Void> result = handler.execute(useCase);

        // Then
        StepVerifier.create(result).verifyErrorMessage("An error occurred while generating output");
    }

    @Test
    public void startCommand_whenChannelConfigHasNoWorld_thenThrowException() {

        // Given
        String channelId = "CHID";
        String expectedErrorMessage = "Channel config has no world linked to it";

        StartCommand useCase = StartCommand.builder()
                .botId("BOTID")
                .botNickname("nickname")
                .botUsername("user.name")
                .channelId(channelId)
                .guildId("GDID")
                .build();

        ChannelConfig channelConfig = ChannelConfigFixture.sample()
                .discordChannelId(channelId)
                .build();

        when(channelConfigRepository.findByDiscordChannelId(anyString())).thenReturn(Optional.of(channelConfig));

        when(discordChannelPort.retrieveEntireHistoryFrom(anyString()))
                .thenReturn(DiscordMessageDataFixture.messageList(5));

        when(worldRepository.findById(anyString())).thenReturn(Optional.empty());

        // When
        Mono<Void> result = handler.execute(useCase);

        // Then
        StepVerifier.create(result).verifyErrorMessage(expectedErrorMessage);
    }
}
