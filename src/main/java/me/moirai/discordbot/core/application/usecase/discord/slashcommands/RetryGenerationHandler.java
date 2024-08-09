package me.moirai.discordbot.core.application.usecase.discord.slashcommands;

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
public class RetryGenerationHandler extends AbstractUseCaseHandler<RetryGeneration, Mono<Void>> {

    private static final String COMMAND_ONLY_WHEN_LAST_MESSAGE_BY_BOT = "This command can only be used if the last message in channel was sent by the bot.";

    private final DiscordChannelPort discordChannelPort;
    private final ChannelConfigRepository channelConfigRepository;
    private final StoryGenerationHelper storyGenerationPort;

    public RetryGenerationHandler(DiscordChannelPort discordChannelPort,
            StoryGenerationHelper storyGenerationPort,
            ChannelConfigRepository channelConfigRepository) {

        this.discordChannelPort = discordChannelPort;
        this.channelConfigRepository = channelConfigRepository;
        this.storyGenerationPort = storyGenerationPort;
    }

    @Override
    public Mono<Void> execute(RetryGeneration useCase) {

        try {
            DiscordMessageData lastMessageSent = discordChannelPort.getLastMessageIn(useCase.getChannelId())
                    .orElseThrow(() -> new IllegalStateException("Channel has no messages"));

            if (!lastMessageSent.getAuthor().getId().equals(useCase.getBotId())) {
                return Mono.error(() -> new IllegalStateException(COMMAND_ONLY_WHEN_LAST_MESSAGE_BY_BOT));
            }

            discordChannelPort.deleteMessageById(useCase.getChannelId(), lastMessageSent.getId());

            DiscordMessageData messageBeforeLast = discordChannelPort.getLastMessageIn(useCase.getChannelId())
                    .orElseThrow(() -> new IllegalStateException("Channel has no messages"));

            return channelConfigRepository.findByDiscordChannelId(useCase.getChannelId())
                    .filter(channelConfig -> channelConfig.getDiscordChannelId().equals(useCase.getChannelId()))
                    .map(channelConfig -> buildGenerationRequest(useCase, channelConfig, messageBeforeLast))
                    .map(generationRequest -> storyGenerationPort.continueStory(generationRequest))
                    .orElseGet(() -> Mono.empty());
        } catch (IllegalStateException e) {
            return Mono.error(e);
        } catch (Exception e) {
            return Mono.error(
                    () -> new IllegalStateException("An error occurred while retrying generation of output"));
        }
    }

    private StoryGenerationRequest buildGenerationRequest(RetryGeneration useCase,
            ChannelConfig channelConfig, DiscordMessageData message) {

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

        List<DiscordMessageData> messageHistory = discordChannelPort.retrieveEntireHistoryFrom(useCase.getChannelId());

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