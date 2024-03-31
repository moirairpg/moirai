package es.thalesalv.chatrpg.infrastructure.inbound.api.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(builderClassName = "Builder")
@AllArgsConstructor
public class CreateWorldRequest {

    @NotEmpty(message = "cannot be empty")
    @NotNull(message = "cannot be null")
    private String name;

    @NotEmpty(message = "cannot be empty")
    @NotNull(message = "cannot be null")
    private String description;

    @NotEmpty(message = "cannot be empty")
    @NotNull(message = "cannot be null")
    private String adventureStart;

    @NotEmpty(message = "cannot be empty")
    @NotNull(message = "cannot be null")
    private String visibility;
    private List<String> usersAllowedToWrite;
    private List<String> usersAllowedToRead;
}
