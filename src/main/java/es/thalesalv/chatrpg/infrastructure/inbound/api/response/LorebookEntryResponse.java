package es.thalesalv.chatrpg.infrastructure.inbound.api.response;

import java.time.OffsetDateTime;

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
public class LorebookEntryResponse {

    private String id;
    private String name;
    private String regex;
    private String description;
    private String playerDiscordId;
    private boolean isPlayerCharacter;
    private OffsetDateTime creationDate;
    private OffsetDateTime lastUpdateDate;
}
