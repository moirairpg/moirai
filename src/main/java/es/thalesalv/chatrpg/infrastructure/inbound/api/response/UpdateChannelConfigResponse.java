package es.thalesalv.chatrpg.infrastructure.inbound.api.response;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateChannelConfigResponse {

    private OffsetDateTime lastUpdateDate;

    public UpdateChannelConfigResponse() {
    }

    private UpdateChannelConfigResponse(OffsetDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public static UpdateChannelConfigResponse build(OffsetDateTime lastUpdateDate) {

        return new UpdateChannelConfigResponse(lastUpdateDate);
    }

    public OffsetDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }
}
