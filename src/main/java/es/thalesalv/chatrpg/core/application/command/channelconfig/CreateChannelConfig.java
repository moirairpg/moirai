package es.thalesalv.chatrpg.core.application.command.channelconfig;

import java.util.List;
import java.util.Map;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(builderClassName = "Builder")
public final class CreateChannelConfig extends UseCase<CreateChannelConfigResult> {

    private final String name;
    private final String worldId;
    private final String personaId;
    private final String visibility;
    private final String aiModel;
    private final String moderation;
    private final Integer maxTokenLimit;
    private final Integer messageHistorySize;
    private final Double temperature;
    private final Double frequencyPenalty;
    private final Double presencePenalty;
    private final List<String> stopSequences;
    private final Map<String, Double> logitBias;
    private final List<String> writerUsers;
    private final List<String> readerUsers;
    private final String requesterDiscordId;
}
