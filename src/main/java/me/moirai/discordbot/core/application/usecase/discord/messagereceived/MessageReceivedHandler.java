package me.moirai.discordbot.core.application.usecase.discord.messagereceived;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.StoryGenerationPort;
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
    private final StoryGenerationPort storyGenerationPort;

    public MessageReceivedHandler(StoryGenerationPort storyGenerationPort,
            ChannelConfigRepository channelConfigRepository) {

        this.channelConfigRepository = channelConfigRepository;
        this.storyGenerationPort = storyGenerationPort;
    }

    @Override
    public Mono<Void> execute(MessageReceived query) {

        return channelConfigRepository.findByDiscordChannelId(query.getChannelId())
                .filter(channelConfig -> channelConfig.getDiscordChannelId().equals(query.getChannelId()))
                .map(channelConfig -> buildGenerationRequest(query, channelConfig))
                .map(generationRequest -> storyGenerationPort.continueStory(generationRequest))
                .orElseGet(() -> Mono.empty());
    }

    private StoryGenerationRequest buildGenerationRequest(MessageReceived useCase, ChannelConfig channelConfig) {

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

        return StoryGenerationRequest.builder()
                .botNickname(useCase.getBotNickname())
                .botUsername(useCase.getBotUsername())
                .channelId(useCase.getChannelId())
                .guildId(useCase.getGuildId())
                .mentionedUsersIds(useCase.getMentionedUsersIds())
                .moderation(moderation)
                .modelConfiguration(modelConfigurationRequest)
                .personaId(channelConfig.getPersonaId())
                .worldId(channelConfig.getWorldId())
                .build();
    }
}