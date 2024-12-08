package me.moirai.discordbot.infrastructure.outbound.persistence.mapper;

import static me.moirai.discordbot.core.domain.adventure.ArtificialIntelligenceModel.fromInternalName;
import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import me.moirai.discordbot.core.application.usecase.adventure.result.GetAdventureResult;
import me.moirai.discordbot.core.application.usecase.adventure.result.SearchAdventuresResult;
import me.moirai.discordbot.core.domain.Permissions;
import me.moirai.discordbot.core.domain.Visibility;
import me.moirai.discordbot.core.domain.adventure.Adventure;
import me.moirai.discordbot.core.domain.adventure.ContextAttributes;
import me.moirai.discordbot.core.domain.adventure.GameMode;
import me.moirai.discordbot.core.domain.adventure.ModelConfiguration;
import me.moirai.discordbot.core.domain.adventure.Moderation;
import me.moirai.discordbot.infrastructure.outbound.persistence.adventure.AdventureEntity;
import me.moirai.discordbot.infrastructure.outbound.persistence.adventure.ContextAttributesEntity;
import me.moirai.discordbot.infrastructure.outbound.persistence.adventure.ModelConfigurationEntity;

@Component
public class AdventurePersistenceMapper {

    public AdventureEntity mapToEntity(Adventure adventure) {

        String creatorOrOwnerDiscordId = isBlank(adventure.getCreatorDiscordId())
                ? adventure.getOwnerDiscordId()
                : adventure.getCreatorDiscordId();

        ModelConfigurationEntity modelConfiguration = ModelConfigurationEntity.builder()
                .aiModel(adventure.getModelConfiguration().getAiModel().getInternalModelName())
                .frequencyPenalty(adventure.getModelConfiguration().getFrequencyPenalty())
                .presencePenalty(adventure.getModelConfiguration().getPresencePenalty())
                .temperature(adventure.getModelConfiguration().getTemperature())
                .logitBias(adventure.getModelConfiguration().getLogitBias())
                .maxTokenLimit(adventure.getModelConfiguration().getMaxTokenLimit())
                .stopSequences(adventure.getModelConfiguration().getStopSequences())
                .build();

        ContextAttributesEntity contextAttributes = ContextAttributesEntity.builder()
                .authorsNote(adventure.getContextAttributes().getAuthorsNote())
                .nudge(adventure.getContextAttributes().getNudge())
                .remember(adventure.getContextAttributes().getRemember())
                .bump(adventure.getContextAttributes().getBump())
                .bumpFrequency(adventure.getContextAttributes().getBumpFrequency())
                .build();

        return AdventureEntity.builder()
                .id(adventure.getId())
                .name(adventure.getName())
                .description(adventure.getDescription())
                .personaId(adventure.getPersonaId())
                .worldId(adventure.getWorldId())
                .discordChannelId(adventure.getDiscordChannelId())
                .modelConfiguration(modelConfiguration)
                .visibility(adventure.getVisibility().toString())
                .moderation(adventure.getModeration().toString())
                .ownerDiscordId(adventure.getOwnerDiscordId())
                .creatorDiscordId(creatorOrOwnerDiscordId)
                .usersAllowedToWrite(adventure.getUsersAllowedToWrite())
                .usersAllowedToRead(adventure.getUsersAllowedToRead())
                .creationDate(adventure.getCreationDate())
                .lastUpdateDate(adventure.getLastUpdateDate())
                .gameMode(adventure.getGameMode().name())
                .isMultiplayer(adventure.isMultiplayer())
                .version(adventure.getVersion())
                .adventureStart(adventure.getAdventureStart())
                .contextAttributes(contextAttributes)
                .build();
    }

    public Adventure mapFromEntity(AdventureEntity adventure) {

        ModelConfiguration modelConfiguration = ModelConfiguration.builder()
                .aiModel(fromInternalName(adventure.getModelConfiguration().getAiModel()))
                .frequencyPenalty(adventure.getModelConfiguration().getFrequencyPenalty())
                .presencePenalty(adventure.getModelConfiguration().getPresencePenalty())
                .temperature(adventure.getModelConfiguration().getTemperature())
                .logitBias(adventure.getModelConfiguration().getLogitBias())
                .maxTokenLimit(adventure.getModelConfiguration().getMaxTokenLimit())
                .stopSequences(adventure.getModelConfiguration().getStopSequences())
                .build();

        Permissions permissions = Permissions.builder()
                .ownerDiscordId(adventure.getOwnerDiscordId())
                .usersAllowedToRead(adventure.getUsersAllowedToRead())
                .usersAllowedToWrite(adventure.getUsersAllowedToWrite())
                .build();

        ContextAttributes contextAttributes = ContextAttributes.builder()
                .authorsNote(adventure.getContextAttributes().getAuthorsNote())
                .nudge(adventure.getContextAttributes().getNudge())
                .remember(adventure.getContextAttributes().getRemember())
                .bump(adventure.getContextAttributes().getBump())
                .bumpFrequency(adventure.getContextAttributes().getBumpFrequency())
                .build();

        return Adventure.builder()
                .id(adventure.getId())
                .name(adventure.getName())
                .description(adventure.getDescription())
                .personaId(adventure.getPersonaId())
                .worldId(adventure.getWorldId())
                .discordChannelId(adventure.getDiscordChannelId())
                .modelConfiguration(modelConfiguration)
                .permissions(permissions)
                .visibility(Visibility.fromString(adventure.getVisibility()))
                .moderation(Moderation.fromString(adventure.getModeration()))
                .creatorDiscordId(adventure.getCreatorDiscordId())
                .creationDate(adventure.getCreationDate())
                .lastUpdateDate(adventure.getLastUpdateDate())
                .gameMode(GameMode.fromString(adventure.getGameMode()))
                .isMultiplayer(adventure.isMultiplayer())
                .version(adventure.getVersion())
                .adventureStart(adventure.getAdventureStart())
                .contextAttributes(contextAttributes)
                .build();
    }

    public GetAdventureResult mapToResult(AdventureEntity adventure) {

        return GetAdventureResult.builder()
                .id(adventure.getId())
                .name(adventure.getName())
                .worldId(adventure.getWorldId())
                .personaId(adventure.getPersonaId())
                .discordChannelId(adventure.getDiscordChannelId())
                .visibility(adventure.getVisibility())
                .aiModel(adventure.getModelConfiguration().getAiModel())
                .moderation(adventure.getModeration())
                .maxTokenLimit(adventure.getModelConfiguration().getMaxTokenLimit())
                .temperature(adventure.getModelConfiguration().getTemperature())
                .frequencyPenalty(adventure.getModelConfiguration().getFrequencyPenalty())
                .presencePenalty(adventure.getModelConfiguration().getPresencePenalty())
                .stopSequences(adventure.getModelConfiguration().getStopSequences())
                .logitBias(adventure.getModelConfiguration().getLogitBias())
                .usersAllowedToWrite(adventure.getUsersAllowedToWrite())
                .usersAllowedToRead(adventure.getUsersAllowedToRead())
                .ownerDiscordId(adventure.getOwnerDiscordId())
                .creationDate(adventure.getCreationDate())
                .lastUpdateDate(adventure.getLastUpdateDate())
                .gameMode(adventure.getGameMode())
                .isMultiplayer(adventure.isMultiplayer())
                .build();
    }

    public SearchAdventuresResult mapToResult(Page<AdventureEntity> pagedResult) {
        return SearchAdventuresResult.builder()
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
