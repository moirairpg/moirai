package me.moirai.discordbot.core.application.usecase.discord.slashcommands;

import java.util.Collections;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.helper.StoryGenerationHelper;
import me.moirai.discordbot.core.application.port.DiscordChannelPort;
import me.moirai.discordbot.core.application.usecase.discord.DiscordMessageData;
import me.moirai.discordbot.core.application.usecase.discord.DiscordUserDetails;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigRepository;
import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.core.domain.world.WorldRepository;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.AiModelRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.ModelConfigurationRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.ModerationConfigurationRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.StoryGenerationRequest;
import reactor.core.publisher.Mono;

@UseCaseHandler
public class StartCommandHandler extends AbstractUseCaseHandler<StartCommand, Mono<Void>> {

    private static final String CHAT_FORMAT = "%s said: %s";

    private final ChannelConfigRepository channelConfigRepository;
    private final WorldRepository worldRepository;
    private final StoryGenerationHelper storyGenerationPort;
    private final DiscordChannelPort discordChannelPort;

    public StartCommandHandler(StoryGenerationHelper storyGenerationPort,
            WorldRepository worldRepository,
            ChannelConfigRepository channelConfigRepository,
            DiscordChannelPort discordChannelPort) {

        this.channelConfigRepository = channelConfigRepository;
        this.worldRepository = worldRepository;
        this.storyGenerationPort = storyGenerationPort;
        this.discordChannelPort = discordChannelPort;
    }

    @Override
    public Mono<Void> execute(StartCommand useCase) {

        try {
            return channelConfigRepository.findByDiscordChannelId(useCase.getChannelId())
                    .filter(channelConfig -> channelConfig.getDiscordChannelId().equals(useCase.getChannelId()))
                    .map(channelConfig -> buildGenerationRequest(useCase, channelConfig))
                    .map(generationRequest -> storyGenerationPort.continueStory(generationRequest))
                    .orElseGet(() -> Mono.empty());
        } catch (AssetNotFoundException e) {
            return Mono.error(() -> e);
        } catch (Exception e) {
            return Mono.error(
                    () -> new IllegalStateException("An error occurred while generating output"));
        }
    }

    private StoryGenerationRequest buildGenerationRequest(StartCommand useCase, ChannelConfig channelConfig) {

        World world = worldRepository.findById(channelConfig.getWorldId())
                .orElseThrow(() -> new AssetNotFoundException("Channel config has no world linked to it"));

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

        discordChannelPort.sendMessageTo(useCase.getChannelId(), world.getAdventureStart());

        DiscordMessageData adventureStartMessage = DiscordMessageData.builder()
                .channelId(useCase.getChannelId())
                .content(String.format(CHAT_FORMAT, useCase.getBotNickname(), world.getAdventureStart()))
                .mentionedUsers(Collections.emptyList())
                .author(DiscordUserDetails.builder()
                        .id(useCase.getBotId())
                        .nickname(useCase.getBotNickname())
                        .username(useCase.getBotUsername())
                        .build())
                .build();

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
                .messageHistory(Collections.singletonList(adventureStartMessage))
                .build();
    }
}
