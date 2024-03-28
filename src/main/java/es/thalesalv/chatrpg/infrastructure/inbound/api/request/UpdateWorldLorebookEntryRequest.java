package es.thalesalv.chatrpg.infrastructure.inbound.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(builderClassName = "Builder")
@AllArgsConstructor
public class UpdateWorldLorebookEntryRequest {

    private String name;
    private String regex;
    private String description;
    private String playerDiscordId;
    private boolean isPlayerCharacter;
}
