package es.thalesalv.chatrpg.core.application.command.world;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(builderClassName = "Builder")
public final class CreateWorldLorebookEntry {

    private String name;
    private String regex;
    private String description;
    private String playerDiscordId;
}
