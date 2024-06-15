package es.thalesalv.chatrpg.core.application.usecase.channelconfig.result;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(builderClassName = "Builder")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetChannelConfigResult {

    private final String id;
    private final String name;
    private final String worldId;
    private final String personaId;
    private final String discordChannelId;
    private final String visibility;
    private final String aiModel;
    private final String moderation;
    private final int maxTokenLimit;
    private final int messageHistorySize;
    private final double temperature;
    private final double frequencyPenalty;
    private final double presencePenalty;
    private final List<String> stopSequences;
    private final Map<String, Double> logitBias;
    private final String ownerDiscordId;
    private final List<String> usersAllowedToRead;
    private final List<String> usersAllowedToWrite;
    private final OffsetDateTime creationDate;
    private final OffsetDateTime lastUpdateDate;
}
