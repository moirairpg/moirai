package me.moirai.discordbot.infrastructure.inbound.api.response;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateLorebookEntryResponse {

    private OffsetDateTime lastUpdateDate;

    public UpdateLorebookEntryResponse() {
    }

    private UpdateLorebookEntryResponse(OffsetDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public static UpdateLorebookEntryResponse build(OffsetDateTime lastUpdateDate) {

        return new UpdateLorebookEntryResponse(lastUpdateDate);
    }

    public OffsetDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }
}
