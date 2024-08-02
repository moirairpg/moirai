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
import me.moirai.discordbot.core.application.port.DiscordChannelPort;
import me.moirai.discordbot.core.application.port.StoryGenerationPort;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigFixture;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigRepository;
import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.core.domain.world.WorldFixture;
import me.moirai.discordbot.core.domain.world.WorldRepository;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.StoryGenerationRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.response.ChatMessageDataFixture;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class StartCommandHandlerTest extends AbstractDiscordTest {

    @Mock
    private ChannelConfigRepository channelConfigRepository;

    @Mock
    private WorldRepository worldRepository;

    @Mock
    private StoryGenerationPort storyGenerationPort;

    @Mock
    private DiscordChannelPort discordChannelPort;

    @InjectMocks
    private StartCommandHandler handler;

    @Test
    public void goCommand_whenIssued_thenGenerateOutput() {

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
                .thenReturn(ChatMessageDataFixture.messageList(5));

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
    public void goCommand_whenUnknownError_thenThrowException() {

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
                .thenReturn(ChatMessageDataFixture.messageList(5));

        // When
        Mono<Void> result = handler.execute(useCase);

        // Then
        StepVerifier.create(result).verifyErrorMessage("An error occurred while generating output");
    }
}
