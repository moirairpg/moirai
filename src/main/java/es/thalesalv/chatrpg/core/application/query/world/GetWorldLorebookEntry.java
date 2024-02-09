package es.thalesalv.chatrpg.core.application.query.world;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(builderClassName = "Builder")
public final class GetWorldLorebookEntry {

    private String name;
    private String regex;
    private String description;
    private String playerDiscordId;
}
