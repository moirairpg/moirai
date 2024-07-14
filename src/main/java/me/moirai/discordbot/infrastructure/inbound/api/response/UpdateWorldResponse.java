package me.moirai.discordbot.infrastructure.inbound.api.response;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateWorldResponse {

    private OffsetDateTime lastUpdateDate;

    public UpdateWorldResponse() {
    }

    private UpdateWorldResponse(OffsetDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public static UpdateWorldResponse build(OffsetDateTime lastUpdateDate) {

        return new UpdateWorldResponse(lastUpdateDate);
    }

    public OffsetDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }
}
