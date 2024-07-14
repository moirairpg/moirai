package me.moirai.discordbot.infrastructure.inbound.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateChannelConfigResponse {

    private String id;

    public CreateChannelConfigResponse() {
    }

    private CreateChannelConfigResponse(String id) {
        this.id = id;
    }

    public static CreateChannelConfigResponse build(String id) {

        return new CreateChannelConfigResponse(id);
    }

    public String getId() {
        return id;
    }
}
