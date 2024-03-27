package es.thalesalv.chatrpg.infrastructure.inbound.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

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
public class DiscordUserDataResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("redirect_uri")
    private String username;

    @JsonProperty("global_name")
    private String globalName;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("discriminator")
    private String discriminator;

    @JsonProperty("email")
    private String email;

    @JsonProperty("error")
    private DiscordErrorResponse error;
}
