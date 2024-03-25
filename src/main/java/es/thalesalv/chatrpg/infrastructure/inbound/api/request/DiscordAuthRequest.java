package es.thalesalv.chatrpg.infrastructure.inbound.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(builderClassName = "Builder")
@AllArgsConstructor
public class DiscordAuthRequest {

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("client_secret")
    private String clientSecret;

    @JsonProperty("grant_type")
    private String grantType;

    @JsonProperty("redirect_uri")
    private String redirectUri;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("code")
    private String code;
}
