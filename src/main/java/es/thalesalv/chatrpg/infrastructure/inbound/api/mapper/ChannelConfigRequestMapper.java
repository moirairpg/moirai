package es.thalesalv.chatrpg.infrastructure.inbound.api.mapper;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.core.application.usecase.channelconfig.request.CreateChannelConfig;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.request.DeleteChannelConfig;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.request.UpdateChannelConfig;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.CreateChannelConfigRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.UpdateChannelConfigRequest;

@Component
public class ChannelConfigRequestMapper {

    public CreateChannelConfig toCommand(CreateChannelConfigRequest request, String requesterDiscordId) {

        return CreateChannelConfig.builder()
                .name(request.getName())
                .worldId(request.getWorldId())
                .personaId(request.getPersonaId())
                .visibility(request.getVisibility())
                .aiModel(request.getAiModel())
                .moderation(request.getModeration())
                .maxTokenLimit(request.getMaxTokenLimit())
                .temperature(request.getTemperature())
                .frequencyPenalty(request.getFrequencyPenalty())
                .presencePenalty(request.getPresencePenalty())
                .stopSequences(request.getStopSequences())
                .logitBias(request.getLogitBias())
                .usersAllowedToWrite(request.getUsersAllowedToWrite())
                .usersAllowedToRead(request.getUsersAllowedToRead())
                .discordChannelId(request.getDiscordChannelId())
                .requesterDiscordId(requesterDiscordId)
                .build();
    }

    public UpdateChannelConfig toCommand(UpdateChannelConfigRequest request, String worldId, String requesterDiscordId) {

        return UpdateChannelConfig.builder()
                .id(worldId)
                .name(request.getName())
                .worldId(request.getWorldId())
                .personaId(request.getPersonaId())
                .visibility(request.getVisibility())
                .aiModel(request.getAiModel())
                .moderation(request.getModeration())
                .maxTokenLimit(request.getMaxTokenLimit())
                .temperature(request.getTemperature())
                .frequencyPenalty(request.getFrequencyPenalty())
                .presencePenalty(request.getPresencePenalty())
                .stopSequencesToAdd(request.getStopSequencesToAdd())
                .stopSequencesToRemove(request.getStopSequencesToRemove())
                .logitBiasToAdd(request.getLogitBiasToAdd())
                .logitBiasToRemove(request.getLogitBiasToRemove())
                .usersAllowedToWriteToAdd(request.getUsersAllowedToWriteToAdd())
                .usersAllowedToWriteToRemove(request.getUsersAllowedToWriteToRemove())
                .usersAllowedToReadToAdd(request.getUsersAllowedToReadToAdd())
                .usersAllowedToReadToRemove(request.getUsersAllowedToReadToRemove())
                .discordChannelId(request.getDiscordChannelId())
                .requesterDiscordId(requesterDiscordId)
                .build();
    }

    public DeleteChannelConfig toCommand(String worldId, String requesterDiscordId) {

        return DeleteChannelConfig.build(worldId, requesterDiscordId);
    }
}
