package es.thalesalv.chatrpg.infrastructure.inbound.api.response;

import java.time.OffsetDateTime;
import java.util.List;

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
public class PersonaResponse {

    private String id;
    private String name;
    private String personality;
    private String nudgeContent;
    private String nudgeRole;
    private String bumpContent;
    private String bumpRole;
    private Integer bumpFrequency;
    private String visibility;
    private String gameMode;
    private String ownerDiscordId;
    private List<String> usersAllowedToWrite;
    private List<String> usersAllowedToRead;
    private OffsetDateTime creationDate;
    private OffsetDateTime lastUpdateDate;
}
