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
public class DiscordTokenRevocationRequest {

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("client_secret")
    private String clientSecret;

    @JsonProperty("token")
    private String token;

    @JsonProperty("token_type_hint")
    private String tokenTypeHint;
}
