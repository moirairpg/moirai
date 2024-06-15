package es.thalesalv.chatrpg.core.application.usecase.channelconfig.request;

import java.util.List;
import java.util.Map;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.result.UpdateChannelConfigResult;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(builderClassName = "Builder")
public final class UpdateChannelConfig extends UseCase<UpdateChannelConfigResult> {

    private final String id;
    private final String name;
    private final String worldId;
    private final String personaId;
    private final String discordChannelId;
    private final String visibility;
    private final String aiModel;
    private final String moderation;
    private final Integer maxTokenLimit;
    private final Double temperature;
    private final Double frequencyPenalty;
    private final Double presencePenalty;
    private final List<String> stopSequencesToAdd;
    private final List<String> stopSequencesToRemove;
    private final Map<String, Double> logitBiasToAdd;
    private final List<String> logitBiasToRemove;
    private final List<String> usersAllowedToWriteToAdd;
    private final List<String> usersAllowedToWriteToRemove;
    private final List<String> usersAllowedToReadToAdd;
    private final List<String> usersAllowedToReadToRemove;
    private final String requesterDiscordId;
}
