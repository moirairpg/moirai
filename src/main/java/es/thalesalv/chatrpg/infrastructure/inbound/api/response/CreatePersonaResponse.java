package es.thalesalv.chatrpg.infrastructure.inbound.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreatePersonaResponse {

    private String id;

    public CreatePersonaResponse() {
    }

    private CreatePersonaResponse(String id) {
        this.id = id;
    }

    public static CreatePersonaResponse build(String id) {

        return new CreatePersonaResponse(id);
    }

    public String getId() {
        return id;
    }
}
