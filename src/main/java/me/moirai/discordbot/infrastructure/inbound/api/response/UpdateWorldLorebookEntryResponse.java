package me.moirai.discordbot.infrastructure.inbound.api.response;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateWorldLorebookEntryResponse {

    private OffsetDateTime lastUpdateDate;

    public UpdateWorldLorebookEntryResponse() {
    }

    private UpdateWorldLorebookEntryResponse(OffsetDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public static UpdateWorldLorebookEntryResponse build(OffsetDateTime lastUpdateDate) {

        return new UpdateWorldLorebookEntryResponse(lastUpdateDate);
    }

    public OffsetDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }
}
