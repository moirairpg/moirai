package es.thalesalv.chatrpg.infrastructure.inbound.api.request;

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
public class CreateWorldLorebookEntryRequest {

    @NotEmpty(message = "cannot be empty")
    @NotNull(message = "cannot be null")
    private String name;
    private String regex;

    @NotEmpty(message = "cannot be empty")
    @NotNull(message = "cannot be null")
    private String description;
    private String playerDiscordId;
    private boolean isPlayerCharacter;
}
