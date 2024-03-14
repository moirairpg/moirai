package es.thalesalv.chatrpg.core.domain.channelconfig;

import static es.thalesalv.chatrpg.core.domain.channelconfig.ArtificialIntelligenceModel.findByInternalModelName;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.exception.AssetNotFoundException;
import es.thalesalv.chatrpg.core.application.command.channelconfig.CreateChannelConfig;
import es.thalesalv.chatrpg.core.application.command.channelconfig.UpdateChannelConfig;
import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.Visibility;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ChannelConfigDomainServiceImpl implements ChannelConfigDomainService {

    private final ChannelConfigRepository repository;

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
                .build();

        Permissions permissions = Permissions.builder()
                .ownerDiscordId(command.getCreatorDiscordId())
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

        // TODO extract real ID from principal when API is ready
        repository.findById(command.getId(), "owner")
                .orElseThrow(() -> new AssetNotFoundException("Channel config to be updated was not found"));

        ModelConfiguration modelConfiguration = ModelConfiguration.builder()
                .aiModel(findByInternalModelName(command.getAiModel()))
                .frequencyPenalty(command.getFrequencyPenalty())
                .presencePenalty(command.getPresencePenalty())
                .temperature(command.getTemperature())
                .logitBias(command.getLogitBias())
                .maxTokenLimit(command.getMaxTokenLimit())
                .messageHistorySize(command.getMessageHistorySize())
                .build();

        Permissions permissions = Permissions.builder()
                .ownerDiscordId(command.getCreatorDiscordId())
                .usersAllowedToRead(command.getReaderUsers())
                .usersAllowedToWrite(command.getWriterUsers())
                .build();

        ChannelConfig channelConfig = ChannelConfig.builder()
                .id(command.getId())
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
}
