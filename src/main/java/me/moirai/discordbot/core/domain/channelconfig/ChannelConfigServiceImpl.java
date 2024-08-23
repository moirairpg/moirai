package me.moirai.discordbot.core.domain.channelconfig;

import static me.moirai.discordbot.core.domain.channelconfig.ArtificialIntelligenceModel.findByInternalModelName;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import me.moirai.discordbot.common.annotation.DomainService;
import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.CreateChannelConfig;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.DeleteChannelConfig;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.UpdateChannelConfig;
import me.moirai.discordbot.core.domain.Permissions;
import me.moirai.discordbot.core.domain.Visibility;

@DomainService
public class ChannelConfigServiceImpl implements ChannelConfigService {

    private final ChannelConfigDomainRepository repository;

    public ChannelConfigServiceImpl(ChannelConfigDomainRepository repository) {
        this.repository = repository;
    }

    @Override
    public ChannelConfig getById(String channelConfigId) {

        ChannelConfig channelConfig = repository.findById(channelConfigId)
                .orElseThrow(() -> new AssetNotFoundException("Channel config to be viewed was not found"));

        return channelConfig;
    }

    @Override
    public void delete(DeleteChannelConfig command) {

        ChannelConfig channelConfig = repository.findById(command.getId())
                .orElseThrow(() -> new AssetNotFoundException("Channel config to be deleted was not found"));

        if (!channelConfig.canUserWrite(command.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException("User does not have permission to modify this channel config");
        }

        repository.deleteById(command.getId());
    }

    @Override
    public ChannelConfig createFrom(CreateChannelConfig command) {

        ModelConfiguration modelConfiguration = ModelConfiguration.builder()
                .aiModel(findByInternalModelName(command.getAiModel()))
                .frequencyPenalty(command.getFrequencyPenalty())
                .presencePenalty(command.getPresencePenalty())
                .temperature(command.getTemperature())
                .logitBias(command.getLogitBias())
                .maxTokenLimit(command.getMaxTokenLimit())
                .stopSequences(command.getStopSequences())
                .build();

        Permissions permissions = Permissions.builder()
                .ownerDiscordId(command.getRequesterDiscordId())
                .usersAllowedToRead(command.getUsersAllowedToRead())
                .usersAllowedToWrite(command.getUsersAllowedToWrite())
                .build();

        ChannelConfig channelConfig = ChannelConfig.builder()
                .modelConfiguration(modelConfiguration)
                .permissions(permissions)
                .name(command.getName())
                .personaId(command.getPersonaId())
                .worldId(command.getWorldId())
                .discordChannelId(command.getDiscordChannelId())
                .gameMode(GameMode.fromString(command.getGameMode()))
                .visibility(Visibility.fromString(command.getVisibility()))
                .moderation(Moderation.fromString(command.getModeration()))
                .build();

        return repository.save(channelConfig);
    }

    @Override
    public ChannelConfig update(UpdateChannelConfig command) {

        ChannelConfig channelConfig = repository.findById(command.getId())
                .orElseThrow(() -> new AssetNotFoundException("Channel config to be updated was not found"));

        if (!channelConfig.canUserWrite(command.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException("User does not have permission to modify this channel config");
        }

        if (StringUtils.isNotBlank(command.getName())) {
            channelConfig.updateName(command.getName());
        }

        if (StringUtils.isNotBlank(command.getWorldId())) {
            channelConfig.updateWorld(command.getWorldId());
        }

        if (StringUtils.isNotBlank(command.getPersonaId())) {
            channelConfig.updatePersona(command.getPersonaId());
        }

        if (StringUtils.isNotBlank(command.getAiModel())) {
            channelConfig.updateAiModel(findByInternalModelName(command.getAiModel()));
        }

        if (StringUtils.isNotBlank(command.getModeration())) {
            channelConfig.updateModeration(Moderation.fromString(command.getModeration()));
        }

        if (StringUtils.isNotBlank(command.getDiscordChannelId())) {
            channelConfig.updateDiscordChannel(command.getDiscordChannelId());
        }

        if (StringUtils.isNotBlank(command.getGameMode())) {
            channelConfig.updateGameMode(GameMode.fromString(command.getGameMode()));
        }

        if (command.getTemperature() != null) {
            channelConfig.updateTemperature(command.getTemperature());
        }

        if (command.getFrequencyPenalty() != null) {
            channelConfig.updateFrequencyPenalty(command.getFrequencyPenalty());
        }

        if (command.getPresencePenalty() != null) {
            channelConfig.updatePresencePenalty(command.getPresencePenalty());
        }

        if (StringUtils.isNotBlank(command.getVisibility())) {
            if (command.getVisibility().equalsIgnoreCase(Visibility.PUBLIC.name())) {
                channelConfig.makePublic();
            } else if (command.getVisibility().equalsIgnoreCase(Visibility.PRIVATE.name())) {
                channelConfig.makePrivate();
            }
        }

        CollectionUtils.emptyIfNull(command.getStopSequencesToAdd())
                .stream()
                .filter(stopSequence -> !channelConfig.getModelConfiguration()
                        .getStopSequences().contains(stopSequence))
                .forEach(channelConfig::addStopSequence);

        CollectionUtils.emptyIfNull(command.getStopSequencesToRemove())
                .forEach(channelConfig::removeStopSequence);

        MapUtils.emptyIfNull(command.getLogitBiasToAdd())
                .entrySet()
                .stream()
                .filter(entry -> !channelConfig.getModelConfiguration().getLogitBias().containsKey(entry.getKey()))
                .forEach(entry -> channelConfig.addLogitBias(entry.getKey(), entry.getValue()));

        CollectionUtils.emptyIfNull(command.getLogitBiasToRemove())
                .forEach(channelConfig::removeLogitBias);

        CollectionUtils.emptyIfNull(command.getUsersAllowedToReadToAdd())
                .stream()
                .filter(userId -> !channelConfig.canUserRead(userId))
                .forEach(channelConfig::addReaderUser);

        CollectionUtils.emptyIfNull(command.getUsersAllowedToWriteToAdd())
                .stream()
                .filter(userId -> !channelConfig.canUserWrite(userId))
                .forEach(channelConfig::addWriterUser);

        CollectionUtils.emptyIfNull(command.getUsersAllowedToReadToRemove())
                .forEach(channelConfig::removeReaderUser);

        CollectionUtils.emptyIfNull(command.getUsersAllowedToWriteToRemove())
                .forEach(channelConfig::removeWriterUser);

        return repository.save(channelConfig);
    }
}
