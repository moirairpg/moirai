package me.moirai.discordbot.core.application.usecase.discord.slashcommands;

import java.util.List;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.DiscordChannelPort;
import me.moirai.discordbot.core.application.port.StoryGenerationPort;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigRepository;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.AiModelRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.ModelConfigurationRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.ModerationConfigurationRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.StoryGenerationRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.response.ChatMessageData;
import reactor.core.publisher.Mono;

@UseCaseHandler
public class GenerateOutputHandler extends AbstractUseCaseHandler<GenerateOutput, Mono<Void>> {

    private final ChannelConfigRepository channelConfigRepository;
    private final StoryGenerationPort storyGenerationPort;
    private final DiscordChannelPort discordChannelPort;

    public GenerateOutputHandler(StoryGenerationPort storyGenerationPort,
            ChannelConfigRepository channelConfigRepository,
            DiscordChannelPort discordChannelPort) {

        this.channelConfigRepository = channelConfigRepository;
        this.storyGenerationPort = storyGenerationPort;
        this.discordChannelPort = discordChannelPort;
    }

    @Override
    public Mono<Void> execute(GenerateOutput useCase) {

        try {
            return channelConfigRepository.findByDiscordChannelId(useCase.getChannelId())
                    .filter(channelConfig -> channelConfig.getDiscordChannelId().equals(useCase.getChannelId()))
                    .map(channelConfig -> buildGenerationRequest(useCase, channelConfig))
                    .map(generationRequest -> storyGenerationPort.continueStory(generationRequest))
                    .orElseGet(() -> Mono.empty());
        } catch (Exception e) {
            return Mono.error(
                    () -> new IllegalStateException("An error occurred while generating output"));
        }
    }

    private StoryGenerationRequest buildGenerationRequest(GenerateOutput useCase, ChannelConfig channelConfig) {

        ModelConfigurationRequest modelConfigurationRequest = ModelConfigurationRequest.builder()
                .frequencyPenalty(channelConfig.getModelConfiguration().getFrequencyPenalty())
                .presencePenalty(channelConfig.getModelConfiguration().getPresencePenalty())
                .logitBias(channelConfig.getModelConfiguration().getLogitBias())
                .maxTokenLimit(channelConfig.getModelConfiguration().getMaxTokenLimit())
                .stopSequences(channelConfig.getModelConfiguration().getStopSequences())
                .temperature(channelConfig.getModelConfiguration().getTemperature())
                .aiModel(AiModelRequest
                        .build(channelConfig.getModelConfiguration().getAiModel().getInternalModelName(),
                                channelConfig.getModelConfiguration().getAiModel().getOfficialModelName(),
                                channelConfig.getModelConfiguration().getAiModel().getHardTokenLimit()))
                .build();

        ModerationConfigurationRequest moderation = ModerationConfigurationRequest
                .build(channelConfig.getModeration().isAbsolute(), channelConfig.getModeration().getThresholds());

        List<ChatMessageData> messageHistory = discordChannelPort.retrieveEntireHistoryFrom(useCase.getChannelId());

        return StoryGenerationRequest.builder()
                .botId(useCase.getBotId())
                .botNickname(useCase.getBotNickname())
                .botUsername(useCase.getBotUsername())
                .channelId(useCase.getChannelId())
                .guildId(useCase.getGuildId())
                .moderation(moderation)
                .modelConfiguration(modelConfigurationRequest)
                .personaId(channelConfig.getPersonaId())
                .worldId(channelConfig.getWorldId())
                .messageHistory(messageHistory)
                .build();
    }
}