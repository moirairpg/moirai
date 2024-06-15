package es.thalesalv.chatrpg.core.application.usecase.world.result;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(builderClassName = "Builder")
public final class WorldLorebookEntryResult {

    private String name;
    private String regex;
    private String description;
    private String playerDiscordId;
}
