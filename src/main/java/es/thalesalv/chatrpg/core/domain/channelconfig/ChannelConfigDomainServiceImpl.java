package es.thalesalv.chatrpg.core.domain.channelconfig;

import static es.thalesalv.chatrpg.core.domain.channelconfig.ArtificialIntelligenceModel.findByInternalModelName;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.exception.AssetAccessDeniedException;
import es.thalesalv.chatrpg.common.exception.AssetNotFoundException;
import es.thalesalv.chatrpg.core.application.command.channelconfig.CreateChannelConfig;
import es.thalesalv.chatrpg.core.application.command.channelconfig.DeleteChannelConfig;
import es.thalesalv.chatrpg.core.application.command.channelconfig.UpdateChannelConfig;
import es.thalesalv.chatrpg.core.application.query.channelconfig.GetChannelConfigById;
import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.Visibility;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ChannelConfigDomainServiceImpl implements ChannelConfigDomainService {

    private final ChannelConfigRepository repository;

    @Override
    public ChannelConfig getChannelConfigById(GetChannelConfigById query) {

        ChannelConfig channelConfig = repository.findById(query.getId())
                .orElseThrow(() -> new AssetNotFoundException("Channel config to be viewed was not found"));

        if (!channelConfig.canUserRead(query.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException("User does not have permission to view this channel config");
        }

        return channelConfig;
    }

    @Override
    public void deleteChannelConfig(DeleteChannelConfig command) {

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
                .messageHistorySize(command.getMessageHistorySize())
                .stopSequences(command.getStopSequences())
                .build();

        Permissions permissions = Permissions.builder()
                .ownerDiscordId(command.getRequesterDiscordId())
                .usersAllowedToRead(command.getReaderUsers())
                .usersAllowedToWrite(command.getWriterUsers())
                .build();

        ChannelConfig channelConfig = ChannelConfig.builder()
                .modelConfiguration(modelConfiguration)
                .permissions(permissions)
                .name(command.getName())
                .personaId(command.getPersonaId())
                .worldId(command.getWorldId())
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

        if (command.getTemperature() != null) {
            channelConfig.updateTemperature(command.getTemperature());
        }

        if (command.getFrequencyPenalty() != null) {
            channelConfig.updateFrequencyPenalty(command.getFrequencyPenalty());
        }

        if (command.getPresencePenalty() != null) {
            channelConfig.updatePresencePenalty(command.getPresencePenalty());
        }

        if (command.getVisibility().equalsIgnoreCase(Visibility.PUBLIC.name())) {
            channelConfig.makePublic();
        } else if (command.getVisibility().equalsIgnoreCase(Visibility.PRIVATE.name())) {
            channelConfig.makePrivate();
        }

        CollectionUtils.emptyIfNull(command.getStopSequencesToAdd())
                .forEach(channelConfig::addStopSequence);

        CollectionUtils.emptyIfNull(command.getStopSequencesToRemove())
                .forEach(channelConfig::removeStopSequence);

        MapUtils.emptyIfNull(command.getLogitBiasToAdd())
                .entrySet()
                .forEach(entry -> channelConfig.addLogitBias(entry.getKey(), entry.getValue()));

        CollectionUtils.emptyIfNull(command.getLogitBiasToRemove())
                .forEach(channelConfig::removeLogitBias);

        CollectionUtils.emptyIfNull(command.getReaderUsersToAdd())
                .forEach(channelConfig::addReaderUser);

        CollectionUtils.emptyIfNull(command.getWriterUsersToAdd())
                .forEach(channelConfig::addWriterUser);

        CollectionUtils.emptyIfNull(command.getReaderUsersToRemove())
                .forEach(channelConfig::removeReaderUser);

        CollectionUtils.emptyIfNull(command.getWriterUsersToRemove())
                .forEach(channelConfig::removeWriterUser);

        return repository.save(channelConfig);
    }
}
