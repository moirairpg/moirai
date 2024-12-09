package me.moirai.discordbot.core.application.usecase.discord.messagereceived;

import static me.moirai.discordbot.core.domain.channelconfig.GameMode.RPG;
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
public class RpgModeHandler extends AbstractUseCaseHandler<RpgModeRequest, Mono<Void>> {

    private static final String CHANNEL_HAS_NO_MESSAGES = "Channel has no messages";

    private final StoryGenerationHelper storyGenerationPort;
    private final ChannelConfigQueryRepository channelConfigRepository;
    private final DiscordChannelPort discordChannelPort;

    public RpgModeHandler(StoryGenerationHelper storyGenerationPort,
            ChannelConfigQueryRepository channelConfigRepository,
            DiscordChannelPort discordChannelPort) {

        this.channelConfigRepository = channelConfigRepository;
        this.storyGenerationPort = storyGenerationPort;
        this.discordChannelPort = discordChannelPort;
    }

    @Override
    public Mono<Void> execute(RpgModeRequest query) {

        return channelConfigRepository.findByDiscordChannelId(query.getChannelId())
                .filter(channelConfig -> channelConfig.getDiscordChannelId().equals(query.getChannelId()))
                .map(channelConfig -> {
                    StoryGenerationRequest generationRequest = buildGenerationRequest(query, channelConfig);

                    if (channelConfig.isMultiplayer()) {
                        return Mono.<Void>empty();
                    }

                    return storyGenerationPort.continueStory(generationRequest);
                })
                .orElseGet(Mono::empty);
    }

    private StoryGenerationRequest buildGenerationRequest(RpgModeRequest useCase, ChannelConfig channelConfig) {

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

        boolean isModerationEnabled = !channelConfig.getModeration().equals(DISABLED);
        ModerationConfigurationRequest moderation = ModerationConfigurationRequest
                .build(isModerationEnabled, channelConfig.getModeration().isAbsolute(),
                        channelConfig.getModeration().getThresholds());

        List<DiscordMessageData> messageHistory = getMessageHistory(useCase.getChannelId());

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
                .gameMode(RPG.name())
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