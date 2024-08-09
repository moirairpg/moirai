package me.moirai.discordbot.core.application.usecase.discord.messagereceived;

import java.util.List;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.helper.StoryGenerationHelper;
import me.moirai.discordbot.core.application.port.DiscordChannelPort;
import me.moirai.discordbot.core.application.usecase.discord.DiscordMessageData;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigRepository;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.AiModelRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.ModelConfigurationRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.ModerationConfigurationRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.StoryGenerationRequest;
import reactor.core.publisher.Mono;

@UseCaseHandler
public class MessageReceivedHandler extends AbstractUseCaseHandler<MessageReceived, Mono<Void>> {

    private final ChannelConfigRepository channelConfigRepository;
    private final StoryGenerationHelper storyGenerationPort;
    private final DiscordChannelPort discordChannelPort;

    public MessageReceivedHandler(StoryGenerationHelper storyGenerationPort,
            ChannelConfigRepository channelConfigRepository,
            DiscordChannelPort discordChannelPort) {

        this.channelConfigRepository = channelConfigRepository;
        this.storyGenerationPort = storyGenerationPort;
        this.discordChannelPort = discordChannelPort;
    }

    @Override
    public Mono<Void> execute(MessageReceived query) {

        return channelConfigRepository.findByDiscordChannelId(query.getChannelId())
                .filter(channelConfig -> channelConfig.getDiscordChannelId().equals(query.getChannelId()))
                .map(channelConfig -> {
                    StoryGenerationRequest generationRequest = buildGenerationRequest(query, channelConfig);
                    return storyGenerationPort.continueStory(generationRequest);
                })
                .orElseGet(() -> Mono.empty());
    }

    private StoryGenerationRequest buildGenerationRequest(MessageReceived useCase, ChannelConfig channelConfig) {

        AiModelRequest aiModel = AiModelRequest
                .build(channelConfig.getModelConfiguration().getAiModel().getInternalModelName(),
                        channelConfig.getModelConfiguration().getAiModel().getOfficialModelName(),
                        channelConfig.getModelConfiguration().getAiModel().getHardTokenLimit());

        ModelConfigurationRequest modelConfigurationRequest = ModelConfigurationRequest.builder()
                .frequencyPenalty(channelConfig.getModelConfiguration().getFrequencyPenalty())
                .presencePenalty(channelConfig.getModelConfiguration().getPresencePenalty())
                .logitBias(channelConfig.getModelConfiguration().getLogitBias())
                .maxTokenLimit(channelConfig.getModelConfiguration().getMaxTokenLimit())
                .stopSequences(channelConfig.getModelConfiguration().getStopSequences())
                .temperature(channelConfig.getModelConfiguration().getTemperature())
                .aiModel(aiModel)
                .build();

        ModerationConfigurationRequest moderation = ModerationConfigurationRequest
                .build(channelConfig.getModeration().isAbsolute(), channelConfig.getModeration().getThresholds());

        List<DiscordMessageData> messageHistory = discordChannelPort.retrieveEntireHistoryFrom(useCase.getChannelId());

        return StoryGenerationRequest.builder()
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