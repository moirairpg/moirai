package es.thalesalv.chatrpg.core.domain.channelconfig;

import static es.thalesalv.chatrpg.core.domain.channelconfig.ArtificialIntelligenceModel.findByInternalModelName;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.core.application.command.channelconfig.CreateChannelConfig;
import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.Visibility;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
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
}
