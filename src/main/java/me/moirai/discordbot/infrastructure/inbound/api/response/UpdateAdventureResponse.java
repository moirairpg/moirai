package me.moirai.discordbot.infrastructure.inbound.api.response;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateAdventureResponse {

    private OffsetDateTime lastUpdateDate;

    public UpdateAdventureResponse() {
    }

    private UpdateAdventureResponse(OffsetDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public static UpdateAdventureResponse build(OffsetDateTime lastUpdateDate) {

        return new UpdateAdventureResponse(lastUpdateDate);
    }

    public OffsetDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }
}
