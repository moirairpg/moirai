package es.thalesalv.chatrpg.infrastructure.inbound.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscordErrorResponse {

    @JsonProperty("error")
    private String error;

    @JsonProperty("error_description")
    private String errorDescription;

    public DiscordErrorResponse() {
    }

    private DiscordErrorResponse(Builder builder) {
        this.error = builder.error;
        this.errorDescription = builder.errorDescription;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getError() {
        return error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    @Override
    public String toString() {
        return "DiscordErrorResponse{" +
                "error='" + error + "\', " +
                "errorDescription='" + errorDescription + '\'' +
                '}';
    }

    public static final class Builder {
        private String error;
        private String errorDescription;

        private Builder() {
        }

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        public Builder errorDescription(String errorDescription) {
            this.errorDescription = errorDescription;
            return this;
        }

        public DiscordErrorResponse build() {
            return new DiscordErrorResponse(this);
        }
    }
}