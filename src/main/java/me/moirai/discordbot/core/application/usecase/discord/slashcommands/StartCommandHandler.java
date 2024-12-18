package me.moirai.discordbot.core.application.usecase.discord.slashcommands;

import static me.moirai.discordbot.core.domain.adventure.Moderation.DISABLED;

import java.util.Collections;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.helper.StoryGenerationHelper;
import me.moirai.discordbot.core.application.port.AdventureQueryRepository;
import me.moirai.discordbot.core.application.port.DiscordChannelPort;
import me.moirai.discordbot.core.application.usecase.discord.DiscordMessageData;
import me.moirai.discordbot.core.application.usecase.discord.DiscordUserDetails;
import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.world.World;
import me.moirai.discordbot.core.domain.world.WorldDomainRepository;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.AiModelRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.ModelConfigurationRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.ModerationConfigurationRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.StoryGenerationRequest;
import reactor.core.publisher.Mono;

@UseCaseHandler
public class StartCommandHandler extends AbstractUseCaseHandler<StartCommand, Mono<Void>> {

    private static final String CHAT_FORMAT = "%s said: %s";

    private final AdventureQueryRepository adventureRepository;
    private final WorldDomainRepository worldRepository;
    private final StoryGenerationHelper storyGenerationPort;
    private final DiscordChannelPort discordChannelPort;

    public StartCommandHandler(StoryGenerationHelper storyGenerationPort,
            WorldDomainRepository worldRepository,
            AdventureQueryRepository adventureRepository,
            DiscordChannelPort discordChannelPort) {

        this.adventureRepository = adventureRepository;
        this.worldRepository = worldRepository;
        this.storyGenerationPort = storyGenerationPort;
        this.discordChannelPort = discordChannelPort;
    }

    @Override
    public Mono<Void> execute(StartCommand useCase) {

        try {
            return adventureRepository.findByDiscordChannelId(useCase.getChannelId())
                    .filter(adventure -> adventure.getDiscordChannelId().equals(useCase.getChannelId()))
                    .map(adventure -> buildGenerationRequest(useCase, adventure))
                    .map(storyGenerationPort::continueStory)
                    .orElseGet(Mono::empty);
        } catch (AssetNotFoundException e) {
            return Mono.error(() -> e);
        } catch (Exception e) {
            return Mono.error(
                    () -> new IllegalStateException("An error occurred while generating output"));
        }
    }

    private StoryGenerationRequest buildGenerationRequest(StartCommand useCase, Adventure adventure) {

        World world = worldRepository.findById(adventure.getWorldId())
                .orElseThrow(() -> new AssetNotFoundException("Adventure has no world linked to it"));

        ModelConfigurationRequest modelConfigurationRequest = ModelConfigurationRequest.builder()
                .frequencyPenalty(adventure.getModelConfiguration().getFrequencyPenalty())
                .presencePenalty(adventure.getModelConfiguration().getPresencePenalty())
                .logitBias(adventure.getModelConfiguration().getLogitBias())
                .maxTokenLimit(adventure.getModelConfiguration().getMaxTokenLimit())
                .stopSequences(adventure.getModelConfiguration().getStopSequences())
                .temperature(adventure.getModelConfiguration().getTemperature())
                .aiModel(AiModelRequest
                        .build(adventure.getModelConfiguration().getAiModel().getInternalModelName(),
                                adventure.getModelConfiguration().getAiModel().getOfficialModelName(),
                                adventure.getModelConfiguration().getAiModel().getHardTokenLimit()))
                .build();

        boolean isModerationEnabled = !adventure.getModeration().equals(DISABLED);
        ModerationConfigurationRequest moderation = ModerationConfigurationRequest
                .build(isModerationEnabled, adventure.getModeration().isAbsolute(),
                        adventure.getModeration().getThresholds());

        discordChannelPort.sendTextMessageTo(useCase.getChannelId(), world.getAdventureStart());

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
                .personaId(adventure.getPersonaId())
                .worldId(adventure.getWorldId())
                .messageHistory(Collections.singletonList(adventureStartMessage))
                .gameMode(adventure.getGameMode().name())
                .nudge(adventure.getContextAttributes().getNudge())
                .authorsNote(adventure.getContextAttributes().getAuthorsNote())
                .remember(adventure.getContextAttributes().getRemember())
                .bump(adventure.getContextAttributes().getBump())
                .bumpFrequency(adventure.getContextAttributes().getBumpFrequency())
                .build();
    }
}
