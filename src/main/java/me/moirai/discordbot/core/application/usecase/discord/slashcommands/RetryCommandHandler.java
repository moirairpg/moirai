package me.moirai.discordbot.core.application.usecase.discord.slashcommands;

import static me.moirai.discordbot.core.domain.channelconfig.Moderation.DISABLED;

import java.util.ArrayList;
import java.util.List;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.helper.StoryGenerationHelper;
import me.moirai.discordbot.core.application.port.ChannelConfigQueryRepository;
import me.moirai.discordbot.core.application.port.DiscordChannelPort;
import me.moirai.discordbot.core.application.usecase.discord.DiscordMessageData;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.AiModelRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.ModelConfigurationRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.ModerationConfigurationRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.StoryGenerationRequest;
import reactor.core.publisher.Mono;

@UseCaseHandler
public class RetryCommandHandler extends AbstractUseCaseHandler<RetryCommand, Mono<Void>> {

    private static final String CHANNEL_HAS_NO_MESSAGES = "Channel has no messages";
    private static final String COMMAND_ONLY_WHEN_LAST_MESSAGE_BY_BOT = "This command can only be used if the last message in channel was sent by the bot.";

    private final DiscordChannelPort discordChannelPort;
    private final StoryGenerationHelper storyGenerationPort;
    private final ChannelConfigQueryRepository channelConfigRepository;

    public RetryCommandHandler(DiscordChannelPort discordChannelPort,
            StoryGenerationHelper storyGenerationPort,
            ChannelConfigQueryRepository channelConfigRepository) {

        this.discordChannelPort = discordChannelPort;
        this.channelConfigRepository = channelConfigRepository;
        this.storyGenerationPort = storyGenerationPort;
    }

    @Override
    public Mono<Void> execute(RetryCommand useCase) {

        try {
            DiscordMessageData lastMessageSent = discordChannelPort.getLastMessageIn(useCase.getChannelId())
                    .orElseThrow(() -> new IllegalStateException(CHANNEL_HAS_NO_MESSAGES));

            if (!lastMessageSent.getAuthor().getId().equals(useCase.getBotId())) {
                return Mono.error(() -> new IllegalStateException(COMMAND_ONLY_WHEN_LAST_MESSAGE_BY_BOT));
            }

            discordChannelPort.deleteMessageById(useCase.getChannelId(), lastMessageSent.getId());

            return channelConfigRepository.findByDiscordChannelId(useCase.getChannelId())
                    .filter(channelConfig -> channelConfig.getDiscordChannelId().equals(useCase.getChannelId()))
                    .map(channelConfig -> buildGenerationRequest(useCase, channelConfig))
                    .map(storyGenerationPort::continueStory)
                    .orElseGet(Mono::empty);
        } catch (IllegalStateException e) {
            return Mono.error(e);
        } catch (Exception e) {
            return Mono.error(
                    () -> new IllegalStateException("An error occurred while retrying generation of output"));
        }
    }

    private StoryGenerationRequest buildGenerationRequest(RetryCommand useCase,
            ChannelConfig channelConfig) {

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

        boolean isModerationEnabled = !channelConfig.getModeration().equals(DISABLED);
        ModerationConfigurationRequest moderation = ModerationConfigurationRequest
                .build(isModerationEnabled, channelConfig.getModeration().isAbsolute(),
                        channelConfig.getModeration().getThresholds());

        List<DiscordMessageData> messageHistory = getMessageHistory(useCase.getChannelId());

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
                .gameMode(channelConfig.getGameMode().name())
                .build();
    }

    private List<DiscordMessageData> getMessageHistory(String channelId) {

        DiscordMessageData lastMessageSent = discordChannelPort.getLastMessageIn(channelId)
                .orElseThrow(() -> new IllegalStateException(CHANNEL_HAS_NO_MESSAGES));

        List<DiscordMessageData> messageHistory = new ArrayList<>(discordChannelPort
                .retrieveEntireHistoryBefore(lastMessageSent.getId(), channelId));

        messageHistory.addFirst(lastMessageSent);

        return messageHistory;
    }
}