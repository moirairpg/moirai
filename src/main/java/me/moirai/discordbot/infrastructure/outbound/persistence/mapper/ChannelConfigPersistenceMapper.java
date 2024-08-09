package me.moirai.discordbot.infrastructure.outbound.persistence.mapper;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import me.moirai.discordbot.core.application.usecase.channelconfig.result.GetChannelConfigResult;
import me.moirai.discordbot.core.application.usecase.channelconfig.result.SearchChannelConfigsResult;
import me.moirai.discordbot.core.domain.Permissions;
import me.moirai.discordbot.core.domain.Visibility;
import me.moirai.discordbot.core.domain.channelconfig.ArtificialIntelligenceModel;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;
import me.moirai.discordbot.core.domain.channelconfig.GameMode;
import me.moirai.discordbot.core.domain.channelconfig.ModelConfiguration;
import me.moirai.discordbot.core.domain.channelconfig.Moderation;
import me.moirai.discordbot.infrastructure.outbound.persistence.channelconfig.ChannelConfigEntity;
import me.moirai.discordbot.infrastructure.outbound.persistence.channelconfig.ModelConfigurationEntity;

@Component
public class ChannelConfigPersistenceMapper {

    public ChannelConfigEntity mapToEntity(ChannelConfig channelConfig) {

        String creatorOrOwnerDiscordId = isBlank(channelConfig.getCreatorDiscordId())
                ? channelConfig.getOwnerDiscordId()
                : channelConfig.getCreatorDiscordId();

        ModelConfigurationEntity modelConfiguration = ModelConfigurationEntity.builder()
                .aiModel(channelConfig.getModelConfiguration().getAiModel().getInternalModelName())
                .frequencyPenalty(channelConfig.getModelConfiguration().getFrequencyPenalty())
                .presencePenalty(channelConfig.getModelConfiguration().getPresencePenalty())
                .temperature(channelConfig.getModelConfiguration().getTemperature())
                .logitBias(channelConfig.getModelConfiguration().getLogitBias())
                .maxTokenLimit(channelConfig.getModelConfiguration().getMaxTokenLimit())
                .stopSequences(channelConfig.getModelConfiguration().getStopSequences())
                .build();

        return ChannelConfigEntity.builder()
                .id(channelConfig.getId())
                .name(channelConfig.getName())
                .personaId(channelConfig.getPersonaId())
                .worldId(channelConfig.getWorldId())
                .discordChannelId(channelConfig.getDiscordChannelId())
                .modelConfiguration(modelConfiguration)
                .visibility(channelConfig.getVisibility().toString())
                .moderation(channelConfig.getModeration().toString())
                .ownerDiscordId(channelConfig.getOwnerDiscordId())
                .creatorDiscordId(creatorOrOwnerDiscordId)
                .usersAllowedToWrite(channelConfig.getUsersAllowedToWrite())
                .usersAllowedToRead(channelConfig.getUsersAllowedToRead())
                .creationDate(channelConfig.getCreationDate())
                .lastUpdateDate(channelConfig.getLastUpdateDate())
                .gameMode(channelConfig.getGameMode().name())
                .build();
    }

    public ChannelConfig mapFromEntity(ChannelConfigEntity channelConfig) {

        ModelConfiguration modelConfiguration = ModelConfiguration.builder()
                .aiModel(ArtificialIntelligenceModel.findByInternalModelName(channelConfig.getModelConfiguration().getAiModel()))
                .frequencyPenalty(channelConfig.getModelConfiguration().getFrequencyPenalty())
                .presencePenalty(channelConfig.getModelConfiguration().getPresencePenalty())
                .temperature(channelConfig.getModelConfiguration().getTemperature())
                .logitBias(channelConfig.getModelConfiguration().getLogitBias())
                .maxTokenLimit(channelConfig.getModelConfiguration().getMaxTokenLimit())
                .stopSequences(channelConfig.getModelConfiguration().getStopSequences())
                .build();

        Permissions permissions = Permissions.builder()
                .ownerDiscordId(channelConfig.getOwnerDiscordId())
                .usersAllowedToRead(channelConfig.getUsersAllowedToRead())
                .usersAllowedToWrite(channelConfig.getUsersAllowedToWrite())
                .build();

        return ChannelConfig.builder()
                .id(channelConfig.getId())
                .name(channelConfig.getName())
                .personaId(channelConfig.getPersonaId())
                .worldId(channelConfig.getWorldId())
                .discordChannelId(channelConfig.getDiscordChannelId())
                .modelConfiguration(modelConfiguration)
                .permissions(permissions)
                .visibility(Visibility.fromString(channelConfig.getVisibility()))
                .moderation(Moderation.fromString(channelConfig.getModeration()))
                .creatorDiscordId(channelConfig.getCreatorDiscordId())
                .creationDate(channelConfig.getCreationDate())
                .lastUpdateDate(channelConfig.getLastUpdateDate())
                .gameMode(GameMode.fromString(channelConfig.getGameMode()))
                .build();
    }

    public GetChannelConfigResult mapToResult(ChannelConfigEntity channelConfig) {

        return GetChannelConfigResult.builder()
                .id(channelConfig.getId())
                .name(channelConfig.getName())
                .worldId(channelConfig.getWorldId())
                .personaId(channelConfig.getPersonaId())
                .discordChannelId(channelConfig.getDiscordChannelId())
                .visibility(channelConfig.getVisibility())
                .aiModel(channelConfig.getModelConfiguration().getAiModel())
                .moderation(channelConfig.getModeration())
                .maxTokenLimit(channelConfig.getModelConfiguration().getMaxTokenLimit())
                .messageHistorySize(channelConfig.getModelConfiguration().getMessageHistorySize())
                .temperature(channelConfig.getModelConfiguration().getTemperature())
                .frequencyPenalty(channelConfig.getModelConfiguration().getFrequencyPenalty())
                .presencePenalty(channelConfig.getModelConfiguration().getPresencePenalty())
                .stopSequences(channelConfig.getModelConfiguration().getStopSequences())
                .logitBias(channelConfig.getModelConfiguration().getLogitBias())
                .usersAllowedToWrite(channelConfig.getUsersAllowedToWrite())
                .usersAllowedToRead(channelConfig.getUsersAllowedToRead())
                .ownerDiscordId(channelConfig.getOwnerDiscordId())
                .creationDate(channelConfig.getCreationDate())
                .lastUpdateDate(channelConfig.getLastUpdateDate())
                .gameMode(channelConfig.getGameMode())
                .build();
    }

    public SearchChannelConfigsResult mapToResult(Page<ChannelConfigEntity> pagedResult) {
        return SearchChannelConfigsResult.builder()
                .results(pagedResult.getContent()
                        .stream()
                        .map(this::mapToResult)
                        .toList())
                .page(pagedResult.getNumber() + 1)
                .items(pagedResult.getNumberOfElements())
                .totalItems(pagedResult.getTotalElements())
                .totalPages(pagedResult.getTotalPages())
                .build();
    }
}
