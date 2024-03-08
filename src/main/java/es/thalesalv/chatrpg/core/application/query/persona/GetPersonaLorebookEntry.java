package es.thalesalv.chatrpg.core.application.query.persona;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(builderClassName = "Builder")
public final class GetPersonaLorebookEntry {

    private String name;
    private String regex;
    private String description;
    private String playerDiscordId;
}
