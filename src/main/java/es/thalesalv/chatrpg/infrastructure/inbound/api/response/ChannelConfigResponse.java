package es.thalesalv.chatrpg.infrastructure.inbound.api.response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(builderClassName = "Builder")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChannelConfigResponse {

    private String id;
    private String name;
    private String worldId;
    private String personaId;
    private String discordChannelId;
    private String visibility;
    private String aiModel;
    private String moderation;
    private Integer maxTokenLimit;
    private Integer messageHistorySize;
    private Double temperature;
    private Double frequencyPenalty;
    private Double presencePenalty;
    private List<String> stopSequences;
    private Map<String, Double> logitBias;
    private String ownerDiscordId;
    private List<String> usersAllowedToRead;
    private List<String> usersAllowedToWrite;
    private OffsetDateTime creationDate;
    private OffsetDateTime lastUpdateDate;
}
