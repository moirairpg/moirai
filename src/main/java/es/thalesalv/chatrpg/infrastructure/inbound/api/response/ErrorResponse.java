package es.thalesalv.chatrpg.infrastructure.inbound.api.response;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse {

    private HttpStatus code;
    private String message;
    private List<String> details;

    public ErrorResponse() {
    }

    private ErrorResponse(Builder builder) {
        this.code = builder.code;
        this.message = builder.message;
        this.details = builder.details;
    }

    public static Builder builder() {
        return new Builder();
    }

    public HttpStatus getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getDetails() {
        return details;
    }

    public static class Builder {
        private HttpStatus code;
        private String message;
        private List<String> details;

        public Builder code(HttpStatus code) {
            this.code = code;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder details(List<String> details) {
            this.details = details;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(this);
        }
    }
}